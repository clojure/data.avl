(ns avl.clj

  "An implementation of persistent sorted maps and sets based on AVL
  trees with API mimicking that of Clojure's sorted maps and
  sets (based on Red-Black Trees). Additionally, the provided map and
  set types support the transients API and logarithmic time rank
  queries via clojure.core/nth (select element by rank) and
  avl.clj/rank-of (discover rank of element)."

  {:author "Michał Marczyk"}

  (:refer-clojure :exclude [sorted-map sorted-map-by sorted-set sorted-set-by]))

(deftype AVLNode [edit
                  ^:mutable key
                  ^:mutable val
                  ^:mutable left
                  ^:mutable right
                  ^:mutable height
                  ^:mutable rank]
  Object
  (getKey [this]
    key)

  (setKey [this k]
    (set! key k)
    this)

  (getVal [this]
    val)

  (setVal [this v]
    (set! val v)
    this)

  (getLeft [this]
    left)

  (setLeft [this l]
    (set! left l)
    this)

  (getRight [this]
    right)

  (setRight [this r]
    (set! right r)
    this)

  (getHeight [this]
    height)

  (setHeight [this h]
    (set! height h)
    this)

  (getRank [this]
    rank)

  (setRank [this r]
    (set! rank r)
    this))

(defn ^:private height [node]
  (if (nil? node)
    0
    (.-height node)))

(defn ^:private ensure-editable
  ([edit]
     (if-not edit
       (throw (ex-info "Transient used after persistent! call" {}))))
  ([edit node]
     (if (identical? edit (.-edit node))
       node
       (AVLNode. edit
                 (.getKey node) (.getVal node)
                 (.getLeft node)
                 (.getRight node)
                 (.getHeight node)
                 (.getRank node)))))

(defn ^:private height [node]
  (if (nil? node)
    0
    (.getHeight node)))

(defn ^:private rotate-left [node]
  (let [l    (.getLeft  node)
        r    (.getRight node)
        rl   (.getLeft  r)
        rr   (.getRight r)
        lh   (height l)
        rlh  (height rl)
        rrh  (height rr)
        rnk  (.getRank node)
        rnkr (.getRank r)]
    (AVLNode. nil
              (.getKey r) (.getVal r)
              (AVLNode. nil
                        (.getKey node) (.getVal node)
                        l
                        rl
                        (inc (max lh rlh))
                        rnk)
              rr
              (max (+ lh 2)
                   (+ rlh 2)
                   (inc rrh))
              (inc (+ rnk rnkr)))))

(defn ^:private rotate-left! [edit node]
  (let [node (ensure-editable edit node)
        l    (.getLeft  node)
        r    (ensure-editable edit (.getRight node))
        rl   (.getLeft  r)
        rr   (.getRight r)
        lh   (height l)
        rlh  (height rl)
        rrh  (height rr)
        rnk  (.getRank node)
        rnkr (.getRank r)]
    (.setLeft   r node)
    (.setHeight r (max (+ lh 2) (+ rlh 2) (inc rrh)))
    (.setRank   r (inc (+ rnk rnkr)))
    (.setRight  node rl)
    (.setHeight node (inc (max lh rlh)))
    r))

(defn ^:private rotate-right [node]
  (let [r    (.getRight node)
        l    (.getLeft  node)
        lr   (.getRight l)
        ll   (.getLeft  l)
        rh   (height r)
        lrh  (height lr)
        llh  (height ll)
        rnk  (.getRank node)
        rnkl (.getRank l)]
    (AVLNode. nil
              (.getKey l) (.getVal l)
              ll
              (AVLNode. nil
                        (.getKey node) (.getVal node)
                        lr
                        r
                        (inc (max rh lrh))
                        (dec (- rnk rnkl)))
              (max (+ rh 2)
                   (+ lrh 2)
                   (inc llh))
              rnkl)))

(defn ^:private rotate-right! [edit node]
  (let [node (ensure-editable edit node)
        r    (.getRight node)
        l    (ensure-editable edit (.getLeft  node))
        lr   (.getRight l)
        ll   (.getLeft  l)
        rh   (height r)
        lrh  (height lr)
        llh  (height ll)
        rnk  (.getRank node)
        rnkl (.getRank l)]
    (.setRight  l node)
    (.setHeight l (max (+ rh 2) (+ lrh 2) (inc llh)))
    (.setLeft   node lr)
    (.setHeight node (inc (max rh lrh)))
    (.setRank   node (dec (- rnk rnkl)))
    l))

