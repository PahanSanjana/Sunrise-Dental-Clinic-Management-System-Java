package view;

import controller.StaffController;
import model.Staff;
import model.User;
import model.LoginSession;
import model.RolePermissions;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class StaffListPanel extends JPanel {
    
    // Color Palette
    private static final Color PRIMARY_DARK = new Color(0x2F3E3C);
    private static final Color MINT = new Color(0xBDDBD1);
    private static final Color SOFT_SURFACE = new Color(0xFBF9F1);
    private static final Color LIGHT_SURFACE = new Color(0xE7E9E3);
    private static final Color HOVER_SURFACE = new Color(0xE8F0F1);
    private static final Color ERROR_COLOR = new Color(220, 80, 80);
    private static final Color SUCCESS_COLOR = new Color(60, 160, 80);
    private static final Color ACTIVE_COLOR = new Color(60, 160, 80);
    private static final Color INACTIVE_COLOR = new Color(200, 80, 80);
    
    // Refresh button colors
    private static final Color COLOR_REFRESH = new Color(52, 152, 219);
    private static final Color COLOR_REFRESH_HOVER = new Color(41, 128, 185);

    private static final String UI_FONT_FAMILY = "Segoe UI";

    // =====================================================
    // ICON HELPERS (Ikonli FontIcon)
    // =====================================================
    private static FontIcon icon(FontAwesomeSolid glyph, int size, Color color) {
        return FontIcon.of(glyph, size, color);
    }

    private static JLabel iconLabel(FontAwesomeSolid glyph, int size, Color color) {
        return new JLabel(icon(glyph, size, color));
    }

    // Components
    private JTable staffTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private RoundedButton searchButton;
    private RoundedButton addButton;
    private RoundedButton viewButton;
    private RoundedButton editButton;
    private RoundedButton deleteButton;
    private RoundedButton toggleStatusButton;
    private JLabel statusLabel;
    private JLabel countLabel;
    private JLabel lastUpdatedLabel;
    private JComboBox<String> filterCombo;
    private JComboBox<String> departmentCombo;
    
    private StaffController controller;

    // Auto-refresh timer (hidden)
    private Timer refreshTimer;
    private static final int AUTO_REFRESH_DELAY = 30000; // 30 seconds

    public StaffListPanel() {
        this.controller = new StaffController(this);
        initComponents();
        loadStaff();
        startAutoRefresh();
        updateActionButtons();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(SOFT_SURFACE);
        setBorder(new EmptyBorder(20, 30, 20, 30));

        // Title Panel - At the top
        add(createTitlePanel(), BorderLayout.NORTH);
        
        // Main Content Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));

        // Search Panel
        mainPanel.add(createSearchPanel(), BorderLayout.NORTH);
        
        // Table Panel
        mainPanel.add(createTablePanel(), BorderLayout.CENTER);
        
        // Footer Panel
        mainPanel.add(createFooterPanel(), BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
    }

    // =====================================================
    // AUTO-REFRESH (Hidden - No UI Indicator)
    // =====================================================

    private void startAutoRefresh() {
        if (refreshTimer == null) {
            refreshTimer = new Timer(AUTO_REFRESH_DELAY, e -> {
                if (isShowing()) {
                    loadStaff();
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
    // ROLE-BASED ACTION BUTTON VISIBILITY
    // =====================================================

    /**
     * Update action button visibility based on user role
     */
    private void updateActionButtons() {
        User currentUser = LoginSession.getInstance().getCurrentUser();
        if (currentUser == null) {
            return;
        }
        
        // Staff management is ADMIN only
        boolean isAdmin = currentUser.isAdmin();
        
        // View button - Only ADMIN can view staff details
        viewButton.setVisible(isAdmin);
        
        // Add button - Only ADMIN can add staff
        addButton.setVisible(isAdmin);
        
        // Edit button - Only ADMIN can edit staff
        editButton.setVisible(isAdmin);
        
        // Toggle Status button - Only ADMIN can toggle status
        toggleStatusButton.setVisible(isAdmin);
        
        // Delete button - Only ADMIN can delete staff
        deleteButton.setVisible(isAdmin);
        
        // Search button - Only ADMIN can search staff
        searchButton.setVisible(isAdmin);
        searchField.setVisible(isAdmin);
        departmentCombo.setVisible(isAdmin);
        filterCombo.setVisible(isAdmin);
        
        // For non-admin users, show a message or hide the entire panel
        if (!isAdmin) {
            // The table will show empty or a message
            statusLabel.setText("Staff management is restricted to Administrators only.");
        }
    }

    // =====================================================
    // CREATE ICON BUTTON (No text, only icon)
    // =====================================================
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

    /**
     * ✅ Title Panel - Separate from other content
     */
    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(SOFT_SURFACE);
        titlePanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JLabel titleLabel = new JLabel("Staff Management");
        titleLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_DARK);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Manage all staff members in the system");
        subtitleLabel.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(107, 123, 121));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 2)));
        titlePanel.add(subtitleLabel);
        
        return titlePanel;
    }

    /**
     * ✅ Search Panel - Search and filter controls
     */
    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setOpaque(false);

        // Department filter
        departmentCombo = new JComboBox<>(new String[]{"All Departments", "Front Desk", "Clinical", "Administration", "Other"});
        departmentCombo.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        departmentCombo.setPreferredSize(new Dimension(150, 35));
        departmentCombo.addActionListener(e -> loadStaff());

        // Status filter
        filterCombo = new JComboBox<>(new String[]{"All", "Active", "Inactive"});
        filterCombo.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        filterCombo.setPreferredSize(new Dimension(100, 35));
        filterCombo.addActionListener(e -> loadStaff());

        searchField = new JTextField(20);
        searchField.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        searchField.setPreferredSize(new Dimension(250, 35));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        searchField.addActionListener(e -> loadStaff());

        searchButton = createStyledButton("Search", PRIMARY_DARK, Color.WHITE);
        searchButton.setPreferredSize(new Dimension(100, 35));
        searchButton.addActionListener(e -> loadStaff());
        searchButton.setIcon(icon(FontAwesomeSolid.SEARCH, 14, Color.WHITE));
        searchButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        searchButton.setIconTextGap(8);

        addButton = createStyledButton("Add Staff", PRIMARY_DARK, Color.WHITE);
        addButton.setPreferredSize(new Dimension(120, 35));
        addButton.addActionListener(e -> {
            // Check permission before navigating
            User currentUser = LoginSession.getInstance().getCurrentUser();
            if (currentUser == null || !currentUser.isAdmin()) {
                showError("Only Administrators can add staff members.");
                return;
            }
            Container parent = getParent();
            while (parent != null && !(parent instanceof MainFrame)) {
                parent = parent.getParent();
            }
            if (parent instanceof MainFrame) {
                ((MainFrame) parent).showCard("STAFF_ADD");
            }
        });
        addButton.setIcon(icon(FontAwesomeSolid.USER_PLUS, 14, Color.WHITE));
        addButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        addButton.setIconTextGap(8);

        searchPanel.add(new JLabel("Department:"));
        searchPanel.add(departmentCombo);
        searchPanel.add(new JLabel("Status:"));
        searchPanel.add(filterCombo);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(addButton);
        
        // Manual Refresh Button - ICON ONLY
        JButton refreshBtn = createIconButton(FontAwesomeSolid.SYNC_ALT, COLOR_REFRESH);
        refreshBtn.setPreferredSize(new Dimension(40, 40));
        refreshBtn.setToolTipText("Refresh Now");
        refreshBtn.addActionListener(e -> loadStaff());
        searchPanel.add(refreshBtn);

        return searchPanel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        String[] columns = {"ID", "Name", "Position", "Department", "Phone", "Email", "Hire Date", "Salary", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        staffTable = new JTable(tableModel);
        staffTable.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        staffTable.setRowHeight(40);
        staffTable.setSelectionBackground(new Color(235, 245, 240));
        staffTable.setSelectionForeground(PRIMARY_DARK);
        staffTable.setShowGrid(true);
        staffTable.setGridColor(LIGHT_SURFACE);
        staffTable.setIntercellSpacing(new Dimension(5, 5));

        // Set column widths
        staffTable.getColumnModel().getColumn(0).setMaxWidth(60);
        staffTable.getColumnModel().getColumn(0).setMinWidth(50);
        staffTable.getColumnModel().getColumn(3).setMaxWidth(120);
        staffTable.getColumnModel().getColumn(4).setMaxWidth(150);
        staffTable.getColumnModel().getColumn(5).setMaxWidth(150);
        staffTable.getColumnModel().getColumn(6).setMaxWidth(100);
        staffTable.getColumnModel().getColumn(7).setMaxWidth(100);
        staffTable.getColumnModel().getColumn(8).setMaxWidth(100);

        // Custom header
        JTableHeader header = staffTable.getTableHeader();
        header.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        header.setBackground(MINT);
        header.setForeground(PRIMARY_DARK);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_DARK));

        // Custom cell renderer for status column
        staffTable.getColumnModel().getColumn(8).setCellRenderer(new StatusCellRenderer());

        // Add mouse listener for double click to view
        staffTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = staffTable.getSelectedRow();
                    if (row != -1) {
                        viewStaff(row);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(staffTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

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

        countLabel = new JLabel("Total: 0 staff members");
        countLabel.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 12));
        countLabel.setForeground(new Color(107, 123, 121));

        lastUpdatedLabel = new JLabel("Last updated: --");
        lastUpdatedLabel.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 12));
        lastUpdatedLabel.setForeground(new Color(107, 123, 121));

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightPanel.setOpaque(false);
        
        viewButton = createStyledButton("View", SOFT_SURFACE, PRIMARY_DARK);
        viewButton.setBorderColor(LIGHT_SURFACE);
        viewButton.setPreferredSize(new Dimension(80, 30));
        viewButton.addActionListener(e -> {
            User currentUser = LoginSession.getInstance().getCurrentUser();
            if (currentUser == null || !currentUser.isAdmin()) {
                showError("Only Administrators can view staff details.");
                return;
            }
            int row = staffTable.getSelectedRow();
            if (row != -1) {
                viewStaff(row);
            } else {
                showError("Please select a staff member to view.");
            }
        });
        viewButton.setIcon(icon(FontAwesomeSolid.EYE, 12, PRIMARY_DARK));
        viewButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        viewButton.setIconTextGap(6);

        editButton = createStyledButton("Edit", SOFT_SURFACE, PRIMARY_DARK);
        editButton.setBorderColor(LIGHT_SURFACE);
        editButton.setPreferredSize(new Dimension(80, 30));
        editButton.addActionListener(e -> {
            User currentUser = LoginSession.getInstance().getCurrentUser();
            if (currentUser == null || !currentUser.isAdmin()) {
                showError("Only Administrators can edit staff members.");
                return;
            }
            int row = staffTable.getSelectedRow();
            if (row != -1) {
                editStaff(row);
            } else {
                showError("Please select a staff member to edit.");
            }
        });
        editButton.setIcon(icon(FontAwesomeSolid.EDIT, 12, PRIMARY_DARK));
        editButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        editButton.setIconTextGap(6);

        toggleStatusButton = createStyledButton("Toggle Status", SOFT_SURFACE, PRIMARY_DARK);
        toggleStatusButton.setBorderColor(LIGHT_SURFACE);
        toggleStatusButton.setPreferredSize(new Dimension(130, 30));
        toggleStatusButton.addActionListener(e -> {
            User currentUser = LoginSession.getInstance().getCurrentUser();
            if (currentUser == null || !currentUser.isAdmin()) {
                showError("Only Administrators can toggle staff status.");
                return;
            }
            int row = staffTable.getSelectedRow();
            if (row != -1) {
                toggleStatus(row);
            } else {
                showError("Please select a staff member to toggle status.");
            }
        });
        toggleStatusButton.setIcon(icon(FontAwesomeSolid.SYNC, 12, PRIMARY_DARK));
        toggleStatusButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        toggleStatusButton.setIconTextGap(6);

        deleteButton = createStyledButton("Delete", SOFT_SURFACE, ERROR_COLOR);
        deleteButton.setBorderColor(LIGHT_SURFACE);
        deleteButton.setPreferredSize(new Dimension(100, 30));
        deleteButton.addActionListener(e -> {
            User currentUser = LoginSession.getInstance().getCurrentUser();
            if (currentUser == null || !currentUser.isAdmin()) {
                showError("Only Administrators can delete staff members.");
                return;
            }
            int row = staffTable.getSelectedRow();
            if (row != -1) {
                deleteStaff(row);
            } else {
                showError("Please select a staff member to delete.");
            }
        });
        deleteButton.setIcon(icon(FontAwesomeSolid.TRASH_ALT, 12, ERROR_COLOR));
        deleteButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        deleteButton.setIconTextGap(6);

        rightPanel.add(viewButton);
        rightPanel.add(editButton);
        rightPanel.add(toggleStatusButton);
        rightPanel.add(deleteButton);

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setOpaque(false);
        leftPanel.add(statusLabel);
        leftPanel.add(lastUpdatedLabel);

        footer.add(leftPanel, BorderLayout.WEST);
        footer.add(countLabel, BorderLayout.CENTER);
        footer.add(rightPanel, BorderLayout.EAST);

        return footer;
    }

    private RoundedButton createStyledButton(String text, Color bg, Color fg) {
        RoundedButton button = new RoundedButton(text, bg, fg);
        button.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 12));
        return button;
    }

    // Inner class for RoundedButton
    private static class RoundedButton extends JButton {
        private Color bg;
        private Color borderColor;
        private Color hoverColor;
        private Color originalBg;

        RoundedButton(String text, Color bg, Color fg) {
            super(text);
            this.bg = bg;
            this.originalBg = bg;
            this.borderColor = bg;

            setForeground(fg);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            if (bg.equals(PRIMARY_DARK)) {
                hoverColor = new Color(40, 55, 53);
            } else if (bg.equals(ERROR_COLOR)) {
                hoverColor = new Color(180, 60, 60);
            } else {
                hoverColor = new Color(220, 220, 210);
            }

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    RoundedButton.this.bg = hoverColor;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    RoundedButton.this.bg = originalBg;
                    repaint();
                }
            });
        }

        public void setBorderColor(Color c) {
            this.borderColor = c;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

            if (borderColor != bg && borderColor != null) {
                g2.setColor(borderColor);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            }

            super.paintComponent(g);
            g2.dispose();
        }
    }

    // Status Cell Renderer
    private class StatusCellRenderer extends javax.swing.table.DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (value != null) {
                String status = value.toString();
                JLabel label = (JLabel) c;
                label.setOpaque(true);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
                
                if (status.equals("Active")) {
                    label.setBackground(ACTIVE_COLOR);
                    label.setForeground(Color.WHITE);
                } else if (status.equals("Inactive")) {
                    label.setBackground(INACTIVE_COLOR);
                    label.setForeground(Color.WHITE);
                } else {
                    label.setBackground(LIGHT_SURFACE);
                    label.setForeground(PRIMARY_DARK);
                }
            }
            return c;
        }
    }

    private static class MouseAdapter extends java.awt.event.MouseAdapter {
        // Empty implementation
    }

    // ========================
    // Public methods
    // ========================

    public void loadStaff() {
        User currentUser = LoginSession.getInstance().getCurrentUser();
        if (currentUser == null || !currentUser.isAdmin()) {
            statusLabel.setText("Staff management is restricted to Administrators only.");
            countLabel.setText("Total: 0 staff members");
            tableModel.setRowCount(0);
            return;
        }
        
        String searchText = searchField != null ? searchField.getText().trim() : "";
        String filter = filterCombo != null ? (String) filterCombo.getSelectedItem() : "All";
        String department = departmentCombo != null ? (String) departmentCombo.getSelectedItem() : "All Departments";
        
        SwingWorker<List<Staff>, Void> worker = new SwingWorker<List<Staff>, Void>() {
            @Override
            protected List<Staff> doInBackground() throws Exception {
                List<Staff> staffList;
                
                if (searchText != null && !searchText.isEmpty()) {
                    staffList = controller.searchStaff(searchText);
                } else {
                    staffList = controller.getAllStaff();
                }
                
                if (department != null && !department.equals("All Departments") && staffList != null) {
                    staffList.removeIf(s -> s.getDepartment() == null || !s.getDepartment().equals(department));
                }
                
                if (filter != null && staffList != null) {
                    if ("Active".equals(filter)) {
                        staffList.removeIf(s -> !s.isActive());
                    } else if ("Inactive".equals(filter)) {
                        staffList.removeIf(s -> s.isActive());
                    }
                }
                
                return staffList;
            }

            @Override
            protected void done() {
                try {
                    List<Staff> staffList = get();
                    displayStaff(staffList);
                    
                    lastUpdatedLabel.setText("Last updated: " + 
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                } catch (Exception e) {
                    showError("Error loading staff: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    public void displayStaff(List<Staff> staffList) {
        if (tableModel == null) return;
        
        tableModel.setRowCount(0);
        
        if (staffList == null || staffList.isEmpty()) {
            statusLabel.setText("No staff members found");
            countLabel.setText("Total: 0 staff members");
            return;
        }

        DecimalFormat df = new DecimalFormat("#.00");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        for (Staff staff : staffList) {
            String status = staff.isActive() ? "Active" : "Inactive";
            Object[] row = {
                staff.getStaffId(),
                staff.getFullName(),
                staff.getPosition() != null ? staff.getPosition() : "N/A",
                staff.getDepartment() != null ? staff.getDepartment() : "N/A",
                staff.getPhone() != null ? staff.getPhone() : "N/A",
                staff.getEmail() != null ? staff.getEmail() : "N/A",
                staff.getHireDate() != null ? sdf.format(staff.getHireDate()) : "N/A",
                "Rs" + df.format(staff.getSalary()),
                status
            };
            tableModel.addRow(row);
        }

        statusLabel.setText("Loaded " + staffList.size() + " staff members");
        countLabel.setText("Total: " + staffList.size() + " staff members");
    }

    public void viewStaff(int row) {
        int staffId = (int) tableModel.getValueAt(row, 0);
        
        // Check permission before proceeding
        User currentUser = LoginSession.getInstance().getCurrentUser();
        if (currentUser == null || !currentUser.isAdmin()) {
            showError("Only Administrators can view staff details.");
            return;
        }
        
        Container parent = getParent();
        while (parent != null && !(parent instanceof MainFrame)) {
            parent = parent.getParent();
        }
        if (parent instanceof MainFrame) {
            MainFrame mainFrame = (MainFrame) parent;
            
            Staff staff = controller.getStaffById(staffId);
            if (staff != null) {
                StaffDetailsPanel detailsPanel = new StaffDetailsPanel(staff);
                detailsPanel.setName("STAFF_DETAILS");
                mainFrame.addScreen("STAFF_DETAILS", detailsPanel);
                mainFrame.showCard("STAFF_DETAILS");
            } else {
                showError("Staff member not found.");
            }
        }
    }

    public void editStaff(int row) {
        int staffId = (int) tableModel.getValueAt(row, 0);
        
        // Check permission before proceeding
        User currentUser = LoginSession.getInstance().getCurrentUser();
        if (currentUser == null || !currentUser.isAdmin()) {
            showError("Only Administrators can edit staff members.");
            return;
        }
        
        Container parent = getParent();
        while (parent != null && !(parent instanceof MainFrame)) {
            parent = parent.getParent();
        }
        if (parent instanceof MainFrame) {
            MainFrame mainFrame = (MainFrame) parent;
            
            Staff staff = controller.getStaffById(staffId);
            if (staff != null) {
                StaffDetailsPanel detailsPanel = new StaffDetailsPanel(staff);
                detailsPanel.setName("STAFF_DETAILS");
                mainFrame.addScreen("STAFF_DETAILS", detailsPanel);
                mainFrame.showCard("STAFF_DETAILS");
                detailsPanel.toggleEditMode();
            } else {
                showError("Staff member not found.");
            }
        }
    }

    public void toggleStatus(int row) {
        int staffId = (int) tableModel.getValueAt(row, 0);
        String staffName = (String) tableModel.getValueAt(row, 1);
        String currentStatus = (String) tableModel.getValueAt(row, 8);
        boolean isActive = "Active".equals(currentStatus);
        String newStatus = isActive ? "Inactive" : "Active";
        
        // Check permission before proceeding
        User currentUser = LoginSession.getInstance().getCurrentUser();
        if (currentUser == null || !currentUser.isAdmin()) {
            showError("Only Administrators can toggle staff status.");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to change " + staffName + "'s status to " + newStatus + "?",
            "Toggle Status",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success;
            if (isActive) {
                success = controller.deactivateStaff(staffId);
            } else {
                success = controller.activateStaff(staffId);
            }
            
            if (success) {
                showSuccess("Staff status updated to " + newStatus + "!");
                loadStaff();
            } else {
                showError("Failed to update staff status.");
            }
        }
    }

    public void deleteStaff(int row) {
        int staffId = (int) tableModel.getValueAt(row, 0);
        String staffName = (String) tableModel.getValueAt(row, 1);
        
        // Check permission before proceeding
        User currentUser = LoginSession.getInstance().getCurrentUser();
        if (currentUser == null || !currentUser.isAdmin()) {
            showError("Only Administrators can delete staff members.");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete staff member: " + staffName + "?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = controller.deleteStaff(staffId);
            if (success) {
                showSuccess("Staff member deleted successfully!");
                loadStaff();
            } else {
                showError("Failed to delete staff member.");
            }
        }
    }

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
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showInfo(String message) {
        statusLabel.setIcon(icon(FontAwesomeSolid.INFO_CIRCLE, 14, new Color(107, 123, 121)));
        statusLabel.setText(message);
        statusLabel.setForeground(new Color(107, 123, 121));
    }
}