import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class Server {
    
    public void run() throws IOException, UnknownHostException, SocketTimeoutException{

        int port = 8010;
        ServerSocket socket = new ServerSocket(port);
        socket.setSoTimeout(20000); // throw SocketTimeoutException if no connection is made within 20 seconds

        while(true){ // server will run forever until the connection is closed 
            System.out.println("Server is listening on port: "+port);

            Socket clientServerSocket = socket.accept();
            // The accept() method is a blocking call. This means execution of the program completely stops here.


            // Once a connection is made, accept() unblocks and returns a new Socket object
            System.out.println("Connected to "+ clientServerSocket.getRemoteSocketAddress());
            

                //PrintWriter accepts an OutputStream                                   //auto-flush
            PrintWriter toClient = new PrintWriter(clientServerSocket.getOutputStream(), true);
            BufferedReader fromClient = new BufferedReader(new InputStreamReader(clientServerSocket.getInputStream()));
                                            //char stream     //converts byte to char              //byte stream

            String line = fromClient.readLine(); //blocking call, waits until full line is received from the client
            System.out.println("client said: "+line);


            toClient.println("Hello World from the server");
        }
    }

    public static void main(String[] args){
        Server server = new Server();
        try{
            server.run();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

}
