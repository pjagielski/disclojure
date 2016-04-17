(ns disclojure.play
  (:require [leipzig.live :as live]
            [leipzig.temperament :refer [equal]]
            [disclojure.inst :as i]
            [overtone.live :refer :all]
            [overtone.inst.synth :refer :all]))

(def controls (atom {:plucky {:amp 1.0 :cutoff 900}}))

(defn to-args [m]
  (mapcat vec m))

(defn play [name params]
  (live/play-note (merge params {:part name :pitch (equal (:note params))})))

(defmethod live/play-note :plucky [{hertz :pitch seconds :duration amp :amp}]
  (when hertz
    (let [params {:freq hertz :dur seconds :volume (or amp 1)}]
      (apply i/plucky (to-args (merge (:plucky @controls) params))))))
