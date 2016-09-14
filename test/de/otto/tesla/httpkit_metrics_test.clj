(ns de.otto.tesla.httpkit-metrics-test
  (:require [clojure.test :refer :all]
            [de.otto.tesla.httpkit-metrics :as metr]))

(defn iinc [v]
  (inc (or v 0)))

(deftest httpkit-metrics
  (testing "should initialize gauges on startup of system"
    (let [registered-gauges (atom {})]
      (with-redefs [metr/internals (constantly {})
                    metrics.gauges/gauge-fn (fn [title _]
                                              (swap! registered-gauges update-in title iinc))]
        (is (= {"httpkit" {"executor" {"active-threads"  1
                                       "completed-tasks" 1}
                           "queue"    {"size" 1}}}
               (metr/initialize-gauges :dummy-server)))))))

