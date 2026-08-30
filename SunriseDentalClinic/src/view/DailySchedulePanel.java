package view;

import controller.AppointmentController;
import model.Appointment;
import model.Patient;
import model.Dentist;
import model.User;
import model.LoginSession;
import model.User.UserRole;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DailySchedulePanel extends JPanel {
    
    // Color Palette
    private static final Color PRIMARY_DARK = new Color(0x2F3E3C);
    private static final Color MINT = new Color(0xBDDBD1);
    private static final Color SOFT_SURFACE = new Color(0xFBF9F1);
    private static final Color LIGHT_SURFACE = new Color(0xE7E9E3);
    private static final Color HOVER_SURFACE = new Color(0xE8F0F1);
    private static final Color ERROR_COLOR = new Color(220, 80, 80);
    private static final Color SUCCESS_COLOR = new Color(60, 160, 80);
    private static final Color SECONDARY_TEXT = new Color(122, 138, 135);
    
    // Status Colors
    private static final Color COLOR_SCHEDULED = new Color(52, 152, 219);
    private static final Color COLOR_CONFIRMED = new Color(46, 204, 113);
    private static final Color COLOR_IN_PROGRESS = new Color(241, 196, 15);
    private static final Color COLOR_COMPLETED = new Color(155, 89, 182);
    private static final Color COLOR_CANCELLED = new Color(231, 76, 60);
    private static final Color COLOR_NO_SHOW = new Color(149, 165, 166);

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

    // Components
    private JTable scheduleTable;
    private DefaultTableModel tableModel;
    private JLabel dateLabel;
    private JLabel statusLabel;
    private JLabel countLabel;
    private JButton prevDayButton;
    private JButton nextDayButton;
    private JButton todayButton;
    private JComboBox<String> dentistFilterCombo;
    private JComboBox<String> statusFilterCombo;
    private JButton refreshButton;
    
    private LocalDate currentDate;
    private AppointmentController controller;
    private User currentUser;
    private int dentistId;

    // ✅ Auto-refresh timer (hidden)
    private Timer refreshTimer;
    private static final int AUTO_REFRESH_DELAY = 30000; // 30 seconds

    public DailySchedulePanel() {
        this.controller = new AppointmentController(this);
        this.currentDate = LocalDate.now();
        this.currentUser = LoginSession.getInstance().getCurrentUser();
        this.dentistId = getCurrentDentistId();
        initComponents();
        loadScheduleData();
        startAutoRefresh();
        updateFilterVisibility();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(SOFT_SURFACE);
        setBorder(new EmptyBorder(20, 30, 20, 30));

        // Header Panel
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Table Panel
        add(createTablePanel(), BorderLayout.CENTER);
        
        // Footer Panel
        add(createFooterPanel(), BorderLayout.SOUTH);
    }

    // =====================================================
    // ✅ AUTO-REFRESH (Hidden - No UI Indicator)
    // =====================================================
    
    private void startAutoRefresh() {
        if (refreshTimer == null) {
            refreshTimer = new Timer(AUTO_REFRESH_DELAY, e -> {
                if (isShowing()) {
                    loadSchedule();
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
    // ✅ GET CURRENT DENTIST ID
    // =====================================================
    
    private int getCurrentDentistId() {
        if (currentUser == null) return -1;
        
        // If user is DENTIST, get their dentist ID
        if (currentUser.isDentist()) {
            return currentUser.getDentistId() != null ? currentUser.getDentistId() : -1;
        }
        return -1;
    }

    // =====================================================
    // ✅ UPDATE FILTER VISIBILITY BASED ON ROLE
    // =====================================================
    
    private void updateFilterVisibility() {
        if (currentUser == null) return;
        
        boolean isAdminOrReception = currentUser.isAdmin() || currentUser.isReception();
        boolean isDentist = currentUser.isDentist();
        boolean isPatient = currentUser.isPatient();
        
        // Dentist filter - Only show for ADMIN and RECEPTION
        dentistFilterCombo.setVisible(isAdminOrReception);
        
        // Status filter - Show for ADMIN, RECEPTION, and DENTIST
        statusFilterCombo.setVisible(isAdminOrReception || isDentist);
        
        // For PATIENT - hide filters and navigation
        if (isPatient) {
            dentistFilterCombo.setVisible(false);
            statusFilterCombo.setVisible(false);
            prevDayButton.setVisible(false);
            nextDayButton.setVisible(false);
            todayButton.setVisible(false);
        }
    }

    // =====================================================
    // ✅ CREATE ICON BUTTON (No text, only icon)
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

    // =====================================================
    // ✅ CREATE NAV BUTTON WITH ICON
    // =====================================================
    private JButton createNavButton(FontAwesomeSolid glyph, String text, Color bg, Color fg) {
        JButton button = new JButton(text);
        button.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        button.setBackground(bg);
        button.setForeground(fg);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(6, 16, 6, 16)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        if (glyph != null) {
            button.setIcon(icon(glyph, 14, fg));
            button.setHorizontalTextPosition(SwingConstants.RIGHT);
            button.setIconTextGap(6);
        }
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(HOVER_SURFACE);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bg);
            }
        });
        
        return button;
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(SOFT_SURFACE);
        header.setBorder(new EmptyBorder(0, 0, 15, 0));

        // Left: Title and Date
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel(getTitleBasedOnRole());
        titleLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_DARK);
        
        JLabel subtitleLabel = new JLabel(getSubtitleBasedOnRole());
        subtitleLabel.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 14));
        subtitleLabel.setForeground(SECONDARY_TEXT);
        
        dateLabel = new JLabel("");
        dateLabel.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 14));
        dateLabel.setForeground(SECONDARY_TEXT);
        
        leftPanel.add(titleLabel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 2)));
        leftPanel.add(subtitleLabel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 2)));
        leftPanel.add(dateLabel);

        // Right: Controls
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);

        // Date Navigation
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        navPanel.setOpaque(false);
        
        prevDayButton = createNavButton(FontAwesomeSolid.ANGLE_LEFT, "", Color.WHITE, PRIMARY_DARK);
        prevDayButton.setPreferredSize(new Dimension(40, 35));
        prevDayButton.addActionListener(e -> navigateDay(-1));
        
        todayButton = createNavButton(FontAwesomeSolid.CALENDAR_ALT, " Today", Color.WHITE, PRIMARY_DARK);
        todayButton.setPreferredSize(new Dimension(100, 35));
        todayButton.addActionListener(e -> goToToday());
        
        nextDayButton = createNavButton(FontAwesomeSolid.ANGLE_RIGHT, "", Color.WHITE, PRIMARY_DARK);
        nextDayButton.setPreferredSize(new Dimension(40, 35));
        nextDayButton.addActionListener(e -> navigateDay(1));
        
        navPanel.add(prevDayButton);
        navPanel.add(todayButton);
        navPanel.add(nextDayButton);
        
        rightPanel.add(navPanel);
        rightPanel.add(Box.createRigidArea(new Dimension(15, 0)));

        // Dentist Filter - Only for ADMIN and RECEPTION
        dentistFilterCombo = createFilterCombo("All Dentists");
        dentistFilterCombo.addActionListener(e -> loadSchedule());
        
        // Status Filter - For ADMIN, RECEPTION, and DENTIST
        statusFilterCombo = createFilterCombo("All Status");
        statusFilterCombo.addActionListener(e -> loadSchedule());
        
        rightPanel.add(dentistFilterCombo);
        rightPanel.add(statusFilterCombo);
        rightPanel.add(Box.createRigidArea(new Dimension(10, 0)));

        // Refresh Button
        refreshButton = createIconButton(FontAwesomeSolid.SYNC_ALT, COLOR_REFRESH);
        refreshButton.setPreferredSize(new Dimension(40, 40));
        refreshButton.setToolTipText("Refresh Schedule");
        refreshButton.addActionListener(e -> loadSchedule());
        rightPanel.add(refreshButton);

        header.add(leftPanel, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);

        return header;
    }

    private String getTitleBasedOnRole() {
        if (currentUser == null) return "Daily Schedule";
        
        switch (currentUser.getRole()) {
            case ADMIN:
            case RECEPTION:
                return "Daily Schedule";
            case DENTIST:
                return "My Schedule";
            case PATIENT:
                return "My Appointments";
            default:
                return "Daily Schedule";
        }
    }

    private String getSubtitleBasedOnRole() {
        if (currentUser == null) return "View daily appointments";
        
        switch (currentUser.getRole()) {
            case ADMIN:
            case RECEPTION:
                return "View all appointments for each day";
            case DENTIST:
                return "View your daily appointments";
            case PATIENT:
                return "View your appointment history";
            default:
                return "View daily appointments";
        }
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(LIGHT_SURFACE, 1));

        // Create table model - Hide Dentist column for PATIENT
        String[] columns;
        if (currentUser != null && currentUser.isPatient()) {
            columns = new String[]{"Time", "Dentist", "Status", "Reason"};
        } else {
            columns = new String[]{"Time", "Patient", "Dentist", "Status", "Reason"};
        }
        
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        scheduleTable = new JTable(tableModel);
        scheduleTable.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        scheduleTable.setRowHeight(42);
        scheduleTable.setSelectionBackground(new Color(235, 245, 240));
        scheduleTable.setSelectionForeground(PRIMARY_DARK);
        scheduleTable.setShowGrid(true);
        scheduleTable.setGridColor(LIGHT_SURFACE);
        scheduleTable.setIntercellSpacing(new Dimension(5, 5));

        // Set column widths
        scheduleTable.getColumnModel().getColumn(0).setMaxWidth(100);
        scheduleTable.getColumnModel().getColumn(0).setMinWidth(80);
        
        if (currentUser != null && currentUser.isPatient()) {
            scheduleTable.getColumnModel().getColumn(1).setPreferredWidth(180);
            scheduleTable.getColumnModel().getColumn(2).setMaxWidth(120);
            scheduleTable.getColumnModel().getColumn(3).setPreferredWidth(250);
        } else {
            scheduleTable.getColumnModel().getColumn(1).setPreferredWidth(200);
            scheduleTable.getColumnModel().getColumn(2).setPreferredWidth(180);
            scheduleTable.getColumnModel().getColumn(3).setMaxWidth(120);
            scheduleTable.getColumnModel().getColumn(4).setPreferredWidth(250);
        }

        // Custom header
        JTableHeader header = scheduleTable.getTableHeader();
        header.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        header.setBackground(MINT);
        header.setForeground(PRIMARY_DARK);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_DARK));

        // Custom cell renderer for status column
        int statusColIndex = currentUser != null && currentUser.isPatient() ? 2 : 3;
        scheduleTable.getColumnModel().getColumn(statusColIndex).setCellRenderer(new StatusCellRenderer());

        // Add mouse listener for double click to view
        scheduleTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = scheduleTable.getSelectedRow();
                    if (row != -1) {
                        viewAppointment(row);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(scheduleTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

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

        countLabel = new JLabel("Total: 0 appointments");
        countLabel.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 12));
        countLabel.setForeground(new Color(107, 123, 121));

        footer.add(statusLabel, BorderLayout.WEST);
        footer.add(countLabel, BorderLayout.EAST);

        return footer;
    }

    // ========================
    // Helper Methods
    // ========================

    private JComboBox<String> createFilterCombo(String defaultText) {
        JComboBox<String> combo = new JComboBox<>();
        combo.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        combo.setBackground(Color.WHITE);
        combo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        combo.setPreferredSize(new Dimension(140, 35));
        combo.addItem(defaultText);
        return combo;
    }

    // Status Cell Renderer
    private class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (value != null) {
                String status = value.toString();
                JLabel label = (JLabel) c;
                label.setOpaque(true);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
                
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

    // ========================
    // Data Loading Methods
    // ========================

    public void loadSchedule() {
        String dateStr = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        
        // Update date label
        String dayOfWeek = currentDate.getDayOfWeek().toString().charAt(0) + 
                          currentDate.getDayOfWeek().toString().substring(1).toLowerCase();
        dateLabel.setText(dayOfWeek + ", " + currentDate.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));

        // Load appointments in background
        SwingWorker<List<Appointment>, Void> worker = new SwingWorker<List<Appointment>, Void>() {
            @Override
            protected List<Appointment> doInBackground() throws Exception {
                List<Appointment> appointments;
                
                // Role-based data fetching
                if (currentUser == null) {
                    return new java.util.ArrayList<>();
                }
                
                if (currentUser.isAdmin() || currentUser.isReception()) {
                    // ADMIN and RECEPTION - get all appointments for the date
                    appointments = controller.getAppointmentsByDate(dateStr);
                } else if (currentUser.isDentist()) {
                    // DENTIST - get only their appointments
                    if (dentistId > 0) {
                        appointments = controller.getAppointmentsByDentistAndDate(dentistId, dateStr);
                    } else {
                        appointments = new java.util.ArrayList<>();
                    }
                } else if (currentUser.isPatient()) {
                    // PATIENT - get only their appointments
                    int patientId = currentUser.getPatientId() != null ? currentUser.getPatientId() : -1;
                    if (patientId > 0) {
                        List<Appointment> allAppointments = controller.getAppointmentsByPatient(patientId);
                        // Filter by date
                        java.time.LocalDate targetDate = LocalDate.parse(dateStr);
                        appointments = new java.util.ArrayList<>();
                        for (Appointment appt : allAppointments) {
                            if (appt.getAppointmentDate() != null) {
                                java.time.LocalDate apptDate = appt.getAppointmentDate().toLocalDate();
                                if (apptDate.equals(targetDate)) {
                                    appointments.add(appt);
                                }
                            }
                        }
                    } else {
                        appointments = new java.util.ArrayList<>();
                    }
                } else {
                    appointments = new java.util.ArrayList<>();
                }
                
                // Apply dentist filter (only for ADMIN and RECEPTION)
                if (currentUser.isAdmin() || currentUser.isReception()) {
                    String dentistFilter = (String) dentistFilterCombo.getSelectedItem();
                    if (dentistFilter != null && !dentistFilter.equals("All Dentists")) {
                        int dentistIdFilter = getDentistIdByName(dentistFilter);
                        if (dentistIdFilter > 0) {
                            appointments.removeIf(a -> a.getDentistId() != dentistIdFilter);
                        }
                    }
                }
                
                // Apply status filter (for ADMIN, RECEPTION, and DENTIST)
                if (currentUser.isAdmin() || currentUser.isReception() || currentUser.isDentist()) {
                    String statusFilter = (String) statusFilterCombo.getSelectedItem();
                    if (statusFilter != null && !statusFilter.equals("All Status")) {
                        appointments.removeIf(a -> a.getStatus() == null || !a.getStatus().equals(statusFilter));
                    }
                }
                
                // Sort by time
                appointments.sort((a1, a2) -> {
                    if (a1.getAppointmentTime() == null || a2.getAppointmentTime() == null) return 0;
                    return a1.getAppointmentTime().compareTo(a2.getAppointmentTime());
                });
                
                return appointments;
            }

            @Override
            protected void done() {
                try {
                    List<Appointment> appointments = get();
                    displayAppointments(appointments);
                } catch (Exception e) {
                    showError("Error loading schedule: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private int getDentistIdByName(String name) {
        List<Dentist> dentists = controller.getAllDentists();
        for (Dentist d : dentists) {
            if (d.getDentistName().equals(name)) {
                return d.getDentistId();
            }
        }
        return -1;
    }

    private void displayAppointments(List<Appointment> appointments) {
        tableModel.setRowCount(0);
        
        if (appointments == null || appointments.isEmpty()) {
            statusLabel.setText("No appointments found for this day");
            countLabel.setText("Total: 0 appointments");
            return;
        }

        java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("HH:mm");
        boolean isPatient = currentUser != null && currentUser.isPatient();
        
        for (Appointment appointment : appointments) {
            String time = appointment.getAppointmentTime() != null ? 
                timeFormat.format(appointment.getAppointmentTime()) : "N/A";
            String patientName = getPatientName(appointment.getPatientId());
            String dentistName = getDentistName(appointment.getDentistId());
            String status = appointment.getStatus() != null ? appointment.getStatus() : "Scheduled";
            String reason = appointment.getReason() != null ? appointment.getReason() : "No reason provided";
            
            Object[] row;
            if (isPatient) {
                // For Patient: Time, Dentist, Status, Reason
                row = new Object[]{time, dentistName, status, reason};
            } else {
                // For Admin, Reception, Dentist: Time, Patient, Dentist, Status, Reason
                row = new Object[]{time, patientName, dentistName, status, reason};
            }
            tableModel.addRow(row);
        }

        statusLabel.setText("Loaded " + appointments.size() + " appointments");
        countLabel.setText("Total: " + appointments.size() + " appointments");
    }

    private String getPatientName(int patientId) {
        Patient patient = controller.getPatientById(patientId);
        return patient != null ? patient.getPatientName() : "Unknown Patient";
    }

    private String getDentistName(int dentistId) {
        Dentist dentist = controller.getDentistById(dentistId);
        return dentist != null ? dentist.getDentistName() : "Unknown Dentist";
    }

    // ========================
    // Navigation Methods
    // ========================

    private void navigateDay(int days) {
        currentDate = currentDate.plusDays(days);
        loadSchedule();
    }

    private void goToToday() {
        currentDate = LocalDate.now();
        loadSchedule();
    }

    private void viewAppointment(int row) {
        // Get appointment ID from row - need to fetch the appointment
        // Since we don't store ID in table, we need to get it from the data
        String time = (String) tableModel.getValueAt(row, 0);
        String patientName = (String) tableModel.getValueAt(row, 1);
        
        // Find appointment by time and patient name
        List<Appointment> appointments = controller.getAppointmentsByDate(
            currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        );
        
        Appointment targetAppt = null;
        for (Appointment appt : appointments) {
            String apptTime = appt.getAppointmentTime() != null ? 
                new java.text.SimpleDateFormat("HH:mm").format(appt.getAppointmentTime()) : "";
            String apptPatient = getPatientName(appt.getPatientId());
            if (apptTime.equals(time) && apptPatient.equals(patientName)) {
                targetAppt = appt;
                break;
            }
        }
        
        if (targetAppt != null) {
            Container parent = getParent();
            while (parent != null && !(parent instanceof MainFrame)) {
                parent = parent.getParent();
            }
            if (parent instanceof MainFrame) {
                MainFrame mainFrame = (MainFrame) parent;
                AppointmentDetailsPanel detailsPanel = new AppointmentDetailsPanel(targetAppt);
                detailsPanel.setName("APPOINTMENT_DETAILS");
                mainFrame.addScreen("APPOINTMENT_DETAILS", detailsPanel);
                mainFrame.showCard("APPOINTMENT_DETAILS");
            }
        } else {
            showError("Appointment not found.");
        }
    }

    private void loadDentistFilter() {
        List<Dentist> dentists = controller.getAllDentists();
        dentistFilterCombo.removeAllItems();
        dentistFilterCombo.addItem("All Dentists");
        if (dentists != null) {
            for (Dentist dentist : dentists) {
                dentistFilterCombo.addItem(dentist.getDentistName());
            }
        }
    }

    // ========================
    // Public methods
    // ========================

    public void loadScheduleData() {
        loadDentistFilter();
        updateFilterVisibility();
        loadSchedule();
    }

    public void showError(String message) {
        statusLabel.setText("Error: " + message);
        statusLabel.setForeground(ERROR_COLOR);
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showSuccess(String message) {
        statusLabel.setText("Success: " + message);
        statusLabel.setForeground(SUCCESS_COLOR);
    }

    public void showInfo(String message) {
        statusLabel.setText("Info: " + message);
        statusLabel.setForeground(new Color(107, 123, 121));
    }

    private static class MouseAdapter extends java.awt.event.MouseAdapter {
        // Empty implementation
    }
}