package view;

import controller.ReportController;
import model.Patient;
import model.Appointment;
import model.Treatment;
import model.Bill;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class PatientReportPanel extends JPanel {
    
    // Color Palette
    private static final Color PRIMARY_DARK = new Color(0x2F3E3C);
    private static final Color MINT = new Color(0xBDDBD1);
    private static final Color SOFT_SURFACE = new Color(0xFBF9F1);
    private static final Color LIGHT_SURFACE = new Color(0xE7E9E3);
    private static final Color ERROR_COLOR = new Color(220, 80, 80);
    private static final Color SUCCESS_COLOR = new Color(60, 160, 80);
    private static final Color SECONDARY_TEXT = new Color(122, 138, 135);

    // Components
    private JComboBox<Patient> patientCombo;
    private RoundedButton generateButton;
    private RoundedButton refreshButton;
    private JLabel statusLabel;
    private JLabel patientInfoLabel;
    
    // Summary Cards - Store references directly
    private JLabel totalAppointmentsLabel;
    private JLabel totalTreatmentsLabel;
    private JLabel totalBillsLabel;
    private JLabel totalSpentLabel;
    
    // Tables
    private JTable appointmentTable;
    private DefaultTableModel appointmentTableModel;
    private JTable treatmentTable;
    private DefaultTableModel treatmentTableModel;
    private JTable billTable;
    private DefaultTableModel billTableModel;
    
    private ReportController controller;
    private DecimalFormat df = new DecimalFormat("#.00");
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private Patient selectedPatient;

    public PatientReportPanel() {
        this.controller = new ReportController(this);
        initComponents();
        loadPatients();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(SOFT_SURFACE);
        setBorder(new EmptyBorder(20, 30, 20, 30));

        // Title Panel - At the top
        add(createTitlePanel(), BorderLayout.NORTH);
        
        // Main Content Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(SOFT_SURFACE);
        
        // Filter Panel
        mainPanel.add(createFilterPanel());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Summary Cards
        mainPanel.add(createSummaryPanel());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Patient Info
        mainPanel.add(createPatientInfoPanel());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Appointment Table
        mainPanel.add(createAppointmentTablePanel());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Treatment Table
        mainPanel.add(createTreatmentTablePanel());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Bill Table
        mainPanel.add(createBillTablePanel());
        
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
        
        // Footer Panel
        add(createFooterPanel(), BorderLayout.SOUTH);
    }

    /**
     * ✅ Title Panel - Separate from other content
     */
    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(SOFT_SURFACE);
        titlePanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JLabel titleLabel = new JLabel("Patient Report");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_DARK);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("View comprehensive patient history and statistics");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(107, 123, 121));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 2)));
        titlePanel.add(subtitleLabel);
        
        return titlePanel;
    }

    /**
     * ✅ Filter Panel - Search and filter controls
     */
    private JPanel createFilterPanel() {
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            new EmptyBorder(10, 15, 10, 15)
        ));

        JLabel patientLabel = new JLabel("Select Patient:");
        patientLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        patientLabel.setForeground(PRIMARY_DARK);

        patientCombo = new JComboBox<>();
        patientCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        patientCombo.setPreferredSize(new Dimension(250, 35));
        patientCombo.addActionListener(e -> {
            selectedPatient = (Patient) patientCombo.getSelectedItem();
            if (selectedPatient != null && selectedPatient.getPatientId() > 0) {
                generateReport();
            }
        });

        generateButton = createStyledButton("Generate Report", PRIMARY_DARK, Color.WHITE);
        generateButton.setPreferredSize(new Dimension(150, 35));
        generateButton.addActionListener(e -> generateReport());

        refreshButton = createStyledButton("Refresh", SOFT_SURFACE, PRIMARY_DARK);
        refreshButton.setBorderColor(LIGHT_SURFACE);
        refreshButton.setPreferredSize(new Dimension(100, 35));
        refreshButton.addActionListener(e -> loadPatients());

        filterPanel.add(patientLabel);
        filterPanel.add(patientCombo);
        filterPanel.add(generateButton);
        filterPanel.add(refreshButton);

        return filterPanel;
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 15, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            new EmptyBorder(15, 20, 15, 20)
        ));

        // Total Appointments
        JPanel apptPanel = createSummaryCard("📋", "Total Appointments", "0", MINT);
        panel.add(apptPanel);

        // Total Treatments
        JPanel treatmentPanel = createSummaryCard("💊", "Total Treatments", "0", new Color(200, 220, 240));
        panel.add(treatmentPanel);

        // Total Bills
        JPanel billPanel = createSummaryCard("💰", "Total Bills", "0", new Color(240, 220, 200));
        panel.add(billPanel);

        // Total Spent
        JPanel spentPanel = createSummaryCard("💵", "Total Spent", "$0.00", new Color(200, 240, 220));
        panel.add(spentPanel);

        return panel;
    }

    private JPanel createSummaryCard(String icon, String title, String defaultValue, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(LIGHT_SURFACE, 1));
        card.setPreferredSize(new Dimension(180, 80));

        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(color);
        leftPanel.setPreferredSize(new Dimension(60, 80));
        leftPanel.setLayout(new GridBagLayout());
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        leftPanel.add(iconLabel);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(new EmptyBorder(5, 10, 5, 10));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        titleLabel.setForeground(SECONDARY_TEXT);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel valueLabel = new JLabel(defaultValue);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        valueLabel.setForeground(PRIMARY_DARK);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        rightPanel.add(titleLabel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 2)));
        rightPanel.add(valueLabel);

        card.add(leftPanel, BorderLayout.WEST);
        card.add(rightPanel, BorderLayout.CENTER);

        // Store reference to value label based on title
        if (title.equals("Total Appointments")) {
            totalAppointmentsLabel = valueLabel;
        } else if (title.equals("Total Treatments")) {
            totalTreatmentsLabel = valueLabel;
        } else if (title.equals("Total Bills")) {
            totalBillsLabel = valueLabel;
        } else if (title.equals("Total Spent")) {
            totalSpentLabel = valueLabel;
        }

        return card;
    }

    private JPanel createPatientInfoPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            new EmptyBorder(15, 20, 15, 20)
        ));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        patientInfoLabel = new JLabel("Please select a patient to view report");
        patientInfoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        patientInfoLabel.setForeground(SECONDARY_TEXT);
        patientInfoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(patientInfoLabel);

        return panel;
    }

    private JPanel createAppointmentTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(MINT, 1),
            "Appointment History",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            PRIMARY_DARK
        ));

        String[] columns = {"ID", "Date", "Time", "Dentist", "Status", "Reason"};
        appointmentTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        appointmentTable = new JTable(appointmentTableModel);
        appointmentTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        appointmentTable.setRowHeight(30);
        appointmentTable.setSelectionBackground(new Color(235, 245, 240));
        appointmentTable.setShowGrid(true);
        appointmentTable.setGridColor(LIGHT_SURFACE);

        JTableHeader header = appointmentTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(MINT);
        header.setForeground(PRIMARY_DARK);

        JScrollPane scrollPane = new JScrollPane(appointmentTable);
        scrollPane.setPreferredSize(new Dimension(600, 120));
        scrollPane.setBorder(BorderFactory.createLineBorder(LIGHT_SURFACE, 1));

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createTreatmentTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(MINT, 1),
            "Treatment History",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            PRIMARY_DARK
        ));

        String[] columns = {"ID", "Treatment", "Category", "Date", "Dentist", "Cost", "Status"};
        treatmentTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        treatmentTable = new JTable(treatmentTableModel);
        treatmentTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        treatmentTable.setRowHeight(30);
        treatmentTable.setSelectionBackground(new Color(235, 245, 240));
        treatmentTable.setShowGrid(true);
        treatmentTable.setGridColor(LIGHT_SURFACE);

        JTableHeader header = treatmentTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(MINT);
        header.setForeground(PRIMARY_DARK);

        JScrollPane scrollPane = new JScrollPane(treatmentTable);
        scrollPane.setPreferredSize(new Dimension(600, 120));
        scrollPane.setBorder(BorderFactory.createLineBorder(LIGHT_SURFACE, 1));

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createBillTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(MINT, 1),
            "Billing History",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            PRIMARY_DARK
        ));

        String[] columns = {"ID", "Bill Number", "Date", "Total", "Paid", "Balance", "Status"};
        billTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        billTable = new JTable(billTableModel);
        billTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        billTable.setRowHeight(30);
        billTable.setSelectionBackground(new Color(235, 245, 240));
        billTable.setShowGrid(true);
        billTable.setGridColor(LIGHT_SURFACE);

        JTableHeader header = billTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(MINT);
        header.setForeground(PRIMARY_DARK);

        JScrollPane scrollPane = new JScrollPane(billTable);
        scrollPane.setPreferredSize(new Dimension(600, 120));
        scrollPane.setBorder(BorderFactory.createLineBorder(LIGHT_SURFACE, 1));

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(SOFT_SURFACE);
        footer.setBorder(new EmptyBorder(10, 0, 0, 0));

        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(107, 123, 121));

        footer.add(statusLabel, BorderLayout.WEST);

        return footer;
    }

    private RoundedButton createStyledButton(String text, Color bg, Color fg) {
        RoundedButton button = new RoundedButton(text, bg, fg);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return button;
    }

    private static class RoundedButton extends JButton {
        private Color bg;
        private Color borderColor;
        private Color hoverColor;
        private Color originalBg;

        RoundedButton(String text, Color bg, Color fg) {
            super(text);
            this.bg = bg;
            this.originalBg = bg;
            this.borderColor = bg;

            setForeground(fg);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            if (bg.equals(PRIMARY_DARK)) {
                hoverColor = new Color(40, 55, 53);
            } else {
                hoverColor = new Color(220, 220, 210);
            }

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    RoundedButton.this.bg = hoverColor;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    RoundedButton.this.bg = originalBg;
                    repaint();
                }
            });
        }

        public void setBorderColor(Color c) {
            this.borderColor = c;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

            if (borderColor != bg && borderColor != null) {
                g2.setColor(borderColor);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            }

            super.paintComponent(g);
            g2.dispose();
        }
    }

    private static class MouseAdapter extends java.awt.event.MouseAdapter {
        // Empty implementation
    }

    // ========================
    // Data Loading Methods
    // ========================

    private void loadPatients() {
        List<Patient> patients = controller.getAllPatients();
        patientCombo.removeAllItems();
        // Add placeholder item
        Patient placeholder = new Patient();
        placeholder.setPatientName("-- Select Patient --");
        patientCombo.addItem(placeholder);
        
        if (patients != null) {
            for (Patient patient : patients) {
                patientCombo.addItem(patient);
            }
        }
    }

    private void generateReport() {
        selectedPatient = (Patient) patientCombo.getSelectedItem();
        if (selectedPatient == null || selectedPatient.getPatientId() <= 0) {
            showError("Please select a valid patient.");
            return;
        }

        statusLabel.setText("Loading patient report...");
        setCursor(new Cursor(Cursor.WAIT_CURSOR));

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            private Patient patientData;
            private List<Appointment> appointments;
            private List<Treatment> treatments;
            private List<Bill> bills;

            @Override
            protected Void doInBackground() throws Exception {
                patientData = controller.getPatientDetails(selectedPatient.getPatientId());
                appointments = controller.getAppointmentsByPatient(selectedPatient.getPatientId());
                treatments = controller.getTreatmentsByPatient(selectedPatient.getPatientId());
                bills = controller.getBillsByPatient(selectedPatient.getPatientId());
                return null;
            }

            @Override
            protected void done() {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                try {
                    displayReport(patientData, appointments, treatments, bills);
                    statusLabel.setText("Report generated successfully!");
                    statusLabel.setForeground(SUCCESS_COLOR);
                } catch (Exception e) {
                    showError("Error generating report: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void displayReport(Patient patient, List<Appointment> appointments, 
                               List<Treatment> treatments, List<Bill> bills) {
        if (patient == null) {
            showError("Patient not found.");
            return;
        }

        // Update patient info
        String info = "<html><b>Patient:</b> " + patient.getPatientName() + 
                     " &nbsp;|&nbsp; <b>Gender:</b> " + (patient.getGender() != null ? patient.getGender() : "N/A") +
                     " &nbsp;|&nbsp; <b>Contact:</b> " + patient.getContactNumber() +
                     " &nbsp;|&nbsp; <b>Email:</b> " + (patient.getEmail() != null ? patient.getEmail() : "N/A") +
                     " &nbsp;|&nbsp; <b>DOB:</b> " + (patient.getDateOfBirth() != null ? patient.getDateOfBirth().toString() : "N/A") +
                     "</html>";
        patientInfoLabel.setText(info);

        // Update summary
        int apptCount = appointments != null ? appointments.size() : 0;
        int treatmentCount = treatments != null ? treatments.size() : 0;
        int billCount = bills != null ? bills.size() : 0;
        double totalSpent = 0;
        if (bills != null) {
            for (Bill bill : bills) {
                if ("Paid".equals(bill.getStatus()) || "Partial".equals(bill.getStatus())) {
                    totalSpent += bill.getTotalAmount();
                }
            }
        }

        totalAppointmentsLabel.setText(String.valueOf(apptCount));
        totalTreatmentsLabel.setText(String.valueOf(treatmentCount));
        totalBillsLabel.setText(String.valueOf(billCount));
        totalSpentLabel.setText("$" + df.format(totalSpent));

        // Update appointment table
        appointmentTableModel.setRowCount(0);
        if (appointments != null) {
            for (Appointment appt : appointments) {
                Object[] row = {
                    appt.getAppointmentId(),
                    appt.getAppointmentDate() != null ? sdf.format(appt.getAppointmentDate()) : "N/A",
                    appt.getAppointmentTime() != null ? appt.getAppointmentTime().toString().substring(0, 5) : "N/A",
                    controller.getDentistName(appt.getDentistId()),
                    appt.getStatus() != null ? appt.getStatus() : "N/A",
                    appt.getReason() != null ? appt.getReason() : "N/A"
                };
                appointmentTableModel.addRow(row);
            }
        }

        // Update treatment table
        treatmentTableModel.setRowCount(0);
        if (treatments != null) {
            for (Treatment treatment : treatments) {
                Object[] row = {
                    treatment.getTreatmentId(),
                    treatment.getTreatmentName(),
                    treatment.getCategory() != null ? treatment.getCategory() : "N/A",
                    treatment.getCreatedAt() != null ? treatment.getCreatedAt().substring(0, 10) : "N/A",
                    "N/A",
                    "$" + df.format(treatment.getCost()),
                    treatment.isActive() ? "Active" : "Inactive"
                };
                treatmentTableModel.addRow(row);
            }
        }

        // Update bill table
        billTableModel.setRowCount(0);
        if (bills != null) {
            for (Bill bill : bills) {
                Object[] row = {
                    bill.getBillId(),
                    bill.getBillNumber(),
                    bill.getBillDate() != null ? sdf.format(bill.getBillDate()) : "N/A",
                    "$" + df.format(bill.getTotalAmount()),
                    "$" + df.format(bill.getAmountPaid()),
                    "$" + df.format(bill.getBalance()),
                    bill.getStatus() != null ? bill.getStatus() : "N/A"
                };
                billTableModel.addRow(row);
            }
        }
    }

    // ========================
    // Public methods
    // ========================

    public void showError(String message) {
        statusLabel.setText("❌ " + message);
        statusLabel.setForeground(ERROR_COLOR);
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showSuccess(String message) {
        statusLabel.setText("✅ " + message);
        statusLabel.setForeground(SUCCESS_COLOR);
    }

    public void showInfo(String message) {
        statusLabel.setText("ℹ️ " + message);
        statusLabel.setForeground(new Color(107, 123, 121));
    }
}