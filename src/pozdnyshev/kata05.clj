(ns pozdnyshev.kata05
  (:require (bigml.sketchy [murmur :as murmur])))

; ===========================================================================
;; utils

(defn bm-idxs [bm x seed-count]
  (map (fn [h seed]
         (mod (murmur/hash h seed)
              (count bm)))
       (repeat seed-count (.hashCode x)) (range seed-count)))

; ===========================================================================
;; API

(defn create [m k]
  {:hs-len k
   :bm-len m
   :bitmap (into [] (repeat m 0))})

(defn insert [{:keys [bitmap hs-len] :as bloom} x]
  (let [idxs (bm-idxs bitmap x hs-len)]
    (assoc bloom :bitmap (reduce #(update %1 %2 (fn [_] 1))
                                 bitmap idxs))))

(defn in? [{:keys [bitmap hs-len]} x]
  (let [idxs (bm-idxs bitmap x hs-len)]
    (= (count idxs) (reduce + 0 (map bitmap idxs)))))