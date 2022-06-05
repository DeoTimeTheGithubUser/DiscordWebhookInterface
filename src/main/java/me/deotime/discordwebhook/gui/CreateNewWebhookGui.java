package me.deotime.discordwebhook.gui;

import me.deotime.discordwebhook.data.WebhookInfo;

import javax.swing.*;
import java.awt.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class CreateNewWebhookGui extends JFrame {

    private Consumer<WebhookInfo> submitAction;

    public CreateNewWebhookGui() {
        super("Create new Webhook");
        setSize(500, 150);
        initGuiElements();
        setVisible(true);
    }

    private void initGuiElements() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        JLabel infoLabel = new JLabel("Create new Webhook");
        infoLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        container.add(infoLabel);

        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new FlowLayout());

        JPanel namePanel = new JPanel();
        namePanel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        namePanel.setLayout(new FlowLayout());
        JLabel nameLabel = new JLabel("Name: ");
        JTextField nameField = new JTextField();
        nameField.setColumns(15);
        namePanel.add(nameLabel);
        namePanel.add(nameField);

        JPanel urlPanel = new JPanel();
        urlPanel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        urlPanel.setLayout(new FlowLayout());
        JLabel urlLabel = new JLabel("URL: ");
        JTextField urlField = new JTextField();
        urlField.setColumns(15);
        urlPanel.add(urlLabel);
        urlPanel.add(urlField);

        JButton createButton = new JButton("Create");
        createButton.setFocusPainted(false);
        createButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        createButton.addActionListener(e -> {
            dispose();
            if(submitAction == null) return;
            submitAction.accept(new WebhookInfo(nameField.getText(), urlField.getText(), "", ""));
        });

        optionsPanel.add(namePanel);
        optionsPanel.add(urlPanel);

        container.add(optionsPanel);
        container.add(createButton);
        container.add(Box.createRigidArea(new Dimension(0, 25)));
        container.setVisible(true);

        add(container);
    }

    public void onSubmit(Consumer<WebhookInfo> submitAction) {
        this.submitAction = submitAction;
    }
}
