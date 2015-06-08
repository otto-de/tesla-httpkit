#tesla-jetty

This library provides a component, that adds an embedded jetty server to [tesla-microservice](https://github.com/otto-de/tesla-mivroservice). 
This componenent has been extracted from tesla-microservice in order to allow for operation with other, especially non-blocking, server implementations as well as headless operation.

`[de.otto/tesla-jetty "0.1.0"]`

[![Build Status](https://travis-ci.org/otto-de/tesla-jetty.svg)](https://travis-ci.org/otto-de/tesla-jetty)
[![Dependencies Status](http://jarkeeper.com/otto-de/tesla-jetty/status.svg)](http://jarkeeper.com/otto-de/tesla-jetty)

## Configuration

The config ```:server-port``` will be used as port. Default is ```8080```. 

## Usage

Because tesla-microservice is a provided dependency, you must always specify two dependencies in your project clj:

```clojure
:dependencies [[de.otto/tesla-microservice "0.1.15"]
               [de.otto/tesla-jetty "0.1.0"]]
```
Add the server to the base-system before starting it. Pass in additional dependencies of the server (e.g. ```:my-page```): 
```clojure
  (system/start (serving-with-jetty/add-server (system/base-system {}) :my-page))
```

See [tesla-examples/simple-example](https://github.com/otto-de/tesla-examples/tree/master/simple-example) for a usage example.

## Compatibility
Versions ```0.1.0``` and above of tesla-jetty are compatible with versions ```0.1.15``` and above of tesla-microservice.

## License
Apache License
