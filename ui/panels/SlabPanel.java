package ui.panels;

import java.awt.*;
import javax.swing.*;

public class SlabPanel extends JPanel {

    // Preset slab types: {label, thickness inches, description}
    private static final Object[][] SLAB_PRESETS = {
        {"Residential Driveway",    4.0,  "Standard 4\" pad for driveways and patios"},
        {"Light Commercial",        5.0,  "5\" slab for small commercial or garage floors"},
        {"Warehouse / Industrial",  6.0,  "6\" heavy-duty pad for forklifts and heavy loads"},
        {"Heavy Industrial",        8.0,  "8\" reinforced slab for extreme loads"},
        {"Custom",                  0.0,  "Enter your own thickness below"},
    };

    private JComboBox<String> slabTypeCombo;
    private JTextField thicknessField;
    private JTextField wasteField;
    private JLabel descriptionLabel;
    private JLabel volumeLabel;

    // To receive area from DimensionsPanel (injected or passed in)
    private double areaSquareFeet = 0;

    public SlabPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));
        setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 8, 10, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel title = new JLabel("Slab Configuration");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(30, 30, 30));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3;
        formPanel.add(title, gbc);
        gbc.gridwidth = 1;

        // Slab Type
        gbc.gridy = 1; gbc.gridx = 0; gbc.weightx = 0.35;
        formPanel.add(makeLabel("Slab Type:"), gbc);
        String[] typeNames = new String[SLAB_PRESETS.length];
        for (int i = 0; i < SLAB_PRESETS.length; i++) typeNames[i] = (String) SLAB_PRESETS[i][0];
        slabTypeCombo = new JComboBox<>(typeNames);
        slabTypeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.weightx = 0.65;
        formPanel.add(slabTypeCombo, gbc);
        gbc.gridwidth = 1;

        // Description label
        descriptionLabel = new JLabel((String) SLAB_PRESETS[0][2]);
        descriptionLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        descriptionLabel.setForeground(new Color(110, 110, 110));
        gbc.gridy = 2; gbc.gridx = 1; gbc.gridwidth = 2;
        formPanel.add(descriptionLabel, gbc);
        gbc.gridwidth = 1;

        // Thickness
        gbc.gridy = 3; gbc.gridx = 0;
        formPanel.add(makeLabel("Thickness (inches):"), gbc);
        thicknessField = new JTextField(8);
        thicknessField.setText(String.valueOf(SLAB_PRESETS[0][1]));
        styleField(thicknessField);
        gbc.gridx = 1;
        formPanel.add(thicknessField, gbc);
        gbc.gridx = 2;
        formPanel.add(makeLabel("inches"), gbc);

        // Waste %
        gbc.gridy = 4; gbc.gridx = 0;
        formPanel.add(makeLabel("Waste Factor (%):"), gbc);
        wasteField = new JTextField(8);
        wasteField.setText("5");
        styleField(wasteField);
        gbc.gridx = 1;
        formPanel.add(wasteField, gbc);
        gbc.gridx = 2;
        formPanel.add(makeLabel("% (typically 5–10%)"), gbc);

        // Calculate button
        JButton calcBtn = new JButton("Calculate Concrete Volume");
        calcBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        calcBtn.setBackground(new Color(70, 130, 200));
        calcBtn.setForeground(Color.WHITE);
        calcBtn.setFocusPainted(false);
        calcBtn.setBorderPainted(false);
        calcBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        calcBtn.addActionListener(e -> calculateVolume());
        gbc.gridy = 5; gbc.gridx = 1; gbc.gridwidth = 2;
        gbc.insets = new Insets(16, 8, 8, 8);
        formPanel.add(calcBtn, gbc);
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 8, 10, 8);

        // Result panel
        JPanel resultPanel = new JPanel(new GridLayout(2, 1, 4, 4));
        resultPanel.setBackground(new Color(225, 245, 230));
        resultPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(140, 200, 160)),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));
        volumeLabel = new JLabel("Concrete Volume: —");
        volumeLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        volumeLabel.setForeground(new Color(20, 110, 50));
        JLabel noteLabel = new JLabel("(includes waste factor)");
        noteLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        noteLabel.setForeground(new Color(80, 130, 90));
        resultPanel.add(volumeLabel);
        resultPanel.add(noteLabel);

        gbc.gridy = 6; gbc.gridx = 0; gbc.gridwidth = 3;
        formPanel.add(resultPanel, gbc);

        // Wire combo → update description + thickness
        slabTypeCombo.addActionListener(e -> {
            int idx = slabTypeCombo.getSelectedIndex();
            descriptionLabel.setText((String) SLAB_PRESETS[idx][2]);
            double preset = (double) SLAB_PRESETS[idx][1];
            if (preset > 0) {
                thicknessField.setText(String.valueOf(preset));
                thicknessField.setEditable(false);
            } else {
                thicknessField.setText("");
                thicknessField.setEditable(true);
            }
        });
        thicknessField.setEditable(false); // default preset selected

        add(formPanel, BorderLayout.CENTER);
    }

    private void calculateVolume() {
        try {
            double thickness = Double.parseDouble(thicknessField.getText().trim());
            double waste = Double.parseDouble(wasteField.getText().trim());
            if (thickness <= 0) throw new NumberFormatException();
            double thicknessFt = thickness / 12.0;
            double rawCubicFt = areaSquareFeet * thicknessFt;
            double totalCubicFt = rawCubicFt * (1 + waste / 100.0);
            double totalCubicYards = totalCubicFt / 27.0;
            volumeLabel.setText(String.format("Concrete Volume: %.2f cu yd  (%.2f cu ft)", totalCubicYards, totalCubicFt));
            volumeLabel.setForeground(new Color(20, 110, 50));
        } catch (NumberFormatException ex) {
            volumeLabel.setText("Invalid input — check thickness and waste fields");
            volumeLabel.setForeground(new Color(180, 40, 40));
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

    /** Call this from the main frame before showing the panel to pass in area from DimensionsPanel */
    public void setAreaSquareFeet(double area) {
        this.areaSquareFeet = area;
    }

    // --- Getters ---
    public double getThicknessInches() {
        try { return Double.parseDouble(thicknessField.getText().trim()); }
        catch (NumberFormatException e) { return 0; }
    }
    public double getWastePercent() {
        try { return Double.parseDouble(wasteField.getText().trim()); }
        catch (NumberFormatException e) { return 5; }
    }
    public double getConcreteCubicYards() {
        double thicknessFt = getThicknessInches() / 12.0;
        double rawCubicFt = areaSquareFeet * thicknessFt;
        double totalCubicFt = rawCubicFt * (1 + getWastePercent() / 100.0);
        return totalCubicFt / 27.0;
    }
    public String getSlabType() {
        return (String) slabTypeCombo.getSelectedItem();
    }
}