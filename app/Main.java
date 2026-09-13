package app;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;

public class Main {

    public static void main(String[] args) throws Exception {

        // Hosting providers (Render, Railway, Fly.io) tell us which port to use via PORT.
        int port = 8080;
        String envPort = System.getenv("PORT");
        if (envPort != null && !envPort.isBlank()) {
            port = Integer.parseInt(envPort.trim());
        }

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/api/register", new UserController());
        server.createContext("/api/login", new UserController());
        server.createContext("/api/logout", new UserController());
        server.createContext("/api/cart", new CartController());
        server.createContext("/api/checkout", new PaymentController());
        server.createContext("/api/orders", new OrderController());
        server.createContext("/", new StaticFileHandler());

        server.setExecutor(null);
        server.start();

        System.out.println("=================================");
        System.out.println(" Mansyra Server Running ");
        System.out.println("=================================");
        System.out.println();
        System.out.println("Open Website : ");
        System.out.println("(Link) --------->           "+"http://localhost:" + port + "           <--------- (Link)");
        System.out.println();
        System.out.println("=================================");
    }
}