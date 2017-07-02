(ns rusty-leopard.handler
  (:require [io.pedestal.http :as server]
            [com.walmartlabs.lacinia.pedestal :as lacinia]
            [rusty-leopard.schema :refer [speakers-rate-schema]]))

(def service (lacinia/pedestal-service (speakers-rate-schema) {:graphiql true}))

(defonce runnable-service (server/create-server service))

(defn -main
  "The entry-point for 'lein run'"
  [& args]
  (println "\nCreating your server ...")
  (server/start runnable-service))
