# nano

> "Me think, why waste time say lot word, when few word do trick.""
> -- Kevin Malone 

## Description

Nano is a the first ever *nanoframework* for managing your system's state in Clojure. It has all the benefits of a microframework, but it's even lighter weight. In fact, it's so lightweight it's not even on Clojars!

## Benefits

* Nano uses a superset of EDN called *Clojure*. It has all the benefits of a data-driven architecture, with the power of a programming language to automate and extend as needed
* Leverages static analysis tools of Clojure, where symbols refer to functions with docstrings and symbol autocompletion
* Nano is extensible, declarative, data driven nano-framework, that you can easily configure to your needs. Expressive, data, with the benefits of programability
* Nano lets your seperate your concerns. Declare your `start` and `stop` functions as Clojure lists, and assemble your system as a Clojure vector. This decouples your configuration from your implementation.
* Dependency trees are *explicitly* declared and easy to reason about
  * Nano assumes a pre-linearized system of dependencies
* Nano only relies on only one framework: Clojure. Clojure provides a low latency, expressive, compositional DSL to describe systems
* Nano lets you retain full control of your system's lifecycle
* Easily leverage existing libraries while using Nano to orchestrate your system

## Installation & Updates

On macOS: ⌘-c ⌘-v

Windows: Ctrl-c Ctrl-v

## Usage

Declare your initialization and teardown functions as regular Clojure functions. 

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

Create your system map inside of a Clojure atom.
```clojure
(defonce system
  (atom
   {::handler handler
    ::port 3000
    :components
    [{:start #'start-server
      :stop  #'stop-server}]}))
```

Conveniently start and restart your system
``` clojure
(defn go    [] (swap! system start))
(defn reset [] (swap! system restart))
```

## Modules

Nano supports a rich ecosystem of modules


## FAQ

* "What if I need multiple systems?" Use multiple atoms to create and manage systems.
* "How do I inject dependencies?" To inject dependencies in your system, parameterize your `:start` and `:stop` functions 
