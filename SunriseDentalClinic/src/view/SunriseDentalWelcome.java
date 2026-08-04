package view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class SunriseDentalWelcome extends JFrame {

    // Color Palette
    private static final Color PRIMARY_DARK = new Color(0x2F3E3C);
    private static final Color MINT = new Color(0xBDDBD1);
    private static final Color SOFT_SURFACE = new Color(0xFBF9F1);
    private static final Color LIGHT_SURFACE = new Color(0xE7E9E3);
    private static final Color HOVER_SURFACE = new Color(0xE8F0F1);
    private static final Color SECONDARY_ACCENT = new Color(0xC7E7EC);

    private BufferedImage logoImage;
    private BufferedImage welcomeImage;

    public SunriseDentalWelcome() {
        
        // Load images
        loadImages();

        setTitle("SUNRISE DENTAL - Clinic Management System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(SOFT_SURFACE);
        root.setBorder(new EmptyBorder(20, 40, 20, 40));

        root.add(createTopNavigation(), BorderLayout.NORTH);
        root.add(createMainContent(), BorderLayout.CENTER);
        root.add(createFooter(), BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void loadImages() {
        try {
            // Load logo with high quality
            logoImage = ImageIO.read(new File(
                "src/resources/Remove Bg light.png"
            ));
            
            // Load welcome hero image (without background)
            welcomeImage = ImageIO.read(new File(
                "src/resources/Welcome.png"
            ));
            
        } catch (Exception e) {
            System.err.println("Error loading images: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private BufferedImage resizeImage(BufferedImage original, int width, int height) {
        if (original == null) return null;
        
        BufferedImage resized = new BufferedImage(
                width,
                height,
                BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = resized.createGraphics();

        // Highest quality rendering settings
        g2.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        g2.setRenderingHint(
                RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        g2.setRenderingHint(
                RenderingHints.KEY_ALPHA_INTERPOLATION,
                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

        g2.setRenderingHint(
                RenderingHints.KEY_COLOR_RENDERING,
                RenderingHints.VALUE_COLOR_RENDER_QUALITY);

        g2.setRenderingHint(
                RenderingHints.KEY_DITHERING,
                RenderingHints.VALUE_DITHER_ENABLE);

        g2.drawImage(original, 0, 0, width, height, null);
        g2.dispose();

        return resized;
    }

    private JPanel createTopNavigation() {
        JPanel nav = new JPanel(new BorderLayout());
        nav.setOpaque(false);
        nav.setBorder(new EmptyBorder(10, 0, 25, 0));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        left.setOpaque(false);

        // Logo - using high-quality rendering with crisp edges
        if (logoImage != null) {
            // Use larger size for sharper logo (120x120 then scale down with quality)
            BufferedImage tempScaled = resizeImage(logoImage, 120, 120);
            BufferedImage scaledLogo = resizeImage(tempScaled, 60, 60);
            
            JLabel logo = new JLabel(new ImageIcon(scaledLogo)) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                                       RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setRenderingHint(RenderingHints.KEY_RENDERING, 
                                       RenderingHints.VALUE_RENDER_QUALITY);
                    super.paintComponent(g2);
                    g2.dispose();
                }
            };
            left.add(logo);
        }

        JPanel brandPanel = new JPanel();
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.Y_AXIS));
        brandPanel.setOpaque(false);

        JLabel title = new JLabel("SUNRISE DENTAL");
        title.setFont(font(Font.BOLD, 24));

        JLabel subtitle = new JLabel("Clinic Management System");
        subtitle.setForeground(new Color(107, 123, 121));
        subtitle.setFont(font(Font.PLAIN, 13));

        brandPanel.add(title);
        brandPanel.add(subtitle);

        left.add(brandPanel);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // FIXED: Changed FlowPanel to FlowLayout
        right.setOpaque(false);

        JLabel badge = new JLabel("v1.0");
        badge.setOpaque(true);
        badge.setBackground(MINT);
        badge.setForeground(PRIMARY_DARK);
        badge.setBorder(new EmptyBorder(8, 18, 8, 18));
        badge.setFont(font(Font.BOLD, 12));

        right.add(badge);

        nav.add(left, BorderLayout.WEST);
        nav.add(right, BorderLayout.EAST);

        return nav;
    }

    private JPanel createMainContent() {
        JPanel main = new JPanel(new GridLayout(1, 2, 40, 0));
        main.setOpaque(false);

        main.add(createLeftSection());
        main.add(createRightSection());

        return main;
    }

    private JPanel createLeftSection() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(Box.createVerticalGlue());

        JLabel heading = new JLabel(
                "<html>Transforming Dental Care<br>Through Smart Management</html>");
        heading.setForeground(PRIMARY_DARK);
        heading.setFont(font(Font.BOLD, 42));
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(heading);
        panel.add(Box.createVerticalStrut(25));

        JLabel desc = new JLabel(
                "<html><div style='width:550px;'>"
                        + "Manage patients, appointments, treatments, billing, "
                        + "staff, and clinic operations from one powerful and "
                        + "beautifully designed platform."
                        + "</div></html>");
        desc.setForeground(new Color(107, 123, 121));
        desc.setFont(font(Font.PLAIN, 18));

        panel.add(desc);
        panel.add(Box.createVerticalStrut(30));

        panel.add(createTrustIndicators());
        panel.add(Box.createVerticalStrut(35));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        btnPanel.setOpaque(false);

        RoundedButton loginBtn = new RoundedButton("Login to Portal", PRIMARY_DARK, Color.WHITE);
        RoundedButton signupBtn = new RoundedButton("Create Account", SOFT_SURFACE, PRIMARY_DARK);
        signupBtn.setBorderColor(MINT);

        btnPanel.add(loginBtn);
        btnPanel.add(signupBtn);

        panel.add(btnPanel);
        panel.add(Box.createVerticalStrut(30));

        panel.add(createWelcomeCard());
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel createTrustIndicators() {
        JPanel trust = new JPanel(new GridLayout(3, 2, 10, 12));
        trust.setOpaque(false);
        trust.setMaximumSize(new Dimension(500, 120));

        String[] items = {
                "Patient Management",
                "Appointment Scheduling",
                "Billing & Payments",
                "Staff & Dentist Management",
                "Reports & Analytics"
        };

        for (String text : items) {
            JLabel lbl = new JLabel("✓ " + text);
            lbl.setFont(font(Font.PLAIN, 15));
            lbl.setForeground(PRIMARY_DARK);
            trust.add(lbl);
        }

        return trust;
    }

    private JPanel createWelcomeCard() {
        JPanel card = new ShadowPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setMaximumSize(new Dimension(620, 140));
        card.setPreferredSize(new Dimension(620, 140));
        card.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel title = new JLabel("Welcome to Sunrise Dental Management System");
        title.setForeground(PRIMARY_DARK);
        title.setFont(font(Font.BOLD, 18));

        JLabel body = new JLabel(
                "<html>Providing excellence in dental healthcare "
                        + "administration through modern technology and "
                        + "intelligent workflow management.</html>");
        body.setForeground(new Color(107, 123, 121));
        body.setFont(font(Font.PLAIN, 14));

        card.add(title, BorderLayout.NORTH);
        card.add(body, BorderLayout.CENTER);

        return card;
    }

    private JPanel createRightSection() {
        JPanel hero = new HeroPanel(welcomeImage);
        hero.setPreferredSize(new Dimension(650, 650));
        return hero;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel();
        footer.setOpaque(false);

        JLabel text = new JLabel(
                "© 2026 Sunrise Dental Clinic    |    Version 1.0    |    Designed for Modern Healthcare Excellence");
        text.setForeground(new Color(138, 151, 148));
        text.setFont(font(Font.PLAIN, 12));

        footer.add(text);

        return footer;
    }

    private Font font(int style, int size) {
        return new Font("Segoe UI", style, size);
    }

    // =======================
    // Hero Panel - Transparent Background Blending
    // =======================

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

            // Highest quality rendering
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setRenderingHint(
                    RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);

            g2.setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);

            g2.setRenderingHint(
                    RenderingHints.KEY_ALPHA_INTERPOLATION,
                    RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

            g2.setRenderingHint(
                    RenderingHints.KEY_COLOR_RENDERING,
                    RenderingHints.VALUE_COLOR_RENDER_QUALITY);

            int width = getWidth();
            int height = getHeight();

            // Subtle decorative elements behind the image
            GradientPaint gradient = new GradientPaint(
                0, 0, new Color(0xE8F0F1),
                width, height, new Color(0xFBF9F1)
            );
            g2.setPaint(gradient);
            g2.fillRoundRect(20, 20, width - 40, height - 40, 40, 40);

            // Soft shadow effect
            g2.setColor(new Color(0, 0, 0, 8));
            g2.fillRoundRect(25, 25, width - 50, height - 50, 40, 40);

            // Display the welcome image with transparency
            if (welcomeImage != null) {
                int imgW = welcomeImage.getWidth();
                int imgH = welcomeImage.getHeight();

                // Calculate padding (10% on each side for breathing room)
                int paddingX = (int) (width * 0.10);
                int paddingY = (int) (height * 0.10);

                int availableW = width - (paddingX * 2);
                int availableH = height - (paddingY * 2);

                double scaleX = availableW / (double) imgW;
                double scaleY = availableH / (double) imgH;

                // Use contain mode to show entire image without cropping
                double scale = Math.min(scaleX, scaleY);

                // Slightly reduce scale to give more breathing room
                scale = scale * 0.92;

                int drawW = (int) (imgW * scale);
                int drawH = (int) (imgH * scale);

                int x = (width - drawW) / 2;
                int y = (height - drawH) / 2;

                // Draw image with transparency preserved
                g2.drawImage(welcomeImage, x, y, drawW, drawH, null);
                
                // Add a subtle glow effect around the image
                Color glowColor = new Color(47, 62, 60, 20);
                g2.setColor(glowColor);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(x - 10, y - 10, drawW + 20, drawH + 20, 30, 30);
                
            } else {
                // Fallback display
                g2.setColor(new Color(200, 200, 200));
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 18));
                String message = "Welcome Image Here";
                FontMetrics fm = g2.getFontMetrics();
                int textX = (width - fm.stringWidth(message)) / 2;
                int textY = (height - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(message, textX, textY);

                g2.setColor(new Color(180, 180, 180));
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                String subMsg = "Place Welcome.png in src/resources/";
                fm = g2.getFontMetrics();
                textX = (width - fm.stringWidth(subMsg)) / 2;
                textY = textY + 30;
                g2.drawString(subMsg, textX, textY);
            }

            g2.dispose();
        }
    }

    // =======================
    // Premium Rounded Button
    // =======================

    static class RoundedButton extends JButton {
        private Color bg;
        private Color fg;
        private Color borderColor;
        private Color hoverColor;
        private Color originalBg;

        RoundedButton(String text, Color bg, Color fg) {
            super(text);
            this.bg = bg;
            this.originalBg = bg;
            this.fg = fg;
            this.borderColor = bg;
            this.hoverColor = bg;

            setForeground(fg);
            setFont(new Font("Segoe UI", Font.BOLD, 15));
            setPreferredSize(new Dimension(180, 48));
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            // Calculate hover color (darker version)
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

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

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

    // =======================
    // Shadow Card
    // =======================

    static class ShadowPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            // Shadow
            g2.setColor(new Color(0, 0, 0, 15));
            g2.fillRoundRect(6, 6, getWidth() - 12, getHeight() - 12, 20, 20);

            // Main card
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth() - 12, getHeight() - 12, 20, 20);

            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        public boolean isOpaque() {
            return false;
        }
    }

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