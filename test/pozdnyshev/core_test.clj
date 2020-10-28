(ns pozdnyshev.core-test
  (:require [clojure.test :refer :all]
            [pozdnyshev.core :as pz]))

; ===========================================================================
;; utils




; ===========================================================================
;; tests

(def simple-ex ["status"
                "pull"
                "checkout"
                "add"
                "commit"
                "push"
                "status"
                "checkout"
                "status"
                "checkout"])

(deftest build-graph
  (let [result (pz/build-graph simple-ex)
        expected {"status"   {"pull" 1, "checkout" 2},
                  "pull"     {"checkout" 1},
                  "checkout" {"add" 1, "status" 1},
                  "add"      {"commit" 1},
                  "commit"   {"push" 1},
                  "push"     {"status" 1}}]
    (is (= result expected))))
