import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
    private final int port;
    private final ThreadPoolExecutor threadPool;
    private final Router router;
    private volatile boolean isRunning = true;
    private ServerSocket serverSocket;

    // Phase 6: Thread Pool Metrics
    public static final AtomicInteger totalRequests = new AtomicInteger(0);

    public Server(int port) {
        this.port = port;
        this.router = new Router();
        
        // Phase 5: Initialize Routes
        setupRoutes();

        // Phase 6: Configure ThreadPoolExecutor
        this.threadPool = new ThreadPoolExecutor(
            10,   // Core pool size
            50,   // Max pool size
            60L, TimeUnit.SECONDS, // Idle thread keep-alive
            new ArrayBlockingQueue<>(1000) // Bounded queue for traffic spikes
        );

        // Phase 7: Register Graceful Shutdown
        setupGracefulShutdown();
    }

    private void setupRoutes() {
        // Define endpoints using the Router
        router.addRoute("GET", "/", request -> {
            HttpResponse response = new HttpResponse(200, "OK");
            response.setBody("<h1>Welcome to the Java Server!</h1>", "text/html");
            return response;
        });

        router.addRoute("GET", "/metrics", request -> {
            HttpResponse response = new HttpResponse(200, "OK");
            String metrics = "Total requests processed: " + totalRequests.get() + 
                             "\nActive threads: " + threadPool.getActiveCount();
            response.setBody(metrics, "text/plain");
            return response;
        });
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port);

            while (isRunning) {
                try {
                    // Phase 2: Server only accepts connections
                    Socket clientSocket = serverSocket.accept();
                    totalRequests.incrementAndGet();
                    
                    // Phase 2: Delegate request processing to ClientHandler
                    threadPool.execute(new ClientHandler(clientSocket, router));
                    
                } catch (Exception e) {
                    // When server shuts down, accept() throws an exception. 
                    // Only print if we didn't trigger the shutdown intentionally.
                    if (isRunning) {
                        System.err.println("Error accepting client connection: " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Server exception: " + e.getMessage());
        }
    }

    private void setupGracefulShutdown() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nInitiating graceful shutdown...");
            isRunning = false; // Break the while loop in start()
            
            try {
                // 1. Stop accepting new connections
                if (serverSocket != null && !serverSocket.isClosed()) {
                    serverSocket.close();
                }
                
                // 2. Prevent new tasks from being submitted to the pool
                threadPool.shutdown();
                System.out.println("Waiting for existing requests to finish...");
                
                // 3. Wait for in-flight requests to complete
                if (!threadPool.awaitTermination(30, TimeUnit.SECONDS)) {
                    System.out.println("Forcing shutdown of pending tasks...");
                    threadPool.shutdownNow(); // Force kill if they take too long
                }
            } catch (Exception e) {
                threadPool.shutdownNow();
                Thread.currentThread().interrupt();
            }
            
            System.out.println("Server stopped safely.");
        }));
    }

    public static void main(String[] args) {
        Server server = new Server(8080);
        server.start();
    }
}