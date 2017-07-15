(ns rusty-leopard.db
  (:require [clojure.data.json :as json]
            [environ.core :refer [env]]
            [yesql.core :refer [defqueries]]))
;; still need to put a password in for this
;; need to be sure the database is password protected!
(def db-spec {:classname "org.postgresql.Driver"
              :connection-uri (or (:db-uri env)
                              (System/getenv "JDBC_DATABASE_URL"))})

(defqueries "sql/operations.sql"
  {:connection db-spec})

;; see: https://gist.github.com/alexpw/2166820
(defmacro check-error
  "Usage: (check-error (create-developer! (core/new-developer \"foo@bar.com\")))"
  [body]
  `(try ~body (catch Exception e# (throw (Exception.(:cause (Throwable->map (.getNextException e#))))))))

(defn talk-from-db
  [talk]
  (assoc talk :videoUrl (:video_url talk) :slidesUrl (:slides_url talk)))

(defn speaker-from-db
  [speaker]
  (assoc speaker :firstName (:first_name speaker)
                 :middleName (:middle_name speaker)
                 :lastName (:last_name speaker)))


(defn resolve-talk
  [context args _value]
  (let [talk (first (check-error (get-talk {:id (:id args)})))]
    (talk-from-db talk)))

(defn resolve-speaker
  [context args _value]
  (let [speaker (first (check-error (get-speaker {:id (:id args)})))]
    (speaker-from-db speaker)))

(defn resolve-speaker-rating
  [context args _value]
  (-> (get-speaker-rating {:speaker_id (:id _value)}) first :rating))

(defn resolve-talk-speaker
  [context args _value]
  (resolve-speaker context {:id (:speaker_id _value)} nil))

(defn resolve-speakers
  [context args _value]
  (map speaker-from-db (get-speakers)))

(defn resolve-speaker-talks
  [context args _value]
  (check-error (get-speaker-talks {:speaker_id (:id _value)})))

(defn resolve-speaker-reviews
  [context args _value]
  (check-error (get-speaker-reviews {:speaker_id (:id _value)})))

(defn resolve-talk-reviews
  [context args _value]
  (check-error (get-talk-reviews {:talk_id (:id _value)})))

(defn resolve-review-talk
  [context args _value]
  (resolve-talk context {:id (:talk_id _value)} nil))

(defn resolve-add-speaker
  [context args _value]
  (let [names {:first_name (:firstName args),
               :middle_name (:middleName args),
               :last_name (:lastName args)}
        exists? (-> (speaker-exists? names) first :result)]
    (if exists?
        {:errors ["Speaker already exists"], :speaker nil}
        (let [speaker_id (-> (add-speaker names) first :id)]
             {:errors nil, :speaker (assoc args :id speaker_id)}))))

(defn resolve-add-talk
  [context args _value]
  (let [params {:speaker_id (:speakerId args),
                :name (:name args),
                :video_url (:videoUrl args),
                :slides_url (:slidesUrl args)}]
    (cond (-> (talk-exists? params) first :result)
            {:errors ["Talk already exists"], :talk nil}
          (not (-> (speaker-exists-by-id? {:id (:speaker_id params)}) first :result))
            {:errors ["Speaker does not exist"], :talk nil}
          :else
            (let [talk_id (-> (add-talk params) first :id)]
                 {:errors nil,
                  :talk (assoc args :id talk_id :speaker_id (:speakerId args))}))))

(defn resolve-add-review
  [context args _value]
  (if (not (-> (talk-exists-by-id? {:id (:talkId args)}) first :result))
    {:errors ["Talk does not exist"], :review nil}
    (let [review_id (-> (add-review {:talk_id (:talkId args),
                                     :rating (:rating args),
                                     :comment (:comment args)})
                     first
                     :id)]
         {:errors nil,
          :review (assoc args :id review_id :talk_id (:talkId args))})))
