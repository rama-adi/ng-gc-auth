package dev.ramaadi.nextgengcauth.utils;

import dev.ramaadi.nextgengcauth.NextgenGCAuth;
import emu.grasscutter.Grasscutter;
import dev.ramaadi.nextgengcauth.json.AuthResponseJson;
import express.http.Request;


import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

import static emu.grasscutter.utils.Utils.bytesToHex;

public final class HMAC {

    private static final String algo = NextgenGCAuth.getConfig().HashAlgorithm;
    private static final String key = NextgenGCAuth.getConfig().SecretAuthKey;

    public static String hash(
            String data,
            String key
    ) throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), algo);
        Mac mac = Mac.getInstance(algo);
        mac.init(secretKeySpec);
        return bytesToHex(mac.doFinal(data.getBytes()));
    }

    public static boolean verify(
            String hash,
            String data,
            String key
    ) throws NoSuchAlgorithmException {
        try {
            return hash(data, key).equals(hash);
        } catch (InvalidKeyException ignored) {
            return false;
        }
    }

    public static boolean verifyHmacFromRequest(Request request) {
        if (Integer.parseInt(request.get("X-AUTH-TIMESTAMP")) > (Instant.now().getEpochSecond() + 20)) {
            Grasscutter.getLogger().warn("[GCAUTH] Endpoint hit with more than tolerable delay. Got {}", request.get("X-AUTH-TIMESTAMP"));
            return false;
        } else {
            try {
                boolean verified = verify(
                        request.get("X-AUTH-HASH"),
                        request.ctx().body(),
                        (key + request.get("X-AUTH-TIMESTAMP"))
                );

                if (verified) {
                    if (NextgenGCAuth.getConfig().VerboseOutput)
                        Grasscutter.getLogger().info("Successful API HMAC Signature verification on {}", request.path());
                    return true;
                } else {

                    if (NextgenGCAuth.getConfig().VerboseOutput) {
                        Grasscutter.getLogger().warn("Failed API HMAC Signature verification on {}", request.path());
                        Grasscutter.getLogger().warn("Expected: {}", verify(
                                request.get("X-AUTH-HASH"),
                                request.ctx().body(),
                                (key + request.get("X-AUTH-TIMESTAMP"))
                        ));
                        Grasscutter.getLogger().warn("Got: {}", request.get("X-AUTH-HASH"));
                    }

                    return false;
                }

            } catch (NoSuchAlgorithmException e) {
                return false;
            }
        }

    }

    public static AuthResponseJson hmacVerificationErrorJson() {
        AuthResponseJson authResponseJson = new AuthResponseJson();
        authResponseJson.success = false;
        authResponseJson.message = "SIGNATURE_MISMATCH";
        authResponseJson.token = "";
        return authResponseJson;
    }
}
