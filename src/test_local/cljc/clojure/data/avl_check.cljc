(ns clojure.data.avl-check
  (:require [clojure.data.avl :as avl]
            #?(:clj [collection-check.core
                     :refer [assert-map-like assert-set-like
                             assert-equivalent-maps assert-equivalent-sets]])
            #?(:cljs clojure.test.check)
            [clojure.test.check.clojure-test
             #?@(:clj [:refer [defspec]]
                 :cljs [:refer-macros [defspec]])]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop
             #?@(:cljs [:include-macros true])])
  (:use #?@(:clj [[clojure.template :only [do-template]]
                  clojure.test]
            :cljs [[cljs.test :only [deftest testing are]]])))

(def igen gen/int)

#?(:clj
   (deftest collection-check
     (do-template [x] (assert-map-like x igen igen)
       (avl/sorted-map)
       (avl/sorted-map-by >))
     (do-template [x] (assert-set-like x igen)
       (avl/sorted-set)
       (avl/sorted-set-by >))))

#?(:cljs
   (defn assert-equivalent-sets [s1 s2]
     (and (= s1 s2)
          (= s2 s1)
          (== (count s1) (count s2)))))

(defn validate-tree
  ([tree]
     (if (or (map? tree) (set? tree))
       (do
         (assert (== (count tree)
                     (peek (validate-tree
                            #?(:clj (.comparator ^clojure.lang.Sorted tree)
                               :cljs (-comparator tree))
                            (.getTree #?(:clj ^clojure.data.avl.IAVLTree tree
                                         :cljs tree))))))
         true)
       (validate-tree #?(:clj clojure.lang.RT/DEFAULT_COMPARATOR
                         :cljs compare)
                      tree)))
  (#?(:clj [^java.util.Comparator comp ^clojure.data.avl.IAVLNode tree]
      :cljs [comp tree])
     (if (nil? tree)
       [0 0]
       (let [left      (.getLeft tree)
             right     (.getRight tree)
             [lh lcnt] (validate-tree comp left)
             [rh rcnt] (validate-tree comp right)
             h         (inc (max lh rh))]
         (if left
           (assert (neg? (#?(:clj .compare) comp
                          (.getKey left) (.getKey tree)))))
         (if right
           (assert (neg? (#?(:clj .compare) comp
                          (.getKey tree) (.getKey right)))))
         (assert (#{-1 0 1} (- lh rh)))
         (assert (== lh (#?(:clj #'avl/height :cljs avl/height) left)))
         (assert (== rh (#?(:clj #'avl/height :cljs avl/height) right)))
         (assert (== lcnt (.getRank tree)))
         (assert (== h (.getHeight tree)))
         [h (inc (+ lcnt rcnt))]))))

(defspec avl-invariant 100
  (testing "AVL invariant is maintained when inserting keys in random order"
    (prop/for-all [ks (gen/vector gen/int)]
      (try
        (validate-tree (apply avl/sorted-set ks))
        (validate-tree (apply avl/sorted-set-by > ks))
        (validate-tree (reduce conj (avl/sorted-set) ks))
        (validate-tree (reduce conj (avl/sorted-set-by >) ks))
        true
        (catch #?(:clj AssertionError :cljs :default) _
          false)))))

(defspec avl-invariant-500 100
  (testing "AVL invariant is maintained when inserting 500 keys in random order"
    (prop/for-all [ks (gen/shuffle (range 500))]
      (try
        (validate-tree (apply avl/sorted-set ks))
        (validate-tree (apply avl/sorted-set-by > ks))
        (validate-tree (reduce conj (avl/sorted-set) ks))
        (validate-tree (reduce conj (avl/sorted-set-by >) ks))
        true
        (catch #?(:clj AssertionError :cljs :default) _
            false)))))

(defn disj!-all [coll ks]
  (persistent! (reduce disj! (transient coll) ks)))

(defn disj-all [coll ks]
  (reduce disj coll ks))

(defspec avl-invariant-with-removals 100
  (testing "AVL invariant is maintained when inserting & removing  keys"
    (prop/for-all [[ks ks']
                   (gen/fmap (fn [ks]
                               [ks (subvec ks (quot (count ks) 2))])
                             (gen/vector gen/int))]
      (try
        (validate-tree (disj!-all (apply avl/sorted-set ks) ks'))
        (validate-tree (disj!-all (apply avl/sorted-set-by > ks) ks'))
        (validate-tree (disj-all (reduce conj (avl/sorted-set) ks) ks'))
        (validate-tree (disj-all (reduce conj (avl/sorted-set-by >) ks) ks'))
        true
        (catch #?(:clj AssertionError :cljs :default) _
          false)))))

#?(:clj
   (defspec print-dup-map-round-trip 100
     (prop/for-all [xs (gen/vector gen/int)]
       (let [m1 (into (avl/sorted-map) (map #(vector % %) xs))
             m2 (read-string (with-out-str (print-dup m1 *out*)))]
         (try
           (assert-equivalent-maps m1 m2)
           true
           (catch AssertionError _
             false))))))

#?(:clj
   (defspec print-dup-set-round-trip 100
     (prop/for-all [xs (gen/vector gen/int)]
       (let [s1 (into (avl/sorted-set) xs)
             s2 (read-string (with-out-str (print-dup s1 *out*)))]
         (try
           (assert-equivalent-sets s1 s2)
           true
           (catch AssertionError _
             false))))))

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
        (catch #?(:clj AssertionError :cljs :default) _
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
        (catch #?(:clj AssertionError :cljs :default) _
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
        (catch #?(:clj AssertionError :cljs :default) _
          false)))))
