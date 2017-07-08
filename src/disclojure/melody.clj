(ns disclojure.melody
  (require [leipzig.melody :refer [wherever]]))

(defn take-beats
  ([end notes] (take-beats 0 end notes))
  ([start end notes]
   (->> notes
        (filter (fn [n] (and (>= (:time n) start)
                             (< (+ (:duration n) (:time n)) end))))
        (wherever :time :time (fn [t] (- t start))))))

(defn- fill-first [notes]
  (let [first (first notes)]
    (if (not= 0 (:time first))
      (concat [{:time 0 :pitch nil :duration (:time first)}] notes)
      notes)))

(defn- fill-last [beats empty-note notes]
  (let [last (last notes)
        gap-time (+ (:time last) (:duration last))
        gap (- beats gap-time)
        add-gap? (> gap 0)]
    (concat notes
            (when add-gap? [(merge empty-note {:time gap-time :duration gap})]))))

(defn fill-to-beats
  ([beats notes] (fill-to-beats beats {:pitch nil} notes))
  ([beats empty-note notes]
   (->>
     notes
     fill-first
     (fill-last beats empty-note))))