(defn ^:private lookup [comp node k]
  (if (nil? node)
    nil
    (let [c (comp k (.getKey node))]
      (cond
        (zero? c) node
        (neg? c)  (recur comp (.getLeft node)  k)
        :else     (recur comp (.getRight node) k)))))

(defn ^:private select [node rank]
  (if (nil? node)
    (throw (ex-info "nth indexed out of bounds in AVL tree" {}))
    (let [node-rank (.getRank node)]
      (cond
        (== node-rank rank) node
        (<  node-rank rank) (recur (.getRight node) (dec (- rank node-rank)))
        :else               (recur (.getLeft node)  rank)))))

(defn ^:private rank [comp node k]
  (if (nil? node)
    -1
    (let [c (comp k (.getKey node))]
      (cond
        (zero? c) (.getRank node)
        (neg? c)  (recur comp (.getLeft node) k)
        :else     (inc (+ (.getRank node) (rank comp (.getRight node) k)))))))

(defn ^:private maybe-rebalance [node]
  (let [l  (.getLeft node)
        r  (.getRight node)
        lh (height l)
        rh (height r)
        b  (- lh rh)]
    (cond
      ;; right-heavy
      (< b -1)
      (let [rl  (.getLeft r)
            rr  (.getRight r)
            rlh (height rl)
            rrh (height rr)]
        (if (>= (- rlh rrh) 2)
          ;; left-heavy
          (let [new-right (rotate-right r)]
            (rotate-left (AVLNode. nil
                                   (.getKey node) (.getVal node)
                                   (.getLeft node)
                                   new-right
                                   (inc (max lh (height new-right)))
                                   (.getRank node))))
          (rotate-left node)))

      ;; left-heavy
      (> b 1)
      (let [ll  (.getLeft l)
            lr  (.getRight l)
            llh (height ll)
            lrh (height lr)]
        ;; right-heavy
        (if (>= (- lrh llh) 2)
          (let [new-left (rotate-left l)]
            (rotate-right (AVLNode. nil
                                    (.getKey node) (.getVal node)
                                    new-left
                                    (.getRight node)
                                    (inc (max rh (height new-left)))
                                    (.getRank node))))
          (rotate-right node)))

      :else
      node)))

(defn ^:private maybe-rebalance! [edit node]
  (let [l  (.getLeft node)
        r  (.getRight node)
        lh (height l)
        rh (height r)
        b  (- lh rh)]
    (cond
      ;; right-heavy
      (< b -1)
      (let [node (ensure-editable edit node)
            rl   (.getLeft r)
            rr   (.getRight r)
            rlh  (height rl)
            rrh  (height rr)]
        (if (>= (- rlh rrh) 2)
          ;; left-heavy
          (let [new-right (rotate-right! edit r)]
            (.setRight  node new-right)
            (.setHeight node (inc (max lh (height new-right))))
            (rotate-left! edit node))
          (rotate-left! edit node)))

      ;; left-heavy
      (> b 1)
      (let [node (ensure-editable edit node)
            ll   (.getLeft l)
            lr   (.getRight l)
            llh  (height ll)
            lrh  (height lr)]
        ;; right-heavy
        (if (>= (- lrh llh) 2)
          (let [new-left (rotate-left! edit l)]
            (.setLeft   node new-left)
            (.setHeight node (inc (max rh (height new-left))))
            (rotate-right! edit node))
          (rotate-right! edit node)))

      :else
      node)))

(defn ^:private insert [comp node k v found?]
  (if (nil? node)
    (AVLNode. nil k v nil nil 1 0)
    (let [nk (.getKey node)
          c  (comp k nk)]
      (cond
        (zero? c)
        (do
          (set! (.-val found?) true)
          (AVLNode. nil
                    k v
                    (.getLeft   node)
                    (.getRight  node)
                    (.getHeight node)
                    (.getRank   node)))

        (neg? c)
        (let [new-child (insert comp (.getLeft node) k v found?)]
          (maybe-rebalance
           (AVLNode. nil
                     nk (.getVal node)
                     new-child
                     (.getRight node)
                     (inc (max (.getHeight new-child)
                               (height (.getRight node))))
                     (if (.-val found?)
                       (.getRank node)
                       (inc (.getRank node))))))

        :else
        (let [new-child (insert comp (.getRight node) k v found?)]
          (maybe-rebalance
           (AVLNode. nil
                     nk (.getVal node)
                     (.getLeft node)
                     new-child
                     (inc (max (.getHeight new-child)
                               (height (.getLeft node))))
                     (.getRank node))))))))

