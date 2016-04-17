(ns repl
  (:require [disclojure.track :as t]
            [disclojure.live :as l]))

(defn raw-track [] (get t/state :raw-track))
(defn track [] (get t/state :track))

(defn commit-track []
  (let [{:keys [track raw-track]} t/state]
    (l/commit-track raw-track track)))

(defn update-track [& kvs]
  (dosync
    (loop [key (first kvs) val (second kvs) rest (nnext kvs)]
      (l/alter-raw-track (raw-track) key val)
      (if rest
        (recur (first rest) (second rest) (nnext rest))))
    (commit-track)))
