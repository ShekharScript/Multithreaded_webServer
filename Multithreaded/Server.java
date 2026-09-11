import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;

public class Server {

    private final  Consumer<Socket> getConsumer =  

        //implementing the accept() method of Consumer interface, using lambda expression
        (clientServerSocket) -> {
            try (PrintWriter toClientStream = new PrintWriter(clientServerSocket.getOutputStream(), true);
            BufferedReader fromClientStream = new BufferedReader(new InputStreamReader(clientServerSocket.getInputStream()));) {

                toClientStream.println("Hello from server " + clientServerSocket.getInetAddress());
                String line = fromClientStream.readLine(); //blocking call
                System.out.println("client said: " + line); 
            } 
            catch (IOException ex) {
                ex.printStackTrace();
            }
        };
    
    
    public static void main(String[] args) {
        int port = 8010;
        Server server = new Server(); //class object
        
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(70000);

            System.out.println("Server is listening on port " + port);
            while (true) {
                Socket clientServerSocket = serverSocket.accept();
                
                // Create and start a new thread for each client

                Consumer<Socket> consumer = server.getConsumer ; //reference to the accept() method of Consumer interface  (consumer object)
                Thread thread = new Thread(() -> consumer.accept(clientServerSocket));
                                            // inside run() method -> accept() method is called of Consumer interface, which is implemented using lambda expression
                
                thread.start();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
}





// // above can be simplified to below code :-

// public class Server {

//     public void handleClient(Socket clientSocket){
//         try{
//             PrintWriter toClient = new PrintWriter( clientSocket.getOutputStream(), true);

//             toClient.println( "Hello from server " + clientSocket.getInetAddress() );

//         }catch(IOException e){
//             e.printStackTrace();
//         }
//     }



//     public static void main(String[] args) throws Exception{

//         Server server = new Server();

//         ServerSocket serverSocket =
//             new ServerSocket(8010);


//         while(true){
//             Socket clientSocket = serverSocket.accept();

//             Thread thread = new Thread( () -> server.handleClient(clientSocket));

//             thread.start();

//         }
//     }
// }