package ui.panels;

import models.InfoManager;
import java.awt.*;
import javax.swing.*;

public class CostPanel extends JPanel {

    private JTextField concretePriceField;
    private JTextField additionalMaterialField;
    private JComboBox<String> discountTypeCombo;
    private JTextField discountValueField;

    private JLabel concreteMatCostLabel;
    private JLabel laborCostDisplayLabel;
    private JLabel additionalCostLabel;
    private JLabel subtotalLabel;
    private JLabel discountLabel;
    private JLabel totalLabel;

    public CostPanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(new Color(240, 240, 240));
        setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Title ---
        JLabel title = new JLabel("Cost Calculations");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        mainPanel.add(title, gbc);

        // --- Material Pricing Section ---
        gbc.gridy = 1; gbc.gridwidth = 2;
        mainPanel.add(makeSectionLabel("Material Pricing"), gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 2; gbc.gridx = 0;
        mainPanel.add(new JLabel("Concrete Price ($/cu yd):"), gbc);
        concretePriceField = new JTextField("150.00", 10);
        gbc.gridx = 1;
        mainPanel.add(concretePriceField, gbc);

        gbc.gridy = 3; gbc.gridx = 0;
        mainPanel.add(new JLabel("Additional Materials ($):"), gbc);
        additionalMaterialField = new JTextField("0.00", 10);
        gbc.gridx = 1;
        mainPanel.add(additionalMaterialField, gbc);

        // --- Discount Section ---
        gbc.gridy = 4; gbc.gridwidth = 2;
        mainPanel.add(new JSeparator(), gbc);
        
        gbc.gridy = 5;
        mainPanel.add(makeSectionLabel("Discount Settings"), gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 6; gbc.gridx = 0;
        mainPanel.add(new JLabel("Discount Type:"), gbc);
        discountTypeCombo = new JComboBox<>(new String[]{"None", "Percentage (%)", "Fixed Amount ($)"});
        gbc.gridx = 1;
        mainPanel.add(discountTypeCombo, gbc);

        gbc.gridy = 7; gbc.gridx = 0;
        mainPanel.add(new JLabel("Discount Value:"), gbc);
        discountValueField = new JTextField("0", 10);
        gbc.gridx = 1;
        mainPanel.add(discountValueField, gbc);

        // --- ACTION BUTTON ---
        JButton calcBtn = new JButton("Calculate Total");
        calcBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        calcBtn.setBackground(new Color(60, 120, 190));
        calcBtn.setForeground(Color.WHITE);
        calcBtn.setPreferredSize(new Dimension(150, 40));
        calcBtn.addActionListener(e -> performCalculations());
        
        gbc.gridy = 8; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 20, 10);
        mainPanel.add(calcBtn, gbc);

        // --- Results Breakdown ---
        gbc.insets = new Insets(5, 10, 5, 10);
        concreteMatCostLabel  = makeResultLabel("Concrete Material: $0.00");
        laborCostDisplayLabel = makeResultLabel("Labor Cost: $0.00");
        additionalCostLabel   = makeResultLabel("Additional Materials: $0.00");
        subtotalLabel         = makeResultLabel("Subtotal: $0.00");
        discountLabel         = makeResultLabel("Discount Applied: $0.00");

        int row = 9;
        for (JLabel lbl : new JLabel[]{concreteMatCostLabel, laborCostDisplayLabel, additionalCostLabel, subtotalLabel, discountLabel}) {
            gbc.gridy = row++;
            mainPanel.add(lbl, gbc);
        }

        totalLabel = new JLabel("FINAL TOTAL: $0.00");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        totalLabel.setForeground(new Color(25, 100, 25));
        gbc.gridy = row;
        mainPanel.add(totalLabel, gbc);

        add(new JScrollPane(mainPanel), BorderLayout.CENTER);
    }

    private void performCalculations() {
        try {
            InfoManager info = InfoManager.getInstance();
            
            double yards = info.concreteNeeded; 
            double labor = info.laborCost; 
            double pricePerYard = parseDouble(concretePriceField.getText());
            double addMat = parseDouble(additionalMaterialField.getText());
            double discInput = parseDouble(discountValueField.getText());

            double concreteCost = yards * pricePerYard;
            double currentSubtotal = concreteCost + labor + addMat;

            double discountAmt = 0;
            String type = (String) discountTypeCombo.getSelectedItem();
            if ("Percentage (%)".equals(type)) {
                discountAmt = currentSubtotal * (discInput / 100.0);
            } else if ("Fixed Amount ($)".equals(type)) {
                discountAmt = discInput;
            }

            double finalTotal = currentSubtotal - discountAmt;

            // --- SAVE EVERYTHING TO INFOMANAGER ---
            info.concretePrice = pricePerYard;
            info.totalConcrete = concreteCost;
            info.subTotal      = currentSubtotal;
            info.discount      = discountAmt;
            // Assuming your InfoManager field is named total
            // info.total = finalTotal; 

            // Update UI Labels
            concreteMatCostLabel.setText(String.format("Concrete Material: $%,.2f", concreteCost));
            laborCostDisplayLabel.setText(String.format("Labor Cost: $%,.2f", labor));
            additionalCostLabel.setText(String.format("Additional: $%,.2f", addMat));
            subtotalLabel.setText(String.format("Subtotal: $%,.2f", currentSubtotal));
            discountLabel.setText(String.format("Discount Applied: -$%,.2f", discountAmt));
            totalLabel.setText(String.format("FINAL TOTAL: $%,.2f", finalTotal));

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers in the pricing fields.");
        }
    }

    private double parseDouble(String val) {
        try { return Double.parseDouble(val.trim()); } catch (Exception e) { return 0; }
    }

    private JLabel makeSectionLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl.setForeground(new Color(80, 80, 80));
        return lbl;
    }

    private JLabel makeResultLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        return lbl;
    }
}