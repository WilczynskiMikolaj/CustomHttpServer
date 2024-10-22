import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileHandler implements Handler {
    public void handle(Request request, Response response) throws IOException {
        String path = request.getPath().substring(1);
        String contentType = getContentType(path);
        try (FileInputStream file = new FileInputStream(path)) {
            response.setResponseCode(200, "OK");
            response.addHeader("Content-Type", contentType);
            StringBuilder buf = new StringBuilder();
            int c;
            while ((c = file.read()) != -1) {
                buf.append((char) c);
            }
            response.addBody(buf.toString());
        } catch (FileNotFoundException e) {
            response.setResponseCode(404, "Not Found");
        }
    }

    private String getContentType(String path) {
        if (path.endsWith(".html")) {
            return "text/html";
        } else if (path.endsWith(".css")) {
            return "text/css";
        } else if (path.endsWith(".js")) {
            return "application/javascript";
        } else if (path.endsWith(".png")) {
            return "image/png";
        } else if (path.endsWith(".jpg") || path.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        return "text/plain";
    }
}