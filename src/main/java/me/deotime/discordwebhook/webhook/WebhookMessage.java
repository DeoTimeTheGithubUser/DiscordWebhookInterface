package me.deotime.discordwebhook.webhook;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class WebhookMessage {

    private String content;
    private String username;
    private String avatarURL;

}
