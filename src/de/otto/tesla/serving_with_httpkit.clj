(ns de.otto.tesla.serving-with-httpkit
  (:require [com.stuartsierra.component :as c]
            [org.httpkit.server :as httpkit]
            [clojure.tools.logging :as log]
            [de.otto.tesla.stateful.handler :as handler]))

(def default-port 3000)

(defn parser-string-config[config element default-value]
(get-in config [:config element] default-value))

(defn parser-integer-config[config element default-value]
  (try
    (Integer. (parser-string-config config element default-value))
    (catch NumberFormatException e default-value)))

(defn server-config [config]
    {:port       (parser-integer-config config :server-port default-port)
     :ip         (parser-string-config config :server-bind "0.0.0.0")
     :thread     (parser-integer-config config :server-thread 4)
     :queue-size (parser-integer-config config :server-queue-size 20000)
     :max-body   (parser-integer-config config :server-max-body 8388608)
     :max-line    (parser-integer-config config :server-max-line 4096)
     })

(defrecord HttpkitServer [config handler]
  c/Lifecycle
  (start [self]
    (log/info "-> starting httpkit")
    (let [server-config (server-config config)
          handlers (handler/handler handler)
          _ (log/info "Starting httpkit with port " (server-config :port) " and bind " (server-config :bind) ".")
          server (httpkit/run-server handlers server-config)]
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

