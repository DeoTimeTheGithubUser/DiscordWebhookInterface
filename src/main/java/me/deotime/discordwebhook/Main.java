package me.deotime.discordwebhook;

import me.deotime.discordwebhook.data.WebhookManager;
import me.deotime.discordwebhook.gui.WebhookControlPanel;
import me.deotime.discordwebhook.gui.WebhookGui;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        WebhookManager.init();
        WebhookGui.initWebhookGui();
        WebhookControlPanel.loadImages();
    }

}
