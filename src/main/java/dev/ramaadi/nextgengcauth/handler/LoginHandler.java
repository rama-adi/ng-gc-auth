package dev.ramaadi.nextgengcauth.handler;

import com.google.gson.Gson;
import dev.ramaadi.nextgengcauth.json.AuthResponseJson;
import dev.ramaadi.nextgengcauth.json.LoginGenerateToken;
import dev.ramaadi.nextgengcauth.utils.Authentication;
import emu.grasscutter.Grasscutter;
import emu.grasscutter.game.Account;
import express.http.HttpContextHandler;
import express.http.Request;
import express.http.Response;

import java.io.IOException;

public class LoginHandler implements HttpContextHandler {
    @Override
    public void handle(Request request, Response response) throws IOException {
        AuthResponseJson authResponse = new AuthResponseJson();

        try {
            String requestBody = request.ctx().body();
            if (requestBody.isEmpty()) {
                authResponse.success = false;
                authResponse.message = "EMPTY_BODY"; // ENG = "No data was sent with the request"
                authResponse.token = "";
            } else {
                LoginGenerateToken loginGenerateToken = new Gson().fromJson(requestBody, LoginGenerateToken.class);
                Account account = Authentication.getAccountByUsernameAndPassword(loginGenerateToken.username, loginGenerateToken.password);
                if (account == null) {
                    authResponse.success = false;
                    authResponse.message = "INVALID_ACCOUNT"; // ENG = "Invalid username or password"
                    authResponse.token = "";
                } else {
                    if (account.getPassword() != null && !account.getPassword().isEmpty()) {
                        authResponse.success = true;
                        authResponse.message = "";
                        authResponse.token = Authentication.generateOneTimeToken(account);
                    } else {
                        authResponse.success = false;
                        authResponse.message = "NO_PASSWORD"; // ENG = "There is no account password set. Please create a password by resetting it."
                        authResponse.token = "";
                    }
                }
            }
        } catch (Exception e) {
            authResponse.success = false;
            authResponse.message = "UNKNOWN"; // ENG = "An unknown error has occurred..."
            authResponse.token = "";
            Grasscutter.getLogger().error("[Dispatch] An error occurred while a user was logging in.");
            e.printStackTrace();
        }

        response.send(authResponse);
    }
}
