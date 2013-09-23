(ns test-runner
  (:require [avl.clj-test :as avl-test])
  (:require-macros [avl.clj.cljs-test-macros :refer [run-tests]]))

(run-tests)
