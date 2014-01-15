(ns clojure.data.avl-test
  (:use clojure.test)
  (:require [clojure.data.avl :as avl]))

(defn twice [x]
  [x x])

(def ks   (range 100000))
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

(def even-numbers (apply avl/sorted-set (range 0 100000 2)))

(deftest sanity-checks
  (testing "AVL collections look like regular sorted collections"
    (is (= rb-map avl-map))
    (is (= rb-set avl-set)))
  (testing "AVL collections with custom comparators looks like regular ones"
    (is (= rb-map-by-> avl-map-by->))
    (is (= rb-set-by-> avl-set-by->)))
  (testing "AVL collection seqs look like regular sorted collection seqs"
    (is (= (seq rb-map) (seq avl-map)))
    (is (= (seq rb-set) (seq avl-set)))
    (is (= (subseq rb-map > 100 < 1000) (subseq avl-map > 100 < 1000)))
    (is (= (subseq rb-set > 100 < 1000) (subseq avl-set > 100 < 1000))))
  (testing "non-transient construction works as expected"
    (is (= avl-map (reduce-kv assoc (avl/sorted-map) rb-map))))
  (testing "dissoc/dissoc! work as expected"
    (is (= (reduce dissoc rb-map ks') (reduce dissoc avl-map ks')))
    (is (= (reduce dissoc rb-map ks')
           (persistent! (reduce dissoc! (transient avl-map) ks')))))
  (testing "disj/disj! work as expected"
    (is (= (reduce disj rb-set ks') (reduce disj avl-set ks')))
    (is (= (reduce disj rb-set ks')
           (persistent! (reduce disj! (transient avl-set) ks')))))
  (testing "*-by seqs look like they should"
    (is (= (seq rb-map-by->) (seq avl-map-by->)))
    (is (= (seq (apply avl/sorted-set-by > (range 32))))))
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
                        (if (== 31000 k)
                          (reduced k)
                          acc))
                      nil
                      avl-map)
           31000))
    (is (= (let [counter (atom 0)]
             (reduce-kv (fn [acc k v]
                          (if (== 31000 k)
                            (reduced k)
                            (swap! counter inc)))
                        nil
                        avl-map)
             @counter)
           31000))))

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

(def midsize-ks   (range 300))
(def midsize-ksks (doall (interleave midsize-ks midsize-ks)))

(def midsize-avl-set   (apply avl/sorted-set midsize-ks))
(def midsize-avl-map   (apply avl/sorted-map midsize-ksks))
(def midsize-avl-set-> (apply avl/sorted-set-by > midsize-ks))
(def midsize-avl-map-> (apply avl/sorted-map-by > midsize-ksks))

(defn subseq-subrange [coll low high]
  (into (empty coll) (subseq coll >= low <= high)))

(deftest subrange
  (testing "subrange should return the correct result"
    (doseq [coll [midsize-avl-set midsize-avl-map]
            i    (range -1 301)
            j    (range i  301)]
      (is (= (avl/subrange coll i j) (subseq-subrange coll i j))))
    (doseq [coll [midsize-avl-set-> midsize-avl-map->]
            i    (range 300 -2 -1)
            j    (range i -2 -1)]
      (is (= (avl/subrange coll i j) (subseq-subrange coll i j))))))

(defn subseq-split [coll x]
  (let [e (empty coll)]
    [(into e (subseq coll < x))
     (if (contains? coll x)
       (if (map? coll)
         (find coll x)
         (get coll x)))
     (into e (subseq coll > x))]))

(deftest split
  (testing "split-at should return the correct result"
    (doseq [coll [midsize-avl-set midsize-avl-map
                  midsize-avl-set-> midsize-avl-map->]
            i    (range -1 301)]
      (is (= (avl/split-at coll i) (subseq-split coll i))))))
