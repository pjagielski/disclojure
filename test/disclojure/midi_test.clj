(ns disclojure.midi-test
  (:require [clojure.test :refer :all]
            [disclojure.midi :as midi]
            [disclojure.melody :refer [fill-to-beats]]))

(deftest midi-parser-test
  (testing "should parse notest"
    (let [notes (->> (midi/midi-file->notes "test/piano.mid"))]
      (is (not-empty notes))
      (is (= (first notes) {:pitch 51, :time 0, :duration 1/2}))))

  (testing "should fill to beats"
    (let [notes (->> (midi/midi-file->notes "test/snake-pluck.mid")
                     (fill-to-beats 16))]
      (is (not-empty notes))
      (is (= (last notes) {:pitch nil, :time 63/4, :duration 1/4})))))
