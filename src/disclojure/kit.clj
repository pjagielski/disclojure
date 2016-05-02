(ns disclojure.kit
  (:require [overtone.live :refer :all]
            [clojure.java.io :as io]
            [leipzig.live :as live]))

(defn without-extension [filename]
  (subs filename 0 (.lastIndexOf filename ".")))

(def kit (atom {}))

(definst player [buf 1]
  (let [env (env-gen (adsr 0.1 1 1 0.2 1) :action FREE)]
    (* env (scaled-play-buf 2 buf :action FREE))))

(defn load-kit! [dir]
  (->>
    (.listFiles (io/file dir))
    (map (fn [f] [(-> (without-extension (.getName f)) keyword)
                  {:sound (partial player (sample (.getAbsolutePath f)))
                   :amp   1}]))
    (into (sorted-map))
    (reset! kit)))

(defmethod live/play-note :beat [note]
  (when-let [fn (-> (get @kit (:drum note)) :sound)]
    (fn :amp (:amp note))))