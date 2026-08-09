package view;

import controller.StaffController;
import model.Staff;
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

public class StaffDetailsPanel extends JPanel {
    
    // Color Palette
    private static final Color PRIMARY_DARK = new Color(0x2F3E3C);
    private static final Color MINT = new Color(0xBDDBD1);
    private static final Color SOFT_SURFACE = new Color(0xFBF9F1);
    private static final Color LIGHT_SURFACE = new Color(0xE7E9E3);
    private static final Color ERROR_COLOR = new Color(220, 80, 80);
    private static final Color SUCCESS_COLOR = new Color(60, 160, 80);
    private static final Color SECONDARY_TEXT = new Color(122, 138, 135);
    private static final Color ACTIVE_COLOR = new Color(60, 160, 80);
    private static final Color INACTIVE_COLOR = new Color(200, 80, 80);

    // Form Fields - View/Edit Mode
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField positionField;
    private JTextField departmentField;
    private JTextField phoneField;
    private JTextField emailField;
    private JTextField hireDateField;
    private JTextField salaryField;
    private JCheckBox activeCheckBox;
    private JLabel staffIdLabel;
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
    private RoundedButton toggleStatusButton;
    
    private JPanel buttonPanel;
    private boolean isEditMode = false;
    private Staff currentStaff;
    private StaffController controller;

    public StaffDetailsPanel() {
        this.controller = new StaffController(this);
        initComponents();
        setViewMode(false);
        displayEmptyState();
    }

