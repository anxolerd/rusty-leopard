(ns rusty-leopard.db
  (:require [clojure.data.json :as json]
            [environ.core :refer [env]]
            [yesql.core :refer [defqueries]]))
;; still need to put a password in for this
;; need to be sure the database is password protected!
(def db-spec {:classname "org.postgresql.Driver"
              :subprotocol "postgresql"
              :subname (str "//"
                            (or (:db-host env)
                                (System/getenv "DB_HOST"))
                            ":"
                            (or (:db-port env)
                                (System/getenv "DB_PORT"))
                            "/"
                            (or (:db-name env)
                                (System/getenv "DB_NAME")))
              :user (or (:db-user env)
                        (System/getenv "DB_USER"))
              :password (or (:db-password env)
                            (System/getenv "DB_PASSWORD"))})

(defqueries "sql/operations.sql"
  {:connection db-spec})

;; see: https://gist.github.com/alexpw/2166820
(defmacro check-error
  "Usage: (check-error (create-developer! (core/new-developer \"foo@bar.com\")))"
  [body]
  `(try ~body (catch Exception e# (throw (Exception.(:cause (Throwable->map (.getNextException e#))))))))

(defn resolve-talk
  [context args _value]
  (let [talk (first (check-error (get-talk {:id (:id args)})))]
    ;; weirdly, snake_case names are not resolved properly O_o
    ;; so turning them into camelCase names
    (assoc talk :videoUrl (:video_url talk) 
                :slidesUrl (:slides_url talk))))

(defn resolve-speaker
  [context args _value]
  (let [speaker (first (check-error (get-speaker {:id (:id args)})))]
    ;; weirdly, snake_case names are not resolved properly O_o
    ;; so turning them into camelCase names
    (assoc speaker :firstName (:first_name speaker) 
                   :lastName (:last_name speaker))))

(defn resolve-talk-speaker
  [context args _value]
  (resolve-speaker context {:id (:speaker_id _value)} nil))

(defn resolve-speakers
  [context args _value]
  (map 
    (fn [s] (assoc s :firstName (:first_name s)
                     :lastName (:last_name s)))
    (get-speakers)))

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
  (let [speaker_id (-> (add-speaker {:first_name (:firstName args),
                                     :last_name (:lastName args)})
                       first
                       :id)]
    (assoc {} :id speaker_id
              :firstName (:firstName args)
              :lastName (:lastName args))))
