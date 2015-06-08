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