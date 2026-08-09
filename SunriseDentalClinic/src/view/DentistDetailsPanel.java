package view;

import controller.DentistController;
import model.Dentist;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DentistDetailsPanel extends JPanel {
    
    // Color Palette
    private static final Color PRIMARY_DARK = new Color(0x2F3E3C);
    private static final Color MINT = new Color(0xBDDBD1);
    private static final Color SOFT_SURFACE = new Color(0xFBF9F1);
    private static final Color LIGHT_SURFACE = new Color(0xE7E9E3);
    private static final Color ERROR_COLOR = new Color(220, 80, 80);
    private static final Color SUCCESS_COLOR = new Color(60, 160, 80);
    private static final Color SECONDARY_TEXT = new Color(122, 138, 135);
    private static final Color AVAILABLE_COLOR = new Color(60, 160, 80);
    private static final Color UNAVAILABLE_COLOR = new Color(200, 80, 80);

    // Form Fields - View/Edit Mode
    private JTextField dentistNameField;
    private JTextField specializationField;
    private JTextField licenseNumberField;
    private JTextField workingHoursField;
    private JTextField phoneField;
    private JTextField emailField;
    private JTextField experienceField;
    private JTextField consultationFeeField;
    private JCheckBox availableCheckBox;
    private JLabel dentistIdLabel;
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
    private RoundedButton toggleAvailabilityButton;
    
    private JPanel buttonPanel;
    private boolean isEditMode = false;
    private Dentist currentDentist;
    private DentistController controller;

    public DentistDetailsPanel() {
        this.controller = new DentistController(this);
        initComponents();
        setViewMode(false);
        displayEmptyState();
    }

    public DentistDetailsPanel(Dentist dentist) {
        this.controller = new DentistController(this);
        this.currentDentist = dentist;
        initComponents();
        setViewMode(false);
        displayDentist(dentist);
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

        // Title and dentist info
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Dentist Details");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_DARK);
        
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        infoPanel.setOpaque(false);
        
        dentistIdLabel = new JLabel("Dentist ID: --");
        dentistIdLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dentistIdLabel.setForeground(SECONDARY_TEXT);
        
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
        
        infoPanel.add(dentistIdLabel);
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
        
        // Professional Information Section
        mainPanel.add(createSectionPanel("Professional Information", createProfessionalInfoPanel()));
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

        // Dentist Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel nameLabel = new JLabel("Dentist Name:");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nameLabel.setForeground(PRIMARY_DARK);
        panel.add(nameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 0.8;
        dentistNameField = createTextField();
        dentistNameField.setEnabled(false);
        panel.add(dentistNameField, gbc);

        return panel;
    }

    private JPanel createProfessionalInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.weightx = 1.0;

        // Specialization
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel specializationLabel = new JLabel("Specialization:");
        specializationLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        specializationLabel.setForeground(PRIMARY_DARK);
        panel.add(specializationLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 0.8;
        specializationField = createTextField();
        specializationField.setEnabled(false);
        panel.add(specializationField, gbc);

        // License Number
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel licenseLabel = new JLabel("License Number:");
        licenseLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        licenseLabel.setForeground(PRIMARY_DARK);
        panel.add(licenseLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        licenseNumberField = createTextField();
        licenseNumberField.setEnabled(false);
        panel.add(licenseNumberField, gbc);

        // Working Hours
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel workingHoursLabel = new JLabel("Working Hours:");
        workingHoursLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        workingHoursLabel.setForeground(PRIMARY_DARK);
        panel.add(workingHoursLabel, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        workingHoursField = createTextField();
        workingHoursField.setEnabled(false);
        panel.add(workingHoursField, gbc);

        // Years of Experience
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel experienceLabel = new JLabel("Years of Experience:");
        experienceLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        experienceLabel.setForeground(PRIMARY_DARK);
        panel.add(experienceLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        experienceField = createTextField();
        experienceField.setEnabled(false);
        panel.add(experienceField, gbc);

        // Consultation Fee
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel feeLabel = new JLabel("Consultation Fee ($):");
        feeLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        feeLabel.setForeground(PRIMARY_DARK);
        panel.add(feeLabel, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        consultationFeeField = createTextField();
        consultationFeeField.setEnabled(false);
        panel.add(consultationFeeField, gbc);

        // Availability
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel availableLabel = new JLabel("Availability:");
        availableLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        availableLabel.setForeground(PRIMARY_DARK);
        panel.add(availableLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 0.8;
        availableCheckBox = new JCheckBox("Available for appointments");
        availableCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        availableCheckBox.setEnabled(false);
        panel.add(availableCheckBox, gbc);

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
        saveButton.addActionListener(e -> saveDentist());

        // Cancel button (hidden initially)
        cancelButton = createStyledButton("Cancel", SOFT_SURFACE, PRIMARY_DARK);
        cancelButton.setBorderColor(LIGHT_SURFACE);
        cancelButton.setPreferredSize(new Dimension(100, 35));
        cancelButton.setVisible(false);
        cancelButton.addActionListener(e -> cancelEdit());

        // Toggle Availability button
        toggleAvailabilityButton = createStyledButton("Toggle Availability", SOFT_SURFACE, PRIMARY_DARK);
        toggleAvailabilityButton.setBorderColor(LIGHT_SURFACE);
        toggleAvailabilityButton.setPreferredSize(new Dimension(140, 35));
        toggleAvailabilityButton.addActionListener(e -> toggleAvailability());

        // Delete button
        deleteButton = createStyledButton("Delete", ERROR_COLOR, Color.WHITE);
        deleteButton.setPreferredSize(new Dimension(100, 35));
        deleteButton.addActionListener(e -> deleteDentist());

        buttonPanel.add(backButton);
        buttonPanel.add(editButton);
        buttonPanel.add(toggleAvailabilityButton);
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

    public void displayDentist(Dentist dentist) {
        this.currentDentist = dentist;
        if (dentist == null) {
            displayEmptyState();
            return;
        }

        dentistIdLabel.setText("Dentist ID: " + dentist.getDentistId());
        dentistNameField.setText(dentist.getDentistName());
        specializationField.setText(dentist.getSpecialization() != null ? dentist.getSpecialization() : "");
        licenseNumberField.setText(dentist.getLicenseNumber() != null ? dentist.getLicenseNumber() : "");
        workingHoursField.setText(dentist.getWorkingHours() != null ? dentist.getWorkingHours() : "");
        phoneField.setText(dentist.getPhone() != null ? dentist.getPhone() : "");
        emailField.setText(dentist.getEmail() != null ? dentist.getEmail() : "");
        experienceField.setText(dentist.getYearsOfExperience() > 0 ? String.valueOf(dentist.getYearsOfExperience()) : "");
        consultationFeeField.setText(dentist.getConsultationFee() > 0 ? String.valueOf(dentist.getConsultationFee()) : "");
        availableCheckBox.setSelected(dentist.isAvailable());
        
        createdDateLabel.setText("Created: " + (dentist.getCreatedAt() != null ? dentist.getCreatedAt() : "--"));
        updatedDateLabel.setText("Last Updated: " + (dentist.getUpdatedAt() != null ? dentist.getUpdatedAt() : "--"));
        
        // Update status badge
        updateStatusBadge(dentist.isAvailable());
        
        statusLabel.setText(" ");
        setViewMode(false);
    }

    private void displayEmptyState() {
        dentistIdLabel.setText("Dentist ID: --");
        dentistNameField.setText("");
        specializationField.setText("");
        licenseNumberField.setText("");
        workingHoursField.setText("");
        phoneField.setText("");
        emailField.setText("");
        experienceField.setText("");
        consultationFeeField.setText("");
        availableCheckBox.setSelected(false);
        createdDateLabel.setText("Created: --");
        updatedDateLabel.setText("Last Updated: --");
        statusBadge.setVisible(false);
        statusLabel.setText("No dentist selected");
        setViewMode(false);
    }

    private void updateStatusBadge(boolean isAvailable) {
        statusBadge.setVisible(true);
        if (isAvailable) {
            statusBadge.setText(" Available ");
            statusBadge.setBackground(AVAILABLE_COLOR);
            statusBadge.setForeground(Color.WHITE);
        } else {
            statusBadge.setText(" Unavailable ");
            statusBadge.setBackground(UNAVAILABLE_COLOR);
            statusBadge.setForeground(Color.WHITE);
        }
    }

    private void setViewMode(boolean editMode) {
        this.isEditMode = editMode;
        
        // Enable/disable fields
        dentistNameField.setEnabled(editMode);
        specializationField.setEnabled(editMode);
        licenseNumberField.setEnabled(editMode);
        workingHoursField.setEnabled(editMode);
        phoneField.setEnabled(editMode);
        emailField.setEnabled(editMode);
        experienceField.setEnabled(editMode);
        consultationFeeField.setEnabled(editMode);
        availableCheckBox.setEnabled(editMode);

        // Show/hide buttons
        editButton.setVisible(!editMode);
        toggleAvailabilityButton.setVisible(!editMode);
        deleteButton.setVisible(!editMode);
        saveButton.setVisible(editMode);
        cancelButton.setVisible(editMode);

        if (editMode) {
            statusLabel.setText("Editing dentist information...");
            statusLabel.setForeground(new Color(0, 120, 215));
        } else {
            statusLabel.setText(" ");
            statusLabel.setForeground(SECONDARY_TEXT);
        }
    }

    public void toggleEditMode() {
        if (currentDentist == null) {
            showError("No dentist loaded to edit.");
            return;
        }
        setViewMode(true);
    }

    private void cancelEdit() {
        if (currentDentist != null) {
            displayDentist(currentDentist);
        } else {
            displayEmptyState();
        }
        setViewMode(false);
        statusLabel.setText("Edit cancelled");
        statusLabel.setForeground(SECONDARY_TEXT);
    }

    private void saveDentist() {
        if (currentDentist == null) {
            showError("No dentist loaded to save.");
            return;
        }

        // Validate fields
        String dentistName = dentistNameField.getText().trim();
        if (dentistName.isEmpty()) {
            showError("Dentist Name is required.");
            return;
        }
        if (dentistName.length() < 2) {
            showError("Dentist Name must be at least 2 characters.");
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

        // Update dentist object
        currentDentist.setDentistName(dentistName);
        currentDentist.setSpecialization(specializationField.getText().trim());
        currentDentist.setLicenseNumber(licenseNumberField.getText().trim());
        currentDentist.setWorkingHours(workingHoursField.getText().trim());
        currentDentist.setPhone(phone);
        currentDentist.setEmail(email);
        currentDentist.setAvailable(availableCheckBox.isSelected());

        // Parse years of experience
        String experienceStr = experienceField.getText().trim();
        if (!experienceStr.isEmpty()) {
            try {
                currentDentist.setYearsOfExperience(Integer.parseInt(experienceStr));
            } catch (NumberFormatException e) {
                showError("Please enter a valid number for years of experience.");
                return;
            }
        }

        // Parse consultation fee
        String feeStr = consultationFeeField.getText().trim();
        if (!feeStr.isEmpty()) {
            try {
                currentDentist.setConsultationFee(Double.parseDouble(feeStr));
            } catch (NumberFormatException e) {
                showError("Please enter a valid number for consultation fee.");
                return;
            }
        }

        // Save to database
        statusLabel.setText("Saving dentist...");
        statusLabel.setForeground(new Color(0, 120, 215));
        
        boolean success = controller.updateDentist(currentDentist);
        
        if (success) {
            statusLabel.setText("Dentist updated successfully!");
            statusLabel.setForeground(SUCCESS_COLOR);
            setViewMode(false);
            displayDentist(currentDentist);
            showSuccess("Dentist information updated successfully!");
        } else {
            statusLabel.setText("Failed to update dentist.");
            statusLabel.setForeground(ERROR_COLOR);
            showError("Failed to update dentist information. Please try again.");
        }
    }

    private void toggleAvailability() {
        if (currentDentist == null) {
            showError("No dentist loaded.");
            return;
        }

        boolean newStatus = !currentDentist.isAvailable();
        String statusText = newStatus ? "Available" : "Unavailable";
        
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to change " + currentDentist.getDentistName() + "'s availability to " + statusText + "?",
            "Toggle Availability",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = controller.updateAvailability(currentDentist.getDentistId(), newStatus);
            
            if (success) {
                currentDentist.setAvailable(newStatus);
                updateStatusBadge(newStatus);
                availableCheckBox.setSelected(newStatus);
                showSuccess("Dentist availability updated to " + statusText + "!");
            } else {
                showError("Failed to update dentist availability.");
            }
        }
    }

    private void deleteDentist() {
        if (currentDentist == null) {
            showError("No dentist loaded to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete dentist: " + currentDentist.getDentistName() + "?\nThis action cannot be undone.",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = controller.deleteDentist(currentDentist.getDentistId());
            
            if (success) {
                showSuccess("Dentist deleted successfully!");
                navigateBack();
            } else {
                showError("Failed to delete dentist. Please try again.");
            }
        }
    }

    private void navigateBack() {
        Container parent = getParent();
        while (parent != null && !(parent instanceof MainFrame)) {
            parent = parent.getParent();
        }
        if (parent instanceof MainFrame) {
            ((MainFrame) parent).showCard("DENTIST_LIST");
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