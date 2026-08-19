package view;

import controller.AppointmentController;
import model.Appointment;
import model.Patient;
import model.Dentist;
import model.User;
import model.LoginSession;
import model.RolePermissions;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.text.SimpleDateFormat;

public class AppointmentDetailsPanel extends JPanel {
    
    // Color Palette
    private static final Color PRIMARY_DARK = new Color(0x2F3E3C);
    private static final Color MINT = new Color(0xBDDBD1);
    private static final Color SOFT_SURFACE = new Color(0xFBF9F1);
    private static final Color LIGHT_SURFACE = new Color(0xE7E9E3);
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

    // Form Fields - View/Edit Mode
    private JLabel patientNameLabel;
    private JLabel patientPhoneLabel;
    private JLabel patientEmailLabel;
    private JLabel dentistNameLabel;
    private JLabel dentistSpecializationLabel;
    private JTextField dateField;
    private JComboBox<String> timeCombo;
    private JComboBox<String> durationCombo;
    private JComboBox<String> statusCombo;
    private JTextArea reasonArea;
    private JTextArea notesArea;
    private JLabel appointmentIdLabel;
    private JLabel createdDateLabel;
    private JLabel updatedDateLabel;
    private JLabel statusLabel;
    private JLabel statusBadge;
    
    // Buttons
    private RoundedButton editButton;
    private RoundedButton saveButton;
    private RoundedButton cancelButton;
    private RoundedButton backButton;
    private RoundedButton deleteButton;
    private RoundedButton cancelAppointmentButton;
    private JButton refreshButton;
    
    private JPanel buttonPanel;
    private boolean isEditMode = false;
    private Appointment currentAppointment;
    private AppointmentController controller;
    private int patientId;
    private int dentistId;

    // ✅ Auto-refresh timer (hidden)
    private Timer refreshTimer;
    private static final int AUTO_REFRESH_DELAY = 30000; // 30 seconds

    public AppointmentDetailsPanel() {
        this.controller = new AppointmentController(this);
        initComponents();
        setViewMode(false);
        displayEmptyState();
        startAutoRefresh();
        updateActionButtons();
    }

    public AppointmentDetailsPanel(Appointment appointment) {
        this.controller = new AppointmentController(this);
        this.currentAppointment = appointment;
        this.patientId = appointment.getPatientId();
        this.dentistId = appointment.getDentistId();
        initComponents();
        setViewMode(false);
        displayAppointment(appointment);
        startAutoRefresh();
        updateActionButtons();
    }

    // =====================================================
    // ✅ AUTO-REFRESH (Hidden - No UI Indicator)
    // =====================================================
    
