package app;

import com.sun.net.httpserver.*;
import java.io.*;
import java.nio.file.*;

public class StaticFileHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String path = exchange.getRequestURI().getPath();

        // Default page
        if (path.equals("/")) {
            path = "/goof.html";
        }

        File file = new File("./static" + path);

        // File not found
        if (!file.exists() || file.isDirectory()) {
            String response = "404 File Not Found";
            exchange.sendResponseHeaders(404, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
            return;
        }

        byte[] fileBytes = Files.readAllBytes(file.toPath());

        // Detect content type
        String contentType = "text/html";

        if (path.endsWith(".css")) contentType = "text/css";
        else if (path.endsWith(".js")) contentType = "application/javascript";
        else if (path.endsWith(".png")) contentType = "image/png";
        else if (path.endsWith(".jpg") || path.endsWith(".jpeg")) contentType = "image/jpeg";
        else if (path.endsWith(".svg")) contentType = "image/svg+xml";
        else if (path.endsWith(".json")) contentType = "application/json";
        else if (path.endsWith(".ico")) contentType = "image/x-icon";

        exchange.getResponseHeaders().set("Content-Type", contentType);

        exchange.sendResponseHeaders(200, fileBytes.length);

        OutputStream os = exchange.getResponseBody();
        os.write(fileBytes);
        os.close();
    }
}