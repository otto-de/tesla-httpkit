#tesla-jetty

This library provides a component, that adds an embedded httpkit server to [tesla-microservice](https://github.com/otto-de/tesla-microservice). 

`[de.otto/tesla-httpkit "0.1.3"]`

[![Build Status](https://travis-ci.org/otto-de/tesla-httpkit.svg)](https://travis-ci.org/otto-de/tesla-httpkit)
[![Dependencies Status](http://jarkeeper.com/otto-de/tesla-httpkit/status.svg)](http://jarkeeper.com/otto-de/tesla-httpkit)

## Configuration

The config ```:server-port``` will be used as port. Default is ```8080```. 
The config ```:server-binding``` will be used as binding. Default is ```"0.0.0.0"```. 

## Usage

Because tesla-microservice is a provided dependency, you must always specify two dependencies in your project clj:

```clojure
:dependencies [[de.otto/tesla-microservice "0.1.15"]
               [de.otto/tesla-httpkit "0.1.3"]]
```
Add the server to the base-system before starting it. Pass in additional dependencies of the server (e.g. ```:my-page```): 
```clojure
  (system/start (serving-with-httpkit/add-server (system/base-system {}) :my-page))
```


## Compatibility
Versions ```0.1.0``` and above of tesla-httpkit are compatible with versions ```0.1.15``` and above of tesla-microservice.

## License
Apache License
