package view;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * TopBarPanel - top bar with search field and notification/message icons.
 * Pure View: purely cosmetic, no business logic or data access here.
 */
public class TopBarPanel extends JPanel {

    private static final Color BG_COLOR = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(230, 230, 230);
    private static final Color SEARCH_BG = new Color(245, 246, 248);
    private static final Color TEXT_MUTED = new Color(120, 120, 120);

    private JTextField searchField;

    public TopBarPanel() {
        setLayout(new BorderLayout());
        setBackground(BG_COLOR);
        setPreferredSize(new Dimension(0, 70));
        setBorder(new EmptyBorder(12, 24, 12, 24));

        add(buildSearchPanel(), BorderLayout.CENTER);
        add(buildIconPanel(), BorderLayout.EAST);
    }

    private JPanel buildSearchPanel() {
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        wrapper.setBackground(BG_COLOR);

        JPanel searchBox = new JPanel(new BorderLayout());
        searchBox.setBackground(SEARCH_BG);
        searchBox.setPreferredSize(new Dimension(360, 40));
        searchBox.setBorder(new EmptyBorder(0, 14, 0, 14));

        JLabel searchIcon = new JLabel("\uD83D\uDD0D"); // 🔍
        searchIcon.setFont(new Font("SansSerif", Font.PLAIN, 14));
        searchIcon.setBorder(new EmptyBorder(0, 0, 0, 8));

        searchField = new JTextField();
        searchField.setBorder(BorderFactory.createEmptyBorder());
        searchField.setBackground(SEARCH_BG);
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        searchField.setForeground(Color.DARK_GRAY);
        addPlaceholder(searchField, "Search");

        searchBox.add(searchIcon, BorderLayout.WEST);
        searchBox.add(searchField, BorderLayout.CENTER);

        wrapper.add(searchBox);
        return wrapper;
    }

    private JPanel buildIconPanel() {
        JPanel iconPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 18, 0));
        iconPanel.setBackground(BG_COLOR);

        iconPanel.add(createIconButton("\uD83D\uDCAC", 0));       // 💬 messages
        iconPanel.add(createIconButton("\uD83D\uDD14", 1));       // 🔔 notifications, with dot

        return iconPanel;
    }

    private JPanel createIconButton(String symbol, int badgeCount) {
        JPanel container = new JPanel(null); // absolute positioning for the badge dot
        container.setPreferredSize(new Dimension(34, 34));
        container.setBackground(BG_COLOR);

        JLabel icon = new JLabel(symbol);
        icon.setFont(new Font("SansSerif", Font.PLAIN, 20));
        icon.setBounds(0, 0, 34, 34);
        icon.setHorizontalAlignment(SwingConstants.CENTER);
        icon.setCursor(new Cursor(Cursor.HAND_CURSOR));
        container.add(icon);

        if (badgeCount > 0) {
            JLabel badge = new JLabel();
            badge.setBounds(22, 2, 10, 10);
            badge.setOpaque(true);
            badge.setBackground(new Color(230, 30, 100));
            container.add(badge);
        }

        return container;
    }

    /**
     * Simple placeholder-text behavior for a JTextField
     * (Swing has no built-in placeholder support).
     */
    private void addPlaceholder(JTextField field, String placeholder) {
        field.setText(placeholder);
        field.setForeground(TEXT_MUTED);

        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.DARK_GRAY);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(TEXT_MUTED);
                }
            }
        });
    }

    public String getSearchText() {
        return searchField.getText();
    }
}