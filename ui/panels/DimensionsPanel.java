package ui.panels;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class DimensionsPanel extends JPanel {

    private JTextField lengthField;
    private JTextField widthField;
    private JLabel areaResultLabel;

    public DimensionsPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));
        setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 8, 10, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel title = new JLabel("Work Area Dimensions");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(30, 30, 30));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3;
        formPanel.add(title, gbc);

        JLabel subtitle = new JLabel("Enter the length and width of the concrete pad in feet.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(new Color(110, 110, 110));
        gbc.gridy = 1;
        formPanel.add(subtitle, gbc);
        gbc.gridwidth = 1;

        // Length
        gbc.gridy = 2; gbc.gridx = 0; gbc.weightx = 0.3;
        formPanel.add(makeLabel("Length (ft):"), gbc);
        lengthField = new JTextField(10);
        styleField(lengthField);
        gbc.gridx = 1; gbc.weightx = 0.4;
        formPanel.add(lengthField, gbc);
        gbc.gridx = 2; gbc.weightx = 0.3;
        formPanel.add(makeLabel("feet"), gbc);

        // Width
        gbc.gridy = 3; gbc.gridx = 0;
        formPanel.add(makeLabel("Width (ft):"), gbc);
        widthField = new JTextField(10);
        styleField(widthField);
        gbc.gridx = 1;
        formPanel.add(widthField, gbc);
        gbc.gridx = 2;
        formPanel.add(makeLabel("feet"), gbc);

        // 🔹 ADDED: Auto-calc listener
        DocumentListener autoCalcListener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { calculateArea(); }
            public void removeUpdate(DocumentEvent e) { calculateArea(); }
            public void changedUpdate(DocumentEvent e) { calculateArea(); }
        };

        lengthField.getDocument().addDocumentListener(autoCalcListener);
        widthField.getDocument().addDocumentListener(autoCalcListener);


        // Result box
        JPanel resultPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 12));
        resultPanel.setBackground(new Color(225, 235, 250));
        resultPanel.setBorder(BorderFactory.createLineBorder(new Color(160, 190, 230)));
        areaResultLabel = new JLabel("Total Area: — sq ft");
        areaResultLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        areaResultLabel.setForeground(new Color(30, 80, 160));
        resultPanel.add(new JLabel("\uD83D\uDCCF"));
        resultPanel.add(areaResultLabel);

        gbc.gridy = 5; gbc.gridx = 0; gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(resultPanel, gbc);

        // Diagram hint label
        JLabel hintLabel = new JLabel("<html><i>Tip: For irregular shapes, break the area into rectangles and add them together.</i></html>");
        hintLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        hintLabel.setForeground(new Color(130, 130, 130));
        gbc.gridy = 6; gbc.gridwidth = 3;
        gbc.insets = new Insets(18, 8, 4, 8);
        formPanel.add(hintLabel, gbc);

        add(formPanel, BorderLayout.CENTER);
    }

    private void calculateArea() {
        try {
            double length = Double.parseDouble(lengthField.getText().trim());
            double width = Double.parseDouble(widthField.getText().trim());
            if (length <= 0 || width <= 0) throw new NumberFormatException();
            double area = length * width;
            areaResultLabel.setText(String.format("Total Area: %.2f sq ft  (%.0f ft \u00D7 %.0f ft)", area, length, width));
            areaResultLabel.setForeground(new Color(30, 80, 160)); // keeps color consistent after error
        } catch (NumberFormatException ex) {
            areaResultLabel.setText("Total Area: Invalid input — enter positive numbers");
            areaResultLabel.setForeground(new Color(180, 40, 40));
        }
    }

    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(new Color(50, 50, 50));
        return lbl;
    }

    private void styleField(JTextField f) {
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180)),
            BorderFactory.createEmptyBorder(4, 6, 4, 6)
        ));
    }

    // --- Getters ---
    public double getProjectLength() {
        try { return Double.parseDouble(lengthField.getText().trim()); }
        catch (NumberFormatException e) { return 0; }
    }
    public double getProjectWidth() {
        try { return Double.parseDouble(widthField.getText().trim()); }
        catch (NumberFormatException e) { return 0; }
    }
    public double getArea() { return getProjectLength() * getProjectWidth(); }
}