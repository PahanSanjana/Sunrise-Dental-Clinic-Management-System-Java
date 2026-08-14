package view;

import controller.LoginController;
import controller.SignupController;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class SunriseDentalWelcome extends JFrame {

    // Color Palette - Modern & Minimal
    private static final Color PRIMARY_DARK = new Color(0x2F3E3C);
    private static final Color PRIMARY_LIGHT = new Color(0x4A6A65);
    private static final Color MINT = new Color(0xBDDBD1);
    private static final Color MINT_LIGHT = new Color(0xE8F5F0);
    private static final Color SOFT_SURFACE = new Color(0xFBF9F1);
    private static final Color LIGHT_SURFACE = new Color(0xE7E9E3);
    private static final Color CARD_BG = new Color(0xFFFFFF);
    private static final Color SECONDARY_TEXT = new Color(107, 123, 121);
    private static final Color ACCENT_GRADIENT_START = new Color(0xBDDBD1);
    private static final Color ACCENT_GRADIENT_END = new Color(0xA8C8BC);

    private BufferedImage logoImage;
    private BufferedImage welcomeImage;
    private RoundedButton loginBtn;
    private RoundedButton signupBtn;

    // Frame size matching MainFrame
    private static final int FRAME_WIDTH = 1400;
    private static final int FRAME_HEIGHT = 820;

    public SunriseDentalWelcome() {
        loadImages();

        setTitle("SUNRISE DENTAL - Clinic Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(SOFT_SURFACE);
        root.setBorder(new EmptyBorder(0, 0, 0, 0));

        root.add(createTopNavigation(), BorderLayout.NORTH);
        root.add(createMainContent(), BorderLayout.CENTER);
        root.add(createFooter(), BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void loadImages() {
        try {
            logoImage = ImageIO.read(new File("src/resources/Remove Bg light.png"));
            welcomeImage = ImageIO.read(new File("src/resources/Welcome.png"));
        } catch (Exception e) {
            System.err.println("Error loading images: " + e.getMessage());
        }
    }

    private BufferedImage resizeImage(BufferedImage original, int width, int height) {
        if (original == null) return null;

        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resized.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

        g2.drawImage(original, 0, 0, width, height, null);
        g2.dispose();

        return resized;
    }

    // =====================================================
    // TOP NAVIGATION - Clean & Minimal
    // =====================================================
    private JPanel createTopNavigation() {
        JPanel nav = new JPanel(new BorderLayout());
        nav.setOpaque(false);
        nav.setBorder(new EmptyBorder(20, 40, 20, 40));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);

        // Logo
        if (logoImage != null) {
            BufferedImage scaledLogo = resizeImage(logoImage, 50, 50);
            JLabel logo = new JLabel(new ImageIcon(scaledLogo));
            left.add(logo);
        }

        // Brand
        JPanel brandPanel = new JPanel();
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.Y_AXIS));
        brandPanel.setOpaque(false);

        JLabel title = new JLabel("SUNRISE DENTAL");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(PRIMARY_DARK);

        JLabel subtitle = new JLabel("Clinic Management System");
        subtitle.setForeground(SECONDARY_TEXT);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        brandPanel.add(title);
        brandPanel.add(Box.createRigidArea(new Dimension(0, 1)));
        brandPanel.add(subtitle);

        left.add(brandPanel);

        nav.add(left, BorderLayout.WEST);

        // Right - Version Badge
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.setOpaque(false);

        JLabel badge = new JLabel("v2.0");
        badge.setOpaque(true);
        badge.setBackground(MINT);
        badge.setForeground(PRIMARY_DARK);
        badge.setBorder(new EmptyBorder(6, 16, 6, 16));
        badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badge.setBorder(BorderFactory.createLineBorder(MINT, 1));
        right.add(badge);

        nav.add(right, BorderLayout.EAST);

        return nav;
    }

    // =====================================================
    // MAIN CONTENT - Modern Split Layout
    // =====================================================
    private JPanel createMainContent() {
        JPanel main = new JPanel(new GridLayout(1, 2, 50, 0));
        main.setOpaque(false);
        main.setBorder(new EmptyBorder(10, 40, 20, 40));

        main.add(createLeftSection());
        main.add(createRightSection());

        return main;
    }

    // =====================================================
    // LEFT SECTION - Content with spacing
    // =====================================================
    private JPanel createLeftSection() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 10, 20, 10));

        panel.add(Box.createVerticalGlue());

        // Badge
        JLabel badge = new JLabel("✦ SMART CLINIC MANAGEMENT");
        badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badge.setForeground(PRIMARY_LIGHT);
        badge.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(badge);
        panel.add(Box.createRigidArea(new Dimension(0, 12)));

        // Heading
        JLabel heading = new JLabel(
                "<html><span style='font-size:38px; font-weight:700; color:#2F3E3C;'>Transforming Dental Care</span><br>" +
                "<span style='font-size:32px; font-weight:300; color:#4A6A65;'>Through Smart Management</span></html>");
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(heading);
        panel.add(Box.createRigidArea(new Dimension(0, 16)));

        // Description
        JLabel desc = new JLabel(
                "<html><div style='width:480px; font-size:15px; color:#6B7B79; line-height:1.6;'>" +
                "Manage patients, appointments, treatments, billing, staff, and clinic operations " +
                "from one powerful and beautifully designed platform." +
                "</div></html>");
        desc.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(desc);
        panel.add(Box.createRigidArea(new Dimension(0, 28)));

        // Features Grid
        panel.add(createFeatureGrid());
        panel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        btnPanel.setOpaque(false);

        loginBtn = new RoundedButton("Login", PRIMARY_DARK, Color.WHITE);
        loginBtn.setPreferredSize(new Dimension(160, 48));
        loginBtn.addActionListener(e -> openLoginView());

        signupBtn = new RoundedButton("Create Account", SOFT_SURFACE, PRIMARY_DARK);
        signupBtn.setBorderColor(MINT);
        signupBtn.setPreferredSize(new Dimension(160, 48));
        signupBtn.addActionListener(e -> openSignupView());

        btnPanel.add(loginBtn);
        btnPanel.add(signupBtn);

        panel.add(btnPanel);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    // =====================================================
    // FEATURE GRID - Clean & Minimal
    // =====================================================
    private JPanel createFeatureGrid() {
        JPanel grid = new JPanel(new GridLayout(2, 3, 12, 10));
        grid.setOpaque(false);
        grid.setMaximumSize(new Dimension(500, 80));

        String[] features = {
            "Patient Management",
            "Appointment Scheduling",
            "Billing & Payments",
            "Staff & Dentist Management",
            "Reports & Analytics",
            "Secure & Reliable"
        };

        for (String text : features) {
            JLabel lbl = new JLabel("✓ " + text);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lbl.setForeground(PRIMARY_DARK);
            grid.add(lbl);
        }

        return grid;
    }

    // =====================================================
    // RIGHT SECTION - Hero Image
    // =====================================================
    private JPanel createRightSection() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);

        JPanel heroContainer = new JPanel(new BorderLayout());
        heroContainer.setOpaque(false);
        heroContainer.setPreferredSize(new Dimension(550, 500));

        HeroPanel hero = new HeroPanel(welcomeImage);
        heroContainer.add(hero, BorderLayout.CENTER);

        wrapper.add(heroContainer);

        return wrapper;
    }

    // =====================================================
    // FOOTER - Minimal
    // =====================================================
    private JPanel createFooter() {
        JPanel footer = new JPanel();
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(10, 40, 20, 40));

        JLabel text = new JLabel("© 2026 Sunrise Dental Clinic | Version 2.0 | Designed for Modern Healthcare");
        text.setForeground(new Color(138, 151, 148));
        text.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        footer.add(text);
        return footer;
    }

    // =====================================================
    // NAVIGATION METHODS
    // =====================================================
    private void openLoginView() {
        SwingUtilities.invokeLater(() -> {
            this.dispose();
            Login loginView = new Login();
            new LoginController(loginView);
            loginView.setVisible(true);
        });
    }

    private void openSignupView() {
        SwingUtilities.invokeLater(() -> {
            this.dispose();
            Signup signupView = new Signup();
            new SignupController(signupView);
            signupView.setVisible(true);
        });
    }

    private Font font(int style, int size) {
        return new Font("Segoe UI", style, size);
    }

    // =====================================================
    // HERO PANEL - Clean Image Display
    // =====================================================
    static class HeroPanel extends JPanel {
        private BufferedImage welcomeImage;

        HeroPanel(BufferedImage image) {
            this.welcomeImage = image;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

            int w = getWidth();
            int h = getHeight();

            // Soft gradient background
            GradientPaint gradient = new GradientPaint(
                0, 0, new Color(0xE8F0F1),
                w, h, new Color(0xFBF9F1)
            );
            g2.setPaint(gradient);
            g2.fillRoundRect(10, 10, w - 20, h - 20, 30, 30);

            // Subtle shadow
            g2.setColor(new Color(0, 0, 0, 6));
            g2.fillRoundRect(15, 15, w - 30, h - 30, 30, 30);

            if (welcomeImage != null) {
                int imgW = welcomeImage.getWidth();
                int imgH = welcomeImage.getHeight();

                int padding = (int) (Math.min(w, h) * 0.08);
                int availableW = w - (padding * 2);
                int availableH = h - (padding * 2);

                double scaleX = availableW / (double) imgW;
                double scaleY = availableH / (double) imgH;
                double scale = Math.min(scaleX, scaleY) * 0.92;

                int drawW = (int) (imgW * scale);
                int drawH = (int) (imgH * scale);
                int x = (w - drawW) / 2;
                int y = (h - drawH) / 2;

                g2.drawImage(welcomeImage, x, y, drawW, drawH, null);

                // Subtle glow
                g2.setColor(new Color(47, 62, 60, 15));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(x - 8, y - 8, drawW + 16, drawH + 16, 25, 25);

            } else {
                // Fallback
                g2.setColor(new Color(200, 200, 200));
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                String msg = "Welcome Image";
                FontMetrics fm = g2.getFontMetrics();
                int tx = (w - fm.stringWidth(msg)) / 2;
                int ty = (h - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(msg, tx, ty);

                g2.setColor(new Color(180, 180, 180));
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                String sub = "Place Welcome.png in src/resources/";
                fm = g2.getFontMetrics();
                tx = (w - fm.stringWidth(sub)) / 2;
                g2.drawString(sub, tx, ty + 28);
            }

            g2.dispose();
        }
    }

    // =====================================================
    // ROUNDED BUTTON - Modern
    // =====================================================
    static class RoundedButton extends JButton {
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
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            if (bg.equals(new Color(0x2F3E3C))) {
                hoverColor = new Color(40, 55, 53);
            } else {
                hoverColor = new Color(230, 230, 225);
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
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);

            if (borderColor != bg && borderColor != null) {
                g2.setColor(borderColor);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
            }

            super.paintComponent(g);
            g2.dispose();
        }
    }

    // =====================================================
    // MAIN METHOD
    // =====================================================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            new SunriseDentalWelcome().setVisible(true);
        });
    }
}