package ui;

import java.awt.*;
import javax.swing.*;
import ui.panels.SummaryPanel;
import ui.persistentui.Navigator;
import ui.persistentui.ProgressTracker;

public class ContentPanel extends JPanel {

    private CardLayout cardLayout;
    private JPanel cardContainer;

    private ProgressTracker tracker;
    private Navigator nav;
    private SummaryPanel summaryPanel;

    private int step = 0;

    // 🔑 Step ↔ Card mapping
    private final String[] steps = {
            "WELCOME",
            "PROJECT",
            "DIMENSIONS",
            "LABOR",
            "COST",
            "SUMMARY"
    };

    public ContentPanel(SummaryPanel summaryPanel) {
        setLayout(new BorderLayout());
        this.summaryPanel = summaryPanel;
        // =========================
        // TOP + BOTTOM UI
        // =========================
        tracker = new ProgressTracker();
        nav = new Navigator();

        add(tracker, BorderLayout.NORTH);
        add(nav, BorderLayout.SOUTH);

        // =========================
        // CARD CONTAINER
        // =========================
        cardLayout = new CardLayout();
        cardContainer = new JPanel(cardLayout);
        add(cardContainer, BorderLayout.CENTER);

        // =========================
        // NAVIGATION LOGIC
        // =========================
        nav.onNext(() -> {
            if (step < steps.length - 1) {
                step++;
                updateUIState();
            }
        });

        nav.onBack(() -> {
            if (step > 0) {
                step--;
                updateUIState();
            }
        });

        // =========================
        // INITIAL STATE
        // =========================
        updateUIState();
    }

    // =========================
    // CORE UPDATE METHOD
    // =========================
    private void updateUIState() {
        tracker.setStep(step);
        nav.setStep(step);

        cardLayout.show(cardContainer, steps[step]);

        if (steps[step].equals("SUMMARY"))
        {
            this.summaryPanel.renderSummary();
        }

        // Debug (optional)
        //System.out.println("Step: " + step + " → " + steps[step]);
    }

    // =========================
    // CARD MANAGEMENT
    // =========================
    public void addCard(String name, JPanel panel) {
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(null); // cleaner look
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        cardContainer.add(scrollPane, name);
        revalidate();
    }

}