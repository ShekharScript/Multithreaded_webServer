import java.util.HashMap;
import java.util.Map;


// This class connects specific URLs (like /) to the logic that handles them


public class Router {
    private final Map<String, RequestHandler> routes = new HashMap<>();

    public void addRoute(String method, String path, RequestHandler handler) {
        routes.put(method + " " + path, handler);
    }

    public HttpResponse route(HttpRequest request) {
        String routeKey = request.getMethod() + " " + request.getPath();
        RequestHandler handler = routes.get(routeKey);
        
        if (handler != null) {
            return handler.handle(request);
        } else {
            // Return a 404 Not Found if the URL doesn't match any route
            HttpResponse notFoundResponse = new HttpResponse(404, "Not Found");
            notFoundResponse.setBody("<h1>404 - Page Not Found</h1>", "text/html");
            return notFoundResponse;
        }
    }
}