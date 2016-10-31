(ns clojure.data.avl-test-runner
  (:require [cljs.test :refer-macros [run-tests]]
            clojure.data.avl-check
            clojure.data.avl-split-key-test
            clojure.data.avl-test))


(enable-console-print!)

(run-tests 'clojure.data.avl-test
           'clojure.data.avl-check
           'clojure.data.avl-split-key-test)
