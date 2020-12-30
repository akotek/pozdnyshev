(ns pozdnyshev.kata05)

; ===========================================================================
;; hash functions:

(defn md5 [s])

(defn sha-1 [s])

(defn sha-2 [s])

(defn whirpool [s])

(defn str->ascii [s]
  (reduce #(+ %1 (int %2)) 0 s))

; ===========================================================================
;; state
(def bloom-filter (atom {} :validator map?))

(defn middleware-fn [f]
  (fn [x]
    (mod (str->ascii (f x))
         (count (:sequence @bloom-filter)))))

(def hash-fs
  (map middleware-fn [md5 sha-1 sha-2 whirpool]))

; ===========================================================================
;; API

(defn init! [m k]
  (assert (< k (count hash-fs)) (format "currently available %s hash functions" (count hash-fs)))
  (swap! bloom-filter {:max-size m
                       :hash-fs  (take k hash-fs)
                       :sequence (repeat m 0)}))

(defn set! [s]
  (doseq [f (:hash-fs @bloom-filter)]
    (let [bf @bloom-filter
          idx (f s)]
      (swap! bloom-filter (fn [_] (update-in bf [:sequence idx] 1))))))