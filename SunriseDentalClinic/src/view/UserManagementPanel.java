package view;

import controller.UserController;
import model.User;
import model.User.UserRole;
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
import java.util.List;

public class UserManagementPanel extends JPanel {
    
    private static final Color PRIMARY_DARK = new Color(0x2F3E3C);
    private static final Color MINT = new Color(0xBDDBD1);
    private static final Color SOFT_SURFACE = new Color(0xFBF9F1);
    private static final Color LIGHT_SURFACE = new Color(0xE7E9E3);
    private static final Color ERROR_COLOR = new Color(220, 80, 80);
    private static final Color SUCCESS_COLOR = new Color(60, 160, 80);

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

    private JTable userTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton refreshButton;
    private RoundedButton editButton;
    private RoundedButton deactivateButton;
    private RoundedButton activateButton;
    private JLabel statusLabel;
    private JLabel countLabel;
    private JComboBox<String> roleFilterCombo;
    private JComboBox<String> statusFilterCombo;
    
    private UserController controller;

    // ✅ Auto-refresh timer (hidden)
    private Timer refreshTimer;
    private static final int AUTO_REFRESH_DELAY = 30000; // 30 seconds

    public UserManagementPanel() {
        this.controller = new UserController(this);
        initComponents();
        loadUsers();
        startAutoRefresh();
        updateActionButtons();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(SOFT_SURFACE);
        setBorder(new EmptyBorder(20, 30, 20, 30));

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);
    }

    // =====================================================
    // ✅ AUTO-REFRESH (Hidden - No UI Indicator)
    // =====================================================
    
    private void startAutoRefresh() {
        if (refreshTimer == null) {
            refreshTimer = new Timer(AUTO_REFRESH_DELAY, e -> {
                if (isShowing()) {
                    loadUsers();
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
        
        // User management is ADMIN only
        boolean isAdmin = currentUser.isAdmin();
        
        // Edit button - Only ADMIN can edit user roles
        editButton.setVisible(isAdmin);
        
        // Deactivate button - Only ADMIN can deactivate users
        deactivateButton.setVisible(isAdmin);
        
        // Activate button - Only ADMIN can activate users
        activateButton.setVisible(isAdmin);
        
        // Role filter - Only ADMIN can filter by role
        roleFilterCombo.setVisible(isAdmin);
        
        // Status filter - Only ADMIN can filter by status
        statusFilterCombo.setVisible(isAdmin);
        
        // Search field - Only ADMIN can search users
        searchField.setVisible(isAdmin);
        
        // Refresh button - Only ADMIN can refresh
        refreshButton.setVisible(isAdmin);
        
        // If not admin, show a message
        if (!isAdmin) {
            statusLabel.setText("User management is restricted to Administrators only.");
        }
    }

    // =====================================================
    // HEADER PANEL - With Manual Refresh Icon Only
    // =====================================================
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(SOFT_SURFACE);
        header.setBorder(new EmptyBorder(0, 0, 15, 0));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("User Management");
        titleLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_DARK);
        
        JLabel subtitleLabel = new JLabel("View and manage user accounts");
        subtitleLabel.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(107, 123, 121));
        
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setOpaque(false);

        // Role filter
        String[] roles = {"All Roles", "ADMIN", "RECEPTION", "DENTIST", "PATIENT"};
        roleFilterCombo = new JComboBox<>(roles);
        roleFilterCombo.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        roleFilterCombo.setPreferredSize(new Dimension(120, 35));
        roleFilterCombo.addActionListener(e -> loadUsers());

        // Status filter
        String[] statuses = {"All Status", "Active", "Inactive"};
        statusFilterCombo = new JComboBox<>(statuses);
        statusFilterCombo.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        statusFilterCombo.setPreferredSize(new Dimension(120, 35));
        statusFilterCombo.addActionListener(e -> loadUsers());

        searchField = new JTextField(20);
        searchField.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        searchField.setPreferredSize(new Dimension(200, 35));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        searchField.addActionListener(e -> loadUsers());

        // ✅ Manual Refresh Button - ICON ONLY
        refreshButton = createIconButton(FontAwesomeSolid.SYNC_ALT, COLOR_REFRESH);
        refreshButton.setPreferredSize(new Dimension(40, 40));
        refreshButton.setToolTipText("Refresh Now");
        refreshButton.addActionListener(e -> loadUsers());

        searchPanel.add(new JLabel("Role:"));
        searchPanel.add(roleFilterCombo);
        searchPanel.add(new JLabel("Status:"));
        searchPanel.add(statusFilterCombo);
        searchPanel.add(searchField);
        searchPanel.add(refreshButton);

        header.add(titlePanel, BorderLayout.WEST);
        header.add(searchPanel, BorderLayout.EAST);

        return header;
    }

    // =====================================================
    // ✅ CREATE ICON BUTTON (No text, only icon)
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

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(LIGHT_SURFACE, 1));

        String[] columns = {"ID", "Username", "Email", "Role", "Status", "Created"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        userTable = new JTable(tableModel);
        userTable.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        userTable.setRowHeight(35);
        userTable.setSelectionBackground(new Color(235, 245, 240));
        userTable.setSelectionForeground(PRIMARY_DARK);
        userTable.setShowGrid(true);
        userTable.setGridColor(LIGHT_SURFACE);

        JTableHeader header = userTable.getTableHeader();
        header.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        header.setBackground(MINT);
        header.setForeground(PRIMARY_DARK);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_DARK));

        userTable.getColumnModel().getColumn(4).setCellRenderer(new StatusCellRenderer());

        JScrollPane scrollPane = new JScrollPane(userTable);
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

        countLabel = new JLabel("Total: 0 users");
        countLabel.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 12));
        countLabel.setForeground(new Color(107, 123, 121));
        countLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        editButton = createStyledButton("Edit Role", SOFT_SURFACE, PRIMARY_DARK);
        editButton.setBorderColor(LIGHT_SURFACE);
        editButton.setPreferredSize(new Dimension(120, 30));
        editButton.addActionListener(e -> {
            User currentUser = LoginSession.getInstance().getCurrentUser();
            if (currentUser == null || !currentUser.isAdmin()) {
                showError("Only Administrators can edit user roles.");
                return;
            }
            editUserRole();
        });
        editButton.setIcon(icon(FontAwesomeSolid.EDIT, 12, PRIMARY_DARK));
        editButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        editButton.setIconTextGap(6);

        deactivateButton = createStyledButton("Deactivate", SOFT_SURFACE, ERROR_COLOR);
        deactivateButton.setBorderColor(LIGHT_SURFACE);
        deactivateButton.setPreferredSize(new Dimension(120, 30));
        deactivateButton.addActionListener(e -> {
            User currentUser = LoginSession.getInstance().getCurrentUser();
            if (currentUser == null || !currentUser.isAdmin()) {
                showError("Only Administrators can deactivate users.");
                return;
            }
            deactivateUser();
        });
        deactivateButton.setIcon(icon(FontAwesomeSolid.TIMES_CIRCLE, 12, ERROR_COLOR));
        deactivateButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        deactivateButton.setIconTextGap(6);

        activateButton = createStyledButton("Activate", SOFT_SURFACE, SUCCESS_COLOR);
        activateButton.setBorderColor(LIGHT_SURFACE);
        activateButton.setPreferredSize(new Dimension(120, 30));
        activateButton.addActionListener(e -> {
            User currentUser = LoginSession.getInstance().getCurrentUser();
            if (currentUser == null || !currentUser.isAdmin()) {
                showError("Only Administrators can activate users.");
                return;
            }
            activateUser();
        });
        activateButton.setIcon(icon(FontAwesomeSolid.CHECK_CIRCLE, 12, SUCCESS_COLOR));
        activateButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        activateButton.setIconTextGap(6);

        buttonPanel.add(editButton);
        buttonPanel.add(deactivateButton);
        buttonPanel.add(activateButton);

        footer.add(statusLabel, BorderLayout.WEST);
        footer.add(buttonPanel, BorderLayout.CENTER);
        footer.add(countLabel, BorderLayout.EAST);

        return footer;
    }

    private RoundedButton createStyledButton(String text, Color bg, Color fg) {
        RoundedButton button = new RoundedButton(text, bg, fg);
        button.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 12));
        return button;
    }

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
            } else if (bg.equals(SUCCESS_COLOR)) {
                hoverColor = new Color(40, 180, 90);
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
                    label.setBackground(SUCCESS_COLOR);
                    label.setForeground(Color.WHITE);
                } else {
                    label.setBackground(ERROR_COLOR);
                    label.setForeground(Color.WHITE);
                }
            }
            return c;
        }
    }

    private static class MouseAdapter extends java.awt.event.MouseAdapter {
        // Empty implementation
    }

    // ========================
    // Data Loading Methods
    // ========================

    public void loadUsers() {
        // Check if user is admin before loading
        User currentUser = LoginSession.getInstance().getCurrentUser();
        if (currentUser == null || !currentUser.isAdmin()) {
            statusLabel.setText("User management is restricted to Administrators only.");
            countLabel.setText("Total: 0 users");
            tableModel.setRowCount(0);
            return;
        }
        
        String searchText = searchField.getText().trim();
        String role = (String) roleFilterCombo.getSelectedItem();
        String status = (String) statusFilterCombo.getSelectedItem();
        
        SwingWorker<List<User>, Void> worker = new SwingWorker<List<User>, Void>() {
            @Override
            protected List<User> doInBackground() throws Exception {
                List<User> users = controller.getAllUsers();
                
                if (role != null && !role.equals("All Roles") && users != null) {
                    users.removeIf(u -> !u.getRole().name().equals(role));
                }
                
                if (status != null && !status.equals("All Status") && users != null) {
                    if (status.equals("Active")) {
                        users.removeIf(u -> !u.isActive());
                    } else {
                        users.removeIf(u -> u.isActive());
                    }
                }
                
                if (searchText != null && !searchText.isEmpty() && users != null) {
                    users.removeIf(u -> 
                        !u.getUsername().toLowerCase().contains(searchText.toLowerCase()) &&
                        (u.getEmail() == null || !u.getEmail().toLowerCase().contains(searchText.toLowerCase()))
                    );
                }
                
                return users;
            }

            @Override
            protected void done() {
                try {
                    List<User> users = get();
                    displayUsers(users);
                } catch (Exception e) {
                    showError("Error loading users: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    public void displayUsers(List<User> users) {
        tableModel.setRowCount(0);
        
        if (users == null || users.isEmpty()) {
            statusLabel.setText("No users found");
            countLabel.setText("Total: 0 users");
            return;
        }

        for (User user : users) {
            Object[] row = {
                user.getUserId(),
                user.getUsername(),
                user.getEmail() != null ? user.getEmail() : "N/A",
                user.getRole().name(),
                user.isActive() ? "Active" : "Inactive",
                user.getCreatedAt() != null ? user.getCreatedAt().substring(0, 10) : "N/A"
            };
            tableModel.addRow(row);
        }

        statusLabel.setText("Loaded " + users.size() + " users");
        countLabel.setText("Total: " + users.size() + " users");
    }

    private void editUserRole() {
        int row = userTable.getSelectedRow();
        if (row == -1) {
            showError("Please select a user to edit.");
            return;
        }
        
        // Double-check permission
        User currentUser = LoginSession.getInstance().getCurrentUser();
        if (currentUser == null || !currentUser.isAdmin()) {
            showError("Only Administrators can edit user roles.");
            return;
        }
        
        int userId = (int) tableModel.getValueAt(row, 0);
        String username = (String) tableModel.getValueAt(row, 1);
        String currentRole = (String) tableModel.getValueAt(row, 3);
        
        String[] roles = {"ADMIN", "RECEPTION", "DENTIST", "PATIENT"};
        String newRole = (String) JOptionPane.showInputDialog(
            this,
            "Select new role for " + username + ":",
            "Edit User Role",
            JOptionPane.QUESTION_MESSAGE,
            null,
            roles,
            currentRole
        );
        
        if (newRole != null && !newRole.equals(currentRole)) {
            User user = controller.getUserById(userId);
            if (user != null) {
                user.setRole(UserRole.valueOf(newRole));
                boolean success = controller.updateUser(user);
                if (success) {
                    showSuccess("User role updated to " + newRole + "!");
                    loadUsers();
                } else {
                    showError("Failed to update user role.");
                }
            }
        }
    }

    private void deactivateUser() {
        int row = userTable.getSelectedRow();
        if (row == -1) {
            showError("Please select a user to deactivate.");
            return;
        }
        
        // Double-check permission
        User currentUser = LoginSession.getInstance().getCurrentUser();
        if (currentUser == null || !currentUser.isAdmin()) {
            showError("Only Administrators can deactivate users.");
            return;
        }
        
        int userId = (int) tableModel.getValueAt(row, 0);
        String username = (String) tableModel.getValueAt(row, 1);
        
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to deactivate user: " + username + "?",
            "Confirm Deactivate",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = controller.deactivateUser(userId);
            if (success) {
                showSuccess("User deactivated successfully!");
                loadUsers();
            } else {
                showError("Failed to deactivate user.");
            }
        }
    }

    private void activateUser() {
        int row = userTable.getSelectedRow();
        if (row == -1) {
            showError("Please select a user to activate.");
            return;
        }
        
        // Double-check permission
        User currentUser = LoginSession.getInstance().getCurrentUser();
        if (currentUser == null || !currentUser.isAdmin()) {
            showError("Only Administrators can activate users.");
            return;
        }
        
        int userId = (int) tableModel.getValueAt(row, 0);
        String username = (String) tableModel.getValueAt(row, 1);
        
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to activate user: " + username + "?",
            "Confirm Activate",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = controller.activateUser(userId);
            if (success) {
                showSuccess("User activated successfully!");
                loadUsers();
            } else {
                showError("Failed to activate user.");
            }
        }
    }

    public void showError(String message) {
        statusLabel.setText("Error: " + message);
        statusLabel.setForeground(ERROR_COLOR);
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showSuccess(String message) {
        statusLabel.setText("Success: " + message);
        statusLabel.setForeground(SUCCESS_COLOR);
    }

    public void showInfo(String message) {
        statusLabel.setText("Info: " + message);
        statusLabel.setForeground(new Color(107, 123, 121));
    }
}