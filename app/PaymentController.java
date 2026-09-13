package app;

import com.sun.net.httpserver.*;
import java.io.*;
import org.json.*;

public class PaymentController implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        try {

            InputStreamReader isr = new InputStreamReader(exchange.getRequestBody());
            BufferedReader br = new BufferedReader(isr);

            StringBuilder body = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null)
                body.append(line);

            JSONObject request = new JSONObject(body.toString());

            String user = request.getString("user");

            JSONObject cart = DataStore.readCart();

            if (cart.has(user)) {
                cart.put(user, new JSONArray());
                DataStore.writeCart(cart);
            }

            sendResponse(exchange, "Payment Successful");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendResponse(HttpExchange exchange, String response) throws IOException {

        exchange.sendResponseHeaders(200, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}