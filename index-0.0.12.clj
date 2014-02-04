{:namespaces
 ({:source-url
   "https://github.com/clojure/data.avl/blob/51a8bb23fe248fab2d2b3c1d8b85cc4d715accb7/src/main/clojure/clojure/data/avl.clj",
   :wiki-url
   "http://clojure.github.com/data.avl/clojure.data.avl-api.html",
   :name "clojure.data.avl",
   :author "Michał Marczyk",
   :doc
   "An implementation of persistent sorted maps and sets based on AVL\ntrees which can be used as drop-in replacements for Clojure's\nbuilt-in sorted maps and sets based on red-black trees. Apart from\nthe standard sorted collection API, the provided map and set types\nsupport the transients API and several additional logarithmic time\noperations: rank queries via clojure.core/nth (select element by\nrank) and clojure.data.avl/rank-of (discover rank of element),\n\"nearest key\" lookups via clojure.data.avl/nearest, splits by key\nand index via clojure.data.avl/split-key and\nclojure.data.avl/split-at, respectively, and subsets/submaps using\nclojure.data.avl/subrange."}),
 :vars
 ({:arglists ([coll test x]),
   :name "nearest",
   :namespace "clojure.data.avl",
   :source-url
   "https://github.com/clojure/data.avl/blob/51a8bb23fe248fab2d2b3c1d8b85cc4d715accb7/src/main/clojure/clojure/data/avl.clj#L1480",
   :raw-source-url
   "https://github.com/clojure/data.avl/raw/51a8bb23fe248fab2d2b3c1d8b85cc4d715accb7/src/main/clojure/clojure/data/avl.clj",
   :wiki-url
   "http://clojure.github.com/data.avl//clojure.data.avl-api.html#clojure.data.avl/nearest",
   :doc
   "Equivalent to, but more efficient than, (first (subseq* coll test x)),\nwhere subseq* is clojure.core/subseq for test in #{>, >=} and\nclojure.core/rsubseq for test in #{<, <=}.",
   :var-type "function",
   :line 1480,
   :file "src/main/clojure/clojure/data/avl.clj"}
  {:arglists ([coll x]),
   :name "rank-of",
   :namespace "clojure.data.avl",
   :source-url
   "https://github.com/clojure/data.avl/blob/51a8bb23fe248fab2d2b3c1d8b85cc4d715accb7/src/main/clojure/clojure/data/avl.clj#L1475",
   :raw-source-url
   "https://github.com/clojure/data.avl/raw/51a8bb23fe248fab2d2b3c1d8b85cc4d715accb7/src/main/clojure/clojure/data/avl.clj",
   :wiki-url
   "http://clojure.github.com/data.avl//clojure.data.avl-api.html#clojure.data.avl/rank-of",
   :doc "Returns the rank of x in coll or -1 if not present.",
   :var-type "function",
   :line 1475,
   :file "src/main/clojure/clojure/data/avl.clj"}
  {:arglists ([& keyvals]),
   :name "sorted-map",
   :namespace "clojure.data.avl",
   :source-url
   "https://github.com/clojure/data.avl/blob/51a8bb23fe248fab2d2b3c1d8b85cc4d715accb7/src/main/clojure/clojure/data/avl.clj#L1441",
   :raw-source-url
   "https://github.com/clojure/data.avl/raw/51a8bb23fe248fab2d2b3c1d8b85cc4d715accb7/src/main/clojure/clojure/data/avl.clj",
   :wiki-url
   "http://clojure.github.com/data.avl//clojure.data.avl-api.html#clojure.data.avl/sorted-map",
   :doc
   "keyval => key val\nReturns a new AVL map with supplied mappings.",
   :var-type "function",
   :line 1441,
   :file "src/main/clojure/clojure/data/avl.clj"}
  {:arglists ([comparator & keyvals]),
   :name "sorted-map-by",
   :namespace "clojure.data.avl",
   :source-url
   "https://github.com/clojure/data.avl/blob/51a8bb23fe248fab2d2b3c1d8b85cc4d715accb7/src/main/clojure/clojure/data/avl.clj#L1450",
   :raw-source-url
   "https://github.com/clojure/data.avl/raw/51a8bb23fe248fab2d2b3c1d8b85cc4d715accb7/src/main/clojure/clojure/data/avl.clj",
   :wiki-url
   "http://clojure.github.com/data.avl//clojure.data.avl-api.html#clojure.data.avl/sorted-map-by",
   :doc
   "keyval => key val\nReturns a new sorted map with supplied mappings, using the supplied\ncomparator.",
   :var-type "function",
   :line 1450,
   :file "src/main/clojure/clojure/data/avl.clj"}
  {:arglists ([& keys]),
   :name "sorted-set",
   :namespace "clojure.data.avl",
   :source-url
   "https://github.com/clojure/data.avl/blob/51a8bb23fe248fab2d2b3c1d8b85cc4d715accb7/src/main/clojure/clojure/data/avl.clj#L1462",
   :raw-source-url
   "https://github.com/clojure/data.avl/raw/51a8bb23fe248fab2d2b3c1d8b85cc4d715accb7/src/main/clojure/clojure/data/avl.clj",
   :wiki-url
   "http://clojure.github.com/data.avl//clojure.data.avl-api.html#clojure.data.avl/sorted-set",
   :doc "Returns a new sorted set with supplied keys.",
   :var-type "function",
   :line 1462,
   :file "src/main/clojure/clojure/data/avl.clj"}
  {:arglists ([comparator & keys]),
   :name "sorted-set-by",
   :namespace "clojure.data.avl",
   :source-url
   "https://github.com/clojure/data.avl/blob/51a8bb23fe248fab2d2b3c1d8b85cc4d715accb7/src/main/clojure/clojure/data/avl.clj#L1467",
   :raw-source-url
   "https://github.com/clojure/data.avl/raw/51a8bb23fe248fab2d2b3c1d8b85cc4d715accb7/src/main/clojure/clojure/data/avl.clj",
   :wiki-url
   "http://clojure.github.com/data.avl//clojure.data.avl-api.html#clojure.data.avl/sorted-set-by",
   :doc
   "Returns a new sorted set with supplied keys, using the supplied comparator.",
   :var-type "function",
   :line 1467,
   :file "src/main/clojure/clojure/data/avl.clj"}
  {:arglists ([n coll]),
   :name "split-at",
   :namespace "clojure.data.avl",
   :source-url
   "https://github.com/clojure/data.avl/blob/51a8bb23fe248fab2d2b3c1d8b85cc4d715accb7/src/main/clojure/clojure/data/avl.clj#L1514",
   :raw-source-url
   "https://github.com/clojure/data.avl/raw/51a8bb23fe248fab2d2b3c1d8b85cc4d715accb7/src/main/clojure/clojure/data/avl.clj",
   :wiki-url
   "http://clojure.github.com/data.avl//clojure.data.avl-api.html#clojure.data.avl/split-at",
   :doc
   "Equivalent to, but more efficient than, \n[(into (empty coll) (take n coll))\n (into (empty coll) (drop n coll))].",
   :var-type "function",
   :line 1514,
   :file "src/main/clojure/clojure/data/avl.clj"}
  {:arglists ([k coll]),
   :name "split-key",
   :namespace "clojure.data.avl",
   :source-url
   "https://github.com/clojure/data.avl/blob/51a8bb23fe248fab2d2b3c1d8b85cc4d715accb7/src/main/clojure/clojure/data/avl.clj#L1487",
   :raw-source-url
   "https://github.com/clojure/data.avl/raw/51a8bb23fe248fab2d2b3c1d8b85cc4d715accb7/src/main/clojure/clojure/data/avl.clj",
   :wiki-url
   "http://clojure.github.com/data.avl//clojure.data.avl-api.html#clojure.data.avl/split-key",
   :doc
   "Returns [left e? right], where left and right are collections of\nthe same type as coll and containing, respectively, the keys below\nand above k in the ordering determined by coll's comparator, while\ne? is the entry at key k for maps, the stored copy of the key k for\nsets, nil if coll does not contain k.",
   :var-type "function",
   :line 1487,
   :file "src/main/clojure/clojure/data/avl.clj"}
  {:arglists ([coll test limit] [coll start-test start end-test end]),
   :name "subrange",
   :namespace "clojure.data.avl",
   :source-url
   "https://github.com/clojure/data.avl/blob/51a8bb23fe248fab2d2b3c1d8b85cc4d715accb7/src/main/clojure/clojure/data/avl.clj#L1526",
   :raw-source-url
   "https://github.com/clojure/data.avl/raw/51a8bb23fe248fab2d2b3c1d8b85cc4d715accb7/src/main/clojure/clojure/data/avl.clj",
   :wiki-url
   "http://clojure.github.com/data.avl//clojure.data.avl-api.html#clojure.data.avl/subrange",
   :doc
   "Returns an AVL collection comprising the entries of coll between\nstart and end (in the sense determined by coll's comparator) in\nlogarithmic time. Whether the endpoints are themselves included in\nthe returned collection depends on the provided tests; start-test\nmust be either > or >=, end-test must be either < or <=.\n\nWhen passed a single test and limit, subrange infers the other end\nof the range from the test: > / >= mean to include items up to the\nend of coll, < / <= mean to include items taken from the beginning\nof coll.\n\n(subrange >= start <= end) is equivalent to, but more efficient\nthan, (into (empty coll) (subseq coll >= start <= end).",
   :var-type "function",
   :line 1526,
   :file "src/main/clojure/clojure/data/avl.clj"}
  {:file nil,
   :raw-source-url nil,
   :source-url nil,
   :wiki-url
   "http://clojure.github.com/data.avl//clojure.data.avl-api.html#clojure.data.avl/AVLMap",
   :namespace "clojure.data.avl",
   :var-type "type",
   :name "AVLMap"}
  {:file nil,
   :raw-source-url nil,
   :source-url nil,
   :wiki-url
   "http://clojure.github.com/data.avl//clojure.data.avl-api.html#clojure.data.avl/AVLMapSeq",
   :namespace "clojure.data.avl",
   :var-type "type",
   :name "AVLMapSeq"}
  {:file nil,
   :raw-source-url nil,
   :source-url nil,
   :wiki-url
   "http://clojure.github.com/data.avl//clojure.data.avl-api.html#clojure.data.avl/AVLNode",
   :namespace "clojure.data.avl",
   :var-type "type",
   :name "AVLNode"}
  {:file nil,
   :raw-source-url nil,
   :source-url nil,
   :wiki-url
   "http://clojure.github.com/data.avl//clojure.data.avl-api.html#clojure.data.avl/AVLSet",
   :namespace "clojure.data.avl",
   :var-type "type",
   :name "AVLSet"}
  {:file nil,
   :raw-source-url nil,
   :source-url nil,
   :wiki-url
   "http://clojure.github.com/data.avl//clojure.data.avl-api.html#clojure.data.avl/AVLTransientMap",
   :namespace "clojure.data.avl",
   :var-type "type",
   :name "AVLTransientMap"}
  {:file nil,
   :raw-source-url nil,
   :source-url nil,
   :wiki-url
   "http://clojure.github.com/data.avl//clojure.data.avl-api.html#clojure.data.avl/AVLTransientSet",
   :namespace "clojure.data.avl",
   :var-type "type",
   :name "AVLTransientSet"})}
