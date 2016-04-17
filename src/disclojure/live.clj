(ns disclojure.live
  (:require [disclojure.track :as t]))

(defn alter-raw-track
  [ref key val]
  (alter ref assoc key val))

(defn commit-track [raw-track-ref track-ref]
  (ref-set track-ref (t/track @raw-track-ref)))