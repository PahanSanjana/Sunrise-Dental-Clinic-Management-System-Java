package view;

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

/**
 * SidebarPanel - premium minimalist navigation for a dental clinic dashboard.
 *
 * IMPORTANT: icons are NOT text glyphs / Unicode symbols. Font glyph coverage
 * varies across OS and installed fonts, which is what caused icons to render
 * as empty "tofu" squares. Instead, every icon here is drawn directly with
 * Java2D vector shapes (lines, ovals, rounded rects) inside IconCanvas -
 * this guarantees identical rendering on every machine, with no font
 * dependency at all.
 *
 * Pure View layer: on selection it only calls MainFrame.showCard(cardName).
 */
public class SidebarPanel extends JPanel {

    // ---- Palette ---------------------------------------------------
    private static final Color PRIMARY_DARK     = Color.decode("#2F3E3C");
    private static final Color MINT_ACCENT      = Color.decode("#BDDBD1");
    private static final Color SOFT_BG          = Color.decode("#FBF9F1");
    private static final Color LIGHT_SURFACE    = Color.decode("#E7E9E3");
    private static final Color HOVER_SURFACE    = Color.decode("#E8F0F1");
    private static final Color SECONDARY_ACCENT = Color.decode("#C7E7EC");
    private static final Color SECONDARY_TEXT   = new Color(122, 138, 135);

    // ---- Typography (with fallback chain) ---------------------------
    private static final String FONT_FAMILY = resolveFontFamily();
    private static final Font FONT_BRAND     = new Font(FONT_FAMILY, Font.BOLD, 18);
    private static final Font FONT_BRAND_SUB = new Font(FONT_FAMILY, Font.PLAIN, 11);
    private static final Font FONT_GROUP     = new Font(FONT_FAMILY, Font.BOLD, 14);
    private static final Font FONT_CHILD     = new Font(FONT_FAMILY, Font.PLAIN, 13);
    private static final Font FONT_NAME      = new Font(FONT_FAMILY, Font.BOLD, 13);
    private static final Font FONT_ROLE      = new Font(FONT_FAMILY, Font.PLAIN, 11);

    // ---- Layout constants --------------------------------------------
    private static final int SIDEBAR_WIDTH    = 260;
    private static final int ROW_ARC          = 10;
    private static final int GROUP_ROW_HEIGHT = 44;
    private static final int CHILD_ROW_HEIGHT = 38;
    private static final int ROW_SIDE_MARGIN  = 14;
    private static final int ICON_SIZE        = 18;

    private final MainFrame mainFrame;
    private RoundedRow activeRow;

