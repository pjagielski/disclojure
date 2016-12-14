(ns disclojure.play
  (:require [leipzig.live :as live]
            [leipzig.temperament :refer [equal]]
            [disclojure.inst :as i]
            [overtone.core :refer :all]
            [overtone.inst.synth :refer :all]))

(def controls (atom {:plucky {:amp 1.0 :cutoff 900}
                     :stab {:amp 0.75 :cutoff 2000}
                     :supersaw {:release 0.2 :cutoff 3000}
                     :bass {:release 0.1 :cutoff 1500}}))

(defn to-args [m]
  (mapcat vec m))

(defn play [name params]
  (live/play-note (merge params {:part name :pitch (equal (:note params))})))

(defn find-instruments
  ([] (find-instruments 'disclojure.inst))
  ([ns]
   (->> (ns-interns ns)
        (filter (fn [e] (= :overtone.studio.inst/instrument
                           (type (val e))))))))

(doseq [[name play-fn] (find-instruments)]
  (let [key (keyword name)]
    (defmethod live/play-note key [{hertz :pitch seconds :duration amp :amp cutoff :cutoff}]
      (when hertz
        (let [params (merge {:freq hertz :dur seconds :amp (or amp 1)}
                            (when cutoff {:cutoff cutoff}))]
          (apply play-fn (to-args (merge (get @controls key) params))))))))
