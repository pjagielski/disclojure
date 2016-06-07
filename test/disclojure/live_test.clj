(ns disclojure.live-test
  (:require [clojure.test :refer :all]
            [disclojure.live :as live]))

(deftest live-test

  (testing "reset track"
    (live/reset-track {:instr [{:time 0 :part :test}]})
    (is (= [{:time 0 :part :test}] @(live/track)))
    (live/reset-track {:instr [{:time 1/2 :part :test}]})
    (is (= [{:time 3/10 :part :test}] @(live/track))))

  (testing "assoc track"
    (live/reset-track {:a [{:time 0 :part :a}]})
    (live/assoc-track :b [{:time 1/2 :part :b}])
    (is (= [{:time 0 :part :a} {:time 3/10 :part :b}] @(live/track)))
    (live/assoc-track :b [{:time 1/4 :part :b}])
    (is (= [{:time 0 :part :a} {:time 3/20 :part :b}] @(live/track))))

  (testing "update track"
    (live/reset-track {:c [{:time 0 :part :c} {:time 1/2 :part :c}]})
    (letfn [(pred [e] (> (:time e) 0))]
      (live/update-track :c (partial remove pred)))
    (is (= [{:time 0 :part :c}] @(live/track)))))
