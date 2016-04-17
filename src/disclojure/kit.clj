(ns disclojure.kit
  (:require [overtone.live :as o]
            [clojure.java.io :as io]
            [leipzig.live :as live]))

(defn without-extension [filename]
  (subs filename 0 (.lastIndexOf filename ".")))

(def kit (atom {}))

(defn load-kit! [dir]
  (->>
    (.listFiles (io/file dir))
    (map (fn [f] [(-> (without-extension (.getName f)) keyword)
                  {:sound (o/sample (.getAbsolutePath f))
                   :amp   1}]))
    (into (sorted-map))
    (reset! kit)))

(defmethod live/play-note :beat [note]
  (when-let [fn (-> (get @kit (:drum note)) :sound)]
    (fn :amp (:amp note))))