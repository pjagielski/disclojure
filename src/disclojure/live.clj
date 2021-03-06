(ns disclojure.live
  (:require [disclojure.track :as t]))

(def initial-state
  {:raw-track (ref {})
   :track (ref [])})

(defonce state initial-state)

(defn- assoc-raw-track
  [ref key val]
  (alter ref assoc key val))

(defn- update-raw-track
  [ref key fun]
  (alter ref update key fun))

(defn- raw-track [] (get state :raw-track))
(defn track [] (get state :track))

(defn- commit-track []
  (let [{:keys [track raw-track]} state]
    (ref-set track (t/track @raw-track))))

(defn- modify-track [fun & kvs]
  (dosync
    (loop [key (first kvs) val (second kvs) rest (nnext kvs)]
      (fun (raw-track) key val)
      (if rest
        (recur (first rest) (second rest) (nnext rest))))
    (commit-track)))

(def assoc-track (partial modify-track assoc-raw-track))

(def update-track (partial modify-track update-raw-track))

(defn reset-track [raw-track]
  (dosync
    (ref-set (:raw-track state) raw-track)
    (commit-track)))
