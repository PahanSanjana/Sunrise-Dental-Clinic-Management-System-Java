package view;

import model.User;
import model.User.UserRole;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

public class SidebarPanel extends JPanel {

    private static final Color PRIMARY_DARK = Color.decode("#2F3E3C");
    private static final Color MINT_ACCENT = Color.decode("#BDDBD1");
    private static final Color SOFT_BG = Color.decode("#FBF9F1");
    private static final Color LIGHT_SURFACE = Color.decode("#E7E9E3");
    private static final Color HOVER_SURFACE = Color.decode("#E8F0F1");
    private static final Color SECONDARY_ACCENT = Color.decode("#C7E7EC");
    private static final Color SECONDARY_TEXT = new Color(122, 138, 135);

    private static final String FONT_FAMILY = "Segoe UI";
    private static final Font FONT_BRAND = new Font(FONT_FAMILY, Font.BOLD, 18);
    private static final Font FONT_BRAND_SUB = new Font(FONT_FAMILY, Font.PLAIN, 11);
    private static final Font FONT_GROUP = new Font(FONT_FAMILY, Font.BOLD, 14);
    private static final Font FONT_CHILD = new Font(FONT_FAMILY, Font.PLAIN, 13);
    private static final Font FONT_NAME = new Font(FONT_FAMILY, Font.BOLD, 13);
    private static final Font FONT_ROLE = new Font(FONT_FAMILY, Font.PLAIN, 11);

    private static final int SIDEBAR_WIDTH = 260;
    private static final int ROW_ARC = 10;
    private static final int GROUP_ROW_HEIGHT = 44;
    private static final int CHILD_ROW_HEIGHT = 38;
    private static final int ROW_SIDE_MARGIN = 14;
    private static final int ICON_SIZE = 18;

    private final MainFrame mainFrame;
    private RoundedRow activeRow;
    private JPanel navListPanel;
    private UserRole currentRole;
    private JLabel userRoleLabel;
    private JLabel userNameLabel;

    public SidebarPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.currentRole = UserRole.ADMIN; // Default

        setLayout(new BorderLayout());
        setBackground(SOFT_BG);
        setPreferredSize(new Dimension(SIDEBAR_WIDTH, 0));
        setBorder(new MatteBorder(0, 0, 0, 1, LIGHT_SURFACE));