    public SidebarPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout());
        setBackground(SOFT_BG);
        setPreferredSize(new Dimension(SIDEBAR_WIDTH, 0));
        setBorder(new MatteBorder(0, 0, 0, 1, LIGHT_SURFACE));

        add(buildBrandHeader(), BorderLayout.NORTH);
        add(buildScrollableNav(), BorderLayout.CENTER);
        add(buildUserFooter(), BorderLayout.SOUTH);
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
    // ICON TYPES - each drawn with pure Java2D, no fonts involved
    // =================================================================
    private enum IconType {
        DASHBOARD, PATIENTS, APPOINTMENTS, BILLING, REPORTS, STAFF, DENTISTS, TREATMENTS, AUDIT
    }

    /** Small fixed-size panel that vector-draws one icon. No text glyphs. */
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

            // The layout manager stretches this canvas to the row's full height
            // (BorderLayout only respects preferred WIDTH for WEST components,
            // not height). Every shape below is drawn on an 18x18 grid, so we
            // translate to the center of whatever space we actually got -
            // this is what keeps the icon vertically/horizontally centered
            // next to its label instead of pinned to the top-left corner.
            int offsetX = (getWidth() - ICON_SIZE) / 2;
            int offsetY = (getHeight() - ICON_SIZE) / 2;
            g2.translate(offsetX, offsetY);

            int s = ICON_SIZE;
            switch (type) {
                case DASHBOARD:
                    // 2x2 grid of small rounded squares
                    g2.drawRoundRect(1, 1, 6, 6, 2, 2);
                    g2.drawRoundRect(11, 1, 6, 6, 2, 2);
                    g2.drawRoundRect(1, 11, 6, 6, 2, 2);
                    g2.drawRoundRect(11, 11, 6, 6, 2, 2);
                    break;

                case PATIENTS:
                    // head + shoulders
                    g2.drawOval(6, 1, 6, 6);
                    g2.drawArc(2, 9, 14, 12, 0, 180);
                    break;

                case APPOINTMENTS:
                    // calendar: rounded rect body + two hangers + one line
                    g2.drawRoundRect(2, 4, 14, 12, 3, 3);
                    g2.drawLine(6, 1, 6, 5);
                    g2.drawLine(12, 1, 12, 5);
                    g2.drawLine(2, 9, 16, 9);
                    break;

                case BILLING:
                    // document with a horizontal line total + currency mark
                    g2.drawRoundRect(3, 1, 12, 16, 2, 2);
                    g2.drawLine(6, 6, 14, 6);
                    g2.drawLine(6, 10, 14, 10);
                    g2.drawLine(6, 14, 11, 14);
                    break;

                case REPORTS:
                    // ascending bar chart
                    g2.drawLine(2, 16, 16, 16); // baseline
                    g2.drawLine(5, 13, 5, 16);
                    g2.drawLine(9, 9, 9, 16);
                    g2.drawLine(13, 4, 13, 16);
                    break;

                case STAFF:
                    // two overlapping people
                    g2.drawOval(2, 2, 6, 6);
                    g2.drawArc(0, 10, 11, 9, 0, 180);
                    g2.drawOval(10, 3, 6, 6);
                    g2.drawArc(8, 11, 11, 9, 0, 180);
                    break;

                case DENTISTS:
                    // simplified tooth: rounded top, two roots
                    g2.drawArc(3, 1, 12, 10, 0, 180);
                    g2.drawLine(3, 6, 3, 9);
                    g2.drawLine(15, 6, 15, 9);
                    g2.drawLine(3, 9, 7, 16);
                    g2.drawLine(15, 9, 11, 16);
                    break;

                case TREATMENTS:
                    // medical cross
                    g2.drawLine(9, 2, 9, 16);
                    g2.drawLine(2, 9, 16, 9);
                    break;

                case AUDIT:
                    // list: three lines with a leading dot each
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

    // =================================================================
    // BRAND HEADER
    // =================================================================
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

    // =================================================================
    // NAVIGATION
    // =================================================================
    private JScrollPane buildScrollableNav() {
        JPanel navListPanel = new JPanel();
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
        return scrollPane;
    }

    private static class NavGroup {
        IconType icon;
        String label;
        String cardName; // non-null only for leaf items (e.g. Dashboard)
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

    private List<NavGroup> buildNavData() {
        List<NavGroup> groups = new ArrayList<>();

        groups.add(new NavGroup(IconType.DASHBOARD, "Dashboard", "DASHBOARD")); // leaf

        NavGroup patients = new NavGroup(IconType.PATIENTS, "Patients", null);
        patients.children.add(new NavChild("Patient List", "PATIENT_LIST"));
        patients.children.add(new NavChild("Add Patient", "PATIENT_ADD"));
        patients.children.add(new NavChild("Patient Details", "PATIENT_DETAILS"));
        groups.add(patients);

        NavGroup appointments = new NavGroup(IconType.APPOINTMENTS, "Appointments", null);
        appointments.children.add(new NavChild("Appointment List", "APPOINTMENT_LIST"));
        appointments.children.add(new NavChild("Book Appointment", "APPOINTMENT_BOOK"));
        appointments.children.add(new NavChild("Appointment Details", "APPOINTMENT_DETAILS"));
        appointments.children.add(new NavChild("Daily Schedule", "APPOINTMENT_SCHEDULE"));
        groups.add(appointments);

        NavGroup billing = new NavGroup(IconType.BILLING, "Billing", null);
        billing.children.add(new NavChild("Bill List", "BILL_LIST"));
        billing.children.add(new NavChild("Generate Bill", "BILL_GENERATE"));
        billing.children.add(new NavChild("Bill Details", "BILL_DETAILS"));
        groups.add(billing);

        NavGroup reports = new NavGroup(IconType.REPORTS, "Reports", null);
        reports.children.add(new NavChild("Report Dashboard", "REPORT_DASHBOARD"));
        reports.children.add(new NavChild("Revenue Report", "REPORT_REVENUE"));
        reports.children.add(new NavChild("Schedule Report", "REPORT_SCHEDULE"));
        reports.children.add(new NavChild("Patient Report", "REPORT_PATIENT"));
        groups.add(reports);

        NavGroup staff = new NavGroup(IconType.STAFF, "Staff", null);
        staff.children.add(new NavChild("Staff List", "STAFF_LIST"));
        staff.children.add(new NavChild("Add Staff", "STAFF_ADD"));
        staff.children.add(new NavChild("Staff Details", "STAFF_DETAILS"));
        groups.add(staff);

        NavGroup dentists = new NavGroup(IconType.DENTISTS, "Dentists", null);
        dentists.children.add(new NavChild("Dentist List", "DENTIST_LIST"));
        dentists.children.add(new NavChild("Add Dentist", "DENTIST_ADD"));
        groups.add(dentists);

        NavGroup treatments = new NavGroup(IconType.TREATMENTS, "Treatments", null);
        treatments.children.add(new NavChild("Treatment List", "TREATMENT_LIST"));
        treatments.children.add(new NavChild("Add Treatment", "TREATMENT_ADD"));
        groups.add(treatments);

        NavGroup audit = new NavGroup(IconType.AUDIT, "Audit Logs", null);
        audit.children.add(new NavChild("Activity Log", "AUDIT_ACTIVITY"));
        audit.children.add(new NavChild("Login History", "AUDIT_LOGIN"));
        groups.add(audit);

        return groups;
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

        JLabel chevron = new JLabel(isLeaf ? "" : "v");
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
                chevron.setText(expanding ? "^" : "v");
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

    /**
     * A single navigation row with a custom-painted rounded background.
     * Owns its IconCanvas (if any) and its label, keeping hover/active
     * state fully in sync across both.
     */
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
            setBorder(new EmptyBorder(0, 44, 0, 12)); // indent under parent icon
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
                g2.fill(new RoundRectangle2D.Float(0, 6, 4, getHeight() - 12, 2, 2)); // left accent indicator
            } else if (hovered) {
                g2.setColor(HOVER_SURFACE);
                g2.fill(new RoundRectangle2D.Float(4, 0, getWidth() - 4, getHeight(), ROW_ARC, ROW_ARC));
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

    // =================================================================
    // USER FOOTER
    // =================================================================
    private JPanel buildUserFooter() {
        JPanel footer = new JPanel(new BorderLayout(12, 0));
        footer.setBackground(SOFT_BG);
        footer.setBorder(new CompoundBorder(
                new MatteBorder(1, 0, 0, 0, LIGHT_SURFACE),
                new EmptyBorder(16, 24, 16, 20)
        ));
        footer.setPreferredSize(new Dimension(0, 68));

        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(SECONDARY_ACCENT);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        avatar.setOpaque(false);
        avatar.setPreferredSize(new Dimension(38, 38));

        JPanel textStack = new JPanel();
        textStack.setLayout(new BoxLayout(textStack, BoxLayout.Y_AXIS));
        textStack.setOpaque(false);

        JLabel name = new JLabel("Amanda Peterson");
        name.setFont(FONT_NAME);
        name.setForeground(PRIMARY_DARK);
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