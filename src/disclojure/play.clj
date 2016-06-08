(ns disclojure.play
  (:require [leipzig.live :as live]
            [leipzig.temperament :refer [equal]]
            [disclojure.inst :as i]
            [overtone.live :refer :all]
            [overtone.inst.synth :refer :all]))

(def controls (atom {:plucky {:amp 1.0 :cutoff 900}
                     :stab {:amp 0.75}
                     :supersaw {:release 0.2 :cutoff 6000}}))

(defn to-args [m]
  (mapcat vec m))

(defn play [name params]
  (live/play-note (merge params {:part name :pitch (equal (:note params))})))

(defmethod live/play-note :plucky [{hertz :pitch seconds :duration amp :amp}]
  (when hertz
    (let [params {:freq hertz :dur seconds :amp (or amp 1)}]
      (apply i/plucky (to-args (merge (:plucky @controls) params))))))

(defmethod live/play-note :stab [{hertz :pitch seconds :duration amp :amp}]
  (when hertz
    (let [params {:freq hertz :dur seconds :amp (or amp 1)}]
      (apply i/stab (to-args (merge (:stab @controls) params))))))

(defmethod live/play-note :supersaw [{hertz :pitch seconds :duration amp :amp cutoff :cutoff}]
  (when hertz
    (let [params {:freq hertz :dur seconds :amp (or amp 1) :cutoff (or cutoff 6000)}]
      (apply i/supersaw (to-args (merge (:supersaw @controls) params))))))
