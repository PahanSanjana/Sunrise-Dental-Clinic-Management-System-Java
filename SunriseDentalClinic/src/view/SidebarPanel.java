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
 * SidebarPanel - JDK 26 compatible. No Unicode symbols, no custom fonts.
 * Uses only standard Swing APIs available since Java 8+.
 */
public class SidebarPanel extends JPanel {

    private static final Color SIDEBAR_BG     = new Color(251, 249, 241);
    private static final Color PRIMARY_TEXT   = new Color(47, 62, 60);
    private static final Color SECONDARY_TEXT = new Color(122, 138, 135);
    private static final Color ACTIVE_BG      = new Color(189, 219, 209);
    private static final Color HOVER_BG       = new Color(232, 240, 241);
    private static final Color ACCENT         = new Color(199, 231, 236);
    private static final Color DIVIDER        = new Color(217, 223, 218);
    private static final Color FOOTER_BG      = new Color(242, 240, 232);

    private static final Font FONT_GROUP = new Font(Font.SANS_SERIF, Font.BOLD, 14);
    private static final Font FONT_CHILD = new Font(Font.SANS_SERIF, Font.PLAIN, 13);
    private static final Font FONT_NAME  = new Font(Font.SANS_SERIF, Font.BOLD, 13);
    private static final Font FONT_ROLE  = new Font(Font.SANS_SERIF, Font.PLAIN, 11);
    private static final Font FONT_TITLE = new Font(Font.SANS_SERIF, Font.BOLD, 20);
    private static final Font FONT_SUB   = new Font(Font.SANS_SERIF, Font.PLAIN, 11);

    private final MainFrame mainFrame;
    private JPanel activeRow;

