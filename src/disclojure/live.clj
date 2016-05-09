(ns disclojure.live
  (:require [disclojure.track :as t]))

(defn assoc-raw-track
  [ref key val]
  (alter ref assoc key val))

(defn update-raw-track
  [ref key fun]
  (alter ref update key fun))

(defn commit-track [raw-track-ref track-ref]
  (ref-set track-ref (t/track @raw-track-ref)))
