package me.deotime.discordwebhook.data;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WebhookInfo {
    private String name;
    private String url;

    private String username;
    private String avatarURL;
}
