(ns disclojure.inst
  (:require [overtone.core :refer :all]))

(defcgen echo [in {:default 0} max-delay {:default 1.0} delay-time {:default 0.4} decay-time {:default 2.0}]
         (:ar (pan2 (+ in (* 0.25 (comb-n in max-delay delay-time decay-time)))))
         (:default :ar))

(definst plucky [freq 440 dur 1 amp 0.8 cutoff 1000 delay-m 2 peak-freq 800]
         (let [env (env-gen (asr 0.02 0.5 1 -4) (line:kr 1.0 0.0 dur) :action FREE)
               level (+ (* 0.25 freq) (env-gen (perc 0 (* dur 1.25)) :level-scale cutoff))
               snd (splay [(lf-saw freq) (pulse freq 0.3) (pink-noise)])]
           (-> snd
               (+ (delay-c snd 0.005 (ranged-rand 0.0001 0.2)))
               (lpf level)
               (clip2 0.85)
               (free-verb :room 2 :mix 0.40)
               (* env amp)
               (b-peak-eq peak-freq 0.6 12)
               (echo :delay-time (* dur delay-m) :max-delay dur))))

(definst pad [freq 220 dur 1.0 amp 0.2 pan 0 cutoff 1800 fil-amt 250]
         (let [env (env-gen (asr 0.01 0.6 1) (line:kr 1.0 0.0 dur) :action FREE)
               fil-env (+ fil-amt (env-gen (perc 0 dur) :level-scale cutoff))
               osc (mix [(sin-osc freq) (saw (+ 2 freq))])]
           (-> osc
               (lpf fil-env)
               (+ (* 0.4 (comb-n:ar osc (* dur 8) dur)))
               (* env amp)
               (free-verb :room 0.7 :mix 0.25)
               pan2)))

(definst supersaw [freq 440 dur 0.2 release 0.5 amp 0.6 cutoff 3500 env-amount 0.5]
         (let [snd-fn (fn [freq]
                        (let [tune (ranged-rand 0.99 1.01)]
                          (-> (lf-saw (* freq tune))
                              (delay-c 0.005 (ranged-rand 0.0001 0.01)))))
               hi-saws (splay (repeatedly 7 #(snd-fn freq)))
               lo-saws (splay (repeatedly 5 #(snd-fn (/ freq 2))))
               noise (pink-noise)
               snd (+ (* 0.65 hi-saws) (* 0.85 lo-saws) (* 0.12 noise))
               env (env-gen (adsr 0.001 0.7 0.2 0.1) (line:kr 1 0 (+ dur release)) :action FREE)]
           (-> snd
               (clip2 0.45)
               (rlpf (+ freq (env-gen (adsr 0.001) (line:kr 1 0 dur) :level-scale cutoff)) 0.75)
               (free-verb :room 1.8 :mix 0.45)
               (* env amp)
               pan2)))

(definst stab [freq 440 detune 0.35 dur 0.2 amp 0.6 cutoff 950]
         (let [freqs (repeat 5 freq)
               freqs (map #(+ (* detune (rand)) %) freqs)
               saws (splay (lf-saw freqs))
               snd (* 0.65 saws)
               env (env-gen (adsr 0.01 0.1 0.8 0.4) (line:kr 1 0 (* dur 1/2)) :action FREE)]
           (-> snd
               (rhpf (+ (* freq 2) cutoff) 0.25)
               (* env amp)
               pan2)))

(definst bass [freq 220 dur 1.0 amp 0.3 osc-mix 0.5 cutoff 1500]
         (let [sub-freq (/ freq 2)
               osc1 (lf-saw freq)
               osc2 (pulse sub-freq 0.51)
               snd (+ (* osc-mix osc2) (* (- 1 osc-mix) osc1))
               level (+ (/ freq 4) (env-gen (perc 0 dur) :level-scale cutoff))
               snd (rlpf snd level 0.7)
               env (env-gen (adsr 0.0001) (line:kr 1.0 0.0 dur) :action FREE)]
           (pan2 (* amp env snd))))
