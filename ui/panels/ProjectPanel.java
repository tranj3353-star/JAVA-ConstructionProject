package ui.panels;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import models.InfoManager;

public class ProjectPanel extends JPanel {

    private JTextField projectNameField;
    private JTextField locationField;
    private JTextField clientNameField;
    private JTextField clientPhoneField;
    private JTextField clientEmailField;
    private JTextArea notesArea;

    private InfoManager info = InfoManager.getInstance();

    public ProjectPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));
        setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel sectionTitle = new JLabel("Project Details");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(sectionTitle, gbc);
        gbc.gridwidth = 1;

        // Project Name
        gbc.gridy = 1; gbc.gridx = 0;
        formPanel.add(makeLabel("Project Name:"), gbc);
        projectNameField = makeTextField("e.g. Warehouse Slab - Downtown");
        gbc.gridx = 1;
        formPanel.add(projectNameField, gbc);

        // Location
        gbc.gridy = 2; gbc.gridx = 0;
        formPanel.add(makeLabel("Location / Address:"), gbc);
        locationField = makeTextField("e.g. 123 Industrial Blvd, Raleigh, NC");
        gbc.gridx = 1;
        formPanel.add(locationField, gbc);

        // Separator
        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(16, 8, 4, 8);
        formPanel.add(new JSeparator(), gbc);
        gbc.gridwidth = 1; gbc.insets = new Insets(8, 8, 8, 8);

        JLabel clientTitle = new JLabel("Client Information");
        clientTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 2;
        formPanel.add(clientTitle, gbc);
        gbc.gridwidth = 1;

        // Client Name
        gbc.gridy = 5; gbc.gridx = 0;
        formPanel.add(makeLabel("Client Name:"), gbc);
        clientNameField = makeTextField("e.g. Jim Henderson");
        gbc.gridx = 1;
        formPanel.add(clientNameField, gbc);

        // Phone
        gbc.gridy = 6; gbc.gridx = 0;
        formPanel.add(makeLabel("Phone:"), gbc);
        clientPhoneField = makeTextField("e.g. (919) 555-0100");
        gbc.gridx = 1;
        formPanel.add(clientPhoneField, gbc);

        // Email
        gbc.gridy = 7; gbc.gridx = 0;
        formPanel.add(makeLabel("Email:"), gbc);
        clientEmailField = makeTextField("e.g. jim@company.com");
        gbc.gridx = 1;
        formPanel.add(clientEmailField, gbc);

        // Notes
        gbc.gridy = 8; gbc.gridx = 0;
        formPanel.add(makeLabel("Notes:"), gbc);
        notesArea = new JTextArea(4, 20);
        notesArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        JScrollPane notesScroll = new JScrollPane(notesArea);
        notesScroll.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        gbc.gridx = 1;
        formPanel.add(notesScroll, gbc);

        add(formPanel, BorderLayout.CENTER);

        // 🔥 HOOK INTO SINGLETON
        hookListeners();
    }

    // =========================
    // 🔥 LISTENERS → UPDATE SINGLETON
    // =========================
    private void hookListeners() {

        addDocListener(projectNameField, () ->
            info.projectName = clean(projectNameField.getText())
        );

        addDocListener(locationField, () ->
            info.projectLocation = clean(locationField.getText())
        );

        addDocListener(clientNameField, () ->
            info.projectClient = clean(clientNameField.getText())
        );

        // Optional fields (only if you add them to InfoManager later)
        addDocListener(clientPhoneField, () -> {
            // info.clientPhone = clean(clientPhoneField.getText());
        });

        addDocListener(clientEmailField, () -> {
            // info.clientEmail = clean(clientEmailField.getText());
        });

        notesArea.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { update(); }
            public void removeUpdate(DocumentEvent e) { update(); }
            public void changedUpdate(DocumentEvent e) { update(); }

            private void update() {
                // info.notes = notesArea.getText().trim();
            }
        });
    }

    private void addDocListener(JTextField field, Runnable action) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { action.run(); }
            public void removeUpdate(DocumentEvent e) { action.run(); }
            public void changedUpdate(DocumentEvent e) { action.run(); }
        });
    }

    private String clean(String text) {
        if (text == null) return "";
        text = text.trim();
        return text.startsWith("e.g.") ? "" : text;
    }

    // =========================
    // UI HELPERS (unchanged)
    // =========================
    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return lbl;
    }

    private JTextField makeTextField(String placeholder) {
        JTextField field = new JTextField(20);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        field.setText(placeholder);
        field.setForeground(Color.GRAY);

        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                }
            }
        });

        return field;
    }
}