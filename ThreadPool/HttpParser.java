import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

// This class reads the raw byte stream from the client socket and translates it into the HttpRequest object 

public class HttpParser {
    public static HttpRequest parse(InputStream inputStream) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        
        // 1. Read the first line (e.g., "GET / HTTP/1.1")
        String requestLine = reader.readLine();
        if (requestLine == null || requestLine.isEmpty()) {
            throw new Exception("Empty Request");
        }
        String[] parts = requestLine.split(" ");
        
        // 2. Read the headers
        Map<String, String> headers = new HashMap<>();
        String headerLine;
        while ((headerLine = reader.readLine()) != null && !headerLine.isEmpty()) {
            String[] headerParts = headerLine.split(": ", 2);
            if (headerParts.length == 2) {
                headers.put(headerParts[0], headerParts[1]);
            }
        }
        
        // 3. Read the body if it exists
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