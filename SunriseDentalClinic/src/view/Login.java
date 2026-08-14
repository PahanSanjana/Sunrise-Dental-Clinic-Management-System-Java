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

public class Login extends javax.swing.JFrame {
    
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
    private JPasswordField passwordField;
    private RoundedButton loginButton;
    private RoundedButton cancelButton;
    private JButton signupLinkButton;
    private JLabel messageLabel;
    private JLabel roleDisplayLabel;

    // Frame size matching MainFrame
    private static final int FRAME_WIDTH = 1400;
    private static final int FRAME_HEIGHT = 820;

    public Login() {
        initComponents();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setTitle("SUNRISE DENTAL - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setMinimumSize(new Dimension(1100, 700));
        setResizable(true);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        mainPanel.setBackground(SOFT_SURFACE);

        // Left Panel - Login Form (60% width)
        mainPanel.add(createLoginPanel());
        
        // Right Panel - Welcome Branding (40% width)
        mainPanel.add(createWelcomePanel());

        setContentPane(mainPanel);
    }

    // =====================================================
    // LEFT PANEL - Login Form (Professional Layout)
    // =====================================================
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(SOFT_SURFACE);
        panel.setLayout(new GridBagLayout());
        panel.setBorder(new EmptyBorder(50, 70, 50, 70));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 0, 0);

        // Row 0: Logo
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 10, 0);
        JLabel logoLabel = createLogoLabel();
        panel.add(logoLabel, gbc);

        // Row 1: Brand Title
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 2, 0);
        JLabel titleLabel = new JLabel("SUNRISE DENTAL");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_DARK);
        panel.add(titleLabel, gbc);

        // Row 2: Subtitle
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 35, 0);
        JLabel subtitleLabel = new JLabel("Welcome Back");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(SECONDARY_TEXT);
        panel.add(subtitleLabel, gbc);

        // Row 3: Username Label
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 6, 0);
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        userLabel.setForeground(PRIMARY_DARK);
        panel.add(userLabel, gbc);

        // Row 4: Username Field
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 22, 0);
        usernameField = createStyledTextField();
        usernameField.setPreferredSize(new Dimension(420, 46));
        usernameField.setMinimumSize(new Dimension(350, 46));
        panel.add(usernameField, gbc);

        // Row 5: Password Label
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 6, 0);
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        passLabel.setForeground(PRIMARY_DARK);
        panel.add(passLabel, gbc);

        // Row 6: Password Field
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 12, 0);
        passwordField = createStyledPasswordField();
        passwordField.setPreferredSize(new Dimension(420, 46));
        passwordField.setMinimumSize(new Dimension(350, 46));
        panel.add(passwordField, gbc);

        // Row 7: Message
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, 18, 0);
        messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        messageLabel.setForeground(ERROR_COLOR);
        panel.add(messageLabel, gbc);

        // Row 8: Buttons Panel
        gbc.gridy = 8;
        gbc.insets = new Insets(0, 0, 18, 0);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        buttonPanel.setOpaque(false);

        loginButton = createRoundedButton("Login", PRIMARY_DARK, Color.WHITE);
        loginButton.setPreferredSize(new Dimension(160, 48));
        
        cancelButton = createRoundedButton("Cancel", SOFT_SURFACE, PRIMARY_DARK);
        cancelButton.setBorderColor(MINT);
        cancelButton.setPreferredSize(new Dimension(160, 48));

        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, gbc);

        // Row 9: Signup Link
        gbc.gridy = 9;
        gbc.insets = new Insets(0, 0, 12, 0);
        JPanel signupLinkPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        signupLinkPanel.setOpaque(false);

        JLabel noAccountLabel = new JLabel("Don't have an account?");
        noAccountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        noAccountLabel.setForeground(SECONDARY_TEXT);
        signupLinkPanel.add(noAccountLabel);

        signupLinkButton = new JButton("Sign up here");
        signupLinkButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        signupLinkButton.setForeground(PRIMARY_DARK);
        signupLinkButton.setBorderPainted(false);
        signupLinkButton.setContentAreaFilled(false);
        signupLinkButton.setFocusPainted(false);
        signupLinkButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signupLinkPanel.add(signupLinkButton);

        panel.add(signupLinkPanel, gbc);

        // Row 10: Role Display
        gbc.gridy = 10;
        gbc.insets = new Insets(0, 0, 0, 0);
        roleDisplayLabel = new JLabel(" ");
        roleDisplayLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        roleDisplayLabel.setForeground(SECONDARY_TEXT);
        panel.add(roleDisplayLabel, gbc);

        // Add vertical glue to push content to top
        gbc.gridy = 11;
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
                    // Scale image properly while maintaining aspect ratio
                    int targetWidth = 500;
                    int targetHeight = 400;
                    Image scaled = img.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
                    imageLabel.setIcon(new ImageIcon(scaled));
                }
            } else {
                // Fallback text
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
        bottomPanel.setBorder(new EmptyBorder(20, 0, 10, 0));
        
        JLabel welcomeText = new JLabel(
            "<html><div style='text-align:center;color:white;font-size:20px;font-weight:300;'>"
            + "Your Trusted Partner in<br><span style='font-weight:600;'>Dental Care Excellence</span>"
            + "</div></html>"
        );
        welcomeText.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeText.setAlignmentX(Component.CENTER_ALIGNMENT);
        bottomPanel.add(welcomeText);
        bottomPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        JLabel subText = new JLabel(
            "<html><div style='text-align:center;color:rgba(255,255,255,0.6);font-size:13px;'>"
            + "Secure Login Portal"
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
                    ImageIcon icon = new ImageIcon(img.getScaledInstance(55, 55, Image.SCALE_SMOOTH));
                    logo.setIcon(icon);
                }
            } else {
                logo.setText("🦷");
                logo.setFont(new Font("Segoe UI", Font.PLAIN, 36));
            }
        } catch (Exception e) {
            logo.setText("🦷");
            logo.setFont(new Font("Segoe UI", Font.PLAIN, 36));
        }
        return logo;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(10, 16, 10, 16)
        ));
        field.setBackground(Color.WHITE);
        field.setOpaque(true);
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(10, 16, 10, 16)
        ));
        field.setBackground(Color.WHITE);
        field.setOpaque(true);
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
            setFont(new Font("Segoe UI", Font.BOLD, 15));
            setPreferredSize(new Dimension(160, 48));
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

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        messageLabel.setText(" ");
        roleDisplayLabel.setText(" ");
    }

    public void clearPassword() {
        passwordField.setText("");
    }

    public void showError(String message) {
        messageLabel.setText("⚠ " + message);
        messageLabel.setForeground(ERROR_COLOR);
    }

    public void showSuccess(String message) {
        messageLabel.setText("✓ " + message);
        messageLabel.setForeground(SUCCESS_COLOR);
    }

    public void showRole(String role) {
        roleDisplayLabel.setText("🔑 Logging in as: " + role);
    }

    public void addLoginListener(ActionListener listener) {
        loginButton.addActionListener(listener);
        usernameField.addActionListener(listener);
        passwordField.addActionListener(listener);
    }

    public void addCancelListener(ActionListener listener) {
        cancelButton.addActionListener(listener);
    }

    public void addSignupLinkListener(ActionListener listener) {
        signupLinkButton.addActionListener(listener);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            
            Login loginView = new Login();
            loginView.setVisible(true);
        });
    }
}