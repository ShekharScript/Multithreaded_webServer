import java.util.Map;



// This class holds all the incoming http data (like the URL path, HTTP method, etc) sent by the browser.
public class HttpRequest {
    private final String method;
    private final String path;
    private final String version;
    private final Map<String, String> headers;
    private final String body;


    // When our HttpParser finishes reading the raw socket data, it calls this constructor,
    // passes all the extracted strings, and creates this object.
    public HttpRequest(String method, String path, String version, Map<String, String> headers, String body) {
        this.method = method;
        this.path = path;
        this.version = version;
        this.headers = headers;
        this.body = body;
    }

    public String getMethod() { return method; } // Getter methods.
    public String getPath() { return path; }
    public String getVersion() { return version; }
    public Map<String, String> getHeaders() { return headers; }
    public String getBody() { return body; }
}