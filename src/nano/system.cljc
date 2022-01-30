(ns nano.system
  "Functions to start and stop system components.
  System looks like the following shape. start/stop functions
  take the whole derefed system map, and should return the new system map.
  (atom {:other      :data
         :components [{:name :component/name
                       :start #'start-fn
                       :stop  #'stop-fn}]})")

(defn- lifecycle-sort
  [lifecycle fns]
  (case lifecycle
    :start fns
    :stop  (reverse fns)))

(defn run-lifecycle [lifecycle state]
  (if (= lifecycle (::lifecycle state))
    state
    (reduce (fn run-component
              [state component-map]
              (try
                (when-let [f (get component-map lifecycle)]
                  (f state))
                (catch Exception _e
                  (let [data {:state     state
                              :component component-map}]
                    (throw (ex-info (str "Failed to run component\n"
                                         data)
                                    data))))))
            (assoc state ::lifecycle lifecycle)
            (->> state
                 :components
                 (lifecycle-sort lifecycle)))))

(defn start
  "Run :start functions in :components in order"
  [state-atom]
  (swap! state-atom
         (fn start-components [state]
           (run-lifecycle :start state))))

(defn stop
  "Run :stop functions in reverse order of :components"
  [state-atom]
  (swap! state-atom
         (fn stop-components [state]
           (run-lifecycle :stop state))))

(defn restart
  "Run stop then start"
  [state-atom]
  (stop state-atom)
  (start state-atom))
