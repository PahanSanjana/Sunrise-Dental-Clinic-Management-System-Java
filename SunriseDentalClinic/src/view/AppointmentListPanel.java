package view;

import controller.AppointmentController;
import model.Appointment;
import model.Patient;
import model.Dentist;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.List;

public class AppointmentListPanel extends JPanel {
    
    // Color Palette
    private static final Color PRIMARY_DARK = new Color(0x2F3E3C);
    private static final Color MINT = new Color(0xBDDBD1);
    private static final Color SOFT_SURFACE = new Color(0xFBF9F1);
    private static final Color LIGHT_SURFACE = new Color(0xE7E9E3);
    private static final Color HOVER_SURFACE = new Color(0xE8F0F1);
    private static final Color ERROR_COLOR = new Color(220, 80, 80);
    private static final Color SUCCESS_COLOR = new Color(60, 160, 80);
    
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
    private JTable appointmentTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private RoundedButton searchButton;
    private RoundedButton addButton;
    private JButton refreshButton;
    private RoundedButton viewButton;
    private RoundedButton editButton;
    private RoundedButton cancelButton;
    private RoundedButton deleteButton;
    private JLabel statusLabel;
    private JLabel countLabel;
    private JComboBox<String> statusCombo;
    private JComboBox<String> dateFilterCombo;
    
    private AppointmentController controller;

    // ✅ Auto-refresh timer (hidden)
    private Timer refreshTimer;
    private static final int AUTO_REFRESH_DELAY = 30000; // 30 seconds

    public AppointmentListPanel() {
        this.controller = new AppointmentController(this);
        initComponents();
        loadAppointments();
        startAutoRefresh();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(SOFT_SURFACE);
        setBorder(new EmptyBorder(20, 30, 20, 30));

        // Title Panel - At the top
        add(createTitlePanel(), BorderLayout.NORTH);
        
        // Main Content Panel (Search + Table + Footer)
        add(createMainContentPanel(), BorderLayout.CENTER);
    }

    // =====================================================
    // ✅ AUTO-REFRESH (Hidden - No UI Indicator)
    // =====================================================
    
    private void startAutoRefresh() {
        if (refreshTimer == null) {
            refreshTimer = new Timer(AUTO_REFRESH_DELAY, e -> {
                if (isShowing()) {
                    loadAppointments();
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

    /**
     * ✅ Title Panel - Separate from other content
     */
    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(SOFT_SURFACE);
        titlePanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JLabel titleLabel = new JLabel("Appointment Management");
        titleLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_DARK);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Manage all appointments in the system");
        subtitleLabel.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(107, 123, 121));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 2)));
        titlePanel.add(subtitleLabel);
        
