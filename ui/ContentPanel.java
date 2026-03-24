package ui;

import java.awt.*;
import javax.swing.*;
import ui.persistentui.Navigator;
import ui.persistentui.ProgressTracker;

public class ContentPanel extends JPanel {

    private CardLayout cardLayout;
    private JPanel cardContainer;
    int step = 0;

    public ContentPanel() {
        setLayout(new BorderLayout());

    ProgressTracker tracker = new ProgressTracker();
    Navigator nav = new Navigator();

    nav.onNext(() -> {
        step++;
        tracker.setStep(step);
        nav.setStep(step);

        // also call your card switch here
    });

    nav.onBack(() -> {
        step--;
        tracker.setStep(step);
        nav.setStep(step);

        // also call your card switch here
    });


        // Top (progress tracker)
        add(tracker, BorderLayout.NORTH);

        // Center (cards)
        cardLayout = new CardLayout();
        cardContainer = new JPanel(cardLayout);
        add(cardContainer, BorderLayout.CENTER);

        // Bottom (navigation buttons)
        add(nav, BorderLayout.SOUTH);
    }

    public void addCard(String name, JPanel panel) {
        cardContainer.add(panel, name);
    }

    public void showCard(String name) {
        cardLayout.show(cardContainer, name);
    }

    public JPanel getCardContainer() {
        return cardContainer;
    }
}