(ns clojure.data.avl-test
  (:use clojure.test)
  (:require [clojure.data.avl :as avl]))

(defmacro deftreesize [name default]
  `(def ~name
     (if-let [size# (System/getProperty
                     ~(str "org.clojure.data.avl.test." name))]
       (Long/parseLong size#)
       ~default)))

(deftreesize small-tree-size  150)
(deftreesize medium-tree-size 2500)
(deftreesize large-tree-size  10000)

(defn validate-invariant [^clojure.data.avl.IAVLTree coll]
  (let [tree (.getTree coll)
        h (fn [^clojure.data.avl.IAVLNode node]
            (if node
              (.getHeight node)
              0))]
    (or (nil? tree)
        (boolean
         (#{-1 0 1} (- (h (.getLeft tree)) (h (.getRight tree))))))))

(defn twice [x]
  [x x])

(def ks   (range large-tree-size))
(def ksks (doall (interleave ks ks)))
(def ks'  (doall (map first (partition 2 ks))))

(def rb-map  (apply sorted-map ksks))
(def avl-map (apply avl/sorted-map ksks))

(def rb-set  (apply sorted-set ks))
(def avl-set (apply avl/sorted-set ks))

(def rb-map-by->  (apply sorted-map-by > ksks))
(def avl-map-by-> (apply avl/sorted-map-by > ksks))

(def rb-set-by->  (apply sorted-set-by > ks))
(def avl-set-by-> (apply avl/sorted-set-by > ks))

(def even-numbers (apply avl/sorted-set (range 0 large-tree-size 2)))

(defn is-same-coll [a b]
  (let [msg  (format "(class a)=%s (class b)=%s a=%s b=%s"
                     (.getName (class a)) (.getName (class b)) a b)
        size (fn [x]
               (if (map? x)
                 (.size ^java.util.Map x)
                 (.size ^java.util.Set x)))]
    (is (= (count a) (count b) (size a) (size b)) msg)
    (is (= a b) msg)
    (is (= b a) msg)
    (is (.equals ^Object a b) msg)
    (is (.equals ^Object b a) msg)
    (is (= (hash a) (hash b)) msg)
    (is (= (.hashCode ^Object a) (.hashCode ^Object b)) msg)))

(deftest sanity-checks
  (testing "AVL collections look like regular sorted collections"
    (is-same-coll rb-map avl-map)
    (is-same-coll rb-set avl-set)
    ;; Check empty maps are equal, and hashes equal
    (is-same-coll (empty rb-map) (empty avl-map))
    (is-same-coll (empty rb-set) (empty avl-set)))
  (testing "AVL collections with custom comparators looks like regular ones"
    (is-same-coll rb-map-by-> avl-map-by->)
    (is-same-coll rb-set-by-> avl-set-by->))
  (testing "AVL collection seqs look like regular sorted collection seqs"
    (is (= (seq rb-map) (seq avl-map)))
    (is (= (seq rb-set) (seq avl-set)))
    (is (= (subseq rb-map > 100 < 1000) (subseq avl-map > 100 < 1000)))
    (is (= (subseq rb-set > 100 < 1000) (subseq avl-set > 100 < 1000))))
  (testing "non-transient construction works as expected"
    (is-same-coll avl-map (reduce-kv assoc (avl/sorted-map) rb-map)))
  (testing "dissoc/dissoc! work as expected"
    (is-same-coll (reduce dissoc rb-map ks') (reduce dissoc avl-map ks'))
    (is-same-coll (reduce dissoc rb-map ks')
                  (persistent! (reduce dissoc! (transient avl-map) ks'))))
  (testing "disj/disj! work as expected"
    (is-same-coll (reduce disj rb-set ks') (reduce disj avl-set ks'))
    (is-same-coll (reduce disj rb-set ks')
                  (persistent! (reduce disj! (transient avl-set) ks'))))
  (testing "*-by seqs look like they should"
    (is (= (seq rb-map-by->) (seq avl-map-by->)))
    (is (= (seq rb-set-by->) (seq avl-set-by->))))
  (testing "reduce-kv returns correct values"
    (is (= (reduce-kv + 0 rb-map) (reduce-kv + 0 avl-map)))
    (is (= (reduce-kv + 0 rb-map-by->) (reduce-kv + 0 avl-map-by->)))))

(deftest standalone-checks
  (testing "seq"
    (is (= (seq avl-map) (map twice ks)))
    (is (= (seq avl-set) ks)))
  (testing "seq on *-by"
    (is (= (seq (apply avl/sorted-map-by > (interleave (range 32) (range 32))))
           (reverse (map (juxt identity identity) (range 32)))))
    (is (= (seq (apply avl/sorted-set-by > (range 32)))
           (reverse (range 32)))))
  (testing "reduce-kv short-circuits appropriately"
    (is (= (reduce-kv (fn [acc k v] (reduced acc)) :foo avl-map) :foo))
    (is (= (reduce-kv (fn [acc k v]
                        (if (== (quot large-tree-size 3) k)
                          (reduced k)
                          acc))
                      nil
                      avl-map)
           (quot large-tree-size 3)))
    (is (= (let [counter (atom 0)]
             (reduce-kv (fn [acc k v]
                          (if (== (quot large-tree-size 3) k)
                            (reduced k)
                            (swap! counter inc)))
                        nil
                        avl-map)
             @counter)
           (quot large-tree-size 3)))))

(deftest rank-queries
  (testing "map rank queries work as expected"
    (is (every? true? (map = ks (map #(key (nth avl-map %)) ks))))
    (is (every? true?
                (map = (reverse ks) (map #(key (nth avl-map-by-> %)) ks))))
    (is (->> (map #(nth avl-map-by-> (avl/rank-of avl-map-by-> %)) ks)
             (map first)
             (map = ks)
             (every? true?)))
    (is (every? #(== % -1) (map #(avl/rank-of avl-map %) [-10 123.5 200000]))))
  (testing "set rank queries work as expected"
    (is (every? true? (map = ks (map #(nth avl-set %) ks))))
    (is (every? true? (map = (reverse ks) (map #(nth avl-set-by-> %) ks))))
    (is (->> (map #(nth avl-set-by-> (avl/rank-of avl-set-by-> %)) ks)
             (map = ks)
             (every? true?)))
    (is (every? #(== % -1) (map #(avl/rank-of avl-set %) [-10 123.5 200000]))))
  (testing "rank-of, nth and contains? agree on sets"
    (is (every? (fn [x]
                  (or (and (not (contains? even-numbers x))
                           (== -1 (avl/rank-of even-numbers x)))
                      (and (contains? even-numbers x)
                           (== (nth even-numbers (avl/rank-of even-numbers x))
                               x))))
                (range (dec (apply min (seq even-numbers)))
                       (+ 2 (apply max (seq even-numbers))))))))

(def keys-for-nearest [-1 0 1 2 3 4 5 6 7 8 9])
(def set-for-nearest  (avl/sorted-set 0 2 4 6 8))
(def rset-for-nearest (avl/sorted-set-by > 0 2 4 6 8))

(defn subseq-nearest [coll test x]
  (let [subseq* (if (#{< <=} test) rsubseq subseq)]
    (first (subseq* coll test x))))

(deftest nearest
  (testing "nearest should find the correct element or nil"
    (doseq [s [set-for-nearest rset-for-nearest]
            t [< <= >= >]]
      (is (= (map #(avl/nearest s t %) keys-for-nearest)
             (map #(subseq-nearest s t %) keys-for-nearest))))))

(def small-ks   (range small-tree-size))
(def small-ksks (doall (interleave small-ks small-ks)))

(def small-avl-set   (apply avl/sorted-set small-ks))
(def small-avl-map   (apply avl/sorted-map small-ksks))
(def small-avl-set-> (apply avl/sorted-set-by > small-ks))
(def small-avl-map-> (apply avl/sorted-map-by > small-ksks))

(defn subseq-subrange [coll low high]
  (into (empty coll) (subseq coll >= low <= high)))

(deftest subrange
  (testing "subrange should return the correct result"
    (doseq [coll [small-avl-set small-avl-map]
            i    (range -1 (inc small-tree-size))
            j    (range i  (inc small-tree-size))]
      (is (= (avl/subrange coll >= i <= j) (subseq-subrange coll i j))))
    (doseq [coll [small-avl-set-> small-avl-map->]
            i    (range small-tree-size -2 -1)
            j    (range i -2 -1)]
      (is (= (avl/subrange coll >= i <= j) (subseq-subrange coll i j))))))

(defn subseq-split-key [x coll]
  (let [e (empty coll)]
    [(into e (subseq coll < x))
     (if (contains? coll x)
       (if (map? coll)
         (find coll x)
         (get coll x)))
     (into e (subseq coll > x))]))

(defn subseq-split-at [n coll]
  [(into (empty coll) (take n coll))
   (into (empty coll) (drop n coll))])

(deftest split
  (testing "split-key should return the correct result"
    (doseq [coll [small-avl-set small-avl-map
                  small-avl-set-> small-avl-map->]
            i    (range -1 (inc small-tree-size))]
      (is (= (avl/split-key i coll) (subseq-split-key i coll)))))
  (testing "split-at should return the correct result"
    (doseq [coll [small-avl-set small-avl-map
                  small-avl-set-> small-avl-map->]
            i    (range 0 (inc small-tree-size))]
      (is (= (avl/split-at i coll) (subseq-split-at i coll))))))

(def midsize-ks (range medium-tree-size))

(deftest avl-invariant
  (testing "AVL invariant is maintained at all times"
    (let [p (atom (avl/sorted-set))
          t (atom (avl/sorted-set))]
      (doseq [k midsize-ks]
        (let [s (swap! p conj k)
              t (swap! t (comp persistent! #(conj! % k) transient))]
          (is (validate-invariant s))
          (is (validate-invariant t))))
      (doseq [k midsize-ks]
        (let [[l _ r] (avl/split-key k @p)
              [l' r'] (avl/split-at k @p)]
          (is (validate-invariant l))
          (is (validate-invariant r))
          (is (validate-invariant l'))
          (is (validate-invariant r'))))
      (doseq [k midsize-ks]
        (let [s (swap! p disj k)
              t (swap! t (comp persistent! #(conj! % k) transient))]
          (is (validate-invariant s))
          (is (validate-invariant t)))))))
