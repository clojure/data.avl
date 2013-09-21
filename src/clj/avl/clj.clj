(ns avl.clj

  "An implementation of persistent sorted maps and sets based on AVL
  trees with API mimicking that of Clojure's sorted maps and
  sets (based on Red-Black Trees). Additionally, the provided map and
  set types support the transients API and logarithmic time rank
  queries via clojure.core/nth."

  {:author "Michał Marczyk"}

  (:refer-clojure :exclude [sorted-map sorted-map-by sorted-set sorted-set-by])
  (:import (clojure.lang RT Util APersistentMap Box MapEntry SeqIterator
                         IPersistentMap IPersistentSet IPersistentStack)
           (java.util Comparator Collections ArrayList)
           (java.util.concurrent.atomic AtomicReference)))

(defn ^:private throw-unsupported []
  (throw (UnsupportedOperationException.)))

(defmacro ^:private caching-hash [coll hash-fn hash-key]
  `(let [h# ~hash-key]
     (if-not (== h# (int -1))
       h#
       (let [h# (~hash-fn ~coll)]
         (set! ~hash-key (int h#))
         h#))))

(defn ^:private hash-imap
  [^IPersistentMap m]
  (APersistentMap/mapHash m))

(defn ^:private hasheq-imap
  [^IPersistentMap m]
  (APersistentMap/mapHasheq m))

(defn ^:private hash-iset [^IPersistentSet s]
  ;; a la clojure.lang.APersistentSet
  (loop [h (int 0) s (seq s)]
    (if s
      (let [e (first s)]
        (recur (unchecked-add-int h (hash e))
               (next s)))
      h)))

(defn ^:private hasheq-iset [^IPersistentSet s]
  (loop [h (int 0) s (seq s)]
    (if s
      (recur (unchecked-add-int h (Util/hasheq (first s)))
             (next s))
      h)))

(defn ^:private hash-seq
  [s]
  (loop [h (int 1) s (seq s)]
    (if s
      (recur (unchecked-add-int (unchecked-multiply-int (int 31) h)
                                (if (nil? (first s))
                                  (int 0)
                                  (.hashCode ^Object (first s))))
             (next s))
      h)))

(defn ^:private hasheq-seq
  [s]
  (loop [h (int 1) s (seq s)]
    (if s
      (recur (unchecked-add-int (unchecked-multiply-int (int 31) h)
                                (Util/hasheq (first s)))
             (next s))
      h)))

(defn ^:private equiv-sequential
  "Assumes x is sequential. Returns true if x equals y, otherwise
  returns false."
  [x y]
  (boolean
   (when (sequential? y)
     (loop [xs (seq x) ys (seq y)]
       (cond (nil? xs) (nil? ys)
             (nil? ys) false
             (= (first xs) (first ys)) (recur (next xs) (next ys))
             :else false)))))

(def ^:private never-equiv (Object.))

(defn ^:private equiv-map
  "Assumes y is a map. Returns true if x equals y, otherwise returns
  false."
  [x y]
  (boolean
    (when (map? y)
      ; assume all maps are counted
      (when (== (count x) (count y))
        (every? identity
                (map (fn [xkv] (= (get y (first xkv) never-equiv)
                                  (second xkv)))
                     x))))))

(gen-interface
 :name avl.clj.IAVLNode
 :methods
 [[getKey    []                 Object]
  [setKey    [Object]           avl.clj.IAVLNode]
  [getVal    []                 Object]
  [setVal    [Object]           avl.clj.IAVLNode]
  [getLeft   []                 avl.clj.IAVLNode]
  [setLeft   [avl.clj.IAVLNode] avl.clj.IAVLNode]
  [getRight  []                 avl.clj.IAVLNode]
  [setRight  [avl.clj.IAVLNode] avl.clj.IAVLNode]
  [getHeight []                 int]
  [setHeight [int]              avl.clj.IAVLNode]
  [getRank   []                 int]
  [setRank   [int]              avl.clj.IAVLNode]])

(import avl.clj.IAVLNode)

(deftype AVLNode [^AtomicReference edit
                  ^:unsynchronized-mutable key
                  ^:unsynchronized-mutable val
                  ^:unsynchronized-mutable ^IAVLNode left
                  ^:unsynchronized-mutable ^IAVLNode right
                  ^:unsynchronized-mutable ^int height
                  ^:unsynchronized-mutable ^int rank]
  IAVLNode
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

(defn ^:private ensure-editable
  (^IAVLNode [^AtomicReference edit]
     (let [owner (.get edit)]
       (cond
         (identical? owner (Thread/currentThread))
         true

         (nil? owner)
         (throw (IllegalAccessError. "Transient used after persistent! call"))

         :else
         (throw (IllegalAccessError. "Transient used by non-owner thread")))))
  (^IAVLNode [^AtomicReference edit ^AVLNode node]
     (if (identical? edit (.-edit node))
       node
       (AVLNode. edit
                 (.getKey node) (.getVal node)
                 (.getLeft node)
                 (.getRight node)
                 (.getHeight node)
                 (.getRank node)))))

(defn ^:private height ^long [^IAVLNode node]
  (if (nil? node)
    0
    (long (.getHeight node))))

(defn ^:private rotate-left ^IAVLNode [^IAVLNode node]
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

(defn ^:private rotate-left! ^IAVLNode [edit ^IAVLNode node]
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

(defn ^:private rotate-right ^IAVLNode [^IAVLNode node]
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

(defn ^:private rotate-right! ^IAVLNode [edit ^IAVLNode node]
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

(defn ^:private lookup ^IAVLNode [^Comparator comp ^IAVLNode node k]
  (if (nil? node)
    nil
    (let [c (.compare comp k (.getKey node))]
      (cond
        (zero? c) node
        (neg? c)  (recur comp (.getLeft node)  k)
        :else     (recur comp (.getRight node) k)))))

(defn ^:private rank-lookup ^IAVLNode [^IAVLNode node rank]
  (if (nil? node)
    (throw
     (IndexOutOfBoundsException. "nth indexed out of bounds in AVL tree"))
    (let [node-rank (.getRank node)]
      (cond
        (== node-rank rank) node
        (<  node-rank rank) (recur (.getRight node) (dec (- rank node-rank)))
        :else               (recur (.getLeft node)  rank)))))

(defn ^:private maybe-rebalance ^IAVLNode [^IAVLNode node]
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

(defn ^:private maybe-rebalance! ^IAVLNode [edit ^IAVLNode node]
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

(defn ^:private insert
  ^IAVLNode [^Comparator comp ^IAVLNode node k v ^Box found?]
  (if (nil? node)
    (AVLNode. nil k v nil nil 1 0)
    (let [nk (.getKey node)
          c  (.compare comp k nk)]
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
                       (unchecked-inc-int (.getRank node))))))

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

(defn ^:private insert!
  ^IAVLNode [edit ^Comparator comp ^IAVLNode node k v ^Box found?]
  (if (nil? node)
    (AVLNode. edit k v nil nil 1 0)
    (let [node (ensure-editable edit node)
          nk   (.getKey node)
          c    (.compare comp k nk)]
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

(defn ^:private get-rightmost ^IAVLNode [^IAVLNode node]
  (if-let [r (.getRight node)]
    (recur r)
    node))

(defn ^:private delete-rightmost ^IAVLNode [^IAVLNode node]
  (if-let [r (.getRight node)]
    (let [l         (.getLeft node)
          new-right (delete-rightmost r)]
      (AVLNode. nil
                (.getKey node) (.getVal node)
                l
                new-right
                (inc (max (height l) (height new-right)))
                (.getRank node)))))

(defn ^:private delete-rightmost! ^IAVLNode [edit ^IAVLNode node]
  (if-not (nil? node)
    (let [node (ensure-editable edit node)
          r    ^IAVLNode (.getRight node)]
      (cond
        (nil? r)
        nil

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

(defn ^:private delete
  ^IAVLNode [^Comparator comp ^IAVLNode node k ^Box found?]
  (if (nil? node)
    nil
    (let [nk (.getKey node)
          c  (.compare comp k nk)]
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

(defn ^:private delete!
  ^IAVLNode [edit ^Comparator comp ^IAVLNode node k ^Box found?]
  (if (nil? node)
    nil
    (let [nk   (.getKey node)
          c    (.compare comp k nk)]
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

(defn ^:private seq-push [^IAVLNode node stack ascending?]
  (loop [node node stack stack]
    (if (nil? node)
      stack
      (recur (if ascending? (.getLeft node) (.getRight node))
             (conj stack node)))))

(defn ^:private avl-map-kv-reduce [^IAVLNode node f init]
  (let [init (if-not (nil? (.getLeft node))
               (avl-map-kv-reduce (.getLeft node) f init)
               init)]
    (if (reduced? init)
      @init
      (let [init (f init (.getKey node) (.getVal node))]
        (if (reduced? init)
          @init
          (let [init (if-not (nil? (.getRight node))
                       (avl-map-kv-reduce (.getRight node) f init)
                       init)]
            (if (reduced? init)
              @init
              init)))))))

(deftype AVLMapSeq [^IPersistentMap _meta
                    ^IPersistentStack stack
                    ^boolean ascending?
                    ^int cnt
                    ^:unsynchronized-mutable ^int _hash
                    ^:unsynchronized-mutable ^int _hasheq]
  :no-print true

  Object
  (toString [this]
    (RT/printString this))

  (hashCode [this]
    (caching-hash this hash-seq _hash))

  clojure.lang.IHashEq
  (hasheq [this]
    (caching-hash this hasheq-seq _hasheq))

  clojure.lang.Seqable
  (seq [this]
    this)

  clojure.lang.Sequential
  clojure.lang.ISeq
  (first [this]
    (let [node ^IAVLNode (peek stack)]
      (MapEntry. (.getKey node) (.getVal node))))

  (more [this]
    (let [node ^IAVLNode (first stack)
          next-stack (seq-push (if ascending? (.getRight node) (.getLeft node))
                               (next stack)
                               ascending?)]
      (if (nil? next-stack)
        ()
        (AVLMapSeq. nil next-stack ascending? (unchecked-dec-int cnt) -1 -1))))

  (next [this]
    (.seq (.more this)))

  clojure.lang.Counted
  (count [this]
    (if (neg? cnt)
      (unchecked-inc-int (count (next this)))
      cnt))

  clojure.lang.IPersistentCollection
  (cons [this x]
    (cons x this))

  (equiv [this that]
    (equiv-sequential this that))

  (empty [this]
    (with-meta () _meta))

  clojure.lang.IMeta
  (meta [this]
    _meta)

  clojure.lang.IObj
  (withMeta [this meta]
    (AVLMapSeq. meta stack ascending? cnt _hash _hasheq))

  java.io.Serializable

  java.util.List
  (toArray [this]
    (RT/seqToArray (seq this)))

  (toArray [this arr]
    (RT/seqToPassedArray (seq this) arr))

  (containsAll [this c]
    (every? #(.contains this %) (iterator-seq (.iterator c))))

  (size [this]
    (count this))

  (isEmpty [this]
    (zero? cnt))

  (contains [this x]
    (or (some #(Util/equiv % x) this) false))

  (iterator [this]
    (SeqIterator. this))

  (subList [this from to]
    (.subList (Collections/unmodifiableList (ArrayList. this)) from to))

  (indexOf [this x]
    (loop [i (int 0) s (seq this)]
      (if s
        (if (Util/equiv (first s) x)
          i
          (recur (unchecked-inc-int i) (next s)))
        (int -1))))

  (lastIndexOf [this x]
    (.lastIndexOf (ArrayList. this) x))

  (listIterator [this]
    (.listIterator (Collections/unmodifiableList (ArrayList. this))))

  (listIterator [this i]
    (.listIterator (Collections/unmodifiableList (ArrayList. this)) i))

  (get [this i]
    (RT/nth this i))

  (add             [this x]      (throw-unsupported))
  (^boolean remove [this x]      (throw-unsupported))
  (addAll          [this c]      (throw-unsupported))
  (clear           [this]        (throw-unsupported))
  (retainAll       [this c]      (throw-unsupported))
  (removeAll       [this c]      (throw-unsupported))
  (set             [this i e]    (throw-unsupported))
  (remove          [this ^int i] (throw-unsupported))
  (add             [this i e]    (throw-unsupported)))

(defn ^:private create-seq [node ascending? cnt]
  (AVLMapSeq. nil (seq-push node nil ascending?) ascending? cnt -1 -1))

(declare ->AVLTransientMap)

(deftype AVLMap [^Comparator comp
                 ^IAVLNode tree
                 ^int cnt
                 ^IPersistentMap _meta
                 ^:unsynchronized-mutable ^int _hash
                 ^:unsynchronized-mutable ^int _hasheq]
  Object
  (toString [this]
    (RT/printString this))

  (hashCode [this]
    (caching-hash this hash-imap _hash))

  (equals [this that]
    (APersistentMap/mapEquals this that))

  clojure.lang.IHashEq
  (hasheq [this]
    (caching-hash this hasheq-imap _hasheq))

  clojure.lang.IMeta
  (meta [this]
    _meta)

  clojure.lang.IObj
  (withMeta [this meta]
    (AVLMap. comp tree cnt meta _hash _hasheq))

  clojure.lang.Counted
  (count [this]
    cnt)

  clojure.lang.Indexed
  (nth [this i]
    (.nth this i nil))

  (nth [this i not-found]
    (if-let [n (rank-lookup tree i)]
      (MapEntry. (.getKey ^IAVLNode n) (.getVal ^IAVLNode n))
      not-found))

  clojure.lang.IPersistentCollection
  (cons [this entry]
    (if (vector? entry)
      (assoc this (nth entry 0) (nth entry 1))
      (reduce conj this entry)))

  (empty [this]
    (AVLMap. comp nil 0 _meta 0 0))

  (equiv [this that]
    (equiv-map this that))

  clojure.core.protocols/IKVReduce
  (kv-reduce [this f init]
    (if-not (nil? tree)
      (avl-map-kv-reduce tree f init)
      init))

  clojure.lang.IFn
  (invoke [this k]
    (.valAt this k))

  (invoke [this k not-found]
    (.valAt this k not-found))

  (applyTo [this args]
    (let [n (RT/boundedLength args 2)]
      (case n
        0 (throw (clojure.lang.ArityException.
                  n (.. this (getClass) (getSimpleName))))
        1 (.invoke this (first args))
        2 (.invoke this (first args) (second args))
        3 (throw (clojure.lang.ArityException.
                  n (.. this (getClass) (getSimpleName)))))))

  clojure.lang.Seqable
  (seq [this]
    (if (pos? cnt)
      (create-seq tree true cnt)))

  clojure.lang.Reversible
  (rseq [this]
    (if (pos? cnt)
      (create-seq tree false cnt)))

  clojure.lang.ILookup
  (valAt [this k]
    (.valAt this k nil))

  (valAt [this k not-found]
    (let [n ^IAVLNode (lookup comp tree k)]
      (if-not (nil? n)
        (.getVal n)
        not-found)))

  clojure.lang.Associative
  (assoc [this k v]
    (let [found?   (Box. false)
          new-tree (insert comp tree k v found?)]
      (AVLMap. comp
               new-tree
               (if (.-val found?) cnt (unchecked-inc-int cnt))
               _meta -1 -1)))

  (containsKey [this k]
    (not (nil? (.entryAt this k))))

  (entryAt [this k]
    (if-let [node (lookup comp tree k)]
      (MapEntry. (.getKey node) (.getVal node))))

  clojure.lang.MapEquivalence
  clojure.lang.IPersistentMap
  (without [this k]
    (let [found?   (Box. false)
          new-tree (delete comp tree k found?)]
      (if (.-val found?)
        (AVLMap. comp
                 new-tree
                 (unchecked-dec-int cnt)
                 _meta -1 -1)
        this)))

  (assocEx [this k v]
    (let [found?   (Box. false)
          new-tree (insert comp tree k v found?)]
      (if (.-val found?)
        (throw (ex-info "key already present" {}))
        (AVLMap. comp
                 new-tree
                 (unchecked-inc-int cnt)
                 _meta -1 -1))))

  clojure.lang.Sorted
  (seq [this ascending?]
    (if (pos? cnt)
      (create-seq tree ascending? cnt)))

  (seqFrom [this k ascending?]
    (if (pos? cnt)
      (loop [stack nil t tree]
        (if-not (nil? t)
          (let [c (.compare comp k (.getKey t))]
            (cond
              (zero? c)  (AVLMapSeq. nil (conj stack t) ascending? -1 -1 -1)
              ascending? (if (neg? c)
                           (recur (conj stack t) (.getLeft t))
                           (recur stack          (.getRight t)))
              :else      (if (pos? c)
                           (recur (conj stack t) (.getRight t))
                           (recur stack          (.getLeft t)))))
          (if-not (nil? stack)
            (AVLMapSeq. nil stack ascending? -1 -1 -1))))))

  (entryKey [this entry]
    (key entry))

  (comparator [this]
    comp)

  clojure.lang.IEditableCollection
  (asTransient [this]
    (->AVLTransientMap
     (AtomicReference. (Thread/currentThread)) comp tree cnt))

  clojure.core.protocols/IKVReduce
  (kv-reduce [this f init]
    (if (nil? tree)
      init
      (avl-map-kv-reduce tree f init)))

  java.io.Serializable

  java.util.Map
  (get [this k]
    (.valAt this k))

  (clear [this]
    (throw-unsupported))

  (containsValue [this v]
    (.. this values (contains v)))

  (entrySet [this]
    (set (seq this)))

  (put [this k v]
    (throw-unsupported))

  (putAll [this m]
    (throw-unsupported))

  (remove [this k]
    (throw-unsupported))

  (size [this]
    cnt)

  (values [this]
    (vals this)))

(deftype AVLTransientMap [^AtomicReference edit
                          ^Comparator comp
                          ^:unsynchronized-mutable ^IAVLNode tree
                          ^:unsynchronized-mutable ^int cnt]
  clojure.lang.Counted
  (count [this]
    cnt)

  clojure.lang.ILookup
  (valAt [this k]
    (.valAt this k nil))

  (valAt [this k not-found]
    (let [n ^IAVLNode (lookup comp tree k)]
      (if-not (nil? n)
        (.getVal n)
        not-found)))

  clojure.lang.IFn
  (invoke [this k]
    (.valAt this k))

  (invoke [this k not-found]
    (.valAt this k not-found))

  (applyTo [this args]
    (let [n (RT/boundedLength args 2)]
      (case n
        0 (throw (clojure.lang.ArityException.
                  n (.. this (getClass) (getSimpleName))))
        1 (.invoke this (first args))
        2 (.invoke this (first args) (second args))
        3 (throw (clojure.lang.ArityException.
                  n (.. this (getClass) (getSimpleName)))))))

  clojure.lang.ITransientCollection
  (conj [this entry]
    (ensure-editable edit)
    (if (vector? entry)
      (assoc! this (nth entry 0) (nth entry 1))
      (reduce conj! this entry)))

  (persistent [this]
    (ensure-editable edit)
    (.set edit nil)
    (AVLMap. comp tree cnt nil -1 -1))

  clojure.lang.ITransientAssociative
  (assoc [this k v]
    (ensure-editable edit)
    (let [found?   (Box. false)
          new-tree (insert! edit comp tree k v found?)]
      (set! tree new-tree)
      (if-not (.-val found?)
        (set! cnt (unchecked-inc-int cnt)))
      this))

  clojure.lang.ITransientMap
  (without [this k]
    (ensure-editable edit)
    (let [found?   (Box. false)
          new-tree (delete! edit comp tree k found?)]
      (when (.-val found?)
        (set! tree new-tree)
        (set! cnt  (unchecked-dec-int cnt)))
      this)))

(declare ->AVLTransientSet)

(deftype AVLSet [^IPersistentMap _meta
                 ^AVLMap avl-map
                 ^:unsynchronized-mutable ^int _hash
                 ^:unsynchronized-mutable ^int _hasheq]
  Object
  (toString [this]
    (RT/printString this))

  (hashCode [this]
    (caching-hash this hash-iset _hash))

  clojure.lang.IHashEq
  (hasheq [this]
    (caching-hash this hasheq-iset _hasheq))

  clojure.lang.IMeta
  (meta [this]
    _meta)

  clojure.lang.IObj
  (withMeta [this meta]
    (AVLSet. meta avl-map _hash _hasheq))

  clojure.lang.Counted
  (count [this]
    (count avl-map))

  clojure.lang.Indexed
  (nth [this i]
    (.nth this i nil))

  (nth [this i not-found]
    (if-let [n (rank-lookup (.-tree avl-map) i)]
      (.getVal ^IAVLNode n)
      not-found))

  clojure.lang.IPersistentCollection
  (cons [this x]
    (AVLSet. _meta (assoc avl-map x x) -1 -1))

  (empty [this]
    (AVLSet. _meta (empty avl-map) 0 0))

  (equiv [this that]
    (and
     (set? that)
     (== (count this) (count that))
     (every? #(contains? this %) that)))

  clojure.lang.Seqable
  (seq [this]
    (keys avl-map))

  clojure.lang.Sorted
  (seq [this ascending?]
     (RT/keys (.seq avl-map ascending?)))

  (seqFrom [this k ascending?]
    (RT/keys (.seqFrom avl-map k ascending?)))

  (entryKey [this entry]
    entry)

  (comparator [this]
    (.comparator avl-map))

  clojure.lang.Reversible
  (rseq [this]
    (map key (rseq avl-map)))

  clojure.lang.ILookup
  (valAt [this v]
    (.valAt this v nil))

  (valAt [this v not-found]
    (let [n (.entryAt avl-map v)]
      (if-not (nil? n)
        (.getKey n)
        not-found)))

  clojure.lang.IPersistentSet
  (disjoin [this v]
    (AVLSet. _meta (dissoc avl-map v) -1 -1))

  (contains [this k]
    (contains? avl-map k))

  (get [this k]
    (.valAt this k nil))

  clojure.lang.IFn
  (invoke [this k]
    (.valAt this k))

  (applyTo [this args]
    (let [n (RT/boundedLength args 1)]
      (case n
        0 (throw (clojure.lang.ArityException.
                  n (.. this (getClass) (getSimpleName))))
        1 (.invoke this (first args))
        2 (throw (clojure.lang.ArityException.
                  n (.. this (getClass) (getSimpleName)))))))

  clojure.lang.IEditableCollection
  (asTransient [this]
    (->AVLTransientSet (.asTransient avl-map)))

  java.io.Serializable

  java.util.Set
  (add       [this o] (throw-unsupported))
  (remove    [this o] (throw-unsupported))
  (addAll    [this c] (throw-unsupported))
  (clear     [this]   (throw-unsupported))
  (retainAll [this c] (throw-unsupported))
  (removeAll [this c] (throw-unsupported))

  (containsAll [this c]
    (every? #(.contains this %) (iterator-seq (.iterator c))))

  (size [this]
    (count this))

  (isEmpty [this]
    (zero? (count this)))

  (iterator [this]
    (SeqIterator. (seq this)))

  (toArray [this]
    (RT/seqToArray (seq this)))

  (toArray [this a]
    (RT/seqToPassedArray (seq this) a)))

(deftype AVLTransientSet
    [^:unsynchronized-mutable ^AVLTransientMap transient-avl-map]
  clojure.lang.ITransientCollection
  (conj [this k]
    (set! transient-avl-map (.assoc transient-avl-map k k))
    this)

  (persistent [this]
    (AVLSet. nil (.persistent transient-avl-map) -1 -1))

  clojure.lang.ITransientSet
  (disjoin [this k]
    (set! transient-avl-map (.without transient-avl-map k))
    this)

  (contains [this k]
    (not (identical? this (.valAt transient-avl-map k this))))

  (get [this k]
    (.valAt transient-avl-map k))

  clojure.lang.IFn
  (invoke [this k]
    (.valAt transient-avl-map k))

  (invoke [this k not-found]
    (.valAt transient-avl-map k not-found))

  (applyTo [this args]
    (let [n (RT/boundedLength args 2)]
      (case n
        0 (throw (clojure.lang.ArityException.
                  n (.. this (getClass) (getSimpleName))))
        1 (.invoke this (first args))
        2 (.invoke this (first args) (second args))
        3 (throw (clojure.lang.ArityException.
                  n (.. this (getClass) (getSimpleName)))))))

  clojure.lang.Counted
  (count [this]
    (.count transient-avl-map)))

(def ^:private empty-map (AVLMap. RT/DEFAULT_COMPARATOR nil 0 nil 0 0))

(def ^:private empty-set (AVLSet. nil empty-map 0 0))

(doseq [v [#'->AVLMapSeq
           #'->AVLNode
           #'->AVLMap
           #'->AVLSet
           #'->AVLTransientMap
           #'->AVLTransientSet]]
  (alter-meta! v assoc :private true))

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
  [^Comparator comparator & keyvals]
  (loop [in  (seq keyvals)
         out (AVLTransientMap.
              (AtomicReference. (Thread/currentThread)) comparator nil 0)]
    (if in
      (recur (nnext in) (assoc! out (first in) (second in)))
      (persistent! out))))

(defn sorted-set
  "Returns a new sorted set with supplied keys."
  [& keys]
  (persistent! (reduce conj! (transient empty-set) keys)))

(defn sorted-set-by
  "Returns a new sorted set with supplied keys, using the supplied comparator."
  [^Comparator comparator & keys]
  (persistent!
   (reduce conj!
           (AVLTransientSet. (transient (sorted-map-by comparator)))
           keys)))

(comment

  (defn debug [avl-map]
    (letfn [(step [node pre indent]
              (let [pad (apply str (repeat indent " "))]
                (when node
                  (when pre
                    (println (str pad "=== " pre)))
                  (print
                   (str pad "K: " (.getKey node)    "\n"
                        pad "V: " (.getVal node)    "\n"
                        pad "H: " (.getHeight node) "\n"))
                  (step (.getLeft node)  "L:" (+ indent 2))
                  (step (.getRight node) "R:" (+ indent 2)))))]
      (step (.-tree avl-map) nil 0)))

  )
