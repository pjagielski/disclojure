(ns example.core
  (:require [leipzig.melody :refer :all]
            [leipzig.scale :as scale]
            [disclojure.play]
            [disclojure.live :as l]))

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

(defonce state
         (l/reset-track initial-track))

