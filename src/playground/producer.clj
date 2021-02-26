(ns playground.producer
  (:require [jackdaw.client :as jc]
            [jsonista.core :as json]
            [playground.schema :as schema])
  (:import java.util.UUID))

(def producer-config
  {"bootstrap.servers" "localhost:9092"
   "key.serializer" "org.apache.kafka.common.serialization.UUIDSerializer"
   "value.serializer" "org.apache.kafka.common.serialization.ByteArraySerializer"
   "acks" "all"
   "cliente.id" "foo"})

(defn get-uuid []
  (UUID/randomUUID))

(defn edn->bytes [value]
  (-> value
      (json/write-value-as-bytes json/default-object-mapper)))

(defn send-message [topic key value]
  (with-open [my-producer (jc/producer producer-config)]
    @(jc/produce! my-producer {:topic-name topic} key value)))


(defn run-produce-in-background[topic-name]
  (future
    (while true
      (send-message topic-name (get-uuid) (edn->bytes (schema/generate-msg)))
      (Thread/sleep 2000))))

