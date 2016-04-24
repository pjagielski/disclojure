(ns disclojure.track
  (:require [overtone.live :as o]
            [leipzig.melody :refer :all]
            [leipzig.temperament :as temperament]
            [disclojure.play]))

(def metro (atom 100))

(defn tap [drum times length & {:keys [amp] :or {amp 1}}]
  (map #(zipmap [:time :duration :drum :amp]
                [%1 (- length %1) drum amp]) times))

(defn sampler [meta]
  (->>
    meta
    (map (fn [[time sample beats amp]]
           (merge {:time time :sample sample :amp (or amp 1) :bpm @metro}
                  (when beats {:beats beats :duration beats}))))
    (all :part :sampler)
    (all :duration 0)))

(defn track [raw-track]
  (->> raw-track
       vals
       (reduce with)
       (wherever :pitch, :pitch temperament/equal)
       (where :time (bpm @metro))
       (where :duration (bpm @metro))))

