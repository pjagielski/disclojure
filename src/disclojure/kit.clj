(ns disclojure.kit
  (:require [overtone.core :refer :all]
            [clojure.java.io :as io]
            [leipzig.live :as live]))

(defn without-extension [filename]
  (subs filename 0 (.lastIndexOf filename ".")))

(def kit (atom {}))

(definst mono-player [buf 1 amp 1]
  (let [env (env-gen (adsr 0.1 1 1 0.2 1) :action FREE)]
    (* amp env (scaled-play-buf 1 buf :action FREE))))

(definst stereo-player [buf 1 amp 1]
  (let [env (env-gen (adsr 0.1 1 1 0.2 1) :action FREE)]
    (* amp env (scaled-play-buf 2 buf :action FREE))))

(defn- -load-sample [file]
  (let [sample (sample (.getAbsolutePath file))
        player (condp = (:n-channels sample)
                      1 mono-player
                      2 stereo-player
                      (throw (str "Could not determine number of channels for" file)))]
    [(-> (without-extension (.getName file)) keyword)
     {:sound (partial player sample)
      :amp   1}]))

(defn load-kit! [dir]
  (->>
    (.listFiles (io/file dir))
    (map -load-sample)
    (into (sorted-map))
    (reset! kit)))

(defmethod live/play-note :beat [note]
  (when-let [fn (-> (get @kit (:drum note)) :sound)]
    (fn :amp (:amp note))))
