package dev.ramaadi.nextgengcauth;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import emu.grasscutter.Grasscutter;
import emu.grasscutter.plugin.Plugin;
import dev.ramaadi.nextgengcauth.handler.NGGCAuthAuthenticationHandler;
import dev.ramaadi.nextgengcauth.utils.Authentication;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;


public class NextgenGCAuth extends Plugin {
    private static Config config;
    private File configFile;
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public void onEnable() {
        configFile = new File(getDataFolder().toPath()+ "/config.json");
        if (!configFile.exists()) {
            try {
                Files.createDirectories(configFile.toPath().getParent());
            } catch (IOException e) {
                Grasscutter.getLogger().error("[NG-GCAuth] Failed to create config.json");
            }
        }
        loadConfig();
        if(Grasscutter.getDispatchServer().registerAuthHandler(new NGGCAuthAuthenticationHandler())) {
            Grasscutter.getLogger().info("[NG-GCAuth] Authentication Enabled!");
            Grasscutter.getLogger().info("[NG-GCAuth] This is a forked version of GCAuth for use with the web component");
            if(Grasscutter.getConfig().getDispatchOptions().AutomaticallyCreateAccounts) {
                Grasscutter.getLogger().warn("[NG-GCAuth] Auth does not support automatic account creation. Please disable in the server's config.json or just ignore this warning.");
            }
        } else {
            Grasscutter.getLogger().error("[NG-GCAuth] Auth could not be enabled");
        }
    }

    @Override
    public void onDisable() {
        if(Grasscutter.getDispatchServer().getAuthHandler().getClass().equals(NGGCAuthAuthenticationHandler.class)) {
            Grasscutter.getDispatchServer().resetAuthHandler();
        }
    }

    public  void loadConfig() {
        String secretKey = Authentication.generateRandomString(50);

        try (FileReader file = new FileReader(configFile)) {
            config = gson.fromJson(file,Config.class);
            if(Objects.equals(config.SecretAuthKey, "")){
                config.SecretAuthKey = secretKey;
                Grasscutter.getLogger().warn("[NG-GCAuth] No Secret key detected! Generated the secret key.");
                Grasscutter.getLogger().warn("[NG-GCAuth] Use this key for the web app: {}", secretKey);
            }
            saveConfig();
        } catch (Exception e) {
            config = new Config();
            config.SecretAuthKey = secretKey;
            saveConfig();
        }
    }

    public void saveConfig() {
        try (FileWriter file = new FileWriter(configFile)) {
            file.write(gson.toJson(config));
        } catch (Exception e) {
            Grasscutter.getLogger().error("[NG-GCAuth] Unable to save config file.");
        }
    }
    public static Config getConfig() {return config;}
}
