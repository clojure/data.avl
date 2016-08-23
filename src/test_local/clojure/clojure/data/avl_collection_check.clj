(ns clojure.data.avl-collection-check
  (:require [clojure.data.avl :as avl]
            [collection-check
             :refer [assert-map-like assert-set-like
                     assert-equivalent-maps assert-equivalent-sets]]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop])
  (:use clojure.test))

(def igen gen/int)

(deftest collection-check
  (are [x] (assert-map-like x igen igen)
    (avl/sorted-map)
    (avl/sorted-map-by >))
  (are [x] (assert-set-like x igen)
    (avl/sorted-set)
    (avl/sorted-set-by >)))

(defn validate-tree
  ([tree]
     (if (or (map? tree) (set? tree))
       (do
         (assert (== (count tree)
                     (peek (validate-tree
                            (.comparator ^clojure.lang.Sorted tree)
                            (.getTree ^clojure.data.avl.IAVLTree tree)))))
         true)
       (validate-tree clojure.lang.RT/DEFAULT_COMPARATOR tree)))
  ([^java.util.Comparator comp ^clojure.data.avl.IAVLNode tree]
     (if (nil? tree)
       [0 0]
       (let [left      (.getLeft tree)
             right     (.getRight tree)
             [lh lcnt] (validate-tree comp left)
             [rh rcnt] (validate-tree comp right)
             h         (inc (max lh rh))]
         (if left
           (assert (neg? (.compare comp (.getKey left) (.getKey tree)))))
         (if right
           (assert (neg? (.compare comp (.getKey tree) (.getKey right)))))
         (assert (#{-1 0 1} (- lh rh)))
         (assert (== lh (#'avl/height left)))
         (assert (== rh (#'avl/height right)))
         (assert (== lcnt (.getRank tree)))
         (assert (== h (.getHeight tree)))
         [h (inc (+ lcnt rcnt))]))))

(deftest avl-invariant
  (testing "AVL invariant is maintained when inserting keys in random order"
    (dotimes [_ 500]
      (let [ks (shuffle (range 500))]
        (validate-tree (apply avl/sorted-set ks))
        (validate-tree (apply avl/sorted-set-by > ks))))))

(defspec print-dup-map-round-trip 100
  (prop/for-all [xs (gen/vector gen/int)]
    (let [m1 (into (avl/sorted-map) (map #(vector % %) xs))
          m2 (read-string (with-out-str (print-dup m1 *out*)))]
      (try
        (assert-equivalent-maps m1 m2)
        true
        (catch AssertionError _
          false)))))

(defspec print-dup-set-round-trip 100
  (prop/for-all [xs (gen/vector gen/int)]
    (let [s1 (into (avl/sorted-set) xs)
          s2 (read-string (with-out-str (print-dup s1 *out*)))]
      (try
        (assert-equivalent-sets s1 s2)
        true
        (catch AssertionError _
          false)))))

(defspec reduce-set 100
  (prop/for-all [xs (gen/vector gen/int)]
    (let [s (into (avl/sorted-set) xs)]
      (= (reduce + s)
         (reduce + 0 s)
         (reduce + 0 (distinct xs))))))

(defspec reduce-map 100
  (prop/for-all [xs (gen/vector gen/int)]
    (let [m (reduce (fn [out x] (assoc out x x)) (avl/sorted-map) xs)
          f (fn
              ([] [])
              ([out] out)
              ([out [k v]] (conj out k v)))]
      (= (reduce f m)
         (reduce f [] m)
         (reduce f [] (map #(vector % %) (sort (distinct xs))))))))

(defspec subrange-subseq 100
  (prop/for-all [xs (gen/vector gen/int)
                 i  gen/int
                 j  gen/int]
    (let [low  (min i j)
          high (max i j)
          s1 (into (avl/sorted-set) xs)
          s2 (into (sorted-set) xs)]
      (= (seq (avl/subrange s1 >= low <= high))
         (seq (subseq s1 >= low <= high))
         (seq (subseq s2 >= low <= high))))))

(defspec subrange-low-reduce 100
  (prop/for-all [xs  (gen/vector gen/int)
                 low gen/int]
    (let [s1 (into (avl/sorted-set) xs)
          s2 (into (sorted-set) xs)
          sub1 (into #{}
                 (map inc)
                 (avl/subrange s1 >= low))
          sub2 (into #{}
                 (map inc)
                 (subseq s2 >= low))]
      (try
        (assert-equivalent-sets sub1 sub2)
        true
        (catch AssertionError _
          false)))))

(defspec subrange-high-reduce 100
  (prop/for-all [xs   (gen/vector gen/int)
                 high gen/int]
    (let [s1 (into (avl/sorted-set) xs)
          s2 (into (sorted-set) xs)
          sub1 (into #{}
                 (map inc)
                 (avl/subrange s1 <= high))
          sub2 (into #{}
                 (map inc)
                 (subseq s2 <= high))]
      (try
        (assert-equivalent-sets sub1 sub2)
        true
        (catch AssertionError _
          false)))))

(defspec subrange-low-high-reduce 100
  (prop/for-all [xs (gen/vector gen/int)
                 i  gen/int
                 j  gen/int]
    (let [low  (min i j)
          high (max i j)
          s1 (into (avl/sorted-set) xs)
          s2 (into (sorted-set) xs)
          sub1 (into #{}
                 (map inc)
                 (avl/subrange s1 >= low <= high))
          sub2 (into #{}
                 (map inc)
                 (subseq s2 >= low <= high))]
      (try
        (assert-equivalent-sets sub1 sub2)
        true
        (catch AssertionError _
          false)))))
