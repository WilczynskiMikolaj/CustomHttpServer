import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpServer {
    private final int port;
    private Handler defaultHandler = null;
    private final Map<String, Map<String, Handler>> handlers = new HashMap<>();

    public HttpServer(int port){
        this.port = port;
    }

    public void start() throws IOException {
        try (ServerSocket socket = new ServerSocket(port)) {
            System.out.println("Listening on port " + port);
            Socket client;
            while ((client = socket.accept()) != null) {
                System.out.println("Received connection from " + client.getRemoteSocketAddress().toString());
                SocketHandler handler = new SocketHandler(client, handlers);
                Thread t = new Thread(handler);
                t.start();
            }
        }
    }

    public void addHandler(String method, String path, Handler handler){
        Map<String, Handler> methodHandlers = handlers.computeIfAbsent(method, k -> new HashMap<String, Handler>());
        methodHandlers.put(path, handler);
    }
}
