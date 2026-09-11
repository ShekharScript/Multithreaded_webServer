# Multi-Threaded & Pooled Java HTTP Web Server 

A low-level, high-performance HTTP/TCP Web Server built entirely from scratch in Core Java. This project was developed to study backend architecture, network socket programming, and concurrency mechanisms by building the underlying tools that modern frameworks (like Spring Boot) abstract away.

---

## 🚀 Key Architectural Features

### HTTP Protocol & Routing Engine
- **Custom HTTP Parser:** Reads raw TCP `InputStream` byte data and parses it into strictly typed, immutable `HttpRequest` data transfer objects.
- **Dynamic Request Routing:** Implements a custom `Router` that maps specific HTTP methods and URL paths (e.g., `GET /`) to modular `RequestHandler` interfaces.
- **Separation of Concerns:** Achieves a production-style architecture by isolating the server lifecycle, socket I/O, HTTP parsing, and business logic into dedicated components.
- **Structured HTTP Responses:** Dynamically constructs standards-compliant HTTP/1.1 response headers, status codes, and payloads via the `HttpResponse` class.

### Concurrency & Performance Optimization
- **Low-Level Socket Programming:** Direct manipulation of the transport layer using Java's native `ServerSocket` and `Socket` APIs.
- **Thread Pool Engine:** Replaced resource-heavy unbounded thread allocation (Thread-per-request) with a highly configurable `ThreadPoolExecutor`.
- **Bounded Task Queue:** Utilizes a bounded blocking queue to safely buffer spikes in incoming traffic, eliminating `OutOfMemoryError` vulnerabilities during high loads.
- **Dual-Stage Graceful Shutdown:** Deploys coordination via `.shutdown()` and `.awaitTermination()` patterns to guarantee that active in-flight requests are cleanly processed before releasing server resources.

---

## 🏗️ Request Lifecycle Architecture

```text
Client Connection
       ↓
Server (Accepts Socket)
       ↓
ThreadPoolExecutor (Assigns Worker Thread)
       ↓
ClientHandler (Reads raw InputStream)
       ↓
HttpParser (Translates Stream -> HttpRequest POJO)
       ↓
Router (Maps HttpRequest.path -> RequestHandler)
       ↓
RequestHandler (Executes business logic -> Returns HttpResponse)
       ↓
ClientHandler (Writes HttpResponse to OutputStream & Closes)