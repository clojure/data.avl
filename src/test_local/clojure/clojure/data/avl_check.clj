(ns clojure.data.avl-check
  (:require [clojure.data.avl :as avl]
            [collection-check :refer [assert-map-like assert-set-like]])
  (:use clojure.test))

(def igen simple-check.generators/int)

(deftest collection-check
  (are [x] (assert-map-like x igen igen)
    (avl/sorted-map)
    (avl/sorted-map-by >))
  (are [x] (assert-set-like x igen)
    (avl/sorted-set)
    (avl/sorted-set-by >)))
