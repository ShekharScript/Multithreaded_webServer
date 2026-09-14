import java.io.ByteArrayOutputStream; // it writes the data into a byte array in memory
import java.io.IOException;
import java.util.HashMap; // to store headers to be sent
import java.util.Map;


/// here, we construct our response as per http1.1 protocol so browser can understand it 
///  It includes the toBytes() method that correctly formats the HTTP text so the browser understands it.

///how a response lookes:- 
/** 
   
        HTTP/1.1 200 OK\r\n
        Content-Type: text/plain\r\n
        Content-Length: 16\r\n
        \r\n
        Login Successful

*/

public class HttpResponse {

    ///core components of an HTTP response.
    private final int statusCode;
    private final String statusMessage;
    private final Map<String, String> headers = new HashMap<>();
    private byte[] body;



    public HttpResponse(int statusCode, String statusMessage) {

        this.statusCode = statusCode;
        this.statusMessage = statusMessage;

    }

    public void setBody(String content, String contentType) {
        
        this.body = content.getBytes();  /// you html or text into bytes coz the server may send images, PDFs, etc 
        headers.put("Content-Type", contentType);
        headers.put("Content-Length", String.valueOf(this.body.length)); /// Without it, the browser doesn't know when the server has finished sending data.

    }


    /// imp method that translates our response object into exact text/byte structure required by the HTTP protocol
    public byte[] toBytes() {

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); /// it writes the data into a byte array in memory(RAM)
            
            String statusLine = "HTTP/1.1 " + statusCode + " " + statusMessage + "\r\n"; /// u meay use \r\n it is preferred but \n works too
            outputStream.write(statusLine.getBytes());
            
            /// Write Headers
            for (Map.Entry<String, String> header : headers.entrySet()) {
                String headerLine = header.getKey() + ": " + header.getValue() + "\r\n";
                outputStream.write(headerLine.getBytes());
            }
            
           
            outputStream.write("\r\n".getBytes());  /// Empty Line (to Separate headers from body) http protocol requires an empty line between headers and body
            
            if (body != null) {
                outputStream.write(body);
            }
            
            return outputStream.toByteArray(); /// this array of bytes is what the ClientHandler eventually pushes into the network socket.

        } catch (IOException e) {
            return new byte[0]; //extreme case of failure, so returning an empty array breaks the browser's request, but it prevents the worker thread from crashing
        }
    }
}


/// all this response are written temporarily in RAM(not disk and not across the network)
/// and our client Handler will take this reponse and decides to send it or not.