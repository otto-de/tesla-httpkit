(ns de.otto.tesla.serving-with-httpkit
  (:require [com.stuartsierra.component :as c]
            [clojure.tools.logging :as log]
            [de.otto.tesla.httpkit-metrics :as htmetr]
            [de.otto.tesla.stateful.handler :as handler])
  (:import (org.httpkit.server TeslaRingHandler TeslaHttpServer)))

(defn parser-string-config [config element default-value]
  (get-in config [:config element] default-value))

(defn parser-integer-config [config element default-value]
  (try
    (Integer. (parser-string-config config element default-value))
    (catch NumberFormatException e default-value)))

(defn server-config [config]
  {:port               (parser-integer-config config :server-port 3000)
   :ip                 (parser-string-config config :server-bind "0.0.0.0")
   :thread             (parser-integer-config config :server-thread 4)
   :queue-size         (parser-integer-config config :server-queue-size 20000)
   :max-body           (parser-integer-config config :server-max-body 8388608)
   :max-line           (parser-integer-config config :server-max-line 4096)
   :max-ws             (parser-integer-config config :server-max-ws 4194304)
   :worker-name-prefix (parser-string-config config :worker-name-prefix "tesla-httpkit-worker-")})

(defn run-server [handler {:keys [port ip thread queue-size max-body max-line max-ws worker-name-prefix]}]
  (log/info "Starting httpkit with port " port " and bind " ip ".")
  (let [handler (TeslaRingHandler. thread handler worker-name-prefix queue-size)
        server (TeslaHttpServer. ip port handler max-body max-line max-ws)]
    (.start server)
    server))

(defn stop-server [server timeout]
  (log/info "<- stopping httpkit with timeout:" timeout "ms")
  (.stop server timeout))

(defrecord HttpkitServer [config handler]
  c/Lifecycle
  (start [self]
    (log/info "-> starting httpkit")
    (let [httpkit-metrics? (get-in config [:config :httpkit-metrics?] true)
          server-config (server-config config)
          handlers (handler/handler handler)
          server (run-server handlers server-config)]
      (when httpkit-metrics?
        (htmetr/initialize-gauges server))
      (assoc self :httpkit server)))

  (stop [self]
    (log/info "<- stopping httpkit")
    (when-let [server (:httpkit self)]
      (let [timeout (get-in config [:config :httpkit-timeout] 100)]
        (stop-server server timeout)))
    self))

(defn new-server [] (map->HttpkitServer {}))

(defn add-server [base-system & server-dependencies]
  (assoc base-system :server (c/using (new-server)
                                      (into [:config :handler] server-dependencies))))

