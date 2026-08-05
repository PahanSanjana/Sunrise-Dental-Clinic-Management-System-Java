package view;

import controller.PatientController;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

public class AddPatientPanel extends JPanel {
    
    // Color Palette
    private static final Color PRIMARY_DARK = new Color(0x2F3E3C);
    private static final Color MINT = new Color(0xBDDBD1);
    private static final Color SOFT_SURFACE = new Color(0xFBF9F1);
    private static final Color LIGHT_SURFACE = new Color(0xE7E9E3);
    private static final Color ERROR_COLOR = new Color(220, 80, 80);
    private static final Color SUCCESS_COLOR = new Color(60, 160, 80);
    
    // Form Fields
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField dobField;
    private JComboBox<String> genderCombo;
    private JTextField phoneField;
    private JTextField emailField;
    private JTextArea addressArea;
    private JTextField emergencyContactField;
    private JTextField emergencyPhoneField;
    private JTextArea medicalHistoryArea;
    private JTextArea allergiesArea;
    
    private RoundedButton saveButton;
    private RoundedButton clearButton;
    private RoundedButton cancelButton;
    private JLabel messageLabel;
    private PatientController controller;

    public AddPatientPanel() {
        this.controller = new PatientController(this);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(SOFT_SURFACE);
        setBorder(new EmptyBorder(20, 30, 20, 30));

        // Header
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Form
        JPanel formPanel = createFormPanel();
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(SOFT_SURFACE);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Add New Patient");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_DARK);

        JLabel subtitleLabel = new JLabel("Enter patient information to register in the system");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(107, 123, 121));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(subtitleLabel);

