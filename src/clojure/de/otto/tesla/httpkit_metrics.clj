(ns de.otto.tesla.httpkit-metrics
  (:require
    [metrics.gauges :as mg])
  (:import
    (org.httpkit.server TeslaHttpServer)
    (java.util.concurrent ThreadPoolExecutor BlockingQueue)))

(defn- internals [^TeslaHttpServer server]
  (let [handler (.getHandler server)
        executor (.getExecutor handler)
        queue (.getQueue executor)]
    {:handler  handler
     :executor ^ThreadPoolExecutor executor
     :queue    ^BlockingQueue queue}))

(defn initialize-gauges [^TeslaHttpServer server]
  (let [{:keys [executor queue]} (internals server)]
    (mg/gauge-fn
      ["httpkit" "executor" "active-threads"]
      (fn []
        (.getActiveCount executor)))

    (mg/gauge-fn
      ["httpkit" "queue" "size"]
      (fn []
        (.size queue)))

    (mg/gauge-fn
      ["httpkit" "executor" "completed-tasks"]
      (fn []
        (.getCompletedTaskCount executor)))))
