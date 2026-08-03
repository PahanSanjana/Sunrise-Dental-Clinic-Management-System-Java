package view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

/**
 * SidebarPanel - professional, collapsible left navigation.
 *
 * Structure: a set of NavGroups, each either a single leaf item (Dashboard)
 * or a parent with an accordion of NavChild items underneath it.
 *
 * Pure View layer: on selection it only calls MainFrame.showCard(cardName).
 * No business logic, no data access.
 */
public class SidebarPanel extends JPanel {

    // ---- Dental Clinic palette ----------------------------------------
    private static final Color CHARCOAL      = Color.decode("#2F3E3C"); // sidebar background
    private static final Color MINT          = Color.decode("#BDDBD1"); // active accent
    private static final Color LIGHT_MINT_BG = Color.decode("#E8F0F1"); // hover tint (on dark, used as text tint)
    private static final Color SKY           = Color.decode("#C7E7EC"); // secondary accent
    private static final Color CREAM         = Color.decode("#FBF9F1"); // primary text on dark
    private static final Color MUTED_TEXT    = new Color(255, 255, 255, 140); // dimmed labels
    private static final Color FOOTER_BG     = Color.decode("#26332F"); // slightly darker than charcoal
    private static final Color DIVIDER       = new Color(255, 255, 255, 20);

    private static final Font FONT_GROUP = new Font("SansSerif", Font.BOLD, 13);
    private static final Font FONT_CHILD = new Font("SansSerif", Font.PLAIN, 13);
    private static final Font FONT_NAME  = new Font("SansSerif", Font.BOLD, 13);
    private static final Font FONT_ROLE  = new Font("SansSerif", Font.PLAIN, 11);

    private final MainFrame mainFrame;
    private JPanel navListPanel;
    private JLabel activeIndicatorTarget; // currently selected row (for accent bar)

    /** One expandable/leaf navigation group, e.g. "Patient Management". */
    private static class NavGroup {
        String icon;
        String label;
        String cardName;      // non-null only for leaf groups (e.g. Dashboard)
        List<NavChild> children = new ArrayList<>();

        NavGroup(String icon, String label, String cardName) {
            this.icon = icon;
            this.label = label;
            this.cardName = cardName;
        }
    }

    /** One sub-item under a group, e.g. "Patient List". */
    private static class NavChild {
        String label;
        String cardName;

        NavChild(String label, String cardName) {
            this.label = label;
            this.cardName = cardName;
        }
    }

