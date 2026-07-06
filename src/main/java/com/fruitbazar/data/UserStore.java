package com.fruitbazar.data;

import com.fruitbazar.model.User;

import java.util.HashMap;
import java.util.Map;

/**
 * In-memory demo user store.
 * Replace with a real database-backed DAO (and hashed passwords) in production.
 *
 * Demo login: username "demo", password "demo123"
 */
public class UserStore {

    private static final Map<String, User> USERS = new HashMap<>();

    static {
        USERS.put("demo", new User("demo", "demo123", "Demo Customer", "demo@example.com"));
    }

    private UserStore() {
    }

    public static User authenticate(String username, String password) {
        User user = USERS.get(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }
}
