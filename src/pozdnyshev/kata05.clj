(ns pozdnyshev.kata05
  (:require [digest :refer :all]))

; ===========================================================================
;; utils

(defn str->ascii [s]
  (reduce #(+ %1 (int %2)) 0 s))

; ===========================================================================
;; state
(def bloom-filter (atom {:max-size 0
                         :hash-fs  []
                         :sequence []} :validator map?))

(defn middleware-fn [f]
  (fn [x]
    (mod (str->ascii (f x))
         (count (:sequence @bloom-filter)))))

(def hash-fs
  (map middleware-fn [digest/md2 digest/md5 digest/sha-1 digest/sha-256 digest/sha-512]))

; ===========================================================================
;; API

(defn init! [m k]
  (assert (<= k (count hash-fs)) (format "currently available %s hash functions" (count hash-fs)))
  (swap! bloom-filter (fn [_]
                        {:max-size m
                         :hash-fs  (take k hash-fs)
                         :sequence (into [] (repeat m 0))})))

(defn add! [x]
  (doseq [f (:hash-fs @bloom-filter)]
    (let [bf @bloom-filter
          idx (f x)]
      (swap! bloom-filter (fn [_] (update bf :sequence
                                          (fn [_]
                                            (assoc (:sequence bf) idx 1))))))))

(defn in? [x]
  (let [bf @bloom-filter
        idxs (map #(% x) (:hash-fs bf))]
    (every? #(= 1 %) (map (:sequence bf) idxs))))