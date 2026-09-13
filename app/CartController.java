package app;

import com.sun.net.httpserver.*;
import java.io.*;
import org.json.*;

public class CartController implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        try{

            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            /* ================= GET CART ================= */

            if(method.equalsIgnoreCase("GET")){

                String query = exchange.getRequestURI().getQuery();
                String user = query.split("=")[1];

                JSONObject cart = DataStore.readCart();

                JSONArray items = cart.has(user)
                        ? cart.getJSONArray(user)
                        : new JSONArray();

                JSONObject response = new JSONObject();

                response.put("items", items);
                response.put("subtotal", calculateSubtotal(items));
                response.put("tax", calculateTax(items));
                response.put("delivery", calculateDelivery(items));
                response.put("total", calculateTotal(items));

                sendResponse(exchange, response.toString());
                return;
            }


            /* ================= READ BODY ================= */

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(exchange.getRequestBody()));

            StringBuilder body = new StringBuilder();
            String line;

            while((line = br.readLine()) != null){
                body.append(line);
            }

            JSONObject request = new JSONObject(body.toString());

            String user = request.getString("user");

            JSONObject cart = DataStore.readCart();

            if(!cart.has(user)){
                cart.put(user,new JSONArray());
            }

            JSONArray userCart = cart.getJSONArray(user);


            /* ================= ADD ITEM ================= */

            if(path.equals("/api/cart")){

                JSONObject item = request.getJSONObject("item");

                userCart.put(item);

                DataStore.writeCart(cart);

                sendResponse(exchange,userCart.toString());
                return;
            }


            /* ================= REMOVE ITEM ================= */

            if(path.equals("/api/cart/remove")){

                JSONObject item = request.getJSONObject("item");

                for(int i=0;i<userCart.length();i++){

                    JSONObject obj = userCart.getJSONObject(i);

                    if(obj.getString("name").equals(item.getString("name"))){

                        userCart.remove(i);
                        break;

                    }

                }

                DataStore.writeCart(cart);

                sendResponse(exchange,"removed");
                return;
            }


            /* ================= CLEAR CART ================= */

            if(path.equals("/api/cart/clear")){

                cart.put(user,new JSONArray());

                DataStore.writeCart(cart);

                sendResponse(exchange,"cleared");
                return;
            }


        }catch(Exception e){
            e.printStackTrace();
        }
    }



    /* ================= CALCULATIONS ================= */

    private double calculateSubtotal(JSONArray items){

        double subtotal = 0;

        for(int i=0;i<items.length();i++){

            JSONObject item = items.getJSONObject(i);

            subtotal += item.getDouble("price");

        }

        return subtotal;
    }


    private double calculateTax(JSONArray items){

        return calculateSubtotal(items) * 0.18;

    }


    private double calculateDelivery(JSONArray items){

        return items.length() > 0 ? 100 : 0;

    }


    private double calculateTotal(JSONArray items){

        return calculateSubtotal(items)
                + calculateTax(items)
                + calculateDelivery(items);

    }


    private void sendResponse(HttpExchange exchange,String response) throws IOException{

        exchange.getResponseHeaders().set("Content-Type","application/json");

        exchange.sendResponseHeaders(200,response.length());

        OutputStream os = exchange.getResponseBody();

        os.write(response.getBytes());

        os.close();
    }
}