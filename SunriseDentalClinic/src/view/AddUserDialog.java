package view;

import controller.UserController;
import model.User;
import model.User.UserRole;
import model.LoginSession;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class AddUserDialog extends JDialog {
    
    private static final Color PRIMARY_DARK = new Color(0x2F3E3C);
    private static final Color MINT = new Color(0xBDDBD1);
    private static final Color SOFT_SURFACE = new Color(0xFBF9F1);
    private static final Color LIGHT_SURFACE = new Color(0xE7E9E3);
    private static final Color ERROR_COLOR = new Color(220, 80, 80);
    private static final Color SUCCESS_COLOR = new Color(60, 160, 80);
    private static final Color SECONDARY_TEXT = new Color(122, 138, 135);

    // User credentials fields
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField emailField;
    private JComboBox<String> roleCombo;
    
    // Role-specific profile panels
    private JPanel profilePanel;
    
    // Staff fields
    private JTextField staffFirstNameField;
    private JTextField staffLastNameField;
    private JTextField staffPositionField;
    private JTextField staffDepartmentField;
    private JTextField staffPhoneField;
    private JTextField staffEmailField;
    private JTextField staffHireDateField;
    private JTextField staffSalaryField;
    
    // Dentist fields
    private JTextField dentistNameField;
    private JTextField dentistSpecializationField;
    private JTextField dentistLicenseField;
    private JTextField dentistWorkingHoursField;
    private JTextField dentistPhoneField;
    private JTextField dentistEmailField;
    private JTextField dentistExperienceField;
    private JTextField dentistFeeField;
    
    // Patient fields
    private JTextField patientNameField;
    private JComboBox<String> patientGenderCombo;
    private JTextArea patientAddressArea;
    private JTextField patientPhoneField;
    private JTextField patientEmailField;
    private JTextField patientDobField;
    private JTextField patientEmergencyContactField;
    private JTextField patientEmergencyPhoneField;
    private JTextArea patientMedicalHistoryArea;
    private JTextArea patientAllergiesArea;
    
    private RoundedButton saveButton;
    private RoundedButton cancelButton;
    private JLabel statusLabel;
    private UserController controller;
    private UserRole selectedRole;
    private int createdBy; // Moved to class level

    public AddUserDialog(JFrame parent, UserController controller) {
        super(parent, "Add New User", true);
        this.controller = controller;
        initComponents();
        setLocationRelativeTo(parent);
        pack();
        setMinimumSize(new Dimension(750, 650));
        setResizable(true);
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(SOFT_SURFACE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(SOFT_SURFACE);
        mainPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        // Header
        JLabel titleLabel = new JLabel("Create New User Account");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(PRIMARY_DARK);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        JLabel subtitleLabel = new JLabel("Fill in the user credentials and profile information");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(SECONDARY_TEXT);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(subtitleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // User Credentials Panel
        mainPanel.add(createUserCredentialsPanel());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Role selection hint
        JLabel roleHintLabel = new JLabel("Select a role to see the required profile fields");
        roleHintLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        roleHintLabel.setForeground(SECONDARY_TEXT);
        roleHintLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(roleHintLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Role-specific Profile Panel
        profilePanel = new JPanel(new BorderLayout());
        profilePanel.setBackground(Color.WHITE);
        profilePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(MINT, 1),
            "Profile Information",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            PRIMARY_DARK
        ));
        profilePanel.setPreferredSize(new Dimension(600, 250));
        
        // Default to staff profile
        JPanel staffPanel = createStaffProfilePanel();
        profilePanel.add(staffPanel, BorderLayout.CENTER);
        selectedRole = UserRole.RECEPTION;
        
        mainPanel.add(profilePanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Status label
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(SECONDARY_TEXT);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(statusLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        saveButton = createButton("Create User", PRIMARY_DARK, Color.WHITE);
        saveButton.setPreferredSize(new Dimension(140, 40));
        saveButton.addActionListener(e -> createUser());

        cancelButton = createButton("Cancel", SOFT_SURFACE, PRIMARY_DARK);
        cancelButton.setBorderColor(LIGHT_SURFACE);
        cancelButton.setPreferredSize(new Dimension(100, 40));
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        mainPanel.add(buttonPanel);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createUserCredentialsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(MINT, 1),
            "Login Credentials",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            PRIMARY_DARK
        ));
        panel.setPreferredSize(new Dimension(600, 180));

        JPanel fieldsPanel = new JPanel(new GridLayout(3, 2, 15, 10));
        fieldsPanel.setOpaque(false);
        fieldsPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Row 0: Username
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        usernameLabel.setForeground(PRIMARY_DARK);
        fieldsPanel.add(usernameLabel);
        
        usernameField = new JTextField();
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        fieldsPanel.add(usernameField);

        // Row 1: Password
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        passwordLabel.setForeground(PRIMARY_DARK);
        fieldsPanel.add(passwordLabel);
        
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        fieldsPanel.add(passwordField);

        // Row 2: Confirm Password
        JLabel confirmLabel = new JLabel("Confirm Password:");
        confirmLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        confirmLabel.setForeground(PRIMARY_DARK);
        fieldsPanel.add(confirmLabel);
        
        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        confirmPasswordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        fieldsPanel.add(confirmPasswordField);

        // Row 3: Email
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        emailLabel.setForeground(PRIMARY_DARK);
        fieldsPanel.add(emailLabel);
        
        emailField = new JTextField();
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        fieldsPanel.add(emailField);

        // Row 4: Role
        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        roleLabel.setForeground(PRIMARY_DARK);
        fieldsPanel.add(roleLabel);
        
        roleCombo = new JComboBox<>(new String[]{"RECEPTION", "DENTIST", "PATIENT"});
        roleCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        roleCombo.setBackground(Color.WHITE);
        roleCombo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        roleCombo.addActionListener(e -> updateProfilePanel());
        fieldsPanel.add(roleCombo);

        panel.add(fieldsPanel);
        return panel;
    }

    private void updateProfilePanel() {
        String role = (String) roleCombo.getSelectedItem();
        profilePanel.removeAll();
        
        JPanel panel = null;
        switch (role) {
            case "RECEPTION":
                panel = createStaffProfilePanel();
                selectedRole = UserRole.RECEPTION;
                break;
            case "DENTIST":
                panel = createDentistProfilePanel();
                selectedRole = UserRole.DENTIST;
                break;
            case "PATIENT":
                panel = createPatientProfilePanel();
                selectedRole = UserRole.PATIENT;
                break;
            default:
                panel = createStaffProfilePanel();
                selectedRole = UserRole.RECEPTION;
                break;
        }
        
        if (panel != null) {
            profilePanel.add(panel, BorderLayout.CENTER);
        }
        profilePanel.revalidate();
        profilePanel.repaint();
        pack();
    }

    // =====================================================
    // STAFF PROFILE PANEL
    // =====================================================

    private JPanel createStaffProfilePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 15, 10, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.weightx = 1.0;

        // Row 0: First Name, Last Name
        addField(panel, gbc, "First Name:", staffFirstNameField = createTextField(), 0, 0);
        addField(panel, gbc, "Last Name:", staffLastNameField = createTextField(), 0, 1);

        // Row 1: Position, Department
        addField(panel, gbc, "Position:", staffPositionField = createTextField(), 1, 0);
        addField(panel, gbc, "Department:", staffDepartmentField = createTextField(), 1, 1);

        // Row 2: Phone, Email
        addField(panel, gbc, "Phone:", staffPhoneField = createTextField(), 2, 0);
        addField(panel, gbc, "Email:", staffEmailField = createTextField(), 2, 1);

        // Row 3: Hire Date, Salary
        addField(panel, gbc, "Hire Date (YYYY-MM-DD):", staffHireDateField = createTextField(LocalDate.now().toString()), 3, 0);
        addField(panel, gbc, "Salary ($):", staffSalaryField = createTextField(), 3, 1);

        return panel;
    }

    // =====================================================
    // DENTIST PROFILE PANEL
    // =====================================================

    private JPanel createDentistProfilePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 15, 10, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.weightx = 1.0;

        // Row 0: Dentist Name (span 2 columns)
        addField(panel, gbc, "Dentist Name:", dentistNameField = createTextField(), 0, 0, 2);

        // Row 1: Specialization, License Number
        addField(panel, gbc, "Specialization:", dentistSpecializationField = createTextField(), 1, 0);
        addField(panel, gbc, "License Number:", dentistLicenseField = createTextField(), 1, 1);

        // Row 2: Working Hours (span 2 columns)
        addField(panel, gbc, "Working Hours:", dentistWorkingHoursField = createTextField(), 2, 0, 2);

        // Row 3: Phone, Email
        addField(panel, gbc, "Phone:", dentistPhoneField = createTextField(), 3, 0);
        addField(panel, gbc, "Email:", dentistEmailField = createTextField(), 3, 1);

        // Row 4: Experience, Consultation Fee
        addField(panel, gbc, "Years of Experience:", dentistExperienceField = createTextField(), 4, 0);
        addField(panel, gbc, "Consultation Fee ($):", dentistFeeField = createTextField(), 4, 1);

        return panel;
    }

    // =====================================================
    // PATIENT PROFILE PANEL
    // =====================================================

    private JPanel createPatientProfilePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 15, 10, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.weightx = 1.0;

        // Row 0: Patient Name (span 2 columns)
        addField(panel, gbc, "Patient Name:", patientNameField = createTextField(), 0, 0, 2);

        // Row 1: Gender, DOB
        JLabel genderLabel = new JLabel("Gender:");
        genderLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        genderLabel.setForeground(PRIMARY_DARK);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(genderLabel, gbc);

        patientGenderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        patientGenderCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        patientGenderCombo.setBackground(Color.WHITE);
        patientGenderCombo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        gbc.gridx = 1;
        panel.add(patientGenderCombo, gbc);

        addField(panel, gbc, "Date of Birth (YYYY-MM-DD):", patientDobField = createTextField(), 1, 1);

        // Row 2: Phone, Email
        addField(panel, gbc, "Phone:", patientPhoneField = createTextField(), 2, 0);
        addField(panel, gbc, "Email:", patientEmailField = createTextField(), 2, 1);

        // Row 3: Address (span 2 columns)
        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        addressLabel.setForeground(PRIMARY_DARK);
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(addressLabel, gbc);

        patientAddressArea = new JTextArea();
        patientAddressArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        patientAddressArea.setLineWrap(true);
        patientAddressArea.setWrapStyleWord(true);
        patientAddressArea.setBorder(BorderFactory.createLineBorder(LIGHT_SURFACE, 1));
        patientAddressArea.setPreferredSize(new Dimension(300, 50));
        JScrollPane addressScroll = new JScrollPane(patientAddressArea);
        addressScroll.setBorder(BorderFactory.createEmptyBorder());
        gbc.gridx = 1;
        gbc.gridwidth = 1;
        panel.add(addressScroll, gbc);

        // Row 4: Emergency Contact, Emergency Phone
        addField(panel, gbc, "Emergency Contact:", patientEmergencyContactField = createTextField(), 4, 0);
        addField(panel, gbc, "Emergency Phone:", patientEmergencyPhoneField = createTextField(), 4, 1);

        // Row 5: Medical History (span 2 columns)
        JLabel historyLabel = new JLabel("Medical History:");
        historyLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        historyLabel.setForeground(PRIMARY_DARK);
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(historyLabel, gbc);

        patientMedicalHistoryArea = new JTextArea();
        patientMedicalHistoryArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        patientMedicalHistoryArea.setLineWrap(true);
        patientMedicalHistoryArea.setWrapStyleWord(true);
        patientMedicalHistoryArea.setBorder(BorderFactory.createLineBorder(LIGHT_SURFACE, 1));
        patientMedicalHistoryArea.setPreferredSize(new Dimension(300, 50));
        JScrollPane historyScroll = new JScrollPane(patientMedicalHistoryArea);
        historyScroll.setBorder(BorderFactory.createEmptyBorder());
        gbc.gridx = 1;
        panel.add(historyScroll, gbc);

        // Row 6: Allergies (span 2 columns)
        JLabel allergiesLabel = new JLabel("Allergies:");
        allergiesLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        allergiesLabel.setForeground(PRIMARY_DARK);
        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(allergiesLabel, gbc);

        patientAllergiesArea = new JTextArea();
        patientAllergiesArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        patientAllergiesArea.setLineWrap(true);
        patientAllergiesArea.setWrapStyleWord(true);
        patientAllergiesArea.setBorder(BorderFactory.createLineBorder(LIGHT_SURFACE, 1));
        patientAllergiesArea.setPreferredSize(new Dimension(300, 50));
        JScrollPane allergiesScroll = new JScrollPane(patientAllergiesArea);
        allergiesScroll.setBorder(BorderFactory.createEmptyBorder());
        gbc.gridx = 1;
        panel.add(allergiesScroll, gbc);

        return panel;
    }

    // ========================
    // Helper Methods
    // ========================

    private JTextField createTextField() {
        return createTextField("");
    }

    private JTextField createTextField(String defaultValue) {
        JTextField field = new JTextField(defaultValue);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        field.setPreferredSize(new Dimension(200, 30));
        return field;
    }

    private void addField(JPanel panel, GridBagConstraints gbc, String labelText, JTextField field, int row, int col) {
        addField(panel, gbc, labelText, field, row, col, 1);
    }

    private void addField(JPanel panel, GridBagConstraints gbc, String labelText, JTextField field, int row, int col, int width) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(PRIMARY_DARK);
        gbc.gridx = col;
        gbc.gridy = row;
        gbc.gridwidth = width == 2 ? 2 : 1;
        panel.add(label, gbc);

        if (width == 2) {
            gbc.gridx = col + 1;
            gbc.gridwidth = 1;
        }
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        field.setPreferredSize(new Dimension(200, 30));
        panel.add(field, gbc);
    }

    private RoundedButton createButton(String text, Color bg, Color fg) {
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
    // Create User Logic - FIXED
    // ========================

    private void createUser() {
        // Validate user credentials
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String email = emailField.getText().trim();
        String role = (String) roleCombo.getSelectedItem();

        // Validate username
        if (username.isEmpty()) {
            showError("Username is required.");
            return;
        }

        if (username.length() < 3) {
            showError("Username must be at least 3 characters.");
            return;
        }

        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            showError("Username can only contain letters, numbers, and underscores.");
            return;
        }

        if (controller.usernameExists(username)) {
            showError("Username already exists. Please choose another.");
            return;
        }

        // Validate password
        if (password.isEmpty()) {
            showError("Password is required.");
            return;
        }

        if (password.length() < 6) {
            showError("Password must be at least 6 characters.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match.");
            return;
        }

        // Validate email
        if (email.isEmpty()) {
            showError("Email is required.");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showError("Please enter a valid email address.");
            return;
        }

        if (controller.emailExists(email)) {
            showError("Email already registered. Please use another.");
            return;
        }

        // Collect profile data based on role
        Map<String, Object> profileData = new HashMap<>();
        UserRole userRole = UserRole.valueOf(role);
        String validationError = null;

        switch (userRole) {
            case RECEPTION:
                validationError = validateStaffProfile(profileData);
                break;
            case DENTIST:
                validationError = validateDentistProfile(profileData);
                break;
            case PATIENT:
                validationError = validatePatientProfile(profileData);
                break;
            default:
                validationError = "Invalid role selected.";
                break;
        }

        if (validationError != null) {
            showError(validationError);
            return;
        }

        // FIXED: Get the current user ID as creator
        createdBy = 0;
        User currentUser = LoginSession.getInstance().getCurrentUser();
        if (currentUser != null) {
            createdBy = currentUser.getUserId();
        }

        showInfo("Creating user account... Please wait.");
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        saveButton.setEnabled(false);

        // FIXED: Make createdBy final or effectively final
        final int creatorId = createdBy;
        
        SwingWorker<User, Void> worker = new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() throws Exception {
                // Use the final variable creatorId instead of createdBy
                return controller.createUserWithProfile(username, password, email, userRole, creatorId, profileData);
            }

            @Override
            protected void done() {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                saveButton.setEnabled(true);
                try {
                    User user = get();
                    if (user != null) {
                        showSuccess("User created successfully! Username: " + username);
                        statusLabel.setForeground(SUCCESS_COLOR);
                        
                        // Close dialog after delay
                        Timer timer = new Timer(1500, e -> dispose());
                        timer.setRepeats(false);
                        timer.start();
                    } else {
                        showError("Failed to create user. Please try again.");
                    }
                } catch (Exception e) {
                    showError("Error creating user: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private String validateStaffProfile(Map<String, Object> data) {
        String firstName = staffFirstNameField.getText().trim();
        String lastName = staffLastNameField.getText().trim();
        String phone = staffPhoneField.getText().trim();
        String salary = staffSalaryField.getText().trim();
        String hireDate = staffHireDateField.getText().trim();

        if (firstName.isEmpty() || lastName.isEmpty()) {
            return "First Name and Last Name are required.";
        }

        if (!firstName.matches("^[a-zA-Z\\s]+$") || !lastName.matches("^[a-zA-Z\\s]+$")) {
            return "Name can only contain letters and spaces.";
        }

        if (phone.isEmpty()) {
            return "Phone number is required.";
        }

        String phoneDigits = phone.replaceAll("[^0-9]", "");
        if (phoneDigits.length() < 10) {
            return "Please enter a valid phone number (at least 10 digits).";
        }

        if (salary.isEmpty()) {
            return "Salary is required.";
        }

        try {
            Double.parseDouble(salary);
        } catch (NumberFormatException e) {
            return "Please enter a valid salary amount.";
        }

        // Parse hire date
        try {
            LocalDate.parse(hireDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (Exception e) {
            return "Invalid hire date format. Please use YYYY-MM-DD.";
        }

        data.put("firstName", firstName);
        data.put("lastName", lastName);
        data.put("position", staffPositionField.getText().trim());
        data.put("department", staffDepartmentField.getText().trim());
        data.put("phone", phone);
        data.put("email", staffEmailField.getText().trim());
        data.put("hireDate", Date.valueOf(LocalDate.parse(hireDate)));
        data.put("salary", Double.parseDouble(salary));

        return null;
    }

    private String validateDentistProfile(Map<String, Object> data) {
        String dentistName = dentistNameField.getText().trim();
        String phone = dentistPhoneField.getText().trim();
        String experience = dentistExperienceField.getText().trim();
        String fee = dentistFeeField.getText().trim();

        if (dentistName.isEmpty()) {
            return "Dentist Name is required.";
        }

        if (phone.isEmpty()) {
            return "Phone number is required.";
        }

        String phoneDigits = phone.replaceAll("[^0-9]", "");
        if (phoneDigits.length() < 10) {
            return "Please enter a valid phone number (at least 10 digits).";
        }

        if (experience.isEmpty()) {
            return "Years of experience is required.";
        }

        try {
            int exp = Integer.parseInt(experience);
            if (exp < 0) {
                return "Years of experience cannot be negative.";
            }
        } catch (NumberFormatException e) {
            return "Please enter a valid number for years of experience.";
        }

        if (fee.isEmpty()) {
            return "Consultation fee is required.";
        }

        try {
            double consultationFee = Double.parseDouble(fee);
            if (consultationFee < 0) {
                return "Consultation fee cannot be negative.";
            }
        } catch (NumberFormatException e) {
            return "Please enter a valid number for consultation fee.";
        }

        data.put("dentistName", dentistName);
        data.put("specialization", dentistSpecializationField.getText().trim());
        data.put("licenseNumber", dentistLicenseField.getText().trim());
        data.put("workingHours", dentistWorkingHoursField.getText().trim());
        data.put("phone", phone);
        data.put("email", dentistEmailField.getText().trim());
        data.put("yearsOfExperience", Integer.parseInt(experience));
        data.put("consultationFee", Double.parseDouble(fee));

        return null;
    }

    private String validatePatientProfile(Map<String, Object> data) {
        String patientName = patientNameField.getText().trim();
        String phone = patientPhoneField.getText().trim();
        String dob = patientDobField.getText().trim();
        String emergencyContact = patientEmergencyContactField.getText().trim();
        String emergencyPhone = patientEmergencyPhoneField.getText().trim();

        if (patientName.isEmpty()) {
            return "Patient Name is required.";
        }

        if (phone.isEmpty()) {
            return "Phone number is required.";
        }

        String phoneDigits = phone.replaceAll("[^0-9]", "");
        if (phoneDigits.length() < 10) {
            return "Please enter a valid phone number (at least 10 digits).";
        }

        if (dob.isEmpty()) {
            return "Date of Birth is required.";
        }

        try {
            LocalDate dobDate = LocalDate.parse(dob, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            if (dobDate.isAfter(LocalDate.now())) {
                return "Date of Birth cannot be in the future.";
            }
        } catch (Exception e) {
            return "Invalid date format. Please use YYYY-MM-DD.";
        }

        if (emergencyContact.isEmpty()) {
            return "Emergency contact name is required.";
        }

        if (emergencyPhone.isEmpty()) {
            return "Emergency phone number is required.";
        }

        String emergencyDigits = emergencyPhone.replaceAll("[^0-9]", "");
        if (emergencyDigits.length() < 10) {
            return "Please enter a valid emergency phone number (at least 10 digits).";
        }

        data.put("patientName", patientName);
        data.put("gender", (String) patientGenderCombo.getSelectedItem());
        data.put("address", patientAddressArea.getText().trim());
        data.put("contactNumber", phone);
        data.put("email", patientEmailField.getText().trim());
        data.put("dateOfBirth", Date.valueOf(LocalDate.parse(dob)));
        data.put("emergencyContact", emergencyContact);
        data.put("emergencyPhone", emergencyPhone);
        data.put("medicalHistory", patientMedicalHistoryArea.getText().trim());
        data.put("allergies", patientAllergiesArea.getText().trim());

        return null;
    }

    private void showError(String message) {
        statusLabel.setText("❌ " + message);
        statusLabel.setForeground(ERROR_COLOR);
    }

    private void showSuccess(String message) {
        statusLabel.setText("✅ " + message);
        statusLabel.setForeground(SUCCESS_COLOR);
    }

    private void showInfo(String message) {
        statusLabel.setText("ℹ️ " + message);
        statusLabel.setForeground(new Color(0, 120, 215));
    }
}