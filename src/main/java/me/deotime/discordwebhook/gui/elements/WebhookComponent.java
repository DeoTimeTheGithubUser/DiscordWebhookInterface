package me.deotime.discordwebhook.gui.elements;

import lombok.Getter;
import lombok.Setter;
import me.deotime.discordwebhook.data.WebhookInfo;
import me.deotime.discordwebhook.data.WebhookManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class WebhookComponent extends JPanel {

    private static WebhookComponent selectedComponent;

    @Getter @Setter
    private WebhookInfo info;

    private Runnable selectAction;

    public WebhookComponent(WebhookInfo info) {
        this.info = info;
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setMaximumSize(new Dimension(99999, 30));
        setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        setBackground(Color.LIGHT_GRAY);
        setAlignmentX(JComponent.CENTER_ALIGNMENT);
        initGuiElements();
        initClickListener();
        setVisible(true);
    }

    private void initGuiElements() {
        JLabel nameLabel = new JLabel(info.getName());
        nameLabel.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        add(nameLabel);
    }

    private void initClickListener() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                select();
            }
        });
    }

    public void select() {
        if(selectedComponent != null) selectedComponent.deselect();
        selectedComponent = this;
        setBackground(new Color(129, 230, 230));
        if(this.selectAction != null) selectAction.run();
    }

    public void deselect() {
        setBackground(Color.LIGHT_GRAY);
    }

    public void onSelect(Runnable selectAction) {
        this.selectAction = selectAction;
    }


}
