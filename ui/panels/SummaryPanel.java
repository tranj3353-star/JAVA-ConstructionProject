package ui.panels;

import java.awt.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import models.InfoManager;

public class SummaryPanel extends JPanel {

    private JTextArea summaryArea;
    private InfoManager info = InfoManager.getInstance();

    // Keep ONLY values that are NOT in InfoManager
    private double concretePrice = 0;
    private double additionalMat = 0;
    private double totalCost     = 0;
    private int    numEmployees  = 0;
    private double laborHours    = 0;
    private double laborCost     = 0;

    public SummaryPanel() {
        setLayout(new BorderLayout(0, 10));
        setBackground(new Color(240, 240, 240));
        setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JPanel headerBar = new JPanel(new BorderLayout());
        headerBar.setOpaque(false);

        JLabel title = new JLabel("Estimate Summary");
        title.setFont(new Font("Segoe UI", Font.BOLD, 21));
        headerBar.add(title, BorderLayout.WEST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setOpaque(false);

        JButton refreshBtn = new JButton("🔄 Refresh");
        JButton saveBtn    = new JButton("💾 Save as TXT");

        styleBtn(refreshBtn, new Color(100, 100, 100));
        styleBtn(saveBtn, new Color(70, 130, 200));

        refreshBtn.addActionListener(e -> renderSummary());
        saveBtn.addActionListener(e -> saveToFile());

        btnPanel.add(refreshBtn);
        btnPanel.add(saveBtn);
        headerBar.add(btnPanel, BorderLayout.EAST);

        add(headerBar, BorderLayout.NORTH);

        summaryArea = new JTextArea();
        summaryArea.setFont(new Font("Courier New", Font.PLAIN, 13));
        summaryArea.setEditable(false);
        summaryArea.setBackground(Color.WHITE);
        summaryArea.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        JScrollPane scroll = new JScrollPane(summaryArea);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));

        add(scroll, BorderLayout.CENTER);

        JLabel hint = new JLabel("Click Refresh to update with latest data.");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        hint.setForeground(new Color(130, 130, 130));
        add(hint, BorderLayout.SOUTH);

        renderSummary();
    }

    public void renderSummary() {
        String ts = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("MM/dd/yyyy  HH:mm"));

        double area = info.length * info.width;
        double concreteCost = info.concreteNeeded * concretePrice;
        double subtotal = concreteCost + laborCost + additionalMat;
        double discount = subtotal - totalCost;

        StringBuilder sb = new StringBuilder();

        sb.append("=".repeat(62)).append("\n");
        sb.append("       CONCRETE PAD ESTIMATE\n");
        sb.append("=".repeat(62)).append("\n");
        sb.append(String.format("  Generated:  %s%n%n", ts));

        // PROJECT
        sb.append("PROJECT DETAILS\n");
        sb.append("-".repeat(40)).append("\n");
        sb.append(row("Project Name", blankOr(info.projectName, "(not entered)")));
        sb.append(row("Location", blankOr(info.projectLocation, "(not entered)")));
        sb.append(row("Client", blankOr(info.projectClient, "(not entered)")));
        sb.append("\n");

        // DIMENSIONS
        sb.append("DIMENSIONS\n");
        sb.append("-".repeat(40)).append("\n");
        sb.append(row("Length", fmt("%.1f ft", info.length)));
        sb.append(row("Width", fmt("%.1f ft", info.width)));
        sb.append(row("Total Area", fmt("%.2f sq ft", area)));
        sb.append("\n");

        // SLAB
        sb.append("SLAB CONFIGURATION\n");
        sb.append("-".repeat(40)).append("\n");
        sb.append(row("Slab Type", blankOr(info.slabType, "(not set)")));
        sb.append(row("Thickness", fmt("%.1f inches", info.thickness)));
        sb.append(row("Waste Factor", fmt("%.0f%%", info.wasteFactor)));
        sb.append(row("Concrete Needed", fmt("%.2f cubic yards", info.concreteNeeded)));
        sb.append("\n");

        // LABOR
        sb.append("LABOR\n");
        sb.append("-".repeat(40)).append("\n");
        sb.append(row("Employees", String.valueOf(numEmployees)));
        sb.append(row("Total Hours", fmt("%.1f hrs", laborHours)));
        sb.append(row("Labor Cost", fmt("$%,.2f", laborCost)));
        sb.append("\n");

        // COST
        sb.append("COST BREAKDOWN\n");
        sb.append("-".repeat(40)).append("\n");
        sb.append(row("Concrete Material", fmt("$%,.2f", concreteCost)));
        sb.append(row("Labor", fmt("$%,.2f", laborCost)));
        sb.append(row("Additional Materials", fmt("$%,.2f", additionalMat)));
        sb.append(row("Subtotal", fmt("$%,.2f", subtotal)));

        if (discount > 0.01) {
            sb.append(row("Discount Applied", fmt("-$%,.2f", discount)));
        }

        sb.append("\n");
        sb.append("=".repeat(62)).append("\n");
        sb.append(String.format("  TOTAL PROJECT COST:   $%,.2f%n", totalCost));
        sb.append("=".repeat(62)).append("\n");

        summaryArea.setText(sb.toString());
        summaryArea.setCaretPosition(0);
    }

    private String row(String label, String value) {
        return String.format("  %-28s %s%n", label + ":", value);
    }

    private String fmt(String format, Object val) {
        return String.format(format, val);
    }

    private String blankOr(String s, String fallback) {
        return (s == null || s.isEmpty()) ? fallback : s;
    }

    private void saveToFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Estimate");

        String filename = (info.projectName == null || info.projectName.isEmpty())
                ? "estimate.txt"
                : info.projectName.replaceAll("[^a-zA-Z0-9]", "_") + ".txt";

        chooser.setSelectedFile(new File(filename));

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (PrintWriter pw = new PrintWriter(chooser.getSelectedFile())) {
                pw.print(summaryArea.getText());
                JOptionPane.showMessageDialog(this, "Saved successfully!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    private void styleBtn(JButton btn, Color bg) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    // setters ONLY for values NOT in InfoManager
    public void setConcretePrice(double v) { this.concretePrice = v; }
    public void setAdditionalMat(double v) { this.additionalMat = v; }
    public void setTotalCost(double v)     { this.totalCost = v; }
    public void setNumEmployees(int v)     { this.numEmployees = v; }
    public void setLaborHours(double v)    { this.laborHours = v; }
    public void setLaborCost(double v)     { this.laborCost = v; }
}