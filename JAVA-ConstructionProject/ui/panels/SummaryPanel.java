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

        JLabel hint = new JLabel("Data pulled directly from Project, Labor, and Cost tabs.");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        hint.setForeground(new Color(130, 130, 130));
        add(hint, BorderLayout.SOUTH);

        renderSummary();
    }

    public void renderSummary() {
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm"));
        
        // Pulling pre-calculated totals from InfoManager
        double matCost = info.totalConcrete;
        double labor = info.laborCost;
        double subtotal = info.subTotal;
        double discount = info.discount;
        double total = subtotal - discount; // Final safety math

        StringBuilder sb = new StringBuilder();

        sb.append("==============================================================\n");
        sb.append("           CONCRETE PAD PROJECT SUMMARY\n");
        sb.append("==============================================================\n");
        sb.append(String.format("  Generated:  %s%n%n", ts));

        // PROJECT
        sb.append("PROJECT DETAILS\n");
        sb.append("----------------------------------------\n");
        sb.append(row("Project Name", blankOr(info.projectName, "(not entered)")));
        sb.append(row("Location", blankOr(info.projectLocation, "(not entered)")));
        sb.append(row("Client", blankOr(info.projectClient, "(not entered)")));
        sb.append("\n");

        // DIMENSIONS
        sb.append("DIMENSIONS\n");
        sb.append("----------------------------------------\n");
        sb.append(row("Length", String.format("%.1f ft", info.length)));
        sb.append(row("Width", String.format("%.1f ft", info.width)));
        sb.append(row("Total Area", String.format("%.2f sq ft", info.length * info.width)));
        sb.append("\n");

        // SLAB
        sb.append("SLAB CONFIGURATION\n");
        sb.append("----------------------------------------\n");
        sb.append(row("Thickness", String.format("%.1f inches", info.thickness)));
        sb.append(row("Waste Factor", String.format("%.0f%%", info.wasteFactor)));
        sb.append(row("Concrete Needed", String.format("%.2f cubic yards", info.concreteNeeded)));
        sb.append("\n");

        // LABOR
        sb.append("LABOR\n");
        sb.append("----------------------------------------\n");
        sb.append(row("Total Hours", String.format("%.2f hrs", info.hours)));
        sb.append(row("Labor Cost", String.format("$%,.2f", labor)));
        sb.append("\n");

        // COST BREAKDOWN
        sb.append("COST BREAKDOWN\n");
        sb.append("----------------------------------------\n");
        sb.append(row("Concrete Material", String.format("$%,.2f", matCost)));
        sb.append(row("Labor Cost", String.format("$%,.2f", labor)));
        sb.append(row("Subtotal", String.format("$%,.2f", subtotal)));

        if (discount > 0.01) {
            sb.append(row("Discount Applied", String.format("-$%,.2f", discount)));
        }

        sb.append("\n");
        sb.append("==============================================================\n");
        sb.append(String.format("  TOTAL PROJECT ESTIMATE:   $%,.2f%n", total));
        sb.append("==============================================================\n");

        summaryArea.setText(sb.toString());
        summaryArea.setCaretPosition(0);
    }

    private String row(String label, String value) {
        return String.format("  %-28s %s%n", label + ":", value);
    }

    private String blankOr(String s, String fallback) {
        return (s == null || s.isEmpty()) ? fallback : s;
    }

    private void styleBtn(JButton btn, Color bg) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void saveToFile() {
        JFileChooser chooser = new JFileChooser();
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
}