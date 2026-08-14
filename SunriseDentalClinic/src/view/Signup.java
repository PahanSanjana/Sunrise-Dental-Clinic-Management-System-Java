package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class Signup extends javax.swing.JFrame {
    
    // Color Palette - Modern & Minimal
    private static final Color PRIMARY_DARK = new Color(0x2F3E3C);
    private static final Color PRIMARY_LIGHT = new Color(0x4A6A65);
    private static final Color MINT = new Color(0xBDDBD1);
    private static final Color MINT_LIGHT = new Color(0xE8F5F0);
    private static final Color SOFT_SURFACE = new Color(0xFBF9F1);
    private static final Color LIGHT_SURFACE = new Color(0xE7E9E3);
    private static final Color SECONDARY_TEXT = new Color(107, 123, 121);
    private static final Color ERROR_COLOR = new Color(220, 80, 80);
    private static final Color SUCCESS_COLOR = new Color(60, 160, 80);
    private static final Color CARD_BG = new Color(255, 255, 255);

    // Form Fields - Full Patient Details
    private JTextField usernameField;
    private JTextField fullNameField;
    private JComboBox<String> genderCombo;
    private JTextField dobField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextArea addressArea;
    private JTextField emergencyContactField;
    private JTextField emergencyPhoneField;
    private JTextArea medicalHistoryArea;
    private JTextArea allergiesArea;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    
    private RoundedButton signupButton;
    private RoundedButton cancelButton;
    private JButton loginLinkButton;
    private JLabel messageLabel;

    // Frame size matching MainFrame
    private static final int FRAME_WIDTH = 1400;
    private static final int FRAME_HEIGHT = 820;

    public Signup() {
        initComponents();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setTitle("SUNRISE DENTAL - Create Account");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setMinimumSize(new Dimension(1100, 700));
        setResizable(true);

        // Main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(SOFT_SURFACE);

        // Create the split panel
        JPanel splitPanel = new JPanel(new GridLayout(1, 2));
        splitPanel.setBackground(SOFT_SURFACE);

        // Left Panel - Signup Form (60% width)
        splitPanel.add(createSignupPanel());
        
        // Right Panel - Welcome Branding (40% width)
        splitPanel.add(createWelcomePanel());

        mainPanel.add(splitPanel, BorderLayout.CENTER);
        setContentPane(mainPanel);
    }

    // =====================================================
    // LEFT PANEL - Signup Form (Professional Layout)
    // =====================================================
    private JPanel createSignupPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(SOFT_SURFACE);
        panel.setBorder(new EmptyBorder(20, 40, 20, 40));

        // Create the form panel with proper layout
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(SOFT_SURFACE);
        formPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

        // =============================================
        // HEADER SECTION
        // =============================================
        
        // Logo + Brand
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        brandPanel.setOpaque(false);
        brandPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel logoLabel = createLogoLabel();
        brandPanel.add(logoLabel);
        
        JLabel brandTitle = new JLabel("SUNRISE DENTAL");
        brandTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        brandTitle.setForeground(PRIMARY_DARK);
        brandPanel.add(brandTitle);
        
        formPanel.add(brandPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        // Title
        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(PRIMARY_DARK);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(titleLabel);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Sign up as a new patient");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(SECONDARY_TEXT);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(subtitleLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        // =============================================
        // FORM FIELDS CONTAINER
        // =============================================
        
        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        fieldsPanel.setBackground(Color.WHITE);
        fieldsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            new EmptyBorder(15, 20, 15, 20)
        ));
        fieldsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldsPanel.setMaximumSize(new Dimension(500, Integer.MAX_VALUE));

        int fieldHeight = 34;
        int labelGap = 2;
        int fieldGap = 8;

        // =============================================
        // LOGIN CREDENTIALS SECTION
        // =============================================
        
        // Section Label
        JLabel loginSectionLabel = new JLabel("LOGIN CREDENTIALS");
        loginSectionLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        loginSectionLabel.setForeground(PRIMARY_LIGHT);
        loginSectionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldsPanel.add(loginSectionLabel);
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        // Username
        JLabel userLabel = new JLabel("Username *");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        userLabel.setForeground(PRIMARY_DARK);
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldsPanel.add(userLabel);
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, labelGap)));

        usernameField = createStyledTextField();
        usernameField.setMaximumSize(new Dimension(460, fieldHeight));
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldsPanel.add(usernameField);
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, fieldGap)));

        // Password
        JLabel passLabelTitle = new JLabel("Password *");
        passLabelTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        passLabelTitle.setForeground(PRIMARY_DARK);
        passLabelTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldsPanel.add(passLabelTitle);
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, labelGap)));

        passwordField = createStyledPasswordField();
        passwordField.setMaximumSize(new Dimension(460, fieldHeight));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldsPanel.add(passwordField);
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, fieldGap)));

        // Confirm Password
        JLabel confirmLabelTitle = new JLabel("Confirm Password *");
        confirmLabelTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        confirmLabelTitle.setForeground(PRIMARY_DARK);
        confirmLabelTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldsPanel.add(confirmLabelTitle);
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, labelGap)));

        confirmPasswordField = createStyledPasswordField();
        confirmPasswordField.setMaximumSize(new Dimension(460, fieldHeight));
        confirmPasswordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldsPanel.add(confirmPasswordField);
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, fieldGap)));

        // =============================================
        // PERSONAL INFORMATION SECTION
        // =============================================
        
        // Section Label
        JLabel personalSectionLabel = new JLabel("PERSONAL INFORMATION");
        personalSectionLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        personalSectionLabel.setForeground(PRIMARY_LIGHT);
        personalSectionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldsPanel.add(personalSectionLabel);
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        // Full Name
        JLabel nameLabel = new JLabel("Full Name *");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        nameLabel.setForeground(PRIMARY_DARK);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldsPanel.add(nameLabel);
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, labelGap)));

        fullNameField = createStyledTextField();
        fullNameField.setMaximumSize(new Dimension(460, fieldHeight));
        fullNameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldsPanel.add(fullNameField);
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, fieldGap)));

        // Gender + DOB in one row
        JPanel rowPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        rowPanel.setOpaque(false);
        rowPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        rowPanel.setMaximumSize(new Dimension(460, fieldHeight + 20));
        
        JPanel genderPanel = new JPanel(new BorderLayout());
        genderPanel.setOpaque(false);
        JLabel genderLabel = new JLabel("Gender");
        genderLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        genderLabel.setForeground(PRIMARY_DARK);
        genderPanel.add(genderLabel, BorderLayout.NORTH);
        
        genderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        genderCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        genderCombo.setPreferredSize(new Dimension(180, fieldHeight));
        genderPanel.add(genderCombo, BorderLayout.CENTER);
        
        JPanel dobPanel = new JPanel(new BorderLayout());
        dobPanel.setOpaque(false);
        JLabel dobLabel = new JLabel("Date of Birth *");
        dobLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        dobLabel.setForeground(PRIMARY_DARK);
        dobPanel.add(dobLabel, BorderLayout.NORTH);
        
        dobField = createStyledTextField();
        dobField.setPreferredSize(new Dimension(180, fieldHeight));
        dobField.setToolTipText("YYYY-MM-DD");
        dobPanel.add(dobField, BorderLayout.CENTER);
        
        rowPanel.add(genderPanel);
        rowPanel.add(dobPanel);
        fieldsPanel.add(rowPanel);
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, fieldGap)));

        // =============================================
        // CONTACT INFORMATION SECTION
        // =============================================
        
        // Section Label
        JLabel contactSectionLabel = new JLabel("CONTACT INFORMATION");
        contactSectionLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        contactSectionLabel.setForeground(PRIMARY_LIGHT);
        contactSectionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldsPanel.add(contactSectionLabel);
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        // Email
        JLabel emailLabelTitle = new JLabel("Email");
        emailLabelTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        emailLabelTitle.setForeground(PRIMARY_DARK);
        emailLabelTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldsPanel.add(emailLabelTitle);
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, labelGap)));

        emailField = createStyledTextField();
        emailField.setMaximumSize(new Dimension(460, fieldHeight));
        emailField.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldsPanel.add(emailField);
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, fieldGap)));

        // Phone
        JLabel phoneLabelTitle = new JLabel("Phone Number *");
        phoneLabelTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        phoneLabelTitle.setForeground(PRIMARY_DARK);
        phoneLabelTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldsPanel.add(phoneLabelTitle);
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, labelGap)));

        phoneField = createStyledTextField();
        phoneField.setMaximumSize(new Dimension(460, fieldHeight));
        phoneField.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldsPanel.add(phoneField);
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, fieldGap)));

        // Address
        JLabel addressLabelTitle = new JLabel("Address");
        addressLabelTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addressLabelTitle.setForeground(PRIMARY_DARK);
        addressLabelTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldsPanel.add(addressLabelTitle);
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, labelGap)));

        addressArea = createStyledTextArea(2);
        addressArea.setMaximumSize(new Dimension(460, 50));
        addressArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        JScrollPane addressScroll = new JScrollPane(addressArea);
        addressScroll.setMaximumSize(new Dimension(460, 50));
        addressScroll.setBorder(BorderFactory.createLineBorder(LIGHT_SURFACE, 1));
        fieldsPanel.add(addressScroll);
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, fieldGap)));

        // =============================================
        // EMERGENCY CONTACT SECTION
        // =============================================
        
        // Section Label
        JLabel emergencySectionLabel = new JLabel("EMERGENCY CONTACT");
        emergencySectionLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        emergencySectionLabel.setForeground(PRIMARY_LIGHT);
        emergencySectionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldsPanel.add(emergencySectionLabel);
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        // Emergency Contact Name
        JLabel emergencyLabel = new JLabel("Contact Name");
        emergencyLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        emergencyLabel.setForeground(PRIMARY_DARK);
        emergencyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldsPanel.add(emergencyLabel);
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, labelGap)));

        emergencyContactField = createStyledTextField();
        emergencyContactField.setMaximumSize(new Dimension(460, fieldHeight));
        emergencyContactField.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldsPanel.add(emergencyContactField);
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, fieldGap)));

        // Emergency Phone
        JLabel emergencyPhoneLabel = new JLabel("Contact Phone");
        emergencyPhoneLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        emergencyPhoneLabel.setForeground(PRIMARY_DARK);
        emergencyPhoneLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldsPanel.add(emergencyPhoneLabel);
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, labelGap)));

        emergencyPhoneField = createStyledTextField();
        emergencyPhoneField.setMaximumSize(new Dimension(460, fieldHeight));
        emergencyPhoneField.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldsPanel.add(emergencyPhoneField);
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, fieldGap)));

        // =============================================
        // MEDICAL INFORMATION SECTION
        // =============================================
        
        // Section Label
        JLabel medicalSectionLabel = new JLabel("MEDICAL INFORMATION");
        medicalSectionLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        medicalSectionLabel.setForeground(PRIMARY_LIGHT);
        medicalSectionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldsPanel.add(medicalSectionLabel);
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        // Medical History
        JLabel medicalLabel = new JLabel("Medical History");
        medicalLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        medicalLabel.setForeground(PRIMARY_DARK);
        medicalLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldsPanel.add(medicalLabel);
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, labelGap)));

        medicalHistoryArea = createStyledTextArea(2);
        medicalHistoryArea.setMaximumSize(new Dimension(460, 40));
        medicalHistoryArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        JScrollPane medicalScroll = new JScrollPane(medicalHistoryArea);
        medicalScroll.setMaximumSize(new Dimension(460, 40));
        medicalScroll.setBorder(BorderFactory.createLineBorder(LIGHT_SURFACE, 1));
        fieldsPanel.add(medicalScroll);
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, fieldGap)));

        // Allergies
        JLabel allergiesLabelTitle = new JLabel("Allergies");
        allergiesLabelTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        allergiesLabelTitle.setForeground(PRIMARY_DARK);
        allergiesLabelTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldsPanel.add(allergiesLabelTitle);
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, labelGap)));

        allergiesArea = createStyledTextArea(2);
        allergiesArea.setMaximumSize(new Dimension(460, 40));
        allergiesArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        JScrollPane allergiesScroll = new JScrollPane(allergiesArea);
        allergiesScroll.setMaximumSize(new Dimension(460, 40));
        allergiesScroll.setBorder(BorderFactory.createLineBorder(LIGHT_SURFACE, 1));
        fieldsPanel.add(allergiesScroll);
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, fieldGap)));

        // =============================================
        // MESSAGE & BUTTONS
        // =============================================
        
        // Message
        messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        messageLabel.setForeground(ERROR_COLOR);
        messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldsPanel.add(messageLabel);
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, 8)));

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        signupButton = createRoundedButton("Sign Up", PRIMARY_DARK, Color.WHITE);
        signupButton.setPreferredSize(new Dimension(150, 44));
        
        cancelButton = createRoundedButton("Cancel", SOFT_SURFACE, PRIMARY_DARK);
        cancelButton.setBorderColor(MINT);
        cancelButton.setPreferredSize(new Dimension(150, 44));

        buttonPanel.add(signupButton);
        buttonPanel.add(cancelButton);
        fieldsPanel.add(buttonPanel);
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, 8)));

        // Login Link
        JPanel loginLinkPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        loginLinkPanel.setOpaque(false);
        loginLinkPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel alreadyHaveLabel = new JLabel("Already have an account?");
        alreadyHaveLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        alreadyHaveLabel.setForeground(SECONDARY_TEXT);
        loginLinkPanel.add(alreadyHaveLabel);

        loginLinkButton = new JButton("Login here");
        loginLinkButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        loginLinkButton.setForeground(PRIMARY_DARK);
        loginLinkButton.setBorderPainted(false);
        loginLinkButton.setContentAreaFilled(false);
        loginLinkButton.setFocusPainted(false);
        loginLinkButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginLinkPanel.add(loginLinkButton);

        fieldsPanel.add(loginLinkPanel);

        // Add fields panel to scroll pane
        JScrollPane scrollPane = new JScrollPane(fieldsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // =====================================================
    // RIGHT PANEL - Welcome Branding
    // =====================================================
    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY_DARK);
        panel.setBorder(new EmptyBorder(40, 40, 40, 40));

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        try {
            File imageFile = new File("src/resources/Welcome.png");
            if (imageFile.exists()) {
                BufferedImage img = ImageIO.read(imageFile);
                if (img != null) {
                    int imgWidth = img.getWidth();
                    int imgHeight = img.getHeight();
                    double aspectRatio = (double) imgWidth / imgHeight;
                    
                    int targetWidth = 480;
                    int targetHeight = (int) (targetWidth / aspectRatio);
                    
                    if (targetHeight > 380) {
                        targetHeight = 380;
                        targetWidth = (int) (targetHeight * aspectRatio);
                    }
                    
                    Image scaled = img.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
                    imageLabel.setIcon(new ImageIcon(scaled));
                }
            } else {
                imageLabel.setText("🦷");
                imageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 80));
                imageLabel.setForeground(Color.WHITE);
            }
        } catch (Exception e) {
            imageLabel.setText("🦷");
            imageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 80));
            imageLabel.setForeground(Color.WHITE);
        }
        
        centerPanel.add(imageLabel);
        panel.add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        JLabel welcomeText = new JLabel(
            "<html><div style='text-align:center;color:white;font-size:20px;font-weight:300;'>"
            + "Start Your Journey to<br><span style='font-weight:600;'>Better Dental Health</span>"
            + "</div></html>"
        );
        welcomeText.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeText.setAlignmentX(Component.CENTER_ALIGNMENT);
        bottomPanel.add(welcomeText);
        bottomPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        JLabel subText = new JLabel(
            "<html><div style='text-align:center;color:rgba(255,255,255,0.6);font-size:13px;'>"
            + "Create your account to book appointments<br>and manage your dental health"
            + "</div></html>"
        );
        subText.setHorizontalAlignment(SwingConstants.CENTER);
        subText.setAlignmentX(Component.CENTER_ALIGNMENT);
        bottomPanel.add(subText);
        
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    // =====================================================
    // HELPER METHODS
    // =====================================================
    
    private JLabel createLogoLabel() {
        JLabel logo = new JLabel();
        try {
            File logoFile = new File("src/resources/Remove Bg light.png");
            if (logoFile.exists()) {
                BufferedImage img = ImageIO.read(logoFile);
                if (img != null) {
                    int imgWidth = img.getWidth();
                    int imgHeight = img.getHeight();
                    double aspectRatio = (double) imgWidth / imgHeight;
                    
                    int targetHeight = 40;
                    int targetWidth = (int) (targetHeight * aspectRatio);
                    
                    Image scaled = img.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
                    logo.setIcon(new ImageIcon(scaled));
                }
            } else {
                logo.setText("🦷");
                logo.setFont(new Font("Segoe UI", Font.PLAIN, 28));
            }
        } catch (Exception e) {
            logo.setText("🦷");
            logo.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        }
        return logo;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setBackground(Color.WHITE);
        field.setOpaque(true);
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setBackground(Color.WHITE);
        field.setOpaque(true);
        return field;
    }

    private JTextArea createStyledTextArea(int rows) {
        JTextArea area = new JTextArea(rows, 20);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        area.setBackground(Color.WHITE);
        return area;
    }

    private RoundedButton createRoundedButton(String text, Color bg, Color fg) {
        return new RoundedButton(text, bg, fg);
    }

    // =====================================================
    // ROUNDED BUTTON
    // =====================================================
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
            this.hoverColor = bg;

            setForeground(fg);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setPreferredSize(new Dimension(150, 44));
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
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);

            if (borderColor != bg && borderColor != null) {
                g2.setColor(borderColor);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 24, 24);
            }

            super.paintComponent(g);
            g2.dispose();
        }
    }

    // =====================================================
    // PUBLIC METHODS FOR CONTROLLER
    // =====================================================
    
    // Login credentials
    public String getUsername() {
        return usernameField.getText().trim();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public String getConfirmPassword() {
        return new String(confirmPasswordField.getPassword());
    }

    // Patient details
    public String getFullName() {
        return fullNameField.getText().trim();
    }

    public String getGender() {
        return (String) genderCombo.getSelectedItem();
    }

    public String getDateOfBirth() {
        return dobField.getText().trim();
    }

    public String getEmail() {
        return emailField.getText().trim();
    }

    public String getPhone() {
        return phoneField.getText().trim();
    }

    public String getAddress() {
        return addressArea.getText().trim();
    }

    public String getEmergencyContact() {
        return emergencyContactField.getText().trim();
    }

    public String getEmergencyPhone() {
        return emergencyPhoneField.getText().trim();
    }

    public String getMedicalHistory() {
        return medicalHistoryArea.getText().trim();
    }

    public String getAllergies() {
        return allergiesArea.getText().trim();
    }

    public void clearFields() {
        usernameField.setText("");
        fullNameField.setText("");
        genderCombo.setSelectedIndex(0);
        dobField.setText("");
        emailField.setText("");
        phoneField.setText("");
        addressArea.setText("");
        emergencyContactField.setText("");
        emergencyPhoneField.setText("");
        medicalHistoryArea.setText("");
        allergiesArea.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        messageLabel.setText(" ");
    }

    public void showError(String message) {
        messageLabel.setText("⚠ " + message);
        messageLabel.setForeground(ERROR_COLOR);
    }

    public void showSuccess(String message) {
        messageLabel.setText("✓ " + message);
        messageLabel.setForeground(SUCCESS_COLOR);
    }

    public void addSignupListener(ActionListener listener) {
        signupButton.addActionListener(listener);
    }

    public void addCancelListener(ActionListener listener) {
        cancelButton.addActionListener(listener);
    }

    public void addLoginLinkListener(ActionListener listener) {
        loginLinkButton.addActionListener(listener);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new Signup().setVisible(true);
        });
    }
}