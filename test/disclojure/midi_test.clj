(ns disclojure.midi-test
  (:require [clojure.test :refer :all]
            [disclojure.midi :as midi]))

(deftest midi-parser-test
  (testing "should parse notest"
    (let [notes (->> (midi/parse-midi-file "test/piano.mid")
                     (midi/from-midi-notes))]
      (is (not-empty notes))
      (is (= (first notes) {:pitch 51, :time 0, :duration 1/2})))))
