package ui.panels;

import java.awt.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.*;

public class SummaryPanel extends JPanel {

    private JTextArea summaryArea;

    // Data fields (set by main frame before showing)
    private String projectName   = "";
    private String location      = "";
    private String clientName    = "";
    private String slabType      = "";
    private double length        = 0;
    private double width         = 0;
    private double thicknessIn   = 0;
    private double wastePercent  = 0;
    private double concreteCuYd  = 0;
    private int    numEmployees  = 0;
    private double laborHours    = 0;
    private double laborCost     = 0;
    private double concretePrice = 0;
    private double additionalMat = 0;
    private double totalCost     = 0;

    public SummaryPanel() {
        setLayout(new BorderLayout(0, 10));
        setBackground(new Color(240, 240, 240));
        setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // Title bar
        JPanel headerBar = new JPanel(new BorderLayout());
        headerBar.setOpaque(false);
        JLabel title = new JLabel("Estimate Summary");
        title.setFont(new Font("Segoe UI", Font.BOLD, 21));
        title.setForeground(new Color(30, 30, 30));
        headerBar.add(title, BorderLayout.WEST);

        // Action buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setOpaque(false);
        JButton refreshBtn = new JButton("\uD83D\uDD04 Refresh");
        JButton saveBtn    = new JButton("\uD83D\uDCBE Save as TXT");
        styleBtn(refreshBtn, new Color(100, 100, 100));
        styleBtn(saveBtn, new Color(70, 130, 200));
        refreshBtn.addActionListener(e -> renderSummary());
        saveBtn.addActionListener(e -> saveToFile());
        btnPanel.add(refreshBtn);
        btnPanel.add(saveBtn);
        headerBar.add(btnPanel, BorderLayout.EAST);

        add(headerBar, BorderLayout.NORTH);

        // Summary text area
        summaryArea = new JTextArea();
        summaryArea.setFont(new Font("Courier New", Font.PLAIN, 13));
        summaryArea.setEditable(false);
        summaryArea.setForeground(new Color(25, 25, 25));
        summaryArea.setBackground(new Color(255, 255, 255));
        summaryArea.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        summaryArea.setLineWrap(false);

        JScrollPane scroll = new JScrollPane(summaryArea);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        add(scroll, BorderLayout.CENTER);

        // Hint
        JLabel hint = new JLabel("Click Refresh to update with latest data from all steps.");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        hint.setForeground(new Color(130, 130, 130));
        add(hint, BorderLayout.SOUTH);

        renderSummary();
    }

    public void renderSummary() {
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy  HH:mm"));
        double area = length * width;
        double concreteCost = concreteCuYd * concretePrice;
        double subtotal = concreteCost + laborCost + additionalMat;
        double discount = subtotal - totalCost;

        StringBuilder sb = new StringBuilder();
        sb.append("=".repeat(62)).append("\n");
        sb.append("       CONCRETE PAD ESTIMATE\n");
        sb.append("=".repeat(62)).append("\n");
        sb.append(String.format("  Generated:  %s%n", ts));
        sb.append("\n");
        sb.append("PROJECT DETAILS\n");
        sb.append("-".repeat(40)).append("\n");
        sb.append(row("Project Name",  blankOr(projectName, "(not entered)")));
        sb.append(row("Location",       blankOr(location,    "(not entered)")));
        sb.append(row("Client",         blankOr(clientName,  "(not entered)")));
        sb.append("\n");
        sb.append("DIMENSIONS\n");
        sb.append("-".repeat(40)).append("\n");
        sb.append(row("Length",         fmt("%.1f ft", length)));
        sb.append(row("Width",          fmt("%.1f ft", width)));
        sb.append(row("Total Area",     fmt("%.2f sq ft", area)));
        sb.append("\n");
        sb.append("SLAB CONFIGURATION\n");
        sb.append("-".repeat(40)).append("\n");
        sb.append(row("Slab Type",      blankOr(slabType, "(not set)")));
        sb.append(row("Thickness",      fmt("%.1f inches", thicknessIn)));
        sb.append(row("Waste Factor",   fmt("%.0f%%", wastePercent)));
        sb.append(row("Concrete Needed",fmt("%.2f cubic yards", concreteCuYd)));
        sb.append("\n");
        sb.append("LABOR\n");
        sb.append("-".repeat(40)).append("\n");
        sb.append(row("Employees",      String.valueOf(numEmployees)));
        sb.append(row("Total Hours",    fmt("%.1f hrs", laborHours)));
        sb.append(row("Labor Cost",     fmt("$%,.2f", laborCost)));
        sb.append("\n");
        sb.append("COST BREAKDOWN\n");
        sb.append("-".repeat(40)).append("\n");
        sb.append(row("Concrete Material",  fmt("$%,.2f", concreteCost)));
        sb.append(row("Labor",              fmt("$%,.2f", laborCost)));
        sb.append(row("Additional Materials",fmt("$%,.2f", additionalMat)));
        sb.append(row("Subtotal",           fmt("$%,.2f", subtotal)));
        if (discount > 0.01)
            sb.append(row("Discount Applied",   fmt("-$%,.2f", discount)));
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
        chooser.setSelectedFile(new File(
            (projectName.isEmpty() ? "estimate" : projectName.replaceAll("[^a-zA-Z0-9]", "_")) + ".txt"
        ));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
                pw.print(summaryArea.getText());
                JOptionPane.showMessageDialog(this, "Estimate saved to:\n" + file.getAbsolutePath(),
                    "Saved", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
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

    // --- Setters called by main frame before showing this panel ---
    public void setProjectName(String v)    { this.projectName   = v; }
    public void setLocation(String v)       { this.location      = v; }
    public void setClientName(String v)     { this.clientName    = v; }
    public void setSlabType(String v)       { this.slabType      = v; }
    public void setLength(double v)         { this.length        = v; }
    public void setWidth(double v)          { this.width         = v; }
    public void setThicknessIn(double v)    { this.thicknessIn   = v; }
    public void setWastePercent(double v)   { this.wastePercent  = v; }
    public void setConcreteCuYd(double v)   { this.concreteCuYd  = v; }
    public void setNumEmployees(int v)      { this.numEmployees  = v; }
    public void setLaborHours(double v)     { this.laborHours    = v; }
    public void setLaborCost(double v)      { this.laborCost     = v; }
    public void setConcretePrice(double v)  { this.concretePrice = v; }
    public void setAdditionalMat(double v)  { this.additionalMat = v; }
    public void setTotalCost(double v)      { this.totalCost     = v; }
}
