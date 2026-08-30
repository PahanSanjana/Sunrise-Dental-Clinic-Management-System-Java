package view;

import controller.ReportController;
import model.Patient;
import model.Appointment;
import model.Treatment;
import model.Bill;
import model.User;
import model.LoginSession;
import model.User.UserRole;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    
    // Refresh button colors
    private static final Color COLOR_REFRESH = new Color(52, 152, 219);
    private static final Color COLOR_REFRESH_HOVER = new Color(41, 128, 185);

    private static final String UI_FONT_FAMILY = "Segoe UI";

    // =====================================================
    // ICON HELPERS (Ikonli FontIcon)
    // =====================================================
    private static FontIcon icon(FontAwesomeSolid glyph, int size, Color color) {
        return FontIcon.of(glyph, size, color);
    }

    private static JLabel iconLabel(FontAwesomeSolid glyph, int size, Color color) {
        return new JLabel(icon(glyph, size, color));
    }

    // Components
    private JComboBox<Patient> patientCombo;
    private RoundedButton generateButton;
    private JLabel statusLabel;
    private JLabel lastUpdatedLabel;
    private JLabel patientInfoLabel;
    private JLabel patientNameDisplayLabel;
    
    // Summary Cards - Store references directly
    private JLabel totalAppointmentsLabel;
    private JLabel totalBillsLabel;
    private JLabel totalSpentLabel;
    
    // Tables
    private JTable appointmentTable;
    private DefaultTableModel appointmentTableModel;
    private JTable billTable;
    private DefaultTableModel billTableModel;
    
    private ReportController controller;
    private DecimalFormat df = new DecimalFormat("#.00");
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private Patient selectedPatient;
    private User currentUser;
    private boolean isPatientRole = false;

    // Auto-refresh timer (hidden)
    private Timer refreshTimer;
    private static final int AUTO_REFRESH_DELAY = 30000; // 30 seconds

    public PatientReportPanel() {
        this.controller = new ReportController(this);
        this.currentUser = LoginSession.getInstance().getCurrentUser();
        initComponents();
        loadPatientsBasedOnRole();
        startAutoRefresh();
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
        
        // Bill Table
        mainPanel.add(createBillTablePanel());
        
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
        
        // Footer Panel
        add(createFooterPanel(), BorderLayout.SOUTH);
    }

    // =====================================================
    // AUTO-REFRESH (Hidden - No UI Indicator)
    // =====================================================

    private void startAutoRefresh() {
        if (refreshTimer == null) {
            refreshTimer = new Timer(AUTO_REFRESH_DELAY, e -> {
                if (isShowing() && selectedPatient != null && selectedPatient.getPatientId() > 0) {
                    generateReport();
                }
            });
            refreshTimer.start();
        }
    }

    private void stopAutoRefresh() {
        if (refreshTimer != null) {
            refreshTimer.stop();
            refreshTimer = null;
        }
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        stopAutoRefresh();
    }

    // =====================================================
    // CREATE ICON BUTTON (No text, only icon)
    // =====================================================
    private JButton createIconButton(FontAwesomeSolid glyph, Color bg) {
        JButton button = new JButton(icon(glyph, 18, Color.WHITE));
        button.setBackground(bg);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setBorderPainted(false);

        Color originalBg = bg;
        Color hoverBg = bg.equals(COLOR_REFRESH) ? COLOR_REFRESH_HOVER : new Color(40, 55, 53);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverBg);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(originalBg);
            }
        });

        return button;
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
        titleLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_DARK);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel(getSubtitleBasedOnRole());
        subtitleLabel.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(107, 123, 121));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 2)));
        titlePanel.add(subtitleLabel);
        
        return titlePanel;
    }

    private String getSubtitleBasedOnRole() {
        if (currentUser == null) return "View comprehensive patient history and statistics";
        
        if (currentUser.isPatient()) {
            return "View your personal health records and statistics";
        }
        return "View comprehensive patient history and statistics";
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
        patientLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        patientLabel.setForeground(PRIMARY_DARK);

        patientCombo = new JComboBox<>();
        patientCombo.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        patientCombo.setPreferredSize(new Dimension(250, 35));
        patientCombo.addActionListener(e -> {
            selectedPatient = (Patient) patientCombo.getSelectedItem();
            if (selectedPatient != null && selectedPatient.getPatientId() > 0) {
                generateReport();
            }
        });

        // For patient role, disable the combo box and hide the label
        if (currentUser != null && currentUser.isPatient()) {
            patientCombo.setEnabled(false);
            patientLabel.setVisible(false);
            // Show patient name instead
            patientNameDisplayLabel = new JLabel("");
            patientNameDisplayLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
            patientNameDisplayLabel.setForeground(PRIMARY_DARK);
            filterPanel.add(patientNameDisplayLabel);
        } else {
            filterPanel.add(patientLabel);
            filterPanel.add(patientCombo);
        }

        generateButton = createStyledButton("Generate Report", PRIMARY_DARK, Color.WHITE);
        generateButton.setPreferredSize(new Dimension(150, 35));
        generateButton.addActionListener(e -> generateReport());
        generateButton.setIcon(icon(FontAwesomeSolid.USER_MD, 14, Color.WHITE));
        generateButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        generateButton.setIconTextGap(8);
        filterPanel.add(generateButton);
        
        // Manual Refresh Button - ICON ONLY
        JButton refreshBtn = createIconButton(FontAwesomeSolid.SYNC_ALT, COLOR_REFRESH);
        refreshBtn.setPreferredSize(new Dimension(40, 40));
        refreshBtn.setToolTipText("Refresh Now");
        refreshBtn.addActionListener(e -> loadPatientsBasedOnRole());
        filterPanel.add(refreshBtn);

        return filterPanel;
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 15, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            new EmptyBorder(15, 20, 15, 20)
        ));

        // Total Appointments
        JPanel apptPanel = createSummaryCard(FontAwesomeSolid.CALENDAR_ALT, "Total Appointments", "0", MINT);
        panel.add(apptPanel);

        // Total Bills
        JPanel billPanel = createSummaryCard(FontAwesomeSolid.FILE_INVOICE, "Total Bills", "0", new Color(240, 220, 200));
        panel.add(billPanel);

        // Total Spent
        JPanel spentPanel = createSummaryCard(FontAwesomeSolid.MONEY_BILL_WAVE, "Total Spent", "RS0.00", new Color(200, 240, 220));
        panel.add(spentPanel);

        return panel;
    }

    private JPanel createSummaryCard(FontAwesomeSolid glyph, String title, String defaultValue, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(LIGHT_SURFACE, 1));
        card.setPreferredSize(new Dimension(180, 80));

        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(color);
        leftPanel.setPreferredSize(new Dimension(60, 80));
        leftPanel.setLayout(new GridBagLayout());
        
        JLabel iconLabel = iconLabel(glyph, 28, Color.WHITE);
        leftPanel.add(iconLabel);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(new EmptyBorder(5, 10, 5, 10));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 11));
        titleLabel.setForeground(SECONDARY_TEXT);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel valueLabel = new JLabel(defaultValue);
        valueLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 18));
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
        panel.setLayout(new BorderLayout());

        patientInfoLabel = new JLabel("Please select a patient to view report");
        patientInfoLabel.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 14));
        patientInfoLabel.setForeground(SECONDARY_TEXT);
        patientInfoLabel.setHorizontalAlignment(SwingConstants.LEFT);
        panel.add(patientInfoLabel, BorderLayout.CENTER);

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
            new Font(UI_FONT_FAMILY, Font.BOLD, 14),
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
        appointmentTable.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 12));
        appointmentTable.setRowHeight(30);
        appointmentTable.setSelectionBackground(new Color(235, 245, 240));
        appointmentTable.setShowGrid(true);
        appointmentTable.setGridColor(LIGHT_SURFACE);

        JTableHeader header = appointmentTable.getTableHeader();
        header.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 12));
        header.setBackground(MINT);
        header.setForeground(PRIMARY_DARK);

        JScrollPane scrollPane = new JScrollPane(appointmentTable);
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
            new Font(UI_FONT_FAMILY, Font.BOLD, 14),
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
        billTable.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 12));
        billTable.setRowHeight(30);
        billTable.setSelectionBackground(new Color(235, 245, 240));
        billTable.setShowGrid(true);
        billTable.setGridColor(LIGHT_SURFACE);

        JTableHeader header = billTable.getTableHeader();
        header.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 12));
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
        statusLabel.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 12));
        statusLabel.setForeground(new Color(107, 123, 121));

        lastUpdatedLabel = new JLabel("Last updated: --");
        lastUpdatedLabel.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 12));
        lastUpdatedLabel.setForeground(new Color(107, 123, 121));
        lastUpdatedLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        footer.add(statusLabel, BorderLayout.WEST);
        footer.add(lastUpdatedLabel, BorderLayout.EAST);

        return footer;
    }

    private RoundedButton createStyledButton(String text, Color bg, Color fg) {
        RoundedButton button = new RoundedButton(text, bg, fg);
        button.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 12));
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

    private void loadPatientsBasedOnRole() {
        patientCombo.removeAllItems();
        
        if (currentUser == null) {
            showError("Please login first.");
            return;
        }

        UserRole role = currentUser.getRole();

        // If user is a patient, load only their own profile
        if (role == UserRole.PATIENT) {
            isPatientRole = true;
            Integer patientId = currentUser.getPatientId();
            if (patientId != null && patientId > 0) {
                Patient loggedInPatient = controller.getPatientById(patientId);
                if (loggedInPatient != null) {
                    patientCombo.addItem(loggedInPatient);
                    patientCombo.setSelectedItem(loggedInPatient);
                    patientCombo.setEnabled(false);
                    
                    // Update display name
                    if (patientNameDisplayLabel != null) {
                        patientNameDisplayLabel.setText("Patient: " + loggedInPatient.getPatientName());
                    }
                    
                    selectedPatient = loggedInPatient;
                    generateReport();
                } else {
                    showError("No patient profile found for your account.");
                }
            } else {
                showError("No patient profile found for your account.");
            }
        } else {
            // For ADMIN, RECEPTION, and DENTIST - show all patients
            isPatientRole = false;
            List<Patient> patients = controller.getAllPatients();
            // Add placeholder item
            Patient placeholder = new Patient();
            placeholder.setPatientName("-- Select Patient --");
            placeholder.setPatientId(0);
            patientCombo.addItem(placeholder);
            
            if (patients != null) {
                for (Patient patient : patients) {
                    patientCombo.addItem(patient);
                }
            }
            patientCombo.setEnabled(true);
            
            // Hide the patient name display label
            if (patientNameDisplayLabel != null) {
                patientNameDisplayLabel.setText("");
            }
        }
    }

    private void generateReport() {
        selectedPatient = (Patient) patientCombo.getSelectedItem();
        
        // For patient role, selectedPatient should already be set
        if (isPatientRole && selectedPatient == null) {
            showError("No patient profile found.");
            return;
        }
        
        if (selectedPatient == null || selectedPatient.getPatientId() <= 0) {
            showError("Please select a valid patient.");
            return;
        }

        statusLabel.setText("Loading patient report...");
        setCursor(new Cursor(Cursor.WAIT_CURSOR));

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            private Patient patientData;
            private List<Appointment> appointments;
            private List<Bill> bills;

            @Override
            protected Void doInBackground() throws Exception {
                patientData = controller.getPatientDetails(selectedPatient.getPatientId());
                appointments = controller.getAppointmentsByPatient(selectedPatient.getPatientId());
                bills = controller.getBillsByPatient(selectedPatient.getPatientId());
                return null;
            }

            @Override
            protected void done() {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                try {
                    displayReport(patientData, appointments, bills);
                    statusLabel.setText("Report generated successfully!");
                    statusLabel.setForeground(SUCCESS_COLOR);
                    
                    lastUpdatedLabel.setText("Last updated: " + 
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                } catch (Exception e) {
                    showError("Error generating report: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void displayReport(Patient patient, List<Appointment> appointments, List<Bill> bills) {
        if (patient == null) {
            showError("Patient not found.");
            return;
        }

        // Update patient info with proper HTML formatting
        StringBuilder info = new StringBuilder();
        info.append("<html><body style='width: 100%;'>");
        info.append("<table cellpadding='4' cellspacing='0' style='border-collapse: collapse;'>");
        info.append("<tr><td><b>Patient:</b></td><td>").append(patient.getPatientName()).append("</td>");
        info.append("<td style='padding-left: 30px;'><b>Gender:</b></td><td>").append(patient.getGender() != null ? patient.getGender() : "N/A").append("</td>");
        info.append("<td style='padding-left: 30px;'><b>Contact:</b></td><td>").append(patient.getContactNumber() != null ? patient.getContactNumber() : "N/A").append("</td></tr>");
        info.append("<tr><td><b>Email:</b></td><td>").append(patient.getEmail() != null ? patient.getEmail() : "N/A").append("</td>");
        info.append("<td style='padding-left: 30px;'><b>DOB:</b></td><td>").append(patient.getDateOfBirth() != null ? patient.getDateOfBirth().toString() : "N/A").append("</td>");
        info.append("<td style='padding-left: 30px;'><b>Address:</b></td><td>").append(patient.getAddress() != null ? patient.getAddress() : "N/A").append("</td></tr>");
        info.append("</table></body></html>");

        patientInfoLabel.setText(info.toString());

        // Update summary
        int apptCount = appointments != null ? appointments.size() : 0;
        int billCount = bills != null ? bills.size() : 0;
        double totalSpent = 0;
        if (bills != null) {
            for (Bill bill : bills) {
                // Only count bills that are paid or partially paid
                if ("Paid".equals(bill.getStatus()) || "Partial".equals(bill.getStatus())) {
                    totalSpent += bill.getTotalAmount();
                }
            }
        }

        totalAppointmentsLabel.setText(String.valueOf(apptCount));
        totalBillsLabel.setText(String.valueOf(billCount));
        totalSpentLabel.setText("RS" + df.format(totalSpent));

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

        // Update bill table
        billTableModel.setRowCount(0);
        if (bills != null) {
            for (Bill bill : bills) {
                Object[] row = {
                    bill.getBillId(),
                    bill.getBillNumber(),
                    bill.getBillDate() != null ? sdf.format(bill.getBillDate()) : "N/A",
                    "RS" + df.format(bill.getTotalAmount()),
                    "RS" + df.format(bill.getAmountPaid()),
                    "RS" + df.format(bill.getBalance()),
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
        statusLabel.setIcon(icon(FontAwesomeSolid.TIMES_CIRCLE, 14, ERROR_COLOR));
        statusLabel.setText(message);
        statusLabel.setForeground(ERROR_COLOR);
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showSuccess(String message) {
        statusLabel.setIcon(icon(FontAwesomeSolid.CHECK_CIRCLE, 14, SUCCESS_COLOR));
        statusLabel.setText(message);
        statusLabel.setForeground(SUCCESS_COLOR);
    }

    public void showInfo(String message) {
        statusLabel.setIcon(icon(FontAwesomeSolid.INFO_CIRCLE, 14, new Color(107, 123, 121)));
        statusLabel.setText(message);
        statusLabel.setForeground(new Color(107, 123, 121));
    }
}