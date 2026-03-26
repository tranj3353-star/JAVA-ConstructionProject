package ui.panels;

import java.awt.*;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

public class LaborPanel extends JPanel {

    private JTextField numEmployeesField;
    private JTextField hoursPerEmployeeField;
    private JTextField hourlyRateField;
    private JLabel laborCostLabel;
    private JLabel totalHoursLabel;

    // CSV table
    private DefaultTableModel csvTableModel;
    private JTable csvTable;
    private JLabel csvStatusLabel;

    public LaborPanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(new Color(240, 240, 240));
        setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // Split: left = manual entry, right = CSV
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(360);
        splitPane.setDividerSize(8);
        splitPane.setOpaque(false);
        splitPane.setBorder(null);

        splitPane.setLeftComponent(buildManualPanel());
        splitPane.setRightComponent(buildCsvPanel());

        add(splitPane, BorderLayout.CENTER);
    }

    // -----------------------------------------------------------------------
    // Left: Manual labor entry
    // -----------------------------------------------------------------------
    private JPanel buildManualPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 16));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(9, 6, 9, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Labor Estimation");
        title.setFont(new Font("Segoe UI", Font.BOLD, 19));
        title.setForeground(new Color(30, 30, 30));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(title, gbc);
        gbc.gridwidth = 1;

        // Number of employees
        gbc.gridy = 1; gbc.gridx = 0; gbc.weightx = 0.45;
        panel.add(makeLabel("Number of Employees:"), gbc);
        numEmployeesField = makeField("e.g. 4");
        gbc.gridx = 1; gbc.weightx = 0.55;
        panel.add(numEmployeesField, gbc);

        // Hours per employee
        gbc.gridy = 2; gbc.gridx = 0;
        panel.add(makeLabel("Hours per Employee:"), gbc);
        hoursPerEmployeeField = makeField("e.g. 8");
        gbc.gridx = 1;
        panel.add(hoursPerEmployeeField, gbc);

        // Hourly rate
        gbc.gridy = 3; gbc.gridx = 0;
        panel.add(makeLabel("Hourly Rate ($/hr):"), gbc);
        hourlyRateField = makeField("e.g. 25.00");
        gbc.gridx = 1;
        panel.add(hourlyRateField, gbc);

        // Calculate button
        JButton calcBtn = new JButton("Calculate Labor Cost");
        calcBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        calcBtn.setBackground(new Color(70, 130, 200));
        calcBtn.setForeground(Color.WHITE);
        calcBtn.setFocusPainted(false);
        calcBtn.setBorderPainted(false);
        calcBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        calcBtn.addActionListener(e -> calculateLabor());
        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(14, 6, 6, 6);
        panel.add(calcBtn, gbc);
        gbc.insets = new Insets(9, 6, 9, 6);
        gbc.gridwidth = 1;

        // Results
        JPanel resultPanel = new JPanel(new GridLayout(2, 1, 4, 6));
        resultPanel.setBackground(new Color(255, 248, 225));
        resultPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 180, 80)),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));
        totalHoursLabel = new JLabel("Total Labor Hours: —");
        totalHoursLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        totalHoursLabel.setForeground(new Color(130, 90, 0));
        laborCostLabel = new JLabel("Labor Cost: —");
        laborCostLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        laborCostLabel.setForeground(new Color(100, 60, 0));
        resultPanel.add(totalHoursLabel);
        resultPanel.add(laborCostLabel);

        gbc.gridy = 5; gbc.gridx = 0; gbc.gridwidth = 2;
        panel.add(resultPanel, gbc);

        return panel;
    }

    // -----------------------------------------------------------------------
    // Right: CSV upload
    // -----------------------------------------------------------------------
    private JPanel buildCsvPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        JLabel title = new JLabel("Import Labor Data (CSV)");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(new Color(30, 30, 30));
        panel.add(title, BorderLayout.NORTH);

        // Instruction
        JLabel instructions = new JLabel("<html><i>CSV must have columns: Employee, Hours, Rate<br>One row per worker. Header row is skipped.</i></html>");
        instructions.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        instructions.setForeground(new Color(110, 110, 110));

        // Upload button
        JButton uploadBtn = new JButton("\uD83D\uDCC2  Choose CSV File...");
        uploadBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        uploadBtn.setBackground(new Color(90, 160, 90));
        uploadBtn.setForeground(Color.WHITE);
        uploadBtn.setFocusPainted(false);
        uploadBtn.setBorderPainted(false);
        uploadBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        uploadBtn.addActionListener(e -> openCsvFile());

        csvStatusLabel = new JLabel("No file loaded");
        csvStatusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        csvStatusLabel.setForeground(new Color(130, 130, 130));

        // Table
        csvTableModel = new DefaultTableModel(new String[]{"Employee", "Hours", "Rate ($/hr)"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        csvTable = new JTable(csvTableModel);
        csvTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        csvTable.setRowHeight(22);
        csvTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        JScrollPane tableScroll = new JScrollPane(csvTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        tableScroll.setPreferredSize(new Dimension(280, 160));

        // Apply CSV to fields button
        JButton applyBtn = new JButton("Apply to Labor Fields");
        applyBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        applyBtn.setBackground(new Color(70, 130, 200));
        applyBtn.setForeground(Color.WHITE);
        applyBtn.setFocusPainted(false);
        applyBtn.setBorderPainted(false);
        applyBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        applyBtn.addActionListener(e -> applyCsvToFields());

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        topBar.setOpaque(false);
        topBar.add(uploadBtn);
        topBar.add(csvStatusLabel);

        JPanel centerContent = new JPanel(new BorderLayout(0, 6));
        centerContent.setOpaque(false);
        centerContent.add(instructions, BorderLayout.NORTH);
        centerContent.add(topBar, BorderLayout.CENTER);
        centerContent.add(tableScroll, BorderLayout.SOUTH);

        panel.add(centerContent, BorderLayout.CENTER);
        panel.add(applyBtn, BorderLayout.SOUTH);

        return panel;
    }

    // -----------------------------------------------------------------------
    // CSV logic
    // -----------------------------------------------------------------------
    private void openCsvFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("CSV Files (*.csv)", "csv"));
        chooser.setDialogTitle("Select Labor CSV File");
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            parseCsv(file);
        }
    }

    private void parseCsv(File file) {
        csvTableModel.setRowCount(0);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            int rowsLoaded = 0;
            while ((line = br.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; } // skip header
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    csvTableModel.addRow(new Object[]{
                        parts[0].trim(),
                        parts[1].trim(),
                        parts[2].trim()
                    });
                    rowsLoaded++;
                }
            }
            csvStatusLabel.setText(file.getName() + " (" + rowsLoaded + " rows)");
            csvStatusLabel.setForeground(new Color(30, 130, 50));
        } catch (IOException ex) {
            csvStatusLabel.setText("Error reading file: " + ex.getMessage());
            csvStatusLabel.setForeground(new Color(180, 40, 40));
        }
    }

    private void applyCsvToFields() {
        int rowCount = csvTableModel.getRowCount();
        if (rowCount == 0) {
            JOptionPane.showMessageDialog(this, "No CSV data loaded.", "No Data", JOptionPane.WARNING_MESSAGE);
            return;
        }
        double totalHours = 0;
        double totalRate = 0;
        for (int i = 0; i < rowCount; i++) {
            try {
                totalHours += Double.parseDouble(csvTableModel.getValueAt(i, 1).toString());
                totalRate += Double.parseDouble(csvTableModel.getValueAt(i, 2).toString());
            } catch (NumberFormatException ignored) {}
        }
        double avgRate = totalRate / rowCount;
        numEmployeesField.setText(String.valueOf(rowCount));
        hoursPerEmployeeField.setText(String.format("%.2f", totalHours / rowCount));
        hourlyRateField.setText(String.format("%.2f", avgRate));

        numEmployeesField.setForeground(new Color(30, 30, 30));
        hoursPerEmployeeField.setForeground(new Color(30, 30, 30));
        hourlyRateField.setForeground(new Color(30, 30, 30));

        calculateLabor();
        JOptionPane.showMessageDialog(this,
            "Applied " + rowCount + " employees from CSV.\nAvg hourly rate: $" + String.format("%.2f", avgRate),
            "CSV Applied", JOptionPane.INFORMATION_MESSAGE);
    }

    private void calculateLabor() {
        try {
            int employees = Integer.parseInt(numEmployeesField.getText().trim());
            double hours = Double.parseDouble(hoursPerEmployeeField.getText().trim());
            double rate = Double.parseDouble(hourlyRateField.getText().trim());
            double totalHours = employees * hours;
            double cost = totalHours * rate;
            totalHoursLabel.setText(String.format("Total Labor Hours: %.1f hrs (%d \u00D7 %.1f)", totalHours, employees, hours));
            laborCostLabel.setText(String.format("Labor Cost: $%,.2f", cost));
            laborCostLabel.setForeground(new Color(100, 60, 0));
        } catch (NumberFormatException ex) {
            laborCostLabel.setText("Invalid input — check all fields");
            laborCostLabel.setForeground(new Color(180, 40, 40));
        }
    }

    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(new Color(50, 50, 50));
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
    public int getNumEmployees() {
        try { return Integer.parseInt(numEmployeesField.getText().trim()); } catch (Exception e) { return 0; }
    }
    public double getHoursPerEmployee() {
        try { return Double.parseDouble(hoursPerEmployeeField.getText().trim()); } catch (Exception e) { return 0; }
    }
    public double getHourlyRate() {
        try { return Double.parseDouble(hourlyRateField.getText().trim()); } catch (Exception e) { return 0; }
    }
    public double getTotalLaborCost() {
        return getNumEmployees() * getHoursPerEmployee() * getHourlyRate();
    }
    public double getTotalLaborHours() {
        return getNumEmployees() * getHoursPerEmployee();
    }
}
