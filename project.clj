(defproject rusty-leopard "0.1.0"
  :description "GraphQL API for rating speakers at conferences"
  :url "https://rusty-leopard.herokuapp.com"
  :min-lein-version "2.0.0"
  :dependencies [[environ "1.1.0"]
                 [org.clojure/clojure "1.8.0"]
                 [org.clojure/data.json "0.2.6"]
                 [org.postgresql/postgresql "9.4-1201-jdbc41"]
                 [yesql "0.5.3"]
                 [com.walmartlabs/lacinia "0.18.0"]
                 [com.walmartlabs/lacinia-pedestal "0.2.0"]
                 [org.slf4j/slf4j-simple "1.7.25"]]
  :plugins [[lein-environ "1.1.0"]]
  :main rusty-leopard.handler)
