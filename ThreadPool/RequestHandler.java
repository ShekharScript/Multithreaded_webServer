

// This is a simple interface that defines how our server responds to requests.


public interface RequestHandler {

    HttpResponse handle(HttpRequest request);


}