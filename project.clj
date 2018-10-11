(defproject de.otto/tesla-httpkit "1.0.2"
            :description "httpkit addon for tesla-microservice."
            :url "https://github.com/otto-de/tesla-httpkit"
            :license {:name "Apache License 2.0"
                      :url  "http://www.apache.org/license/LICENSE-2.0.html"}
            :scm {:name "git"
                  :url  "https://github.com/otto-de/tesla-httpkit"}

            :dependencies [[org.clojure/clojure "1.8.0"]
                           [http-kit "2.3.0"]
                           [metrics-clojure-jvm "2.8.0"]]

            :source-paths ["src/clojure"]
            :java-source-paths ["src/java"]
            :lein-release {:deploy-via :clojars}
            :exclusions [org.clojure/clojure
                         org.slf4j/slf4j-nop
                         org.slf4j/slf4j-log4j12
                         log4j
                         commons-logging/commons-logging]
            :profiles {:provided {:dependencies [[de.otto/tesla-microservice "0.7.1"]
                                                 [com.stuartsierra/component "0.3.2"]]}
                       :dev      {:dependencies [[javax.servlet/servlet-api "2.5"]
                                                 [org.slf4j/slf4j-api "1.7.22"]
                                                 [ch.qos.logback/logback-core "1.1.9"]
                                                 [ch.qos.logback/logback-classic "1.1.9"]
                                                 [ring-mock "0.1.5"]]
                                  :plugins      [[lein-ancient "0.6.10"][lein-release/lein-release "1.0.9"]]}}
            :test-paths ["test" "test-resources"])
