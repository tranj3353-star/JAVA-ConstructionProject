package ui.panels;

import java.awt.*;
import javax.swing.*;

public class CostPanel extends JPanel {

    private JTextField concretePriceField;
    private JTextField additionalMaterialField;
    private JTextField discountTypeCombo_placeholder; // replaced by combo
    private JComboBox<String> discountTypeCombo;
    private JTextField discountValueField;

    private JLabel concreteMatCostLabel;
    private JLabel laborCostDisplayLabel;
    private JLabel additionalCostLabel;
    private JLabel subtotalLabel;
    private JLabel discountLabel;
    private JLabel totalLabel;

    // Injected from other panels
    private double concreteCubicYards = 0;
    private double laborCost = 0;

    public CostPanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(new Color(240, 240, 240));
        setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(9, 8, 9, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel title = new JLabel("Cost Calculations");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(30, 30, 30));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3;
        mainPanel.add(title, gbc);
        gbc.gridwidth = 1;

        // --- Material inputs ---
        JLabel inputSec = new JLabel("Material Pricing");
        inputSec.setFont(new Font("Segoe UI", Font.BOLD, 14));
        inputSec.setForeground(new Color(70, 70, 70));
        gbc.gridy = 1; gbc.gridx = 0; gbc.gridwidth = 3;
        mainPanel.add(inputSec, gbc);
        gbc.gridwidth = 1;

        // Concrete price per yard
        gbc.gridy = 2; gbc.gridx = 0; gbc.weightx = 0.4;
        mainPanel.add(makeLabel("Concrete Price ($/cu yd):"), gbc);
        concretePriceField = makeField("e.g. 150.00");
        gbc.gridx = 1; gbc.weightx = 0.3;
        mainPanel.add(concretePriceField, gbc);
        gbc.gridx = 2; gbc.weightx = 0.3;
        mainPanel.add(makeLabel("per cubic yard"), gbc);

        // Additional materials
        gbc.gridy = 3; gbc.gridx = 0;
        mainPanel.add(makeLabel("Additional Materials ($):"), gbc);
        additionalMaterialField = makeField("e.g. 500.00");
        gbc.gridx = 1;
        mainPanel.add(additionalMaterialField, gbc);
        gbc.gridx = 2;
        mainPanel.add(makeLabel("rebar, mesh, forms..."), gbc);

        // --- Discount ---
        JSeparator sep1 = new JSeparator();
        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 3; gbc.insets = new Insets(14, 8, 4, 8);
        mainPanel.add(sep1, gbc);
        gbc.gridwidth = 1; gbc.insets = new Insets(9, 8, 9, 8);

        JLabel discSec = new JLabel("Discount (Optional)");
        discSec.setFont(new Font("Segoe UI", Font.BOLD, 14));
        discSec.setForeground(new Color(70, 70, 70));
        gbc.gridy = 5; gbc.gridx = 0; gbc.gridwidth = 3;
        mainPanel.add(discSec, gbc);
        gbc.gridwidth = 1;

        gbc.gridy = 6; gbc.gridx = 0;
        mainPanel.add(makeLabel("Discount Type:"), gbc);
        discountTypeCombo = new JComboBox<>(new String[]{"None", "Percentage (%)", "Fixed Amount ($)"});
        discountTypeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 1;
        mainPanel.add(discountTypeCombo, gbc);

        gbc.gridy = 7; gbc.gridx = 0;
        mainPanel.add(makeLabel("Discount Value:"), gbc);
        discountValueField = makeField("0");
        gbc.gridx = 1;
        mainPanel.add(discountValueField, gbc);

        // --- Calculate button ---
        JButton calcBtn = new JButton("Calculate Total Cost");
        calcBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        calcBtn.setBackground(new Color(70, 130, 200));
        calcBtn.setForeground(Color.WHITE);
        calcBtn.setFocusPainted(false);
        calcBtn.setBorderPainted(false);
        calcBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        calcBtn.addActionListener(e -> calculateCosts());
        gbc.gridy = 8; gbc.gridx = 0; gbc.gridwidth = 3;
        gbc.insets = new Insets(16, 8, 8, 8);
        mainPanel.add(calcBtn, gbc);
        gbc.gridwidth = 1; gbc.insets = new Insets(9, 8, 9, 8);

        // --- Results breakdown ---
        JSeparator sep2 = new JSeparator();
        gbc.gridy = 9; gbc.gridx = 0; gbc.gridwidth = 3; gbc.insets = new Insets(4, 8, 4, 8);
        mainPanel.add(sep2, gbc);
        gbc.gridwidth = 1; gbc.insets = new Insets(5, 8, 5, 8);

