# data.avl

Persistent sorted maps and sets with support for transients and
logarithmic time rank queries (via `clojure.core/nth` and
`clojure.data.avl/rank-of`), using AVL trees as the underlying data
structure.

## Usage

data.avl supports both Clojure and ClojureScript. It exports a single
namespace with five public functions, four of which are drop-in
replacements for `clojure.core` / `cljs.core` functions of the same
names:

    (require '[clojure.data.avl :as avl])
    
    (doc avl/sorted-map)
    (doc avl/sorted-map-by)
    (doc avl/sorted-set)
    (doc avl/sorted-set-by)

The fifth function finds the rank of the given element in an AVL map
or set (-1 if not found; will return primitive `long`s where
possible):

    (doc avl/rank-of)

The maps and sets returned by these functions behave like the core
Clojure variants, with the following differences:

1. They have transient counterparts:

        (persistent! (assoc! (transient (avl/sorted-map) 0 0)))
        ;= {0 0}

   and use transients during construction:

        (apply avl/sorted-map (interleave (range 32) (range 32)))
        ;; ^- uses transients

2. They support logarithmic time rank queries via `clojure.core/nth`
   and `clojure.data.avl/rank-of`:

        (nth (avl/sorted-map 0 0 1 1 2 2) 1)
        ;= [1 1]
        (nth (avl/sorted-set 0 1 2) 1)
        ;= 1
        
        (avl/rank-of (avl/sorted-map-by > 0 0 1 1 2 2) 0)
        2
        (avl/rank-of (avl/sorted-set-by > 0 1 2) 0)
        2

3. They are typically noticeably faster during lookups and somewhat
   slower during non-transient "updates" (`assoc`, `dissoc`) than the
   built-in sorted collections. Note that batch "updates" using
   transients typically perform better than batch "updates" on the
   non-transient-enabled built-ins.

4. They add some memory overhead -- a reference and two `int`s per
   key. The additional node fields are used to support transients (one
   reference field per key), rank queries (one `int`) and the
   rebalancing algorithm itself (the final `int`).

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

Copyright © 2013 Michał Marczyk, Rich Hickey

Distributed under the Eclipse Public License, the same as Clojure.
