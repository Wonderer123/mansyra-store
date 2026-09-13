package app;

import java.io.*;
import java.nio.file.*;
import org.json.*;
import org.json.JSONArray;

public class DataStore {

    private static final String USER_FILE = "newusers.json";
    private static final String CART_FILE = "cart.json";

    public static JSONArray readUsers() throws Exception {
        String content = new String(Files.readAllBytes(Paths.get(USER_FILE)));
        return new JSONArray(content);
    }

    public static void writeUsers(JSONArray users) throws Exception {
        Files.write(Paths.get(USER_FILE), users.toString(4).getBytes());
    }

    public static JSONObject readCart() throws Exception {
        String content = new String(Files.readAllBytes(Paths.get(CART_FILE)));
        return new JSONObject(content);
    }

    public static void writeCart(JSONObject cart) throws Exception {
        Files.write(Paths.get(CART_FILE), cart.toString(4).getBytes());
    }

    public static JSONArray getUsers() throws Exception {

        File file = new File("newusers.json");

        if (!file.exists()) {
            return new JSONArray();
        }

        BufferedReader br = new BufferedReader(new FileReader(file));
        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = br.readLine()) != null) {
            sb.append(line);
        }

        br.close();

        return new JSONArray(sb.toString());
    }
}