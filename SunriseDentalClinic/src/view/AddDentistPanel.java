package view;

import controller.DentistController;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public class AddDentistPanel extends JPanel {
    
    // Color Palette
    private static final Color PRIMARY_DARK = new Color(0x2F3E3C);
    private static final Color MINT = new Color(0xBDDBD1);
    private static final Color SOFT_SURFACE = new Color(0xFBF9F1);
    private static final Color LIGHT_SURFACE = new Color(0xE7E9E3);
    private static final Color ERROR_COLOR = new Color(220, 80, 80);
    private static final Color SUCCESS_COLOR = new Color(60, 160, 80);
    private static final Color SECONDARY_TEXT = new Color(122, 138, 135);

    // Form Fields
    private JTextField dentistNameField;
    private JTextField specializationField;
    private JTextField licenseNumberField;
    private JTextField workingHoursField;
    private JTextField phoneField;
    private JTextField emailField;
    private JTextField experienceField;
    private JTextField consultationFeeField;
    private JCheckBox availableCheckBox;
    
    // Buttons
    private RoundedButton saveButton;
    private RoundedButton clearButton;
    private RoundedButton cancelButton;
    
    private JLabel statusLabel;
    private DentistController controller;

    public AddDentistPanel() {
        initComponents();
        // Initialize controller AFTER all components are created
        this.controller = new DentistController(this);
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

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(SOFT_SURFACE);
        header.setBorder(new EmptyBorder(0, 0, 15, 0));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Add New Dentist");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_DARK);
        
        JLabel subtitleLabel = new JLabel("Register a new dentist in the system");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
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
        
        // Professional Information Section
        formPanel.add(createSectionPanel("Professional Information", createProfessionalInfoPanel()));
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Contact Information Section
        formPanel.add(createSectionPanel("Contact Information", createContactInfoPanel()));

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
        dentistNameField.setToolTipText("Full name of the dentist");
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
        specializationField.setToolTipText("e.g., Orthodontics, General Dentistry, etc.");
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
        licenseNumberField.setToolTipText("Unique dental license number");
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
        workingHoursField.setToolTipText("e.g., Mon-Fri 9:00 AM - 5:00 PM");
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
        experienceField.setToolTipText("Number of years of experience");
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
        consultationFeeField.setToolTipText("Fee for consultation in USD");
        panel.add(consultationFeeField, gbc);

        // Availability
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel availableLabel = new JLabel("Available:");
        availableLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        availableLabel.setForeground(PRIMARY_DARK);
        panel.add(availableLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 0.8;
        availableCheckBox = new JCheckBox("Yes, this dentist is currently available");
        availableCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        availableCheckBox.setSelected(true);
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
        phoneField.setToolTipText("Contact phone number (at least 10 digits)");
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
        emailField.setToolTipText("Valid email address");
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

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        saveButton = createStyledButton("Save Dentist", PRIMARY_DARK, Color.WHITE);
        saveButton.setPreferredSize(new Dimension(160, 40));

        clearButton = createStyledButton("Clear", SOFT_SURFACE, PRIMARY_DARK);
        clearButton.setBorderColor(LIGHT_SURFACE);
        clearButton.setPreferredSize(new Dimension(100, 40));

        cancelButton = createStyledButton("Cancel", SOFT_SURFACE, PRIMARY_DARK);
        cancelButton.setBorderColor(LIGHT_SURFACE);
        cancelButton.setPreferredSize(new Dimension(100, 40));

        buttonPanel.add(clearButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

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
        field.setPreferredSize(new Dimension(200, 35));
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
    // Public methods for Controller
    // ========================

    public String getDentistName() { 
        return dentistNameField.getText().trim(); 
    }
    
    public String getSpecialization() { 
        return specializationField.getText().trim(); 
    }
    
    public String getLicenseNumber() { 
        return licenseNumberField.getText().trim(); 
    }
    
    public String getWorkingHours() { 
        return workingHoursField.getText().trim(); 
    }
    
    public String getPhone() { 
        return phoneField.getText().trim(); 
    }
    
    public String getEmail() { 
        return emailField.getText().trim(); 
    }
    
    public String getExperience() { 
        return experienceField.getText().trim(); 
    }
    
    public String getConsultationFee() { 
        return consultationFeeField.getText().trim(); 
    }
    
    public boolean isAvailable() { 
        return availableCheckBox.isSelected(); 
    }

    public void clearForm() {
        dentistNameField.setText("");
        specializationField.setText("");
        licenseNumberField.setText("");
        workingHoursField.setText("");
        phoneField.setText("");
        emailField.setText("");
        experienceField.setText("");
        consultationFeeField.setText("");
        availableCheckBox.setSelected(true);
        statusLabel.setText("Form cleared");
        statusLabel.setForeground(SECONDARY_TEXT);
    }

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
        statusLabel.setForeground(new Color(0, 120, 215));
    }

    public void addSaveListener(ActionListener listener) {
        if (saveButton != null) {
            saveButton.addActionListener(listener);
        }
    }

    public void addClearListener(ActionListener listener) {
        if (clearButton != null) {
            clearButton.addActionListener(listener);
        }
    }

    public void addCancelListener(ActionListener listener) {
        if (cancelButton != null) {
            cancelButton.addActionListener(listener);
        }
    }
}