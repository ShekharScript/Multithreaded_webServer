# Multi-Threaded & Pooled TCP Web Server from Scratch

A low-level, high-performance HTTP/TCP Web Server built from scratch in Java using foundational network socket programming and multi-threading control structures. This project was developed iteratively across three distinct concurrency models to study architectural trade-offs, resource optimization, and transport-layer scaling mechanics.

---

## 🚀 Key Architectural Features

- **Low-Level Socket Programming:** Direct manipulation of TCP layers using Java's native `ServerSocket` and `Socket` APIs.
- **Custom Thread Pool Engine:** Replaced resource-heavy unbounded thread allocation with a highly configurable `ThreadPoolExecutor`.
- **Bounded Task Queue Optimization:** Utilizes a custom-bounded `LinkedBlockingQueue` to buffer spikes in incoming traffic safely, eliminating potential `OutOfMemoryError` vulnerabilities.
- **Thread-Safe Telemetry:** Leverages `AtomicInteger` overhead primitives to safely monitor active network connections in a highly concurrent environment.
- **Robust Resource Lifecycle Management:** Implements Java's **Try-with-resources** syntax across all endpoints to fully mitigate file descriptor and socket leaks.
- **Dual-Stage Graceful Shutdown:** Deploys coordination via `.shutdown()` and `.awaitTermination()` patterns to guarantee that active connection pipelines are cleanly processed before server tear-down.

---

## 📂 Project Evolution & Directory Structure

The project is structured into three progressive iterations to demonstrate systematic scaling solutions:

```text
├── SingleThreaded/       # Iteration 1: Sequential processing model (Baseline)
│   ├── Server.java       # Blocks on network I/O; handles one client at a time
│   └── Client.java       # Test client for sequential communication
│
├── Multithreaded/        # Iteration 2: Thread-per-request model (Unbounded Concurrency)
│   ├── Server.java       # Spawns a dedicated OS thread asynchronously per connection
│   └── Client.java       # Fires 100 parallel requests to test concurrent scaling
│
└── ThreadPool/           # Iteration 3: Production-grade Fixed Pool (Optimized)
    └── Server.java       # Manages fixed worker threads backed by a bounded blocking queue
