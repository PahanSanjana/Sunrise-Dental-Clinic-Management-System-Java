package view;

import controller.AdminDashboardController;
import model.DashboardStats;
import model.RecentActivity;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminDashboardPanel extends JPanel {

    // Color Palette
    private static final Color PRIMARY_DARK = new Color(0x2F3E3C);
    private static final Color MINT = new Color(0xBDDBD1);
    private static final Color SOFT_SURFACE = new Color(0xFBF9F1);
    private static final Color LIGHT_SURFACE = new Color(0xE7E9E3);
    private static final Color HOVER_SURFACE = new Color(0xE8F0F1);
    private static final Color ERROR_COLOR = new Color(220, 80, 80);
    private static final Color SUCCESS_COLOR = new Color(60, 160, 80);
    private static final Color SECONDARY_TEXT = new Color(122, 138, 135);

    // Card Colors
    private static final Color COLOR_TOTAL = new Color(52, 152, 219);
    private static final Color COLOR_PATIENTS = new Color(46, 204, 113);
    private static final Color COLOR_APPOINTMENTS = new Color(241, 196, 15);
    private static final Color COLOR_REVENUE = new Color(155, 89, 182);
    private static final Color COLOR_STAFF = new Color(231, 76, 60);
    private static final Color COLOR_DENTISTS = new Color(149, 165, 166);

    // Refresh button colors
    private static final Color COLOR_REFRESH = new Color(52, 152, 219);
    private static final Color COLOR_REFRESH_HOVER = new Color(41, 128, 185);

    private static final String UI_FONT_FAMILY = "Segoe UI";

    // =====================================================
    // ICON HELPERS (Ikonli FontIcon — real Icon objects,
    // no dependency on OS emoji fonts, renders identically
    // on Windows/macOS/Linux).
    // =====================================================
    private static FontIcon icon(FontAwesomeSolid glyph, int size, Color color) {
        return FontIcon.of(glyph, size, color);
    }

    private static JLabel iconLabel(FontAwesomeSolid glyph, int size, Color color) {
        return new JLabel(icon(glyph, size, color));
    }

    // Components
    private JLabel statusLabel;
    private JLabel lastUpdatedLabel;

    // Quick Stats Cards
    private JLabel totalUsersLabel;
    private JLabel totalPatientsLabel;
    private JLabel totalAppointmentsLabel;
    private JLabel totalRevenueLabel;
    private JLabel totalStaffLabel;
    private JLabel totalDentistsLabel;

    // Recent Activity
    private JList<RecentActivity> recentActivityList;
    private DefaultListModel<RecentActivity> activityModel;

    // Quick Actions
    private JPanel quickActionsPanel;

    private AdminDashboardController controller;
    private DecimalFormat df = new DecimalFormat("#.00");
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    // Auto-refresh timer (hidden)
    private Timer refreshTimer;
    private static final int AUTO_REFRESH_DELAY = 30000; // 30 seconds

    public AdminDashboardPanel() {
        this.controller = new AdminDashboardController(this);
        initComponents();
        loadDashboardData();
        startAutoRefresh();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(SOFT_SURFACE);
        setBorder(new EmptyBorder(20, 30, 20, 30));

        add(createHeaderPanel(), BorderLayout.NORTH);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(SOFT_SURFACE);

        mainPanel.add(createStatsPanel());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        bottomPanel.setOpaque(false);
        bottomPanel.add(createQuickActionsPanel());
        bottomPanel.add(createRecentActivityPanel());
        mainPanel.add(bottomPanel);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        add(createFooterPanel(), BorderLayout.SOUTH);
    }

    // =====================================================
    // AUTO-REFRESH (Hidden - No UI Indicator)
    // =====================================================

    private void startAutoRefresh() {
        if (refreshTimer == null) {
            refreshTimer = new Timer(AUTO_REFRESH_DELAY, e -> {
                if (isShowing()) {
                    loadDashboardData();
                }
            });
            refreshTimer.start();
        }
    }

    private void stopAutoRefresh() {
        if (refreshTimer != null) {
            refreshTimer.stop();
            refreshTimer = null;
        }
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        stopAutoRefresh();
    }

    // =====================================================
    // HEADER PANEL
    // =====================================================
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(SOFT_SURFACE);
        header.setBorder(new EmptyBorder(0, 0, 15, 0));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Admin Dashboard");
        titleLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_DARK);

        JLabel subtitleLabel = new JLabel("Overview of clinic performance and key metrics");
        subtitleLabel.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(107, 123, 121));

        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);

        header.add(titlePanel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);

        JButton refreshBtn = createIconButton(FontAwesomeSolid.SYNC_ALT, COLOR_REFRESH);
        refreshBtn.setPreferredSize(new Dimension(40, 40));
        refreshBtn.setToolTipText("Refresh Now");
        refreshBtn.addActionListener(e -> loadDashboardData());
        rightPanel.add(refreshBtn);

        header.add(rightPanel, BorderLayout.EAST);

        return header;
    }

    private JButton createIconButton(FontAwesomeSolid glyph, Color bg) {
        JButton button = new JButton(icon(glyph, 18, Color.WHITE));
        button.setBackground(bg);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setBorderPainted(false);

        Color originalBg = bg;
        Color hoverBg = bg.equals(COLOR_REFRESH) ? COLOR_REFRESH_HOVER : new Color(40, 55, 53);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverBg);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(originalBg);
            }
        });

        return button;
    }

    // =====================================================
    // STATS PANEL
    // =====================================================
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            new EmptyBorder(20, 20, 20, 20)
        ));

        JPanel userCard = createStatCard(FontAwesomeSolid.USERS, "Total Users", "0", COLOR_TOTAL);
        panel.add(userCard);
        totalUsersLabel = findValueLabel(userCard);

        JPanel patientCard = createStatCard(FontAwesomeSolid.HOSPITAL, "Total Patients", "0", COLOR_PATIENTS);
        panel.add(patientCard);
        totalPatientsLabel = findValueLabel(patientCard);

        JPanel appointmentCard = createStatCard(FontAwesomeSolid.CLIPBOARD_LIST, "Total Appointments", "0", COLOR_APPOINTMENTS);
        panel.add(appointmentCard);
        totalAppointmentsLabel = findValueLabel(appointmentCard);

        JPanel revenueCard = createStatCard(FontAwesomeSolid.MONEY_BILL_WAVE, "Total Revenue", "RS0.00", COLOR_REVENUE);
        panel.add(revenueCard);
        totalRevenueLabel = findValueLabel(revenueCard);

        JPanel staffCard = createStatCard(FontAwesomeSolid.USER_TIE, "Total Staff", "0", COLOR_STAFF);
        panel.add(staffCard);
        totalStaffLabel = findValueLabel(staffCard);

        JPanel dentistCard = createStatCard(FontAwesomeSolid.TOOTH, "Total Dentists", "0", COLOR_DENTISTS);
        panel.add(dentistCard);
        totalDentistsLabel = findValueLabel(dentistCard);

        return panel;
    }

    private JPanel createStatCard(FontAwesomeSolid glyph, String title, String defaultValue, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            new EmptyBorder(10, 15, 10, 15)
        ));
        card.setPreferredSize(new Dimension(200, 80));

        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(color);
        leftPanel.setPreferredSize(new Dimension(50, 80));
        leftPanel.setLayout(new GridBagLayout());

        JLabel icon = iconLabel(glyph, 24, Color.WHITE);
        leftPanel.add(icon);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(new EmptyBorder(5, 10, 5, 10));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 11));
        titleLabel.setForeground(SECONDARY_TEXT);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel valueLabel = new JLabel(defaultValue);
        valueLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 18));
        valueLabel.setForeground(PRIMARY_DARK);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        rightPanel.add(titleLabel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 2)));
        rightPanel.add(valueLabel);

        card.add(leftPanel, BorderLayout.WEST);
        card.add(rightPanel, BorderLayout.CENTER);

        return card;
    }

    private JLabel findValueLabel(JPanel card) {
        JPanel rightPanel = (JPanel) card.getComponent(1);
        return (JLabel) rightPanel.getComponent(2);
    }

    private JPanel createQuickActionsPanel() {
        quickActionsPanel = new JPanel();
        quickActionsPanel.setLayout(new BoxLayout(quickActionsPanel, BoxLayout.Y_AXIS));
        quickActionsPanel.setBackground(Color.WHITE);
        quickActionsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            new EmptyBorder(15, 20, 15, 20)
        ));

        JLabel titleLabel = new JLabel("Quick Actions");
        titleLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 16));
        titleLabel.setForeground(PRIMARY_DARK);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        quickActionsPanel.add(titleLabel);
        quickActionsPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        Object[][] actions = {
            {FontAwesomeSolid.USER_PLUS, "Add New Patient", "Register a new patient"},
            {FontAwesomeSolid.CALENDAR_ALT, "Book Appointment", "Schedule an appointment"},
            {FontAwesomeSolid.USER_MD, "Add Dentist", "Register a new dentist"},
            {FontAwesomeSolid.PILLS, "Add Treatment", "Create a new treatment"},
            {FontAwesomeSolid.FILE_INVOICE_DOLLAR, "Generate Bill", "Create a new bill"},
            {FontAwesomeSolid.CHART_BAR, "View Reports", "Generate reports"}
        };

        for (Object[] action : actions) {
            JPanel actionPanel = createActionButton((FontAwesomeSolid) action[0], (String) action[1], (String) action[2]);
            quickActionsPanel.add(actionPanel);
            quickActionsPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        }

        quickActionsPanel.add(Box.createVerticalGlue());

        return quickActionsPanel;
    }

    private JPanel createActionButton(FontAwesomeSolid glyph, String title, String description) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            new EmptyBorder(10, 15, 10, 15)
        ));
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                navigateToAction(title);
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setBackground(HOVER_SURFACE);
                panel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(MINT, 1),
                    new EmptyBorder(10, 15, 10, 15)
                ));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                panel.setBackground(Color.WHITE);
                panel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
                    new EmptyBorder(10, 15, 10, 15)
                ));
            }
        });

        JLabel icon = iconLabel(glyph, 22, PRIMARY_DARK);
        panel.add(icon, BorderLayout.WEST);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.setBorder(new EmptyBorder(0, 10, 0, 0));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 14));
        titleLabel.setForeground(PRIMARY_DARK);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 11));
        descLabel.setForeground(SECONDARY_TEXT);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        textPanel.add(titleLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 2)));
        textPanel.add(descLabel);

        panel.add(textPanel, BorderLayout.CENTER);

        JLabel arrowLabel = iconLabel(FontAwesomeSolid.ANGLE_RIGHT, 18, SECONDARY_TEXT);
        panel.add(arrowLabel, BorderLayout.EAST);

        return panel;
    }

    // =====================================================
    // RECENT ACTIVITY PANEL
    // =====================================================
    private JPanel createRecentActivityPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            new EmptyBorder(15, 20, 15, 20)
        ));

        JLabel titleLabel = new JLabel("Recent Activity");
        titleLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 16));
        titleLabel.setForeground(PRIMARY_DARK);
        panel.add(titleLabel, BorderLayout.NORTH);

        activityModel = new DefaultListModel<>();
        recentActivityList = new JList<>(activityModel);
        recentActivityList.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        recentActivityList.setBackground(Color.WHITE);
        recentActivityList.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        recentActivityList.setCellRenderer(new ActivityListRenderer());

        JScrollPane scrollPane = new JScrollPane(recentActivityList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setPreferredSize(new Dimension(300, 200));

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(SOFT_SURFACE);
        footer.setBorder(new EmptyBorder(10, 0, 0, 0));

        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 12));
        statusLabel.setForeground(new Color(107, 123, 121));

        lastUpdatedLabel = new JLabel("Last updated: --");
        lastUpdatedLabel.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 12));
        lastUpdatedLabel.setForeground(new Color(107, 123, 121));
        lastUpdatedLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        footer.add(statusLabel, BorderLayout.WEST);
        footer.add(lastUpdatedLabel, BorderLayout.EAST);

        return footer;
    }

    // =====================================================
    // ActivityListRenderer
    // Renders a real Icon (via setIcon) alongside plain text
    // (via setText) — no font-mixing tricks needed, because
    // JLabel natively supports an Icon + a text string side
    // by side.
    // =====================================================
    private static final Map<String, FontAwesomeSolid> ACTIVITY_ICON_MAP = buildActivityIconMap();

    private static Map<String, FontAwesomeSolid> buildActivityIconMap() {
        // Maps legacy icon keys (e.g. stored in the DB/model as short
        // codes such as "patient", "appointment", "bill") to a glyph.
        // Adjust the keys on the left to match whatever RecentActivity
        // .getIcon() actually returns in your model.
        Map<String, FontAwesomeSolid> map = new HashMap<>();
        map.put("patient", FontAwesomeSolid.USER_PLUS);
        map.put("appointment", FontAwesomeSolid.CALENDAR_ALT);
        map.put("dentist", FontAwesomeSolid.USER_MD);
        map.put("treatment", FontAwesomeSolid.PILLS);
        map.put("bill", FontAwesomeSolid.FILE_INVOICE_DOLLAR);
        map.put("report", FontAwesomeSolid.CHART_BAR);
        map.put("user", FontAwesomeSolid.USER);
        return map;
    }

    private class ActivityListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {

            JLabel label = (JLabel) super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);

            if (value instanceof RecentActivity) {
                RecentActivity activity = (RecentActivity) value;
                FontAwesomeSolid glyph = ACTIVITY_ICON_MAP.getOrDefault(
                    activity.getIcon() == null ? "" : activity.getIcon().toLowerCase(),
                    FontAwesomeSolid.THUMBTACK
                );
                label.setIcon(icon(glyph, 16, PRIMARY_DARK));
                label.setIconTextGap(8);

                String time = activity.getTimestamp() != null
                        ? sdf.format(activity.getTimestamp()) : "";
                label.setText(activity.getMessage() + (time.isEmpty() ? "" : " - " + time));
            } else {
                // Fallback for plain-string placeholder entries (e.g. "No recent activity")
                label.setIcon(icon(FontAwesomeSolid.THUMBTACK, 16, PRIMARY_DARK));
                label.setIconTextGap(8);
                label.setText(String.valueOf(value));
            }

            label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, LIGHT_SURFACE),
                BorderFactory.createEmptyBorder(8, 5, 8, 5)
            ));

            if (isSelected) {
                label.setBackground(new Color(235, 245, 240));
                label.setForeground(PRIMARY_DARK);
            }

            label.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));

            return label;
        }
    }

    // ========================
    // Data Loading Methods
    // ========================

    public void loadDashboardData() {
        statusLabel.setText("Loading dashboard data...");
        setCursor(new Cursor(Cursor.WAIT_CURSOR));

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            private DashboardStats stats;
            private List<RecentActivity> activities;

            @Override
            protected Void doInBackground() throws Exception {
                stats = controller.getDashboardStats();
                activities = controller.getRecentActivities();
                return null;
            }

            @Override
            protected void done() {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                try {
                    displayStats(stats);
                    displayActivities(activities);
                    statusLabel.setText("Dashboard updated successfully!");
                    statusLabel.setForeground(SUCCESS_COLOR);

                    lastUpdatedLabel.setText("Last updated: " +
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                } catch (Exception e) {
                    showError("Error loading dashboard data: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void displayStats(DashboardStats stats) {
        if (stats == null) {
            totalUsersLabel.setText("0");
            totalPatientsLabel.setText("0");
            totalAppointmentsLabel.setText("0");
            totalRevenueLabel.setText("RS0.00");
            totalStaffLabel.setText("0");
            totalDentistsLabel.setText("0");
            return;
        }

        totalUsersLabel.setText(String.valueOf(stats.getTotalUsers()));
        totalPatientsLabel.setText(String.valueOf(stats.getTotalPatients()));
        totalAppointmentsLabel.setText(String.valueOf(stats.getTotalAppointments()));
        totalRevenueLabel.setText("RS" + df.format(stats.getTotalRevenue()));
        totalStaffLabel.setText(String.valueOf(stats.getTotalStaff()));
        totalDentistsLabel.setText(String.valueOf(stats.getTotalDentists()));
    }

    private void displayActivities(List<RecentActivity> activities) {
        activityModel.clear();

        if (activities == null || activities.isEmpty()) {
            RecentActivity placeholder = new RecentActivity();
            placeholder.setMessage("No recent activity");
            activityModel.addElement(placeholder);
            return;
        }

        for (RecentActivity activity : activities) {
            activityModel.addElement(activity);
        }
    }

    private void navigateToAction(String action) {
        Container parent = getParent();
        while (parent != null && !(parent instanceof MainFrame)) {
            parent = parent.getParent();
        }
        if (parent instanceof MainFrame) {
            MainFrame mainFrame = (MainFrame) parent;
            switch (action) {
                case "Add New Patient":
                    mainFrame.showCard("PATIENT_ADD");
                    break;
                case "Book Appointment":
                    mainFrame.showCard("APPOINTMENT_BOOK");
                    break;
                case "Add Dentist":
                    mainFrame.showCard("DENTIST_ADD");
                    break;
                case "Add Treatment":
                    mainFrame.showCard("TREATMENT_ADD");
                    break;
                case "Generate Bill":
                    mainFrame.showCard("BILL_GENERATE");
                    break;
                case "View Reports":
                    mainFrame.showCard("REPORT_DASHBOARD");
                    break;
                default:
                    break;
            }
        }
    }

    // ========================
    // Public methods
    // ========================

    public void showError(String message) {
        statusLabel.setIcon(icon(FontAwesomeSolid.TIMES_CIRCLE, 14, ERROR_COLOR));
        statusLabel.setText(message);
        statusLabel.setForeground(ERROR_COLOR);
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showSuccess(String message) {
        statusLabel.setIcon(icon(FontAwesomeSolid.CHECK_CIRCLE, 14, SUCCESS_COLOR));
        statusLabel.setText(message);
        statusLabel.setForeground(SUCCESS_COLOR);
    }

    public void showInfo(String message) {
        statusLabel.setIcon(icon(FontAwesomeSolid.INFO_CIRCLE, 14, new Color(107, 123, 121)));
        statusLabel.setText(message);
        statusLabel.setForeground(new Color(107, 123, 121));
    }

    private static class MouseAdapter extends java.awt.event.MouseAdapter {
        // Empty implementation
    }
}