(ns disclojure.sampler
  (:require [clojure.java.io :as io]
            [overtone.live :refer :all]
            [plumbing.core :refer [map-vals]]
            [leipzig.live :as live])
  (:import (java.io File)))

(def sample-regex #"(\d*)_(\d*)_([a-zA-Z0-9\-]*).(\w*)")

(def file-regex #"(.*)\.(\w*)")

(def defaults {:bpm 120 :beats 8})

(defn meta-from-filename [^File file]
  (-> (if-let [[_ bpm beats name _]
               (re-matches sample-regex (.getName file))]
        {:bpm (read-string bpm) :beats (read-string beats) :name (keyword name)}
        (let [[_ name _] (re-matches file-regex (.getName file))]
          (merge defaults {:name (keyword name)})))
      (merge {:sound (sample (.getAbsolutePath file))})))

(defonce samples (atom {}))

(defn load-samples! [dir]
  (->>
    (.listFiles (io/file dir))
    (map meta-from-filename)
    (group-by :name)
    (map-vals first)
    (into (sorted-map))
    (reset! samples)))

(definst sampler [in 0 bpm 120 total-beats 4 beats 4 amp 1 pos 0 out-bus 0]
  (let [env (env-gen (envelope [1 1 0] [(* beats (/ 60 bpm)) 0.2] :welch))
        rate (/ (buf-dur:kr in) (* total-beats (/ 60 bpm)))
        output (play-buf 1 in (* rate (buf-rate-scale in)) :start-pos pos :action FREE)]
    (* amp env (pan2 output))))

(defmethod live/play-note :sampler [data]
  (when-let [s (get @samples (:sample data))]
    (sampler (:sound s) (:bpm data) (:beats s)
             (or (:beats data) (:beats s)) (or (:amp data) 1))))
