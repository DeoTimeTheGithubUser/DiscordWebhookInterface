package me.deotime.discordwebhook.webhook;

import com.google.gson.JsonObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

public class DiscordWebhook {

    private final URL url;

    private DiscordWebhook(URL url) {
        this.url = url;
    }

    public void sendSimpleMessage(String message) {
        JsonObject obj = new JsonObject();
        obj.addProperty("content", message);
        makeRequest(obj);
    }

    public void sendMessage(WebhookMessage message) {
        JsonObject obj = new JsonObject();

        if (message.getContent() == null || message.getContent().isEmpty())
            throw new IllegalArgumentException("Cannot have empty content field.");

        obj.addProperty("content", message.getContent());
        if (message.getUsername() != null && !message.getUsername().isEmpty())
            obj.addProperty("username", message.getUsername());
        if (message.getAvatarURL() != null && !message.getAvatarURL().isEmpty())
            obj.addProperty("avatar_url", message.getAvatarURL());
        makeRequest(obj);
    }

    private void makeRequest(JsonObject obj) {
        try {
            HttpsURLConnection con = (HttpsURLConnection) this.url.openConnection();
            con.addRequestProperty("Content-Type", "application/json");
            con.addRequestProperty("User-Agent", "Mozilla/5.0");
            con.setDoOutput(true);
            try (OutputStream output = con.getOutputStream()) {
                output.write(obj.toString().getBytes());
            }
            con.getInputStream().close();
            con.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static DiscordWebhook createWebhook(URL webhookURL) {
        return new DiscordWebhook(webhookURL);
    }
}