    public StaffDetailsPanel(Staff staff) {
        this.controller = new StaffController(this);
        this.currentStaff = staff;
        initComponents();
        setViewMode(false);
        displayStaff(staff);
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

        // Title and staff info
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Staff Details");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_DARK);
        
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        infoPanel.setOpaque(false);
        
        staffIdLabel = new JLabel("Staff ID: --");
        staffIdLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        staffIdLabel.setForeground(SECONDARY_TEXT);
        
        createdDateLabel = new JLabel("Created: --");
        createdDateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        createdDateLabel.setForeground(SECONDARY_TEXT);
        
        updatedDateLabel = new JLabel("Last Updated: --");
        updatedDateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        updatedDateLabel.setForeground(SECONDARY_TEXT);
        
        // Status badge
        statusBadge = new JLabel("--");
        statusBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusBadge.setOpaque(true);
        statusBadge.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        statusBadge.setVisible(false);
        
        infoPanel.add(staffIdLabel);
        infoPanel.add(createdDateLabel);
        infoPanel.add(updatedDateLabel);
        infoPanel.add(statusBadge);
        
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        titlePanel.add(infoPanel);

        header.add(titlePanel, BorderLayout.WEST);
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

        // Personal Information Section
        mainPanel.add(createSectionPanel("Personal Information", createPersonalInfoPanel()));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Employment Information Section
        mainPanel.add(createSectionPanel("Employment Information", createEmploymentInfoPanel()));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Contact Information Section
        mainPanel.add(createSectionPanel("Contact Information", createContactInfoPanel()));

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
            new Font("Segoe UI", Font.BOLD, 14),
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
        firstNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        firstNameLabel.setForeground(PRIMARY_DARK);
        panel.add(firstNameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        firstNameField = createTextField();
        firstNameField.setEnabled(false);
        panel.add(firstNameField, gbc);

        // Last Name
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel lastNameLabel = new JLabel("Last Name:");
        lastNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lastNameLabel.setForeground(PRIMARY_DARK);
        panel.add(lastNameLabel, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        lastNameField = createTextField();
        lastNameField.setEnabled(false);
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
        positionLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        positionLabel.setForeground(PRIMARY_DARK);
        panel.add(positionLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        positionField = createTextField();
        positionField.setEnabled(false);
        panel.add(positionField, gbc);

        // Department
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel departmentLabel = new JLabel("Department:");
        departmentLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        departmentLabel.setForeground(PRIMARY_DARK);
        panel.add(departmentLabel, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        departmentField = createTextField();
        departmentField.setEnabled(false);
        panel.add(departmentField, gbc);

        // Hire Date
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel hireDateLabel = new JLabel("Hire Date:");
        hireDateLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        hireDateLabel.setForeground(PRIMARY_DARK);
        panel.add(hireDateLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        hireDateField = createTextField();
        hireDateField.setEnabled(false);
        panel.add(hireDateField, gbc);

        // Salary
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel salaryLabel = new JLabel("Salary ($):");
        salaryLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        salaryLabel.setForeground(PRIMARY_DARK);
        panel.add(salaryLabel, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        salaryField = createTextField();
        salaryField.setEnabled(false);
        panel.add(salaryField, gbc);

        // Status
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel activeLabel = new JLabel("Status:");
        activeLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        activeLabel.setForeground(PRIMARY_DARK);
        panel.add(activeLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 0.8;
        activeCheckBox = new JCheckBox("Active");
        activeCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        activeCheckBox.setEnabled(false);
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
        phoneLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        phoneLabel.setForeground(PRIMARY_DARK);
        panel.add(phoneLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        phoneField = createTextField();
        phoneField.setEnabled(false);
        panel.add(phoneField, gbc);

        // Email
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        emailLabel.setForeground(PRIMARY_DARK);
        panel.add(emailLabel, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        emailField = createTextField();
        emailField.setEnabled(false);
        panel.add(emailField, gbc);

        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(SOFT_SURFACE);
        footer.setBorder(new EmptyBorder(15, 0, 0, 0));

        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(SECONDARY_TEXT);

        buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        // Back button
        backButton = createStyledButton("← Back", SOFT_SURFACE, PRIMARY_DARK);
        backButton.setBorderColor(LIGHT_SURFACE);
        backButton.setPreferredSize(new Dimension(100, 35));
        backButton.addActionListener(e -> navigateBack());

        // Edit button
        editButton = createStyledButton("Edit", PRIMARY_DARK, Color.WHITE);
        editButton.setPreferredSize(new Dimension(100, 35));
        editButton.addActionListener(e -> toggleEditMode());

        // Save button (hidden initially)
        saveButton = createStyledButton("Save", PRIMARY_DARK, Color.WHITE);
        saveButton.setPreferredSize(new Dimension(100, 35));
        saveButton.setVisible(false);
        saveButton.addActionListener(e -> saveStaff());

        // Cancel button (hidden initially)
        cancelButton = createStyledButton("Cancel", SOFT_SURFACE, PRIMARY_DARK);
        cancelButton.setBorderColor(LIGHT_SURFACE);
        cancelButton.setPreferredSize(new Dimension(100, 35));
        cancelButton.setVisible(false);
        cancelButton.addActionListener(e -> cancelEdit());

        // Toggle Status button
        toggleStatusButton = createStyledButton("Toggle Status", SOFT_SURFACE, PRIMARY_DARK);
        toggleStatusButton.setBorderColor(LIGHT_SURFACE);
        toggleStatusButton.setPreferredSize(new Dimension(120, 35));
        toggleStatusButton.addActionListener(e -> toggleStatus());

        // Delete button
        deleteButton = createStyledButton("Delete", ERROR_COLOR, Color.WHITE);
        deleteButton.setPreferredSize(new Dimension(100, 35));
        deleteButton.addActionListener(e -> deleteStaff());

        buttonPanel.add(backButton);
        buttonPanel.add(editButton);
        buttonPanel.add(toggleStatusButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(deleteButton);

        footer.add(statusLabel, BorderLayout.WEST);
        footer.add(buttonPanel, BorderLayout.EAST);

        return footer;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        field.setBackground(Color.WHITE);
        return field;
    }

    private RoundedButton createStyledButton(String text, Color bg, Color fg) {
        RoundedButton button = new RoundedButton(text, bg, fg);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
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
    // Public methods
    // ========================

    public void displayStaff(Staff staff) {
        this.currentStaff = staff;
        if (staff == null) {
            displayEmptyState();
            return;
        }

        staffIdLabel.setText("Staff ID: " + staff.getStaffId());
        firstNameField.setText(staff.getFirstName());
        lastNameField.setText(staff.getLastName());
        positionField.setText(staff.getPosition() != null ? staff.getPosition() : "");
        departmentField.setText(staff.getDepartment() != null ? staff.getDepartment() : "");
        phoneField.setText(staff.getPhone() != null ? staff.getPhone() : "");
        emailField.setText(staff.getEmail() != null ? staff.getEmail() : "");
        
        if (staff.getHireDate() != null) {
            hireDateField.setText(staff.getHireDate().toString());
        } else {
            hireDateField.setText("");
        }
        
        salaryField.setText(staff.getSalary() > 0 ? String.valueOf(staff.getSalary()) : "");
        activeCheckBox.setSelected(staff.isActive());
        
        createdDateLabel.setText("Created: " + (staff.getCreatedAt() != null ? staff.getCreatedAt() : "--"));
        updatedDateLabel.setText("Last Updated: " + (staff.getUpdatedAt() != null ? staff.getUpdatedAt() : "--"));
        
        // Update status badge
        updateStatusBadge(staff.isActive());
        
        statusLabel.setText(" ");
        setViewMode(false);
    }

    private void displayEmptyState() {
        staffIdLabel.setText("Staff ID: --");
        firstNameField.setText("");
        lastNameField.setText("");
        positionField.setText("");
        departmentField.setText("");
        phoneField.setText("");
        emailField.setText("");
        hireDateField.setText("");
        salaryField.setText("");
        activeCheckBox.setSelected(false);
        createdDateLabel.setText("Created: --");
        updatedDateLabel.setText("Last Updated: --");
        statusBadge.setVisible(false);
        statusLabel.setText("No staff member selected");
        setViewMode(false);
    }

    private void updateStatusBadge(boolean isActive) {
        statusBadge.setVisible(true);
        if (isActive) {
            statusBadge.setText(" Active ");
            statusBadge.setBackground(ACTIVE_COLOR);
            statusBadge.setForeground(Color.WHITE);
        } else {
            statusBadge.setText(" Inactive ");
            statusBadge.setBackground(INACTIVE_COLOR);
            statusBadge.setForeground(Color.WHITE);
        }
    }

    private void setViewMode(boolean editMode) {
        this.isEditMode = editMode;
        
        // Enable/disable fields
        firstNameField.setEnabled(editMode);
        lastNameField.setEnabled(editMode);
        positionField.setEnabled(editMode);
        departmentField.setEnabled(editMode);
        phoneField.setEnabled(editMode);
        emailField.setEnabled(editMode);
        hireDateField.setEnabled(editMode);
        salaryField.setEnabled(editMode);
        activeCheckBox.setEnabled(editMode);

        // Show/hide buttons
        editButton.setVisible(!editMode);
        toggleStatusButton.setVisible(!editMode);
        deleteButton.setVisible(!editMode);
        saveButton.setVisible(editMode);
        cancelButton.setVisible(editMode);

        if (editMode) {
            statusLabel.setText("Editing staff information...");
            statusLabel.setForeground(new Color(0, 120, 215));
        } else {
            statusLabel.setText(" ");
            statusLabel.setForeground(SECONDARY_TEXT);
        }
    }

    public void toggleEditMode() {
        if (currentStaff == null) {
            showError("No staff member loaded to edit.");
            return;
        }
        setViewMode(true);
    }

    private void cancelEdit() {
        if (currentStaff != null) {
            displayStaff(currentStaff);
        } else {
            displayEmptyState();
        }
        setViewMode(false);
        statusLabel.setText("Edit cancelled");
        statusLabel.setForeground(SECONDARY_TEXT);
    }

    private void saveStaff() {
        if (currentStaff == null) {
            showError("No staff member loaded to save.");
            return;
        }

        // Validate fields
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        
        if (firstName.isEmpty() || lastName.isEmpty()) {
            showError("First Name and Last Name are required.");
            return;
        }
        if (firstName.length() < 2 || lastName.length() < 2) {
            showError("Name must be at least 2 characters.");
            return;
        }

        String phone = phoneField.getText().trim();
        if (phone.isEmpty()) {
            showError("Phone number is required.");
            return;
        }
        String phoneDigits = phone.replaceAll("[^0-9]", "");
        if (phoneDigits.length() < 10) {
            showError("Please enter a valid phone number (at least 10 digits).");
            return;
        }

        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            showError("Email is required.");
            return;
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showError("Please enter a valid email address.");
            return;
        }

        // Update staff object
        currentStaff.setFirstName(firstName);
        currentStaff.setLastName(lastName);
        currentStaff.setPosition(positionField.getText().trim());
        currentStaff.setDepartment(departmentField.getText().trim());
        currentStaff.setPhone(phone);
        currentStaff.setEmail(email);
        currentStaff.setActive(activeCheckBox.isSelected());

        // Parse salary
        String salaryStr = salaryField.getText().trim();
        if (!salaryStr.isEmpty()) {
            try {
                currentStaff.setSalary(Double.parseDouble(salaryStr));
            } catch (NumberFormatException e) {
                showError("Please enter a valid salary amount.");
                return;
            }
        }

        // Parse hire date
        String hireDateStr = hireDateField.getText().trim();
        if (!hireDateStr.isEmpty()) {
            try {
                LocalDate localDate = LocalDate.parse(hireDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                currentStaff.setHireDate(Date.valueOf(localDate));
            } catch (Exception e) {
                showError("Invalid date format. Please use YYYY-MM-DD.");
                return;
            }
        }

        // Save to database
        statusLabel.setText("Saving staff...");
        statusLabel.setForeground(new Color(0, 120, 215));
        
        boolean success = controller.updateStaff(currentStaff);
        
        if (success) {
            statusLabel.setText("Staff updated successfully!");
            statusLabel.setForeground(SUCCESS_COLOR);
            setViewMode(false);
            displayStaff(currentStaff);
            showSuccess("Staff information updated successfully!");
        } else {
            statusLabel.setText("Failed to update staff.");
            statusLabel.setForeground(ERROR_COLOR);
            showError("Failed to update staff information. Please try again.");
        }
    }

    private void toggleStatus() {
        if (currentStaff == null) {
            showError("No staff member loaded.");
            return;
        }

        boolean newStatus = !currentStaff.isActive();
        String statusText = newStatus ? "Active" : "Inactive";
        
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to change " + currentStaff.getFullName() + "'s status to " + statusText + "?",
            "Toggle Status",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success;
            if (newStatus) {
                success = controller.activateStaff(currentStaff.getStaffId());
            } else {
                success = controller.deactivateStaff(currentStaff.getStaffId());
            }
            
            if (success) {
                currentStaff.setActive(newStatus);
                updateStatusBadge(newStatus);
                activeCheckBox.setSelected(newStatus);
                showSuccess("Staff status updated to " + statusText + "!");
            } else {
                showError("Failed to update staff status.");
            }
        }
    }

    private void deleteStaff() {
        if (currentStaff == null) {
            showError("No staff member loaded to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete staff member: " + currentStaff.getFullName() + "?\nThis action cannot be undone.",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = controller.deleteStaff(currentStaff.getStaffId());
            
            if (success) {
                showSuccess("Staff member deleted successfully!");
                navigateBack();
            } else {
                showError("Failed to delete staff member. Please try again.");
            }
        }
    }

    private void navigateBack() {
        Container parent = getParent();
        while (parent != null && !(parent instanceof MainFrame)) {
            parent = parent.getParent();
        }
        if (parent instanceof MainFrame) {
            ((MainFrame) parent).showCard("STAFF_LIST");
        }
    }

    // ========================
    // Public methods for Controller
    // ========================

    public void showError(String message) {
        statusLabel.setText("❌ " + message);
        statusLabel.setForeground(ERROR_COLOR);
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showSuccess(String message) {
        statusLabel.setText("✅ " + message);
        statusLabel.setForeground(SUCCESS_COLOR);
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showInfo(String message) {
        statusLabel.setText("ℹ️ " + message);
        statusLabel.setForeground(new Color(107, 123, 121));
    }

   
}