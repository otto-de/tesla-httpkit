(ns de.otto.tesla.serving-with-httpkit-test
  (:require [clojure.test :refer :all]
            [de.otto.tesla.serving-with-httpkit :as with-httpkit]
            [org.httpkit.server :as httpkit]
            [de.otto.tesla.system :as system]))

(deftest should-start-up-jetty
  (let [was-started (atom false)]
    (with-redefs [httpkit/run-server (fn [_ _]
                                    (reset! was-started true)
                                    nil)]
      (let [started (system/start (with-httpkit/add-server (system/base-system {})))
            _ (system/stop started)]
        (is (= true @was-started))))))

(deftest parser-string-config
  (testing "should parse the config item and return the value"
    (is (=(with-httpkit/parser-string-config {} :server-thread "0.0.0.0") "0.0.0.0")))
  (testing "should parse the config item and return the value"
    (is (=(with-httpkit/parser-string-config {:config {:server-thread "A"}} :server-thread "0.0.0.0") "A"))))

(deftest parser-int-config
  (testing "should parse the config item and return the value"
    (is (=(with-httpkit/parser-integer-config {} :server-port 3000) 3000)))
  (testing "should parse the config item and return the value"
    (is (=(with-httpkit/parser-integer-config {:config {:server-port "A"}} :server-port 3000) 3000)))
  (testing "should parse the config item and return the value"
    (is (=(with-httpkit/parser-integer-config {:config {:server-port 4000}} :server-port 3000) 4000))))



(deftest server-config
  (testing "should build up server config parameter with default value for empty config"
  (is (=(with-httpkit/server-config {}) {:port 3000 :ip "0.0.0.0" :thread 4 :queue-size 20000
                                  :max-body 8388608 :max-line 4096})))
  (testing "override the default value from the config"
  (is (=(with-httpkit/server-config {:config {:server-port "10000" :server-bind "1.1.1.1" :server-thread "9" :server-queue-size "10"
                                              :server-max-body "1000" :server-max-line "8192"}})
        {:port 10000 :ip "1.1.1.1" :thread 9 :queue-size 10
                                  :max-body 1000 :max-line 8192}))))

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