package dev.ramaadi.nextgengcauth.handler;


import dev.ramaadi.nextgengcauth.utils.HMAC;
import emu.grasscutter.Grasscutter;
import emu.grasscutter.game.Account;
import emu.grasscutter.server.dispatch.authentication.AuthenticationHandler;
import emu.grasscutter.server.dispatch.json.LoginAccountRequestJson;
import emu.grasscutter.server.dispatch.json.LoginResultJson;
import express.http.Request;
import express.http.Response;
import dev.ramaadi.nextgengcauth.utils.Authentication;

import java.io.IOException;

public class NGGCAuthAuthenticationHandler implements AuthenticationHandler {

    @Override
    public void handleLogin(Request req, Response res) {

        try {
            if (!HMAC.verifyHmacFromRequest(req)) res.send(HMAC.hmacVerificationErrorJson());
            else new LoginHandler().handle(req, res);
        } catch (IOException e) {
            Grasscutter.getLogger().warn("[NG-GCAuth] Unable to handle login request");
            e.printStackTrace();
        }
    }

    @Override
    public void handleRegister(Request req, Response res) {


        try {
            if (!HMAC.verifyHmacFromRequest(req)) res.send(HMAC.hmacVerificationErrorJson());
            else new RegisterHandler().handle(req, res);
        } catch (IOException e) {
            Grasscutter.getLogger().warn("[NG-GCAuth] Unable to handle register request");
            e.printStackTrace();
        }
    }

    @Override
    public void handleChangePassword(Request req, Response res) {


        try {
            if (!HMAC.verifyHmacFromRequest(req)) res.send(HMAC.hmacVerificationErrorJson());
            else new ChangePasswordHandler().handle(req, res);
        } catch (IOException e) {
            Grasscutter.getLogger().warn("[NG-GCAuth] Unable to handle change password request");
            e.printStackTrace();
        }
    }

    @Override
    public LoginResultJson handleGameLogin(Request request, LoginAccountRequestJson requestData) {
        LoginResultJson responseData = new LoginResultJson();

        // Login
        Account account = Authentication.getAccountByOneTimeToken(requestData.account);
        if (account == null) {
            Grasscutter.getLogger().info("[NG-GCAuth] Client " + request.ip() + " failed to log in");
            responseData.retcode = -201;
            responseData.message = "Token is invalid";
            return responseData;
        }

        // Account was found, log the player in
        responseData.message = "OK";
        responseData.data.account.uid = account.getId();
        responseData.data.account.token = account.generateSessionKey();
        responseData.data.account.email = account.getEmail();

        Grasscutter.getLogger().info(String.format("[NG-GCAuth] Client %s logged in as %s", request.ip(), responseData.data.account.uid));

        return responseData;
    }
}