(defn ^:private insert! [edit comp node k v found?]
  (if (nil? node)
    (AVLNode. edit k v nil nil 1 0)
    (let [node (ensure-editable edit node)
          nk   (.getKey node)
          c    (comp k nk)]
      (cond
        (zero? c)
        (do
          (set! (.-val found?) true)
          (.setKey node k)
          (.setVal node v)
          node)

        (neg? c)
        (let [new-child (insert! edit comp (.getLeft node) k v found?)]
          (.setLeft node new-child)
          (.setHeight node
                      (inc (max (.getHeight new-child)
                                (height (.getRight node)))))
          (if-not (.-val found?)
            (.setRank node (unchecked-inc-int (.getRank node))))
          (maybe-rebalance! edit node))

        :else
        (let [new-child (insert! edit comp (.getRight node) k v found?)]
          (.setRight node new-child)
          (.setHeight node
                      (inc (max (.getHeight new-child)
                                (height (.getLeft node)))))
          (maybe-rebalance! edit node))))))

(defn ^:private get-rightmost [node]
  (if-let [r (.getRight node)]
    (recur r)
    node))

(defn ^:private delete-rightmost [node]
  (if-let [r (.getRight node)]
    (let [l         (.getLeft node)
          new-right (delete-rightmost r)]
      (AVLNode. nil
                (.getKey node) (.getVal node)
                l
                new-right
                (inc (max (height l) (height new-right)))
                (.getRank node)))
    (.getLeft node)))

(defn ^:private delete-rightmost! [edit node]
  (if-not (nil? node)
    (let [node (ensure-editable edit node)
          r    (.getRight node)]
      (cond
        (nil? r)
        (if-let [l (.getLeft node)]
          (ensure-editable edit l))

        (nil? (.getRight r))
        (do
          (.setRight node (.getLeft r))
          (.setHeight node
                      (inc (max (height (.getLeft node))
                                (height (.getLeft r)))))
          node)

        :else
        (let [new-right (delete-rightmost! edit r)]
          (.setRight node new-right)
          (.setHeight node
                      (inc (max (height (.getLeft node))
                                (height new-right))))
          node)))))

