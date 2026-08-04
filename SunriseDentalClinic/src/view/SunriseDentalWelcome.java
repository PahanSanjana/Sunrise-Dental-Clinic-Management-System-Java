/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package view;

/**
 *
 * @author HP
 */
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

    public SunriseDentalWelcome() {

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

    private JPanel createTopNavigation() {

        JPanel nav = new JPanel(new BorderLayout());
        nav.setOpaque(false);
        nav.setBorder(new EmptyBorder(10, 0, 25, 0));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        left.setOpaque(false);

        // Logo
        ImageIcon icon = new ImageIcon(
                "src/resources/Remove Bg light.png");

        Image img = icon.getImage().getScaledInstance(
                60,
                60,
                Image.SCALE_SMOOTH);

        JLabel logo = new JLabel(new ImageIcon(img));

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

        left.add(logo);
        left.add(brandPanel);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
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

        RoundedButton loginBtn =
                new RoundedButton("Login to Portal",
                        PRIMARY_DARK,
                        Color.WHITE);

        RoundedButton signupBtn =
                new RoundedButton("Create Account",
                        SOFT_SURFACE,
                        PRIMARY_DARK);

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

        JLabel title = new JLabel(
                "Welcome to Sunrise Dental Management System");

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

        JPanel hero = new HeroPanel();
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
    // Hero SaaS Illustration
    // =======================

    static class HeroPanel extends JPanel {

        HeroPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {

            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // Main SaaS board
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(50, 50,
                    w - 100,
                    h - 100,
                    40,
                    40);

            // Header
            g2.setColor(new Color(0xE8F0F1));
            g2.fillRoundRect(80, 90,
                    w - 160,
                    60,
                    20,
                    20);

            // Dashboard cards
            g2.setColor(new Color(0xBDDBD1));
            g2.fillRoundRect(90, 180, 180, 120, 25, 25);

            g2.setColor(new Color(0xC7E7EC));
            g2.fillRoundRect(300, 180, 180, 120, 25, 25);

            g2.setColor(new Color(0xE8F0F1));
            g2.fillRoundRect(510, 180, 180, 120, 25, 25);

            // Patient card
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(100, 350, 260, 180, 25, 25);

            // Calendar widget
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(390, 350, 300, 180, 25, 25);

            // Decorative circles
            g2.setColor(new Color(0xBDDBD1));
            g2.fillOval(580, 40, 80, 80);

            g2.setColor(new Color(0xC7E7EC));
            g2.fillOval(30, 500, 100, 100);

            // Dental icon
            g2.setColor(new Color(0x2F3E3C));
            g2.setFont(new Font("Segoe UI Symbol", Font.BOLD, 70));
            g2.drawString("🦷", 300, 130);

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

        RoundedButton(String text, Color bg, Color fg) {

            super(text);

            this.bg = bg;
            this.fg = fg;
            this.borderColor = bg;

            setForeground(fg);
            setFont(new Font("Segoe UI", Font.BOLD, 15));

            setPreferredSize(new Dimension(180, 48));

            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {

                @Override
                public void mouseEntered(MouseEvent e) {
                    setBackground(new Color(40, 55, 53));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    repaint();
                }
            });
        }

        public void setBorderColor(Color c) {
            borderColor = c;
        }

        @Override
        protected void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(bg);
            g2.fillRoundRect(
                    0,
                    0,
                    getWidth(),
                    getHeight(),
                    24,
                    24);

            if (borderColor != bg) {

                g2.setColor(borderColor);
                g2.drawRoundRect(
                        0,
                        0,
                        getWidth() - 1,
                        getHeight() - 1,
                        24,
                        24);
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

            g2.setColor(new Color(0, 0, 0, 15));
            g2.fillRoundRect(
                    6,
                    6,
                    getWidth() - 12,
                    getHeight() - 12,
                    20,
                    20);

            g2.setColor(getBackground());
            g2.fillRoundRect(
                    0,
                    0,
                    getWidth() - 12,
                    getHeight() - 12,
                    20,
                    20);

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
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }

            new SunriseDentalWelcome().setVisible(true);
        });
    }
}