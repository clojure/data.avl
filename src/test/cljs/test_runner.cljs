(ns test-runner
  (:require [clojure.data.avl-test :as avl-test])
  (:require-macros [clojure.data.avl.cljs-test-macros :refer [run-tests]]))

(run-tests)
