(ns disclojure.inst
  (:require [overtone.live :refer :all]))

(defcgen cut-out [input {:default :none}]
         (:ar (let [_ (detect-silence input :action FREE)]
                input))
         (:default :ar))

(defcgen effects [input  {:default :none}
                  pan    {:default 0}
                  wet    {:default 0.33}
                  room   {:default 0.5}
                  damp   {:default 0.5}
                  volume {:default 1.0}
                  early  {:default 0.1}
                  high   {:default 20000}
                  low    {:default 0}]
         (:ar (-> input
                  (* volume)
                  (pan2 pan)
                  (free-verb :mix wet :room room :damp damp)
                  (lpf high)
                  cut-out))
         (:default :ar))

(definst plucky [freq 440 dur 1 amp 1 cutoff 3000 fil-dur 0.1]
 (let [env (env-gen (asr 0 1 1) (line:kr 1.0 0.0 dur) :action FREE)
       level (+ (* 0.85 freq) (env-gen (perc 0 fil-dur) :level-scale cutoff))]
   (-> (pulse freq)
       (lpf level)
       (effects :room 1 :wet 0.45)
       (* env amp))))
