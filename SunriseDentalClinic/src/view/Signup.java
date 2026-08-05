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
    
    // Color Palette
    private static final Color PRIMARY_DARK = new Color(0x2F3E3C);
    private static final Color MINT = new Color(0xBDDBD1);
    private static final Color SOFT_SURFACE = new Color(0xFBF9F1);
    private static final Color LIGHT_SURFACE = new Color(0xE7E9E3);
    private static final Color SECONDARY_TEXT = new Color(122, 138, 135);
    private static final Color ERROR_COLOR = new Color(220, 80, 80);
    private static final Color SUCCESS_COLOR = new Color(60, 160, 80);

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

    public Signup() {
        initComponents();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setTitle("SUNRISE DENTAL - Create Account");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 700);
        setMinimumSize(new Dimension(900, 600));
        setResizable(true);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        mainPanel.setBackground(SOFT_SURFACE);

        // Left Panel - Signup Form
        mainPanel.add(createSignupPanel());
        
        // Right Panel - Welcome Image/Branding
        mainPanel.add(createWelcomePanel());

        setContentPane(mainPanel);
    }

    private JPanel createSignupPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(SOFT_SURFACE);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(40, 50, 40, 50));

        // Logo
        JLabel logoLabel = createLogoLabel();
        logoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(logoLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_DARK);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);

        JLabel subtitleLabel = new JLabel("Sign up as a new patient");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(SECONDARY_TEXT);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(subtitleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 25)));

        // Username
        panel.add(createLabeledField("Username", usernameField = createStyledTextField()));
        
        // Full Name
        panel.add(createLabeledField("Full Name", fullNameField = createStyledTextField()));
        
        // Email
        panel.add(createLabeledField("Email", emailField = createStyledTextField()));
        
        // Phone
        panel.add(createLabeledField("Phone Number", phoneField = createStyledTextField()));
        
        // Password
        panel.add(createLabeledField("Password", passwordField = createStyledPasswordField()));
        
        // Confirm Password
        panel.add(createLabeledField("Confirm Password", confirmPasswordField = createStyledPasswordField()));

        // Message label
        messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        messageLabel.setForeground(ERROR_COLOR);
        messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(messageLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        signupButton = createRoundedButton("Sign Up", PRIMARY_DARK, Color.WHITE);
        signupButton.setPreferredSize(new Dimension(140, 44));
        
        cancelButton = createRoundedButton("Cancel", SOFT_SURFACE, PRIMARY_DARK);
        cancelButton.setBorderColor(MINT);
        cancelButton.setPreferredSize(new Dimension(140, 44));

        buttonPanel.add(signupButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel);

        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Login link
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

        panel.add(loginLinkPanel);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY_DARK);
        panel.setBorder(new EmptyBorder(40, 40, 40, 40));

        // Load and display welcome image
        JLabel imageLabel = new JLabel();
        try {
            BufferedImage img = ImageIO.read(new File("src/resources/Welcome.png"));
            if (img != null) {
                ImageIcon icon = new ImageIcon(img.getScaledInstance(450, 400, Image.SCALE_SMOOTH));
                imageLabel.setIcon(icon);
            }
        } catch (Exception e) {
            imageLabel.setText("🦷 Join Sunrise Dental");
            imageLabel.setForeground(Color.WHITE);
            imageLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        }
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(imageLabel, BorderLayout.CENTER);

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        
        JLabel welcomeText = new JLabel(
            "<html><div style='text-align:center;color:white;font-size:18px;font-weight:bold;'>"
            + "Start Your Journey to<br>Better Dental Health"
            + "</div></html>"
        );
        welcomeText.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeText.setAlignmentX(Component.CENTER_ALIGNMENT);
        textPanel.add(welcomeText);
        textPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        JLabel subText = new JLabel(
            "<html><div style='text-align:center;color:rgba(255,255,255,0.7);font-size:13px;'>"
            + "Create your account to book appointments<br>and manage your dental health"
            + "</div></html>"
        );
        subText.setHorizontalAlignment(SwingConstants.CENTER);
        subText.setAlignmentX(Component.CENTER_ALIGNMENT);
        textPanel.add(subText);
        
        panel.add(textPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createLabeledField(String labelText, JComponent field) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(400, 70));
        panel.setPreferredSize(new Dimension(400, 70));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(PRIMARY_DARK);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));

        field.setMaximumSize(new Dimension(400, 38));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(field);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));

        return panel;
    }

    private JLabel createLogoLabel() {
        JLabel logo = new JLabel();
        try {
            BufferedImage img = ImageIO.read(new File("src/resources/Remove Bg light.png"));
            if (img != null) {
                ImageIcon icon = new ImageIcon(img.getScaledInstance(40, 40, Image.SCALE_SMOOTH));
                logo.setIcon(icon);
            }
        } catch (Exception e) {
            logo.setText("🦷");
            logo.setFont(new Font("Segoe UI", Font.PLAIN, 30));
        }
        return logo;
    }

    private JTextField createStyledTextField() {
        return new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(LIGHT_SURFACE);
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
    }

    private JPasswordField createStyledPasswordField() {
        return new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(LIGHT_SURFACE);
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
    }

    private RoundedButton createRoundedButton(String text, Color bg, Color fg) {
        return new RoundedButton(text, bg, fg);
    }

    // Inner class for rounded button
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
            setPreferredSize(new Dimension(140, 44));
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

    // ========================
    // Public methods for Controller
    // ========================
    
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