        header.add(textPanel, BorderLayout.WEST);
        return header;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            new EmptyBorder(30, 30, 30, 30)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 10, 8, 10);

        int row = 0;

        // Personal Information Section
        JLabel sectionLabel1 = createSectionLabel("Personal Information");
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 15, 10);
        formPanel.add(sectionLabel1, gbc);
        row++;

        // First Name
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 10, 5, 10);
        addFormField(formPanel, gbc, "First Name:", firstNameField = createTextField(), row++, 0);
        addFormField(formPanel, gbc, "Last Name:", lastNameField = createTextField(), row++, 1);

        // Date of Birth
        addFormField(formPanel, gbc, "Date of Birth (YYYY-MM-DD):", dobField = createTextField(), row++, 0);
        
        // Gender
        JLabel genderLabel = new JLabel("Gender:");
        genderLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        genderLabel.setForeground(PRIMARY_DARK);
        genderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        genderCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        genderCombo.setPreferredSize(new Dimension(200, 35));
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        formPanel.add(genderLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(genderCombo, gbc);
        row++;

        // Contact Information Section
        JLabel sectionLabel2 = createSectionLabel("Contact Information");
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 15, 10);
        formPanel.add(sectionLabel2, gbc);
        row++;

        // Phone and Email
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 10, 5, 10);
        addFormField(formPanel, gbc, "Phone:", phoneField = createTextField(), row++, 0);
        addFormField(formPanel, gbc, "Email:", emailField = createTextField(), row++, 1);

        // Address
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 10, 5, 10);
        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        addressLabel.setForeground(PRIMARY_DARK);
        formPanel.add(addressLabel, gbc);
        row++;

        addressArea = createTextArea();
        JScrollPane addressScroll = new JScrollPane(addressArea);
        addressScroll.setPreferredSize(new Dimension(400, 60));
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 10, 5, 10);
        formPanel.add(addressScroll, gbc);
        row++;

        // Emergency Contact Section
        JLabel sectionLabel3 = createSectionLabel("Emergency Contact");
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 15, 10);
        formPanel.add(sectionLabel3, gbc);
        row++;

        // Emergency Contact
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 10, 5, 10);
        addFormField(formPanel, gbc, "Contact Name:", emergencyContactField = createTextField(), row++, 0);
        addFormField(formPanel, gbc, "Contact Phone:", emergencyPhoneField = createTextField(), row++, 1);

        // Medical Information Section
        JLabel sectionLabel4 = createSectionLabel("Medical Information");
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 15, 10);
        formPanel.add(sectionLabel4, gbc);
        row++;

        // Medical History
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 10, 5, 10);
        JLabel historyLabel = new JLabel("Medical History:");
        historyLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        historyLabel.setForeground(PRIMARY_DARK);
        formPanel.add(historyLabel, gbc);
        row++;

        medicalHistoryArea = createTextArea();
        JScrollPane historyScroll = new JScrollPane(medicalHistoryArea);
        historyScroll.setPreferredSize(new Dimension(400, 60));
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 10, 5, 10);
        formPanel.add(historyScroll, gbc);
        row++;

        // Allergies
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 10, 5, 10);
        JLabel allergiesLabel = new JLabel("Allergies:");
        allergiesLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        allergiesLabel.setForeground(PRIMARY_DARK);
        formPanel.add(allergiesLabel, gbc);
        row++;

        allergiesArea = createTextArea();
        JScrollPane allergiesScroll = new JScrollPane(allergiesArea);
        allergiesScroll.setPreferredSize(new Dimension(400, 60));
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 10, 10, 10);
        formPanel.add(allergiesScroll, gbc);
        row++;

        // Message Label
        messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        messageLabel.setForeground(ERROR_COLOR);
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 5, 10);
        formPanel.add(messageLabel, gbc);

        return formPanel;
    }

    private JLabel createSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(PRIMARY_DARK);
        label.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, MINT));
        return label;
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, String labelText, 
                              JComponent field, int row, int col) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(PRIMARY_DARK);
        
        gbc.gridx = col;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = col == 0 ? 0.3 : 0.7;
        panel.add(label, gbc);
        
        gbc.gridx = col + 1;
        field.setPreferredSize(new Dimension(200, 35));
        panel.add(field, gbc);
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
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
        return area;
    }

    private JPanel createFooterPanel() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        footer.setBackground(SOFT_SURFACE);

        saveButton = createStyledButton("Save Patient", PRIMARY_DARK, Color.WHITE);
        clearButton = createStyledButton("Clear Form", SOFT_SURFACE, PRIMARY_DARK);
        clearButton.setBorderColor(LIGHT_SURFACE);  // Now works because clearButton is RoundedButton
        cancelButton = createStyledButton("Cancel", SOFT_SURFACE, PRIMARY_DARK);
        cancelButton.setBorderColor(LIGHT_SURFACE);  // Now works because cancelButton is RoundedButton

        footer.add(clearButton);
        footer.add(cancelButton);
        footer.add(saveButton);

        return footer;
    }

    private RoundedButton createStyledButton(String text, Color bg, Color fg) {
        RoundedButton button = new RoundedButton(text, bg, fg);
        button.setPreferredSize(new Dimension(140, 40));
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
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
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

            if (borderColor != bg && borderColor != null) {
                g2.setColor(borderColor);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            }

            super.paintComponent(g);
            g2.dispose();
        }
    }

    // ========================
    // Public methods for Controller
    // ========================

    public String getFirstName() { return firstNameField.getText().trim(); }
    public String getLastName() { return lastNameField.getText().trim(); }
    public String getDateOfBirth() { return dobField.getText().trim(); }
    public String getGender() { return (String) genderCombo.getSelectedItem(); }
    public String getPhone() { return phoneField.getText().trim(); }
    public String getEmail() { return emailField.getText().trim(); }
    public String getAddress() { return addressArea.getText().trim(); }
    public String getEmergencyContact() { return emergencyContactField.getText().trim(); }
    public String getEmergencyPhone() { return emergencyPhoneField.getText().trim(); }
    public String getMedicalHistory() { return medicalHistoryArea.getText().trim(); }
    public String getAllergies() { return allergiesArea.getText().trim(); }

    public void clearForm() {
        firstNameField.setText("");
        lastNameField.setText("");
        dobField.setText("");
        genderCombo.setSelectedIndex(0);
        phoneField.setText("");
        emailField.setText("");
        addressArea.setText("");
        emergencyContactField.setText("");
        emergencyPhoneField.setText("");
        medicalHistoryArea.setText("");
        allergiesArea.setText("");
        messageLabel.setText(" ");
        messageLabel.setForeground(ERROR_COLOR);
    }

    public void showError(String message) {
        messageLabel.setText("⚠ " + message);
        messageLabel.setForeground(ERROR_COLOR);
    }

    public void showSuccess(String message) {
        messageLabel.setText("✓ " + message);
        messageLabel.setForeground(SUCCESS_COLOR);
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

    // MouseAdapter inner class for button hover
    private static class MouseAdapter extends java.awt.event.MouseAdapter {
        // Empty implementation - used by RoundedButton
    }
}