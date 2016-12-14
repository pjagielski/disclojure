(ns disclojure.melody-test
  (:require [clojure.test :refer :all]
            [disclojure.melody :refer [take-beats fill-to-beats]]))

(deftest melody-test

  (testing "should take start beats"
    (let [notes (->> [{:time 0 :pitch 2 :duration 1} {:time 5 :pitch 3 :duration 2}]
                     (take-beats 16))]
      (is (= notes
             [{:time 0 :pitch 2 :duration 1} {:time 5 :pitch 3 :duration 2}]))))

  (testing "should take middle beats"
    (let [notes (->> [{:time 32 :pitch 2 :duration 1} {:time 33 :pitch 3 :duration 2}]
                     (take-beats 32 48))]
      (is (= notes
             [{:time 0 :pitch 2 :duration 1} {:time 1 :pitch 3 :duration 2}]))))

  (testing "should fill start"
    (let [notes (->> [{:time 7/2 :pitch 2 :duration 1/2}]
                     (fill-to-beats 4))]
      (is (= notes
             [{:time 0 :pitch nil :duration 7/2}
              {:time 7/2 :pitch 2 :duration 1/2}]))))

  (testing "should fill end"
    (let [notes (->> [{:time 0 :pitch 2 :duration 7/2} {:time 7/2 :pitch 2 :duration 1/4}]
                     (fill-to-beats 4))]
      (is (= notes
             [{:time 0 :pitch 2 :duration 7/2}
              {:time 7/2 :pitch 2 :duration 1/4}
              {:time 15/4 :pitch nil :duration 1/4}])))))

