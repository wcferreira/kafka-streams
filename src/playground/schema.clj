(ns playground.schema
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]))

(s/def ::id uuid?)
(s/def ::code nat-int?)
(s/def ::company (s/and string? #(> (count %) 5)))
(s/def ::cnpj (s/and string? #(> (count %) 10)))
(s/def ::price (s/and decimal? #(> % 0)))
(s/def ::operations (s/map-of keyword? string?))
(s/def ::trx
  (s/keys
    :req [::id ::code ::company ::cnpj ::price ::operations]))

(defn generate-msg []
   (gen/generate (s/gen ::trx)))

