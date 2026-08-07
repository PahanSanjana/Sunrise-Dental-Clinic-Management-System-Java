package view;

import controller.PatientController;
import model.Patient;
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

public class PatientDetailsPanel extends JPanel {
    
    // Color Palette
    private static final Color PRIMARY_DARK = new Color(0x2F3E3C);
    private static final Color MINT = new Color(0xBDDBD1);
    private static final Color SOFT_SURFACE = new Color(0xFBF9F1);
    private static final Color LIGHT_SURFACE = new Color(0xE7E9E3);
    private static final Color ERROR_COLOR = new Color(220, 80, 80);
    private static final Color SUCCESS_COLOR = new Color(60, 160, 80);
    private static final Color SECONDARY_TEXT = new Color(122, 138, 135);

    // Form Fields - View/Edit Mode
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
    private JLabel patientIdLabel;
    private JLabel createdDateLabel;
    private JLabel updatedDateLabel;
    private JLabel statusLabel;
    
    // Buttons
    private RoundedButton editButton;
    private RoundedButton saveButton;
    private RoundedButton cancelButton;
    private RoundedButton backButton;
    private RoundedButton deleteButton;
    
    private JPanel buttonPanel;
    private boolean isEditMode = false;
    private Patient currentPatient;
    private PatientController controller;

    public PatientDetailsPanel() {
        this.controller = new PatientController(this);
        initComponents();
        setViewMode(false);
        displayEmptyState();
    }

