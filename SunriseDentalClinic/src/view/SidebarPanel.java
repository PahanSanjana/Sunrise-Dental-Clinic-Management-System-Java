package view;

import model.User;
import model.User.UserRole;
import model.LoginSession;
import model.RolePermissions;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

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

    // =====================================================
    // ICON HELPERS (Ikonli FontIcon)
    // =====================================================
    private static FontIcon icon(FontAwesomeSolid glyph, int size, Color color) {
        return FontIcon.of(glyph, size, color);
    }

    private static JLabel iconLabel(FontAwesomeSolid glyph, int size, Color color) {
        return new JLabel(icon(glyph, size, color));
    }

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

    /**
     * Build navigation data based on current user role
     * Uses RolePermissions to check page access
     */
    private List<NavGroup> buildNavData() {
        List<NavGroup> groups = new ArrayList<>();
        User currentUser = LoginSession.getInstance().getCurrentUser();
        UserRole role = currentUser != null ? currentUser.getRole() : currentRole;
        
        // Dashboard - All roles
        if (RolePermissions.hasPageAccess(role, "DASHBOARD")) {
            groups.add(new NavGroup(FontAwesomeSolid.TACHOMETER_ALT, "Dashboard", "DASHBOARD"));
        }

        // My Profile - All roles
        if (RolePermissions.hasPageAccess(role, "USER_PROFILE")) {
            NavGroup profile = new NavGroup(FontAwesomeSolid.USER, "My Profile", "USER_PROFILE");
            groups.add(profile);
        }

        // User Management - Admin only
        if (RolePermissions.hasPageAccess(role, "USER_MANAGEMENT")) {
            NavGroup users = new NavGroup(FontAwesomeSolid.USERS, "User Management", "USER_MANAGEMENT");
            groups.add(users);
        }

        // Patients - All roles can see (but different permissions)
        if (RolePermissions.hasPageAccess(role, "PATIENT_LIST")) {
            NavGroup patients = new NavGroup(FontAwesomeSolid.HOSPITAL, "Patients", null);
            patients.children.add(new NavChild("Patient List", "PATIENT_LIST"));
            
            // Only ADMIN and RECEPTION can add patients
            if (RolePermissions.hasActionPermission(role, "ADD_PATIENTS")) {
                patients.children.add(new NavChild("Add Patient", "PATIENT_ADD"));
            }
            groups.add(patients);
        }

        // Appointments - Different access for different roles
        if (RolePermissions.hasPageAccess(role, "APPOINTMENT_LIST") || 
            RolePermissions.hasPageAccess(role, "APPOINTMENT_BOOK") ||
            RolePermissions.hasPageAccess(role, "APPOINTMENT_SCHEDULE")) {
            
            NavGroup appointments = new NavGroup(FontAwesomeSolid.CALENDAR_ALT, "Appointments", null);
            
            if (RolePermissions.hasPageAccess(role, "APPOINTMENT_LIST")) {
                appointments.children.add(new NavChild("Appointment List", "APPOINTMENT_LIST"));
            }
            
            if (RolePermissions.hasPageAccess(role, "APPOINTMENT_BOOK")) {
                appointments.children.add(new NavChild("Book Appointment", "APPOINTMENT_BOOK"));
            }
            
            if (RolePermissions.hasPageAccess(role, "APPOINTMENT_SCHEDULE")) {
                appointments.children.add(new NavChild("Daily Schedule", "APPOINTMENT_SCHEDULE"));
            }
            
            if (!appointments.children.isEmpty()) {
                groups.add(appointments);
            }
        }

        // Billing
        if (RolePermissions.hasPageAccess(role, "BILL_LIST") || 
            RolePermissions.hasPageAccess(role, "BILL_GENERATE")) {
            
            NavGroup billing = new NavGroup(FontAwesomeSolid.FILE_INVOICE_DOLLAR, "Billing", null);
            
            if (RolePermissions.hasPageAccess(role, "BILL_LIST")) {
                billing.children.add(new NavChild(role == UserRole.PATIENT ? "My Bills" : "Bill List", "BILL_LIST"));
            }
            
            if (RolePermissions.hasPageAccess(role, "BILL_GENERATE")) {
                billing.children.add(new NavChild("Generate Bill", "BILL_GENERATE"));
            }
            
            if (!billing.children.isEmpty()) {
                groups.add(billing);
            }
        }

        // Reports
        if (RolePermissions.hasPageAccess(role, "REPORT_DASHBOARD") ||
            RolePermissions.hasPageAccess(role, "REPORT_REVENUE") ||
            RolePermissions.hasPageAccess(role, "REPORT_SCHEDULE") ||
            RolePermissions.hasPageAccess(role, "REPORT_PATIENT")) {
            
            NavGroup reports = new NavGroup(FontAwesomeSolid.CHART_BAR, "Reports", null);
            
            if (RolePermissions.hasPageAccess(role, "REPORT_DASHBOARD")) {
                reports.children.add(new NavChild("Report Dashboard", "REPORT_DASHBOARD"));
            }
            
            if (RolePermissions.hasPageAccess(role, "REPORT_REVENUE")) {
                reports.children.add(new NavChild("Revenue Report", "REPORT_REVENUE"));
            }
            
            if (RolePermissions.hasPageAccess(role, "REPORT_SCHEDULE")) {
                reports.children.add(new NavChild("Schedule Report", "REPORT_SCHEDULE"));
            }
            
            if (RolePermissions.hasPageAccess(role, "REPORT_PATIENT")) {
                reports.children.add(new NavChild("Patient Report", "REPORT_PATIENT"));
            }
            
            if (!reports.children.isEmpty()) {
                groups.add(reports);
            }
        }

        // Staff - Only ADMIN can access
        if (RolePermissions.hasPageAccess(role, "STAFF_LIST") ||
            RolePermissions.hasPageAccess(role, "STAFF_ADD")) {
            
            NavGroup staff = new NavGroup(FontAwesomeSolid.USER_TIE, "Staff", null);
            
            if (RolePermissions.hasPageAccess(role, "STAFF_LIST")) {
                staff.children.add(new NavChild("Staff List", "STAFF_LIST"));
            }
            
            if (RolePermissions.hasPageAccess(role, "STAFF_ADD")) {
                staff.children.add(new NavChild("Add Staff", "STAFF_ADD"));
            }
            
            if (!staff.children.isEmpty()) {
                groups.add(staff);
            }
        }

        // Dentists
        if (RolePermissions.hasPageAccess(role, "DENTIST_LIST") ||
            RolePermissions.hasPageAccess(role, "DENTIST_ADD")) {
            
            NavGroup dentists = new NavGroup(FontAwesomeSolid.USER_MD, "Dentists", null);
            
            if (RolePermissions.hasPageAccess(role, "DENTIST_LIST")) {
                dentists.children.add(new NavChild("Dentist List", "DENTIST_LIST"));
            }
            
            if (RolePermissions.hasPageAccess(role, "DENTIST_ADD")) {
                dentists.children.add(new NavChild("Add Dentist", "DENTIST_ADD"));
            }
            
            if (!dentists.children.isEmpty()) {
                groups.add(dentists);
            }
        }

        // Treatments
        if (RolePermissions.hasPageAccess(role, "TREATMENT_LIST") ||
            RolePermissions.hasPageAccess(role, "TREATMENT_ADD")) {
            
            NavGroup treatments = new NavGroup(FontAwesomeSolid.PILLS, "Treatments", null);
            
            if (RolePermissions.hasPageAccess(role, "TREATMENT_LIST")) {
                treatments.children.add(new NavChild("Treatment List", "TREATMENT_LIST"));
            }
            
            if (RolePermissions.hasPageAccess(role, "TREATMENT_ADD")) {
                treatments.children.add(new NavChild("Add Treatment", "TREATMENT_ADD"));
            }
            
            if (!treatments.children.isEmpty()) {
                groups.add(treatments);
            }
        }

        // Help - All roles
        if (RolePermissions.hasPageAccess(role, "HELP")) {
            NavGroup help = new NavGroup(FontAwesomeSolid.QUESTION_CIRCLE, "Help", "HELP");
            groups.add(help);
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

        // Create icon label for the group
        JLabel iconLabel = iconLabel(group.icon, ICON_SIZE, PRIMARY_DARK);

        RoundedRow groupRow = new RoundedRow(GROUP_ROW_HEIGHT);
        groupRow.build(iconLabel, group.label, FONT_GROUP, chevron);

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
                User currentUser = LoginSession.getInstance().getCurrentUser();
                String initial = currentUser != null ? 
                    String.valueOf(currentUser.getUsername().charAt(0)).toUpperCase() : "U";
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
        User currentUser = LoginSession.getInstance().getCurrentUser();
        if (currentUser != null) {
            userNameLabel.setText(currentUser.getUsername());
            userRoleLabel.setText(currentUser.getRole().name());
        } else {
            userNameLabel.setText("Guest");
            userRoleLabel.setText("User");
        }
        revalidate();
        repaint();
    }

    // =================================================================
    // Inner class for RowClickHandler
    // =================================================================
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

    // =================================================================
    // Inner class for RoundedRow
    // =================================================================
    private static class RoundedRow extends JPanel {
        private final int rowHeight;
        private boolean hovered = false;
        private boolean active = false;
        private boolean isChildRow = false;
        private JLabel iconLabel;
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

        void build(JLabel icon, String label, Font labelFont, JLabel trailing) {
            this.iconLabel = icon;

            textLabel = new JLabel(label);
            textLabel.setFont(labelFont);
            textLabel.setForeground(PRIMARY_DARK);
            textLabel.setVerticalAlignment(SwingConstants.CENTER);

            JPanel left = new JPanel(new BorderLayout(12, 0));
            left.setOpaque(false);
            left.add(icon, BorderLayout.WEST);
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
            if (iconLabel != null) {
                iconLabel.setForeground(PRIMARY_DARK);
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

    // =================================================================
    // Inner classes for navigation data
    // =================================================================
    private static class NavGroup {
        FontAwesomeSolid icon;
        String label;
        String cardName;
        List<NavChild> children = new ArrayList<>();

        NavGroup(FontAwesomeSolid icon, String label, String cardName) {
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
}