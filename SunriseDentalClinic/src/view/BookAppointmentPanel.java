package view;

import controller.AppointmentController;
import model.Patient;
import model.Dentist;
import model.Appointment;
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
import java.util.List;

public class BookAppointmentPanel extends JPanel {
    
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

    // Form Fields
    private JComboBox<Patient> patientCombo;
    private JComboBox<Dentist> dentistCombo;
    private JTextField dateField;
    private JComboBox<String> timeCombo;
    private JComboBox<String> durationCombo;
    private JTextArea reasonArea;
    private JTextArea notesArea;
    private JComboBox<String> statusCombo;
    
    // Labels for additional info
    private JLabel patientPhoneLabel;
    private JLabel patientEmailLabel;
    private JLabel dentistSpecializationLabel;
    private JLabel consultationFeeLabel;
    
    // Buttons
    private RoundedButton bookButton;
    private RoundedButton clearButton;
    private RoundedButton cancelButton;
    private RoundedButton checkAvailabilityButton;
    private JButton refreshButton;
    
    private JLabel statusLabel;
    private AppointmentController controller;

    // ✅ Auto-refresh timer (hidden)
    private Timer refreshTimer;
    private static final int AUTO_REFRESH_DELAY = 30000; // 30 seconds

    public BookAppointmentPanel() {
        initComponents();
        this.controller = new AppointmentController(this);
        loadData();
        startAutoRefresh();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(SOFT_SURFACE);
        setBorder(new EmptyBorder(20, 30, 20, 30));

        // Header Panel
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Form Panel
        add(createFormPanel(), BorderLayout.CENTER);
        
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
                    loadData();
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

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(SOFT_SURFACE);
        header.setBorder(new EmptyBorder(0, 0, 15, 0));

        // Title
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Book Appointment");
        titleLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_DARK);
        
