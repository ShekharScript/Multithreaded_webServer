

// This is a simple interface that defines how our server responds to requests.


public interface RequestHandler {

    //one method which takes a http resquest obj and return a http response. 
    HttpResponse handle(HttpRequest request) ;


}