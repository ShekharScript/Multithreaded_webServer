import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    
    public void run() throws UnknownHostException, IOException{

        int port = 8010; //same port as the server is listening on

        InetAddress address = InetAddress.getByName("localhost"); 

        Socket socket = new Socket(address, port); //attempts connects to the server socket at the specified address and port

        PrintWriter toServerStream = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader fromServerStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        toServerStream.println("Hello World from client "+ socket.getLocalSocketAddress() );
        
        String line = fromServerStream.readLine(); //blocking call, waits until full line is received from the server
        System.out.println("server said: "+line);

        toServerStream.close();   //close the resources
        fromServerStream.close();
        socket.close(); //close the socket connection
    }
    
    public static void main(String[] args) {
        Client singleThreadedWebServer_Client = new Client();
        try{
            singleThreadedWebServer_Client.run();
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }
}




// Interviewer Question: "What is wrong with this Single-Threaded design? Why can't we use it in production?"

// Answer: "In this implementation, everything runs sequentially on a single Main Thread. When Client A connects,
//  the server accepts it and starts reading/writing to Client A's streams. 

// If Client A has a slow internet connection, or takes 10 seconds to send data, the single thread blocks at fromClient.readLine().
// During those 10 seconds, if Client B tries to connect, Client B will hang indefinitely and cannot be served.
// The server cannot return to the top of the while(true) loop to execute socket.accept() for Client B until it has completely 
// finished processing Client A."