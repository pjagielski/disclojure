(ns disclojure.midi
  (:use clojure.java.data)
  (:require [clojure.math.numeric-tower :as math])
  (:import (java.io File)
           (javax.sound.midi MidiSystem ShortMessage MetaMessage Track)))

; code taken from https://github.com/pbaille/bartok/blob/new-prims/src/bartok/midi/parser.clj but it's not realeased anywhere...

(defmacro or=
  ([expr coll] `(or= ~expr ~@coll))
  ([expr o & ors]
   `(or ~@(map (fn [o] `(= ~expr ~o)) (cons o ors)))))

(defmacro or-> [arg & exprs]
  `(or ~@(map (fn [expr] (if (symbol? expr)
                           `(~expr ~arg)
                           (cons (first expr) (cons arg (next expr)))))
              exprs)))

;shortcuts
(def a apply)

;**********************************************

(defn- note-on?        [msg] (or= (.getCommand msg) (range 0x90 0xA0)))
(defn- note-off?       [msg] (or= (.getCommand msg) (range 0x80 0x90)))
(defn- poly-after?     [msg] (or= (.getCommand msg) (range 0xA0 0xB0)))
(defn- control-change? [msg] (or= (.getCommand msg) (range 0xB0 0xC0)))
(defn- program-change? [msg] (or= (.getCommand msg) (range 0xC0 0xD0)))
(defn- chan-after?     [msg] (or= (.getCommand msg) (range 0xD0 0xE0)))
(defn- pitch-wheel?    [msg] (or= (.getCommand msg) (range 0xE0 0xF0)))

(defn- note-msg? [msg] (or (note-on? msg)(note-off? msg)))

(defn- tempo-msg?          [msg] (= (.getType msg) 0x51))
(defn- time-signature-msg? [msg] (= (.getType msg) 0x58))
(defn- key-signature-msg?  [msg] (= (.getType msg) 0x59))

(def int->key
  {0 :C -1 :Bb -2 :Eb -3 :Ab -4 :Db
   -5 :Gb -6 :Cb -7 :Fb 1 :G 2 :D
   3 :A 4 :E 5 :B 6 :F# 7 :C#})

; (defn- bpm-at [midi-pos parsed-file] ())

(defn- valid-meta-msg? [msg]
  (or-> msg
        tempo-msg?
        time-signature-msg?
        key-signature-msg?))

(defn- valid-msg? [msg]
  (or-> msg
        note-on?
        note-off?
        poly-after?
        control-change?
        program-change?
        chan-after?
        pitch-wheel?))

(defn- parse-meta-message [msg tick]
  (cond
    (tempo-msg? msg)
    {:type     :tempo
     :position tick
     :bpm      (->> (a format "0x%x%x%x" (.getData msg))
                    read-string
                    (/ 60000000)
                    float
                    (math/round))}
    (time-signature-msg? msg)
    {:type      :time-signature
     :position  tick
     :signature (let [[n d] (from-java (.getData msg))]
                  [n (int (math/expt 2 d))])}
    (key-signature-msg? msg)
    {:type     :key-signature
     :position tick
     :key      (get int->key (first (from-java (.getData msg))))}
    :else nil))

(defn- parse-message [msg tick]
  (cond
    (note-msg? msg)
    {:type     :note
     :channel  (.getChannel msg)
     :pitch    (.getData1 msg)
     :velocity (if (note-on? msg) (.getData2 msg) 0)
     :position tick}
    (poly-after? msg)
    {:type     :poly-after
     :channel  (.getChannel msg)
     :data     [(.getData1 msg) (.getData2 msg)]
     :position tick}
    (control-change? msg)
    {:type     :control-change
     :channel  (.getChannel msg)
     :data     [(.getData1 msg) (.getData2 msg)]
     :position tick}
    (program-change? msg)
    {:type     :program-change
     :channel  (.getChannel msg)
     :data     (.getData msg)
     :position tick}
    (chan-after? msg)
    {:type     :chan-after
     :channel  (.getChannel msg)
     :data     (.getData msg)
     :position tick}
    (pitch-wheel? msg)
    {:type     :pitch-wheel
     :channel  (.getChannel msg)
     :data     (.getData msg)
     :position tick}
    :else nil))

(defn find-first
  [f coll]
  (first (filter f coll)))

;set start-position to zero and convert durations and positions into beat unit
(defn- time-format [resolution parsed]
  (let [start-offset (:position (find-first #(= (:type %) :note) parsed))]
    (map (fn [event]
           (let [pos (/ (- (:position event) start-offset) resolution)
                 event (assoc event :position pos)]
             (if (:duration event)
               (update-in event [:duration] / resolution)
               event)))
         parsed)))

;grab all note-on and note-off message and couple them into :note type with duration
(defn- on-off-coupling [parsed]
  (let [{notes :note :as by-type} (group-by :type parsed)
        {ons :ons offs :offs} (group-by #(if (zero? (:velocity %)) :offs :ons) notes)
        coupled (map (fn [{pos-on :position :as m} {pos-off :position}]
                       (assoc m :duration (- pos-off pos-on)))
                     ons offs)]
    (->> (assoc by-type :note coupled) vals (a concat))))

(defn- parse-track [track]
  (loop [parsed []
         event-index 0]
    (let [event (.get track event-index)
          tick (.getTick event)
          message (.getMessage event)]
      (cond
        (= (inc event-index) (.size track)) parsed
        (and (instance? MetaMessage message) (valid-meta-msg? message))
        (recur (conj parsed (parse-meta-message message tick)) (inc event-index))
        (and (instance? ShortMessage message) (valid-msg? message))
        (recur (conj parsed (parse-message message tick)) (inc event-index))
        :else (recur parsed (inc event-index))))))

;main
(defn parse-midi-file [file-name]
  (let [midi-seq (-> (File. file-name) MidiSystem/getSequence)
        tracks (.getTracks midi-seq)
        res (.getResolution midi-seq)
        cnt (-> tracks from-java count)]
    (->> (for [n (range cnt)]
           (parse-track (aget tracks n)))
         (mapcat on-off-coupling)
         (sort-by :position)
         (time-format res))))

;(parse-midi-file "src/midi-files/rmmlo.mid")

(defn filter-msg-type [type-kw parsed-midi-file]
  (filter #(= (:type %) type-kw) parsed-midi-file))

(defn- map-midi-note [[last-time last-dur result] {:keys [position duration] :as note}]
  (let [to-add (concat (when (< last-time position)
                         [{:position (+ last-dur last-time)
                           :duration (- position last-time)
                           :pitch    nil}])
                       [note])]
    [position duration (concat result to-add)]))

(defn from-midi-notes [parsed-midi-file]
  (as-> parsed-midi-file $
        (filter-msg-type :note $)
        (reduce map-midi-note [0 0 []] $)
        (nth $ 2)
        (map (fn [n] (clojure.set/rename-keys n {:position :time})) $)
        (map (fn [n] (select-keys n [:pitch :time :duration])) $)))