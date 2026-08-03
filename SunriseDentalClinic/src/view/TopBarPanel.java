package view;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

/**
 * TopBarPanel - premium top navigation bar for the dental clinic dashboard.
 *
 * Matches SidebarPanel's design system exactly: same palette, same font
 * fallback chain, and the same "no font-glyph icons" rule - every icon here
 * (search, bell, gear) is hand-drawn with Java2D so it renders identically
 * on every OS instead of depending on which symbol glyphs happen to be
 * installed.
 *
 * Pure View layer: no business logic, no data access. The logout button
 * exposes an addLogoutListener() hook for MainFrame/Controller to wire up.
 */
public class TopBarPanel extends JPanel {

    // ---- Palette (matches SidebarPanel) --------------------------------
    private static final Color PRIMARY_DARK     = Color.decode("#2F3E3C");
    private static final Color MINT_ACCENT      = Color.decode("#BDDBD1");
    private static final Color SOFT_BG          = Color.decode("#FBF9F1");
    private static final Color LIGHT_SURFACE    = Color.decode("#E7E9E3");
    private static final Color HOVER_SURFACE    = Color.decode("#E8F0F1");
    private static final Color SECONDARY_ACCENT = Color.decode("#C7E7EC");
    private static final Color SECONDARY_TEXT   = new Color(122, 138, 135); // #7A8A87

    // ---- Typography ------------------------------------------------
    private static final String FONT_FAMILY = resolveFontFamily();
    private static final Font FONT_SEARCH    = new Font(FONT_FAMILY, Font.PLAIN, 13);
    private static final Font FONT_LOGOUT    = new Font(FONT_FAMILY, Font.BOLD, 13);

    // ---- Layout constants --------------------------------------------
    private static final int BAR_HEIGHT     = 72;
    private static final int LOGO_HEIGHT    = 52;
    private static final String LOGO_PATH =
            "C:\\Users\\HP\\OneDrive\\Documents\\Project\\Sunrise-Dental-Clinic-Management-System-Java"
          + "\\SunriseDentalClinic\\src\\resources\\Remove Bg light.png";

    private JTextField searchField;
    private LogoutButton logoutButton;

    public TopBarPanel() {
        setLayout(new BorderLayout());
        setBackground(SOFT_BG);
        setPreferredSize(new Dimension(0, BAR_HEIGHT));
        setBorder(new MatteBorder(0, 0, 1, 0, LIGHT_SURFACE));

        add(buildBrandSection(), BorderLayout.WEST);
        add(buildSearchSection(), BorderLayout.CENTER);
        add(buildActionsSection(), BorderLayout.EAST);
    }

    private static String resolveFontFamily() {
        String[] preferred = {"Segoe UI", "DejaVu Sans", "SansSerif"};
        List<String> available = java.util.Arrays.asList(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        for (String name : preferred) {
            if (available.contains(name)) {
                return name;
            }
        }
        return Font.SANS_SERIF;
    }

    // =================================================================
    // LEFT: brand (logo only - name/subtitle removed, sidebar already
    // carries the clinic name so the top bar doesn't need to repeat it)
    // =================================================================
    private JPanel buildBrandSection() {
        JPanel section = new JPanel(new GridBagLayout());
        section.setOpaque(false);
        section.setBorder(new EmptyBorder(0, 24, 0, 16));
        section.add(buildLogoComponent());
        return section;
    }

    /**
     * Loads the clinic logo from disk with high-quality scaling, preserving
     * aspect ratio at a fixed display height. Falls back to a small hand-drawn
     * mark (consistent with the sidebar's vector-icon approach) if the file
     * isn't found on this machine, so the bar never breaks visually.
     */
    private JComponent buildLogoComponent() {
        File logoFile = new File(LOGO_PATH);
        if (logoFile.exists()) {
            ImageIcon rawIcon = new ImageIcon(LOGO_PATH);
            int originalWidth = rawIcon.getIconWidth();
            int originalHeight = rawIcon.getIconHeight();
            if (originalWidth > 0 && originalHeight > 0) {
                int scaledWidth = Math.round(((float) originalWidth / originalHeight) * LOGO_HEIGHT);
                Image scaled = rawIcon.getImage()
                        .getScaledInstance(scaledWidth, LOGO_HEIGHT, Image.SCALE_SMOOTH);
                JLabel logoLabel = new JLabel(new ImageIcon(scaled));
                logoLabel.setPreferredSize(new Dimension(scaledWidth, LOGO_HEIGHT));
                return logoLabel;
            }
        }
        // Fallback mark: simple hand-drawn tooth glyph, no external asset needed.
        return new LogoFallbackMark();
    }

    /** Minimal vector "tooth" mark used only if the logo file can't be loaded. */
    private static class LogoFallbackMark extends JPanel {
        LogoFallbackMark() {
            setOpaque(false);
            Dimension size = new Dimension(LOGO_HEIGHT, LOGO_HEIGHT);
            setPreferredSize(size);
            setMinimumSize(size);
            setMaximumSize(size);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(MINT_ACCENT);
            g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 12, 12);
            g2.setColor(PRIMARY_DARK);
            g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int cx = getWidth() / 2, cy = getHeight() / 2;
            g2.drawArc(cx - 9, cy - 10, 18, 14, 0, 180);
            g2.drawLine(cx - 9, cy - 3, cx - 5, cy + 9);
            g2.drawLine(cx + 9, cy - 3, cx + 5, cy + 9);
            g2.dispose();
        }
    }

