package view;

import controller.ReportController;
import model.Bill;
import model.Appointment;
import model.Patient;
import model.Dentist;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class ReportDashboardPanel extends JPanel {
    
    // Color Palette
    private static final Color PRIMARY_DARK = new Color(0x2F3E3C);
    private static final Color MINT = new Color(0xBDDBD1);
    private static final Color SOFT_SURFACE = new Color(0xFBF9F1);
    private static final Color LIGHT_SURFACE = new Color(0xE7E9E3);
    private static final Color ERROR_COLOR = new Color(220, 80, 80);
    private static final Color SUCCESS_COLOR = new Color(60, 160, 80);
    private static final Color SECONDARY_TEXT = new Color(122, 138, 135);
    private static final Color HOVER_SURFACE = new Color(0xE8F0F1);
    // Card Colors
    private static final Color COLOR_PATIENTS = new Color(52, 152, 219);
    private static final Color COLOR_APPOINTMENTS = new Color(46, 204, 113);
    private static final Color COLOR_REVENUE = new Color(241, 196, 15);
    private static final Color COLOR_DENTISTS = new Color(155, 89, 182);
    private static final Color COLOR_TREATMENTS = new Color(231, 76, 60);
    private static final Color COLOR_STAFF = new Color(149, 165, 166);

    // Components
    private JLabel statusLabel;
    private JLabel lastUpdatedLabel;
    private JPanel cardPanel;
    
    // Quick Stats Cards
    private JLabel totalPatientsLabel;
    private JLabel totalAppointmentsLabel;
    private JLabel totalRevenueLabel;
    private JLabel totalDentistsLabel;
    private JLabel totalTreatmentsLabel;
    private JLabel totalStaffLabel;
    
    // Recent Activity
    private JList<String> recentActivityList;
    private DefaultListModel<String> activityModel;
    
    private ReportController controller;
    private DecimalFormat df = new DecimalFormat("#.00");
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public ReportDashboardPanel() {
        this.controller = new ReportController(this);
        initComponents();
        loadDashboardData();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(SOFT_SURFACE);
        setBorder(new EmptyBorder(20, 30, 20, 30));

        // Header Panel
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Main Content Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(SOFT_SURFACE);
        
        // Quick Stats Cards
        mainPanel.add(createStatsPanel());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Quick Actions and Recent Activity
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        bottomPanel.setOpaque(false);
        bottomPanel.add(createQuickActionsPanel());
        bottomPanel.add(createRecentActivityPanel());
        mainPanel.add(bottomPanel);
        
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
        
        // Footer Panel
        add(createFooterPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(SOFT_SURFACE);
        header.setBorder(new EmptyBorder(0, 0, 15, 0));

        // Title
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Report Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_DARK);
        
        JLabel subtitleLabel = new JLabel("Overview of clinic performance and key metrics");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(107, 123, 121));
        
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);

        header.add(titlePanel, BorderLayout.WEST);
        
        // Refresh button
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        
        JButton refreshBtn = createStyledButton("⟳ Refresh", PRIMARY_DARK, Color.WHITE);
        refreshBtn.setPreferredSize(new Dimension(120, 35));
        refreshBtn.addActionListener(e -> loadDashboardData());
        rightPanel.add(refreshBtn);
        
        header.add(rightPanel, BorderLayout.EAST);

        return header;
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            new EmptyBorder(20, 20, 20, 20)
        ));

        // Total Patients
        JPanel patientCard = createStatCard("👤", "Total Patients", "0", COLOR_PATIENTS);
        panel.add(patientCard);
        totalPatientsLabel = findValueLabel(patientCard);

        // Total Appointments
        JPanel appointmentCard = createStatCard("📋", "Total Appointments", "0", COLOR_APPOINTMENTS);
        panel.add(appointmentCard);
        totalAppointmentsLabel = findValueLabel(appointmentCard);

        // Total Revenue
        JPanel revenueCard = createStatCard("💰", "Total Revenue", "$0.00", COLOR_REVENUE);
        panel.add(revenueCard);
        totalRevenueLabel = findValueLabel(revenueCard);

        // Total Dentists
        JPanel dentistCard = createStatCard("🦷", "Total Dentists", "0", COLOR_DENTISTS);
        panel.add(dentistCard);
        totalDentistsLabel = findValueLabel(dentistCard);

        // Total Treatments
        JPanel treatmentCard = createStatCard("💊", "Total Treatments", "0", COLOR_TREATMENTS);
        panel.add(treatmentCard);
        totalTreatmentsLabel = findValueLabel(treatmentCard);

        // Total Staff
        JPanel staffCard = createStatCard("👨‍💼", "Total Staff", "0", COLOR_STAFF);
        panel.add(staffCard);
        totalStaffLabel = findValueLabel(staffCard);

        return panel;
    }

    private JPanel createStatCard(String icon, String title, String defaultValue, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        card.setPreferredSize(new Dimension(200, 80));

        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(color);
        leftPanel.setPreferredSize(new Dimension(50, 80));
        leftPanel.setLayout(new GridBagLayout());
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
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

        return card;
    }

    private JLabel findValueLabel(JPanel card) {
        JPanel rightPanel = (JPanel) card.getComponent(1);
        return (JLabel) rightPanel.getComponent(2);
    }

    private JPanel createQuickActionsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            new EmptyBorder(15, 20, 15, 20)
        ));

        JLabel titleLabel = new JLabel("Quick Reports");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(PRIMARY_DARK);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Action buttons
        String[][] actions = {
            {"👤", "Patient Report", "View detailed patient history"},
            {"📅", "Schedule Report", "View appointment statistics"},
            {"💰", "Revenue Report", "View financial overview"}
        };

        for (String[] action : actions) {
            JPanel actionPanel = createActionButton(action[0], action[1], action[2]);
            panel.add(actionPanel);
            panel.add(Box.createRigidArea(new Dimension(0, 8)));
        }

        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel createActionButton(String icon, String title, String description) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            new EmptyBorder(10, 15, 10, 15)
        ));
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                navigateToReport(title);
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setBackground(HOVER_SURFACE);
                panel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(MINT, 1),
                    new EmptyBorder(10, 15, 10, 15)
                ));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                panel.setBackground(Color.WHITE);
                panel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
                    new EmptyBorder(10, 15, 10, 15)
                ));
            }
        });

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        panel.add(iconLabel, BorderLayout.WEST);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.setBorder(new EmptyBorder(0, 10, 0, 0));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(PRIMARY_DARK);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        descLabel.setForeground(SECONDARY_TEXT);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        textPanel.add(titleLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 2)));
        textPanel.add(descLabel);
        
        panel.add(textPanel, BorderLayout.CENTER);
        
        JLabel arrowLabel = new JLabel("→");
        arrowLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        arrowLabel.setForeground(SECONDARY_TEXT);
        panel.add(arrowLabel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createRecentActivityPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            new EmptyBorder(15, 20, 15, 20)
        ));

        JLabel titleLabel = new JLabel("Recent Activity");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(PRIMARY_DARK);
        panel.add(titleLabel, BorderLayout.NORTH);

        activityModel = new DefaultListModel<>();
        recentActivityList = new JList<>(activityModel);
        recentActivityList.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        recentActivityList.setBackground(Color.WHITE);
        recentActivityList.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        recentActivityList.setCellRenderer(new ActivityListRenderer());

        JScrollPane scrollPane = new JScrollPane(recentActivityList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setPreferredSize(new Dimension(300, 200));

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

        lastUpdatedLabel = new JLabel("Last updated: --");
        lastUpdatedLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lastUpdatedLabel.setForeground(new Color(107, 123, 121));
        lastUpdatedLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        footer.add(statusLabel, BorderLayout.WEST);
        footer.add(lastUpdatedLabel, BorderLayout.EAST);

        return footer;
    }

    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(bg);
        button.setForeground(fg);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(40, 55, 53));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(PRIMARY_DARK);
            }
        });
        
        return button;
    }

    // Custom list cell renderer for activity
    private class ActivityListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            
            JLabel label = (JLabel) super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);
            
            label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, LIGHT_SURFACE),
                BorderFactory.createEmptyBorder(8, 5, 8, 5)
            ));
            
            if (isSelected) {
                label.setBackground(new Color(235, 245, 240));
                label.setForeground(PRIMARY_DARK);
            }
            
            return label;
        }
    }

    // ========================
    // Data Loading Methods
    // ========================

    private void loadDashboardData() {
        statusLabel.setText("Loading dashboard data...");
        setCursor(new Cursor(Cursor.WAIT_CURSOR));

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            private int patientCount;
            private int appointmentCount;
            private double revenue;
            private int dentistCount;
            private int treatmentCount;
            private int staffCount;
            private List<String> activities;

            @Override
            protected Void doInBackground() throws Exception {
                // Get counts from controllers
                patientCount = controller.getPatientCount();
                appointmentCount = controller.getAppointmentCount();
                revenue = controller.getTotalRevenue();
                dentistCount = controller.getDentistCount();
                treatmentCount = controller.getTreatmentCount();
                staffCount = controller.getStaffCount();
                
                // Generate recent activities
                activities = generateRecentActivities();
                
                return null;
            }

            @Override
            protected void done() {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                try {
                    displayStats(patientCount, appointmentCount, revenue, 
                                dentistCount, treatmentCount, staffCount);
                    displayActivities(activities);
                    statusLabel.setText("Dashboard updated successfully!");
                    statusLabel.setForeground(SUCCESS_COLOR);
                    lastUpdatedLabel.setText("Last updated: " + 
                        LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                } catch (Exception e) {
                    showError("Error loading dashboard data: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void displayStats(int patients, int appointments, double revenue,
                              int dentists, int treatments, int staff) {
        totalPatientsLabel.setText(String.valueOf(patients));
        totalAppointmentsLabel.setText(String.valueOf(appointments));
        totalRevenueLabel.setText("$" + df.format(revenue));
        totalDentistsLabel.setText(String.valueOf(dentists));
        totalTreatmentsLabel.setText(String.valueOf(treatments));
        totalStaffLabel.setText(String.valueOf(staff));
    }

    private List<String> generateRecentActivities() {
        List<String> activities = new java.util.ArrayList<>();
        
        // Get recent appointments
        List<Appointment> recentAppointments = controller.getRecentAppointments(5);
        if (recentAppointments != null) {
            for (Appointment appt : recentAppointments) {
                String patientName = controller.getPatientName(appt.getPatientId());
                String date = appt.getAppointmentDate() != null ? 
                    appt.getAppointmentDate().toString() : "N/A";
                activities.add("📋 Appointment: " + patientName + " on " + date + 
                    " (" + appt.getStatus() + ")");
            }
        }
        
        // Get recent bills
        List<Bill> recentBills = controller.getRecentBills(3);
        if (recentBills != null) {
            for (Bill bill : recentBills) {
                String patientName = controller.getPatientName(bill.getPatientId());
                activities.add("💰 Bill: " + bill.getBillNumber() + 
                    " - " + patientName + " ($" + df.format(bill.getTotalAmount()) + ")");
            }
        }
        
        return activities;
    }

    private void displayActivities(List<String> activities) {
        activityModel.clear();
        if (activities != null) {
            for (String activity : activities) {
                activityModel.addElement(activity);
            }
        }
        if (activityModel.isEmpty()) {
            activityModel.addElement("No recent activity");
        }
    }

    private void navigateToReport(String reportType) {
        Container parent = getParent();
        while (parent != null && !(parent instanceof MainFrame)) {
            parent = parent.getParent();
        }
        if (parent instanceof MainFrame) {
            MainFrame mainFrame = (MainFrame) parent;
            switch (reportType) {
                case "Patient Report":
                    mainFrame.showCard("REPORT_PATIENT");
                    break;
                case "Schedule Report":
                    mainFrame.showCard("REPORT_SCHEDULE");
                    break;
                case "Revenue Report":
                    mainFrame.showCard("REPORT_REVENUE");
                    break;
                default:
                    break;
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

    private static class MouseAdapter extends java.awt.event.MouseAdapter {
        // Empty implementation
    }
}