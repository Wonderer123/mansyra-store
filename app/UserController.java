package app;

import com.sun.net.httpserver.*;
import java.io.*;
import org.json.*;

public class UserController implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        try {

            String path = exchange.getRequestURI().getPath();

            // Allow only POST requests
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                sendResponse(exchange, "Invalid Request");
                return;
            }

            InputStreamReader isr = new InputStreamReader(exchange.getRequestBody());
            BufferedReader br = new BufferedReader(isr);
            StringBuilder body = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null)
                body.append(line);

            JSONObject request = new JSONObject(body.toString());

            if (path.equals("/api/register")) {
                handleRegister(exchange, request);
            }
            else if (path.equals("/api/login")) {
                handleLogin(exchange, request);
            }
            else if (path.equals("/api/logout")) {
                handleLogout(exchange, request);
            }
            else {
                sendResponse(exchange, "Invalid API");
            }

        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, "Server Error");
        }
    }

    private void handleRegister(HttpExchange exchange, JSONObject request) throws Exception {

        JSONArray users = DataStore.readUsers();

        String email = request.getString("email");
        String phone = request.getString("phone");

        for (int i = 0; i < users.length(); i++) {

            JSONObject u = users.getJSONObject(i);

            if (u.getString("email").equals(email) ||
                    u.getString("phone").equals(phone)) {

                sendResponse(exchange, "User already exists");
                return;
            }
        }

        users.put(request);
        DataStore.writeUsers(users);

        sendResponse(exchange, "Registration successful. Please login.");
    }

    private void handleLogin(HttpExchange exchange, JSONObject request) throws Exception {

        String userInput = request.getString("user");
        String password = request.getString("password");

        JSONArray users = DataStore.readUsers();

        boolean userExists = false;

        for (int i = 0; i < users.length(); i++) {

            JSONObject u = users.getJSONObject(i);

            if (u.getString("email").equals(userInput) ||
                    u.getString("phone").equals(userInput)) {

                userExists = true;

                if (u.getString("password").equals(password)) {

                    String token = SessionManager.createSession(userInput);

                    JSONObject response = new JSONObject();
                    response.put("status", "success");
                    response.put("token", token);
                    response.put("user", u.getString("name"));

                    sendResponse(exchange, response.toString());
                    return;
                }
                else {
                    JSONObject response = new JSONObject();
                    response.put("status", "wrong_password");
                    sendResponse(exchange, response.toString());
                    return;
                }
            }
        }

        if (!userExists) {
            JSONObject response = new JSONObject();
            response.put("status", "user_not_found");
            sendResponse(exchange, response.toString());
        }
    }

    private void handleLogout(HttpExchange exchange, JSONObject request) throws Exception {

        String token = request.getString("token");

        SessionManager.removeSession(token);

        sendResponse(exchange, "logged out");
    }

    private void sendResponse(HttpExchange exchange, String response) throws IOException {

        exchange.getResponseHeaders().set("Content-Type", "application/json");

        exchange.sendResponseHeaders(200, response.length());

        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}