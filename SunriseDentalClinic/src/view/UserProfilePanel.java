package view;

import controller.UserProfileController;
import model.User;
import model.User.UserRole;
import model.LoginSession;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class UserProfilePanel extends JPanel {
    
    // Color Palette
    private static final Color PRIMARY_DARK = new Color(0x2F3E3C);
    private static final Color MINT = new Color(0xBDDBD1);
    private static final Color SOFT_SURFACE = new Color(0xFBF9F1);
    private static final Color LIGHT_SURFACE = new Color(0xE7E9E3);
    private static final Color ERROR_COLOR = new Color(220, 80, 80);
    private static final Color SUCCESS_COLOR = new Color(60, 160, 80);
    private static final Color SECONDARY_TEXT = new Color(122, 138, 135);

    // Components
    private JLabel usernameLabel;
    private JLabel emailLabel;
    private JLabel roleLabel;
    private JLabel statusLabel;
    private JLabel createdDateLabel;
    private JLabel lastLoginLabel;
    
    // Profile details for each role
    private JPanel profileDetailsPanel;
    
    // Buttons
    private RoundedButton editProfileButton;
    private RoundedButton changePasswordButton;
    private RoundedButton refreshButton;
    
    private JLabel statusMessageLabel;
    private UserProfileController controller;
    private User currentUser;

    public UserProfilePanel() {
        initComponents();
        this.controller = new UserProfileController(this);
        loadUserProfile();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(SOFT_SURFACE);
        setBorder(new EmptyBorder(20, 30, 20, 30));

        // Header Panel
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Main Content Panel
        add(createMainContentPanel(), BorderLayout.CENTER);
        
        // Footer Panel
        add(createFooterPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(SOFT_SURFACE);
        header.setBorder(new EmptyBorder(0, 0, 15, 0));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("My Profile");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_DARK);
        
        JLabel subtitleLabel = new JLabel("View and manage your account information");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(SECONDARY_TEXT);
        
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);

        // Refresh button in header
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        
        refreshButton = createStyledButton("↻ Refresh", SOFT_SURFACE, PRIMARY_DARK);
        refreshButton.setBorderColor(LIGHT_SURFACE);
        refreshButton.setPreferredSize(new Dimension(110, 35));
        refreshButton.addActionListener(e -> loadUserProfile());
        rightPanel.add(refreshButton);

        header.add(titlePanel, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);

        return header;
    }

    private JPanel createMainContentPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            new EmptyBorder(20, 30, 20, 30)
        ));

        // User Info Section
        mainPanel.add(createUserInfoSection());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Profile Details Section (role-specific)
        mainPanel.add(createProfileDetailsSection());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Action Buttons
        mainPanel.add(createActionPanel());

        return mainPanel;
    }

    private JPanel createUserInfoSection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(MINT, 1),
            "Account Information",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            PRIMARY_DARK
        ));

        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.weightx = 1.0;

        // Row 0: Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.15;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        userLabel.setForeground(PRIMARY_DARK);
        infoPanel.add(userLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.35;
        usernameLabel = new JLabel("--");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        infoPanel.add(usernameLabel, gbc);

        // Row 0: Role
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.15;
        JLabel roleLabelTitle = new JLabel("Role:");
        roleLabelTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        roleLabelTitle.setForeground(PRIMARY_DARK);
        infoPanel.add(roleLabelTitle, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.35;
        roleLabel = new JLabel("--");
        roleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        roleLabel.setForeground(PRIMARY_DARK);
        infoPanel.add(roleLabel, gbc);

        // Row 1: Email
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.15;
        JLabel emailLabelTitle = new JLabel("Email:");
        emailLabelTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        emailLabelTitle.setForeground(PRIMARY_DARK);
        infoPanel.add(emailLabelTitle, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.35;
        emailLabel = new JLabel("--");
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        infoPanel.add(emailLabel, gbc);

        // Row 1: Status
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.15;
        JLabel statusLabelTitle = new JLabel("Status:");
        statusLabelTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        statusLabelTitle.setForeground(PRIMARY_DARK);
        infoPanel.add(statusLabelTitle, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.35;
        statusLabel = new JLabel("--");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        infoPanel.add(statusLabel, gbc);

        // Row 2: Created Date
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.15;
        JLabel createdLabelTitle = new JLabel("Created:");
        createdLabelTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        createdLabelTitle.setForeground(PRIMARY_DARK);
        infoPanel.add(createdLabelTitle, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 0.85;
        createdDateLabel = new JLabel("--");
        createdDateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        infoPanel.add(createdDateLabel, gbc);

        // Row 3: Last Login
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.15;
        JLabel lastLoginTitle = new JLabel("Last Login:");
        lastLoginTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lastLoginTitle.setForeground(PRIMARY_DARK);
        infoPanel.add(lastLoginTitle, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 0.85;
        lastLoginLabel = new JLabel("--");
        lastLoginLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        infoPanel.add(lastLoginLabel, gbc);

        panel.add(infoPanel);
        return panel;
    }

    private JPanel createProfileDetailsSection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(MINT, 1),
            "Profile Details",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            PRIMARY_DARK
        ));

        profileDetailsPanel = new JPanel();
        profileDetailsPanel.setBackground(Color.WHITE);
        profileDetailsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        profileDetailsPanel.setLayout(new BoxLayout(profileDetailsPanel, BoxLayout.Y_AXIS));

        // Initially show loading message
        JLabel loadingLabel = new JLabel("Loading profile details...");
        loadingLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        loadingLabel.setForeground(SECONDARY_TEXT);
        profileDetailsPanel.add(loadingLabel);

        panel.add(profileDetailsPanel);
        return panel;
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(MINT, 1),
            "Account Actions",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            PRIMARY_DARK
        ));

        editProfileButton = createStyledButton("✏️ Edit Profile", PRIMARY_DARK, Color.WHITE);
        editProfileButton.setPreferredSize(new Dimension(160, 40));
        editProfileButton.addActionListener(e -> openEditProfile());

        changePasswordButton = createStyledButton("🔒 Change Password", MINT, PRIMARY_DARK);
        changePasswordButton.setPreferredSize(new Dimension(180, 40));
        changePasswordButton.addActionListener(e -> openChangePassword());

        panel.add(editProfileButton);
        panel.add(changePasswordButton);

        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(SOFT_SURFACE);
        footer.setBorder(new EmptyBorder(15, 0, 0, 0));

        statusMessageLabel = new JLabel(" ");
        statusMessageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusMessageLabel.setForeground(SECONDARY_TEXT);

        footer.add(statusMessageLabel, BorderLayout.WEST);
        return footer;
    }

    private RoundedButton createStyledButton(String text, Color bg, Color fg) {
        RoundedButton button = new RoundedButton(text, bg, fg);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
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
            } else if (bg.equals(MINT)) {
                hoverColor = new Color(150, 200, 180);
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

    // =====================================================
    // PUBLIC METHODS FOR CONTROLLER
    // =====================================================

    /**
     * ✅ FIXED: Load user profile - refreshes from database
     */
    public void loadUserProfile() {
        // ✅ Get current user ID from session
        int userId = LoginSession.getInstance().getCurrentUserId();
        
        if (userId <= 0) {
            showError("No user logged in.");
            return;
        }

        // ✅ Load fresh user data from database
        this.currentUser = controller.getUserById(userId);
        
        if (currentUser == null) {
            showError("User not found in database.");
            return;
        }

        // ✅ Update the session with fresh user data
        LoginSession.getInstance().setCurrentUser(currentUser);

        // Update account info
        usernameLabel.setText(currentUser.getUsername());
        emailLabel.setText(currentUser.getEmail() != null ? currentUser.getEmail() : "N/A");
        roleLabel.setText(currentUser.getRole().name());
        statusLabel.setText(currentUser.isActive() ? "🟢 Active" : "🔴 Inactive");
        statusLabel.setForeground(currentUser.isActive() ? SUCCESS_COLOR : ERROR_COLOR);
        createdDateLabel.setText(currentUser.getCreatedAt() != null ? currentUser.getCreatedAt() : "N/A");
        
        // Load role-specific profile
        loadRoleProfile(currentUser);

        showSuccess("Profile loaded successfully!");
    }

    private void loadRoleProfile(User user) {
        profileDetailsPanel.removeAll();
        profileDetailsPanel.setLayout(new BoxLayout(profileDetailsPanel, BoxLayout.Y_AXIS));

        switch (user.getRole()) {
            case ADMIN:
                loadAdminProfile(user);
                break;
            case PATIENT:
                loadPatientProfile(user);
                break;
            case RECEPTION:
                loadStaffProfile(user);
                break;
            case DENTIST:
                loadDentistProfile(user);
                break;
            default:
                JLabel label = new JLabel("No profile details available for this role.");
                label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                label.setForeground(SECONDARY_TEXT);
                profileDetailsPanel.add(label);
                break;
        }

        profileDetailsPanel.revalidate();
        profileDetailsPanel.repaint();
    }

    private void loadAdminProfile(User user) {
        addProfileField("Role", "System Administrator");
        addProfileField("Permissions", "Full access to all system features");
        addProfileField("User ID", String.valueOf(user.getUserId()));
    }

    private void loadPatientProfile(User user) {
        controller.loadPatientProfile(user.getUserId(), data -> {
            if (data != null) {
                addProfileField("Patient ID", String.valueOf(data.get("patientId")));
                addProfileField("Full Name", (String) data.get("patientName"));
                addProfileField("Gender", (String) data.get("gender"));
                addProfileField("Contact Number", (String) data.get("contactNumber"));
                addProfileField("Email", (String) data.get("email"));
                addProfileField("Address", (String) data.get("address"));
                addProfileField("Date of Birth", (String) data.get("dateOfBirth"));
                addProfileField("Medical History", (String) data.get("medicalHistory"));
                addProfileField("Allergies", (String) data.get("allergies"));
            } else {
                addProfileField("Status", "No patient profile found. Please contact admin.");
            }
        });
    }

    private void loadStaffProfile(User user) {
        controller.loadStaffProfile(user.getUserId(), data -> {
            if (data != null) {
                addProfileField("Staff ID", String.valueOf(data.get("staffId")));
                addProfileField("First Name", (String) data.get("firstName"));
                addProfileField("Last Name", (String) data.get("lastName"));
                addProfileField("Position", (String) data.get("position"));
                addProfileField("Department", (String) data.get("department"));
                addProfileField("Phone", (String) data.get("phone"));
                addProfileField("Email", (String) data.get("email"));
                addProfileField("Hire Date", (String) data.get("hireDate"));
                addProfileField("Salary", "$" + data.get("salary"));
            } else {
                addProfileField("Status", "No staff profile found. Please contact admin.");
            }
        });
    }

    private void loadDentistProfile(User user) {
        controller.loadDentistProfile(user.getUserId(), data -> {
            if (data != null) {
                addProfileField("Dentist ID", String.valueOf(data.get("dentistId")));
                addProfileField("Dentist Name", (String) data.get("dentistName"));
                addProfileField("Specialization", (String) data.get("specialization"));
                addProfileField("License Number", (String) data.get("licenseNumber"));
                addProfileField("Working Hours", (String) data.get("workingHours"));
                addProfileField("Phone", (String) data.get("phone"));
                addProfileField("Email", (String) data.get("email"));
                addProfileField("Years of Experience", String.valueOf(data.get("yearsOfExperience")));
                addProfileField("Consultation Fee", "$" + data.get("consultationFee"));
                addProfileField("Available", (Boolean) data.get("isAvailable") ? "✅ Yes" : "❌ No");
            } else {
                addProfileField("Status", "No dentist profile found. Please contact admin.");
            }
        });
    }

    private void addProfileField(String label, String value) {
        JPanel fieldPanel = new JPanel(new BorderLayout());
        fieldPanel.setBackground(Color.WHITE);
        fieldPanel.setBorder(new EmptyBorder(3, 5, 3, 5));
        fieldPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel labelComp = new JLabel(label + ":");
        labelComp.setFont(new Font("Segoe UI", Font.BOLD, 13));
        labelComp.setForeground(PRIMARY_DARK);
        labelComp.setPreferredSize(new Dimension(160, 25));

        JLabel valueComp = new JLabel(value != null && !value.isEmpty() ? value : "N/A");
        valueComp.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        valueComp.setForeground(Color.BLACK);

        fieldPanel.add(labelComp, BorderLayout.WEST);
        fieldPanel.add(valueComp, BorderLayout.CENTER);

        profileDetailsPanel.add(fieldPanel);
    }

    private void openEditProfile() {
        if (currentUser == null) {
            showError("No user logged in.");
            return;
        }
        
        EditProfileDialog dialog = new EditProfileDialog(
            (JFrame) SwingUtilities.getWindowAncestor(this),
            currentUser,
            controller
        );
        dialog.setVisible(true);
        
        // ✅ Reload profile after editing
        loadUserProfile();
    }

    private void openChangePassword() {
        if (currentUser == null) {
            showError("No user logged in.");
            return;
        }
        
        ChangePasswordDialog dialog = new ChangePasswordDialog(
            (JFrame) SwingUtilities.getWindowAncestor(this),
            currentUser,
            controller
        );
        dialog.setVisible(true);
        
        // ✅ Reload profile after password change
        loadUserProfile();
    }

    public void showError(String message) {
        statusMessageLabel.setText("❌ " + message);
        statusMessageLabel.setForeground(ERROR_COLOR);
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showSuccess(String message) {
        statusMessageLabel.setText("✅ " + message);
        statusMessageLabel.setForeground(SUCCESS_COLOR);
    }

    public void showInfo(String message) {
        statusMessageLabel.setText("ℹ️ " + message);
        statusMessageLabel.setForeground(new Color(0, 120, 215));
    }

    private static class MouseAdapter extends java.awt.event.MouseAdapter {
        // Empty implementation
    }
}