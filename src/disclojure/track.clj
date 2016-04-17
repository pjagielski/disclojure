(ns disclojure.track
  (:require [overtone.live :as o]
            [leipzig.melody :refer :all]
            [leipzig.chord :refer :all]
            [leipzig.scale :as scale]
            [leipzig.temperament :as temperament]
            [disclojure.kit :as k]
            [disclojure.sampler :as s]
            [disclojure.play]
            [clojure.java.io :as io]))

(k/load-kit! (io/file "work/kits/big_room"))
(s/load-samples! (io/file "work/samples"))

(def metro (atom 100))

(defn sampler [meta]
  (->>
    meta
    (map (fn [[time sample amp beats]]
           (merge {:time time :sample sample :amp (or amp 1) :bpm @metro}
                  (when beats {:beats beats}))))
    (all :part :sampler)
    (all :duration 0)))

(def leanon-chords
  [[-9 -2 0 2 4]
   [-8 -1 -3 1 3]
   [-7 0 2 4]
   [-5 -1 2 4 5 6]
   [-5 -1 2 3 4]])

(def leanon
  (let [[ch1 ch2 ch3 ch4 ch5] leanon-chords]
    (->> (phrase (concat (take 9 (cycle [1/2 1/4]))
                         [1/2]
                         (take 9 (cycle [1/2 1/4]))
                         [1/2])
                 [ch1 nil ch1 nil ch1 nil ch2 nil ch2 nil ch3 nil ch3 nil ch3 nil ch4 nil ch4 ch5])
         (wherever :pitch, :pitch (comp scale/low scale/G scale/minor))
         (all :part :plucky)
         (all :amp 1))))

(def initial-track
  {:plucky (times 2 leanon)})

(defn track [raw-track]
  (->> raw-track
       vals
       (reduce with)
       (wherever :pitch, :pitch temperament/equal)
       (where :time (bpm @metro))
       (where :duration (bpm @metro))))

(defonce state
         {:raw-track (ref initial-track)
          :track (ref (track initial-track))})