        return titlePanel;
    }

    /**
     * ✅ Main Content Panel - Contains search, table, and footer
     */
    private JPanel createMainContentPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));

        // Search Panel
        mainPanel.add(createSearchPanel(), BorderLayout.NORTH);
        
        // Table Panel
        mainPanel.add(createTablePanel(), BorderLayout.CENTER);
        
        // Footer Panel
        mainPanel.add(createFooterPanel(), BorderLayout.SOUTH);

        return mainPanel;
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setOpaque(false);

        // Date filter
        String[] dateFilters = {"All Dates", "Today", "Tomorrow", "This Week", "Next Week"};
        dateFilterCombo = new JComboBox<>(dateFilters);
        dateFilterCombo.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        dateFilterCombo.setPreferredSize(new Dimension(120, 35));
        dateFilterCombo.addActionListener(e -> loadAppointments());

        // Status filter
        String[] statuses = {"All Status", "Scheduled", "Confirmed", "In Progress", "Completed", "Cancelled", "No Show"};
        statusCombo = new JComboBox<>(statuses);
        statusCombo.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        statusCombo.setPreferredSize(new Dimension(120, 35));
        statusCombo.addActionListener(e -> loadAppointments());

        searchField = new JTextField(20);
        searchField.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        searchField.setPreferredSize(new Dimension(200, 35));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        searchField.addActionListener(e -> loadAppointments());

        searchButton = createStyledButton("Search", PRIMARY_DARK, Color.WHITE);
        searchButton.setPreferredSize(new Dimension(100, 35));
        searchButton.addActionListener(e -> loadAppointments());
        searchButton.setIcon(icon(FontAwesomeSolid.SEARCH, 14, Color.WHITE));
        searchButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        searchButton.setIconTextGap(6);

        addButton = createStyledButton("Book Appointment", PRIMARY_DARK, Color.WHITE);
        addButton.setPreferredSize(new Dimension(150, 35));
        addButton.addActionListener(e -> {
            Container parent = getParent();
            while (parent != null && !(parent instanceof MainFrame)) {
                parent = parent.getParent();
            }
            if (parent instanceof MainFrame) {
                ((MainFrame) parent).showCard("APPOINTMENT_BOOK");
            }
        });
        addButton.setIcon(icon(FontAwesomeSolid.CALENDAR_PLUS, 14, Color.WHITE));
        addButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        addButton.setIconTextGap(6);

        // ✅ Manual Refresh Button - ICON ONLY
        refreshButton = createIconButton(FontAwesomeSolid.SYNC_ALT, COLOR_REFRESH);
        refreshButton.setPreferredSize(new Dimension(40, 40));
        refreshButton.setToolTipText("Refresh Now");
        refreshButton.addActionListener(e -> loadAppointments());

        searchPanel.add(new JLabel("Date:"));
        searchPanel.add(dateFilterCombo);
        searchPanel.add(new JLabel("Status:"));
        searchPanel.add(statusCombo);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(addButton);
        searchPanel.add(refreshButton);

        return searchPanel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // ✅ Removed "Actions" column - No buttons inside table
        String[] columns = {"ID", "Patient", "Dentist", "Date", "Time", "Status", "Reason"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        appointmentTable = new JTable(tableModel);
        appointmentTable.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        appointmentTable.setRowHeight(40);
        appointmentTable.setSelectionBackground(new Color(235, 245, 240));
        appointmentTable.setSelectionForeground(PRIMARY_DARK);
        appointmentTable.setShowGrid(true);
        appointmentTable.setGridColor(LIGHT_SURFACE);
        appointmentTable.setIntercellSpacing(new Dimension(5, 5));

        // Set column widths
        appointmentTable.getColumnModel().getColumn(0).setMaxWidth(60);
        appointmentTable.getColumnModel().getColumn(0).setMinWidth(50);
        appointmentTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        appointmentTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        appointmentTable.getColumnModel().getColumn(5).setMaxWidth(120);

        // Custom header
        JTableHeader header = appointmentTable.getTableHeader();
        header.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        header.setBackground(MINT);
        header.setForeground(PRIMARY_DARK);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_DARK));

        // Custom cell renderer for status column
        appointmentTable.getColumnModel().getColumn(5).setCellRenderer(new StatusCellRenderer());

        // Add mouse listener for double click to view
        appointmentTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = appointmentTable.getSelectedRow();
                    if (row != -1) {
                        viewAppointment(row);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(appointmentTable);
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

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightPanel.setOpaque(false);
        
        viewButton = createStyledButton("View", SOFT_SURFACE, PRIMARY_DARK);
        viewButton.setBorderColor(LIGHT_SURFACE);
        viewButton.setPreferredSize(new Dimension(80, 30));
        viewButton.addActionListener(e -> {
            int row = appointmentTable.getSelectedRow();
            if (row != -1) {
                viewAppointment(row);
            } else {
                showError("Please select an appointment to view.");
            }
        });
        viewButton.setIcon(icon(FontAwesomeSolid.EYE, 12, PRIMARY_DARK));
        viewButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        viewButton.setIconTextGap(6);

        editButton = createStyledButton("Edit", SOFT_SURFACE, PRIMARY_DARK);
        editButton.setBorderColor(LIGHT_SURFACE);
        editButton.setPreferredSize(new Dimension(80, 30));
        editButton.addActionListener(e -> {
            int row = appointmentTable.getSelectedRow();
            if (row != -1) {
                editAppointment(row);
            } else {
                showError("Please select an appointment to edit.");
            }
        });
        editButton.setIcon(icon(FontAwesomeSolid.EDIT, 12, PRIMARY_DARK));
        editButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        editButton.setIconTextGap(6);

        cancelButton = createStyledButton("Cancel", SOFT_SURFACE, ERROR_COLOR);
        cancelButton.setBorderColor(LIGHT_SURFACE);
        cancelButton.setPreferredSize(new Dimension(100, 30));
        cancelButton.addActionListener(e -> {
            int row = appointmentTable.getSelectedRow();
            if (row != -1) {
                cancelAppointment(row);
            } else {
                showError("Please select an appointment to cancel.");
            }
        });
        cancelButton.setIcon(icon(FontAwesomeSolid.TIMES, 12, ERROR_COLOR));
        cancelButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        cancelButton.setIconTextGap(6);

        deleteButton = createStyledButton("Delete", SOFT_SURFACE, ERROR_COLOR);
        deleteButton.setBorderColor(LIGHT_SURFACE);
        deleteButton.setPreferredSize(new Dimension(100, 30));
        deleteButton.addActionListener(e -> {
            int row = appointmentTable.getSelectedRow();
            if (row != -1) {
                deleteAppointment(row);
            } else {
                showError("Please select an appointment to delete.");
            }
        });
        deleteButton.setIcon(icon(FontAwesomeSolid.TRASH_ALT, 12, ERROR_COLOR));
        deleteButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        deleteButton.setIconTextGap(6);

        rightPanel.add(viewButton);
        rightPanel.add(editButton);
        rightPanel.add(cancelButton);
        rightPanel.add(deleteButton);

        footer.add(statusLabel, BorderLayout.WEST);
        footer.add(countLabel, BorderLayout.CENTER);
        footer.add(rightPanel, BorderLayout.EAST);

        return footer;
    }

    private RoundedButton createStyledButton(String text, Color bg, Color fg) {
        RoundedButton button = new RoundedButton(text, bg, fg);
        button.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 12));
        return button;
    }

    // Inner class for RoundedButton
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
            } else if (bg.equals(ERROR_COLOR)) {
                hoverColor = new Color(180, 60, 60);
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
    // Public methods
    // ========================

    public void loadAppointments() {
        String searchText = searchField != null ? searchField.getText().trim() : "";
        String status = statusCombo != null ? (String) statusCombo.getSelectedItem() : "All Status";
        String dateFilter = dateFilterCombo != null ? (String) dateFilterCombo.getSelectedItem() : "All Dates";
        
        SwingWorker<List<Appointment>, Void> worker = new SwingWorker<List<Appointment>, Void>() {
            @Override
            protected List<Appointment> doInBackground() throws Exception {
                List<Appointment> appointments = controller.getAllAppointments();
                
                if (status != null && !status.equals("All Status") && appointments != null) {
                    appointments.removeIf(a -> !a.getStatus().equals(status));
                }
                
                if (dateFilter != null && !dateFilter.equals("All Dates") && appointments != null) {
                    java.time.LocalDate today = java.time.LocalDate.now();
                    switch (dateFilter) {
                        case "Today":
                            appointments.removeIf(a -> !a.getAppointmentDate().toLocalDate().equals(today));
                            break;
                        case "Tomorrow":
                            appointments.removeIf(a -> !a.getAppointmentDate().toLocalDate().equals(today.plusDays(1)));
                            break;
                        case "This Week":
                            java.time.LocalDate weekStart = today.minusDays(today.getDayOfWeek().getValue() - 1);
                            java.time.LocalDate weekEnd = weekStart.plusDays(6);
                            appointments.removeIf(a -> {
                                java.time.LocalDate date = a.getAppointmentDate().toLocalDate();
                                return date.isBefore(weekStart) || date.isAfter(weekEnd);
                            });
                            break;
                        case "Next Week":
                            java.time.LocalDate nextWeekStart = today.plusDays(7 - today.getDayOfWeek().getValue() + 1);
                            java.time.LocalDate nextWeekEnd = nextWeekStart.plusDays(6);
                            appointments.removeIf(a -> {
                                java.time.LocalDate date = a.getAppointmentDate().toLocalDate();
                                return date.isBefore(nextWeekStart) || date.isAfter(nextWeekEnd);
                            });
                            break;
                    }
                }
                
                if (searchText != null && !searchText.isEmpty() && appointments != null) {
                    appointments.removeIf(a -> {
                        String patientName = getPatientName(a.getPatientId());
                        String dentistName = getDentistName(a.getDentistId());
                        return !patientName.toLowerCase().contains(searchText.toLowerCase()) &&
                               !dentistName.toLowerCase().contains(searchText.toLowerCase()) &&
                               !a.getStatus().toLowerCase().contains(searchText.toLowerCase());
                    });
                }
                
                return appointments;
            }

            @Override
            protected void done() {
                try {
                    List<Appointment> appointments = get();
                    displayAppointments(appointments);
                } catch (Exception e) {
                    showError("Error loading appointments: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private String getPatientName(int patientId) {
        Patient patient = controller.getPatientById(patientId);
        return patient != null ? patient.getPatientName() : "Unknown";
    }

    private String getDentistName(int dentistId) {
        Dentist dentist = controller.getDentistById(dentistId);
        return dentist != null ? dentist.getDentistName() : "Unknown";
    }

    public void displayAppointments(List<Appointment> appointments) {
        if (tableModel == null) return;
        
        tableModel.setRowCount(0);
        
        if (appointments == null || appointments.isEmpty()) {
            statusLabel.setText("No appointments found");
            countLabel.setText("Total: 0 appointments");
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        
        for (Appointment appointment : appointments) {
            String patientName = getPatientName(appointment.getPatientId());
            String dentistName = getDentistName(appointment.getDentistId());
            String date = appointment.getAppointmentDate() != null ? dateFormat.format(appointment.getAppointmentDate()) : "N/A";
            String time = appointment.getAppointmentTime() != null ? timeFormat.format(appointment.getAppointmentTime()) : "N/A";
            
            Object[] row = {
                appointment.getAppointmentId(),
                patientName,
                dentistName,
                date,
                time,
                appointment.getStatus() != null ? appointment.getStatus() : "N/A",
                appointment.getReason() != null ? appointment.getReason() : "N/A"
            };
            tableModel.addRow(row);
        }

        statusLabel.setText("Loaded " + appointments.size() + " appointments");
        countLabel.setText("Total: " + appointments.size() + " appointments");
    }

    public void viewAppointment(int row) {
        int appointmentId = (int) tableModel.getValueAt(row, 0);
        
        Container parent = getParent();
        while (parent != null && !(parent instanceof MainFrame)) {
            parent = parent.getParent();
        }
        if (parent instanceof MainFrame) {
            MainFrame mainFrame = (MainFrame) parent;
            Appointment appointment = controller.getAppointmentById(appointmentId);
            if (appointment != null) {
                AppointmentDetailsPanel detailsPanel = new AppointmentDetailsPanel(appointment);
                detailsPanel.setName("APPOINTMENT_DETAILS");
                mainFrame.addScreen("APPOINTMENT_DETAILS", detailsPanel);
                mainFrame.showCard("APPOINTMENT_DETAILS");
            } else {
                showError("Appointment not found.");
            }
        }
    }

    public void editAppointment(int row) {
        int appointmentId = (int) tableModel.getValueAt(row, 0);
        
        Container parent = getParent();
        while (parent != null && !(parent instanceof MainFrame)) {
            parent = parent.getParent();
        }
        if (parent instanceof MainFrame) {
            MainFrame mainFrame = (MainFrame) parent;
            Appointment appointment = controller.getAppointmentById(appointmentId);
            if (appointment != null) {
                AppointmentDetailsPanel detailsPanel = new AppointmentDetailsPanel(appointment);
                detailsPanel.setName("APPOINTMENT_DETAILS");
                mainFrame.addScreen("APPOINTMENT_DETAILS", detailsPanel);
                mainFrame.showCard("APPOINTMENT_DETAILS");
                detailsPanel.toggleEditMode();
            } else {
                showError("Appointment not found.");
            }
        }
    }

    public void cancelAppointment(int row) {
        int appointmentId = (int) tableModel.getValueAt(row, 0);
        String patientName = (String) tableModel.getValueAt(row, 1);
        
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to cancel the appointment for " + patientName + "?",
            "Cancel Appointment",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = controller.cancelAppointment(appointmentId);
            if (success) {
                showSuccess("Appointment cancelled successfully!");
                loadAppointments();
            } else {
                showError("Failed to cancel appointment.");
            }
        }
    }

    public void deleteAppointment(int row) {
        int appointmentId = (int) tableModel.getValueAt(row, 0);
        String patientName = (String) tableModel.getValueAt(row, 1);
        
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete the appointment for " + patientName + "?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = controller.deleteAppointment(appointmentId);
            if (success) {
                showSuccess("Appointment deleted successfully!");
                loadAppointments();
            } else {
                showError("Failed to delete appointment.");
            }
        }
    }

    public void showError(String message) {
        statusLabel.setText("Error: " + message);
        statusLabel.setForeground(ERROR_COLOR);
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showSuccess(String message) {
        statusLabel.setText("Success: " + message);
        statusLabel.setForeground(SUCCESS_COLOR);
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showInfo(String message) {
        statusLabel.setText("Info: " + message);
        statusLabel.setForeground(new Color(107, 123, 121));
    }

    private static class MouseAdapter extends java.awt.event.MouseAdapter {
        // Empty implementation
    }
}