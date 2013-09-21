(ns avl.clj-test
  (:use clojure.test)
  (:require [avl.clj :as avl]))

(def ks   (range 100000))
(def ksks (doall (interleave ks ks)))
(def ks'  (doall (map first (partition 2 ks))))

(def rb-map  (apply sorted-map ksks))
(def avl-map (apply avl/sorted-map ksks))

(def rb-set  (apply sorted-set ks))
(def avl-set (apply avl/sorted-set ks))

(deftest sanity-checks
  (testing "AVL collections look like regular sorted collections"
    (is (= rb-map avl-map))
    (is (= rb-set avl-set)))
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
    (is (= (seq (apply avl/sorted-map-by > (interleave (range 32) (range 32))))
           (reverse (map (juxt identity identity) (range 32)))))
    (is (= (seq (apply avl/sorted-set-by > (range 32)))))))

(deftest rank-queries
  (testing "map rank queries work as expected"
    (is (every? true? (map = ks (map #(key (nth avl-map %)) ks)))))
  (testing "set rank queries work as expected"
    (is (every? true? (map = ks (map #(nth avl-set %) ks))))))