        add(buildBrandHeader(), BorderLayout.NORTH);
        buildNavigation();
        add(buildUserFooter(), BorderLayout.SOUTH);
    }

    public void configureForRole(UserRole role) {
        this.currentRole = role;
        buildNavigation();
        updateUserFooter();
        revalidate();
        repaint();
    }

    private void buildNavigation() {
        if (navListPanel != null) {
            remove(navListPanel);
        }
        
        navListPanel = new JPanel();
        navListPanel.setLayout(new BoxLayout(navListPanel, BoxLayout.Y_AXIS));
        navListPanel.setBackground(SOFT_BG);
        navListPanel.setBorder(new EmptyBorder(8, 0, 8, 0));

        for (NavGroup group : buildNavData()) {
            navListPanel.add(buildGroup(group));
        }

        JScrollPane scrollPane = new JScrollPane(navListPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(SOFT_BG);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(14);

        add(scrollPane, BorderLayout.CENTER);
    }

    private List<NavGroup> buildNavData() {
        List<NavGroup> groups = new ArrayList<>();
        
        // Dashboard - All roles
        groups.add(new NavGroup(IconType.DASHBOARD, "Dashboard", "DASHBOARD"));

        // Patients - All roles can see, but Dentist cannot add
        NavGroup patients = new NavGroup(IconType.PATIENTS, "Patients", null);
        patients.children.add(new NavChild("Patient List", "PATIENT_LIST"));
        
        // Only ADMIN and RECEPTION can add patients
        if (currentRole == UserRole.ADMIN || currentRole == UserRole.RECEPTION) {
            patients.children.add(new NavChild("Add Patient", "PATIENT_ADD"));
        }
        patients.children.add(new NavChild("Patient Details", "PATIENT_DETAILS"));
        groups.add(patients);

        // Appointments - Different access for different roles
        NavGroup appointments = new NavGroup(IconType.APPOINTMENTS, "Appointments", null);
        
        if (currentRole == UserRole.PATIENT) {
            // Patients only see booking and their details
            appointments.children.add(new NavChild("Book Appointment", "APPOINTMENT_BOOK"));
            appointments.children.add(new NavChild("Appointment Details", "APPOINTMENT_DETAILS"));
        } else {
            // Other roles see full appointment management
            appointments.children.add(new NavChild("Appointment List", "APPOINTMENT_LIST"));
            appointments.children.add(new NavChild("Book Appointment", "APPOINTMENT_BOOK"));
            appointments.children.add(new NavChild("Appointment Details", "APPOINTMENT_DETAILS"));
            appointments.children.add(new NavChild("Daily Schedule", "APPOINTMENT_SCHEDULE"));
        }
        groups.add(appointments);

        // Billing
        NavGroup billing = new NavGroup(IconType.BILLING, "Billing", null);
        if (currentRole == UserRole.PATIENT) {
            // Patients only see their bill details
            billing.children.add(new NavChild("Bill Details", "BILL_DETAILS"));
        } else {
            billing.children.add(new NavChild("Bill List", "BILL_LIST"));
            billing.children.add(new NavChild("Generate Bill", "BILL_GENERATE"));
            billing.children.add(new NavChild("Bill Details", "BILL_DETAILS"));
        }
        groups.add(billing);

        // Reports
        NavGroup reports = new NavGroup(IconType.REPORTS, "Reports", null);
        if (currentRole == UserRole.PATIENT) {
            // Patients only see patient report
            reports.children.add(new NavChild("Patient Report", "REPORT_PATIENT"));
        } else {
            reports.children.add(new NavChild("Report Dashboard", "REPORT_DASHBOARD"));
            reports.children.add(new NavChild("Revenue Report", "REPORT_REVENUE"));
            reports.children.add(new NavChild("Schedule Report", "REPORT_SCHEDULE"));
            reports.children.add(new NavChild("Patient Report", "REPORT_PATIENT"));
        }
        groups.add(reports);

        // Staff - Only ADMIN can access
        if (currentRole == UserRole.ADMIN) {
            NavGroup staff = new NavGroup(IconType.STAFF, "Staff", null);
            staff.children.add(new NavChild("Staff List", "STAFF_LIST"));
            staff.children.add(new NavChild("Add Staff", "STAFF_ADD"));
            staff.children.add(new NavChild("Staff Details", "STAFF_DETAILS"));
            groups.add(staff);
        }

        // Dentists - Only ADMIN can access
        if (currentRole == UserRole.ADMIN) {
            NavGroup dentists = new NavGroup(IconType.DENTISTS, "Dentists", null);
            dentists.children.add(new NavChild("Dentist List", "DENTIST_LIST"));
            dentists.children.add(new NavChild("Add Dentist", "DENTIST_ADD"));
            groups.add(dentists);
        }

        // Treatments
        NavGroup treatments = new NavGroup(IconType.TREATMENTS, "Treatments", null);
        if (currentRole == UserRole.PATIENT) {
            // Patients only see treatment list
            treatments.children.add(new NavChild("Treatment List", "TREATMENT_LIST"));
        } else {
            treatments.children.add(new NavChild("Treatment List", "TREATMENT_LIST"));
            treatments.children.add(new NavChild("Add Treatment", "TREATMENT_ADD"));
        }
        groups.add(treatments);

        // Audit Log - Only ADMIN can access
        if (currentRole == UserRole.ADMIN) {
            NavGroup audit = new NavGroup(IconType.AUDIT, "Audit Logs", null);
            audit.children.add(new NavChild("Activity Log", "AUDIT_ACTIVITY"));
            audit.children.add(new NavChild("Login History", "AUDIT_LOGIN"));
            groups.add(audit);
        }

        return groups;
    }

    private JPanel buildBrandHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(SOFT_BG);
        header.setBorder(new EmptyBorder(28, 24, 20, 24));

        JLabel title = new JLabel("Sunrise Dental");
        title.setFont(FONT_BRAND);
        title.setForeground(PRIMARY_DARK);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Clinic Management System");
        subtitle.setFont(FONT_BRAND_SUB);
        subtitle.setForeground(SECONDARY_TEXT);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel divider = new JPanel();
        divider.setBackground(MINT_ACCENT);
        divider.setAlignmentX(Component.LEFT_ALIGNMENT);
        divider.setMaximumSize(new Dimension(32, 3));
        divider.setPreferredSize(new Dimension(32, 3));

        header.add(title);
        header.add(Box.createRigidArea(new Dimension(0, 3)));
        header.add(subtitle);
        header.add(Box.createRigidArea(new Dimension(0, 16)));
        header.add(divider);

        return header;
    }

    private JPanel buildGroup(NavGroup group) {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBackground(SOFT_BG);
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrapper.setBorder(new EmptyBorder(2, 0, 2, 0));

        boolean isLeaf = group.cardName != null;

        JPanel childContainer = new JPanel();
        childContainer.setLayout(new BoxLayout(childContainer, BoxLayout.Y_AXIS));
        childContainer.setBackground(SOFT_BG);
        childContainer.setVisible(false);

        JLabel chevron = new JLabel(isLeaf ? "" : "▼");
        chevron.setFont(new Font(FONT_FAMILY, Font.BOLD, 10));
        chevron.setForeground(SECONDARY_TEXT);

        RoundedRow groupRow = new RoundedRow(GROUP_ROW_HEIGHT);
        groupRow.build(group.icon, group.label, FONT_GROUP, chevron);

        if (isLeaf) {
            groupRow.addMouseListener(new RowClickHandler(groupRow, () -> selectLeaf(groupRow, group.cardName)));
        } else {
            groupRow.addMouseListener(new RowClickHandler(groupRow, () -> {
                boolean expanding = !childContainer.isVisible();
                childContainer.setVisible(expanding);
                chevron.setText(expanding ? "▲" : "▼");
                wrapper.revalidate();
            }));

            for (NavChild child : group.children) {
                RoundedRow childRow = new RoundedRow(CHILD_ROW_HEIGHT);
                childRow.buildChild(child.label);
                childRow.addMouseListener(new RowClickHandler(childRow, () -> selectLeaf(childRow, child.cardName)));
                childContainer.add(childRow);
                childContainer.add(Box.createRigidArea(new Dimension(0, 2)));
            }
        }

        wrapper.add(groupRow);
        wrapper.add(childContainer);
        return wrapper;
    }

    private void selectLeaf(RoundedRow row, String cardName) {
        mainFrame.showCard(cardName);
        if (activeRow != null) {
            activeRow.setActive(false);
        }
        row.setActive(true);
        activeRow = row;
    }

    private JPanel buildUserFooter() {
        JPanel footer = new JPanel(new BorderLayout(12, 0));
        footer.setBackground(SOFT_BG);
        footer.setBorder(new CompoundBorder(
                new MatteBorder(1, 0, 0, 0, LIGHT_SURFACE),
                new EmptyBorder(16, 24, 16, 20)
        ));
        footer.setPreferredSize(new Dimension(0, 80));

        // Avatar
        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(SECONDARY_ACCENT);
                g2.fillOval(0, 0, getWidth(), getHeight());
                
                // Draw user initial
                g2.setColor(PRIMARY_DARK);
                g2.setFont(new Font(FONT_FAMILY, Font.BOLD, 16));
                FontMetrics fm = g2.getFontMetrics();
                String initial = "U";
                int textX = (getWidth() - fm.stringWidth(initial)) / 2;
                int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(initial, textX, textY);
                
                g2.dispose();
            }
        };
        avatar.setOpaque(false);
        avatar.setPreferredSize(new Dimension(38, 38));

        JPanel textStack = new JPanel();
        textStack.setLayout(new BoxLayout(textStack, BoxLayout.Y_AXIS));
        textStack.setOpaque(false);

        userNameLabel = new JLabel("User");
        userNameLabel.setFont(FONT_NAME);
        userNameLabel.setForeground(PRIMARY_DARK);
        userNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        userRoleLabel = new JLabel("Role");
        userRoleLabel.setFont(FONT_ROLE);
        userRoleLabel.setForeground(SECONDARY_TEXT);
        userRoleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        textStack.add(userNameLabel);
        textStack.add(Box.createRigidArea(new Dimension(0, 2)));
        textStack.add(userRoleLabel);

        footer.add(avatar, BorderLayout.WEST);
        footer.add(textStack, BorderLayout.CENTER);

        return footer;
    }

    private void updateUserFooter() {
        User currentUser = model.LoginSession.getInstance().getCurrentUser();
        if (currentUser != null) {
            userNameLabel.setText(currentUser.getUsername());
            userRoleLabel.setText(currentUser.getRole().name());
        }
    }

    // Inner class for RowClickHandler
    private class RowClickHandler extends MouseAdapter {
        private final RoundedRow row;
        private final Runnable onClick;

        RowClickHandler(RoundedRow row, Runnable onClick) {
            this.row = row;
            this.onClick = onClick;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            onClick.run();
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            row.setHovered(true);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            row.setHovered(false);
        }
    }

    // Inner class for RoundedRow
    private static class RoundedRow extends JPanel {
        private final int rowHeight;
        private boolean hovered = false;
        private boolean active = false;
        private boolean isChildRow = false;
        private IconCanvas iconCanvas;
        private JLabel textLabel;

        RoundedRow(int rowHeight) {
            this.rowHeight = rowHeight;
            setOpaque(false);
            setLayout(new BorderLayout(10, 0));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            Dimension size = new Dimension(SIDEBAR_WIDTH - (ROW_SIDE_MARGIN * 2), rowHeight);
            setPreferredSize(size);
            setMinimumSize(size);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, rowHeight));
            setAlignmentX(Component.LEFT_ALIGNMENT);
            setBorder(new EmptyBorder(0, 14, 0, 12));
        }

        void build(IconType icon, String label, Font labelFont, JLabel trailing) {
            iconCanvas = new IconCanvas(icon);

            textLabel = new JLabel(label);
            textLabel.setFont(labelFont);
            textLabel.setForeground(PRIMARY_DARK);
            textLabel.setVerticalAlignment(SwingConstants.CENTER);

            JPanel left = new JPanel(new BorderLayout(12, 0));
            left.setOpaque(false);
            left.add(iconCanvas, BorderLayout.WEST);
            left.add(textLabel, BorderLayout.CENTER);

            add(left, BorderLayout.CENTER);
            if (trailing != null) {
                add(trailing, BorderLayout.EAST);
            }
        }

        void buildChild(String label) {
            isChildRow = true;
            textLabel = new JLabel(label);
            textLabel.setFont(FONT_CHILD);
            textLabel.setForeground(SECONDARY_TEXT);
            setBorder(new EmptyBorder(0, 44, 0, 12));
            add(textLabel, BorderLayout.CENTER);
        }

        void setHovered(boolean hovered) {
            this.hovered = hovered;
            repaint();
        }

        void setActive(boolean active) {
            this.active = active;
            Color textColor = active ? PRIMARY_DARK : (isChildRow ? SECONDARY_TEXT : PRIMARY_DARK);
            textLabel.setForeground(textColor);
            if (iconCanvas != null) {
                iconCanvas.setColor(PRIMARY_DARK);
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (active) {
                g2.setColor(MINT_ACCENT);
                g2.fill(new RoundRectangle2D.Float(4, 0, getWidth() - 4, getHeight(), ROW_ARC, ROW_ARC));
                g2.setColor(PRIMARY_DARK);
                g2.fill(new RoundRectangle2D.Float(0, 6, 4, getHeight() - 12, 2, 2));
            } else if (hovered) {
                g2.setColor(HOVER_SURFACE);
                g2.fill(new RoundRectangle2D.Float(4, 0, getWidth() - 4, getHeight(), ROW_ARC, ROW_ARC));
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

    // Inner class for IconCanvas
    private static class IconCanvas extends JPanel {
        private final IconType type;
        private Color color = PRIMARY_DARK;

        IconCanvas(IconType type) {
            this.type = type;
            setOpaque(false);
            setPreferredSize(new Dimension(ICON_SIZE, ICON_SIZE));
            setMinimumSize(new Dimension(ICON_SIZE, ICON_SIZE));
            setMaximumSize(new Dimension(ICON_SIZE, ICON_SIZE));
        }

        void setColor(Color color) {
            this.color = color;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(color);

            int offsetX = (getWidth() - ICON_SIZE) / 2;
            int offsetY = (getHeight() - ICON_SIZE) / 2;
            g2.translate(offsetX, offsetY);

            int s = ICON_SIZE;
            switch (type) {
                case DASHBOARD:
                    g2.drawRoundRect(1, 1, 6, 6, 2, 2);
                    g2.drawRoundRect(11, 1, 6, 6, 2, 2);
                    g2.drawRoundRect(1, 11, 6, 6, 2, 2);
                    g2.drawRoundRect(11, 11, 6, 6, 2, 2);
                    break;

                case PATIENTS:
                    g2.drawOval(6, 1, 6, 6);
                    g2.drawArc(2, 9, 14, 12, 0, 180);
                    break;

                case APPOINTMENTS:
                    g2.drawRoundRect(2, 4, 14, 12, 3, 3);
                    g2.drawLine(6, 1, 6, 5);
                    g2.drawLine(12, 1, 12, 5);
                    g2.drawLine(2, 9, 16, 9);
                    break;

                case BILLING:
                    g2.drawRoundRect(3, 1, 12, 16, 2, 2);
                    g2.drawLine(6, 6, 14, 6);
                    g2.drawLine(6, 10, 14, 10);
                    g2.drawLine(6, 14, 11, 14);
                    break;

                case REPORTS:
                    g2.drawLine(2, 16, 16, 16);
                    g2.drawLine(5, 13, 5, 16);
                    g2.drawLine(9, 9, 9, 16);
                    g2.drawLine(13, 4, 13, 16);
                    break;

                case STAFF:
                    g2.drawOval(2, 2, 6, 6);
                    g2.drawArc(0, 10, 11, 9, 0, 180);
                    g2.drawOval(10, 3, 6, 6);
                    g2.drawArc(8, 11, 11, 9, 0, 180);
                    break;

                case DENTISTS:
                    g2.drawArc(3, 1, 12, 10, 0, 180);
                    g2.drawLine(3, 6, 3, 9);
                    g2.drawLine(15, 6, 15, 9);
                    g2.drawLine(3, 9, 7, 16);
                    g2.drawLine(15, 9, 11, 16);
                    break;

                case TREATMENTS:
                    g2.drawLine(9, 2, 9, 16);
                    g2.drawLine(2, 9, 16, 9);
                    break;

                case AUDIT:
                    for (int row = 0; row < 3; row++) {
                        int y = 3 + (row * 6);
                        g2.fillOval(1, y - 1, 3, 3);
                        g2.drawLine(7, y, 16, y);
                    }
                    break;
            }
            g2.dispose();
        }
    }

    // Inner classes for navigation data
    private static class NavGroup {
        IconType icon;
        String label;
        String cardName;
        List<NavChild> children = new ArrayList<>();

        NavGroup(IconType icon, String label, String cardName) {
            this.icon = icon;
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

    // IconType enum
    private enum IconType {
        DASHBOARD, PATIENTS, APPOINTMENTS, BILLING, REPORTS, STAFF, DENTISTS, TREATMENTS, AUDIT
    }
}