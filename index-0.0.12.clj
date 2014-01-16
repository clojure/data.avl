{:namespaces
 ({:source-url
   "https://github.com/clojure/data.avl/blob/51a8bb23fe248fab2d2b3c1d8b85cc4d715accb7/src/main/clojure/clojure/data/avl.clj",
   :wiki-url
   "http://clojure.github.com/data.avl/clojure.data.avl-api.html",
   :name "clojure.data.avl",
   :author "Michał Marczyk",
   :doc
   "An implementation of persistent sorted maps and sets based on AVL\ntrees with API mimicking that of Clojure's sorted maps and\nsets (based on Red-Black Trees). Additionally, the provided map and\nset types support the transients API and logarithmic time rank\nqueries via clojure.core/nth (select element by rank) and\nclojure.data.avl/rank-of (discover rank of element)."}),
 :vars
 ({:arglists ([coll test x]),
   :name "nearest",
   :namespace "clojure.data.avl",
   :source-url
   "https://github.com/clojure/data.avl/blob/51a8bb23fe248fab2d2b3c1d8b85cc4d715accb7/src/main/clojure/clojure/data/avl.clj#L1472",
   :raw-source-url
   "https://github.com/clojure/data.avl/raw/51a8bb23fe248fab2d2b3c1d8b85cc4d715accb7/src/main/clojure/clojure/data/avl.clj",
   :wiki-url
   "http://clojure.github.com/data.avl//clojure.data.avl-api.html#clojure.data.avl/nearest",
   :doc
   "Equivalent to, but more efficient than, (first (subseq* coll test x)),\nwhere subseq* is clojure.core/subseq for test in #{>, >=} and\nclojure.core/rsubseq for test in #{<, <=}.",
   :var-type "function",
   :line 1472,
   :file "src/main/clojure/clojure/data/avl.clj"}
  {:arglists ([coll x]),
   :name "rank-of",
   :namespace "clojure.data.avl",
   :source-url
   "https://github.com/clojure/data.avl/blob/51a8bb23fe248fab2d2b3c1d8b85cc4d715accb7/src/main/clojure/clojure/data/avl.clj#L1467",
   :raw-source-url
   "https://github.com/clojure/data.avl/raw/51a8bb23fe248fab2d2b3c1d8b85cc4d715accb7/src/main/clojure/clojure/data/avl.clj",
   :wiki-url
   "http://clojure.github.com/data.avl//clojure.data.avl-api.html#clojure.data.avl/rank-of",
   :doc "Returns the rank of x in coll or -1 if not present.",
   :var-type "function",
   :line 1467,
   :file "src/main/clojure/clojure/data/avl.clj"}
  {:arglists ([& keyvals]),
   :name "sorted-map",
   :namespace "clojure.data.avl",
   :source-url
   "https://github.com/clojure/data.avl/blob/51a8bb23fe248fab2d2b3c1d8b85cc4d715accb7/src/main/clojure/clojure/data/avl.clj#L1433",
   :raw-source-url
   "https://github.com/clojure/data.avl/raw/51a8bb23fe248fab2d2b3c1d8b85cc4d715accb7/src/main/clojure/clojure/data/avl.clj",
   :wiki-url
   "http://clojure.github.com/data.avl//clojure.data.avl-api.html#clojure.data.avl/sorted-map",
   :doc
   "keyval => key val\nReturns a new AVL map with supplied mappings.",
   :var-type "function",
   :line 1433,
   :file "src/main/clojure/clojure/data/avl.clj"}
  {:arglists ([comparator & keyvals]),
   :name "sorted-map-by",
   :namespace "clojure.data.avl",
   :source-url
   "https://github.com/clojure/data.avl/blob/51a8bb23fe248fab2d2b3c1d8b85cc4d715accb7/src/main/clojure/clojure/data/avl.clj#L1442",
   :raw-source-url
   "https://github.com/clojure/data.avl/raw/51a8bb23fe248fab2d2b3c1d8b85cc4d715accb7/src/main/clojure/clojure/data/avl.clj",
   :wiki-url
   "http://clojure.github.com/data.avl//clojure.data.avl-api.html#clojure.data.avl/sorted-map-by",
   :doc
   "keyval => key val\nReturns a new sorted map with supplied mappings, using the supplied\ncomparator.",
   :var-type "function",
   :line 1442,
   :file "src/main/clojure/clojure/data/avl.clj"}
  {:arglists ([& keys]),
   :name "sorted-set",
   :namespace "clojure.data.avl",
   :source-url
   "https://github.com/clojure/data.avl/blob/51a8bb23fe248fab2d2b3c1d8b85cc4d715accb7/src/main/clojure/clojure/data/avl.clj#L1454",
   :raw-source-url
   "https://github.com/clojure/data.avl/raw/51a8bb23fe248fab2d2b3c1d8b85cc4d715accb7/src/main/clojure/clojure/data/avl.clj",
   :wiki-url
   "http://clojure.github.com/data.avl//clojure.data.avl-api.html#clojure.data.avl/sorted-set",
   :doc "Returns a new sorted set with supplied keys.",
   :var-type "function",
   :line 1454,
   :file "src/main/clojure/clojure/data/avl.clj"}
  {:arglists ([comparator & keys]),
   :name "sorted-set-by",
   :namespace "clojure.data.avl",
   :source-url
   "https://github.com/clojure/data.avl/blob/51a8bb23fe248fab2d2b3c1d8b85cc4d715accb7/src/main/clojure/clojure/data/avl.clj#L1459",
   :raw-source-url
   "https://github.com/clojure/data.avl/raw/51a8bb23fe248fab2d2b3c1d8b85cc4d715accb7/src/main/clojure/clojure/data/avl.clj",
   :wiki-url
   "http://clojure.github.com/data.avl//clojure.data.avl-api.html#clojure.data.avl/sorted-set-by",
   :doc
   "Returns a new sorted set with supplied keys, using the supplied comparator.",
   :var-type "function",
   :line 1459,
   :file "src/main/clojure/clojure/data/avl.clj"}
  {:arglists ([coll x]),
   :name "split-at",
   :namespace "clojure.data.avl",
   :source-url
   "https://github.com/clojure/data.avl/blob/51a8bb23fe248fab2d2b3c1d8b85cc4d715accb7/src/main/clojure/clojure/data/avl.clj#L1479",
   :raw-source-url
   "https://github.com/clojure/data.avl/raw/51a8bb23fe248fab2d2b3c1d8b85cc4d715accb7/src/main/clojure/clojure/data/avl.clj",
   :wiki-url
   "http://clojure.github.com/data.avl//clojure.data.avl-api.html#clojure.data.avl/split-at",
   :doc
   "Returns [left e? right], where left and right are collections of\nthe same type as coll and containing, respectively, the keys below\nand above x in the ordering determined by coll's comparator, while\ne? is the entry at key x for maps, the stored copy of the key x for\nsets, nil if coll does not contain x.",
   :var-type "function",
   :line 1479,
   :file "src/main/clojure/clojure/data/avl.clj"}
  {:arglists ([coll start end]),
   :name "subrange",
   :namespace "clojure.data.avl",
   :source-url
   "https://github.com/clojure/data.avl/blob/51a8bb23fe248fab2d2b3c1d8b85cc4d715accb7/src/main/clojure/clojure/data/avl.clj#L1506",
   :raw-source-url
   "https://github.com/clojure/data.avl/raw/51a8bb23fe248fab2d2b3c1d8b85cc4d715accb7/src/main/clojure/clojure/data/avl.clj",
   :wiki-url
   "http://clojure.github.com/data.avl//clojure.data.avl-api.html#clojure.data.avl/subrange",
   :doc
   "Returns an AVL collection comprising the entries of coll between\nstart and end (inclusive, in the sense determined by coll's\ncomparator) in logarithmic time.\n\nIn other words, equivalent to, but more efficient than,\n(into (empty coll) (subseq coll >= start <= end).",
   :var-type "function",
   :line 1506,
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
