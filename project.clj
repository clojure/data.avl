(defproject org.clojure/data.avl "0.0.18-SNAPSHOT"
  :description "Persistent sorted maps and sets with log-time rank queries"
  :url "https://github.com/clojure/data.avl"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :min-lein-version "2.6.1"
  :parent [org.clojure/pom.contrib "1.2.0"]
  :dependencies [[org.clojure/clojure "1.9.0"]]
  :jvm-opts ^:replace ["-Dorg.clojure.data.avl.test.large-tree-size=100000"
                       "-Dorg.clojure.data.avl.test.medium-tree-size=100000"
                       "-Dorg.clojure.data.avl.test.small-tree-size=300"]
  :source-paths ["src/main/clojure" "src/main/cljs"]
  :test-paths ["src/test/clojure"]
  :aliases {"all" ["with-profile" "dev:dev,1.6:dev,1.7:dev,1.8:dev,1.9"]}
  :profiles {:dbg  {:jvm-opts ["-XX:-OmitStackTraceInFastThrow"]}
             :cljs {:dependencies [[org.clojure/clojure "1.10.3"]
                                   [org.clojure/clojurescript "1.10.520"]
                                   [org.clojure/test.check "1.1.1"]
                                   [collection-check "0.1.7"]]
                    :hooks [leiningen.cljsbuild]
                    :plugins [[lein-cljsbuild "1.1.4"]]
                    :cljsbuild
                    {:test-commands {"phantom" ["phantomjs" "out/test.js"]}
                     :builds {:test
                              {:source-paths ["src/main/cljs"
                                              "src/test/clojure"
                                              "src/test_local/cljc"
                                              "src/test/cljs"]
                               :compiler {:output-to "out/test.js"
                                          :main clojure.data.avl-test-runner
                                          :optimizations :advanced
                                          :pretty-print false
                                          :static-fns true}}}}}
             :1.9  {:dependencies [[org.clojure/clojure "1.9.0"]
                                   [org.clojure/test.check "1.1.1"]
                                   [collection-check "0.1.7"]]
                    :test-paths ["src/test_local/cljc"]}})
