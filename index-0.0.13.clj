{:namespaces
 ({:source-url
   "https://github.com/clojure/data.avl/blob/7dc39e8b4e5ff331dc4624a8b21256c776f81385/src/main/clojure/clojure/data/avl.clj",
   :wiki-url
   "http://clojure.github.com/data.avl/clojure.data.avl-api.html",
   :name "clojure.data.avl",
   :author "Michał Marczyk",
   :doc
   "An implementation of persistent sorted maps and sets based on AVL\ntrees which can be used as drop-in replacements for Clojure's\nbuilt-in sorted maps and sets based on red-black trees. Apart from\nthe standard sorted collection API, the provided map and set types\nsupport the transients API and several additional logarithmic time\noperations: rank queries via clojure.core/nth (select element by\nrank) and clojure.data.avl/rank-of (discover rank of element),\n\"nearest key\" lookups via clojure.data.avl/nearest, splits by key\nand index via clojure.data.avl/split-key and\nclojure.data.avl/split-at, respectively, and subsets/submaps using\nclojure.data.avl/subrange."}),
 :vars
 ({:arglists ([] [m] [m1 m2] [m1 m2 m3 & more]),
   :name "merge",
   :namespace "clojure.data.avl",
   :source-url
   "https://github.com/clojure/data.avl/blob/7dc39e8b4e5ff331dc4624a8b21256c776f81385/src/main/clojure/clojure/data/avl.clj#L2079",
   :raw-source-url
   "https://github.com/clojure/data.avl/raw/7dc39e8b4e5ff331dc4624a8b21256c776f81385/src/main/clojure/clojure/data/avl.clj",
   :wiki-url
   "http://clojure.github.com/data.avl//clojure.data.avl-api.html#clojure.data.avl/merge",
   :doc
   "(alpha)\n\nMerges the given AVL maps which should all use the same comparator.\nnil is accepted and converted into an empty AVL map. The value\nreturned is itself an AVL map, except in the nullary case in which\nnil is returned.\n\nIn case of key collisions, mappings from maps further to the right\nin the argument list take precedence.",
   :var-type "function",
   :line 2079,
   :file "src/main/clojure/clojure/data/avl.clj"}
  {:arglists ([f] [f m] [f m1 m2] [f m1 m2 m3 & more]),
   :name "merge-with",
   :namespace "clojure.data.avl",
   :source-url
   "https://github.com/clojure/data.avl/blob/7dc39e8b4e5ff331dc4624a8b21256c776f81385/src/main/clojure/clojure/data/avl.clj#L2107",
   :raw-source-url
   "https://github.com/clojure/data.avl/raw/7dc39e8b4e5ff331dc4624a8b21256c776f81385/src/main/clojure/clojure/data/avl.clj",
   :wiki-url
   "http://clojure.github.com/data.avl//clojure.data.avl-api.html#clojure.data.avl/merge-with",
   :doc
   "(alpha)\n\nMerges the given AVL maps which should all use the same comparator.\nnil is accepted and converted into an empty AVL map. The value\nreturned is itself an AVL map, except in the nullary case in which\nnil is returned.\n\nUse f to combine values in case of key collisions.",
   :var-type "function",
   :line 2107,
   :file "src/main/clojure/clojure/data/avl.clj"}
  {:arglists ([coll test x]),
   :name "nearest",
   :namespace "clojure.data.avl",
   :source-url
   "https://github.com/clojure/data.avl/blob/7dc39e8b4e5ff331dc4624a8b21256c776f81385/src/main/clojure/clojure/data/avl.clj#L1946",
   :raw-source-url
   "https://github.com/clojure/data.avl/raw/7dc39e8b4e5ff331dc4624a8b21256c776f81385/src/main/clojure/clojure/data/avl.clj",
   :wiki-url
   "http://clojure.github.com/data.avl//clojure.data.avl-api.html#clojure.data.avl/nearest",
   :doc
   "(alpha)\n\nEquivalent to, but more efficient than, (first (subseq* coll test x)),\nwhere subseq* is clojure.core/subseq for test in #{>, >=} and\nclojure.core/rsubseq for test in #{<, <=}.",
   :var-type "function",
   :line 1946,
   :file "src/main/clojure/clojure/data/avl.clj"}
  {:arglists ([coll x]),
   :name "rank-of",
   :namespace "clojure.data.avl",
   :source-url
   "https://github.com/clojure/data.avl/blob/7dc39e8b4e5ff331dc4624a8b21256c776f81385/src/main/clojure/clojure/data/avl.clj#L1941",
   :raw-source-url
   "https://github.com/clojure/data.avl/raw/7dc39e8b4e5ff331dc4624a8b21256c776f81385/src/main/clojure/clojure/data/avl.clj",
   :wiki-url
   "http://clojure.github.com/data.avl//clojure.data.avl-api.html#clojure.data.avl/rank-of",
   :doc "Returns the rank of x in coll or -1 if not present.",
   :var-type "function",
   :line 1941,
   :file "src/main/clojure/clojure/data/avl.clj"}
  {:arglists ([& keyvals]),
   :name "sorted-map",
   :namespace "clojure.data.avl",
   :source-url
   "https://github.com/clojure/data.avl/blob/7dc39e8b4e5ff331dc4624a8b21256c776f81385/src/main/clojure/clojure/data/avl.clj#L1907",
   :raw-source-url
   "https://github.com/clojure/data.avl/raw/7dc39e8b4e5ff331dc4624a8b21256c776f81385/src/main/clojure/clojure/data/avl.clj",
   :wiki-url
   "http://clojure.github.com/data.avl//clojure.data.avl-api.html#clojure.data.avl/sorted-map",
   :doc
   "keyval => key val\nReturns a new AVL map with supplied mappings.",
   :var-type "function",
   :line 1907,
   :file "src/main/clojure/clojure/data/avl.clj"}
  {:arglists ([comparator & keyvals]),
   :name "sorted-map-by",
   :namespace "clojure.data.avl",
   :source-url
   "https://github.com/clojure/data.avl/blob/7dc39e8b4e5ff331dc4624a8b21256c776f81385/src/main/clojure/clojure/data/avl.clj#L1916",
   :raw-source-url
   "https://github.com/clojure/data.avl/raw/7dc39e8b4e5ff331dc4624a8b21256c776f81385/src/main/clojure/clojure/data/avl.clj",
   :wiki-url
   "http://clojure.github.com/data.avl//clojure.data.avl-api.html#clojure.data.avl/sorted-map-by",
   :doc
   "keyval => key val\nReturns a new sorted map with supplied mappings, using the supplied\ncomparator.",
   :var-type "function",
   :line 1916,
   :file "src/main/clojure/clojure/data/avl.clj"}
  {:arglists ([& keys]),
   :name "sorted-set",
   :namespace "clojure.data.avl",
   :source-url
   "https://github.com/clojure/data.avl/blob/7dc39e8b4e5ff331dc4624a8b21256c776f81385/src/main/clojure/clojure/data/avl.clj#L1928",
   :raw-source-url
   "https://github.com/clojure/data.avl/raw/7dc39e8b4e5ff331dc4624a8b21256c776f81385/src/main/clojure/clojure/data/avl.clj",
   :wiki-url
   "http://clojure.github.com/data.avl//clojure.data.avl-api.html#clojure.data.avl/sorted-set",
   :doc "Returns a new sorted set with supplied keys.",
   :var-type "function",
   :line 1928,
   :file "src/main/clojure/clojure/data/avl.clj"}
  {:arglists ([comparator & keys]),
   :name "sorted-set-by",
   :namespace "clojure.data.avl",
   :source-url
   "https://github.com/clojure/data.avl/blob/7dc39e8b4e5ff331dc4624a8b21256c776f81385/src/main/clojure/clojure/data/avl.clj#L1933",
   :raw-source-url
   "https://github.com/clojure/data.avl/raw/7dc39e8b4e5ff331dc4624a8b21256c776f81385/src/main/clojure/clojure/data/avl.clj",
   :wiki-url
   "http://clojure.github.com/data.avl//clojure.data.avl-api.html#clojure.data.avl/sorted-set-by",
   :doc
   "Returns a new sorted set with supplied keys, using the supplied comparator.",
   :var-type "function",
   :line 1933,
   :file "src/main/clojure/clojure/data/avl.clj"}
  {:arglists ([n coll]),
   :name "split-at",
   :namespace "clojure.data.avl",
   :source-url
   "https://github.com/clojure/data.avl/blob/7dc39e8b4e5ff331dc4624a8b21256c776f81385/src/main/clojure/clojure/data/avl.clj#L1984",
   :raw-source-url
   "https://github.com/clojure/data.avl/raw/7dc39e8b4e5ff331dc4624a8b21256c776f81385/src/main/clojure/clojure/data/avl.clj",
   :wiki-url
   "http://clojure.github.com/data.avl//clojure.data.avl-api.html#clojure.data.avl/split-at",
   :doc
   "(alpha)\n\nEquivalent to, but more efficient than,\n[(into (empty coll) (take n coll))\n (into (empty coll) (drop n coll))].",
   :var-type "function",
   :line 1984,
   :file "src/main/clojure/clojure/data/avl.clj"}
  {:arglists ([k coll]),
   :name "split-key",
   :namespace "clojure.data.avl",
   :source-url
   "https://github.com/clojure/data.avl/blob/7dc39e8b4e5ff331dc4624a8b21256c776f81385/src/main/clojure/clojure/data/avl.clj#L1955",
   :raw-source-url
   "https://github.com/clojure/data.avl/raw/7dc39e8b4e5ff331dc4624a8b21256c776f81385/src/main/clojure/clojure/data/avl.clj",
   :wiki-url
   "http://clojure.github.com/data.avl//clojure.data.avl-api.html#clojure.data.avl/split-key",
   :doc
   "(alpha)\n\nReturns [left e? right], where left and right are collections of\nthe same type as coll and containing, respectively, the keys below\nand above k in the ordering determined by coll's comparator, while\ne? is the entry at key k for maps, the stored copy of the key k for\nsets, nil if coll does not contain k.",
   :var-type "function",
   :line 1955,
   :file "src/main/clojure/clojure/data/avl.clj"}
  {:arglists ([coll test limit] [coll start-test start end-test end]),
   :name "subrange",
   :namespace "clojure.data.avl",
   :source-url
   "https://github.com/clojure/data.avl/blob/7dc39e8b4e5ff331dc4624a8b21256c776f81385/src/main/clojure/clojure/data/avl.clj#L1998",
   :raw-source-url
   "https://github.com/clojure/data.avl/raw/7dc39e8b4e5ff331dc4624a8b21256c776f81385/src/main/clojure/clojure/data/avl.clj",
   :wiki-url
   "http://clojure.github.com/data.avl//clojure.data.avl-api.html#clojure.data.avl/subrange",
   :doc
   "(alpha)\n\nReturns an AVL collection comprising the entries of coll between\nstart and end (in the sense determined by coll's comparator) in\nlogarithmic time. Whether the endpoints are themselves included in\nthe returned collection depends on the provided tests; start-test\nmust be either > or >=, end-test must be either < or <=.\n\nWhen passed a single test and limit, subrange infers the other end\nof the range from the test: > / >= mean to include items up to the\nend of coll, < / <= mean to include items taken from the beginning\nof coll.\n\n(subrange >= start <= end) is equivalent to, but more efficient\nthan, (into (empty coll) (subseq coll >= start <= end).",
   :var-type "function",
   :line 1998,
   :file "src/main/clojure/clojure/data/avl.clj"}
  {:arglists ([] [s] [s1 s2] [s1 s2 s3 & more]),
   :name "union",
   :namespace "clojure.data.avl",
   :source-url
   "https://github.com/clojure/data.avl/blob/7dc39e8b4e5ff331dc4624a8b21256c776f81385/src/main/clojure/clojure/data/avl.clj#L2135",
   :raw-source-url
   "https://github.com/clojure/data.avl/raw/7dc39e8b4e5ff331dc4624a8b21256c776f81385/src/main/clojure/clojure/data/avl.clj",
   :wiki-url
   "http://clojure.github.com/data.avl//clojure.data.avl-api.html#clojure.data.avl/union",
   :doc
   "(alpha)\n\nComputes the union of the given AVL sets which should all use the\nsame comparator. nil is accepted and converted into an empty AVL\nset. The value returned is itself an AVL set, except in the nullary\ncase in which nil is returned.",
   :var-type "function",
   :line 2135,
   :file "src/main/clojure/clojure/data/avl.clj"}
  {:arglists ([] [coll] [coll1 coll2] [coll1 coll2 coll3 & more]),
   :name "unsafe-join",
   :namespace "clojure.data.avl",
   :source-url
   "https://github.com/clojure/data.avl/blob/7dc39e8b4e5ff331dc4624a8b21256c776f81385/src/main/clojure/clojure/data/avl.clj#L2051",
   :raw-source-url
   "https://github.com/clojure/data.avl/raw/7dc39e8b4e5ff331dc4624a8b21256c776f81385/src/main/clojure/clojure/data/avl.clj",
   :wiki-url
   "http://clojure.github.com/data.avl//clojure.data.avl-api.html#clojure.data.avl/unsafe-join",
   :doc
   "(alpha)\n\nATTN: This function DOES NOT validate its inputs and WILL return\nmalformed results if the inputs do not satisfy the contract.\n\nMerges or computes the union of colls. All colls must be AVL\ncollections of the same type using the same comparator. If collx\noccurs earlier than colly among the arguments to unsafe-join,\ncollx's greatest element must be strictly smaller than the smallest\nelement of colly (in the sense determined by the comparator).",
   :var-type "function",
   :line 2051,
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