        concreteMatCostLabel   = makeResultLabel("Concrete Material:  —");
        laborCostDisplayLabel  = makeResultLabel("Labor:  —");
        additionalCostLabel    = makeResultLabel("Additional Materials:  —");
        subtotalLabel          = makeResultLabel("Subtotal:  —");
        discountLabel          = makeResultLabel("Discount:  —");

        totalLabel = new JLabel("Total:  —");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        totalLabel.setForeground(new Color(30, 100, 30));

        int row = 10;
        for (JLabel lbl : new JLabel[]{concreteMatCostLabel, laborCostDisplayLabel, additionalCostLabel, subtotalLabel, discountLabel}) {
            gbc.gridy = row++; gbc.gridx = 0; gbc.gridwidth = 3;
            mainPanel.add(lbl, gbc);
        }

        JSeparator sep3 = new JSeparator();
        gbc.gridy = row++; gbc.gridx = 0; gbc.gridwidth = 3;
        mainPanel.add(sep3, gbc);

        gbc.gridy = row; gbc.gridx = 0; gbc.gridwidth = 3;
        mainPanel.add(totalLabel, gbc);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void calculateCosts() {
        try {
            double concretePrice = Double.parseDouble(concretePriceField.getText().trim());
            double additionalMat = Double.parseDouble(additionalMaterialField.getText().trim());
            double discountVal = 0;
            try { discountVal = Double.parseDouble(discountValueField.getText().trim()); } catch (Exception ignored) {}

            double concreteCost = concreteCubicYards * concretePrice;
            double subtotal = concreteCost + laborCost + additionalMat;

            double discountAmount = 0;
            String discountType = (String) discountTypeCombo.getSelectedItem();
            if ("Percentage (%)".equals(discountType)) {
                discountAmount = subtotal * (discountVal / 100.0);
            } else if ("Fixed Amount ($)".equals(discountType)) {
                discountAmount = discountVal;
            }
            double total = subtotal - discountAmount;

            concreteMatCostLabel.setText(String.format("Concrete Material:  $%,.2f  (%.2f cu yd @ $%.2f/yd)", concreteCost, concreteCubicYards, concretePrice));
            laborCostDisplayLabel.setText(String.format("Labor:  $%,.2f", laborCost));
            additionalCostLabel.setText(String.format("Additional Materials:  $%,.2f", additionalMat));
            subtotalLabel.setText(String.format("Subtotal:  $%,.2f", subtotal));
            if (discountAmount > 0)
                discountLabel.setText(String.format("Discount (%s):  -$%,.2f", discountType, discountAmount));
            else
                discountLabel.setText("Discount:  None");
            totalLabel.setText(String.format("TOTAL:  $%,.2f", total));
            totalLabel.setForeground(new Color(20, 110, 40));
        } catch (NumberFormatException ex) {
            totalLabel.setText("Invalid input — check pricing fields");
            totalLabel.setForeground(new Color(180, 40, 40));
        }
    }

    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(new Color(50, 50, 50));
        return lbl;
    }

    private JLabel makeResultLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbl.setForeground(new Color(60, 60, 60));
        return lbl;
    }

    private JTextField makeField(String placeholder) {
        JTextField field = new JTextField(10);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setText(placeholder);
        field.setForeground(Color.GRAY);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180)),
            BorderFactory.createEmptyBorder(4, 6, 4, 6)
        ));
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText(""); field.setForeground(new Color(30, 30, 30));
                }
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder); field.setForeground(Color.GRAY);
                }
            }
        });
        return field;
    }

    /** Called by main frame before showing this panel */
    public void setConcreteCubicYards(double yards) { this.concreteCubicYards = yards; }
    public void setLaborCost(double cost) { this.laborCost = cost; }

    // --- Getters for SummaryPanel ---
    public double getConcretePricePerYard() {
        try { return Double.parseDouble(concretePriceField.getText().trim()); } catch (Exception e) { return 0; }
    }
    public double getAdditionalMaterialCost() {
        try { return Double.parseDouble(additionalMaterialField.getText().trim()); } catch (Exception e) { return 0; }
    }
    public double getTotalCost() {
        double concreteCost = concreteCubicYards * getConcretePricePerYard();
        double subtotal = concreteCost + laborCost + getAdditionalMaterialCost();
        double discountVal = 0;
        try { discountVal = Double.parseDouble(discountValueField.getText().trim()); } catch (Exception ignored) {}
        double discountAmount = 0;
        String discountType = (String) discountTypeCombo.getSelectedItem();
        if ("Percentage (%)".equals(discountType)) discountAmount = subtotal * (discountVal / 100.0);
        else if ("Fixed Amount ($)".equals(discountType)) discountAmount = discountVal;
        return subtotal - discountAmount;
    }
}