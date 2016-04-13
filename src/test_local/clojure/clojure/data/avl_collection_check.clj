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
      (assert-equivalent-maps m1 m2)
      true)))

(defspec print-dup-set-round-trip 100
  (prop/for-all [xs (gen/vector gen/int)]
    (let [s1 (into (avl/sorted-set) xs)
          s2 (read-string (with-out-str (print-dup s1 *out*)))]
      (assert-equivalent-sets s1 s2)
      true)))
