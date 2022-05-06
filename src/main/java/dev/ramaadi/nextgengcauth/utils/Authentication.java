package dev.ramaadi.nextgengcauth.utils;

import emu.grasscutter.database.DatabaseHelper;
import emu.grasscutter.game.Account;

import java.util.HashMap;

public final class Authentication {
    public static final HashMap<String,String> tokens = new HashMap<>();

    public static Account getAccountByUsernameAndPassword(String username, String password) {
        Account account = DatabaseHelper.getAccountByName(username);
        if(!account.getPassword().equals(password)) account = null;
        return account;
    }

    public static Account getAccountByOneTimeToken(String token) {
        String username = Authentication.tokens.get(token);
        if (username == null) return null;
        Authentication.tokens.remove(token);
        return DatabaseHelper.getAccountByName(username);
    }

    public static String generateRandomString(int length){
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < length; i++) {
            sb.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return sb.toString();
    }

    public static String generateOneTimeToken(Account account) {
        String token = Authentication.generateRandomString(32);
        Authentication.tokens.put(token, account.getUsername());
        return token;
    }

}
