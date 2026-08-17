package view;

import controller.ReportController;
import model.Appointment;
import model.Patient;
import model.Dentist;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class ScheduleReportPanel extends JPanel {
    
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
    
    // Status Colors
    private static final Color COLOR_SCHEDULED = new Color(52, 152, 219);
    private static final Color COLOR_CONFIRMED = new Color(46, 204, 113);
    private static final Color COLOR_IN_PROGRESS = new Color(241, 196, 15);
    private static final Color COLOR_COMPLETED = new Color(155, 89, 182);
    private static final Color COLOR_CANCELLED = new Color(231, 76, 60);
    private static final Color COLOR_NO_SHOW = new Color(149, 165, 166);

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
    private JComboBox<String> periodCombo;
    private JComboBox<Dentist> dentistCombo;
    private JComboBox<String> statusCombo;
    private RoundedButton generateButton;
    private JLabel statusLabel;
    private JLabel summaryLabel;
    private JLabel lastUpdatedLabel;
    
    // Summary Cards
    private JLabel totalAppointmentsLabel;
    private JLabel scheduledLabel;
    private JLabel confirmedLabel;
    private JLabel completedLabel;
    private JLabel cancelledLabel;
    
    // Table
    private JTable scheduleTable;
    private DefaultTableModel tableModel;
    
    // Chart Panel (placeholder)
    private JPanel chartPanel;
    
    private ReportController controller;
    private DecimalFormat df = new DecimalFormat("#.00");
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    // Auto-refresh timer (hidden)
    private Timer refreshTimer;
    private static final int AUTO_REFRESH_DELAY = 30000; // 30 seconds

    public ScheduleReportPanel() {
        this.controller = new ReportController(this);
        initComponents();
        loadData();
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
        
        // Chart Panel (placeholder)
        mainPanel.add(createChartPanel());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Table
        mainPanel.add(createTablePanel());
        
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
                if (isShowing()) {
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
        
        JLabel titleLabel = new JLabel("Schedule Report");
        titleLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_DARK);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("View appointment statistics and schedule overview");
        subtitleLabel.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 14));
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

        // Period filter
        String[] periods = {"Today", "This Week", "This Month", "Next Week", "Next Month", "Custom Range"};
        periodCombo = new JComboBox<>(periods);
        periodCombo.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        periodCombo.setPreferredSize(new Dimension(130, 35));
        periodCombo.addActionListener(e -> {
            if ("Custom Range".equals(periodCombo.getSelectedItem())) {
                showCustomDateDialog();
            }
        });

        // Dentist filter
        dentistCombo = new JComboBox<>();
        dentistCombo.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        dentistCombo.setPreferredSize(new Dimension(150, 35));
        dentistCombo.addItem(createPlaceholderDentist("All Dentists"));

        // Status filter
        String[] statuses = {"All Status", "Scheduled", "Confirmed", "In Progress", "Completed", "Cancelled", "No Show"};
        statusCombo = new JComboBox<>(statuses);
        statusCombo.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        statusCombo.setPreferredSize(new Dimension(130, 35));

        generateButton = createStyledButton("Generate Report", PRIMARY_DARK, Color.WHITE);
        generateButton.setPreferredSize(new Dimension(150, 35));
        generateButton.addActionListener(e -> generateReport());
        generateButton.setIcon(icon(FontAwesomeSolid.CALENDAR_ALT, 14, Color.WHITE));
        generateButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        generateButton.setIconTextGap(8);

        filterPanel.add(new JLabel("Period:"));
        filterPanel.add(periodCombo);
        filterPanel.add(new JLabel("Dentist:"));
        filterPanel.add(dentistCombo);
        filterPanel.add(new JLabel("Status:"));
        filterPanel.add(statusCombo);
        filterPanel.add(generateButton);
        
        // Manual Refresh Button - ICON ONLY
        JButton refreshBtn = createIconButton(FontAwesomeSolid.SYNC_ALT, COLOR_REFRESH);
        refreshBtn.setPreferredSize(new Dimension(40, 40));
        refreshBtn.setToolTipText("Refresh Now");
        refreshBtn.addActionListener(e -> loadData());
        filterPanel.add(refreshBtn);

        return filterPanel;
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 5, 12, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            new EmptyBorder(15, 20, 15, 20)
        ));

        // Total Appointments
        JPanel totalPanel = createSummaryCard(FontAwesomeSolid.CALENDAR_ALT, "Total", "0", MINT);
        panel.add(totalPanel);
        totalAppointmentsLabel = findValueLabel(totalPanel);

        // Scheduled
        JPanel scheduledPanel = createSummaryCard(FontAwesomeSolid.CLOCK, "Scheduled", "0", COLOR_SCHEDULED);
        panel.add(scheduledPanel);
        scheduledLabel = findValueLabel(scheduledPanel);

        // Confirmed
        JPanel confirmedPanel = createSummaryCard(FontAwesomeSolid.CHECK_CIRCLE, "Confirmed", "0", COLOR_CONFIRMED);
        panel.add(confirmedPanel);
        confirmedLabel = findValueLabel(confirmedPanel);

        // Completed
        JPanel completedPanel = createSummaryCard(FontAwesomeSolid.CHECK_DOUBLE, "Completed", "0", COLOR_COMPLETED);
        panel.add(completedPanel);
        completedLabel = findValueLabel(completedPanel);

        // Cancelled
        JPanel cancelledPanel = createSummaryCard(FontAwesomeSolid.TIMES_CIRCLE, "Cancelled", "0", COLOR_CANCELLED);
        panel.add(cancelledPanel);
        cancelledLabel = findValueLabel(cancelledPanel);

        return panel;
    }

    private JPanel createSummaryCard(FontAwesomeSolid glyph, String title, String defaultValue, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(LIGHT_SURFACE, 1));
        card.setPreferredSize(new Dimension(140, 70));

        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(color);
        leftPanel.setPreferredSize(new Dimension(50, 70));
        leftPanel.setLayout(new GridBagLayout());
        
        JLabel iconLabel = iconLabel(glyph, 22, Color.WHITE);
        leftPanel.add(iconLabel);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(new EmptyBorder(5, 8, 5, 8));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 10));
        titleLabel.setForeground(SECONDARY_TEXT);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel valueLabel = new JLabel(defaultValue);
        valueLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 16));
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

    private JPanel createChartPanel() {
        chartPanel = new JPanel();
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(MINT, 1),
            "Appointment Status Distribution",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font(UI_FONT_FAMILY, Font.BOLD, 14),
            PRIMARY_DARK
        ));
        chartPanel.setPreferredSize(new Dimension(600, 150));
        chartPanel.setLayout(new GridBagLayout());
        
        JLabel chartPlaceholder = iconLabel(FontAwesomeSolid.CHART_PIE, 24, SECONDARY_TEXT);
        JLabel chartText = new JLabel(" Chart visualization coming soon");
        chartText.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 16));
        chartText.setForeground(SECONDARY_TEXT);
        
        JPanel placeholderPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        placeholderPanel.setOpaque(false);
        placeholderPanel.add(chartPlaceholder);
        placeholderPanel.add(chartText);
        
        chartPanel.add(placeholderPanel);
        
        return chartPanel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(MINT, 1),
            "Appointment Details",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font(UI_FONT_FAMILY, Font.BOLD, 14),
            PRIMARY_DARK
        ));

        String[] columns = {"ID", "Date", "Time", "Patient", "Dentist", "Status", "Reason"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        scheduleTable = new JTable(tableModel);
        scheduleTable.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 12));
        scheduleTable.setRowHeight(32);
        scheduleTable.setSelectionBackground(new Color(235, 245, 240));
        scheduleTable.setShowGrid(true);
        scheduleTable.setGridColor(LIGHT_SURFACE);

        JTableHeader header = scheduleTable.getTableHeader();
        header.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 12));
        header.setBackground(MINT);
        header.setForeground(PRIMARY_DARK);

        // Custom cell renderer for status column
        scheduleTable.getColumnModel().getColumn(5).setCellRenderer(new StatusCellRenderer());

        JScrollPane scrollPane = new JScrollPane(scheduleTable);
        scrollPane.setPreferredSize(new Dimension(600, 200));
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

        summaryLabel = new JLabel("Total: 0 appointments");
        summaryLabel.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 12));
        summaryLabel.setForeground(new Color(107, 123, 121));
        summaryLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        // Use a panel to hold both summary and last updated on the right
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);
        rightPanel.add(summaryLabel);
        rightPanel.add(lastUpdatedLabel);

        footer.add(statusLabel, BorderLayout.WEST);
        footer.add(rightPanel, BorderLayout.EAST);

        return footer;
    }

    private RoundedButton createStyledButton(String text, Color bg, Color fg) {
        RoundedButton button = new RoundedButton(text, bg, fg);
        button.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 12));
        return button;
    }

    // Status Cell Renderer
    private class StatusCellRenderer extends javax.swing.table.DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (value != null) {
                String status = value.toString();
                JLabel label = (JLabel) c;
                label.setOpaque(true);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
                
                switch (status) {
                    case "Scheduled":
                        label.setBackground(COLOR_SCHEDULED);
                        label.setForeground(Color.WHITE);
                        break;
                    case "Confirmed":
                        label.setBackground(COLOR_CONFIRMED);
                        label.setForeground(Color.WHITE);
                        break;
                    case "In Progress":
                        label.setBackground(COLOR_IN_PROGRESS);
                        label.setForeground(Color.BLACK);
                        break;
                    case "Completed":
                        label.setBackground(COLOR_COMPLETED);
                        label.setForeground(Color.WHITE);
                        break;
                    case "Cancelled":
                        label.setBackground(COLOR_CANCELLED);
                        label.setForeground(Color.WHITE);
                        break;
                    case "No Show":
                        label.setBackground(COLOR_NO_SHOW);
                        label.setForeground(Color.WHITE);
                        break;
                    default:
                        label.setBackground(LIGHT_SURFACE);
                        label.setForeground(PRIMARY_DARK);
                        break;
                }
            }
            return c;
        }
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
    // Helper Methods
    // ========================

    private Dentist createPlaceholderDentist(String name) {
        Dentist d = new Dentist();
        d.setDentistName(name);
        return d;
    }

    private void showCustomDateDialog() {
        JOptionPane.showMessageDialog(this, 
            "Custom date range selection coming soon.\nUsing this month for now.",
            "Custom Range",
            JOptionPane.INFORMATION_MESSAGE);
        periodCombo.setSelectedItem("This Month");
    }

    private void loadData() {
        List<Dentist> dentists = controller.getAllDentists();
        dentistCombo.removeAllItems();
        dentistCombo.addItem(createPlaceholderDentist("All Dentists"));
        if (dentists != null) {
            for (Dentist dentist : dentists) {
                dentistCombo.addItem(dentist);
            }
        }
        generateReport();
    }

    private void generateReport() {
        String period = (String) periodCombo.getSelectedItem();
        Dentist dentist = (Dentist) dentistCombo.getSelectedItem();
        String status = (String) statusCombo.getSelectedItem();

        statusLabel.setText("Generating report...");
        setCursor(new Cursor(Cursor.WAIT_CURSOR));

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            private List<Appointment> appointments;
            private Map<String, Integer> statusCounts;

            @Override
            protected Void doInBackground() throws Exception {
                // Get appointments based on period
                appointments = controller.getAppointmentsByPeriod(period);
                
                // Filter by dentist
                if (dentist != null && !"All Dentists".equals(dentist.getDentistName()) && appointments != null) {
                    appointments.removeIf(a -> a.getDentistId() != dentist.getDentistId());
                }
                
                // Filter by status
                if (status != null && !"All Status".equals(status) && appointments != null) {
                    appointments.removeIf(a -> !a.getStatus().equals(status));
                }
                
                // Count statuses
                statusCounts = new HashMap<>();
                if (appointments != null) {
                    for (Appointment appt : appointments) {
                        String s = appt.getStatus() != null ? appt.getStatus() : "Unknown";
                        statusCounts.put(s, statusCounts.getOrDefault(s, 0) + 1);
                    }
                }
                
                return null;
            }

            @Override
            protected void done() {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                try {
                    displayReport(appointments, statusCounts);
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

    private void displayReport(List<Appointment> appointments, Map<String, Integer> statusCounts) {
        tableModel.setRowCount(0);
        
        if (appointments == null || appointments.isEmpty()) {
            summaryLabel.setText("Total: 0 appointments");
            totalAppointmentsLabel.setText("0");
            scheduledLabel.setText("0");
            confirmedLabel.setText("0");
            completedLabel.setText("0");
            cancelledLabel.setText("0");
            return;
        }

        // Update summary
        int total = appointments.size();
        int scheduled = statusCounts.getOrDefault("Scheduled", 0);
        int confirmed = statusCounts.getOrDefault("Confirmed", 0);
        int completed = statusCounts.getOrDefault("Completed", 0);
        int cancelled = statusCounts.getOrDefault("Cancelled", 0);
        int inProgress = statusCounts.getOrDefault("In Progress", 0);
        int noShow = statusCounts.getOrDefault("No Show", 0);

        totalAppointmentsLabel.setText(String.valueOf(total));
        scheduledLabel.setText(String.valueOf(scheduled));
        confirmedLabel.setText(String.valueOf(confirmed));
        completedLabel.setText(String.valueOf(completed));
        cancelledLabel.setText(String.valueOf(cancelled));

        // Update table
        for (Appointment appt : appointments) {
            String patientName = controller.getPatientName(appt.getPatientId());
            String dentistName = controller.getDentistName(appt.getDentistId());
            
            Object[] row = {
                appt.getAppointmentId(),
                appt.getAppointmentDate() != null ? sdf.format(appt.getAppointmentDate()) : "N/A",
                appt.getAppointmentTime() != null ? appt.getAppointmentTime().toString().substring(0, 5) : "N/A",
                patientName,
                dentistName,
                appt.getStatus() != null ? appt.getStatus() : "N/A",
                appt.getReason() != null ? appt.getReason() : "N/A"
            };
            tableModel.addRow(row);
        }

        summaryLabel.setText("Total: " + total + " appointments");
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