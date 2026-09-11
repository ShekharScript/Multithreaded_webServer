import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;


// This class builds the response we send back to the browser.
//  It includes the toBytes() method that correctly formats the HTTP text so the browser understands it.

public class HttpResponse {
    private final int statusCode;
    private final String statusMessage;
    private final Map<String, String> headers = new HashMap<>();
    private byte[] body;

    public HttpResponse(int statusCode, String statusMessage) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public void setBody(String content, String contentType) {
        this.body = content.getBytes();
        headers.put("Content-Type", contentType);
        headers.put("Content-Length", String.valueOf(this.body.length));
    }

    public byte[] toBytes() {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            
            String statusLine = "HTTP/1.1 " + statusCode + " " + statusMessage + "\n";
            outputStream.write(statusLine.getBytes());
            
            // Write Headers
            for (Map.Entry<String, String> header : headers.entrySet()) {
                String headerLine = header.getKey() + ": " + header.getValue() + "\n";
                outputStream.write(headerLine.getBytes());
            }
            
           
            outputStream.write("\n".getBytes());  // Empty Line (Separates headers from body)
            
            if (body != null) {
                outputStream.write(body);
            }
            
            return outputStream.toByteArray();

        } catch (Exception e) {
            return new byte[0];
        }
    }
}