(defn ^:private delete [comp node k found?]
  (if (nil? node)
    nil
    (let [nk (.getKey node)
          c  (comp k nk)]
      (cond
        (zero? c)
        (let [l (.getLeft node)
              r (.getRight node)]
          (set! (.-val found?) true)
          (if (and l r)
            (let [p  (get-rightmost l)
                  l' (delete-rightmost l)]
              (AVLNode. nil
                        (.getKey p) (.getVal p)
                        l'
                        r
                        (inc (max (height l') (height r)))
                        (unchecked-dec-int (.getRank node))))
            (or l r)))

        (neg? c)
        (let [new-child (delete comp (.getLeft node) k found?)]
          (if (identical? new-child (.getLeft node))
            node
            (maybe-rebalance
             (AVLNode. nil
                       nk (.getVal node)
                       new-child
                       (.getRight node)
                       (inc (max (height new-child)
                                 (height (.getRight node))))
                       (if (.-val found?)
                         (unchecked-dec-int (.getRank node))
                         (.getRank node))))))

        :else
        (let [new-child (delete comp (.getRight node) k found?)]
          (if (identical? new-child (.getRight node))
            node
            (maybe-rebalance
             (AVLNode. nil
                       nk (.getVal node)
                       (.getLeft node)
                       new-child
                       (inc (max (height new-child)
                                 (height (.getLeft node))))
                       (.getRank node)))))))))

(defn ^:private delete! [edit comp node k found?]
  (if (nil? node)
    nil
    (let [nk   (.getKey node)
          c    (comp k nk)]
      (cond
        (zero? c)
        (let [l (.getLeft node)
              r (.getRight node)]
          (set! (.-val found?) true)
          (cond
            (and l r)
            (let [node (ensure-editable edit node)
                  p    (get-rightmost l)
                  l'   (delete-rightmost! edit l)]
              (.setKey    node (.getKey p))
              (.setVal    node (.getVal p))
              (.setLeft   node l')
              (.setHeight node (inc (max (height l') (height r))))
              (.setRank   node (unchecked-dec-int (.getRank node)))
              node)

            l l
            r r
            :else nil))

        (neg? c)
        (let [new-child (delete! edit comp (.getLeft node) k found?)]
          (if (identical? new-child (.getLeft node))
            node
            (let [node (ensure-editable edit node)]
              (.setLeft node new-child)
              (.setHeight node
                          (inc (max (height new-child)
                                    (height (.getRight node)))))
              (if (.-val found?)
                (.setRank node (unchecked-dec-int (.getRank node))))
              (maybe-rebalance! edit node))))

        :else
        (let [new-child (delete! edit comp (.getRight node) k found?)]
          (if (identical? new-child (.getRight node))
            node
            (let [node (ensure-editable edit node)]
              (.setRight node new-child)
              (.setHeight node
                          (inc (max (height new-child)
                                    (height (.getLeft node)))))
              (maybe-rebalance! edit node))))))))

(defn ^:private seq-push [node stack ascending?]
  (loop [node node stack stack]
    (if (nil? node)
      stack
      (recur (if ascending? (.-left node) (.-right node))
             (conj stack node)))))

(declare ->AVLMapSeq)

(defn ^:private create-seq [node ascending? cnt]
  (->AVLMapSeq nil (seq-push node nil ascending?) ascending? cnt nil))

(defn ^:private avl-map-kv-reduce [node f init]
  (let [init (if (nil? (.getLeft node))
               init
               (avl-map-kv-reduce (.getLeft node) f init))]
    (if (reduced? init)
      init
      (let [init (f init (.getKey node) (.getVal node))]
        (if (reduced? init)
          init
          (let [init (if (nil? (.getRight node))
                       init
                       (avl-map-kv-reduce (.getRight node) f init))]
            init))))))

(deftype AVLMapSeq [_meta stack ascending? cnt ^:mutable _hash]
  Object
  (toString [this]
    (pr-str* this))

  IHash
  (-hash [this]
    (caching-hash this hash-coll _hash))

  ISeqable
  (-seq [this]
    this)

  ISequential
  ISeq
  (-first [this]
    (let [node (peek stack)]
      [(.-key node) (.-val node)]))

  (-rest [this]
    (let [node (first stack)
          next-stack (seq-push (if ascending? (.-right node) (.-left node))
                               (next stack)
                               ascending?)]
      (if (nil? next-stack)
        ()
        (AVLMapSeq. nil next-stack ascending? (dec cnt) nil))))

  INext
  (-next [this]
    (-seq (-rest this)))

  ICounted
  (-count [this]
    (if (neg? cnt)
      (inc (-count (-next this)))
      cnt))

  ICollection
  (-conj [this x]
    (cons x this))

  IEquiv
  (-equiv [this that]
    (equiv-sequential this that))

  IEmptyableCollection
  (-empty [this]
    (with-meta () _meta))

  IMeta
  (-meta [this]
    _meta)

  IWithMeta
  (-with-meta [this meta]
    (AVLMapSeq. meta stack ascending? cnt _hash))

  IReduce
  (-reduce [this f]
    (seq-reduce f this))

  (-reduce [this f start]
    (seq-reduce f start this)))

(declare ->AVLTransientMap)

(deftype AVLMap [comp tree cnt _meta ^:mutable _hash]
  Object
  (toString [this]
    (pr-str* this))

  (getTree [this]
    tree)

  IHash
  (-hash [this]
    (caching-hash this hash-imap _hash))

  IMeta
  (meta [this]
    _meta)

  IWithMeta
  (-with-meta [this meta]
    (AVLMap. comp tree cnt meta _hash))

  ICounted
  (-count [this]
    cnt)

  IIndexed
  (-nth [this i]
    (-nth this i nil))

  (-nth [this i not-found]
    (if-let [n (select tree i)]
      [(.getKey n) (.getVal n)]
      not-found))

  ICollection
  (-conj [this entry]
    (if (vector? entry)
      (assoc this (-nth entry 0) (-nth entry 1))
      (reduce -conj this entry)))

  IEmptyableCollection
  (-empty [this]
    (AVLMap. comp nil 0 _meta 0))

  IEquiv
  (-equiv [this that]
    (equiv-map this that))

  IKVReduce
  (-kv-reduce [this f init]
    (if (nil? tree)
      init
      (let [init (avl-map-kv-reduce tree f init)]
        (if (reduced? init)
          @init
          init))))

  IFn
  (-invoke [this k]
    (-lookup this k))

  (-invoke [this k not-found]
    (-lookup this k not-found))

  ISeqable
  (-seq [this]
    (if (pos? cnt)
      (create-seq tree true cnt)))

  IReversible
  (-rseq [this]
    (if (pos? cnt)
      (create-seq tree false cnt)))

  ILookup
  (-lookup [this k]
    (-lookup this k nil))

  (-lookup [this k not-found]
    (let [n (lookup comp tree k)]
      (if-not (nil? n)
        (.-val n)
        not-found)))

  IAssociative
  (-assoc [this k v]
    (let [found?   (Box. false)
          new-tree (insert comp tree k v found?)]
      (AVLMap. comp
               new-tree
               (if (.-val found?) cnt (inc cnt))
               _meta nil)))

  (-contains-key? [this k]
    (not (nil? (.entryAt this k))))

  IMap
  (-dissoc [this k]
    (let [found?   (Box. false)
          new-tree (delete comp tree k found?)]
      (if (.-val found?)
        (AVLMap. comp
                 new-tree
                 (dec cnt)
                 _meta nil)
        this)))

  ISorted
  (-sorted-seq [this ascending?]
    (if (pos? cnt)
      (create-seq tree ascending? cnt)))

  (-sorted-seq-from [this k ascending?]
    (if (pos? cnt)
      (loop [stack nil t tree]
        (if-not (nil? t)
          (let [c (comp k (.-key t))]
            (cond
              (zero? c)  (AVLMapSeq. nil (conj stack t) ascending? -1 nil)
              ascending? (if (neg? c)
                           (recur (conj stack t) (.-left t))
                           (recur stack          (.-right t)))
              :else      (if (pos? c)
                           (recur (conj stack t) (.-right t))
                           (recur stack          (.-left t)))))
          (if-not (nil? stack)
            (AVLMapSeq. nil stack ascending? -1 nil))))))

  (-entry-key [this entry]
    (key entry))

  (-comparator [this]
    comp)

  IEditableCollection
  (-as-transient [this]
    (->AVLTransientMap (js-obj) comp tree cnt)))

(deftype AVLTransientMap [^:mutable edit comp ^:mutable tree ^:mutable cnt]
  ICounted
  (-count [this]
    cnt)

  ILookup
  (-lookup [this k]
    (-lookup this k nil))

  (-lookup [this k not-found]
    (let [n ^IAVLNode (lookup comp tree k)]
      (if-not (nil? n)
        (.getVal n)
        not-found)))

  IFn
  (-invoke [this k]
    (-lookup this k))

  (-invoke [this k not-found]
    (-lookup this k not-found))

  ITransientCollection
  (-conj! [this entry]
    (if (vector? entry)
      (assoc! this (nth entry 0) (nth entry 1))
      (reduce conj! this entry)))

  (-persistent! [this]
    (ensure-editable edit)
    (set! edit nil)
    (AVLMap. comp tree cnt nil nil))

  ITransientAssociative
  (-assoc! [this k v]
    (ensure-editable edit)
    (let [found?   (Box. false)
          new-tree (insert! edit comp tree k v found?)]
      (set! tree new-tree)
      (if-not (.-val found?)
        (set! cnt (inc cnt)))
      this))

  ITransientMap
  (-dissoc! [this k]
    (ensure-editable edit)
    (let [found?   (Box. false)
          new-tree (delete! edit comp tree k found?)]
      (when (.-val found?)
        (set! tree new-tree)
        (set! cnt  (dec cnt)))
      this)))

(declare ->AVLTransientSet)

(deftype AVLSet [_meta avl-map ^:mutable _hash]
  Object
  (toString [this]
    (pr-str* this))

  (getTree [this]
    (.-tree avl-map))

  IHash
  (-hash [this]
    (caching-hash this hash-iset _hash))

  IMeta
  (meta [this]
    _meta)

  IWithMeta
  (-with-meta [this meta]
    (AVLSet. meta avl-map _hash))

  ICounted
  (-count [this]
    (-count avl-map))

  IIndexed
  (-nth [this i]
    (-nth this i nil))

  (-nth [this i not-found]
    (if-let [n (select (.-tree avl-map) i)]
      (.getVal n)
      not-found))

  ICollection
  (-conj [this x]
    (AVLSet. _meta (assoc avl-map x x) nil))

  IEmptyableCollection
  (-empty [this]
    (AVLSet. _meta (empty avl-map) 0))

  IEquiv
  (-equiv [this that]
    (and
     (set? that)
     (== (count this) (count that))
     (every? #(contains? this %) that)))

  ISeqable
  (-seq [this]
    (keys avl-map))

  ISorted
  (-sorted-seq [this ascending?]
    (keys (-sorted-seq avl-map ascending?)))

  (-sorted-seq-from [this k ascending?]
    (keys (-sorted-seq-from avl-map k ascending?)))

  (-entry-key [this entry]
    entry)

  (-comparator [this]
    (-comparator avl-map))

  IReversible
  (-rseq [this]
    (map key (rseq avl-map)))

  ILookup
  (-lookup [this v]
    (-lookup this v nil))

  (-lookup [this v not-found]
    (let [n (.entryAt avl-map v)]
      (if-not (nil? n)
        (.-key n)
        not-found)))

  ISet
  (-disjoin [this v]
    (AVLSet. _meta (dissoc avl-map v) nil))

  IFn
  (invoke [this k]
    (-lookup this k))

  (invoke [this k not-found]
    (-lookup this k not-found))

  IEditableCollection
  (-as-transient [this]
    (->AVLTransientSet (-as-transient avl-map))))

(deftype AVLTransientSet [^:mutable transient-avl-map]
  ITransientCollection
  (-conj! [this k]
    (set! transient-avl-map (-assoc! transient-avl-map k k))
    this)

  (-persistent! [this]
    (if (nil? (.-edit transient-avl-map))
      (throw (ex-info "persistent! used twice" {}))
      (AVLSet. nil (-persistent! transient-avl-map) nil)))

  ITransientSet
  (-disjoin! [this k]
    (set! transient-avl-map (-dissoc! transient-avl-map k))
    this)

  ICounted
  (-count [this]
    (-count transient-avl-map))

  ILookup
  (-lookup [this k]
    (-lookup this k nil))

  (-lookup [this k not-found]
    (if (identical? (-lookup transient-avl-map k lookup-sentinel)
                    lookup-sentinel)
      not-found
      k))

  IFn
  (-invoke [this k]
    (-lookup transient-avl-map k))

  (invoke [this k not-found]
    (-lookup transient-avl-map k not-found)))

(def ^:private empty-map (AVLMap. compare nil 0 nil 0))

(def ^:private empty-set (AVLSet. nil empty-map 0))

(extend-protocol IPrintWithWriter
  AVLMapSeq
  (-pr-writer [this writer opts]
    (pr-sequential-writer writer pr-writer "(" " " ")" opts this))

  AVLMap
  (-pr-writer [this writer opts]
    (letfn [(pr-pair [keyval]
              (pr-sequential-writer writer pr-writer "" " " "" opts keyval))]
      (pr-sequential-writer writer pr-pair "{" ", " "}" opts this)))

  AVLSet
  (-pr-writer [this writer opts]
    (pr-sequential-writer writer pr-writer "#{" " " "}" opts this)))

(defn sorted-map
  "keyval => key val
  Returns a new AVL map with supplied mappings."
  [& keyvals]
  (loop [in (seq keyvals) out (transient empty-map)]
    (if in
      (recur (nnext in) (assoc! out (first in) (second in)))
      (persistent! out))))

(defn sorted-map-by
  "keyval => key val
  Returns a new sorted map with supplied mappings, using the supplied
  comparator."
  [comparator & keyvals]
  (loop [in  (seq keyvals)
         out (AVLTransientMap. (js-obj) (fn->comparator comparator) nil 0)]
    (if in
      (recur (nnext in) (assoc! out (first in) (second in)))
      (persistent! out))))

(defn sorted-set
  "Returns a new sorted set with supplied keys."
  [& keys]
  (persistent! (reduce conj! (transient empty-set) keys)))

(defn sorted-set-by
  "Returns a new sorted set with supplied keys, using the supplied comparator."
  [comparator & keys]
  (persistent!
   (reduce conj!
           (AVLTransientSet.
            (-as-transient (sorted-map-by (fn->comparator comparator))))
           keys)))

(defn rank-of
  "Returns the rank of x in coll or -1 if not present."
  [coll x]
  (rank (-comparator coll) (.getTree coll) x))
