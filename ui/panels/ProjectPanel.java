package ui.panels;

import java.awt.*;
import javax.swing.*;

public class ProjectPanel extends JPanel {

    private JTextField projectNameField;
    private JTextField locationField;
    private JTextField clientNameField;
    private JTextField clientPhoneField;
    private JTextField clientEmailField;
    private JTextArea notesArea;

    public ProjectPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));
        setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Section title ---
        JLabel sectionTitle = new JLabel("Project Details");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        sectionTitle.setForeground(new Color(30, 30, 30));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(sectionTitle, gbc);
        gbc.gridwidth = 1;

        // Project Name
        gbc.gridy = 1; gbc.gridx = 0; gbc.weightx = 0.3;
        formPanel.add(makeLabel("Project Name:"), gbc);
        projectNameField = makeTextField("e.g. Warehouse Slab - Downtown");
        gbc.gridx = 1; gbc.weightx = 0.7;
        formPanel.add(projectNameField, gbc);

        // Location
        gbc.gridy = 2; gbc.gridx = 0; gbc.weightx = 0.3;
        formPanel.add(makeLabel("Location / Address:"), gbc);
        locationField = makeTextField("e.g. 123 Industrial Blvd, Raleigh, NC");
        gbc.gridx = 1; gbc.weightx = 0.7;
        formPanel.add(locationField, gbc);

        // Separator
        JSeparator sep = new JSeparator();
        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2; gbc.insets = new Insets(16, 8, 4, 8);
        formPanel.add(sep, gbc);
        gbc.gridwidth = 1; gbc.insets = new Insets(8, 8, 8, 8);

        // Client section sub-title
        JLabel clientTitle = new JLabel("Client Information");
        clientTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        clientTitle.setForeground(new Color(60, 60, 60));
        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 2;
        formPanel.add(clientTitle, gbc);
        gbc.gridwidth = 1;

        // Client Name
        gbc.gridy = 5; gbc.gridx = 0;
        formPanel.add(makeLabel("Client Name:"), gbc);
        clientNameField = makeTextField("e.g. Jim Henderson");
        gbc.gridx = 1;
        formPanel.add(clientNameField, gbc);

        // Client Phone
        gbc.gridy = 6; gbc.gridx = 0;
        formPanel.add(makeLabel("Phone:"), gbc);
        clientPhoneField = makeTextField("e.g. (919) 555-0100");
        gbc.gridx = 1;
        formPanel.add(clientPhoneField, gbc);

        // Client Email
        gbc.gridy = 7; gbc.gridx = 0;
        formPanel.add(makeLabel("Email:"), gbc);
        clientEmailField = makeTextField("e.g. jim@hendersonconstruction.com");
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
    }

    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(new Color(50, 50, 50));
        return lbl;
    }

    private JTextField makeTextField(String placeholder) {
        JTextField field = new JTextField(20);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setForeground(new Color(30, 30, 30));
        // Simple placeholder via focus listener
        field.setText(placeholder);
        field.setForeground(Color.GRAY);
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(new Color(30, 30, 30));
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

    // --- Getters ---
    public String getProjectName() {
        String v = projectNameField.getText().trim();
        return v.startsWith("e.g.") ? "" : v;
    }
    public String getProjectLocation() {
        String v = locationField.getText().trim();
        return v.startsWith("e.g.") ? "" : v;
    }
    public String getClientName() {
        String v = clientNameField.getText().trim();
        return v.startsWith("e.g.") ? "" : v;
    }
    public String getClientPhone() {
        String v = clientPhoneField.getText().trim();
        return v.startsWith("e.g.") ? "" : v;
    }
    public String getClientEmail() {
        String v = clientEmailField.getText().trim();
        return v.startsWith("e.g.") ? "" : v;
    }
    public String getNotes() {
        return notesArea.getText().trim();
    }
}
