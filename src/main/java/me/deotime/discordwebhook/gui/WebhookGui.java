package me.deotime.discordwebhook.gui;

import me.deotime.discordwebhook.data.WebhookInfo;
import me.deotime.discordwebhook.data.WebhookManager;
import me.deotime.discordwebhook.gui.elements.WebhookComponent;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.function.Function;

public class WebhookGui extends JFrame {

    private JPanel contentPanel;
    private JPanel webhooksContainer;
    private JPanel webhooksPanel;
    private JButton createNewWebhookButton;

    public WebhookGui() {
        super("Discord Webhook Interface");
        setSize(800, 500);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        initGuiElements();
        setVisible(true);
    }

    private void initGuiElements() {
        this.contentPanel = new JPanel();
        contentPanel.setSize(getSize());
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.X_AXIS));

        this.webhooksContainer = new JPanel();
        webhooksContainer.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        webhooksContainer.setLayout(new BoxLayout(webhooksContainer, BoxLayout.Y_AXIS));
        webhooksContainer.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        webhooksContainer.setMinimumSize(new Dimension(150, 0));
        webhooksContainer.setMaximumSize(new Dimension(150, 99999));

        JPanel controlPanelContainer = new JPanel();
        controlPanelContainer.setLayout(new GridBagLayout());

        JPanel defaultControlPanel = createDefaultControlPanel();
        defaultControlPanel.setAlignmentX(JComponent.LEFT_ALIGNMENT);

        Component filler1 = Box.createRigidArea(new Dimension(0, 10));
        Component filler2 = Box.createRigidArea(new Dimension(150, 0));
        webhooksContainer.add(filler1);
        webhooksContainer.add(filler2);

        WebhookInfo[] webhooks = WebhookManager.getWebhookManager().getAllWebhookInfo();
        this.webhooksPanel = new JPanel();
        webhooksPanel.setLayout(new BoxLayout(webhooksPanel, BoxLayout.Y_AXIS));
        webhooksPanel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        JLabel noWebhooks = new JLabel("No webhooks.");
        if(webhooks == null) webhooksPanel.add(noWebhooks);
        webhooksContainer.add(webhooksPanel);
        webhooksContainer.add(Box.createRigidArea(new Dimension(0, 10)));

        Function<WebhookInfo, WebhookComponent> addNewWebhook = info -> {
            webhooksPanel.remove(noWebhooks);
            webhooksContainer.remove(filler1);
            WebhookComponent webhookComponent = new WebhookComponent(info);
            webhookComponent.onSelect(() -> {
                controlPanelContainer.removeAll();
                controlPanelContainer.setLayout(new BoxLayout(controlPanelContainer, BoxLayout.Y_AXIS));
                controlPanelContainer.add(WebhookControlPanel.getCachedControlPanels().containsKey(webhookComponent)
                        ? WebhookControlPanel.getCachedControlPanels().get(webhookComponent)
                        : new WebhookControlPanel(webhookComponent));
                controlPanelContainer.updateUI();
            });
            webhooksPanel.add(webhookComponent);
            webhooksPanel.updateUI();
            webhooksContainer.updateUI();
            return webhookComponent;
        };

        if(webhooks != null) Arrays.stream(webhooks).forEach(addNewWebhook::apply);

        this.createNewWebhookButton = new JButton("Create new");
        createNewWebhookButton.setFocusPainted(false);
        createNewWebhookButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        createNewWebhookButton.addActionListener(e -> {
            CreateNewWebhookGui createNew = new CreateNewWebhookGui();
            createNew.onSubmit(info -> {
                WebhookManager.getWebhookManager().add(info);
                WebhookComponent comp = addNewWebhook.apply(info);
                comp.select();
            });
        });
        webhooksContainer.add(createNewWebhookButton);

        contentPanel.add(webhooksContainer);
        controlPanelContainer.add(defaultControlPanel);
        contentPanel.add(controlPanelContainer);

        contentPanel.setVisible(true);
        add(contentPanel);
    }

    private JPanel createDefaultControlPanel() {
        JPanel defaultPanel = new JPanel();
        defaultPanel.setLayout(new GridBagLayout());
        JLabel defaultText = new JLabel("No Webhook selected.");
        defaultText.setFont(new Font(defaultText.getFont().getFontName(), Font.BOLD, 25));
        defaultPanel.add(defaultText);
        defaultPanel.setVisible(true);
        return defaultPanel;
    }

    private static WebhookGui instance;

    public static void initWebhookGui() {
        instance = new WebhookGui();
    }
}
