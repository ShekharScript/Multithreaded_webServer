import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final ExecutorService threadPool;

    private final AtomicInteger activeClients = new AtomicInteger(0);

    public Server(int poolSize) {
        this.threadPool = Executors.newFixedThreadPool(poolSize); //uses unbounded queue linkedblockingqueue 
    }


    public void handleClient(Socket clientServerSocket) {

        // increase the count of active clients
        int currentCount = activeClients.incrementAndGet();
        System.out.println("Active clients connected right now: " + currentCount);


        try (PrintWriter toClientStream = new PrintWriter(clientServerSocket.getOutputStream(), true)) {
            toClientStream.println("Hello from server " + clientServerSocket.getInetAddress());

            Thread.sleep(2000);// Fake processing
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int port = 8010;
        int poolSize = 10; // Adjust the pool size as needed
        Server server = new Server(poolSize);

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(70000);
            System.out.println("Server is listening on port " + port);

            while (true) {
                Socket clientServerSocket = serverSocket.accept();

                // Use the thread pool to handle the client
                server.threadPool.execute(() -> server.handleClient(clientServerSocket));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            //shuting down the server gracefully
            System.out.println("Shutting down server gracefully...");
            server.threadPool.shutdown(); // Naye tasks lena band karo
        
            try {
                // Wait karo 10 seconds tak taaki chal rahe clients poore ho sakein
                if (!server.threadPool.awaitTermination(10, TimeUnit.SECONDS)) {
                    server.threadPool.shutdownNow(); // Agar phir bhi khatam nahi hue toh force close karo
                }
            } catch (InterruptedException ie) {
                server.threadPool.shutdownNow();
            }
            System.out.println("Server stopped safely.");
        }
    }
}
