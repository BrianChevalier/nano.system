# nano.system
Orchestrate your stateful Clojure system

## Installation
`nano.system` can be installed by using git deps. Add this to your `deps.edn` 
``` clojure
io.github.BrianChevalier/nano.system {:git/tag "v1.0.0" :git/sha ""}
```

## Usage

Define your components with regular functions and maps. `:start/:stop` should be functions that take the current state of the system and return a new state map. In your namespace, define your components.

``` clojure
(ns my.server.namespace
  (:require [ring.adapter.jetty :as jetty]))

(defn start-handler [system]
  (assoc system 
         ::handler 
         (fn [req] 
           {:status 200
            :body   "Hello, world!"})))

(def handler-component
  {:name ::handler
   :start #'start-handler})

(defn start-server [state]
  (let [handler (::handler state)
        config {:port (::port state) :join? false}
        server (jetty/run-jetty handler config)])
  (assoc state ::server server))

(defn stop-server [state]
  (when-let [server (::server state)]
    (.stop server))
  (dissoc state ::server))

(def server-component
  {:name  ::server
   :start #'start-server
   :stop  #'stop-server})
```

Now in your main namespace
``` clojure
(ns my.main.namespace
  (:require [my.server.namespace :as server]
            [nano.system]))

(defonce state 
  (atom {:components [server/handler-component
                      server/server-component]}))

```

Finally, we can use nano to start and stop the system.
``` clojure
(nano.system/start   state)
(nano.system/stop    state)
(nano.system/restart state)
```
