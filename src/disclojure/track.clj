(ns disclojure.track
  (:require [leipzig.melody :refer :all]
            [leipzig.temperament :as temperament]
            [disclojure.play]))

(def metro (atom 100))

(defn tap [drum times length & {:keys [amp] :or {amp 1}}]
  (map #(zipmap [:time :duration :drum :amp]
                [%1 (- length %1) drum amp]) times))

(defn sampler-entry [times sample beats & {:keys [amp start-beat cutoff] :or {amp 1 start-beat 0 cutoff 10000}}]
  (let [base (merge {:sample sample :amp (or amp 1) :bpm @metro :cutoff (or cutoff 10000)}
                    (when start-beat {:start-beat start-beat})
                    (when beats {:beats beats :duration beats}))]
    (if (sequential? times)
      (map (fn [t] (merge base {:time t})) times)
      [(merge base {:time times})])))

(defn sampler [meta]
  (->>
    meta
    (mapcat #(apply sampler-entry %))
    (sort-by :time)
    (all :part :sampler)))

(defn track [raw-track]
  (->> raw-track
       vals
       (reduce with)
       (wherever :pitch, :pitch temperament/equal)
       (where :time (bpm @metro))
       (where :duration (bpm @metro))))

