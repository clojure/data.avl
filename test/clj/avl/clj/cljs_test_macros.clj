(ns avl.clj.cljs-test-macros
  (:require [clojure.walk :as walk]))

(def tests (atom []))

(defmacro deftest [name & body]
  (swap! tests conj
         (symbol (clojure.core/name (.-name *ns*)) (clojure.core/name name)))
  `(defn ~name []
     ~@(walk/prewalk (fn [f]
                       (if (seq? f)
                         (condp = (first f)
                           'is (cons 'assert (next f))
                           'testing (list* 'do
                                           (list 'println (second f))
                                           (nnext f))
                           f)
                         f))
                     body)))

(defmacro run-tests []
  `(do ~'(set-print-fn! js/print)
       (~'this-as ~'this
         (let [tests# ~(deref tests)]
           (doseq [t# tests#]
             (t#))))
       (println "Tests completed without exception.")))
