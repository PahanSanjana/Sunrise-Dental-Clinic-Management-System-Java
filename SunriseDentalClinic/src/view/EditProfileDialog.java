package view;

import controller.UserProfileController;
import model.User;
import model.User.UserRole;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

public class EditProfileDialog extends JDialog {
    
    private static final Color PRIMARY_DARK = new Color(0x2F3E3C);
    private static final Color MINT = new Color(0xBDDBD1);
    private static final Color LIGHT_SURFACE = new Color(0xE7E9E3);
    private static final Color ERROR_COLOR = new Color(220, 80, 80);
    private static final Color SUCCESS_COLOR = new Color(60, 160, 80);

    private User currentUser;
    private UserProfileController controller;
    private Map<String, Object> profileData;
    
    // Common fields
    private JTextField emailField;
    
    // Patient fields
    private JTextField patientNameField;
    private JComboBox<String> genderCombo;
    private JTextArea addressArea;
    private JTextField contactNumberField;
    private JTextField dobField;
    private JTextField emergencyContactField;
    private JTextField emergencyPhoneField;
    private JTextArea medicalHistoryArea;
    private JTextArea allergiesArea;
    
    // Staff fields
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField positionField;
    private JTextField departmentField;
    private JTextField phoneField;
    private JTextField hireDateField;
    private JTextField salaryField;
    
    // Dentist fields
    private JTextField dentistNameField;
    private JTextField specializationField;
    private JTextField licenseNumberField;
    private JTextField workingHoursField;
    private JTextField dentistPhoneField;
    private JTextField dentistEmailField;
    private JTextField experienceField;
    private JTextField consultationFeeField;
    private JCheckBox availableCheckBox;
    
    private JButton saveButton;
    private JButton cancelButton;
    private JLabel statusLabel;

    public EditProfileDialog(JFrame parent, User user, UserProfileController controller) {
        super(parent, "Edit Profile", true);
        this.currentUser = user;
        this.controller = controller;
        this.profileData = controller.getProfileData(user.getUserId(), user.getRole());
        
        initComponents();
        loadData();
        setSize(600, 700);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);
        setModal(true);

