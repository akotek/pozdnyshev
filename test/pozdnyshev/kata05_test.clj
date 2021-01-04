(ns pozdnyshev.kata05-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [pozdnyshev.kata05 :as bf]
            [clojure.string :as s]))

; ===========================================================================
;; tests

(def bloom (bf/create 5000000 5))

(deftest test-set-membership
  (let [in ["a" "b" "c"]
        out ["d" "e" "f"]
        b1 (reduce bf/insert bloom in)
        bf-in? (partial bf/in? b1)]
    (is (every? true? (map bf-in? in)))
    (is (every? false? (map bf-in? out)))))

(deftest test-spell-check
  (let [dict (s/split-lines (slurp "resources/wordlist.txt"))
        spelled (take 5 (shuffle dict))
        misspelled (map #(str "1" %) spelled)
        b1 (reduce bf/insert bloom dict)
        bf-in? (partial bf/in? b1)]
    (is (every? true? (map bf-in? spelled)))
    (is (every? false? (map bf-in? misspelled)))))