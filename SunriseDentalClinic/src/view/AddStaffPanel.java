package view;

import controller.StaffController;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public class AddStaffPanel extends JPanel {
    
    // Color Palette
    private static final Color PRIMARY_DARK = new Color(0x2F3E3C);
    private static final Color MINT = new Color(0xBDDBD1);
    private static final Color SOFT_SURFACE = new Color(0xFBF9F1);
    private static final Color LIGHT_SURFACE = new Color(0xE7E9E3);
    private static final Color ERROR_COLOR = new Color(220, 80, 80);
    private static final Color SUCCESS_COLOR = new Color(60, 160, 80);
    private static final Color SECONDARY_TEXT = new Color(122, 138, 135);

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

    // Form Fields - Staff Details
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField positionField;
    private JTextField departmentField;
    private JTextField phoneField;
    private JTextField emailField;
    private JTextField hireDateField;
    private JTextField salaryField;
    private JCheckBox activeCheckBox;
    
    // Login Credentials Section (NEW)
    private JCheckBox createLoginCheckBox;
    private JPanel loginPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JLabel passwordStrengthLabel;
    
    // Buttons
    private RoundedButton saveButton;
    private RoundedButton clearButton;
    private RoundedButton cancelButton;
    
    private JLabel statusLabel;
    private StaffController controller;

    // Auto-refresh timer (hidden)
    private Timer refreshTimer;
    private static final int AUTO_REFRESH_DELAY = 30000; // 30 seconds

    public AddStaffPanel() {
        initComponents();
        this.controller = new StaffController(this);
        loginPanel.setVisible(false); // Initially hidden
        startAutoRefresh();
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
    // AUTO-REFRESH (Hidden - No UI Indicator)
    // =====================================================

    private void startAutoRefresh() {
        if (refreshTimer == null) {
            refreshTimer = new Timer(AUTO_REFRESH_DELAY, e -> {
                if (isShowing()) {
                    // Auto-refresh logic - e.g., check for session expiry
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

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(SOFT_SURFACE);
        header.setBorder(new EmptyBorder(0, 0, 15, 0));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Add New Staff Member");
        titleLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_DARK);
        
        JLabel subtitleLabel = new JLabel("Register a new staff member with optional login account");
        subtitleLabel.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(107, 123, 121));
        
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);

        header.add(titlePanel, BorderLayout.WEST);
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

        // Personal Information Section
        formPanel.add(createSectionPanel("Personal Information", createPersonalInfoPanel()));
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Employment Information Section
        formPanel.add(createSectionPanel("Employment Information", createEmploymentInfoPanel()));
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Contact Information Section
        formPanel.add(createSectionPanel("Contact Information", createContactInfoPanel()));
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Login Credentials Section (NEW)
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
        createLoginCheckBox = new JCheckBox("Create login account for this staff member");
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
        passwordField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                checkPasswordStrength();
            }
        });
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
        
        // Row 2: Password Strength
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.weightx = 0.8;
        passwordStrengthLabel = new JLabel(" ");
        passwordStrengthLabel.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 12));
        passwordStrengthLabel.setForeground(SECONDARY_TEXT);
        loginPanel.add(passwordStrengthLabel, gbc);
        
        mainPanel.add(loginPanel, BorderLayout.CENTER);
        panel.add(mainPanel, BorderLayout.CENTER);
        return panel;
    }

    private void checkPasswordStrength() {
        String password = new String(passwordField.getPassword());
        if (password.isEmpty()) {
            passwordStrengthLabel.setText(" ");
            passwordStrengthLabel.setForeground(SECONDARY_TEXT);
            return;
        }
        
        int strength = 0;
        if (password.length() >= 6) strength++;
        if (password.length() >= 10) strength++;
        if (password.matches(".*[A-Z].*")) strength++;
        if (password.matches(".*[a-z].*")) strength++;
        if (password.matches(".*\\d.*")) strength++;
        if (password.matches(".*[!@#$%^&*].*")) strength++;
        
        if (strength <= 2) {
            passwordStrengthLabel.setIcon(icon(FontAwesomeSolid.TIMES_CIRCLE, 12, ERROR_COLOR));
            passwordStrengthLabel.setText(" Weak");
            passwordStrengthLabel.setForeground(ERROR_COLOR);
        } else if (strength <= 4) {
            passwordStrengthLabel.setIcon(icon(FontAwesomeSolid.EXCLAMATION_TRIANGLE, 12, new Color(200, 180, 0)));
            passwordStrengthLabel.setText(" Medium");
            passwordStrengthLabel.setForeground(new Color(200, 180, 0));
        } else {
            passwordStrengthLabel.setIcon(icon(FontAwesomeSolid.CHECK_CIRCLE, 12, SUCCESS_COLOR));
            passwordStrengthLabel.setText(" Strong");
            passwordStrengthLabel.setForeground(SUCCESS_COLOR);
        }
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

        // First Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel firstNameLabel = new JLabel("First Name:");
        firstNameLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        firstNameLabel.setForeground(PRIMARY_DARK);
        panel.add(firstNameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        firstNameField = createTextField();
        panel.add(firstNameField, gbc);

        // Last Name
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel lastNameLabel = new JLabel("Last Name:");
        lastNameLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        lastNameLabel.setForeground(PRIMARY_DARK);
        panel.add(lastNameLabel, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        lastNameField = createTextField();
        panel.add(lastNameField, gbc);

        return panel;
    }

    private JPanel createEmploymentInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.weightx = 1.0;

        // Position
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel positionLabel = new JLabel("Position:");
        positionLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        positionLabel.setForeground(PRIMARY_DARK);
        panel.add(positionLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        positionField = createTextField();
        positionField.setToolTipText("e.g., Receptionist, Dental Assistant, Manager");
        panel.add(positionField, gbc);

        // Department
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel departmentLabel = new JLabel("Department:");
        departmentLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        departmentLabel.setForeground(PRIMARY_DARK);
        panel.add(departmentLabel, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        departmentField = createTextField();
        departmentField.setToolTipText("e.g., Front Desk, Clinical, Administration");
        panel.add(departmentField, gbc);

        // Hire Date
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel hireDateLabel = new JLabel("Hire Date (YYYY-MM-DD):");
        hireDateLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        hireDateLabel.setForeground(PRIMARY_DARK);
        panel.add(hireDateLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        hireDateField = createTextField();
        hireDateField.setText(LocalDate.now().toString());
        hireDateField.setToolTipText("Date of joining");
        panel.add(hireDateField, gbc);

        // Salary
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel salaryLabel = new JLabel("Salary (RS):");
        salaryLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        salaryLabel.setForeground(PRIMARY_DARK);
        panel.add(salaryLabel, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        salaryField = createTextField();
        salaryField.setToolTipText("Annual salary in RS");
        panel.add(salaryField, gbc);

        // Status
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel activeLabel = new JLabel("Status:");
        activeLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        activeLabel.setForeground(PRIMARY_DARK);
        panel.add(activeLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 0.8;
        activeCheckBox = new JCheckBox("Active");
        activeCheckBox.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        activeCheckBox.setSelected(true);
        activeCheckBox.setToolTipText("Check if the staff member is currently active");
        panel.add(activeCheckBox, gbc);

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

        // Phone
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        phoneLabel.setForeground(PRIMARY_DARK);
        panel.add(phoneLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        phoneField = createTextField();
        phoneField.setToolTipText("Contact phone number (at least 10 digits)");
        panel.add(phoneField, gbc);

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
        emailField.setToolTipText("Valid email address");
        panel.add(emailField, gbc);

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

        saveButton = createStyledButton("Save Staff", PRIMARY_DARK, Color.WHITE);
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

    // Staff getters
    public String getFirstName() { return firstNameField.getText().trim(); }
    public String getLastName() { return lastNameField.getText().trim(); }
    public String getPosition() { return positionField.getText().trim(); }
    public String getDepartment() { return departmentField.getText().trim(); }
    public String getPhone() { return phoneField.getText().trim(); }
    public String getEmail() { return emailField.getText().trim(); }
    public String getHireDate() { return hireDateField.getText().trim(); }
    public String getSalary() { return salaryField.getText().trim(); }
    public boolean isActive() { return activeCheckBox.isSelected(); }
    
    // Login getters
    public boolean isCreateLogin() { return createLoginCheckBox.isSelected(); }
    public String getUsername() { return usernameField != null ? usernameField.getText().trim() : ""; }
    public String getPassword() { return passwordField != null ? new String(passwordField.getPassword()) : ""; }
    public String getConfirmPassword() { return confirmPasswordField != null ? new String(confirmPasswordField.getPassword()) : ""; }

    public void clearForm() {
        firstNameField.setText("");
        lastNameField.setText("");
        positionField.setText("");
        departmentField.setText("");
        phoneField.setText("");
        emailField.setText("");
        hireDateField.setText(LocalDate.now().toString());
        salaryField.setText("");
        activeCheckBox.setSelected(true);
        usernameField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        createLoginCheckBox.setSelected(false);
        loginPanel.setVisible(false);
        passwordStrengthLabel.setText(" ");
        statusLabel.setText("Form cleared");
        statusLabel.setForeground(SECONDARY_TEXT);
    }

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
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showInfo(String message) {
        statusLabel.setIcon(icon(FontAwesomeSolid.INFO_CIRCLE, 14, new Color(0, 120, 215)));
        statusLabel.setText(message);
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