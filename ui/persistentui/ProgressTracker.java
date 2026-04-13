package ui.persistentui;

import java.awt.*;
import javax.swing.*;

public class ProgressTracker extends JPanel {

    // =========================
    // 🎨 COLORS (EDIT THESE)
    // =========================
    private final Color ACTIVE_COLOR = new Color(66, 135, 245);
    private final Color INACTIVE_COLOR = new Color(200, 200, 200);
    private final Color TEXT_COLOR = Color.BLACK;
    private final Color BACKGROUND_COLOR = Color.WHITE;

    // =========================
    // ⚙️ STATE
    // =========================
    private String[] steps = {
            "Welcome", "Project", "Dimensions", "Labor", "Cost", "Summary"
    };

    private int currentStep = 0;

    public ProgressTracker() {
        setPreferredSize(new Dimension(0, 70));
        setBackground(BACKGROUND_COLOR);
    }

    public void setStep(int step) {
        this.currentStep = step;
        repaint();
    }

    public int getStep() {
        return currentStep;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        int circleSize = 20;
        int spacing = width / (steps.length + 1);
        int y = height / 2;

        for (int i = 0; i < steps.length; i++) {

            int x = spacing * (i + 1);

            // Draw line (except last)
            if (i < steps.length - 1) {
                g2.setColor(i < currentStep ? ACTIVE_COLOR : INACTIVE_COLOR);
                int nextX = spacing * (i + 2);
                g2.fillRect(x + circleSize / 2, y - 2,
                        nextX - x - circleSize, 4);
            }

            // Draw circle
            if (i <= currentStep) {
                g2.setColor(ACTIVE_COLOR);
            } else {
                g2.setColor(INACTIVE_COLOR);
            }

            g2.fillOval(x - circleSize / 2, y - circleSize / 2,
                    circleSize, circleSize);

            // Step label
            g2.setColor(TEXT_COLOR);
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(steps[i]);

            g2.drawString(steps[i],
                    x - textWidth / 2,
                    y + 25);
        }
    }
}
