(ns de.otto.tesla.serving-with-httpkit-test
  (:require [clojure.test :refer :all]
            [de.otto.tesla.serving-with-httpkit :as with-httpkit]
            [org.httpkit.server :as httpkit]
            [de.otto.tesla.httpkit-metrics :as metr]
            [de.otto.tesla.system :as system]
            [com.stuartsierra.component :as component]
            [com.stuartsierra.component :as c]))

(deftest should-start-up-jetty
  (let [was-started (atom false)]
    (with-redefs [with-httpkit/run-server (fn [_ _]
                                            (reset! was-started true)
                                            nil)
                  metr/initialize-gauges (constantly nil)]
      (let [started (system/start (with-httpkit/add-server (system/base-system {})))
            _ (system/stop started)]
        (is (= true @was-started))))))

(deftest parser-string-config
  (testing "should parse the config item and return the value"
    (is (= (with-httpkit/get-config {} :server-thread "0.0.0.0") "0.0.0.0")))
  (testing "should parse the config item and return the value"
    (is (= (with-httpkit/get-config {:config {:server-thread "A"}} :server-thread "0.0.0.0") "A"))))

(deftest parser-int-config
  (testing "should parse the config item and return the value"
    (is (= (with-httpkit/parser-integer-config {} :server-port 3000) 3000)))
  (testing "should parse the config item and return the value"
    (is (= (with-httpkit/parser-integer-config {:config {:server-port "A"}} :server-port 3000) 3000)))
  (testing "should parse the config item and return the value"
    (is (= (with-httpkit/parser-integer-config {:config {:server-port 4000}} :server-port 3000) 4000))))


(deftest server-config
  (testing "should build up server config parameter with default value for empty config"
    (is (= {:port               3000
            :ip                 "0.0.0.0"
            :thread             4
            :queue-size         20000
            :max-body           8388608
            :max-line           4096
            :max-ws             4194304
            :worker-name-prefix "tesla-httpkit-worker-"
            :proxy-protocol :disable}
           (with-httpkit/server-config {}))))
  (testing "override the default value from the config"
    (is (= {:port               10000
            :ip                 "1.1.1.1"
            :thread             9
            :queue-size         10
            :max-body           1000
            :max-line           8192
            :max-ws             4194304
            :worker-name-prefix "tesla-httpkit-worker-"
            :proxy-protocol :enable}
           (with-httpkit/server-config {:config {:server-port       "10000"
                                                 :server-bind       "1.1.1.1"
                                                 :server-thread     "9"
                                                 :server-queue-size "10"
                                                 :server-max-body   "1000"
                                                 :server-max-line   "8192"
                                                 :proxy-protocol :enable}})))))

(deftest server-dependencies
  (with-redefs [httpkit/run-server (fn [_ _] nil)]
    (testing "it starts up the server with no extra dependencies"
      (let [system-with-server (with-httpkit/add-server (system/base-system {}))
            started (system/start system-with-server)
            _ (system/stop started)]
        (is (= #{:config :handler :httpkit} (into #{} (keys (:server started)))))))

    (testing "it starts up the server with extra dependencies"
      (let [with-page (assoc (system/base-system {}) :dummy-page (Object.))]
        (let [system-with-server (with-httpkit/add-server with-page :dummy-page)
              started (system/start system-with-server)
              _ (system/stop started)]
          (is (= #{:config :handler :httpkit :dummy-page} (into #{} (keys (:server started))))))))))

(deftest should-apply-configured-timeout
  (testing "should stop the server with configured timeout"
    (let [record (atom [])
          server (-> (with-httpkit/new-server)
                     (assoc :config {:config {:httpkit-timeout 12345}})
                     (assoc :httpkit :dummy-server-to-stop))]
      (with-redefs [with-httpkit/stop-server (fn [_ timeout]
                                               (reset! record timeout))]
        (component/stop server)
        (is (= @record 12345)))))

  (testing "should stop the server with default timeout"
    (let [record (atom [])
          server (-> (with-httpkit/new-server)
                     (assoc :httpkit :dummy-server-to-stop))]
      (with-redefs [with-httpkit/stop-server (fn [_ timeout]
                                               (reset! record timeout))]
        (component/stop server)
        (is (= @record 100))))))

(deftest httpkit-metrics
  (testing "should initialize gauges on startup of system"
    (let [initialize-gauges-callcount (atom 0)]
      (with-redefs [metr/initialize-gauges (fn [_] (swap! initialize-gauges-callcount inc))]
        (let [config {:server-port 3001}
              system (assoc (system/base-system config)
                       :httpkit (c/using (with-httpkit/new-server) [:config :handler]))
              started (c/start-system system)]
          (try
            (is (= 1 @initialize-gauges-callcount))
            (finally
              (c/stop-system started)))))))

  (testing "should not initialize gauges on startup of system"
    (let [initialize-gauges-callcount (atom 0)]
      (with-redefs [metr/initialize-gauges (fn [_] (swap! initialize-gauges-callcount inc))]
        (let [config {:server-port 3001
                      :httpkit-metrics? false}
              system (assoc (system/base-system config)
                       :httpkit (c/using (with-httpkit/new-server) [:config :handler]))
              started (c/start-system system)]
          (try
            (is (= 0 @initialize-gauges-callcount))
            (finally
              (c/stop-system started))))))))
