(ns de.otto.tesla.serving-with-httpkit
  (:require [com.stuartsierra.component :as c]
            [org.httpkit.server :as httpkit]
            [clojure.tools.logging :as log]
            [de.otto.tesla.stateful.handler :as handler]))

(def default-port 3000)

(defrecord HttpkitServer [config handler]
  c/Lifecycle
  (start [self]
    (log/info "-> starting httpkit")
    (let [port (get-in config [:config :server-port] default-port)
          bind (get-in config [:config :server-bind] "0.0.0.0")
          handlers (handler/handler handler)
          _ (println "Starting httpkit with port " port " and bind " bind ".")
          server (httpkit/run-server handlers
                                     {:port (Integer. port)
                                      :ip   bind})]
      (assoc self :httpkit server)))

  (stop [self]
    (log/info "<- stopping httpkit")
    (when-let [server (:httpkit self)]
      (server))
    self))

(defn new-server [] (map->HttpkitServer {}))

(defn add-server [base-system & server-dependencies]
  (assoc base-system :server (c/using (new-server)
                                      (into [:config :handler] server-dependencies))))

