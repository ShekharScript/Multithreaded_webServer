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

    public static final AtomicInteger totalRequests = new AtomicInteger(0);




    public Server(int port) {
        this.port = port;
        this.router = new Router();
        
        setupRoutes();

        this.threadPool = new ThreadPoolExecutor(
            10,   // Core pool size
            50,   // Max pool size
            60L, TimeUnit.SECONDS, // Idle thread keep-alive
            new ArrayBlockingQueue<>(1000) // Bounded queue for traffic spikes
        );

        setupGracefulShutdown(); /// registers a shutdown hook to JVM.
    }




    private void setupRoutes() {
        // Define all backend endpoints.

        router.addRoute("GET", "/", (request) -> {
            HttpResponse response = new HttpResponse(200, "OK");
            response.setBody("<h1> Hi There, Welcome to the Java Server which is uses java executor framework!</h1>", "text/html");
            return response;
        });

        router.addRoute("GET", "/metrics", (request) -> {
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

                    Socket clientSocket = serverSocket.accept(); /// This is a blocking call. The main thread freezes here until a browser actually connects.
                    totalRequests.incrementAndGet();
                    
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

        /// here Runtime.getRuntime().addShutdownHook(threadObj) means, 
        /// When the Java application is about to shut down, execute this thread,
        /// i.e the JVM promises to run this specific block of code before dying.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Initiating graceful shutdown...");
            isRunning = false; // Break the while loop in start() of main thread // isRunning is volatile
            
            try {

                if (serverSocket != null && !serverSocket.isClosed()) {
                    serverSocket.close(); 
                    /// we close the main ServerSocket so no new users can connect.
                }
                
                threadPool.shutdown();
                System.out.println("Waiting for existing requests to finish...");
                

                if (!threadPool.awaitTermination(30, TimeUnit.SECONDS)) {
                    //This method causes the shutdown thread to go to sleep and wait for up to 30 seconds.
                    // While it is waiting, the OS or the JVM might forcefully interrupt this thread so exception will be thrown
                    System.out.println("Forcing shutdown of pending tasks...");
                    threadPool.shutdownNow(); 
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

        /// if we try to call the graceful here after start in the main thread,
        /// it will never be called because the start method is blocking since it contains a while (isRunning) loop that runs infinitely.
        /// So, if we tried to call setupGracefulShutdown() after calling start(), the code would literally never reach it.
        /// for that we used 'Runtime.getRuntime().addShutdownHook()' inside the setupGracefulShutdown() method.
        /// then JVM runs a specific thread for shutdown part 
        /// since isRunning is volatile, the main thread will see the change made by the shutdown hook thread, theerby exiting while(isRunning) gracefully.
        
    }
}






/// why is isRunning volatile?

/**
        Why is isRunning declared as volatile? Because the server loop runs on the main thread,
        but the shutdown hook (which changes isRunning to false) runs on a different background thread.
        Without volatile, the main thread might cache the value as true in its CPU register
        and never notice that the shutdown thread changed it to false.
        volatile forces all threads to read the variable directly from main RAM, ensuring visibility
*/


/// graceful shutdown

/**
        Server running
            ↓
        Requests being handled
            ↓
        Ctrl + C / application shutdown
            ↓
        Shutdown hook runs -> graceful shutdown initiated
            ↓
        Stop accepting new requests
            ↓
        Finish/cleanup existing work
            ↓
        Close resources
            ↓
        Server exits
 */