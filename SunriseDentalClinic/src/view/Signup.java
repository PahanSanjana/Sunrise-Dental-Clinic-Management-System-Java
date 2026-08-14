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

    private JTextField usernameField;
    private JTextField fullNameField;
    private JTextField emailField;
    private JTextField phoneField;
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

        // Main panel with GridBagLayout for perfect centering
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(SOFT_SURFACE);
        mainPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

        GridBagConstraints mainGbc = new GridBagConstraints();
        mainGbc.fill = GridBagConstraints.BOTH;
        mainGbc.weightx = 1.0;
        mainGbc.weighty = 1.0;

        // Create the split panel
        JPanel splitPanel = new JPanel(new GridLayout(1, 2, 0, 0));
        splitPanel.setBackground(SOFT_SURFACE);

        // Left Panel - Signup Form (60% width)
        splitPanel.add(createSignupPanel());
        
        // Right Panel - Welcome Branding (40% width)
        splitPanel.add(createWelcomePanel());

        mainPanel.add(splitPanel, mainGbc);
        setContentPane(mainPanel);
    }

    // =====================================================
    // LEFT PANEL - Signup Form (Professional Layout)
    // =====================================================
    private JPanel createSignupPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(SOFT_SURFACE);
        panel.setLayout(new GridBagLayout());
        panel.setBorder(new EmptyBorder(35, 60, 35, 60));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 0, 0);

        // Row 0: Logo + Brand
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 6, 0);
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        brandPanel.setOpaque(false);
        
        JLabel logoLabel = createLogoLabel();
        brandPanel.add(logoLabel);
        
        JLabel brandTitle = new JLabel("SUNRISE DENTAL");
        brandTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        brandTitle.setForeground(PRIMARY_DARK);
        brandPanel.add(brandTitle);
        
        panel.add(brandPanel, gbc);

        // Row 1: Title
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 2, 0);
        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_DARK);
        panel.add(titleLabel, gbc);

        // Row 2: Subtitle
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 20, 0);
        JLabel subtitleLabel = new JLabel("Sign up as a new patient");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(SECONDARY_TEXT);
        panel.add(subtitleLabel, gbc);

        // Row 3: Form Fields Container
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 0, 0);
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

        GridBagConstraints fGbc = new GridBagConstraints();
        fGbc.gridx = 0;
        fGbc.fill = GridBagConstraints.HORIZONTAL;
        fGbc.anchor = GridBagConstraints.WEST;
        fGbc.weightx = 1.0;

        int row = 0;
        
        // Username
        fGbc.gridy = row++;
        fGbc.insets = new Insets(0, 0, 4, 0);
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        userLabel.setForeground(PRIMARY_DARK);
        formPanel.add(userLabel, fGbc);

        fGbc.gridy = row++;
        fGbc.insets = new Insets(0, 0, 12, 0);
        usernameField = createStyledTextField();
        usernameField.setPreferredSize(new Dimension(400, 42));
        usernameField.setMinimumSize(new Dimension(300, 42));
        formPanel.add(usernameField, fGbc);

        // Full Name
        fGbc.gridy = row++;
        fGbc.insets = new Insets(0, 0, 4, 0);
        JLabel nameLabel = new JLabel("Full Name");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nameLabel.setForeground(PRIMARY_DARK);
        formPanel.add(nameLabel, fGbc);

        fGbc.gridy = row++;
        fGbc.insets = new Insets(0, 0, 12, 0);
        fullNameField = createStyledTextField();
        fullNameField.setPreferredSize(new Dimension(400, 42));
        fullNameField.setMinimumSize(new Dimension(300, 42));
        formPanel.add(fullNameField, fGbc);

        // Email
        fGbc.gridy = row++;
        fGbc.insets = new Insets(0, 0, 4, 0);
        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        emailLabel.setForeground(PRIMARY_DARK);
        formPanel.add(emailLabel, fGbc);

        fGbc.gridy = row++;
        fGbc.insets = new Insets(0, 0, 12, 0);
        emailField = createStyledTextField();
        emailField.setPreferredSize(new Dimension(400, 42));
        emailField.setMinimumSize(new Dimension(300, 42));
        formPanel.add(emailField, fGbc);

        // Phone
        fGbc.gridy = row++;
        fGbc.insets = new Insets(0, 0, 4, 0);
        JLabel phoneLabel = new JLabel("Phone Number");
        phoneLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        phoneLabel.setForeground(PRIMARY_DARK);
        formPanel.add(phoneLabel, fGbc);

        fGbc.gridy = row++;
        fGbc.insets = new Insets(0, 0, 12, 0);
        phoneField = createStyledTextField();
        phoneField.setPreferredSize(new Dimension(400, 42));
        phoneField.setMinimumSize(new Dimension(300, 42));
        formPanel.add(phoneField, fGbc);

        // Password
        fGbc.gridy = row++;
        fGbc.insets = new Insets(0, 0, 4, 0);
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        passLabel.setForeground(PRIMARY_DARK);
        formPanel.add(passLabel, fGbc);

        fGbc.gridy = row++;
        fGbc.insets = new Insets(0, 0, 12, 0);
        passwordField = createStyledPasswordField();
        passwordField.setPreferredSize(new Dimension(400, 42));
        passwordField.setMinimumSize(new Dimension(300, 42));
        formPanel.add(passwordField, fGbc);

        // Confirm Password
        fGbc.gridy = row++;
        fGbc.insets = new Insets(0, 0, 4, 0);
        JLabel confirmLabel = new JLabel("Confirm Password");
        confirmLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        confirmLabel.setForeground(PRIMARY_DARK);
        formPanel.add(confirmLabel, fGbc);

        fGbc.gridy = row++;
        fGbc.insets = new Insets(0, 0, 10, 0);
        confirmPasswordField = createStyledPasswordField();
        confirmPasswordField.setPreferredSize(new Dimension(400, 42));
        confirmPasswordField.setMinimumSize(new Dimension(300, 42));
        formPanel.add(confirmPasswordField, fGbc);

        // Message
        fGbc.gridy = row++;
        fGbc.insets = new Insets(0, 0, 14, 0);
        messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        messageLabel.setForeground(ERROR_COLOR);
        formPanel.add(messageLabel, fGbc);

        // Buttons
        fGbc.gridy = row++;
        fGbc.insets = new Insets(0, 0, 14, 0);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        buttonPanel.setOpaque(false);

        signupButton = createRoundedButton("Sign Up", PRIMARY_DARK, Color.WHITE);
        signupButton.setPreferredSize(new Dimension(150, 46));
        
        cancelButton = createRoundedButton("Cancel", SOFT_SURFACE, PRIMARY_DARK);
        cancelButton.setBorderColor(MINT);
        cancelButton.setPreferredSize(new Dimension(150, 46));

        buttonPanel.add(signupButton);
        buttonPanel.add(cancelButton);
        formPanel.add(buttonPanel, fGbc);

        // Login Link
        fGbc.gridy = row++;
        fGbc.insets = new Insets(0, 0, 0, 0);
        JPanel loginLinkPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        loginLinkPanel.setOpaque(false);

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

        formPanel.add(loginLinkPanel, fGbc);

        panel.add(formPanel, gbc);

        // Add vertical glue to push content to top
        gbc.gridy = 4;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(Box.createVerticalGlue(), gbc);

        return panel;
    }

    // =====================================================
    // RIGHT PANEL - Welcome Branding (Professional)
    // =====================================================
    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY_DARK);
        panel.setBorder(new EmptyBorder(40, 40, 40, 40));

        // Center - Image with proper scaling
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

        // Bottom - Text with proper spacing
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
                    
                    int targetHeight = 42;
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
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));
        field.setBackground(Color.WHITE);
        field.setOpaque(true);
        field.setPreferredSize(new Dimension(400, 42));
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));
        field.setBackground(Color.WHITE);
        field.setOpaque(true);
        field.setPreferredSize(new Dimension(400, 42));
        return field;
    }

    private RoundedButton createRoundedButton(String text, Color bg, Color fg) {
        return new RoundedButton(text, bg, fg);
    }

    // =====================================================
    // ROUNDED BUTTON - Modern
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
            setPreferredSize(new Dimension(150, 46));
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
    
    public String getUsername() {
        return usernameField.getText().trim();
    }

    public String getFullName() {
        return fullNameField.getText().trim();
    }

    public String getEmail() {
        return emailField.getText().trim();
    }

    public String getPhone() {
        return phoneField.getText().trim();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public String getConfirmPassword() {
        return new String(confirmPasswordField.getPassword());
    }

    public void clearFields() {
        usernameField.setText("");
        fullNameField.setText("");
        emailField.setText("");
        phoneField.setText("");
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