    private static class NavGroup {
        String label;
        String cardName;
        List<NavChild> children = new ArrayList<>();
        NavGroup(String label, String cardName) {
            this.label = label;
            this.cardName = cardName;
        }
    }

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
        setBackground(SIDEBAR_BG);
        setPreferredSize(new Dimension(270, 0));
        setBorder(new MatteBorder(0, 0, 0, 1, DIVIDER));
        add(buildBrandHeader(), BorderLayout.NORTH);
        add(buildScrollableNav(), BorderLayout.CENTER);
        add(buildUserFooter(), BorderLayout.SOUTH);
    }

    private JPanel buildBrandHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(SIDEBAR_BG);
        header.setBorder(new EmptyBorder(28, 24, 0, 24));

        JLabel title = new JLabel("SUNRISE DENTAL");
        title.setFont(FONT_TITLE);
        title.setForeground(PRIMARY_TEXT);

        JLabel subtitle = new JLabel("Clinic Management System");
        subtitle.setFont(FONT_SUB);
        subtitle.setForeground(SECONDARY_TEXT);

        JPanel textStack = new JPanel();
        textStack.setLayout(new BoxLayout(textStack, BoxLayout.Y_AXIS));
        textStack.setBackground(SIDEBAR_BG);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        textStack.add(title);
        textStack.add(Box.createRigidArea(new Dimension(0, 3)));
        textStack.add(subtitle);

        JSeparator sep = new JSeparator();
        sep.setForeground(ACCENT);
        sep.setBackground(ACCENT);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);

        textStack.add(Box.createRigidArea(new Dimension(0, 16)));
        textStack.add(sep);

        header.add(textStack, BorderLayout.CENTER);
        return header;
    }

    private JScrollPane buildScrollableNav() {
        JPanel navListPanel = new JPanel();
        navListPanel.setLayout(new BoxLayout(navListPanel, BoxLayout.Y_AXIS));
        navListPanel.setBackground(SIDEBAR_BG);
        navListPanel.setBorder(new EmptyBorder(12, 0, 12, 0));

        for (NavGroup group : buildNavData()) {
            navListPanel.add(buildGroup(group));
        }

        JScrollPane scrollPane = new JScrollPane(navListPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(SIDEBAR_BG);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(14);
        return scrollPane;
    }

    private List<NavGroup> buildNavData() {
        List<NavGroup> groups = new ArrayList<>();
        groups.add(new NavGroup("Dashboard", "DASHBOARD"));

        NavGroup patients = new NavGroup("Patients", null);
        patients.children.add(new NavChild("Patient List", "PATIENT_LIST"));
        patients.children.add(new NavChild("Add Patient", "PATIENT_ADD"));
        patients.children.add(new NavChild("Patient Details", "PATIENT_DETAILS"));
        groups.add(patients);

        NavGroup appointments = new NavGroup("Appointments", null);
        appointments.children.add(new NavChild("Appointment List", "APPOINTMENT_LIST"));
        appointments.children.add(new NavChild("Book Appointment", "APPOINTMENT_BOOK"));
        appointments.children.add(new NavChild("Appointment Details", "APPOINTMENT_DETAILS"));
        appointments.children.add(new NavChild("Daily Schedule", "APPOINTMENT_SCHEDULE"));
        groups.add(appointments);

        NavGroup billing = new NavGroup("Billing", null);
        billing.children.add(new NavChild("Bill List", "BILL_LIST"));
        billing.children.add(new NavChild("Generate Bill", "BILL_GENERATE"));
        billing.children.add(new NavChild("Bill Details", "BILL_DETAILS"));
        groups.add(billing);

        NavGroup reports = new NavGroup("Reports", null);
        reports.children.add(new NavChild("Report Dashboard", "REPORT_DASHBOARD"));
        reports.children.add(new NavChild("Revenue Report", "REPORT_REVENUE"));
        reports.children.add(new NavChild("Schedule Report", "REPORT_SCHEDULE"));
        reports.children.add(new NavChild("Patient Report", "REPORT_PATIENT"));
        groups.add(reports);

        NavGroup staff = new NavGroup("Staff", null);
        staff.children.add(new NavChild("Staff List", "STAFF_LIST"));
        staff.children.add(new NavChild("Add Staff", "STAFF_ADD"));
        staff.children.add(new NavChild("Staff Details", "STAFF_DETAILS"));
        groups.add(staff);

        NavGroup dentists = new NavGroup("Dentists", null);
        dentists.children.add(new NavChild("Dentist List", "DENTIST_LIST"));
        dentists.children.add(new NavChild("Add Dentist", "DENTIST_ADD"));
        groups.add(dentists);

        NavGroup treatments = new NavGroup("Treatments", null);
        treatments.children.add(new NavChild("Treatment List", "TREATMENT_LIST"));
        treatments.children.add(new NavChild("Add Treatment", "TREATMENT_ADD"));
        groups.add(treatments);

        NavGroup audit = new NavGroup("Audit Logs", null);
        audit.children.add(new NavChild("Activity Log", "AUDIT_ACTIVITY"));
        audit.children.add(new NavChild("Login History", "AUDIT_LOGIN"));
        groups.add(audit);

        return groups;
    }

    private JPanel buildGroup(NavGroup group) {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBackground(SIDEBAR_BG);
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

        boolean isLeaf = group.cardName != null;

        JPanel childContainer = new JPanel();
        childContainer.setLayout(new BoxLayout(childContainer, BoxLayout.Y_AXIS));
        childContainer.setBackground(SIDEBAR_BG);
        childContainer.setVisible(false);

        JLabel chevron = new JLabel(isLeaf ? "" : ">");
        chevron.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        chevron.setForeground(SECONDARY_TEXT);

        JPanel groupRow = buildRow(group.label, FONT_GROUP, PRIMARY_TEXT, chevron);
        groupRow.setPreferredSize(new Dimension(260, 48));
        groupRow.setMinimumSize(new Dimension(260, 48));
        groupRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

        if (isLeaf) {
            groupRow.addMouseListener(new RowClickHandler(groupRow, () -> selectLeaf(groupRow, group.cardName)));
        } else {
            groupRow.addMouseListener(new RowClickHandler(groupRow, () -> {
                boolean expanding = !childContainer.isVisible();
                childContainer.setVisible(expanding);
                chevron.setText(expanding ? "v" : ">");
                wrapper.revalidate();
                wrapper.repaint();
            }));

            for (NavChild child : group.children) {
                JPanel childRow = buildChildRow(child.label);
                childRow.setPreferredSize(new Dimension(260, 40));
                childRow.setMinimumSize(new Dimension(260, 40));
                childRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
                childRow.addMouseListener(new RowClickHandler(childRow, () -> selectLeaf(childRow, child.cardName)));
                childContainer.add(childRow);
            }
        }

        wrapper.add(groupRow);
        wrapper.add(childContainer);
        return wrapper;
    }

    private JPanel buildRow(String label, Font font, Color textColor, JLabel trailing) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setBackground(SIDEBAR_BG);
        row.setBorder(new EmptyBorder(0, 24, 0, 20));
        row.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel textLabel = new JLabel(label);
        textLabel.setFont(font);
        textLabel.setForeground(textColor);

        row.add(textLabel, BorderLayout.CENTER);
        if (trailing != null) {
            row.add(trailing, BorderLayout.EAST);
        }
        return row;
    }

    private JPanel buildChildRow(String label) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(SIDEBAR_BG);
        row.setBorder(new EmptyBorder(0, 40, 0, 20));
        row.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel textLabel = new JLabel(label);
        textLabel.setFont(FONT_CHILD);
        textLabel.setForeground(SECONDARY_TEXT);

        row.add(textLabel, BorderLayout.CENTER);
        return row;
    }

    private void selectLeaf(JPanel row, String cardName) {
        mainFrame.showCard(cardName);
        highlightActive(row);
    }

    private void highlightActive(JPanel row) {
        if (activeRow != null) {
            resetRowStyle(activeRow);
        }
        row.setBackground(ACTIVE_BG);
        row.setBorder(BorderFactory.createMatteBorder(0, 4, 0, 0, PRIMARY_TEXT));
        setRowTextColor(row, PRIMARY_TEXT);
        activeRow = row;
    }

    private void resetRowStyle(JPanel row) {
        boolean child = isChildRow(row);
        row.setBorder(new EmptyBorder(0, child ? 40 : 24, 0, 20));
        row.setBackground(SIDEBAR_BG);
        setRowTextColor(row, child ? SECONDARY_TEXT : PRIMARY_TEXT);
    }

    private boolean isChildRow(JPanel row) {
        if (row.getBorder() instanceof EmptyBorder) {
            EmptyBorder eb = (EmptyBorder) row.getBorder();
            return eb.getBorderInsets().left >= 40;
        }
        return false;
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
                row.setBackground(HOVER_BG);
                setRowTextColor(row, PRIMARY_TEXT);
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if (row != activeRow) {
                row.setBackground(SIDEBAR_BG);
                setRowTextColor(row, isChildRow(row) ? SECONDARY_TEXT : PRIMARY_TEXT);
            }
        }
    }

    private JPanel buildUserFooter() {
        JPanel footer = new JPanel(new BorderLayout(12, 0));
        footer.setBackground(FOOTER_BG);
        footer.setPreferredSize(new Dimension(0, 68));
        footer.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(1, 0, 0, 0, DIVIDER),
            new EmptyBorder(16, 22, 16, 18)
        ));

        // Painted circle avatar - no Unicode, works on every JDK and OS
        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ACCENT);
                int d = Math.min(getWidth(), getHeight()) - 4;
                int x = (getWidth() - d) / 2;
                int y = (getHeight() - d) / 2;
                g2.fillOval(x, y, d, d);
                g2.dispose();
            }
        };
        avatar.setOpaque(false);
        avatar.setPreferredSize(new Dimension(36, 36));

        JPanel textStack = new JPanel();
        textStack.setLayout(new BoxLayout(textStack, BoxLayout.Y_AXIS));
        textStack.setOpaque(false);

        JLabel name = new JLabel("Amanda Peterson");
        name.setFont(FONT_NAME);
        name.setForeground(PRIMARY_TEXT);
        name.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel role = new JLabel("Clinic Manager");
        role.setFont(FONT_ROLE);
        role.setForeground(SECONDARY_TEXT);
        role.setAlignmentX(Component.LEFT_ALIGNMENT);

        textStack.add(name);
        textStack.add(Box.createRigidArea(new Dimension(0, 2)));
        textStack.add(role);

        footer.add(avatar, BorderLayout.WEST);
        footer.add(textStack, BorderLayout.CENTER);

        return footer;
    }
}