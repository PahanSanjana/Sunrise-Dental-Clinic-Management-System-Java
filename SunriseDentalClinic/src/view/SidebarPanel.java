package view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * SidebarPanel - left navigation bar.
 * Pure View: on click, tells MainFrame which card to show.
 * Business logic (auth, data) stays out of this class.
 */
public class SidebarPanel extends JPanel {

    private static final Color BG_COLOR = new Color(21, 101, 192);      // dark blue
    private static final Color ACTIVE_COLOR = new Color(30, 130, 220);  // lighter blue highlight
    private static final Color TEXT_COLOR = Color.WHITE;

    private final MainFrame mainFrame;
    private JButton activeButton;

    // Each entry: display label, icon-ish symbol (swap for real icons later), card name
    private final String[][] navItems = {
        {"Dashboard",     "DASHBOARD"},
        {"Patients",      "PATIENTS"},
        {"Doctors",       "DOCTORS"},
        {"Calendar",      "CALENDAR"},
        {"Messages",      "MESSAGES"},
        {"Payments",      "PAYMENTS"},
        {"Analytics",     "ANALYTICS"},
        {"Settings",      "SETTINGS"}
    };

    public SidebarPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout());
        setBackground(BG_COLOR);
        setPreferredSize(new Dimension(240, 0));

        add(buildNavPanel(), BorderLayout.CENTER);
        add(buildUserPanel(), BorderLayout.SOUTH);
    }

    private JPanel buildNavPanel() {
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(BG_COLOR);
        navPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        for (String[] item : navItems) {
            JButton navButton = createNavButton(item[0], item[1]);
            navPanel.add(navButton);
            navPanel.add(Box.createRigidArea(new Dimension(0, 4)));

            // Mark Dashboard as active by default
            if (item[1].equals("DASHBOARD")) {
                setActive(navButton);
            }
        }

        return navPanel;
    }

    private JButton createNavButton(String label, String cardName) {
        JButton button = new JButton(label);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFont(new Font("SansSerif", Font.PLAIN, 15));
        button.setForeground(TEXT_COLOR);
        button.setBackground(BG_COLOR);
        button.setBorder(new EmptyBorder(10, 24, 10, 10));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addActionListener(e -> {
            mainFrame.showCard(cardName);
            setActive(button);
        });

        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button != activeButton) {
                    button.setBackground(ACTIVE_COLOR);
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (button != activeButton) {
                    button.setBackground(BG_COLOR);
                }
            }
        });

        return button;
    }

    private void setActive(JButton button) {
        if (activeButton != null) {
            activeButton.setBackground(BG_COLOR);
        }
        button.setBackground(ACTIVE_COLOR);
        activeButton = button;
    }

    private JPanel buildUserPanel() {
        JPanel userPanel = new JPanel(new BorderLayout(10, 0));
        userPanel.setBackground(new Color(15, 80, 160)); // slightly darker footer
        userPanel.setBorder(new EmptyBorder(14, 16, 14, 16));
        userPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        // Placeholder avatar circle (swap for a real ImageIcon later)
        JLabel avatar = new JLabel("👤");
        avatar.setFont(new Font("SansSerif", Font.PLAIN, 28));
        avatar.setPreferredSize(new Dimension(40, 40));
        avatar.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel nameLabel = new JLabel("<html><b style='color:white;'>Amanda Piterson</b><br>"
                + "<span style='color:#cfe0f7; font-size:11px;'>Manager</span></html>");

        userPanel.add(avatar, BorderLayout.WEST);
        userPanel.add(nameLabel, BorderLayout.CENTER);

        return userPanel;
    }
}