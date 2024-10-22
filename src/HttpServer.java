import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {
    private final int port;
    private volatile boolean running = true;
    private ServerSocket serverSocket;
    private final ExecutorService threadPool = Executors.newFixedThreadPool(100);
    private final Map<String, Map<String, Handler>> handlers = new HashMap<>();

    public HttpServer(int port){
        this.port = port;
    }

    public void start() throws IOException {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Listening on port " + port);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    stop();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }));

            while (running) {
                Socket client = serverSocket.accept();
                System.out.println("Received connection from " + client.getRemoteSocketAddress().toString());
                SocketHandler handler = new SocketHandler(client, handlers);
                threadPool.submit(handler);
            }
        } catch (IOException e) {
            if (running) {
                System.out.println(e.getMessage());
                stop();
            }
        }
    }

    public void stop() throws IOException {
        running = false;
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
        threadPool.shutdown();
        System.out.println("Server is shutting down.");
    }

    public void addHandler(String method, String path, Handler handler){
        Map<String, Handler> methodHandlers = handlers.computeIfAbsent(method, k -> new HashMap<String, Handler>());
        methodHandlers.put(path, handler);
    }
}
