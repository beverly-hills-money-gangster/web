# Web

A lightweight HTTP/1.1 web framework for Java 25+, built on top of
the [ioc](https://github.com/beverly-hills-money-gangster/ioc) dependency injection framework.

The project focuses on simplicity, modern Java features, and predictable performance. Network I/O is
handled by virtual threads while request processing is delegated to a fixed worker pool, allowing
application code to perform CPU-intensive work or use blocking/pinning APIs without impacting the
I/O layer.

## Features

* HTTP/1.1 server
* GET, POST, PUT, PATCH and DELETE support
* Persistent (Keep-Alive) connections
* Request filtering
* JSON request/response handling (Jackson)
* HTML server-side rendering using Mustache
* Form data parsing
* Static file serving
* Spring Actuator-like health endpoint
* Exception-to-response mapping

## Architecture

The server separates networking from application logic:

* **I/O layer** runs on virtual threads and is responsible for accepting connections and
  reading/writing HTTP traffic.
* **Request processing** is executed on a fixed thread pool, preventing CPU-bound or
  thread-pinning application code from blocking network operations.

This design combines the scalability of virtual threads with predictable request execution.

## Requirements

* Java 25 or newer
* Maven

## Running


```java
public class Main {

  static void main() throws IOException {
    try (var container = WebContainer.init(Main.class, Set.of("enableStaticFiles"))) {
      var serverRunner = container.getInstance(ServerRunner.class);
      serverRunner.start(8080);
    }
  }

}
```
