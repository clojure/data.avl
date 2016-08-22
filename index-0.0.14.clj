{:namespaces
 ({:doc
   "An implementation of persistent sorted maps and sets based on AVL\ntrees which can be used as drop-in replacements for Clojure's\nbuilt-in sorted maps and sets based on red-black trees. Apart from\nthe standard sorted collection API, the provided map and set types\nsupport the transients API and several additional logarithmic time\noperations: rank queries via clojure.core/nth (select element by\nrank) and clojure.data.avl/rank-of (discover rank of element),\n\"nearest key\" lookups via clojure.data.avl/nearest, splits by key\nand index via clojure.data.avl/split-key and\nclojure.data.avl/split-at, respectively, and subsets/submaps using\nclojure.data.avl/subrange.",
   :author "Michał Marczyk",
   :name "clojure.data.avl",
   :wiki-url "http://clojure.github.io/data.avl/index.html",
   :source-url
   "https://github.com/clojure/data.avl/blob/7dc39e8b4e5ff331dc4624a8b21256c776f81385/src/main/clojure/clojure/data/avl.clj"}),
 :vars
 ({:raw-source-url
   "https://github.com/clojure/data.avl/raw/7dc39e8b4e5ff331dc4624a8b21256c776f81385/src/main/clojure/clojure/data/avl.clj",
   :name "merge",
   :file "src/main/clojure/clojure/data/avl.clj",
   :source-url
   "https://github.com/clojure/data.avl/blob/7dc39e8b4e5ff331dc4624a8b21256c776f81385/src/main/clojure/clojure/data/avl.clj#L2230",
   :line 2230,
   :var-type "function",
   :arglists ([] [m] [m1 m2] [m1 m2 m3 & more]),
   :doc
   "(alpha)\n\nMerges the given AVL maps which should all use the same comparator.\nnil is accepted and converted into an empty AVL map. The value\nreturned is itself an AVL map, except in the nullary case in which\nnil is returned.\n\nIn case of key collisions, mappings from maps further to the right\nin the argument list take precedence.",
   :namespace "clojure.data.avl",
   :wiki-url
   "http://clojure.github.io/data.avl//index.html#clojure.data.avl/merge"}
  {:raw-source-url
   "https://github.com/clojure/data.avl/raw/7dc39e8b4e5ff331dc4624a8b21256c776f81385/src/main/clojure/clojure/data/avl.clj",
   :name "merge-with",
   :file "src/main/clojure/clojure/data/avl.clj",
   :source-url
   "https://github.com/clojure/data.avl/blob/7dc39e8b4e5ff331dc4624a8b21256c776f81385/src/main/clojure/clojure/data/avl.clj#L2258",
   :line 2258,
   :var-type "function",
   :arglists ([f] [f m] [f m1 m2] [f m1 m2 m3 & more]),
   :doc
   "(alpha)\n\nMerges the given AVL maps which should all use the same comparator.\nnil is accepted and converted into an empty AVL map. The value\nreturned is itself an AVL map, except in the nullary case in which\nnil is returned.\n\nUse f to combine values in case of key collisions.",
   :namespace "clojure.data.avl",
   :wiki-url
   "http://clojure.github.io/data.avl//index.html#clojure.data.avl/merge-with"}
  {:raw-source-url
   "https://github.com/clojure/data.avl/raw/7dc39e8b4e5ff331dc4624a8b21256c776f81385/src/main/clojure/clojure/data/avl.clj",
   :added "0.0.12",
   :name "nearest",
   :file "src/main/clojure/clojure/data/avl.clj",
   :source-url
   "https://github.com/clojure/data.avl/blob/7dc39e8b4e5ff331dc4624a8b21256c776f81385/src/main/clojure/clojure/data/avl.clj#L2091",
   :line 2091,
   :var-type "function",
   :arglists ([coll test x]),
   :doc
   "(alpha)\n\nEquivalent to, but more efficient than, (first (subseq* coll test x)),\nwhere subseq* is clojure.core/subseq for test in #{>, >=} and\nclojure.core/rsubseq for test in #{<, <=}.",
   :namespace "clojure.data.avl",
   :wiki-url
   "http://clojure.github.io/data.avl//index.html#clojure.data.avl/nearest"}
  {:raw-source-url
   "https://github.com/clojure/data.avl/raw/7dc39e8b4e5ff331dc4624a8b21256c776f81385/src/main/clojure/clojure/data/avl.clj",
   :added "0.0.6",
   :name "rank-of",
   :file "src/main/clojure/clojure/data/avl.clj",
   :source-url
   "https://github.com/clojure/data.avl/blob/7dc39e8b4e5ff331dc4624a8b21256c776f81385/src/main/clojure/clojure/data/avl.clj#L2085",
   :line 2085,
   :var-type "function",
   :arglists ([coll x]),
   :doc "Returns the rank of x in coll or -1 if not present.",
   :namespace "clojure.data.avl",
   :wiki-url
   "http://clojure.github.io/data.avl//index.html#clojure.data.avl/rank-of"}
  {:raw-source-url
   "https://github.com/clojure/data.avl/raw/7dc39e8b4e5ff331dc4624a8b21256c776f81385/src/main/clojure/clojure/data/avl.clj",
   :added "0.0.1",
   :name "sorted-map",
   :file "src/main/clojure/clojure/data/avl.clj",
   :source-url
   "https://github.com/clojure/data.avl/blob/7dc39e8b4e5ff331dc4624a8b21256c776f81385/src/main/clojure/clojure/data/avl.clj#L2037",
   :line 2037,
   :var-type "function",
   :arglists ([& keyvals]),
   :doc
   "keyval => key val\nReturns a new AVL map with supplied mappings.",
   :namespace "clojure.data.avl",
   :wiki-url
   "http://clojure.github.io/data.avl//index.html#clojure.data.avl/sorted-map"}
  {:raw-source-url
   "https://github.com/clojure/data.avl/raw/7dc39e8b4e5ff331dc4624a8b21256c776f81385/src/main/clojure/clojure/data/avl.clj",
   :added "0.0.1",
   :name "sorted-map-by",
   :file "src/main/clojure/clojure/data/avl.clj",
   :source-url
   "https://github.com/clojure/data.avl/blob/7dc39e8b4e5ff331dc4624a8b21256c776f81385/src/main/clojure/clojure/data/avl.clj#L2052",
   :line 2052,
   :var-type "function",
   :arglists ([comparator & keyvals]),
   :doc
   "keyval => key val\nReturns a new sorted map with supplied mappings, using the supplied\ncomparator.",
   :namespace "clojure.data.avl",
   :wiki-url
   "http://clojure.github.io/data.avl//index.html#clojure.data.avl/sorted-map-by"}
  {:raw-source-url
   "https://github.com/clojure/data.avl/raw/7dc39e8b4e5ff331dc4624a8b21256c776f81385/src/main/clojure/clojure/data/avl.clj",
   :added "0.0.1",
   :name "sorted-set",
   :file "src/main/clojure/clojure/data/avl.clj",
   :source-url
   "https://github.com/clojure/data.avl/blob/7dc39e8b4e5ff331dc4624a8b21256c776f81385/src/main/clojure/clojure/data/avl.clj#L2070",
   :line 2070,
   :var-type "function",
   :arglists ([& keys]),
   :doc "Returns a new sorted set with supplied keys.",
   :namespace "clojure.data.avl",
   :wiki-url
   "http://clojure.github.io/data.avl//index.html#clojure.data.avl/sorted-set"}
  {:raw-source-url
   "https://github.com/clojure/data.avl/raw/7dc39e8b4e5ff331dc4624a8b21256c776f81385/src/main/clojure/clojure/data/avl.clj",
   :added "0.0.1",
   :name "sorted-set-by",
   :file "src/main/clojure/clojure/data/avl.clj",
   :source-url
   "https://github.com/clojure/data.avl/blob/7dc39e8b4e5ff331dc4624a8b21256c776f81385/src/main/clojure/clojure/data/avl.clj#L2076",
   :line 2076,
   :var-type "function",
   :arglists ([comparator & keys]),
   :doc
   "Returns a new sorted set with supplied keys, using the supplied comparator.",
   :namespace "clojure.data.avl",
   :wiki-url
   "http://clojure.github.io/data.avl//index.html#clojure.data.avl/sorted-set-by"}
  {:raw-source-url
   "https://github.com/clojure/data.avl/raw/7dc39e8b4e5ff331dc4624a8b21256c776f81385/src/main/clojure/clojure/data/avl.clj",
   :added "0.0.12",
   :name "split-at",
   :file "src/main/clojure/clojure/data/avl.clj",
   :source-url
   "https://github.com/clojure/data.avl/blob/7dc39e8b4e5ff331dc4624a8b21256c776f81385/src/main/clojure/clojure/data/avl.clj#L2131",
   :line 2131,
   :var-type "function",
   :arglists ([n coll]),
   :doc
   "(alpha)\n\nEquivalent to, but more efficient than,\n[(into (empty coll) (take n coll))\n (into (empty coll) (drop n coll))].",
   :namespace "clojure.data.avl",
   :wiki-url
   "http://clojure.github.io/data.avl//index.html#clojure.data.avl/split-at"}
  {:raw-source-url
   "https://github.com/clojure/data.avl/raw/7dc39e8b4e5ff331dc4624a8b21256c776f81385/src/main/clojure/clojure/data/avl.clj",
   :added "0.0.12",
   :name "split-key",
   :file "src/main/clojure/clojure/data/avl.clj",
   :source-url
   "https://github.com/clojure/data.avl/blob/7dc39e8b4e5ff331dc4624a8b21256c776f81385/src/main/clojure/clojure/data/avl.clj#L2101",
   :line 2101,
   :var-type "function",
   :arglists ([k coll]),
   :doc
   "(alpha)\n\nReturns [left e? right], where left and right are collections of\nthe same type as coll and containing, respectively, the keys below\nand above k in the ordering determined by coll's comparator, while\ne? is the entry at key k for maps, the stored copy of the key k for\nsets, nil if coll does not contain k.",
   :namespace "clojure.data.avl",
   :wiki-url
   "http://clojure.github.io/data.avl//index.html#clojure.data.avl/split-key"}
  {:raw-source-url
   "https://github.com/clojure/data.avl/raw/7dc39e8b4e5ff331dc4624a8b21256c776f81385/src/main/clojure/clojure/data/avl.clj",
   :added "0.0.12",
   :name "subrange",
   :file "src/main/clojure/clojure/data/avl.clj",
   :source-url
   "https://github.com/clojure/data.avl/blob/7dc39e8b4e5ff331dc4624a8b21256c776f81385/src/main/clojure/clojure/data/avl.clj#L2146",
   :line 2146,
   :var-type "function",
   :arglists ([coll test limit] [coll start-test start end-test end]),
   :doc
   "(alpha)\n\nReturns an AVL collection comprising the entries of coll between\nstart and end (in the sense determined by coll's comparator) in\nlogarithmic time. Whether the endpoints are themselves included in\nthe returned collection depends on the provided tests; start-test\nmust be either > or >=, end-test must be either < or <=.\n\nWhen passed a single test and limit, subrange infers the other end\nof the range from the test: > / >= mean to include items up to the\nend of coll, < / <= mean to include items taken from the beginning\nof coll.\n\n(subrange >= start <= end) is equivalent to, but more efficient\nthan, (into (empty coll) (subseq coll >= start <= end).",
   :namespace "clojure.data.avl",
   :wiki-url
   "http://clojure.github.io/data.avl//index.html#clojure.data.avl/subrange"}
  {:raw-source-url
   "https://github.com/clojure/data.avl/raw/7dc39e8b4e5ff331dc4624a8b21256c776f81385/src/main/clojure/clojure/data/avl.clj",
   :name "union",
   :file "src/main/clojure/clojure/data/avl.clj",
   :source-url
   "https://github.com/clojure/data.avl/blob/7dc39e8b4e5ff331dc4624a8b21256c776f81385/src/main/clojure/clojure/data/avl.clj#L2286",
   :line 2286,
   :var-type "function",
   :arglists ([] [s] [s1 s2] [s1 s2 s3 & more]),
   :doc
   "(alpha)\n\nComputes the union of the given AVL sets which should all use the\nsame comparator. nil is accepted and converted into an empty AVL\nset. The value returned is itself an AVL set, except in the nullary\ncase in which nil is returned.",
   :namespace "clojure.data.avl",
   :wiki-url
   "http://clojure.github.io/data.avl//index.html#clojure.data.avl/union"}
  {:raw-source-url
   "https://github.com/clojure/data.avl/raw/7dc39e8b4e5ff331dc4624a8b21256c776f81385/src/main/clojure/clojure/data/avl.clj",
   :name "unsafe-join",
   :file "src/main/clojure/clojure/data/avl.clj",
   :source-url
   "https://github.com/clojure/data.avl/blob/7dc39e8b4e5ff331dc4624a8b21256c776f81385/src/main/clojure/clojure/data/avl.clj#L2202",
   :line 2202,
   :var-type "function",
   :arglists ([] [coll] [coll1 coll2] [coll1 coll2 coll3 & more]),
   :doc
   "(alpha)\n\nATTN: This function DOES NOT validate its inputs and WILL return\nmalformed results if the inputs do not satisfy the contract.\n\nMerges or computes the union of colls. All colls must be AVL\ncollections of the same type using the same comparator. If collx\noccurs earlier than colly among the arguments to unsafe-join,\ncollx's greatest element must be strictly smaller than the smallest\nelement of colly (in the sense determined by the comparator).",
   :namespace "clojure.data.avl",
   :wiki-url
   "http://clojure.github.io/data.avl//index.html#clojure.data.avl/unsafe-join"}
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
