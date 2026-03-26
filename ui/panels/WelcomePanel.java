package ui.panels;

import java.awt.*;
import javax.swing.*;

public class WelcomePanel extends JPanel {

    public WelcomePanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(60, 80, 60, 80));

        // Icon / logo area
        JLabel iconLabel = new JLabel("\uD83C\uDFD7", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Title
        JLabel titleLabel = new JLabel("Concrete Pad Estimator");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(30, 30, 30));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Professional estimates for concrete pad construction projects");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitleLabel.setForeground(new Color(100, 100, 100));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Divider
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(500, 2));
        sep.setForeground(new Color(200, 200, 200));

        // Description
        JTextArea descArea = new JTextArea(
            "This application will guide you through:\n\n" +
            "  \u2022  Project details (name, location)\n" +
            "  \u2022  Work area dimensions\n" +
            "  \u2022  Slab type and thickness\n" +
            "  \u2022  Labor estimation\n" +
            "  \u2022  Cost calculations with optional discounts\n\n" +
            "Click Next to begin your estimate."
        );
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descArea.setForeground(new Color(60, 60, 60));
        descArea.setOpaque(false);
        descArea.setEditable(false);
        descArea.setFocusable(false);
        descArea.setAlignmentX(Component.CENTER_ALIGNMENT);
        descArea.setMaximumSize(new Dimension(520, 200));

        centerPanel.add(iconLabel);
        centerPanel.add(Box.createVerticalStrut(16));
        centerPanel.add(titleLabel);
        centerPanel.add(Box.createVerticalStrut(8));
        centerPanel.add(subtitleLabel);
        centerPanel.add(Box.createVerticalStrut(24));
        centerPanel.add(sep);
        centerPanel.add(Box.createVerticalStrut(24));
        centerPanel.add(descArea);

        add(centerPanel, BorderLayout.CENTER);
    }
}
