package view;

import controller.UserProfileController;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ChangePasswordDialog extends JDialog {
    
    private static final Color PRIMARY_DARK = new Color(0x2F3E3C);
    private static final Color LIGHT_SURFACE = new Color(0xE7E9E3);
    private static final Color ERROR_COLOR = new Color(220, 80, 80);
    private static final Color SUCCESS_COLOR = new Color(60, 160, 80);
    private static final Color SECONDARY_TEXT = new Color(122, 138, 135);

    private User currentUser;
    private UserProfileController controller;
    
    private JPasswordField currentPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JLabel passwordStrengthLabel;
    private JLabel statusLabel;
    
    private JButton changeButton;
    private JButton cancelButton;

    public ChangePasswordDialog(JFrame parent, User user, UserProfileController controller) {
        super(parent, "Change Password", true);
        this.currentUser = user;
        this.controller = controller;
        
        initComponents();
        setSize(500, 400);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);
        setModal(true);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(30, 30, 20, 30));

        // Header
        JLabel titleLabel = new JLabel("Change Password");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_DARK);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        JLabel subtitleLabel = new JLabel("Update your login password");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(SECONDARY_TEXT);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(subtitleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Password fields
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBackground(Color.WHITE);
        fieldsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.weightx = 1.0;

        // Row 0: Current Password
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.25;
        JLabel currentLabel = new JLabel("Current Password:");
        currentLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        currentLabel.setForeground(PRIMARY_DARK);
        fieldsPanel.add(currentLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 0.75;
        currentPasswordField = new JPasswordField();
        currentPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        currentPasswordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        fieldsPanel.add(currentPasswordField, gbc);

        // Row 1: New Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.25;
        JLabel newLabel = new JLabel("New Password:");
        newLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        newLabel.setForeground(PRIMARY_DARK);
        fieldsPanel.add(newLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 0.75;
        newPasswordField = new JPasswordField();
        newPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        newPasswordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        newPasswordField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                checkPasswordStrength();
            }
        });
        fieldsPanel.add(newPasswordField, gbc);

        // Row 2: Confirm Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.25;
        JLabel confirmLabel = new JLabel("Confirm Password:");
        confirmLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        confirmLabel.setForeground(PRIMARY_DARK);
        fieldsPanel.add(confirmLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 0.75;
        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        confirmPasswordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        fieldsPanel.add(confirmPasswordField, gbc);

        // Row 3: Password Strength
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        gbc.weightx = 0.75;
        passwordStrengthLabel = new JLabel(" ");
        passwordStrengthLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        passwordStrengthLabel.setForeground(SECONDARY_TEXT);
        fieldsPanel.add(passwordStrengthLabel, gbc);

        mainPanel.add(fieldsPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(Color.WHITE);

        changeButton = new JButton("Change Password");
        changeButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        changeButton.setBackground(PRIMARY_DARK);
        changeButton.setForeground(Color.WHITE);
        changeButton.setFocusPainted(false);
        changeButton.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        changeButton.addActionListener(e -> changePassword());

        cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cancelButton.setBackground(LIGHT_SURFACE);
        cancelButton.setForeground(PRIMARY_DARK);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(changeButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel);

        // Status
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBackground(Color.WHITE);
        statusPanel.setBorder(new EmptyBorder(0, 10, 5, 10));
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusPanel.add(statusLabel);
        mainPanel.add(statusPanel);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void checkPasswordStrength() {
        String password = new String(newPasswordField.getPassword());
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
            passwordStrengthLabel.setText("🔴 Weak");
            passwordStrengthLabel.setForeground(ERROR_COLOR);
        } else if (strength <= 4) {
            passwordStrengthLabel.setText("🟡 Medium");
            passwordStrengthLabel.setForeground(new Color(200, 180, 0));
        } else {
            passwordStrengthLabel.setText("🟢 Strong");
            passwordStrengthLabel.setForeground(SUCCESS_COLOR);
        }
    }

    private void changePassword() {
        String currentPassword = new String(currentPasswordField.getPassword());
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        // Validate current password
        if (currentPassword.isEmpty()) {
            showError("Please enter your current password.");
            return;
        }

        if (!controller.verifyCurrentPassword(currentUser.getUserId(), currentPassword)) {
            showError("Current password is incorrect.");
            return;
        }

        // Validate new password
        if (newPassword.isEmpty()) {
            showError("Please enter a new password.");
            return;
        }
        if (newPassword.length() < 6) {
            showError("Password must be at least 6 characters.");
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            showError("Passwords do not match.");
            return;
        }

        // Change password
        boolean success = controller.changePassword(currentUser.getUserId(), newPassword);
        
        if (success) {
            showSuccess("Password changed successfully!");
            
            // Close dialog after 1 second
            Timer timer = new Timer(1000, e -> dispose());
            timer.setRepeats(false);
            timer.start();
        } else {
            showError("Failed to change password. Please try again.");
        }
    }

    private void showError(String message) {
        statusLabel.setText("❌ " + message);
        statusLabel.setForeground(ERROR_COLOR);
    }

    private void showSuccess(String message) {
        statusLabel.setText("✅ " + message);
        statusLabel.setForeground(SUCCESS_COLOR);
    }
}