        JLabel subtitleLabel = new JLabel("Schedule a new appointment for a patient");
        subtitleLabel.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(107, 123, 121));
        
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);

        header.add(titlePanel, BorderLayout.WEST);
        
        // ✅ Manual Refresh Button - ICON ONLY
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);
        
        refreshButton = createIconButton(FontAwesomeSolid.SYNC_ALT, COLOR_REFRESH);
        refreshButton.setPreferredSize(new Dimension(40, 40));
        refreshButton.setToolTipText("Refresh Data");
        refreshButton.addActionListener(e -> loadData());
        rightPanel.add(refreshButton);

        header.add(rightPanel, BorderLayout.EAST);

        return header;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            new EmptyBorder(20, 30, 20, 30)
        ));

        // Patient Information Section
        formPanel.add(createSectionPanel("Patient Information", createPatientPanel()));
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Appointment Details Section
        formPanel.add(createSectionPanel("Appointment Details", createAppointmentPanel()));
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Additional Information Section
        formPanel.add(createSectionPanel("Additional Information", createAdditionalInfoPanel()));

        return formPanel;
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

        // Patient Selection
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel patientLabel = new JLabel("Select Patient:");
        patientLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        patientLabel.setForeground(PRIMARY_DARK);
        panel.add(patientLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 0.8;
        patientCombo = new JComboBox<>();
        patientCombo.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        patientCombo.setPreferredSize(new Dimension(300, 35));
        patientCombo.addActionListener(e -> loadPatientDetails());
        panel.add(patientCombo, gbc);

        // Patient Details
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

    private JPanel createAppointmentPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.weightx = 1.0;

        // Dentist Selection
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel dentistLabel = new JLabel("Select Dentist:");
        dentistLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        dentistLabel.setForeground(PRIMARY_DARK);
        panel.add(dentistLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 0.8;
        dentistCombo = new JComboBox<>();
        dentistCombo.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        dentistCombo.setPreferredSize(new Dimension(300, 35));
        dentistCombo.addActionListener(e -> loadDentistDetails());
        panel.add(dentistCombo, gbc);

        // Dentist Details
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel specLabel = new JLabel("Specialization:");
        specLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        specLabel.setForeground(PRIMARY_DARK);
        panel.add(specLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        dentistSpecializationLabel = new JLabel("--");
        dentistSpecializationLabel.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        dentistSpecializationLabel.setForeground(SECONDARY_TEXT);
        panel.add(dentistSpecializationLabel, gbc);

        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel feeLabel = new JLabel("Consultation Fee:");
        feeLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        feeLabel.setForeground(PRIMARY_DARK);
        panel.add(feeLabel, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        consultationFeeLabel = new JLabel("--");
        consultationFeeLabel.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        consultationFeeLabel.setForeground(SECONDARY_TEXT);
        panel.add(consultationFeeLabel, gbc);

        // Date
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel dateLabel = new JLabel("Date (YYYY-MM-DD):");
        dateLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        dateLabel.setForeground(PRIMARY_DARK);
        panel.add(dateLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        dateField = new JTextField(LocalDate.now().toString());
        dateField.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        dateField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        panel.add(dateField, gbc);

        // Check Availability Button
        gbc.gridx = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 0.5;
        checkAvailabilityButton = createStyledButton("Check Availability", PRIMARY_DARK, Color.WHITE);
        checkAvailabilityButton.setPreferredSize(new Dimension(160, 35));
        checkAvailabilityButton.addActionListener(e -> checkAvailability());
        checkAvailabilityButton.setIcon(icon(FontAwesomeSolid.CHECK_CIRCLE, 14, Color.WHITE));
        checkAvailabilityButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        checkAvailabilityButton.setIconTextGap(8);
        panel.add(checkAvailabilityButton, gbc);

        // Time
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel timeLabel = new JLabel("Time:");
        timeLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        timeLabel.setForeground(PRIMARY_DARK);
        panel.add(timeLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        timeCombo = new JComboBox<>(generateTimeSlots());
        timeCombo.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        timeCombo.setPreferredSize(new Dimension(150, 35));
        panel.add(timeCombo, gbc);

        // Duration
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel durationLabel = new JLabel("Duration:");
        durationLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        durationLabel.setForeground(PRIMARY_DARK);
        panel.add(durationLabel, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        durationCombo = new JComboBox<>(new String[]{"15 min", "30 min", "45 min", "60 min", "90 min", "120 min"});
        durationCombo.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        durationCombo.setPreferredSize(new Dimension(100, 35));
        panel.add(durationCombo, gbc);

        // Status
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        statusLabel.setForeground(PRIMARY_DARK);
        panel.add(statusLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 0.8;
        statusCombo = new JComboBox<>(new String[]{"Scheduled", "Confirmed", "Pending"});
        statusCombo.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        statusCombo.setPreferredSize(new Dimension(200, 35));
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

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        bookButton = createStyledButton("Book Appointment", PRIMARY_DARK, Color.WHITE);
        bookButton.setPreferredSize(new Dimension(180, 40));
        bookButton.addActionListener(e -> bookAppointment());
        bookButton.setIcon(icon(FontAwesomeSolid.CALENDAR_PLUS, 14, Color.WHITE));
        bookButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        bookButton.setIconTextGap(8);

        clearButton = createStyledButton("Clear", SOFT_SURFACE, PRIMARY_DARK);
        clearButton.setBorderColor(LIGHT_SURFACE);
        clearButton.setPreferredSize(new Dimension(100, 40));
        clearButton.addActionListener(e -> clearForm());
        clearButton.setIcon(icon(FontAwesomeSolid.ERASER, 14, PRIMARY_DARK));
        clearButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        clearButton.setIconTextGap(8);

        cancelButton = createStyledButton("Cancel", SOFT_SURFACE, PRIMARY_DARK);
        cancelButton.setBorderColor(LIGHT_SURFACE);
        cancelButton.setPreferredSize(new Dimension(100, 40));
        cancelButton.addActionListener(e -> navigateBack());
        cancelButton.setIcon(icon(FontAwesomeSolid.TIMES, 14, PRIMARY_DARK));
        cancelButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        cancelButton.setIconTextGap(8);

        buttonPanel.add(clearButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(bookButton);

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
    // Data Loading Methods
    // ========================

    private void loadData() {
        loadPatients();
        loadDentists();
    }

    private void loadPatients() {
        List<Patient> patients = controller.getAllPatients();
        patientCombo.removeAllItems();
        if (patients != null) {
            for (Patient patient : patients) {
                patientCombo.addItem(patient);
            }
        }
    }

    private void loadDentists() {
        List<Dentist> dentists = controller.getAllDentists();
        dentistCombo.removeAllItems();
        if (dentists != null) {
            for (Dentist dentist : dentists) {
                dentistCombo.addItem(dentist);
            }
        }
    }

    private void loadPatientDetails() {
        Patient selected = (Patient) patientCombo.getSelectedItem();
        if (selected != null) {
            patientPhoneLabel.setText(selected.getContactNumber() != null ? selected.getContactNumber() : "--");
            patientEmailLabel.setText(selected.getEmail() != null ? selected.getEmail() : "--");
        } else {
            patientPhoneLabel.setText("--");
            patientEmailLabel.setText("--");
        }
    }

    private void loadDentistDetails() {
        Dentist selected = (Dentist) dentistCombo.getSelectedItem();
        if (selected != null) {
            dentistSpecializationLabel.setText(selected.getSpecialization() != null ? selected.getSpecialization() : "--");
            consultationFeeLabel.setText(selected.getConsultationFee() > 0 ? "RS" + selected.getConsultationFee() : "--");
        } else {
            dentistSpecializationLabel.setText("--");
            consultationFeeLabel.setText("--");
        }
    }

    private void checkAvailability() {
        Dentist dentist = (Dentist) dentistCombo.getSelectedItem();
        String date = dateField.getText().trim();
        String time = (String) timeCombo.getSelectedItem();

        if (dentist == null) {
            showError("Please select a dentist.");
            return;
        }

        if (date.isEmpty()) {
            showError("Please enter a date.");
            return;
        }

        if (time == null || time.isEmpty()) {
            showError("Please select a time.");
            return;
        }

        boolean available = controller.checkAvailability(dentist.getDentistId(), date, time);
        
        if (available) {
            showSuccess("The dentist is available at " + time + " on " + date);
        } else {
            showError("The dentist is not available at " + time + " on " + date + ". Please select another time.");
        }
    }

    // ========================
    // Core Actions
    // ========================

    private void bookAppointment() {
        Patient patient = (Patient) patientCombo.getSelectedItem();
        Dentist dentist = (Dentist) dentistCombo.getSelectedItem();
        String date = dateField.getText().trim();
        String time = (String) timeCombo.getSelectedItem();
        String duration = (String) durationCombo.getSelectedItem();
        String reason = reasonArea.getText().trim();
        String notes = notesArea.getText().trim();
        String status = (String) statusCombo.getSelectedItem();

        if (patient == null) {
            showError("Please select a patient.");
            return;
        }

        if (dentist == null) {
            showError("Please select a dentist.");
            return;
        }

        if (date.isEmpty()) {
            showError("Please enter a date.");
            return;
        }

        Date appointmentDate = null;
        try {
            LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            appointmentDate = Date.valueOf(localDate);
            
            if (localDate.isBefore(LocalDate.now())) {
                showError("Appointment date cannot be in the past.");
                return;
            }
        } catch (Exception e) {
            showError("Invalid date format. Please use YYYY-MM-DD.");
            return;
        }

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

        int durationMinutes = 30;
        if (duration != null) {
            durationMinutes = Integer.parseInt(duration.replace(" min", ""));
        }

        LocalTime endTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm")).plusMinutes(durationMinutes);

        Appointment appointment = new Appointment(
            patient.getPatientId(),
            dentist.getDentistId(),
            appointmentDate,
            appointmentTime,
            Time.valueOf(endTime),
            status,
            reason,
            notes
        );

        showInfo("Booking appointment... Please wait.");
        setCursor(new Cursor(Cursor.WAIT_CURSOR));

        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return controller.bookAppointment(appointment);
            }

            @Override
            protected void done() {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                try {
                    boolean success = get();
                    if (success) {
                        showSuccess("Appointment booked successfully!");
                        clearForm();
                        
                        Timer timer = new Timer(1500, e -> navigateBack());
                        timer.setRepeats(false);
                        timer.start();
                    } else {
                        showError("Failed to book appointment. Please try again.");
                    }
                } catch (Exception e) {
                    showError("Error booking appointment: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void clearForm() {
        if (patientCombo.getItemCount() > 0) patientCombo.setSelectedIndex(0);
        if (dentistCombo.getItemCount() > 0) dentistCombo.setSelectedIndex(0);
        dateField.setText(LocalDate.now().toString());
        if (timeCombo.getItemCount() > 0) timeCombo.setSelectedIndex(0);
        if (durationCombo.getItemCount() > 0) durationCombo.setSelectedIndex(1);
        reasonArea.setText("");
        notesArea.setText("");
        if (statusCombo.getItemCount() > 0) statusCombo.setSelectedIndex(0);
        statusLabel.setText("Form cleared");
        statusLabel.setForeground(SECONDARY_TEXT);
        loadPatientDetails();
        loadDentistDetails();
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
        statusLabel.setForeground(new Color(0, 120, 215));
    }

    // Getters for Controller
    public JComboBox<Patient> getPatientCombo() { return patientCombo; }
    public JComboBox<Dentist> getDentistCombo() { return dentistCombo; }
    public JTextField getDateField() { return dateField; }
    public JComboBox<String> getTimeCombo() { return timeCombo; }
    public JComboBox<String> getDurationCombo() { return durationCombo; }
    public JTextArea getReasonArea() { return reasonArea; }
    public JTextArea getNotesArea() { return notesArea; }
    public JComboBox<String> getStatusCombo() { return statusCombo; }
}