package me.deotime.discordwebhook.gui;

import lombok.Getter;
import me.deotime.discordwebhook.data.WebhookManager;
import me.deotime.discordwebhook.gui.elements.WebhookComponent;
import me.deotime.discordwebhook.webhook.DiscordWebhook;
import me.deotime.discordwebhook.webhook.WebhookMessage;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class WebhookControlPanel extends JPanel {

    private static ImageIcon DEFAULT_AVATAR;

    @Getter
    private static final Map<WebhookComponent, WebhookControlPanel> cachedControlPanels = new HashMap<>();

    public static void loadImages() {
        new Thread(() -> {
            try {
                DEFAULT_AVATAR = new ImageIcon(new URL("https://cdn.siasat.com/wp-content/uploads/2021/05/Discord.jpg"));
                DEFAULT_AVATAR.setImage(DEFAULT_AVATAR.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private WebhookComponent component;

    public WebhookControlPanel(WebhookComponent component) {
        cachedControlPanels.put(component, this);
        this.component = component;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setAlignmentX(JComponent.CENTER_ALIGNMENT);
        initGuiElements();
        setVisible(true);
    }

    private void initGuiElements() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setAlignmentX(JComponent.CENTER_ALIGNMENT);

        JLabel webhookNameLabel = new JLabel("Webhook \"" + component.getInfo().getName() + "\"");
        webhookNameLabel.setFont(new Font(webhookNameLabel.getFont().getFontName(), Font.BOLD, 30));
        webhookNameLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        container.add(webhookNameLabel);
        container.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.setAlignmentX(JComponent.CENTER_ALIGNMENT);

        JPanel contentPanel = new JPanel();
        contentPanel.setMaximumSize(new Dimension(99999, 50));
        contentPanel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        JLabel contentLabel = new JLabel("Message Content: ");
        JTextArea contentField = new JTextArea();
        contentField.setColumns(25);
        contentField.setRows(5);
        contentField.setLineWrap(true);
        contentPanel.add(contentLabel);
        contentPanel.add(contentField);

        JPanel extraInfoPanel = new JPanel();
        extraInfoPanel.setLayout(new FlowLayout());
        extraInfoPanel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        extraInfoPanel.setMaximumSize(new Dimension(99999, 150));

        JPanel extraOptionsPanel = new JPanel();
        extraOptionsPanel.setLayout(new BoxLayout(extraOptionsPanel, BoxLayout.Y_AXIS));

        JPanel usernamePanel = new JPanel();
        usernamePanel.setMaximumSize(new Dimension(99999, 50));
        usernamePanel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        JLabel usernameLabel = new JLabel("Username: ");
        JTextField usernameField = new JTextField();
        usernameField.setText(component.getInfo().getUsername());
        usernameField.setColumns(15);
        usernamePanel.add(usernameLabel);
        usernamePanel.add(usernameField);

        JPanel avatarPanel = new JPanel();
        avatarPanel.setMaximumSize(new Dimension(99999, 50));
        avatarPanel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        JLabel avatarLabel = new JLabel("Avatar URL: ");
        JTextField avatarField = new JTextField();
        avatarField.setText(component.getInfo().getAvatarURL());
        avatarField.setColumns(15);
        avatarPanel.add(avatarLabel);
        avatarPanel.add(avatarField);

        JPanel visualPanel = new JPanel();
        visualPanel.setLayout(new BoxLayout(visualPanel, BoxLayout.Y_AXIS));

        JLabel avatarIcon = new JLabel();
        avatarIcon.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        if (component.getInfo().getAvatarURL().isEmpty()) avatarIcon.setIcon(DEFAULT_AVATAR);
        else {
            ImageIcon icon = getIcon(component.getInfo().getAvatarURL());
            avatarIcon.setIcon(icon == null ? DEFAULT_AVATAR : icon);
        }
        avatarField.getDocument().addDocumentListener(new AvatarDocumentAdapter(avatarField, avatarIcon));

        JLabel usernameDisplay = new JLabel(component.getInfo().getUsername().isEmpty() ? "Captain Hook" : component.getInfo().getUsername());
        usernameDisplay.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        usernameField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateUsername();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateUsername();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateUsername();
            }

            private void updateUsername() {
                String username = usernameField.getText();
                if (username == null || username.isEmpty()) {
                    usernameDisplay.setText("Captain Hook");
                    return;
                }
                usernameDisplay.setText(username);
            }
        });

        visualPanel.add(avatarIcon);
        visualPanel.add(usernameDisplay);

        extraOptionsPanel.add(usernamePanel);
        extraOptionsPanel.add(avatarPanel);

        extraInfoPanel.add(extraOptionsPanel);
        extraInfoPanel.add(visualPanel);

        optionsPanel.add(contentPanel);
        optionsPanel.add(extraInfoPanel);

        container.add(optionsPanel);

        JButton sendButton = new JButton("Send");
        sendButton.setFocusPainted(false);
        sendButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);

        JLabel statusLabel = new JLabel();
        statusLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);

        sendButton.addActionListener(e -> {
            if (contentField.getText().isEmpty()) {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Cannot have empty content field.");
                return;
            }
            try {
                DiscordWebhook webhook = DiscordWebhook.createWebhook(new URL(component.getInfo().getUrl()));
                WebhookMessage message = WebhookMessage.builder()
                        .username(usernameField.getText())
                        .avatarURL(avatarField.getText())
                        .content(contentField.getText())
                        .build();
                webhook.sendMessage(message);
                statusLabel.setForeground(Color.GREEN);
                statusLabel.setText("Successfully sent message.");
                component.getInfo().setUsername(usernameField.getText());
                component.getInfo().setAvatarURL(avatarField.getText());
                WebhookManager.getWebhookManager().save();
            } catch (Exception ex) {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("An error occurred while sending message.");
            } finally {
                container.updateUI();
            }
        });

        container.add(sendButton);
        container.add(statusLabel);

        container.setVisible(true);
        add(container);
    }

    private static ImageIcon getIcon(String url) {
        try {
            ImageIcon icon = new ImageIcon(new URL(url));
            if (icon.getIconWidth() > 100 && icon.getIconHeight() > 100)
                icon.setImage(icon.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT));
            return icon;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static class AvatarDocumentAdapter implements DocumentListener {
        private boolean updated = false;

        public AvatarDocumentAdapter(JTextField avatarField, JLabel avatarIcon) {
            new Thread(() -> {

                while (true) {
                    synchronized (this) {
                        if (!updated) continue;
                        updated = false;
                        try {
                            String urlInput = avatarField.getText();
                            // not using function here because need to keep lock off of bool
                            ImageIcon icon = new ImageIcon(new URL(urlInput));
                            if (!updated)
                                icon.setImage(icon.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT));
                            if (!updated) avatarIcon.setIcon(icon);
                            avatarIcon.updateUI();
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                            avatarIcon.setIcon(DEFAULT_AVATAR);
                        }
                    }
                }

            }).start();
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            updated = true;
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            updated = true;
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            updated = true;
        }
    }

}
