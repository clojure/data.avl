{:namespaces
 ({:doc
   "An implementation of persistent sorted maps and sets based on AVL\ntrees which can be used as drop-in replacements for Clojure's\nbuilt-in sorted maps and sets based on red-black trees. Apart from\nthe standard sorted collection API, the provided map and set types\nsupport the transients API and several additional logarithmic time\noperations: rank queries via clojure.core/nth (select element by\nrank) and clojure.data.avl/rank-of (discover rank of element),\n\"nearest key\" lookups via clojure.data.avl/nearest, splits by key\nand index via clojure.data.avl/split-key and\nclojure.data.avl/split-at, respectively, and subsets/submaps using\nclojure.data.avl/subrange.",
   :author "Michał Marczyk",
   :name "clojure.data.avl",
   :wiki-url "http://clojure.github.io/data.avl/index.html",
   :source-url
   "https://github.com/clojure/data.avl/blob/579118e7572234ab619b4e9105761f90d4453730/src/main/clojure/clojure/data/avl.clj"}),
 :vars
 ({:raw-source-url
   "https://github.com/clojure/data.avl/raw/579118e7572234ab619b4e9105761f90d4453730/src/main/clojure/clojure/data/avl.clj",
   :added "0.0.12",
   :name "nearest",
   :file "src/main/clojure/clojure/data/avl.clj",
   :source-url
   "https://github.com/clojure/data.avl/blob/579118e7572234ab619b4e9105761f90d4453730/src/main/clojure/clojure/data/avl.clj#L1919",
   :line 1919,
   :var-type "function",
   :arglists ([coll test x]),
   :doc
   "(alpha)\n\nEquivalent to, but more efficient than, (first (subseq* coll test x)),\nwhere subseq* is clojure.core/subseq for test in #{>, >=} and\nclojure.core/rsubseq for test in #{<, <=}.",
   :namespace "clojure.data.avl",
   :wiki-url
   "http://clojure.github.io/data.avl//index.html#clojure.data.avl/nearest"}
  {:raw-source-url
   "https://github.com/clojure/data.avl/raw/579118e7572234ab619b4e9105761f90d4453730/src/main/clojure/clojure/data/avl.clj",
   :added "0.0.6",
   :name "rank-of",
   :file "src/main/clojure/clojure/data/avl.clj",
   :source-url
   "https://github.com/clojure/data.avl/blob/579118e7572234ab619b4e9105761f90d4453730/src/main/clojure/clojure/data/avl.clj#L1913",
   :line 1913,
   :var-type "function",
   :arglists ([coll x]),
   :doc "Returns the rank of x in coll or -1 if not present.",
   :namespace "clojure.data.avl",
   :wiki-url
   "http://clojure.github.io/data.avl//index.html#clojure.data.avl/rank-of"}
  {:raw-source-url
   "https://github.com/clojure/data.avl/raw/579118e7572234ab619b4e9105761f90d4453730/src/main/clojure/clojure/data/avl.clj",
   :added "0.0.1",
   :name "sorted-map",
   :file "src/main/clojure/clojure/data/avl.clj",
   :source-url
   "https://github.com/clojure/data.avl/blob/579118e7572234ab619b4e9105761f90d4453730/src/main/clojure/clojure/data/avl.clj#L1865",
   :line 1865,
   :var-type "function",
   :arglists ([& keyvals]),
   :doc
   "keyval => key val\nReturns a new AVL map with supplied mappings.",
   :namespace "clojure.data.avl",
   :wiki-url
   "http://clojure.github.io/data.avl//index.html#clojure.data.avl/sorted-map"}
  {:raw-source-url
   "https://github.com/clojure/data.avl/raw/579118e7572234ab619b4e9105761f90d4453730/src/main/clojure/clojure/data/avl.clj",
   :added "0.0.1",
   :name "sorted-map-by",
   :file "src/main/clojure/clojure/data/avl.clj",
   :source-url
   "https://github.com/clojure/data.avl/blob/579118e7572234ab619b4e9105761f90d4453730/src/main/clojure/clojure/data/avl.clj#L1880",
   :line 1880,
   :var-type "function",
   :arglists ([comparator & keyvals]),
   :doc
   "keyval => key val\nReturns a new sorted map with supplied mappings, using the supplied\ncomparator.",
   :namespace "clojure.data.avl",
   :wiki-url
   "http://clojure.github.io/data.avl//index.html#clojure.data.avl/sorted-map-by"}
  {:raw-source-url
   "https://github.com/clojure/data.avl/raw/579118e7572234ab619b4e9105761f90d4453730/src/main/clojure/clojure/data/avl.clj",
   :added "0.0.1",
   :name "sorted-set",
   :file "src/main/clojure/clojure/data/avl.clj",
   :source-url
   "https://github.com/clojure/data.avl/blob/579118e7572234ab619b4e9105761f90d4453730/src/main/clojure/clojure/data/avl.clj#L1898",
   :line 1898,
   :var-type "function",
   :arglists ([& keys]),
   :doc "Returns a new sorted set with supplied keys.",
   :namespace "clojure.data.avl",
   :wiki-url
   "http://clojure.github.io/data.avl//index.html#clojure.data.avl/sorted-set"}
  {:raw-source-url
   "https://github.com/clojure/data.avl/raw/579118e7572234ab619b4e9105761f90d4453730/src/main/clojure/clojure/data/avl.clj",
   :added "0.0.1",
   :name "sorted-set-by",
   :file "src/main/clojure/clojure/data/avl.clj",
   :source-url
   "https://github.com/clojure/data.avl/blob/579118e7572234ab619b4e9105761f90d4453730/src/main/clojure/clojure/data/avl.clj#L1904",
   :line 1904,
   :var-type "function",
   :arglists ([comparator & keys]),
   :doc
   "Returns a new sorted set with supplied keys, using the supplied comparator.",
   :namespace "clojure.data.avl",
   :wiki-url
   "http://clojure.github.io/data.avl//index.html#clojure.data.avl/sorted-set-by"}
  {:raw-source-url
   "https://github.com/clojure/data.avl/raw/579118e7572234ab619b4e9105761f90d4453730/src/main/clojure/clojure/data/avl.clj",
   :added "0.0.12",
   :name "split-at",
   :file "src/main/clojure/clojure/data/avl.clj",
   :source-url
   "https://github.com/clojure/data.avl/blob/579118e7572234ab619b4e9105761f90d4453730/src/main/clojure/clojure/data/avl.clj#L1959",
   :line 1959,
   :var-type "function",
   :arglists ([n coll]),
   :doc
   "(alpha)\n\nEquivalent to, but more efficient than,\n[(into (empty coll) (take n coll))\n (into (empty coll) (drop n coll))].",
   :namespace "clojure.data.avl",
   :wiki-url
   "http://clojure.github.io/data.avl//index.html#clojure.data.avl/split-at"}
  {:raw-source-url
   "https://github.com/clojure/data.avl/raw/579118e7572234ab619b4e9105761f90d4453730/src/main/clojure/clojure/data/avl.clj",
   :added "0.0.12",
   :name "split-key",
   :file "src/main/clojure/clojure/data/avl.clj",
   :source-url
   "https://github.com/clojure/data.avl/blob/579118e7572234ab619b4e9105761f90d4453730/src/main/clojure/clojure/data/avl.clj#L1929",
   :line 1929,
   :var-type "function",
   :arglists ([k coll]),
   :doc
   "(alpha)\n\nReturns [left e? right], where left and right are collections of\nthe same type as coll and containing, respectively, the keys below\nand above k in the ordering determined by coll's comparator, while\ne? is the entry at key k for maps, the stored copy of the key k for\nsets, nil if coll does not contain k.",
   :namespace "clojure.data.avl",
   :wiki-url
   "http://clojure.github.io/data.avl//index.html#clojure.data.avl/split-key"}
  {:raw-source-url
   "https://github.com/clojure/data.avl/raw/579118e7572234ab619b4e9105761f90d4453730/src/main/clojure/clojure/data/avl.clj",
   :added "0.0.12",
   :name "subrange",
   :file "src/main/clojure/clojure/data/avl.clj",
   :source-url
   "https://github.com/clojure/data.avl/blob/579118e7572234ab619b4e9105761f90d4453730/src/main/clojure/clojure/data/avl.clj#L1974",
   :line 1974,
   :var-type "function",
   :arglists ([coll test limit] [coll start-test start end-test end]),
   :doc
   "(alpha)\n\nReturns an AVL collection comprising the entries of coll between\nstart and end (in the sense determined by coll's comparator) in\nlogarithmic time. Whether the endpoints are themselves included in\nthe returned collection depends on the provided tests; start-test\nmust be either > or >=, end-test must be either < or <=.\n\nWhen passed a single test and limit, subrange infers the other end\nof the range from the test: > / >= mean to include items up to the\nend of coll, < / <= mean to include items taken from the beginning\nof coll.\n\n(subrange coll >= start <= end) is equivalent to, but more efficient\nthan, (into (empty coll) (subseq coll >= start <= end)).",
   :namespace "clojure.data.avl",
   :wiki-url
   "http://clojure.github.io/data.avl//index.html#clojure.data.avl/subrange"}
  {:name "AVLMap",
   :var-type "type",
   :namespace "clojure.data.avl",
   :arglists nil,
   :wiki-url
   "http://clojure.github.io/data.avl//index.html#clojure.data.avl/AVLMap",
   :source-url nil,
   :raw-source-url nil,
   :file nil}
  {:name "AVLMapSeq",
   :var-type "type",
   :namespace "clojure.data.avl",
   :arglists nil,
   :wiki-url
   "http://clojure.github.io/data.avl//index.html#clojure.data.avl/AVLMapSeq",
   :source-url nil,
   :raw-source-url nil,
   :file nil}
  {:name "AVLNode",
   :var-type "type",
   :namespace "clojure.data.avl",
   :arglists nil,
   :wiki-url
   "http://clojure.github.io/data.avl//index.html#clojure.data.avl/AVLNode",
   :source-url nil,
   :raw-source-url nil,
   :file nil}
  {:name "AVLSet",
   :var-type "type",
   :namespace "clojure.data.avl",
   :arglists nil,
   :wiki-url
   "http://clojure.github.io/data.avl//index.html#clojure.data.avl/AVLSet",
   :source-url nil,
   :raw-source-url nil,
   :file nil}
  {:name "AVLTransientMap",
   :var-type "type",
   :namespace "clojure.data.avl",
   :arglists nil,
   :wiki-url
   "http://clojure.github.io/data.avl//index.html#clojure.data.avl/AVLTransientMap",
   :source-url nil,
   :raw-source-url nil,
   :file nil}
  {:name "AVLTransientSet",
   :var-type "type",
   :namespace "clojure.data.avl",
   :arglists nil,
   :wiki-url
   "http://clojure.github.io/data.avl//index.html#clojure.data.avl/AVLTransientSet",
   :source-url nil,
   :raw-source-url nil,
   :file nil})}
