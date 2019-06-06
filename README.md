# data.avl

Persistent sorted maps and sets with support for the full clojure.core
sorted collections API (in particular `clojure.core/(r)?(sub)?seq`),
transients and additional logarithmic time operations: rank queries
(via `clojure.core/nth` and `clojure.data.avl/rank-of`), "nearest key"
lookups, splits by index or key and subsets/submaps.

Persistent AVL trees are used as the underlying data structure.

## Synopsis

data.avl supports both Clojure and ClojureScript. It exports a single
namespace with nine public functions, four of which are constructor
functions which can be used as drop-in replacements for `clojure.core`
/ `cljs.core` functions of the same names, while the remaining five
expose data.avl-specific functionality:

    (require '[clojure.data.avl :as avl])

    ;; drop-in replacements for clojure.core counterparts
    (doc avl/sorted-map)
    (doc avl/sorted-map-by)
    (doc avl/sorted-set)
    (doc avl/sorted-set-by)

    ;; find rank of element as primitive long, -1 if not found
    (doc avl/rank-of)

    ;; find element closest to the given key and </<=/>=/> according
    ;; to coll's comparator
    (doc avl/nearest)

    ;; split the given collection at the given key returning
    ;; [left entry? right]
    (doc avl/split-key)

    ;; split the given collection at the given index; similar to
    ;; clojure.core/split-at, but operates on and returns data.avl
    ;; collections
    (doc avl/split-at)

    ;; return subset/submap of the given collection; accepts arguments
    ;; reminiscent of clojure.core/{subseq,rsubseq}
    (doc avl/subrange)

All data.avl collection-returning public functions return first-class
collections (see below for a discussion).

## Description

data.avl maps and sets behave like the core Clojure variants, with the
following differences:

1. They have transient counterparts:

        (persistent! (assoc! (transient (avl/sorted-map)) 0 0))
        ;= {0 0}

   and use transients during construction:

        (apply avl/sorted-map (interleave (range 32) (range 32)))
        ;; ^- uses transients

2. They are typically noticeably faster during lookups and somewhat
   slower during non-transient "updates" (`assoc`, `dissoc`) than the
   built-in sorted collections. Note that batch "updates" using
   transients typically perform better than batch "updates" on the
   non-transient-enabled built-ins.

3. They add some memory overhead -- a reference and two `int`s per
   key. The additional node fields are used to support transients (one
   reference field per key), rank queries (one `int`) and the
   rebalancing algorithm itself (the final `int`).

Additionally, data.avl collections support several features that the
built-ins do not:

1. Logarithmic time rank queries via `clojure.core/nth` and
   `clojure.data.avl/rank-of`:

        (nth (avl/sorted-map 0 0 1 1 2 2) 1)
        ;= [1 1]
        (nth (avl/sorted-set 0 1 2) 1)
        ;= 1
        
        (avl/rank-of (avl/sorted-map-by > 0 0 1 1 2 2) 0)
        2
        (avl/rank-of (avl/sorted-set-by > 0 1 2) 0)
        2

2. Logarithmic time lookups of "nearest entries" via
   `clojure.data.avl/nearest`:

        (avl/nearest (avl/sorted-set 0 1 2) < 1)
        ;= 0
        (avl/nearest (avl/sorted-set 0 1 2) <= 1) ; or >=
        ;= 1
        (avl/nearest (avl/sorted-set 0 1 2) > 1)
        ;= 2
        (avl/nearest (avl/sorted-set 0 1 2) > 2)
        ;= nil

3. Logarithmic time splitting by key:

        (avl/split-key 3 (avl/sorted-set 0 1 2 3 4 5))
        ;= [#{0 1 2} 3 #{4 5 6}]
        (avl/split-key 1 (avl/sorted-map 0 0 1 1 2 2))
        ;= [{0 0} [1 1] {2 2}]
        (avl/split-key 2 (avl/sorted-set 0 1 3 4))
        ;= [#{0 1} nil #{3 4}]

   The middle element of the returned vector is the entry at the given
   key for maps, stored copy of the key for sets and `nil` if the key
   is absent from the collection.

   The remaining two elements are the "left" and "right"
   subcollections of the original collection argument when split with
   the given key, comprising, respectively, the keys preceding and
   succeeding the given key in the order determined by the input
   collection's comparator.
   
4. Logarithmic time splitting by index:

        (avl/split-at 2 (avl/sorted-set 0 1 2 3 4 5))
        ;= [#{0 1} #{2 3 4 5}]

5. Logarithmic time slicing:

        (avl/subrange (avl/sorted-set 0 1 2 3 4 5) > 1)
        ;= #{2 3 4 5}
        (avl/subrange (avl/sorted-set 0 1 2 3 4 5) <= 4)
        ;= #{0 1 2 3 4}
        (avl/subrange (avl/sorted-set 0 1 2 3 4 5) >= 2 < 5)
        ;= #{2 3 4}

6. `clojure.data.avl/split-key`, `clojure.data.avl/split-at` and
   `clojure.data.avl/subrange` all return first-class data.avl
   collections, completely independent of the originals. In
   particular, they do not prevent the originals from being garbage
   collected and they support insertion of arbitrary keys, including
   outside original `subrange` bounds.

## Documentation

* [API Reference](https://clojure.github.io/data.avl/) (Autogenerated)

## Releases and dependency information

data.avl requires Clojure >= 1.5.0. The ClojureScript version is
regularly tested against the most recent ClojureScript release.

data.avl releases are available from Maven Central. Development
snapshots are available from the Sonatype OSS repository.

 * [Released versions](http://search.maven.org/#search%7Cga%7C1%7Corg.clojure%2Fdata.avl)

 * [Development snapshots](https://oss.sonatype.org/index.html#nexus-search;gav~org.clojure~data.avl~~~)

Follow the first link above to discover the current release number.

[Leiningen](http://leiningen.org/) dependency information:

    [org.clojure/data.avl "${version}"]

[Maven](http://maven.apache.org/) dependency information:

    <dependency>
      <groupId>org.clojure</groupId>
      <artifactId>data.avl</artifactId>
      <version>${version}</version>
    </dependency>

[Gradle](http://www.gradle.org/) dependency information:

    compile "org.clojure:data.avl:${version}"

## Developer information

data.avl is being developed as a Clojure Contrib project, see the
[What is Clojure Contrib](http://dev.clojure.org/pages/viewpage.action?pageId=5767464)
page for details. Patches will only be accepted from developers who
have signed the Clojure Contributor Agreement.

* [GitHub project](https://github.com/clojure/data.avl)

* [Bug Tracker](http://dev.clojure.org/jira/browse/DAVL)

* [Continuous Integration](http://build.clojure.org/job/data.avl/)

* [Compatibility Test Matrix](http://build.clojure.org/job/data.avl-test-matrix/)

## Clojure(Script) code reuse

data.avl sorted maps and sets support the same basic functionality
regular Clojure's sorted maps and sets do (with the additions listed
above). Some of the code supporting various Clojure(Script) interfaces
and protocols is adapted from the ClojureScript implementations of the
red-black-tree-based sorted collections, which themselves are ports of
Clojure's implementations written in Java. The Clojure(Script) source
files containing the relevant code carry the following copyright
notice:

    Copyright (c) Rich Hickey. All rights reserved.
    The use and distribution terms for this software are covered by the
    Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
    which can be found in the file epl-v10.html at the root of this distribution.
    By using this software in any fashion, you are agreeing to be bound by
      the terms of this license.
    You must not remove this notice, or any other, from this software.

## Licence

Copyright © 2013-2016 Michał Marczyk, Rich Hickey and contributors

Distributed under the Eclipse Public License, the same as Clojure.
