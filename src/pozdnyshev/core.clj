(ns pozdnyshev.core)


; ===========================================================================
;; utils



; ===========================================================================
;; API

;; history | awk '$2 == "git" {print $3}' > history.txt

(defn build-graph [coll]
  (->> (partition 2 1 coll)
       (reduce (fn [res [fst scd]]
                 (update-in res [fst scd] (fnil inc 0)))
               {})))