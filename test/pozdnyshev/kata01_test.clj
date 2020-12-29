(ns pozdnyshev.kata01-test
  (:require [clojure.test :refer :all]
            [pozdnyshev.kata01 :as co]))

; ===========================================================================
;; utils

(def price-matrix
  ; Item Price Special Price
  [["A" 50 "3 for 130"]
   ["B" 30 "2 for 45"]
   ["C" 20]
   ["D" 15]])

(defn price [coll]
  (doseq [item coll]
    (co/scan! item))
  (co/get-price))

; ===========================================================================
;; tests

(defn setup [f]
  (co/init! price-matrix)
  (f))

(use-fixtures
  :once
  setup)

(deftest test-total-price
  (is (= 0 (price [])))
  (let [[itemsA itemsB itemsC itemsD] (map #(repeat {:name %}) ["A" "B" "C" "D"])]
    (is (= 50 (->> itemsA (take 1) price)))
    (co/init!)
    (is (= 130 (->> itemsA (take 3) price)))
    (co/init!)
    (is (= 80 (->> (concat (take 1 itemsA) (take 1 itemsB)) price)))
    (co/init!)
    (is (= (+ 130 45) (->> (concat (take 3 itemsA) (take 2 itemsB)) price)))
    (co/init!)
    (is (= (+ 50 30 20 15) (->> (mapcat #(take 1 %) [itemsA itemsB itemsC itemsD]) price)))))

(deftest test-incremental-price
  (is (= 0 (co/get-price)))
  (co/scan! {:name "A"})
  (is (= 50 (co/get-price)))
  (dotimes [_ 2]
    (co/scan! {:name "B"}))
  (is (= (+ 50 45) (co/get-price)))
  (co/scan! {:name "C"})
  (is (= (+ 50 45 20) (co/get-price)))
  (co/scan! {:name "D"})
  (is (= (+ 50 45 20 15) (co/get-price))))