    private void startAutoRefresh() {
        if (refreshTimer == null) {
            refreshTimer = new Timer(AUTO_REFRESH_DELAY, e -> {
                if (isShowing() && currentAppointment != null) {
                    loadAppointmentData();
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

    // =====================================================
    // ROLE-BASED ACTION BUTTON VISIBILITY
    // =====================================================

    /**
     * Update action button visibility based on user role
     */
    private void updateActionButtons() {
        User currentUser = LoginSession.getInstance().getCurrentUser();
        if (currentUser == null) {
            return;
        }
        
        // Check if current user can perform actions on this appointment
        boolean canEdit = false;
        boolean canCancel = false;
        boolean canDelete = false;
        
        if (currentAppointment != null) {
            canEdit = controller.canEditAppointment(currentAppointment, currentUser);
            canCancel = controller.canCancelAppointment(currentAppointment, currentUser);
            canDelete = controller.canDeleteAppointment(currentAppointment, currentUser);
        }
        
        // Edit button - visible if user can edit
        editButton.setVisible(canEdit);
        
        // Cancel Appointment button - visible if user can cancel
        cancelAppointmentButton.setVisible(canCancel && !"Cancelled".equals(currentAppointment != null ? currentAppointment.getStatus() : ""));
        
        // Delete button - only ADMIN can delete
        deleteButton.setVisible(canDelete);
        
        // Save and Cancel (edit mode) buttons - managed by setViewMode()
    }

    private void loadAppointmentData() {
        if (currentAppointment != null) {
            Appointment updated = controller.getAppointmentById(currentAppointment.getAppointmentId());
            if (updated != null) {
                currentAppointment = updated;
                displayAppointment(currentAppointment);
                updateActionButtons();
            }
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(SOFT_SURFACE);
        setBorder(new EmptyBorder(20, 30, 20, 30));

        // Header Panel
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Details Panel
        add(createDetailsPanel(), BorderLayout.CENTER);
        
        // Footer Panel
        add(createFooterPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(SOFT_SURFACE);
        header.setBorder(new EmptyBorder(0, 0, 15, 0));

        // Title and appointment info
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Appointment Details");
        titleLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_DARK);
        
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        infoPanel.setOpaque(false);
        
        appointmentIdLabel = new JLabel("Appointment ID: --");
        appointmentIdLabel.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 14));
        appointmentIdLabel.setForeground(SECONDARY_TEXT);
        
        createdDateLabel = new JLabel("Created: --");
        createdDateLabel.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 14));
        createdDateLabel.setForeground(SECONDARY_TEXT);
        
        updatedDateLabel = new JLabel("Last Updated: --");
        updatedDateLabel.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 14));
        updatedDateLabel.setForeground(SECONDARY_TEXT);
        
        // Status badge
        statusBadge = new JLabel("--");
        statusBadge.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 12));
        statusBadge.setOpaque(true);
        statusBadge.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        statusBadge.setVisible(false);
        
        infoPanel.add(appointmentIdLabel);
        infoPanel.add(createdDateLabel);
        infoPanel.add(updatedDateLabel);
        infoPanel.add(statusBadge);
        
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        titlePanel.add(infoPanel);

        header.add(titlePanel, BorderLayout.WEST);
        
        // ✅ Manual Refresh Button - ICON ONLY
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);
        
        refreshButton = createIconButton(FontAwesomeSolid.SYNC_ALT, COLOR_REFRESH);
        refreshButton.setPreferredSize(new Dimension(40, 40));
        refreshButton.setToolTipText("Refresh Now");
        refreshButton.addActionListener(e -> loadAppointmentData());
        rightPanel.add(refreshButton);

        header.add(rightPanel, BorderLayout.EAST);

        return header;
    }

    private JPanel createDetailsPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            new EmptyBorder(20, 30, 20, 30)
        ));

        // Patient Information Section
        mainPanel.add(createSectionPanel("Patient Information", createPatientPanel()));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Dentist Information Section
        mainPanel.add(createSectionPanel("Dentist Information", createDentistPanel()));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Appointment Details Section
        mainPanel.add(createSectionPanel("Appointment Details", createAppointmentDetailsPanel()));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Additional Information Section
        mainPanel.add(createSectionPanel("Additional Information", createAdditionalInfoPanel()));

        return mainPanel;
    }

    private JPanel createSectionPanel(String title, JPanel content) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(MINT, 1),
            title,
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font(UI_FONT_FAMILY, Font.BOLD, 14),
            PRIMARY_DARK
        ));
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createPatientPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.weightx = 1.0;

        // Patient Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel nameLabel = new JLabel("Patient Name:");
        nameLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        nameLabel.setForeground(PRIMARY_DARK);
        panel.add(nameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 0.8;
        patientNameLabel = new JLabel("--");
        patientNameLabel.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        patientNameLabel.setForeground(SECONDARY_TEXT);
        panel.add(patientNameLabel, gbc);

        // Phone
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        phoneLabel.setForeground(PRIMARY_DARK);
        panel.add(phoneLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        patientPhoneLabel = new JLabel("--");
        patientPhoneLabel.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        patientPhoneLabel.setForeground(SECONDARY_TEXT);
        panel.add(patientPhoneLabel, gbc);

        // Email
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        emailLabel.setForeground(PRIMARY_DARK);
        panel.add(emailLabel, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        patientEmailLabel = new JLabel("--");
        patientEmailLabel.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        patientEmailLabel.setForeground(SECONDARY_TEXT);
        panel.add(patientEmailLabel, gbc);

        return panel;
    }

    private JPanel createDentistPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.weightx = 1.0;

        // Dentist Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel nameLabel = new JLabel("Dentist Name:");
        nameLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        nameLabel.setForeground(PRIMARY_DARK);
        panel.add(nameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        dentistNameLabel = new JLabel("--");
        dentistNameLabel.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        dentistNameLabel.setForeground(SECONDARY_TEXT);
        panel.add(dentistNameLabel, gbc);

        // Specialization
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel specLabel = new JLabel("Specialization:");
        specLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        specLabel.setForeground(PRIMARY_DARK);
        panel.add(specLabel, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        dentistSpecializationLabel = new JLabel("--");
        dentistSpecializationLabel.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        dentistSpecializationLabel.setForeground(SECONDARY_TEXT);
        panel.add(dentistSpecializationLabel, gbc);

        return panel;
    }

    private JPanel createAppointmentDetailsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.weightx = 1.0;

        // Date
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel dateLabel = new JLabel("Date:");
        dateLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        dateLabel.setForeground(PRIMARY_DARK);
        panel.add(dateLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        dateField = createTextField();
        dateField.setEnabled(false);
        panel.add(dateField, gbc);

        // Time
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel timeLabel = new JLabel("Time:");
        timeLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        timeLabel.setForeground(PRIMARY_DARK);
        panel.add(timeLabel, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        timeCombo = new JComboBox<>(generateTimeSlots());
        timeCombo.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        timeCombo.setPreferredSize(new Dimension(150, 35));
        timeCombo.setEnabled(false);
        panel.add(timeCombo, gbc);

        // Duration
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel durationLabel = new JLabel("Duration:");
        durationLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        durationLabel.setForeground(PRIMARY_DARK);
        panel.add(durationLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        durationCombo = new JComboBox<>(new String[]{"15 min", "30 min", "45 min", "60 min", "90 min", "120 min"});
        durationCombo.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        durationCombo.setPreferredSize(new Dimension(100, 35));
        durationCombo.setEnabled(false);
        panel.add(durationCombo, gbc);

        // Status
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        statusLabel.setForeground(PRIMARY_DARK);
        panel.add(statusLabel, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        statusCombo = new JComboBox<>(new String[]{"Scheduled", "Confirmed", "In Progress", "Completed", "Cancelled", "No Show"});
        statusCombo.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        statusCombo.setPreferredSize(new Dimension(150, 35));
        statusCombo.setEnabled(false);
        panel.add(statusCombo, gbc);

        return panel;
    }

    private JPanel createAdditionalInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.weightx = 1.0;

        // Reason
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel reasonLabel = new JLabel("Reason for Visit:");
        reasonLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        reasonLabel.setForeground(PRIMARY_DARK);
        panel.add(reasonLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.weightx = 0.8;
        reasonArea = createTextArea();
        reasonArea.setEnabled(false);
        JScrollPane reasonScroll = new JScrollPane(reasonArea);
        reasonScroll.setPreferredSize(new Dimension(400, 60));
        panel.add(reasonScroll, gbc);

        // Notes
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel notesLabel = new JLabel("Additional Notes:");
        notesLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        notesLabel.setForeground(PRIMARY_DARK);
        panel.add(notesLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 0.8;
        notesArea = createTextArea();
        notesArea.setEnabled(false);
        JScrollPane notesScroll = new JScrollPane(notesArea);
        notesScroll.setPreferredSize(new Dimension(400, 60));
        panel.add(notesScroll, gbc);

        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(SOFT_SURFACE);
        footer.setBorder(new EmptyBorder(15, 0, 0, 0));

        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 12));
        statusLabel.setForeground(SECONDARY_TEXT);

        buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        // Back button
        backButton = createStyledButton("Back", SOFT_SURFACE, PRIMARY_DARK);
        backButton.setBorderColor(LIGHT_SURFACE);
        backButton.setPreferredSize(new Dimension(100, 35));
        backButton.addActionListener(e -> navigateBack());
        backButton.setIcon(icon(FontAwesomeSolid.ARROW_LEFT, 12, PRIMARY_DARK));
        backButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        backButton.setIconTextGap(6);

        // Edit button
        editButton = createStyledButton("Edit", PRIMARY_DARK, Color.WHITE);
        editButton.setPreferredSize(new Dimension(100, 35));
        editButton.addActionListener(e -> toggleEditMode());
        editButton.setIcon(icon(FontAwesomeSolid.EDIT, 12, Color.WHITE));
        editButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        editButton.setIconTextGap(6);

        // Save button (hidden initially)
        saveButton = createStyledButton("Save", PRIMARY_DARK, Color.WHITE);
        saveButton.setPreferredSize(new Dimension(100, 35));
        saveButton.setVisible(false);
        saveButton.addActionListener(e -> saveAppointment());
        saveButton.setIcon(icon(FontAwesomeSolid.SAVE, 12, Color.WHITE));
        saveButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        saveButton.setIconTextGap(6);

        // Cancel button (hidden initially)
        cancelButton = createStyledButton("Cancel", SOFT_SURFACE, PRIMARY_DARK);
        cancelButton.setBorderColor(LIGHT_SURFACE);
        cancelButton.setPreferredSize(new Dimension(100, 35));
        cancelButton.setVisible(false);
        cancelButton.addActionListener(e -> cancelEdit());
        cancelButton.setIcon(icon(FontAwesomeSolid.TIMES, 12, PRIMARY_DARK));
        cancelButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        cancelButton.setIconTextGap(6);

        // Cancel Appointment button
        cancelAppointmentButton = createStyledButton("Cancel Appointment", SOFT_SURFACE, ERROR_COLOR);
        cancelAppointmentButton.setBorderColor(LIGHT_SURFACE);
        cancelAppointmentButton.setPreferredSize(new Dimension(160, 35));
        cancelAppointmentButton.addActionListener(e -> cancelAppointment());
        cancelAppointmentButton.setIcon(icon(FontAwesomeSolid.TIMES, 12, ERROR_COLOR));
        cancelAppointmentButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        cancelAppointmentButton.setIconTextGap(6);

        // Delete button
        deleteButton = createStyledButton("Delete", ERROR_COLOR, Color.WHITE);
        deleteButton.setPreferredSize(new Dimension(100, 35));
        deleteButton.addActionListener(e -> deleteAppointment());
        deleteButton.setIcon(icon(FontAwesomeSolid.TRASH_ALT, 12, Color.WHITE));
        deleteButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        deleteButton.setIconTextGap(6);

        buttonPanel.add(backButton);
        buttonPanel.add(editButton);
        buttonPanel.add(cancelAppointmentButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(deleteButton);

        footer.add(statusLabel, BorderLayout.WEST);
        footer.add(buttonPanel, BorderLayout.EAST);

        return footer;
    }

    // ========================
    // Helper Methods
    // ========================

    private String[] generateTimeSlots() {
        String[] tempSlots = new String[25];
        int index = 0;
        for (int hour = 8; hour <= 20; hour++) {
            for (int minute = 0; minute < 60; minute += 30) {
                if (hour == 20 && minute > 0) break;
                if (index >= tempSlots.length) break;
                String time = String.format("%02d:%02d", hour, minute);
                tempSlots[index++] = time;
            }
        }
        String[] result = new String[index];
        System.arraycopy(tempSlots, 0, result, 0, index);
        return result;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        field.setBackground(Color.WHITE);
        return field;
    }

    private JTextArea createTextArea() {
        JTextArea area = new JTextArea();
        area.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        area.setBackground(Color.WHITE);
        return area;
    }

    private RoundedButton createStyledButton(String text, Color bg, Color fg) {
        RoundedButton button = new RoundedButton(text, bg, fg);
        button.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 12));
        return button;
    }

    private void updateStatusBadge(String status) {
        statusBadge.setVisible(true);
        statusBadge.setText(" " + status + " ");
        
        switch (status) {
            case "Scheduled":
                statusBadge.setBackground(COLOR_SCHEDULED);
                break;
            case "Confirmed":
                statusBadge.setBackground(COLOR_CONFIRMED);
                break;
            case "In Progress":
                statusBadge.setBackground(COLOR_IN_PROGRESS);
                statusBadge.setForeground(Color.BLACK);
                break;
            case "Completed":
                statusBadge.setBackground(COLOR_COMPLETED);
                break;
            case "Cancelled":
                statusBadge.setBackground(COLOR_CANCELLED);
                break;
            case "No Show":
                statusBadge.setBackground(COLOR_NO_SHOW);
                break;
            default:
                statusBadge.setBackground(LIGHT_SURFACE);
                statusBadge.setForeground(PRIMARY_DARK);
                break;
        }
        statusBadge.setForeground(Color.WHITE);
    }

    private void loadPatientDetails(int patientId) {
        Patient patient = controller.getPatientById(patientId);
        if (patient != null) {
            patientNameLabel.setText(patient.getPatientName());
            patientPhoneLabel.setText(patient.getContactNumber() != null ? patient.getContactNumber() : "--");
            patientEmailLabel.setText(patient.getEmail() != null ? patient.getEmail() : "--");
        }
    }

    private void loadDentistDetails(int dentistId) {
        Dentist dentist = controller.getDentistById(dentistId);
        if (dentist != null) {
            dentistNameLabel.setText(dentist.getDentistName());
            dentistSpecializationLabel.setText(dentist.getSpecialization() != null ? dentist.getSpecialization() : "--");
        }
    }

    private int getDurationInMinutes(String durationStr) {
        if (durationStr == null) return 30;
        return Integer.parseInt(durationStr.replace(" min", ""));
    }

    private String getDurationString(int minutes) {
        return minutes + " min";
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

    private static class MouseAdapter extends java.awt.event.MouseAdapter {
        // Empty implementation
    }

    // ========================
    // Public methods
    // ========================

    public void displayAppointment(Appointment appointment) {
        this.currentAppointment = appointment;
        if (appointment == null) {
            displayEmptyState();
            return;
        }

        this.patientId = appointment.getPatientId();
        this.dentistId = appointment.getDentistId();

        appointmentIdLabel.setText("Appointment ID: " + appointment.getAppointmentId());
        
        loadPatientDetails(patientId);
        loadDentistDetails(dentistId);
        
        if (appointment.getAppointmentDate() != null) {
            dateField.setText(appointment.getAppointmentDate().toString());
        }
        
        if (appointment.getAppointmentTime() != null) {
            String timeStr = appointment.getAppointmentTime().toString().substring(0, 5);
            timeCombo.setSelectedItem(timeStr);
        }
        
        if (appointment.getEndTime() != null && appointment.getAppointmentTime() != null) {
            LocalTime start = appointment.getAppointmentTime().toLocalTime();
            LocalTime end = appointment.getEndTime().toLocalTime();
            int durationMinutes = (int) java.time.Duration.between(start, end).toMinutes();
            durationCombo.setSelectedItem(getDurationString(durationMinutes));
        } else {
            durationCombo.setSelectedIndex(1);
        }
        
        statusCombo.setSelectedItem(appointment.getStatus() != null ? appointment.getStatus() : "Scheduled");
        reasonArea.setText(appointment.getReason() != null ? appointment.getReason() : "");
        notesArea.setText(appointment.getNotes() != null ? appointment.getNotes() : "");
        
        createdDateLabel.setText("Created: " + (appointment.getCreatedAt() != null ? appointment.getCreatedAt() : "--"));
        updatedDateLabel.setText("Last Updated: " + (appointment.getUpdatedAt() != null ? appointment.getUpdatedAt() : "--"));
        
        updateStatusBadge(appointment.getStatus() != null ? appointment.getStatus() : "Scheduled");
        
        statusLabel.setText(" ");
        setViewMode(false);
        updateActionButtons();
    }

    private void displayEmptyState() {
        appointmentIdLabel.setText("Appointment ID: --");
        patientNameLabel.setText("--");
        patientPhoneLabel.setText("--");
        patientEmailLabel.setText("--");
        dentistNameLabel.setText("--");
        dentistSpecializationLabel.setText("--");
        dateField.setText("");
        timeCombo.setSelectedIndex(0);
        durationCombo.setSelectedIndex(1);
        statusCombo.setSelectedIndex(0);
        reasonArea.setText("");
        notesArea.setText("");
        createdDateLabel.setText("Created: --");
        updatedDateLabel.setText("Last Updated: --");
        statusBadge.setVisible(false);
        statusLabel.setText("No appointment selected");
        setViewMode(false);
        updateActionButtons();
    }

    private void setViewMode(boolean editMode) {
        this.isEditMode = editMode;
        
        dateField.setEnabled(editMode);
        timeCombo.setEnabled(editMode);
        durationCombo.setEnabled(editMode);
        statusCombo.setEnabled(editMode);
        reasonArea.setEnabled(editMode);
        notesArea.setEnabled(editMode);

        editButton.setVisible(!editMode && canEditAppointment());
        cancelAppointmentButton.setVisible(!editMode && canCancelAppointment());
        deleteButton.setVisible(!editMode && canDeleteAppointment());
        saveButton.setVisible(editMode);
        cancelButton.setVisible(editMode);

        if (editMode) {
            statusLabel.setText("Editing appointment information...");
            statusLabel.setForeground(new Color(0, 120, 215));
        } else {
            statusLabel.setText(" ");
            statusLabel.setForeground(SECONDARY_TEXT);
        }
    }

    private boolean canEditAppointment() {
        User currentUser = LoginSession.getInstance().getCurrentUser();
        if (currentUser == null || currentAppointment == null) {
            return false;
        }
        return controller.canEditAppointment(currentAppointment, currentUser);
    }

    private boolean canCancelAppointment() {
        User currentUser = LoginSession.getInstance().getCurrentUser();
        if (currentUser == null || currentAppointment == null) {
            return false;
        }
        if ("Cancelled".equals(currentAppointment.getStatus())) {
            return false;
        }
        return controller.canCancelAppointment(currentAppointment, currentUser);
    }

    private boolean canDeleteAppointment() {
        User currentUser = LoginSession.getInstance().getCurrentUser();
        if (currentUser == null || currentAppointment == null) {
            return false;
        }
        return controller.canDeleteAppointment(currentAppointment, currentUser);
    }

    public void toggleEditMode() {
        if (currentAppointment == null) {
            showError("No appointment loaded to edit.");
            return;
        }
        if (!canEditAppointment()) {
            showError("You don't have permission to edit this appointment.");
            return;
        }
        setViewMode(true);
    }

    private void cancelEdit() {
        if (currentAppointment != null) {
            displayAppointment(currentAppointment);
        } else {
            displayEmptyState();
        }
        setViewMode(false);
        statusLabel.setText("Edit cancelled");
        statusLabel.setForeground(SECONDARY_TEXT);
    }

    private void saveAppointment() {
        if (currentAppointment == null) {
            showError("No appointment loaded to save.");
            return;
        }

        String dateStr = dateField.getText().trim();
        if (dateStr.isEmpty()) {
            showError("Date is required.");
            return;
        }

        Date appointmentDate = null;
        try {
            LocalDate localDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            appointmentDate = Date.valueOf(localDate);
            if (localDate.isBefore(LocalDate.now())) {
                showError("Appointment date cannot be in the past.");
                return;
            }
        } catch (Exception e) {
            showError("Invalid date format. Please use YYYY-MM-DD.");
            return;
        }

        String time = (String) timeCombo.getSelectedItem();
        if (time == null || time.isEmpty()) {
            showError("Please select a time.");
            return;
        }

        Time appointmentTime = null;
        try {
            LocalTime localTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
            appointmentTime = Time.valueOf(localTime);
        } catch (Exception e) {
            showError("Invalid time format.");
            return;
        }

        String duration = (String) durationCombo.getSelectedItem();
        int durationMinutes = getDurationInMinutes(duration);
        LocalTime endTime = appointmentTime.toLocalTime().plusMinutes(durationMinutes);

        currentAppointment.setAppointmentDate(appointmentDate);
        currentAppointment.setAppointmentTime(appointmentTime);
        currentAppointment.setEndTime(Time.valueOf(endTime));
        currentAppointment.setStatus((String) statusCombo.getSelectedItem());
        currentAppointment.setReason(reasonArea.getText().trim());
        currentAppointment.setNotes(notesArea.getText().trim());

        statusLabel.setText("Saving appointment...");
        statusLabel.setForeground(new Color(0, 120, 215));
        
        boolean success = controller.updateAppointment(currentAppointment);
        
        if (success) {
            statusLabel.setText("Appointment updated successfully!");
            statusLabel.setForeground(SUCCESS_COLOR);
            setViewMode(false);
            displayAppointment(currentAppointment);
            showSuccess("Appointment updated successfully!");
        } else {
            statusLabel.setText("Failed to update appointment.");
            statusLabel.setForeground(ERROR_COLOR);
            showError("Failed to update appointment. Please try again.");
        }
    }

    private void cancelAppointment() {
        if (currentAppointment == null) {
            showError("No appointment loaded to cancel.");
            return;
        }

        if (!canCancelAppointment()) {
            showError("You don't have permission to cancel this appointment.");
            return;
        }

        if ("Cancelled".equals(currentAppointment.getStatus())) {
            showError("This appointment is already cancelled.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to cancel this appointment?",
            "Cancel Appointment",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = controller.cancelAppointment(currentAppointment.getAppointmentId());
            
            if (success) {
                currentAppointment.setStatus("Cancelled");
                updateStatusBadge("Cancelled");
                statusCombo.setSelectedItem("Cancelled");
                showSuccess("Appointment cancelled successfully!");
                statusLabel.setText("Appointment cancelled");
                statusLabel.setForeground(SUCCESS_COLOR);
                updateActionButtons();
            } else {
                showError("Failed to cancel appointment. Please try again.");
            }
        }
    }

    private void deleteAppointment() {
        if (currentAppointment == null) {
            showError("No appointment loaded to delete.");
            return;
        }

        if (!canDeleteAppointment()) {
            showError("You don't have permission to delete this appointment.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete this appointment?\nThis action cannot be undone.",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = controller.deleteAppointment(currentAppointment.getAppointmentId());
            
            if (success) {
                showSuccess("Appointment deleted successfully!");
                navigateBack();
            } else {
                showError("Failed to delete appointment. Please try again.");
            }
        }
    }

    private void navigateBack() {
        Container parent = getParent();
        while (parent != null && !(parent instanceof MainFrame)) {
            parent = parent.getParent();
        }
        if (parent instanceof MainFrame) {
            ((MainFrame) parent).showCard("APPOINTMENT_LIST");
        }
    }

    // ========================
    // Public methods for Controller
    // ========================

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
}