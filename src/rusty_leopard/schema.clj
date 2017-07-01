(ns rusty-leopard.schema
  (:require
   [rusty-leopard.db :as db]
   [clojure.java.io :as io]
   [clojure.edn :as edn]
   [com.walmartlabs.lacinia.schema :as schema]
   [com.walmartlabs.lacinia.util :refer [attach-resolvers]]))

(defn speakers-rate-schema
  []
  (-> (io/resource "edn/speakers-rate-schema.edn")
      slurp
      edn/read-string
      (attach-resolvers {:resolve-talk db/resolve-talk
                         :resolve-speaker db/resolve-speaker
                         :resolve-speakers (constantly [])
                         :resolve-talk-speaker db/resolve-talk-speaker
                         :resolve-speaker-talks db/resolve-speaker-talks
                         :resolve-speaker-reviews db/resolve-speaker-reviews
                         :resolve-talk-reviews db/resolve-talk-reviews
                         :resolve-review-talk db/resolve-review-talk})
      schema/compile))
