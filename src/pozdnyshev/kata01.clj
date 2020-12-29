(ns pozdnyshev.kata01
  (:require [clojure.string :as str]))

; ===========================================================================
;; utils

(defn str->price-tuple [s]
  (let [[first _ third] (str/split s #" ")]
    [(read-string first) (read-string third)]))

(defn price-matrix->price-map [M]
  (->> M
       (map (fn [[item price special-price]]
              {item {:price    price
                     :sp-price (when special-price
                                 (str->price-tuple special-price))}}))
       (into {})))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;; State
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def pricing-rules (atom {} :validator map?))

(def co-initial {:total-price 0
                 :items-count {}})

(def co (atom co-initial :validator map?))

(defn apply-special-price? [items-count sp-price]
  (and sp-price
       (pos? (int (/ items-count sp-price)))))

(defn addition-price [cur-co name]
  (let [{:keys [price sp-price]} (get @pricing-rules name)
        items-count (get-in cur-co [:items-count name] 0)]
    (if (apply-special-price? items-count (first sp-price))
      (- (last sp-price) (* (dec (first sp-price)) price))
      price)))

; ===========================================================================
;; API

(defn init!
  ([]
   (init! nil))
  ([M]
   (when M
     (swap! pricing-rules (fn [_] (price-matrix->price-map M))))
   (reset! co co-initial)))

(defn scan! [{:keys [name]}]
  (let [inced-co (update-in @co [:items-count name] (fnil inc 0))]
    (swap! co (fn [_]
                (update inced-co :total-price + (addition-price inced-co name))))))

(defn get-price []
  (-> @co :total-price))