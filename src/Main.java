import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException{
        HttpServer server = new HttpServer(8080);
        server.addHandler("GET", "/hello", new Handler()  {
            public void handle(Request request, Response response) throws IOException {
                String html = "It works, " + request.getParam("name");
                response.setResponseCode(200, "OK");
                response.addHeader("Content-Type", "text/html");
                response.addBody(html);
            }
        });
        server.addHandler("GET", "/*", new FileHandler());
        server.start();
    }
}