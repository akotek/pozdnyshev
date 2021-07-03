(ns pozdnyshev.deck-of-cards
  (:require [clojure.set :as set]))

; ===========================================================================
;; utils

(def ranks (range 2 15))

(def suits [:Clubs :Diamonds :Hearts :Spades])

(def cards (atom
             (into #{}
                   (for [r ranks
                         s suits]
                     {:rank r
                      :suit s}))))

(defn swap [v idx1 idx2]
  (assoc v idx1 (v idx2) idx2 (v idx1)))

(defn drop-idx [idx coll]
  (into (subvec coll 0 idx) (subvec coll (inc idx))))

; ===========================================================================
;; API

(defn deal-card []
  (let [idx (rand (count @cards))
        card (first (drop idx @cards))]
    (swap! cards set/difference #{card})
    card))

(defn shuffle' []
  (swap! cards #(shuffle %)))

(defn shuffle'' []
  ;; some O(n2) shuffling
  (loop [crds (vec @cards)
         aux crds
         n 0]
    (if (= n (count crds))
      (swap! cards (fn [_] (set crds)))
      (let [idx2 (rand-int (count aux))]
        (recur (swap crds n idx2) (drop-idx idx2 crds) (inc n))))))


(comment
  (shuffle')
  (deal-card))