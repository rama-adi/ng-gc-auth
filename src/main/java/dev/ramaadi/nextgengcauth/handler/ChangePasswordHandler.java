package dev.ramaadi.nextgengcauth.handler;

import dev.ramaadi.nextgengcauth.json.AuthResponseJson;
import express.http.HttpContextHandler;
import express.http.Request;
import express.http.Response;

import java.io.IOException;

public class ChangePasswordHandler implements HttpContextHandler {

    // Change password is disabled
    @Override
    public void handle(Request request, Response response) throws IOException {
        AuthResponseJson authResponse = new AuthResponseJson();
        authResponse.success = false;
        authResponse.message = "DISABLED"; // ENG = "An unknown error has occurred..."
        authResponse.token = "";
        response.send(authResponse);
    }
}
