(ns pozdnyshev.kata05
  (:require [digest :refer :all]))

; ===========================================================================
;; utils

(defn str->ascii [s]
  (reduce #(+ %1 (int %2)) 0 s))

; ===========================================================================
;; state
(def bloom-filter (atom {:hash-fs  []
                         :bitmap []} :validator map?))

(defn middleware-fn [f]
  (fn [x]
    (mod (str->ascii (f x))
         (count (:bitmap @bloom-filter)))))

(def hash-fs
  (map middleware-fn [digest/md2 digest/md5 digest/sha-1 digest/sha-256 digest/sha-512]))

; ===========================================================================
;; API

(defn init! [m k]
  (assert (<= k (count hash-fs)) (format "currently available %s hash functions" (count hash-fs)))
  (swap! bloom-filter (fn [_]
                        {:hash-fs  (take k hash-fs)
                         :bitmap (into [] (repeat m 0))})))

(defn add! [x]
  (doseq [f (:hash-fs @bloom-filter)]
    (let [bf @bloom-filter
          idx (f x)]
      (swap! bloom-filter (fn [_]
                            (update bf :bitmap
                                    (fn [_] (assoc (:bitmap bf) idx 1))))))))

(defn in? [x]
  (let [bf @bloom-filter
        idxs (map #(% x) (:hash-fs bf))]
    (every? #(= 1 %) (map (:bitmap bf) idxs))))