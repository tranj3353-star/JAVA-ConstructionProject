package ui.panels;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import models.InfoManager;

/**
 * DimensionsPanel
 * Covers work area dimensions + slab details.
 * All validated values are written to InfoManager singleton on every change.
 */
public class DimensionsPanel extends JPanel {

    // --- Dimension fields ---
    private JTextField lengthField;
    private JTextField widthField;

    // --- Slab fields ---
    private JTextField thicknessField;
    private JTextField slabDescField;
    private JTextField wasteField;

    // --- Result labels ---
    private JLabel areaLabel;
    private JLabel cubicFtLabel;
    private JLabel cubicYdLabel;

    public DimensionsPanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(new Color(240, 240, 240));
        setBorder(BorderFactory.createEmptyBorder(24, 54, 24, 54));

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(7, 8, 7, 8);

        // ================================================================
        // Section: Dimensions
        // ================================================================
        addSectionHeader(mainPanel, gbc, 0, "Work Area Dimensions");
        addSubLabel(mainPanel, gbc, 1, "Enter the length and width of the concrete pad in feet.");

        gbc.gridy = 2; gbc.gridx = 0; gbc.weightx = 0.38; gbc.gridwidth = 1;
        mainPanel.add(makeLabel("Length (ft):"), gbc);
        lengthField = makeField();
        gbc.gridx = 1; gbc.weightx = 0.32;
        mainPanel.add(lengthField, gbc);
        gbc.gridx = 2; gbc.weightx = 0.30;
        mainPanel.add(makeHint("feet"), gbc);

        gbc.gridy = 3; gbc.gridx = 0; gbc.weightx = 0.38;
        mainPanel.add(makeLabel("Width (ft):"), gbc);
        widthField = makeField();
        gbc.gridx = 1; gbc.weightx = 0.32;
        mainPanel.add(widthField, gbc);
        gbc.gridx = 2; gbc.weightx = 0.30;
        mainPanel.add(makeHint("feet"), gbc);

        // ================================================================
        // Divider
        // ================================================================
        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 3;
        gbc.insets = new Insets(16, 8, 4, 8);
        mainPanel.add(new JSeparator(), gbc);
        gbc.insets = new Insets(7, 8, 7, 8);
        gbc.gridwidth = 1;

        // ================================================================
        // Section: Slab Details
        // ================================================================
        addSectionHeader(mainPanel, gbc, 5, "Slab Details");
        addSubLabel(mainPanel, gbc, 6, "Describe the slab and enter thickness. Waste factor accounts for over-pour.");

        // Slab description (free-form)
        gbc.gridy = 7; gbc.gridx = 0; gbc.weightx = 0.38; gbc.gridwidth = 1;
        mainPanel.add(makeLabel("Slab Description:"), gbc);
        slabDescField = makeField();
        slabDescField.setToolTipText("e.g. Warehouse floor, Driveway, Patio, Equipment pad...");
        gbc.gridx = 1; gbc.weightx = 0.62; gbc.gridwidth = 2;
        mainPanel.add(slabDescField, gbc);
        gbc.gridwidth = 1;

        // Thickness
        gbc.gridy = 8; gbc.gridx = 0; gbc.weightx = 0.38;
        mainPanel.add(makeLabel("Thickness (inches):"), gbc);
        thicknessField = makeField();
        gbc.gridx = 1; gbc.weightx = 0.32;
        mainPanel.add(thicknessField, gbc);
        gbc.gridx = 2; gbc.weightx = 0.30;
        mainPanel.add(makeHint("inches"), gbc);

        // Waste factor
        gbc.gridy = 9; gbc.gridx = 0; gbc.weightx = 0.38;
        mainPanel.add(makeLabel("Waste Factor (%):"), gbc);
        wasteField = makeField();
        wasteField.setText("5");
        gbc.gridx = 1; gbc.weightx = 0.32;
        mainPanel.add(wasteField, gbc);
        gbc.gridx = 2; gbc.weightx = 0.30;
        mainPanel.add(makeHint("% (typically 5–10%)"), gbc);

        // ================================================================
        // Divider
        // ================================================================
        gbc.gridy = 10; gbc.gridx = 0; gbc.gridwidth = 3;
        gbc.insets = new Insets(16, 8, 4, 8);
        mainPanel.add(new JSeparator(), gbc);
        gbc.insets = new Insets(7, 8, 7, 8);
        gbc.gridwidth = 1;

        // ================================================================
        // Section: Live Results
        // ================================================================
        addSectionHeader(mainPanel, gbc, 11, "Calculated Results");

        JPanel resultsCard = buildResultsCard();
        gbc.gridy = 12; gbc.gridx = 0; gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(resultsCard, gbc);

        add(mainPanel, BorderLayout.CENTER);