    public PatientDetailsPanel(Patient patient) {
        this.controller = new PatientController(this);
        this.currentPatient = patient;
        initComponents();
        setViewMode(false);
        displayPatient(patient);
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

        // Title and patient info
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Patient Details");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_DARK);
        
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        infoPanel.setOpaque(false);
        
        patientIdLabel = new JLabel("Patient ID: --");
        patientIdLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        patientIdLabel.setForeground(SECONDARY_TEXT);
        
        createdDateLabel = new JLabel("Created: --");
        createdDateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        createdDateLabel.setForeground(SECONDARY_TEXT);
        
        updatedDateLabel = new JLabel("Last Updated: --");
        updatedDateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        updatedDateLabel.setForeground(SECONDARY_TEXT);
        
        infoPanel.add(patientIdLabel);
        infoPanel.add(createdDateLabel);
        infoPanel.add(updatedDateLabel);
        
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

        // Patient Information Section
        mainPanel.add(createSectionPanel("Personal Information", createPersonalInfoPanel()));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Contact Information Section
        mainPanel.add(createSectionPanel("Contact Information", createContactInfoPanel()));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Emergency Contact Section
        mainPanel.add(createSectionPanel("Emergency Contact", createEmergencyContactPanel()));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Medical Information Section
        mainPanel.add(createSectionPanel("Medical Information", createMedicalInfoPanel()));

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

        // Row 0: Patient Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel nameLabel = new JLabel("Patient Name:");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nameLabel.setForeground(PRIMARY_DARK);
        panel.add(nameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 0.8;
        patientNameField = createTextField();
        patientNameField.setEnabled(false);
        panel.add(patientNameField, gbc);

        // Row 1: Gender
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel genderLabel = new JLabel("Gender:");
        genderLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        genderLabel.setForeground(PRIMARY_DARK);
        panel.add(genderLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        genderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        genderCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        genderCombo.setEnabled(false);
        panel.add(genderCombo, gbc);

        // Row 2: Date of Birth
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel dobLabel = new JLabel("Date of Birth:");
        dobLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        dobLabel.setForeground(PRIMARY_DARK);
        panel.add(dobLabel, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        dobField = createTextField();
        dobField.setEnabled(false);
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

        // Row 0: Contact Number
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel contactLabel = new JLabel("Contact Number:");
        contactLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        contactLabel.setForeground(PRIMARY_DARK);
        panel.add(contactLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        contactNumberField = createTextField();
        contactNumberField.setEnabled(false);
        panel.add(contactNumberField, gbc);

        // Row 1: Email
        gbc.gridx = 2;
        gbc.gridy = 0;
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

        // Row 2: Address
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        addressLabel.setForeground(PRIMARY_DARK);
        panel.add(addressLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 0.8;
        addressArea = createTextArea();
        addressArea.setEnabled(false);
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

        // Row 0: Emergency Contact Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel contactNameLabel = new JLabel("Contact Name:");
        contactNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        contactNameLabel.setForeground(PRIMARY_DARK);
        panel.add(contactNameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        emergencyContactField = createTextField();
        emergencyContactField.setEnabled(false);
        panel.add(emergencyContactField, gbc);

        // Row 1: Emergency Phone
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel phoneLabel = new JLabel("Contact Phone:");
        phoneLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        phoneLabel.setForeground(PRIMARY_DARK);
        panel.add(phoneLabel, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        emergencyPhoneField = createTextField();
        emergencyPhoneField.setEnabled(false);
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

        // Row 0: Medical History
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel historyLabel = new JLabel("Medical History:");
        historyLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        historyLabel.setForeground(PRIMARY_DARK);
        panel.add(historyLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.weightx = 0.8;
        medicalHistoryArea = createTextArea();
        medicalHistoryArea.setEnabled(false);
        JScrollPane historyScroll = new JScrollPane(medicalHistoryArea);
        historyScroll.setPreferredSize(new Dimension(400, 60));
        panel.add(historyScroll, gbc);

        // Row 1: Allergies
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel allergiesLabel = new JLabel("Allergies:");
        allergiesLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        allergiesLabel.setForeground(PRIMARY_DARK);
        panel.add(allergiesLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 0.8;
        allergiesArea = createTextArea();
        allergiesArea.setEnabled(false);
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
        saveButton.addActionListener(e -> savePatient());

        // Cancel button (hidden initially)
        cancelButton = createStyledButton("Cancel", SOFT_SURFACE, PRIMARY_DARK);
        cancelButton.setBorderColor(LIGHT_SURFACE);
        cancelButton.setPreferredSize(new Dimension(100, 35));
        cancelButton.setVisible(false);
        cancelButton.addActionListener(e -> cancelEdit());

        // Delete button
        deleteButton = createStyledButton("Delete", ERROR_COLOR, Color.WHITE);
        deleteButton.setPreferredSize(new Dimension(100, 35));
        deleteButton.addActionListener(e -> deletePatient());

        buttonPanel.add(backButton);
        buttonPanel.add(editButton);
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

    private JTextArea createTextArea() {
        JTextArea area = new JTextArea();
        area.setFont(new Font("Segoe UI", Font.PLAIN, 13));
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

    // ========================
    // Public methods
    // ========================

    public void displayPatient(Patient patient) {
        this.currentPatient = patient;
        if (patient == null) {
            displayEmptyState();
            return;
        }

        patientIdLabel.setText("Patient ID: " + patient.getPatientId());
        patientNameField.setText(patient.getPatientName());
        genderCombo.setSelectedItem(patient.getGender() != null ? patient.getGender() : "Male");
        contactNumberField.setText(patient.getContactNumber());
        emailField.setText(patient.getEmail() != null ? patient.getEmail() : "");
        addressArea.setText(patient.getAddress() != null ? patient.getAddress() : "");
        emergencyContactField.setText(patient.getEmergencyContact() != null ? patient.getEmergencyContact() : "");
        emergencyPhoneField.setText(patient.getEmergencyPhone() != null ? patient.getEmergencyPhone() : "");
        medicalHistoryArea.setText(patient.getMedicalHistory() != null ? patient.getMedicalHistory() : "");
        allergiesArea.setText(patient.getAllergies() != null ? patient.getAllergies() : "");
        
        if (patient.getDateOfBirth() != null) {
            dobField.setText(patient.getDateOfBirth().toString());
        } else {
            dobField.setText("");
        }
        
        createdDateLabel.setText("Created: " + (patient.getCreatedAt() != null ? patient.getCreatedAt() : "--"));
        updatedDateLabel.setText("Last Updated: " + (patient.getUpdatedAt() != null ? patient.getUpdatedAt() : "--"));
        
        statusLabel.setText(" ");
        setViewMode(false);
    }

    public void toggleEditMode() {
        if (currentPatient == null) {
            showError("No patient loaded to edit.");
            return;
        }
        setViewMode(true);
    }

    private void displayEmptyState() {
        patientIdLabel.setText("Patient ID: --");
        patientNameField.setText("");
        genderCombo.setSelectedIndex(0);
        contactNumberField.setText("");
        emailField.setText("");
        addressArea.setText("");
        emergencyContactField.setText("");
        emergencyPhoneField.setText("");
        medicalHistoryArea.setText("");
        allergiesArea.setText("");
        dobField.setText("");
        createdDateLabel.setText("Created: --");
        updatedDateLabel.setText("Last Updated: --");
        statusLabel.setText("No patient selected");
        setViewMode(false);
    }

    private void setViewMode(boolean editMode) {
        this.isEditMode = editMode;
        
        // Enable/disable fields
        patientNameField.setEnabled(editMode);
        genderCombo.setEnabled(editMode);
        contactNumberField.setEnabled(editMode);
        emailField.setEnabled(editMode);
        addressArea.setEnabled(editMode);
        emergencyContactField.setEnabled(editMode);
        emergencyPhoneField.setEnabled(editMode);
        medicalHistoryArea.setEnabled(editMode);
        allergiesArea.setEnabled(editMode);
        dobField.setEnabled(editMode);

        // Show/hide buttons
        editButton.setVisible(!editMode);
        deleteButton.setVisible(!editMode);
        saveButton.setVisible(editMode);
        cancelButton.setVisible(editMode);

        if (editMode) {
            statusLabel.setText("Editing patient information...");
            statusLabel.setForeground(new Color(0, 120, 215));
        } else {
            statusLabel.setText(" ");
            statusLabel.setForeground(SECONDARY_TEXT);
        }
    }

    private void cancelEdit() {
        if (currentPatient != null) {
            displayPatient(currentPatient);
        } else {
            displayEmptyState();
        }
        setViewMode(false);
        statusLabel.setText("Edit cancelled");
        statusLabel.setForeground(SECONDARY_TEXT);
    }

    private void savePatient() {
        if (currentPatient == null) {
            showError("No patient loaded to save.");
            return;
        }

        // Validate fields
        String patientName = patientNameField.getText().trim();
        if (patientName.isEmpty()) {
            showError("Patient Name is required.");
            return;
        }
        if (patientName.length() < 2) {
            showError("Patient Name must be at least 2 characters.");
            return;
        }

        String contactNumber = contactNumberField.getText().trim();
        if (contactNumber.isEmpty()) {
            showError("Contact Number is required.");
            return;
        }
        String contactDigits = contactNumber.replaceAll("[^0-9]", "");
        if (contactDigits.length() < 10) {
            showError("Please enter a valid contact number (at least 10 digits).");
            return;
        }

        String email = emailField.getText().trim();
        if (!email.isEmpty() && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showError("Please enter a valid email address.");
            return;
        }

        // Update patient object
        currentPatient.setPatientName(patientName);
        currentPatient.setGender((String) genderCombo.getSelectedItem());
        currentPatient.setContactNumber(contactNumber);
        currentPatient.setEmail(email);
        currentPatient.setAddress(addressArea.getText().trim());
        currentPatient.setEmergencyContact(emergencyContactField.getText().trim());
        currentPatient.setEmergencyPhone(emergencyPhoneField.getText().trim());
        currentPatient.setMedicalHistory(medicalHistoryArea.getText().trim());
        currentPatient.setAllergies(allergiesArea.getText().trim());

        // Save to database
        statusLabel.setText("Saving patient...");
        statusLabel.setForeground(new Color(0, 120, 215));
        
        boolean success = controller.updatePatient(currentPatient);
        
        if (success) {
            statusLabel.setText("Patient updated successfully!");
            statusLabel.setForeground(SUCCESS_COLOR);
            setViewMode(false);
            // Refresh display
            displayPatient(currentPatient);
            showSuccess("Patient information updated successfully!");
        } else {
            statusLabel.setText("Failed to update patient.");
            statusLabel.setForeground(ERROR_COLOR);
            showError("Failed to update patient information. Please try again.");
        }
    }

    private void deletePatient() {
        if (currentPatient == null) {
            showError("No patient loaded to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete patient: " + currentPatient.getPatientName() + "?\nThis action cannot be undone.",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = controller.deletePatient(currentPatient.getPatientId());
            
            if (success) {
                showSuccess("Patient deleted successfully!");
                navigateBack();
            } else {
                showError("Failed to delete patient. Please try again.");
            }
        }
    }

    private void navigateBack() {
        Container parent = getParent();
        while (parent != null && !(parent instanceof MainFrame)) {
            parent = parent.getParent();
        }
        if (parent instanceof MainFrame) {
            ((MainFrame) parent).showCard("PATIENT_LIST");
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

    private static class MouseAdapter extends java.awt.event.MouseAdapter {
        // Empty implementation
    }
}