        // Main panel with scroll
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        // Header
        JLabel titleLabel = new JLabel("Edit Profile");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_DARK);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Common fields
        mainPanel.add(createCommonFields());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Role-specific fields
        switch (currentUser.getRole()) {
            case PATIENT:
                mainPanel.add(createPatientFields());
                break;
            case RECEPTION:
                mainPanel.add(createStaffFields());
                break;
            case DENTIST:
                mainPanel.add(createDentistFields());
                break;
            case ADMIN:
                // Admin has no additional fields
                JLabel adminLabel = new JLabel("Admin users have no additional profile fields to edit.");
                adminLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
                adminLabel.setForeground(new Color(107, 123, 121));
                adminLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                mainPanel.add(adminLabel);
                break;
        }

        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Buttons
        mainPanel.add(createButtonPanel());

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // Status label at bottom
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBackground(Color.WHITE);
        statusPanel.setBorder(new EmptyBorder(5, 30, 10, 30));
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.SOUTH);
    }

    private JPanel createCommonFields() {
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

        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBackground(Color.WHITE);
        fieldsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.weightx = 1.0;

        // Email (editable for all)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        emailLabel.setForeground(PRIMARY_DARK);
        fieldsPanel.add(emailLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 0.8;
        emailField = new JTextField();
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        fieldsPanel.add(emailField, gbc);

        panel.add(fieldsPanel);
        return panel;
    }

    private JPanel createPatientFields() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(MINT, 1),
            "Patient Information",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            PRIMARY_DARK
        ));

        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBackground(Color.WHITE);
        fieldsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.weightx = 1.0;

        // Patient Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel nameLabel = new JLabel("Full Name:");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nameLabel.setForeground(PRIMARY_DARK);
        fieldsPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 0.8;
        patientNameField = new JTextField();
        patientNameField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        patientNameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        fieldsPanel.add(patientNameField, gbc);

        // Gender
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel genderLabel = new JLabel("Gender:");
        genderLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        genderLabel.setForeground(PRIMARY_DARK);
        fieldsPanel.add(genderLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        genderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        genderCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        fieldsPanel.add(genderCombo, gbc);

        // Date of Birth
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel dobLabel = new JLabel("Date of Birth:");
        dobLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        dobLabel.setForeground(PRIMARY_DARK);
        fieldsPanel.add(dobLabel, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        dobField = new JTextField();
        dobField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dobField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        fieldsPanel.add(dobField, gbc);

        // Contact Number
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel contactLabel = new JLabel("Contact Number:");
        contactLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        contactLabel.setForeground(PRIMARY_DARK);
        fieldsPanel.add(contactLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        contactNumberField = new JTextField();
        contactNumberField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        contactNumberField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        fieldsPanel.add(contactNumberField, gbc);

        // Email (already in common fields, but we'll show it again for patient)
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        emailLabel.setForeground(PRIMARY_DARK);
        fieldsPanel.add(emailLabel, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        JTextField patientEmailField = new JTextField();
        patientEmailField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        patientEmailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        fieldsPanel.add(patientEmailField, gbc);

        // Address
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        addressLabel.setForeground(PRIMARY_DARK);
        fieldsPanel.add(addressLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        gbc.weightx = 0.8;
        addressArea = new JTextArea(3, 20);
        addressArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        addressArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        JScrollPane addressScroll = new JScrollPane(addressArea);
        addressScroll.setPreferredSize(new Dimension(300, 60));
        fieldsPanel.add(addressScroll, gbc);

        // Emergency Contact
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel emergencyLabel = new JLabel("Emergency Contact:");
        emergencyLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        emergencyLabel.setForeground(PRIMARY_DARK);
        fieldsPanel.add(emergencyLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        emergencyContactField = new JTextField();
        emergencyContactField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        emergencyContactField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        fieldsPanel.add(emergencyContactField, gbc);

        // Emergency Phone
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel emergencyPhoneLabel = new JLabel("Emergency Phone:");
        emergencyPhoneLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        emergencyPhoneLabel.setForeground(PRIMARY_DARK);
        fieldsPanel.add(emergencyPhoneLabel, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        emergencyPhoneField = new JTextField();
        emergencyPhoneField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        emergencyPhoneField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        fieldsPanel.add(emergencyPhoneField, gbc);

        // Medical History
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel historyLabel = new JLabel("Medical History:");
        historyLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        historyLabel.setForeground(PRIMARY_DARK);
        fieldsPanel.add(historyLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.gridwidth = 3;
        gbc.weightx = 0.8;
        medicalHistoryArea = new JTextArea(3, 20);
        medicalHistoryArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        medicalHistoryArea.setLineWrap(true);
        medicalHistoryArea.setWrapStyleWord(true);
        medicalHistoryArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        JScrollPane historyScroll = new JScrollPane(medicalHistoryArea);
        historyScroll.setPreferredSize(new Dimension(300, 60));
        fieldsPanel.add(historyScroll, gbc);

        // Allergies
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel allergiesLabel = new JLabel("Allergies:");
        allergiesLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        allergiesLabel.setForeground(PRIMARY_DARK);
        fieldsPanel.add(allergiesLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.gridwidth = 3;
        gbc.weightx = 0.8;
        allergiesArea = new JTextArea(3, 20);
        allergiesArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        allergiesArea.setLineWrap(true);
        allergiesArea.setWrapStyleWord(true);
        allergiesArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        JScrollPane allergiesScroll = new JScrollPane(allergiesArea);
        allergiesScroll.setPreferredSize(new Dimension(300, 60));
        fieldsPanel.add(allergiesScroll, gbc);

        panel.add(fieldsPanel);
        return panel;
    }

    private JPanel createStaffFields() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(MINT, 1),
            "Staff Information",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            PRIMARY_DARK
        ));

        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBackground(Color.WHITE);
        fieldsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

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
        fieldsPanel.add(firstNameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        firstNameField = new JTextField();
        firstNameField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        firstNameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        fieldsPanel.add(firstNameField, gbc);

        // Last Name
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel lastNameLabel = new JLabel("Last Name:");
        lastNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lastNameLabel.setForeground(PRIMARY_DARK);
        fieldsPanel.add(lastNameLabel, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        lastNameField = new JTextField();
        lastNameField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lastNameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        fieldsPanel.add(lastNameField, gbc);

        // Position
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel positionLabel = new JLabel("Position:");
        positionLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        positionLabel.setForeground(PRIMARY_DARK);
        fieldsPanel.add(positionLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        positionField = new JTextField();
        positionField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        positionField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        fieldsPanel.add(positionField, gbc);

        // Department
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel departmentLabel = new JLabel("Department:");
        departmentLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        departmentLabel.setForeground(PRIMARY_DARK);
        fieldsPanel.add(departmentLabel, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        departmentField = new JTextField();
        departmentField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        departmentField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        fieldsPanel.add(departmentField, gbc);

        // Phone
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        phoneLabel.setForeground(PRIMARY_DARK);
        fieldsPanel.add(phoneLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        phoneField = new JTextField();
        phoneField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        phoneField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        fieldsPanel.add(phoneField, gbc);

        // Hire Date
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel hireDateLabel = new JLabel("Hire Date:");
        hireDateLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        hireDateLabel.setForeground(PRIMARY_DARK);
        fieldsPanel.add(hireDateLabel, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        hireDateField = new JTextField();
        hireDateField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        hireDateField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        fieldsPanel.add(hireDateField, gbc);

        // Salary
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel salaryLabel = new JLabel("Salary:");
        salaryLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        salaryLabel.setForeground(PRIMARY_DARK);
        fieldsPanel.add(salaryLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 0.8;
        salaryField = new JTextField();
        salaryField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        salaryField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        fieldsPanel.add(salaryField, gbc);

        panel.add(fieldsPanel);
        return panel;
    }

    private JPanel createDentistFields() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(MINT, 1),
            "Dentist Information",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            PRIMARY_DARK
        ));

        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBackground(Color.WHITE);
        fieldsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

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
        fieldsPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 0.8;
        dentistNameField = new JTextField();
        dentistNameField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dentistNameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        fieldsPanel.add(dentistNameField, gbc);

        // Specialization
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel specLabel = new JLabel("Specialization:");
        specLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        specLabel.setForeground(PRIMARY_DARK);
        fieldsPanel.add(specLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        specializationField = new JTextField();
        specializationField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        specializationField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        fieldsPanel.add(specializationField, gbc);

        // License Number
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel licenseLabel = new JLabel("License Number:");
        licenseLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        licenseLabel.setForeground(PRIMARY_DARK);
        fieldsPanel.add(licenseLabel, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        licenseNumberField = new JTextField();
        licenseNumberField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        licenseNumberField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        licenseNumberField.setEditable(false);
        fieldsPanel.add(licenseNumberField, gbc);

        // Working Hours
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel hoursLabel = new JLabel("Working Hours:");
        hoursLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        hoursLabel.setForeground(PRIMARY_DARK);
        fieldsPanel.add(hoursLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        workingHoursField = new JTextField();
        workingHoursField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        workingHoursField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        fieldsPanel.add(workingHoursField, gbc);

        // Phone
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        phoneLabel.setForeground(PRIMARY_DARK);
        fieldsPanel.add(phoneLabel, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        dentistPhoneField = new JTextField();
        dentistPhoneField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dentistPhoneField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        fieldsPanel.add(dentistPhoneField, gbc);

        // Experience
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel expLabel = new JLabel("Years of Experience:");
        expLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        expLabel.setForeground(PRIMARY_DARK);
        fieldsPanel.add(expLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        experienceField = new JTextField();
        experienceField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        experienceField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        fieldsPanel.add(experienceField, gbc);

        // Consultation Fee
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel feeLabel = new JLabel("Consultation Fee:");
        feeLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        feeLabel.setForeground(PRIMARY_DARK);
        fieldsPanel.add(feeLabel, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        consultationFeeField = new JTextField();
        consultationFeeField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        consultationFeeField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        fieldsPanel.add(consultationFeeField, gbc);

        // Availability
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel availLabel = new JLabel("Available:");
        availLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        availLabel.setForeground(PRIMARY_DARK);
        fieldsPanel.add(availLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 0.8;
        availableCheckBox = new JCheckBox("Available for appointments");
        availableCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        fieldsPanel.add(availableCheckBox, gbc);

        panel.add(fieldsPanel);
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBackground(Color.WHITE);

        saveButton = new JButton("Save Changes");
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        saveButton.setBackground(PRIMARY_DARK);
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        saveButton.addActionListener(e -> saveProfile());

        cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cancelButton.setBackground(LIGHT_SURFACE);
        cancelButton.setForeground(PRIMARY_DARK);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        cancelButton.addActionListener(e -> dispose());

        panel.add(saveButton);
        panel.add(cancelButton);
        return panel;
    }

    private void loadData() {
        // Load common data
        emailField.setText(currentUser.getEmail());

        // Load role-specific data
        if (profileData != null) {
            switch (currentUser.getRole()) {
                case PATIENT:
                    patientNameField.setText((String) profileData.getOrDefault("patientName", ""));
                    genderCombo.setSelectedItem((String) profileData.getOrDefault("gender", "Other"));
                    addressArea.setText((String) profileData.getOrDefault("address", ""));
                    contactNumberField.setText((String) profileData.getOrDefault("contactNumber", ""));
                    dobField.setText((String) profileData.getOrDefault("dateOfBirth", ""));
                    emergencyContactField.setText((String) profileData.getOrDefault("emergencyContact", ""));
                    emergencyPhoneField.setText((String) profileData.getOrDefault("emergencyPhone", ""));
                    medicalHistoryArea.setText((String) profileData.getOrDefault("medicalHistory", ""));
                    allergiesArea.setText((String) profileData.getOrDefault("allergies", ""));
                    break;
                    
                case RECEPTION:
                    firstNameField.setText((String) profileData.getOrDefault("firstName", ""));
                    lastNameField.setText((String) profileData.getOrDefault("lastName", ""));
                    positionField.setText((String) profileData.getOrDefault("position", ""));
                    departmentField.setText((String) profileData.getOrDefault("department", ""));
                    phoneField.setText((String) profileData.getOrDefault("phone", ""));
                    hireDateField.setText((String) profileData.getOrDefault("hireDate", ""));
                    salaryField.setText(String.valueOf(profileData.getOrDefault("salary", 0)));
                    break;
                    
                case DENTIST:
                    dentistNameField.setText((String) profileData.getOrDefault("dentistName", ""));
                    specializationField.setText((String) profileData.getOrDefault("specialization", ""));
                    licenseNumberField.setText((String) profileData.getOrDefault("licenseNumber", ""));
                    workingHoursField.setText((String) profileData.getOrDefault("workingHours", ""));
                    dentistPhoneField.setText((String) profileData.getOrDefault("phone", ""));
                    experienceField.setText(String.valueOf(profileData.getOrDefault("yearsOfExperience", 0)));
                    consultationFeeField.setText(String.valueOf(profileData.getOrDefault("consultationFee", 0)));
                    availableCheckBox.setSelected((Boolean) profileData.getOrDefault("isAvailable", true));
                    break;
            }
        }
    }

    private void saveProfile() {
        // Validate email
        String email = emailField.getText().trim();
        if (!email.isEmpty() && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showError("Please enter a valid email address.");
            return;
        }

        // Save common data (email)
        boolean success = controller.updateUserEmail(currentUser.getUserId(), email);
        
        if (!success) {
            showError("Failed to update email.");
            return;
        }

        // Save role-specific data
        switch (currentUser.getRole()) {
            case PATIENT:
                success = controller.updatePatientProfile(currentUser.getUserId(), collectPatientData());
                break;
            case RECEPTION:
                success = controller.updateStaffProfile(currentUser.getUserId(), collectStaffData());
                break;
            case DENTIST:
                success = controller.updateDentistProfile(currentUser.getUserId(), collectDentistData());
                break;
            case ADMIN:
                success = true; // No additional profile for admin
                break;
        }

        if (success) {
            showSuccess("Profile updated successfully!");
            dispose();
        } else {
            showError("Failed to update profile. Please try again.");
        }
    }

    private Map<String, Object> collectPatientData() {
        Map<String, Object> data = new java.util.HashMap<>();
        data.put("patientName", patientNameField.getText().trim());
        data.put("gender", genderCombo.getSelectedItem());
        data.put("address", addressArea.getText().trim());
        data.put("contactNumber", contactNumberField.getText().trim());
        data.put("email", emailField.getText().trim());
        data.put("dateOfBirth", dobField.getText().trim());
        data.put("emergencyContact", emergencyContactField.getText().trim());
        data.put("emergencyPhone", emergencyPhoneField.getText().trim());
        data.put("medicalHistory", medicalHistoryArea.getText().trim());
        data.put("allergies", allergiesArea.getText().trim());
        return data;
    }

    private Map<String, Object> collectStaffData() {
        Map<String, Object> data = new java.util.HashMap<>();
        data.put("firstName", firstNameField.getText().trim());
        data.put("lastName", lastNameField.getText().trim());
        data.put("position", positionField.getText().trim());
        data.put("department", departmentField.getText().trim());
        data.put("phone", phoneField.getText().trim());
        data.put("email", emailField.getText().trim());
        data.put("hireDate", hireDateField.getText().trim());
        try {
            data.put("salary", Double.parseDouble(salaryField.getText().trim()));
        } catch (NumberFormatException e) {
            data.put("salary", 0.0);
        }
        return data;
    }

    private Map<String, Object> collectDentistData() {
        Map<String, Object> data = new java.util.HashMap<>();
        data.put("dentistName", dentistNameField.getText().trim());
        data.put("specialization", specializationField.getText().trim());
        data.put("workingHours", workingHoursField.getText().trim());
        data.put("phone", dentistPhoneField.getText().trim());
        data.put("email", emailField.getText().trim());
        try {
            data.put("yearsOfExperience", Integer.parseInt(experienceField.getText().trim()));
        } catch (NumberFormatException e) {
            data.put("yearsOfExperience", 0);
        }
        try {
            data.put("consultationFee", Double.parseDouble(consultationFeeField.getText().trim()));
        } catch (NumberFormatException e) {
            data.put("consultationFee", 0.0);
        }
        data.put("isAvailable", availableCheckBox.isSelected());
        return data;
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