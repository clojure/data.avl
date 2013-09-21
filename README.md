# avl.clj

Persistent sorted maps and sets with support for transients and
logarithmic time rank queries (via `clojure.core/nth`), using AVL
trees as the underlying data structure.

## Usage

avl.clj supports Clojure and ClojureScript. It exports a single
namespace with four public functions, all of which are drop-in
replacements for `clojure.core` / `cljs.core` functions of the same
names:

    (require '[avl.clj :as avl])
    
    (doc avl/sorted-map)
    (doc avl/sorted-map-by)
    (doc avl/sorted-set)
    (doc avl/sorted-set-by)

The maps and sets returned by these functions behave like the core
Clojure variants, with two differences:

1. they have transient counterparts:

        (persistent! (assoc! (transient (avl/sorted-map) 0 0)))
        ;= {0 0}

   and use transients during construction:

        (apply avl/sorted-map (interleave (range 32) (range 32)))
        ;; ^- uses transients

2. they support logarithmic time rank queries via `clojure.core/nth`:

        (nth (avl/sorted-map 0 0 1 1 2 2) 1)
        ;= [1 1]
        (nth (avl/sorted-set 0 1 2) 1)
        ;= 1

## Releases and dependency information

[avl.clj releases are available from Clojars.](https://clojars.org/avl.clj)
Please follow the link to discover the current release number.

[Leiningen](http://leiningen.org/) dependency information:

    [avl.clj "${version}"]

[Maven](http://maven.apache.org/) dependency information:

    <dependency>
      <groupId>avl.clj</groupId>
      <artifactId>avl.clj</artifactId>
      <version>${version}</version>
    </dependency>

## Developer information

Please note that patches will only be accepted from developers who
have submitted the Clojure CA and would be happy with the code they
submit to avl.clj becoming part of the Clojure project.

## Clojure(Script) code reuse

avl.clj sorted maps and sets support the same basic functionality
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

Copyright © 2013 Michał Marczyk

Distributed under the Eclipse Public License, the same as Clojure.
