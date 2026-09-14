import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

// This class reads the raw byte stream from the client socket and translates it into the HttpRequest object 


//http reuest structure , \r\n are hidden hota h
/**
        POST /login HTTP/1.1\r\n
        Host: localhost:8080\r\n
        User-Agent: Chrome\r\n
        Content-Length: 15\r\n
        \r\n
        username=shekhar
 */


public class HttpParser {

    public static HttpRequest parse(InputStream inputStream) throws Exception {

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        
        /// Read the first line (eg: "GET /metrics HTTP/1.1") 
        String requestLine = reader.readLine(); /// read the first line of the request, which contains the HTTP method, path, and version

        if (requestLine == null || requestLine.isEmpty()) {
            throw new Exception("Empty Request");
        }
        String[] parts = requestLine.split(" "); // 3 parts separated by spaces, [Method] [Path] [Protocol]




        Map<String, String> headers = new HashMap<>();
        String headerLine;
                                                            //stop before body
        while ((headerLine = reader.readLine()) != null && !headerLine.isEmpty()) {

                                                       //limit
            String[] headerParts = headerLine.split(": ",  2);  // the header value itself might contain a colon, rg : "Host: localhost:8080"
            if (headerParts.length == 2) {
                headers.put(headerParts[0], headerParts[1]);
            }

        }
        
        StringBuilder body = new StringBuilder();

        if (headers.containsKey("Content-Length")) {
            int contentLength = Integer.parseInt(headers.get("Content-Length")); 
            for (int i = 0; i < contentLength; i++) {
                body.append((char) reader.read());
            }
        }
        
        return new HttpRequest(parts[0], parts[1], parts[2], headers, body.toString());
    }


}


/// Why is parse() method static ? 
///Because HttpParser is a "Utility Class. It doesn't need to store any state or remember anything between requests.