(ns pozdnyshev.kata05-test
  (:require [clojure.test :refer :all]
            [pozdnyshev.kata05 :as bf]))


; ===========================================================================
;; utils



; ===========================================================================
;; tests

(defn setup [f]
  (bf/init! 50 5)
  (f))

(use-fixtures
  :once
  setup)

(deftest test-set-membership
  (let [in ["a" "b" "c"]
        out ["d" "e" "f"]]
    (doseq [x in]
      (bf/add! x))
    (doseq [x in]
      (is (true? (bf/in? x))))
    (doseq [x out]
      (is (false? (bf/in? x))))))