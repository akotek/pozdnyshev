(ns pozdnyshev.spec
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as stest])
  (:import (java.util UUID)))

;; ===========================================================================
;; utils

(defn str-uuid? [s]
  (try (UUID/fromString s) true
       (catch Exception _ false)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;; SPEC, general:
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(s/def ::customer_id str-uuid?)

(s/def ::product_id str-uuid?)

(s/def ::product_type_id (set (range 8)))

(s/def ::progress string?)

(s/def ::product (s/keys :req-un [::product_type_id ::product_id ::progress]))

(s/def ::products coll?)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; SPEC /who

(s/def ::user (s/keys :req-un [::products ::customer_id]))

(s/def ::ab_testing #{"a" "b"})

(s/def ::token string?)


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn identity-handler [{:keys [::secret ::customers ::misc data]}]
  (let [c-id (if (:customer_id data) (:customer_id data) (str (UUID/randomUUID)))
        dice (* 100 (float (/ (Integer/parseInt (subs c-id (- (.length c-id) 2)) 16) 255)))
        products [{:product_type_id 1
                   :product_id      (str (UUID/randomUUID))
                   :progress        "get-life-insurance/zipcode"}]
        user {:products products :customer_id c-id}]
    {:user user :ab_testing "a" :token "token" :types [{:id "1" :type "Term Life" :sub_type "Term Life Regular"}]}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(s/def ::who-data (s/keys :req-un [::customer_id]))

(s/def ::who-ret (s/keys :req-un [::user ::ab_testing ::token ::types]))

(s/fdef identity-handler
        :args (s/cat :data ::who-data)
        :ret ::who-ret)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


;; REPL

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; General spec
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(s/def ::customer_id str-uuid?)
(s/def ::product_type_id int?)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn who [data]
  5)

(s/def ::who-data (s/keys :req-un [::customer_id
                                   ::product_type_id]))

(s/valid? ::who-data {:customer_id "a"
                      :product_type_id 1})

(s/def ::who-ret int?)

(s/fdef who
        :args (s/cat :data ::who-data)
        :ret ::who-ret)

(doc who)
(stest/instrument `who)

(who {:customer_id "a"
      :product_type_id "b"})

(who {:customer_id (str (UUID/randomUUID))
      :product_type_id 1})

;; Usages:

;; validation
;; generative testing (instead of example based testing)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; validation
(defmacro with-validation [spec-k params & body]
  `(if (s/valid? ~spec-k ~params)
     (do ~@body)
     (s/explain ~spec-k ~params)))  ;transform to some better messaging system


(defn who-handler [client params]
  (with-validation ::who-data params
                   (let [x 5]
                     x)))




(who-handler "client" {:a "b"})




;; Q's:
; 1. where to hold all s/defs? same NS ? different NS ?
; instrumentation?