    public SidebarPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout());
        setBackground(CHARCOAL);
        setPreferredSize(new Dimension(270, 0));
        setBorder(new MatteBorder(0, 0, 0, 1, DIVIDER));

        add(buildBrandHeader(), BorderLayout.NORTH);
        add(buildScrollableNav(), BorderLayout.CENTER);
        add(buildUserFooter(), BorderLayout.SOUTH);
    }

    // ---------------------------------------------------------------
    // Brand header
    // ---------------------------------------------------------------
    private JPanel buildBrandHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(CHARCOAL);
        header.setBorder(new EmptyBorder(26, 24, 22, 24));

        JLabel title = new JLabel("Sunrise Dental");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(CREAM);

        JLabel subtitle = new JLabel("Clinic Management");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 11));
        subtitle.setForeground(MUTED_TEXT);

        JPanel textStack = new JPanel();
        textStack.setLayout(new BoxLayout(textStack, BoxLayout.Y_AXIS));
        textStack.setBackground(CHARCOAL);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        textStack.add(title);
        textStack.add(Box.createRigidArea(new Dimension(0, 2)));
        textStack.add(subtitle);

        header.add(textStack, BorderLayout.WEST);
        return header;
    }

    // ---------------------------------------------------------------
    // Scrollable nav list
    // ---------------------------------------------------------------
    private JScrollPane buildScrollableNav() {
        navListPanel = new JPanel();
        navListPanel.setLayout(new BoxLayout(navListPanel, BoxLayout.Y_AXIS));
        navListPanel.setBackground(CHARCOAL);
        navListPanel.setBorder(new EmptyBorder(8, 0, 8, 0));

        for (NavGroup group : buildNavData()) {
            navListPanel.add(buildGroup(group));
        }

        JScrollPane scrollPane = new JScrollPane(navListPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(CHARCOAL);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(14);
        return scrollPane;
    }

    /** The full navigation tree, matching the required structure. */
    private List<NavGroup> buildNavData() {
        List<NavGroup> groups = new ArrayList<>();

        groups.add(new NavGroup("\u25A3", "Dashboard", "DASHBOARD")); // leaf

        NavGroup patients = new NavGroup("\uD83D\uDC64", "Patient Management", null);
        patients.children.add(new NavChild("Patient List", "PATIENT_LIST"));
        patients.children.add(new NavChild("Add Patient", "PATIENT_ADD"));
        patients.children.add(new NavChild("Patient Details", "PATIENT_DETAILS"));
        groups.add(patients);

        NavGroup appointments = new NavGroup("\uD83D\uDCC5", "Appointment Management", null);
        appointments.children.add(new NavChild("Appointment List", "APPOINTMENT_LIST"));
        appointments.children.add(new NavChild("Book Appointment", "APPOINTMENT_BOOK"));
        appointments.children.add(new NavChild("Appointment Details", "APPOINTMENT_DETAILS"));
        appointments.children.add(new NavChild("Daily Schedule", "APPOINTMENT_SCHEDULE"));
        groups.add(appointments);

        NavGroup billing = new NavGroup("\uD83D\uDCB0", "Billing Management", null);
        billing.children.add(new NavChild("Bill List", "BILL_LIST"));
        billing.children.add(new NavChild("Generate Bill", "BILL_GENERATE"));
        billing.children.add(new NavChild("Bill Details", "BILL_DETAILS"));
        groups.add(billing);

        NavGroup reports = new NavGroup("\uD83D\uDCC8", "Reports & Analytics", null);
        reports.children.add(new NavChild("Report Dashboard", "REPORT_DASHBOARD"));
        reports.children.add(new NavChild("Revenue Report", "REPORT_REVENUE"));
        reports.children.add(new NavChild("Schedule Report", "REPORT_SCHEDULE"));
        reports.children.add(new NavChild("Patient Report", "REPORT_PATIENT"));
        groups.add(reports);

        NavGroup staff = new NavGroup("\uD83D\uDC65", "Staff Management", null);
        staff.children.add(new NavChild("Staff List", "STAFF_LIST"));
        staff.children.add(new NavChild("Add Staff", "STAFF_ADD"));
        staff.children.add(new NavChild("Staff Details", "STAFF_DETAILS"));
        groups.add(staff);

        NavGroup dentists = new NavGroup("\uD83E\uDDB7", "Dentist Management", null);
        dentists.children.add(new NavChild("Dentist List", "DENTIST_LIST"));
        dentists.children.add(new NavChild("Add Dentist", "DENTIST_ADD"));
        groups.add(dentists);

        NavGroup treatments = new NavGroup("\uD83D\uDC8A", "Treatment Management", null);
        treatments.children.add(new NavChild("Treatment List", "TREATMENT_LIST"));
        treatments.children.add(new NavChild("Add Treatment", "TREATMENT_ADD"));
        groups.add(treatments);

        NavGroup audit = new NavGroup("\uD83D\uDCCB", "Audit Logs", null);
        audit.children.add(new NavChild("Activity Log", "AUDIT_ACTIVITY"));
        audit.children.add(new NavChild("Login History", "AUDIT_LOGIN"));
        groups.add(audit);

        return groups;
    }

    // ---------------------------------------------------------------
    // Build one group: leaf row, or parent row + collapsible children
    // ---------------------------------------------------------------
    private JPanel buildGroup(NavGroup group) {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBackground(CHARCOAL);
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

        boolean isLeaf = group.cardName != null;

        JPanel childContainer = new JPanel();
        childContainer.setLayout(new BoxLayout(childContainer, BoxLayout.Y_AXIS));
        childContainer.setBackground(CHARCOAL);
        childContainer.setVisible(false); // collapsed by default

        JLabel chevron = new JLabel(isLeaf ? "" : "\u2304"); // ⌄
        chevron.setFont(new Font("SansSerif", Font.PLAIN, 11));
        chevron.setForeground(MUTED_TEXT);

        JPanel groupRow = buildRow(group.icon, group.label, FONT_GROUP, CREAM, chevron);

        if (isLeaf) {
            groupRow.addMouseListener(new RowClickHandler(groupRow, () -> selectLeaf(groupRow, group.cardName)));
        } else {
            groupRow.addMouseListener(new RowClickHandler(groupRow, () -> {
                boolean expanding = !childContainer.isVisible();
                childContainer.setVisible(expanding);
                chevron.setText(expanding ? "\u2303" : "\u2304"); // ⌃ expanded / ⌄ collapsed
                wrapper.revalidate();
            }));

            for (NavChild child : group.children) {
                JPanel childRow = buildChildRow(child.label);
                childRow.addMouseListener(new RowClickHandler(childRow, () -> selectLeaf(childRow, child.cardName)));
                childContainer.add(childRow);
            }
        }

        wrapper.add(groupRow);
        wrapper.add(childContainer);
        return wrapper;
    }

    private JPanel buildRow(String icon, String label, Font font, Color textColor, JLabel trailing) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setBackground(CHARCOAL);
        row.setBorder(new EmptyBorder(11, 24, 11, 20));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        row.setCursor(new Cursor(Cursor.HAND_CURSOR));
        row.putClientProperty("baseColor", CHARCOAL);

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
        iconLabel.setForeground(textColor);
        iconLabel.setPreferredSize(new Dimension(20, 20));

        JLabel textLabel = new JLabel(label);
        textLabel.setFont(font);
        textLabel.setForeground(textColor);

        JPanel left = new JPanel(new BorderLayout(12, 0));
        left.setOpaque(false);
        left.add(iconLabel, BorderLayout.WEST);
        left.add(textLabel, BorderLayout.CENTER);

        row.add(left, BorderLayout.CENTER);
        if (trailing != null) {
            row.add(trailing, BorderLayout.EAST);
        }
        return row;
    }

    private JPanel buildChildRow(String label) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(CHARCOAL);
        row.setBorder(new EmptyBorder(9, 24, 9, 20));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        row.setCursor(new Cursor(Cursor.HAND_CURSOR));
        row.putClientProperty("baseColor", CHARCOAL);

        JLabel textLabel = new JLabel("    " + label); // indent under parent icon
        textLabel.setFont(FONT_CHILD);
        textLabel.setForeground(MUTED_TEXT);
        textLabel.putClientProperty("isChildLabel", true);

        row.add(textLabel, BorderLayout.CENTER);
        return row;
    }

    // ---------------------------------------------------------------
    // Selection + hover behaviour
    // ---------------------------------------------------------------
    private void selectLeaf(JPanel row, String cardName) {
        mainFrame.showCard(cardName);
        highlightActive(row);
    }

    private JPanel activeRow;

    private void highlightActive(JPanel row) {
        // Reset previously active row back to its base look
        if (activeRow != null) {
            resetRowStyle(activeRow);
        }
        row.setBackground(MINT);
        setRowTextColor(row, CHARCOAL);
        activeRow = row;
    }

    private void resetRowStyle(JPanel row) {
        row.setBackground(CHARCOAL);
        setRowTextColor(row, CREAM);
    }

    private void setRowTextColor(JPanel row, Color color) {
        setLabelColorsRecursively(row, color);
    }

    private void setLabelColorsRecursively(Container container, Color color) {
        for (Component c : container.getComponents()) {
            if (c instanceof JLabel) {
                ((JLabel) c).setForeground(color);
            } else if (c instanceof Container) {
                setLabelColorsRecursively((Container) c, color);
            }
        }
    }

    /** Shared hover + click handling for both group rows and child rows. */
    private class RowClickHandler extends MouseAdapter {
        private final JPanel row;
        private final Runnable onClick;

        RowClickHandler(JPanel row, Runnable onClick) {
            this.row = row;
            this.onClick = onClick;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            onClick.run();
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            if (row != activeRow) {
                row.setBackground(new Color(255, 255, 255, 18));
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if (row != activeRow) {
                row.setBackground(CHARCOAL);
            }
        }
    }

    // ---------------------------------------------------------------
    // Footer: logged-in user
    // ---------------------------------------------------------------
    private JPanel buildUserFooter() {
        JPanel footer = new JPanel(new BorderLayout(12, 0));
        footer.setBackground(FOOTER_BG);
        footer.setBorder(new EmptyBorder(16, 22, 16, 18));
        footer.setPreferredSize(new Dimension(0, 68));

        JLabel avatar = new JLabel("\u25CF");
        avatar.setFont(new Font("SansSerif", Font.PLAIN, 30));
        avatar.setForeground(SKY);
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.setPreferredSize(new Dimension(36, 36));

        JPanel textStack = new JPanel();
        textStack.setLayout(new BoxLayout(textStack, BoxLayout.Y_AXIS));
        textStack.setOpaque(false);

        JLabel name = new JLabel("Amanda Piterson");
        name.setFont(FONT_NAME);
        name.setForeground(CREAM);
        name.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel role = new JLabel("Clinic Manager");
        role.setFont(FONT_ROLE);
        role.setForeground(MUTED_TEXT);
        role.setAlignmentX(Component.LEFT_ALIGNMENT);

        textStack.add(name);
        textStack.add(Box.createRigidArea(new Dimension(0, 2)));
        textStack.add(role);

        footer.add(avatar, BorderLayout.WEST);
        footer.add(textStack, BorderLayout.CENTER);

        return footer;
    }
}