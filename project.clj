(defproject de.otto/tesla-jetty "0.1.0"
            :description "basic microservice."
            :url "https://github.com/otto-de/tesla-jetty"
            :license {:name "Apache License 2.0"
                      :url  "http://www.apache.org/license/LICENSE-2.0.html"}
            :scm {:name "git"
                  :url  "https://github.com/otto-de/tesla-jetty"}

            :dependencies [[org.clojure/clojure "1.6.0"]
                           [ring/ring-jetty-adapter "1.3.2"]]

            :exclusions [org.clojure/clojure
                         org.slf4j/slf4j-nop
                         org.slf4j/slf4j-log4j12
                         log4j
                         commons-logging/commons-logging]

            :profiles {:provided {:dependencies [[de.otto/tesla-microservice "0.1.14"]
                                                 [com.stuartsierra/component "0.2.3"]]}
                       :dev      {:dependencies [[javax.servlet/servlet-api "2.5"]
                                                 [org.slf4j/slf4j-api "1.7.12"]
                                                 [ch.qos.logback/logback-core "1.1.3"]
                                                 [ch.qos.logback/logback-classic "1.1.3"]
                                                 [ring-mock "0.1.5"]]
                                  :plugins      [[lein-ancient "0.5.4"]]}}
            :test-paths ["test" "test-resources"])
