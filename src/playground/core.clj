(ns playground.core
  (:require [clojure.string :as str]
            [jackdaw.streams :as j]
            [jsonista.core :as json]
            [clojure.walk :as walk]
            [playground.producer :as p])

  (:import [org.apache.kafka.common.serialization Serdes]))

(defonce application (atom nil))
(def topic-name "trx")
(def p (p/run-produce-in-background topic-name))

(defn topic-config
  [topic-name]
  {:topic-name         topic-name
   :partition-count    1
   :replication-factor 1
   :key-serde          (Serdes/UUID)
   :value-serde        (Serdes/ByteArray)})

(defn app-config
  []
  {"application.id" "trx-id"
   "bootstrap.servers" "localhost:9092"})

(defn byte->edn [value]
  (-> value
      (json/read-value json/default-object-mapper)
      walk/keywordize-keys))

(defn build-topology
  [builder]
  (-> (j/kstream builder (topic-config topic-name))
      (j/peek (fn [[k v]]
                (prn {:key k :value (byte->edn v)})))
      ;(j/to (topic-config "trx-out"))
      )
  builder)

(defn start-app
  [app-config]
  (let [builder (j/streams-builder)
        topology (build-topology builder)
        app (j/kafka-streams topology app-config)]
    (j/start app)
    (prn "Application is up")
    app))

(defn stop
  []
  (j/close @application)
  (prn "Application is down"))

(defn start
  []
  (reset! application (start-app (app-config))))