        // ================================================================
        // Wire all numeric fields → live recalculate + InfoManager update
        // ================================================================
        DocumentListener liveCalc = new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { recalculate(); }
            public void removeUpdate(DocumentEvent e)  { recalculate(); }
            public void changedUpdate(DocumentEvent e) { recalculate(); }
        };

        // Slab description is text-only — just sync to InfoManager, no recalc needed
        DocumentListener slabDescSync = new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { syncSlabDesc(); }
            public void removeUpdate(DocumentEvent e)  { syncSlabDesc(); }
            public void changedUpdate(DocumentEvent e) { syncSlabDesc(); }
        };

        lengthField.getDocument().addDocumentListener(liveCalc);
        widthField.getDocument().addDocumentListener(liveCalc);
        thicknessField.getDocument().addDocumentListener(liveCalc);
        wasteField.getDocument().addDocumentListener(liveCalc);
        slabDescField.getDocument().addDocumentListener(slabDescSync);

        recalculate();
    }

    // -----------------------------------------------------------------------
    // Results card
    // -----------------------------------------------------------------------
    private JPanel buildResultsCard() {
        JPanel card = new JPanel(new GridLayout(3, 1, 0, 6));
        card.setBackground(new Color(235, 243, 255));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(160, 195, 235)),
            BorderFactory.createEmptyBorder(12, 18, 12, 18)
        ));

        areaLabel    = makeResultLabel("Total Area:              —");
        cubicFtLabel = makeResultLabel("Volume (cubic feet):     —");
        cubicYdLabel = makeResultLabel("Volume (cubic yards):    —");

        cubicYdLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        cubicYdLabel.setForeground(new Color(20, 80, 180));

        card.add(areaLabel);
        card.add(cubicFtLabel);
        card.add(cubicYdLabel);
        return card;
    }

    // -----------------------------------------------------------------------
    // Core recalculate — updates UI labels AND InfoManager
    // -----------------------------------------------------------------------
    private void recalculate() {
        InfoManager info = InfoManager.getInstance();

        double length   = parseDouble(lengthField);
        double width    = parseDouble(widthField);
        double thickIn  = parseDouble(thicknessField);
        double wastePct = parseDouble(wasteField);

        boolean dimsOk = length > 0 && width > 0;
        boolean slabOk = thickIn > 0;

        // Always sync whatever is valid into InfoManager
        info.length = dimsOk ? length : 0;
        info.width  = dimsOk ? width  : 0;

        if (!dimsOk) {
            info.totalArea      = 0;
            info.thickness      = 0;
            info.wasteFactor    = 0;
            info.concreteNeeded = 0;

            areaLabel.setText("Total Area:              enter length and width");
            areaLabel.setForeground(new Color(150, 150, 150));
            cubicFtLabel.setText("Volume (cubic feet):     —");
            cubicFtLabel.setForeground(new Color(150, 150, 150));
            cubicYdLabel.setText("Volume (cubic yards):    —");
            cubicYdLabel.setForeground(new Color(150, 150, 150));
            return;
        }

        double area    = length * width;
        info.totalArea = area;

        areaLabel.setText(String.format(
            "Total Area:              %.2f sq ft  (%.1f ft \u00D7 %.1f ft)", area, length, width));
        areaLabel.setForeground(new Color(40, 40, 40));

        if (!slabOk) {
            info.thickness      = 0;
            info.wasteFactor    = 0;
            info.concreteNeeded = 0;

            cubicFtLabel.setText("Volume (cubic feet):     enter thickness");
            cubicFtLabel.setForeground(new Color(150, 150, 150));
            cubicYdLabel.setText("Volume (cubic yards):    —");
            cubicYdLabel.setForeground(new Color(150, 150, 150));
            return;
        }

        double waste     = wastePct >= 0 ? wastePct : 0;
        double thickFt   = thickIn / 12.0;
        double rawCuFt   = area * thickFt;
        double totalCuFt = rawCuFt * (1.0 + waste / 100.0);
        double totalCuYd = totalCuFt / 27.0;

        info.thickness      = thickIn;
        info.wasteFactor    = waste;
        info.concreteNeeded = totalCuYd;

        cubicFtLabel.setText(String.format(
            "Volume (cubic feet):     %.2f cu ft  (%.1f\" thick, %.0f%% waste)",
            totalCuFt, thickIn, waste));
        cubicFtLabel.setForeground(new Color(40, 40, 40));

        cubicYdLabel.setText(String.format(
            "Volume (cubic yards):    %.2f cu yd", totalCuYd));
        cubicYdLabel.setForeground(new Color(20, 80, 180));
    }

    /** Syncs slab description text field → InfoManager.slabType */
    private void syncSlabDesc() {
        InfoManager.getInstance().slabType = slabDescField.getText().trim();
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------
    private void addSectionHeader(JPanel panel, GridBagConstraints gbc, int row, String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lbl.setForeground(new Color(25, 25, 25));
        gbc.gridy = row; gbc.gridx = 0; gbc.gridwidth = 3;
        panel.add(lbl, gbc);
        gbc.gridwidth = 1;
    }

    private void addSubLabel(JPanel panel, GridBagConstraints gbc, int row, String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(new Color(115, 115, 115));
        gbc.gridy = row; gbc.gridx = 0; gbc.gridwidth = 3;
        panel.add(lbl, gbc);
        gbc.gridwidth = 1;
    }

    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(new Color(45, 45, 45));
        return lbl;
    }

    private JLabel makeHint(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lbl.setForeground(new Color(140, 140, 140));
        return lbl;
    }

    private JLabel makeResultLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbl.setForeground(new Color(40, 40, 40));
        return lbl;
    }

    private JTextField makeField() {
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180)),
            BorderFactory.createEmptyBorder(5, 7, 5, 7)
        ));
        return f;
    }

    private double parseDouble(JTextField f) {
        try {
            return Double.parseDouble(f.getText().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // -----------------------------------------------------------------------
    // Public getters — delegate to InfoManager so truth lives in one place
    // -----------------------------------------------------------------------
    public double getProjectLength()      { return InfoManager.getInstance().length; }
    public double getProjectWidth()       { return InfoManager.getInstance().width; }
    public double getArea()               { return InfoManager.getInstance().totalArea; }
    public double getThicknessInches()    { return InfoManager.getInstance().thickness; }
    public double getWastePercent()       { return InfoManager.getInstance().wasteFactor; }
    public String getSlabDescription()    { return InfoManager.getInstance().slabType; }
    public double getConcreteCubicYards() { return InfoManager.getInstance().concreteNeeded; }
}