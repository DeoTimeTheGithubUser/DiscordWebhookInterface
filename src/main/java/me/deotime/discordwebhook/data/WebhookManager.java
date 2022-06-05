package me.deotime.discordwebhook.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WebhookManager {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final File webhookFile;
    private List<WebhookInfo> webhookInfos;

    public WebhookManager() {
        this.webhookFile = new File(WebhookManager.class.getClassLoader().getResource("webhooks.json").getFile());
        loadWebhookInfo();
    }

    private void loadWebhookInfo() {
        webhookInfos = new ArrayList<>();
        try {
            WebhookInfo[] webhooks = gson.fromJson(new FileReader(webhookFile), WebhookInfo[].class);
            if (webhooks == null) return;
            webhookInfos.addAll(Arrays.asList(webhooks));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try (FileWriter writer = new FileWriter(webhookFile)) {
            String data = gson.toJson(getAllWebhookInfo());
            writer.write(data);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void add(WebhookInfo info) {
        webhookInfos.add(info);
        save();
    }

    public WebhookInfo[] getAllWebhookInfo() {
        return webhookInfos == null || webhookInfos.isEmpty() ? null : webhookInfos.toArray(WebhookInfo[]::new);
    }


    @Getter
    private static WebhookManager webhookManager;

    public static void init() {
        webhookManager = new WebhookManager();
    }
}
