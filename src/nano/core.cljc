(ns nano.core)

(defn start [system]
  (if (::running? system)
    system
   (reduce
    (fn start-system [system start] (start system))
    (assoc system ::running? true)
    (map :start (:components system)))))

(defn stop [system]
  (if-not (::running? system)
    system
    (reduce
     (fn stop-system [system stop] (stop system))
     (assoc system ::running? false)
     (keep :stop (reverse (:components system))))))

(defn restart [system] (-> system stop start))
