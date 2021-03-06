# nano

> "Me think, why waste time say lot word, when few word do trick."
> -- Kevin Malone 

## Description

Nano is a the first ever *nanoframework* for managing your system's state in Clojure. It has all the benefits of a microframework, but it's even lighter weight. In fact, it's so lightweight it's not even on Clojars!

Managing the start and stop of your system in a REPL session should be [simple](https://www.youtube.com/watch?v=cidchWg74Y4) and [decoupled](https://www.youtube.com/watch?v=LEZv-kQUSi4) from your code. Nano aims to be dead simple and get the job done. Nano gets out of your way and doesn't require usage throughout your project. Instead, bring in the pieces of your system you need into a single namespace and use nano's `start` and `restart` functions (example usage below).

## Why use nano?

* Nano uses a superset of EDN called *Clojure*. It has all the benefits of a data-driven architecture, with the power of a programming language to automate and extend as needed with tools you already know
* Leverages static analysis tools of Clojure, where symbols refer to functions with docstrings as well as symbol autocompletion
* Nano is extensible, declarative, data driven nanoframework, that you can easily configure to your needs. Expressive, data, with the benefits of programability
* Nano lets you seperate your concerns. Declare your `:start` and `:stop` functions as Clojure `lists`, and assemble your system as a Clojure `vector`
  * Orchestration of these functions is taken care of by nano
* Dependency trees are *explicitly* declared and easy to reason about
  * Nano assumes a pre-linearized system of dependencies
* Nano only relies on only one framework: Clojure. Clojure provides a low latency, expressive, compositional DSL to describe systems
* Nano lets you retain full control of your system's lifecycle
* Decouple your configuration from your implementation as needed using Clojure's maps and functions
* Easily leverage existing libraries by using *adapter* functions (example below)


## Principles

Nano aims to:

* stay out of your way
* avoid difficult to reason about webs of dependencies
* be easy to replace, with minimal API surface 
* use simple Clojure tools and avoid reinventing tools provided by the language
* keep your stack lean and agile

## Installation & Updates

The implementation of nano is just a few lines of Clojure. Grab the code from `src/core.cljc`

On macOS: ⌘-c ⌘-v

Windows: Ctrl-c Ctrl-v

## Usage

Declare your initialization and teardown functions as regular Clojure functions in whatever way you find most convenient. Note: these start and stop functions taken in the state of a system as a map and return a state.

``` clojure
(defn start-server [state]
  (let [handler (::handler state)
        config {:port (::port state) :join? false}
        server (ring/run-jetty handler config)])
  (assoc state ::server server))

(defn stop-server [state]
  (when-let [server (::server state)]
    (.stop server))
  (dissoc state ::server))

(defn handler [req]
  {:status 200
   :body "Hello, world!"})
```

Create your system map inside of a Clojure atom. Note, you can easily inject dependencies in the root of the map using namespaced keys, and use those dependencies where needed.
```clojure
(defonce system
  (atom
   {::handler handler
    ::port 3000
    :components
    [{:start #'start-server
      :stop  #'stop-server}]}))
```

## Environments and Profiles

Easily conditionalize your systems startup for development and production using Clojure's [*dynamic vars*](https://clojure.org/reference/vars).

``` clojure
(ns your.system)
(def ^:dynamic *env* ::dev)

(defonce system
  (atom
   {::handler handler
    ::port (case *env* ::dev 3000 ::prod 8080)
    :components
    [{:start #'start-server
      :stop  #'stop-server}]}))

(ns main
  (:require [your.system :as s]))
(defn -main []
  (binding [s/*env* ::s/prod]
    (swap! s/system nano.core/start)))
```

Too verbose? Use the tools you already know to keep your code DRY, while allowing others to easily reason about, and inspect your code and documentation.

``` clojure
(defn profile
  "conditionalize for ::dev and ::prod profiles/environments
  usage: (profile {::dev 3000 ::prod 8080})"
  [m]
  (get m *env*))
```


Conveniently start and restart your system in a familiar way while developing. Add these to your `user` or `dev` namespace.
``` clojure
(defn go    [] (swap! system nano.core/start))
(defn reset [] (swap! system nano.core/restart))
```

## Modules & Extensibility

Nano supports a rich ecosystem of 'modules' through existing Clojure libraries. For instance, you can start a server with `ring` or start a database connection with `jdbc`.

1. Require an existing Clojure library using your existing tools
2. Use `adapter` functions to interoperate with nano
3. Reference these adapters in your system map

## FAQ

* "What if I need multiple systems?"
  * Use multiple atoms to create and manage systems
* "How do I inject dependencies?"
  * To inject dependencies in your system, parameterize your `:start` and `:stop` functions
