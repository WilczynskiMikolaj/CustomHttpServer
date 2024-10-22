import java.io.*;
import java.net.Socket;
import java.util.Map;

public class SocketHandler implements Runnable{
    private final Socket socket;
    private Handler defaultHandler;
    private final Map<String, Map<String, Handler>> handlers;

    public SocketHandler(Socket socket, Map<String, Map<String, Handler>> handlers){
        this.socket = socket;
        this.handlers = handlers;
    }

    private void respond(int statusCode, String msg, OutputStream out) throws IOException {
        String responseLine = "HTTP/1.1 " + statusCode + " " + msg + "\r\n\r\n";
        log(responseLine);
        out.write(responseLine.getBytes());
    }

    public void run(){
        BufferedReader in = null;
        OutputStream out = null;

        try{
            in = new BufferedReader((new InputStreamReader(socket.getInputStream())));
            out = socket.getOutputStream();

            Request request = new Request(in);
            if(!request.parse()) {
                respond(500, "Unable to parse request", out);
                return;
            }

            boolean foundHandler = false;
            Response response = new Response(out);
            Map<String, Handler> methodHandlers = handlers.get(request.getMethod());
            if(methodHandlers == null){
                respond(405, "Method not supported", out);
                return;
            }
            for(String handlerPath: methodHandlers.keySet()){
                if(handlerPath.equals(request.getPath())){
                    methodHandlers.get(request.getPath()).handle(request, response);
                    response.send();
                    foundHandler = true;
                    break;
                }
            }
            if(!foundHandler)  {
                if (methodHandlers.get("/*") != null)  {
                    methodHandlers.get("/*").handle(request, response);
                    response.send();
                } else  {
                    respond(404, "Not Found", out);
                }
            }
        } catch (IOException e)  {
            e.printStackTrace();
        } finally  {
            try  {
                if (out != null)  {
                    out.close();
                }
                if (in != null)  {
                    in.close();
                }
                socket.close();
            } catch (IOException e)  {
                e.printStackTrace();
            }
        }

    }

    private void log(String msg)  {
        System.out.println(msg);
    }

}
