(ns pozdnyshev.trie)


(defn create
  ([]
   (create nil))
  ([s]
   {:value    s
    :children []}))

(defn find' [trie c])

(defn add [trie s]
  (loop [ptr trie
         cur trie
         c s]
    (if-not c
      ptr
      (let [fst (first s)
            tmp (find' cur c)]
        (when-not tmp
          (let [new-cur (update fst :children (fn [t]
                                                (conj (:children t) tmp)))]))
        (recur ())))))