    // =================================================================
    // CENTER: global search
    // =================================================================
    private JPanel buildSearchSection() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);

        RoundedSearchField searchBox = new RoundedSearchField();
        searchField = searchBox.getTextField();

        GridBagConstraints gbc = new GridBagConstraints();
        wrapper.add(searchBox, gbc);
        return wrapper;
    }

    public String getSearchText() {
        return searchField.getText();
    }

    /** Self-painted rounded search pill with a hand-drawn magnifier icon. */
    private static class RoundedSearchField extends JPanel {
        private static final int FIELD_WIDTH = 340;
        private static final int FIELD_HEIGHT = 40;
        private boolean focused = false;
        private final JTextField textField;

        RoundedSearchField() {
            setOpaque(false);
            setLayout(new BorderLayout(8, 0));
            Dimension size = new Dimension(FIELD_WIDTH, FIELD_HEIGHT);
            setPreferredSize(size);
            setMinimumSize(size);
            setMaximumSize(size);
            setBorder(new EmptyBorder(0, 14, 0, 14));

            SearchIcon icon = new SearchIcon();

            textField = new JTextField();
            textField.setOpaque(false);
            textField.setBorder(BorderFactory.createEmptyBorder());
            textField.setFont(FONT_SEARCH);
            textField.setForeground(PRIMARY_DARK);
            textField.setCaretColor(PRIMARY_DARK);
            installPlaceholder(textField, "Search patients, appointments, bills...");

            textField.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    focused = true;
                    repaint();
                }

                @Override
                public void focusLost(FocusEvent e) {
                    focused = false;
                    repaint();
                }
            });

            add(icon, BorderLayout.WEST);
            add(textField, BorderLayout.CENTER);
        }

        JTextField getTextField() {
            return textField;
        }

        private void installPlaceholder(JTextField field, String placeholder) {
            field.setText(placeholder);
            field.setForeground(SECONDARY_TEXT);
            field.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (field.getText().equals(placeholder)) {
                        field.setText("");
                        field.setForeground(PRIMARY_DARK);
                    }
                }

                @Override
                public void focusLost(FocusEvent e) {
                    if (field.getText().isEmpty()) {
                        field.setText(placeholder);
                        field.setForeground(SECONDARY_TEXT);
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(HOVER_SURFACE); // #E8F0F1 fill at all times per spec
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), getHeight(), getHeight()));

            if (focused) {
                g2.setColor(MINT_ACCENT);
                g2.setStroke(new BasicStroke(1.6f));
                g2.draw(new RoundRectangle2D.Float(0.8f, 0.8f, getWidth() - 1.6f, getHeight() - 1.6f,
                        getHeight(), getHeight()));
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Hand-drawn magnifying glass icon - no font glyph dependency. */
    private static class SearchIcon extends JPanel {
        SearchIcon() {
            setOpaque(false);
            Dimension size = new Dimension(18, 18);
            setPreferredSize(size);
            setMinimumSize(size);
            setMaximumSize(size);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(SECONDARY_TEXT);
            g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            // Same fix as the sidebar icons: BorderLayout.WEST stretches this
            // panel to the search field's full height, so we translate to the
            // center of whatever space we actually got instead of drawing at
            // a fixed (0,0) origin - otherwise the glass sits above-center.
            int offsetX = (getWidth() - 18) / 2;
            int offsetY = (getHeight() - 18) / 2;
            g2.translate(offsetX, offsetY);

            g2.drawOval(1, 1, 10, 10);
            g2.drawLine(10, 10, 16, 16);
            g2.dispose();
        }
    }

    // =================================================================
    // =================================================================
    // RIGHT: logout only (avatar and name/role removed per request)
    // =================================================================
    private JPanel buildActionsSection() {
        JPanel section = new JPanel(new FlowLayout(FlowLayout.RIGHT, 18, 0));
        section.setOpaque(false);
        section.setBorder(new EmptyBorder(0, 16, 0, 24));

        logoutButton = new LogoutButton();
        section.add(logoutButton);

        return section;
    }

    public void addLogoutListener(ActionListener listener) {
        logoutButton.addActionListener(listener);
    }

    /** Premium outline logout button - mint fill on hover, no red anywhere. */
    private static class LogoutButton extends JPanel {
        private static final int HEIGHT = 36;
        private boolean hovered = false;
        private final List<ActionListener> listeners = new java.util.ArrayList<>();

        LogoutButton() {
            setOpaque(false);
            setLayout(new GridBagLayout());
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            JLabel label = new JLabel("Logout");
            label.setFont(FONT_LOGOUT);
            label.setForeground(PRIMARY_DARK);
            add(label);

            int textWidth = label.getPreferredSize().width;
            Dimension size = new Dimension(textWidth + 36, HEIGHT);
            setPreferredSize(size);
            setMinimumSize(size);
            setMaximumSize(size);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hovered = false;
                    repaint();
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    for (ActionListener l : listeners) {
                        l.actionPerformed(new ActionEvent(LogoutButton.this, ActionEvent.ACTION_PERFORMED, "logout"));
                    }
                }
            });
        }

        void addActionListener(ActionListener listener) {
            listeners.add(listener);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(hovered ? MINT_ACCENT : SOFT_BG);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));

            g2.setColor(MINT_ACCENT);
            g2.setStroke(new BasicStroke(1.4f));
            g2.draw(new RoundRectangle2D.Float(0.7f, 0.7f, getWidth() - 1.4f, getHeight() - 1.4f, 20, 20));

            g2.dispose();
            super.paintComponent(g);
        }
    }
}