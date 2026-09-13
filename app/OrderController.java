package app;

import com.sun.net.httpserver.*;
import java.io.*;
import java.nio.file.*;
import org.json.*;

public class OrderController implements HttpHandler {

    private static final String FILE = "orders.json";

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        try{

            String method = exchange.getRequestMethod();

            if(method.equalsIgnoreCase("POST")){

                BufferedReader br = new BufferedReader(
                        new InputStreamReader(exchange.getRequestBody()));

                StringBuilder body = new StringBuilder();
                String line;

                while((line=br.readLine())!=null){
                    body.append(line);
                }

                JSONObject request = new JSONObject(body.toString());

                String user = request.getString("user");
                JSONObject order = request.getJSONObject("order");

                JSONObject db = read();

                if(!db.has(user)){
                    db.put(user,new JSONArray());
                }

                db.getJSONArray(user).put(order);

                write(db);

                send(exchange,"saved");

            }

            if(method.equalsIgnoreCase("GET")){

                String query = exchange.getRequestURI().getQuery();
                String user = query.split("=")[1];

                JSONObject db = read();

                JSONArray orders = db.has(user)
                        ? db.getJSONArray(user)
                        : new JSONArray();

                send(exchange,orders.toString());

            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private JSONObject read() throws Exception{

        File f = new File(FILE);

        if(!f.exists()) return new JSONObject();

        String text = new String(Files.readAllBytes(Paths.get(FILE)));

        if(text.trim().isEmpty()) return new JSONObject();

        return new JSONObject(text);

    }

    private void write(JSONObject obj) throws Exception{

        Files.write(Paths.get(FILE), obj.toString(4).getBytes());

    }

    private void send(HttpExchange ex,String res) throws IOException{

        ex.sendResponseHeaders(200,res.length());

        OutputStream os = ex.getResponseBody();

        os.write(res.getBytes());

        os.close();

    }

}