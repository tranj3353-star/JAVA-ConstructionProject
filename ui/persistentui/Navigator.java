package ui.persistentui;

import java.awt.*;
import javax.swing.*;

public class Navigator extends JPanel {

    // =========================
    // 🎨 COLORS
    // =========================
    private final Color BUTTON_COLOR = new Color(66, 135, 245);
    private final Color BUTTON_TEXT_COLOR = Color.WHITE;
    private final Color BACKGROUND_COLOR = Color.WHITE;

    // =========================
    // ⚙️ STATE
    // =========================
    private JButton backButton;
    private JButton nextButton;

    private int currentStep = 0;
    private int maxSteps = 6;

    public Navigator() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(0, 60));
        setBackground(BACKGROUND_COLOR);

        initComponents();
    }

    private void initComponents() {
        backButton = createButton("Back");
        nextButton = createButton("Next");

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        left.setOpaque(false);
        right.setOpaque(false);

        left.add(backButton);
        right.add(nextButton);

        add(left, BorderLayout.WEST);
        add(right, BorderLayout.EAST);

        updateButtons();
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setBackground(BUTTON_COLOR);
        btn.setForeground(BUTTON_TEXT_COLOR);
        return btn;
    }

    // =========================
    // 🔗 PUBLIC CONTROL
    // =========================

    public void setStep(int step) {
        this.currentStep = step;
        updateButtons();
    }

    public void setMaxSteps(int maxSteps) {
        this.maxSteps = maxSteps;
    }

    public void onNext(Runnable action) {
        nextButton.addActionListener(e -> action.run());
    }

    public void onBack(Runnable action) {
        backButton.addActionListener(e -> action.run());
    }

    private void updateButtons() {
        backButton.setEnabled(currentStep > 0);
        nextButton.setEnabled(currentStep < maxSteps);
    }
}
