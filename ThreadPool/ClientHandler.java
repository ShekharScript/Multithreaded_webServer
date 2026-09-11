import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


// worker task that our ThreadPoolExecutor runs

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final Router router;

    public ClientHandler(Socket clientSocket, Router router) {
        this.clientSocket = clientSocket;
        this.router = router;
    }

    @Override
    public void run() {
        try (
            InputStream input = clientSocket.getInputStream();
            OutputStream output = clientSocket.getOutputStream()
        ) {

            HttpRequest request = HttpParser.parse(input);
            
            HttpResponse response = router.route(request);
            
            output.write(response.toBytes());
            output.flush();

        } catch (Exception e) {
            System.err.println("Connection closed or error: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (Exception e) {
                System.err.println("Error closing socket: " + e.getMessage());
            }
        }
    }
}