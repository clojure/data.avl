(defproject org.clojure/data.avl "0.0.14-SNAPSHOT"
  :description "Persistent sorted maps and sets with log-time rank queries"
  :url "https://github.com/clojure/data.avl"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :min-lein-version "2.6.1"
  :parent [org.clojure/pom.contrib "0.1.2"]
  :dependencies [[org.clojure/clojure "1.5.1"]]
  :jvm-opts ^:replace ["-Dorg.clojure.data.avl.test.large-tree-size=100000"
                       "-Dorg.clojure.data.avl.test.medium-tree-size=100000"
                       "-Dorg.clojure.data.avl.test.small-tree-size=300"]
  :source-paths ["src/main/clojure" "src/main/cljs"]
  :test-paths ["src/test/clojure"]
  :aliases {"all" ["with-profile" "dev:dev,1.6:dev,1.7:dev,1.8"]}
  :profiles {:dbg  {:jvm-opts ["-XX:-OmitStackTraceInFastThrow"]}
             :cljs {:dependencies [[org.clojure/clojure "1.9.0-alpha10"]
                                   [org.clojure/clojurescript "1.9.216"]]
                    :hooks [leiningen.cljsbuild]
                    :plugins [[lein-cljsbuild "1.1.3"]]
                    :cljsbuild
                    {:test-commands {"phantom" ["phantomjs" "out.test.js"]}
                     :builds {:test
                              {:source-paths ["src/main/cljs"
                                              "src/test/clojure"
                                              "src/test/cljs"]
                               :compiler {:output-to "out/test.js"
                                          :main test-runner
                                          :optimizations :advanced
                                          :pretty-print false
                                          :static-fns true}}}}}
             :1.6  {:dependencies [[org.clojure/clojure "1.6.0"]]}
             :1.7  {:dependencies [[org.clojure/clojure "1.7.0"]
                                   [org.clojure/test.check "0.9.0"]
                                   [collection-check "0.1.6"]]
                    :test-paths ["src/test_local/clojure"]}
             :1.8  {:dependencies [[org.clojure/clojure "1.8.0"]
                                   [org.clojure/test.check "0.9.0"]
                                   [collection-check "0.1.6"]]
                    :test-paths ["src/test_local/clojure"]}
             :1.9  {:dependencies [[org.clojure/clojure "1.9.0-alpha6"]
                                   [org.clojure/test.check "0.9.0"]
                                   [collection-check "0.1.6"]]
                    :test-paths ["src/test_local/clojure"]}})
