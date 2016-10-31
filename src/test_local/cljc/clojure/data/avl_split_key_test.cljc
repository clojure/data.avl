(ns clojure.data.avl-split-key-test
  (:require [clojure.data.avl :as avl]
            [clojure.test :refer [is]]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]))

(def gen-distinct-vec
  (gen/fmap (comp vec distinct) (gen/vector gen/int)))

(def gen-sorted-map
  (->> gen-distinct-vec
       (gen/fmap #(apply avl/sorted-map
                    (interleave (sort %) (range (count %)))))
       (gen/such-that #(not (empty? %)))))

(defn range-low-high [from to m]
  (let [[_ eq1 gt] (avl/split-key from m)
        [lt eq2 _] (avl/split-key to gt)]
    (cond-> lt
      eq1 (conj eq1)
      eq2 (conj eq2))))

(defn range-high-low [from to m]
  (let [[lt eq1 _] (avl/split-key to m)
        [_ eq2 gt] (avl/split-key from lt)]
    (cond-> gt
      eq1 (conj eq1)
      eq2 (conj eq2))))

(defspec spec-range-low-high
         (prop/for-all
           [[from to] (gen/fmap sort (gen/tuple gen/int gen/int))
            m gen-sorted-map]
           (let [ks (vec (keys m))]
             (is (= (seq (filter #(<= from % to) (keys m)))
                    (seq (keys (range-low-high from to m))))))))

(defspec spec-range-high-low
         (prop/for-all
           [[from to] (gen/fmap sort (gen/tuple gen/int gen/int))
            m gen-sorted-map]
           (let [ks (vec (keys m))]
             (is (= (seq (filter #(<= from % to) (keys m)))
                    (seq (keys (range-high-low from to m))))))))
