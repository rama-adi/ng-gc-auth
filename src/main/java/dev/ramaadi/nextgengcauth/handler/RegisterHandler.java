package dev.ramaadi.nextgengcauth.handler;

import com.google.gson.Gson;
import dev.ramaadi.nextgengcauth.json.RegisterAccount;
import emu.grasscutter.Grasscutter;
import emu.grasscutter.database.DatabaseHelper;
import emu.grasscutter.game.Account;
import express.http.HttpContextHandler;
import express.http.Request;
import express.http.Response;
import dev.ramaadi.nextgengcauth.json.AuthResponseJson;

import java.io.IOException;

public class RegisterHandler implements HttpContextHandler {
    @Override
    public void handle(Request request, Response response) throws IOException {
        AuthResponseJson authResponse = new AuthResponseJson();

        try {
            String requestBody = request.ctx().body();
            if (requestBody.isEmpty()) {
                authResponse.success = false;
                authResponse.message = "EMPTY_BODY"; // ENG = "No data was sent with the request"
                authResponse.token = "";
            }else{
                RegisterAccount registerAccount = new Gson().fromJson(requestBody, RegisterAccount.class);
                if(DatabaseHelper.getAccountByName(registerAccount.username) != null){
                    authResponse.success = false;
                    authResponse.message = "USERNAME_TAKEN";
                    authResponse.token = "";
                } else {
                    DatabaseHelper.createAccount(registerAccount.username);
                    Account account = DatabaseHelper.getAccountByName(registerAccount.username);
                    account.setPassword(registerAccount.password);
                    account.save();
                    authResponse.success = true;
                    authResponse.message = "";
                    authResponse.token = account.getId(); // reuse for sending UID
                }
            }
        } catch (Exception e) {
            authResponse.success = false;
            authResponse.message = "UNKNOWN"; // ENG = "An unknown error has occurred..."
            authResponse.token = "";
            Grasscutter.getLogger().error("[Dispatch] An error occurred while creating an account.");
            e.printStackTrace();
        }

        response.send(authResponse);
    }
}
