package app;

import java.util.*;

public class SessionManager {

    private static Map<String, String> sessions = new HashMap<>();

    public static String createSession(String user) {
        String token = UUID.randomUUID().toString();
        sessions.put(token, user);
        return token;
    }

    public static String getUser(String token) {
        return sessions.get(token);
    }

    public static void removeSession(String token) {
        sessions.remove(token);
    }
}