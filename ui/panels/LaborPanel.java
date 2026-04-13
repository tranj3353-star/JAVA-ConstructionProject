package ui.panels;

import models.InfoManager;
import java.awt.*;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

public class LaborPanel extends JPanel {

    private DefaultTableModel csvTableModel;
    private JTable csvTable;
    private JLabel csvStatusLabel;
    private JLabel totalLaborLabel;
    private JLabel totalCostLabel;

    public LaborPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(240, 240, 240));
        setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildCsvTablePanel(), BorderLayout.CENTER);
        add(buildSummaryPanel(), BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel title = new JLabel("Labor Data Import");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        
        JButton uploadBtn = new JButton("📂  Upload CSV");
        uploadBtn.setBackground(new Color(70, 130, 200));
        uploadBtn.setForeground(Color.WHITE);
        uploadBtn.setFocusPainted(false);
        uploadBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        uploadBtn.addActionListener(e -> openCsvFile());

        panel.add(title, BorderLayout.WEST);
        panel.add(uploadBtn, BorderLayout.EAST);
        return panel;
    }

    private JPanel buildCsvTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);

        csvStatusLabel = new JLabel("Requirement: CSV with Employee, Hours, Rate columns");
        csvStatusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));

        csvTableModel = new DefaultTableModel(new String[]{"Employee", "Hours", "Rate ($/hr)", "Subtotal"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        csvTable = new JTable(csvTableModel);
        csvTable.setRowHeight(25);
        csvTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        JScrollPane scrollPane = new JScrollPane(csvTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        panel.add(csvStatusLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 20, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        totalLaborLabel = new JLabel("Total Hours: 0.0");
        totalLaborLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

        totalCostLabel = new JLabel("Total Labor Cost: $0.00");
        totalCostLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalCostLabel.setForeground(new Color(40, 110, 40));

        panel.add(totalLaborLabel);
        panel.add(totalCostLabel);
        return panel;
    }

    private void openCsvFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            parseAndSyncCsv(chooser.getSelectedFile());
        }
    }

    private void parseAndSyncCsv(File file) {
        csvTableModel.setRowCount(0);
        double runningHours = 0;
        double runningCost = 0;
        int rowCount = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isHeader = true;

            while ((line = br.readLine()) != null) {
                if (isHeader) { isHeader = false; continue; } // Skip headers
                
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    try {
                        String name = parts[0].trim();
                        double hrs = Double.parseDouble(parts[1].trim());
                        double rate = Double.parseDouble(parts[2].trim());
                        double subtotal = hrs * rate;

                        csvTableModel.addRow(new Object[]{name, hrs, rate, String.format("$%.2f", subtotal)});
                        
                        runningHours += hrs;
                        runningCost += subtotal;
                        rowCount++;
                    } catch (NumberFormatException ignored) {}
                }
            }

            // Sync with InfoManager
            InfoManager info = InfoManager.getInstance();
            info.hours = runningHours;
            info.laborCost = runningCost;

            // Update UI Labels
            totalLaborLabel.setText("Total Hours: " + String.format("%.2f", runningHours));
            totalCostLabel.setText("Total Labor Cost: $" + String.format("%,.2f", runningCost));
            csvStatusLabel.setText("Successfully loaded " + rowCount + " records from " + file.getName());

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error reading CSV: " + ex.getMessage());
        }
    }
}