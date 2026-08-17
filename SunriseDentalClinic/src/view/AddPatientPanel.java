package view;

import controller.PatientController;
import model.Patient;
import model.User;
import model.User.UserRole;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AddPatientPanel extends JPanel {
    
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

    // Form Fields - Patient Details
    private JTextField patientNameField;
    private JComboBox<String> genderCombo;
    private JTextArea addressArea;
    private JTextField contactNumberField;
    private JTextField emailField;
    private JTextField dobField;
    private JTextField emergencyContactField;
    private JTextField emergencyPhoneField;
    private JTextArea medicalHistoryArea;
    private JTextArea allergiesArea;
    
    // Login Credentials Section
    private JCheckBox createLoginCheckBox;
    private JPanel loginPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    
    // Buttons
    private RoundedButton saveButton;
    private RoundedButton clearButton;
    private RoundedButton cancelButton;
    private JButton refreshButton;
    
    private JLabel statusLabel;
    private PatientController controller;

    // ✅ Auto-refresh timer (hidden)
    private Timer refreshTimer;
    private static final int AUTO_REFRESH_DELAY = 30000; // 30 seconds

    public AddPatientPanel() {
        initComponents();
        this.controller = new PatientController(this);
        loginPanel.setVisible(false); // Initially hidden
        startAutoRefresh(); // ✅ Start auto-refresh
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(SOFT_SURFACE);
        setBorder(new EmptyBorder(20, 30, 20, 30));

        // Header Panel
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Form Panel
        JPanel formPanel = createFormPanel();
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
        
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
                    // Just refresh the status or any dynamic data
                    // For add panel, we just keep it clean
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

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Add New Patient");
        titleLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_DARK);
        
        JLabel subtitleLabel = new JLabel("Register a new patient with optional login account");
        subtitleLabel.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(107, 123, 121));
        
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);

        // ✅ Manual Refresh Button - ICON ONLY
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);
        
        refreshButton = createIconButton(FontAwesomeSolid.SYNC_ALT, COLOR_REFRESH);
        refreshButton.setPreferredSize(new Dimension(40, 40));
        refreshButton.setToolTipText("Refresh Form");
        refreshButton.addActionListener(e -> clearForm());
        rightPanel.add(refreshButton);

        header.add(titlePanel, BorderLayout.WEST);
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
        formPanel.add(createSectionPanel("Personal Information", createPersonalInfoPanel()));
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Contact Information Section
        formPanel.add(createSectionPanel("Contact Information", createContactInfoPanel()));
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Emergency Contact Section
        formPanel.add(createSectionPanel("Emergency Contact", createEmergencyContactPanel()));
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Medical Information Section
        formPanel.add(createSectionPanel("Medical Information", createMedicalInfoPanel()));
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Login Credentials Section
        formPanel.add(createLoginSection());
        
        return formPanel;
    }

    private JPanel createLoginSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(MINT, 1),
            "Login Account (Optional)",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font(UI_FONT_FAMILY, Font.BOLD, 14),
            PRIMARY_DARK
        ));
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Checkbox
        JPanel checkPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        checkPanel.setOpaque(false);
        createLoginCheckBox = new JCheckBox("Create login account for this patient");
        createLoginCheckBox.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        createLoginCheckBox.setForeground(PRIMARY_DARK);
        createLoginCheckBox.addActionListener(e -> loginPanel.setVisible(createLoginCheckBox.isSelected()));
        checkPanel.add(createLoginCheckBox);
        
        mainPanel.add(checkPanel, BorderLayout.NORTH);
        
        // Login Credentials Panel (hidden by default)
        loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(Color.WHITE);
        loginPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            new EmptyBorder(10, 10, 10, 10)
        ));
        loginPanel.setVisible(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.weightx = 1.0;
        
        // Row 0: Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        usernameLabel.setForeground(PRIMARY_DARK);
        loginPanel.add(usernameLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 0.8;
        usernameField = new JTextField();
        usernameField.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        loginPanel.add(usernameField, gbc);
        
        // Row 1: Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        passwordLabel.setForeground(PRIMARY_DARK);
        loginPanel.add(passwordLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        passwordField = new JPasswordField();
        passwordField.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        loginPanel.add(passwordField, gbc);
        
        // Row 1: Confirm Password
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel confirmLabel = new JLabel("Confirm Password:");
        confirmLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        confirmLabel.setForeground(PRIMARY_DARK);
        loginPanel.add(confirmLabel, gbc);
        
        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        confirmPasswordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        loginPanel.add(confirmPasswordField, gbc);
        
        mainPanel.add(loginPanel, BorderLayout.CENTER);
        
        panel.add(mainPanel, BorderLayout.CENTER);
        return panel;
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

    private JPanel createPersonalInfoPanel() {
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
        patientNameField = createTextField();
        panel.add(patientNameField, gbc);

        // Gender
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel genderLabel = new JLabel("Gender:");
        genderLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        genderLabel.setForeground(PRIMARY_DARK);
        panel.add(genderLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        genderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        genderCombo.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        panel.add(genderCombo, gbc);

        // Date of Birth
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel dobLabel = new JLabel("Date of Birth (YYYY-MM-DD):");
        dobLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        dobLabel.setForeground(PRIMARY_DARK);
        panel.add(dobLabel, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        dobField = createTextField();
        panel.add(dobField, gbc);

        return panel;
    }

    private JPanel createContactInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.weightx = 1.0;

        // Contact Number
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel contactLabel = new JLabel("Contact Number:");
        contactLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        contactLabel.setForeground(PRIMARY_DARK);
        panel.add(contactLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        contactNumberField = createTextField();
        panel.add(contactNumberField, gbc);

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
        emailField = createTextField();
        panel.add(emailField, gbc);

        // Address
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        addressLabel.setForeground(PRIMARY_DARK);
        panel.add(addressLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 0.8;
        addressArea = createTextArea();
        JScrollPane addressScroll = new JScrollPane(addressArea);
        addressScroll.setPreferredSize(new Dimension(400, 60));
        panel.add(addressScroll, gbc);

        return panel;
    }

    private JPanel createEmergencyContactPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.weightx = 1.0;

        // Emergency Contact Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel contactNameLabel = new JLabel("Contact Name:");
        contactNameLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        contactNameLabel.setForeground(PRIMARY_DARK);
        panel.add(contactNameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        emergencyContactField = createTextField();
        panel.add(emergencyContactField, gbc);

        // Emergency Phone
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel phoneLabel = new JLabel("Contact Phone:");
        phoneLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        phoneLabel.setForeground(PRIMARY_DARK);
        panel.add(phoneLabel, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        emergencyPhoneField = createTextField();
        panel.add(emergencyPhoneField, gbc);

        return panel;
    }

    private JPanel createMedicalInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.weightx = 1.0;

        // Medical History
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel historyLabel = new JLabel("Medical History:");
        historyLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        historyLabel.setForeground(PRIMARY_DARK);
        panel.add(historyLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.weightx = 0.8;
        medicalHistoryArea = createTextArea();
        JScrollPane historyScroll = new JScrollPane(medicalHistoryArea);
        historyScroll.setPreferredSize(new Dimension(400, 60));
        panel.add(historyScroll, gbc);

        // Allergies
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel allergiesLabel = new JLabel("Allergies:");
        allergiesLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        allergiesLabel.setForeground(PRIMARY_DARK);
        panel.add(allergiesLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 0.8;
        allergiesArea = createTextArea();
        JScrollPane allergiesScroll = new JScrollPane(allergiesArea);
        allergiesScroll.setPreferredSize(new Dimension(400, 60));
        panel.add(allergiesScroll, gbc);

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

        saveButton = createStyledButton("Save Patient", PRIMARY_DARK, Color.WHITE);
        saveButton.setPreferredSize(new Dimension(160, 40));
        saveButton.setIcon(icon(FontAwesomeSolid.SAVE, 14, Color.WHITE));
        saveButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        saveButton.setIconTextGap(8);

        clearButton = createStyledButton("Clear", SOFT_SURFACE, PRIMARY_DARK);
        clearButton.setBorderColor(LIGHT_SURFACE);
        clearButton.setPreferredSize(new Dimension(100, 40));
        clearButton.setIcon(icon(FontAwesomeSolid.ERASER, 14, PRIMARY_DARK));
        clearButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        clearButton.setIconTextGap(8);

        cancelButton = createStyledButton("Cancel", SOFT_SURFACE, PRIMARY_DARK);
        cancelButton.setBorderColor(LIGHT_SURFACE);
        cancelButton.setPreferredSize(new Dimension(100, 40));
        cancelButton.setIcon(icon(FontAwesomeSolid.TIMES, 14, PRIMARY_DARK));
        cancelButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        cancelButton.setIconTextGap(8);

        buttonPanel.add(clearButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        footer.add(statusLabel, BorderLayout.WEST);
        footer.add(buttonPanel, BorderLayout.EAST);

        return footer;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        field.setPreferredSize(new Dimension(200, 35));
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
    // Public methods for Controller
    // ========================

    // Patient getters
    public String getPatientName() { return patientNameField.getText().trim(); }
    public String getGender() { return (String) genderCombo.getSelectedItem(); }
    public String getAddress() { return addressArea.getText().trim(); }
    public String getContactNumber() { return contactNumberField.getText().trim(); }
    public String getEmail() { return emailField.getText().trim(); }
    public String getDateOfBirth() { return dobField.getText().trim(); }
    public String getEmergencyContact() { return emergencyContactField.getText().trim(); }
    public String getEmergencyPhone() { return emergencyPhoneField.getText().trim(); }
    public String getMedicalHistory() { return medicalHistoryArea.getText().trim(); }
    public String getAllergies() { return allergiesArea.getText().trim(); }
    
    // Login getters
    public boolean isCreateLogin() { return createLoginCheckBox.isSelected(); }
    public String getUsername() { return usernameField != null ? usernameField.getText().trim() : ""; }
    public String getPassword() { return passwordField != null ? new String(passwordField.getPassword()) : ""; }
    public String getConfirmPassword() { return confirmPasswordField != null ? new String(confirmPasswordField.getPassword()) : ""; }

    public void clearForm() {
        patientNameField.setText("");
        genderCombo.setSelectedIndex(0);
        addressArea.setText("");
        contactNumberField.setText("");
        emailField.setText("");
        dobField.setText("");
        emergencyContactField.setText("");
        emergencyPhoneField.setText("");
        medicalHistoryArea.setText("");
        allergiesArea.setText("");
        usernameField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        createLoginCheckBox.setSelected(false);
        loginPanel.setVisible(false);
        statusLabel.setText("Form cleared");
        statusLabel.setForeground(SECONDARY_TEXT);
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
        statusLabel.setForeground(new Color(0, 120, 215));
    }

    public void addSaveListener(ActionListener listener) {
        saveButton.addActionListener(listener);
    }

    public void addClearListener(ActionListener listener) {
        clearButton.addActionListener(listener);
    }

    public void addCancelListener(ActionListener listener) {
        cancelButton.addActionListener(listener);
    }
}