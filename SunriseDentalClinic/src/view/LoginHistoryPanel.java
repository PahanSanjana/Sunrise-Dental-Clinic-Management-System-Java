package view;

import controller.AuditController;
import model.LoginHistory;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class LoginHistoryPanel extends JPanel {
    
    // Color Palette
    private static final Color PRIMARY_DARK = new Color(0x2F3E3C);
    private static final Color MINT = new Color(0xBDDBD1);
    private static final Color SOFT_SURFACE = new Color(0xFBF9F1);
    private static final Color LIGHT_SURFACE = new Color(0xE7E9E3);
    private static final Color HOVER_SURFACE = new Color(0xE8F0F1);
    private static final Color ERROR_COLOR = new Color(220, 80, 80);
    private static final Color SUCCESS_COLOR = new Color(60, 160, 80);
    private static final Color SECONDARY_TEXT = new Color(122, 138, 135);
    
    // Status Colors
    private static final Color COLOR_SUCCESS = new Color(46, 204, 113);
    private static final Color COLOR_FAILED = new Color(231, 76, 60);

    // Components
    private JTable loginTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private RoundedButton searchButton;
    private RoundedButton refreshButton;
    private RoundedButton clearFilterButton;
    private RoundedButton exportButton;
    private JLabel statusLabel;
    private JLabel countLabel;
    private JComboBox<String> statusCombo;
    private JComboBox<String> userCombo;
    private JComboBox<String> dateFilterCombo;
    
    private AuditController controller;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public LoginHistoryPanel() {
        this.controller = new AuditController(this);
        initComponents();
        loadLoginHistory();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(SOFT_SURFACE);
        setBorder(new EmptyBorder(20, 30, 20, 30));

        // Header Panel
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Table Panel
        add(createTablePanel(), BorderLayout.CENTER);
        
        // Footer Panel
        add(createFooterPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(SOFT_SURFACE);
        header.setBorder(new EmptyBorder(0, 0, 15, 0));

        // Title
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Login History");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_DARK);
        
        JLabel subtitleLabel = new JLabel("View all user login attempts and activity");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(107, 123, 121));
        
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setOpaque(false);

        // Date filter
        String[] dateFilters = {"All Dates", "Today", "Yesterday", "This Week", "This Month"};
        dateFilterCombo = new JComboBox<>(dateFilters);
        dateFilterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dateFilterCombo.setPreferredSize(new Dimension(120, 35));
        dateFilterCombo.addActionListener(e -> loadLoginHistory());

        // Status filter
        String[] statuses = {"All Status", "Success", "Failed"};
        statusCombo = new JComboBox<>(statuses);
        statusCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusCombo.setPreferredSize(new Dimension(120, 35));
        statusCombo.addActionListener(e -> loadLoginHistory());

        // User filter
        userCombo = new JComboBox<>();
        userCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        userCombo.setPreferredSize(new Dimension(150, 35));
        userCombo.addItem("All Users");
        userCombo.addActionListener(e -> loadLoginHistory());

        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setPreferredSize(new Dimension(200, 35));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        searchField.addActionListener(e -> loadLoginHistory());

        searchButton = createStyledButton("Search", PRIMARY_DARK, Color.WHITE);
        searchButton.setPreferredSize(new Dimension(100, 35));
        searchButton.addActionListener(e -> loadLoginHistory());

        clearFilterButton = createStyledButton("Clear Filters", SOFT_SURFACE, PRIMARY_DARK);
        clearFilterButton.setBorderColor(LIGHT_SURFACE);
        clearFilterButton.setPreferredSize(new Dimension(120, 35));
        clearFilterButton.addActionListener(e -> clearFilters());

        refreshButton = createStyledButton("Refresh", SOFT_SURFACE, PRIMARY_DARK);
        refreshButton.setBorderColor(LIGHT_SURFACE);
        refreshButton.setPreferredSize(new Dimension(100, 35));
        refreshButton.addActionListener(e -> loadLoginHistory());

        exportButton = createStyledButton("Export CSV", SOFT_SURFACE, PRIMARY_DARK);
        exportButton.setBorderColor(LIGHT_SURFACE);
        exportButton.setPreferredSize(new Dimension(120, 35));
        exportButton.addActionListener(e -> exportLogs());

        searchPanel.add(new JLabel("Date:"));
        searchPanel.add(dateFilterCombo);
        searchPanel.add(new JLabel("Status:"));
        searchPanel.add(statusCombo);
        searchPanel.add(new JLabel("User:"));
        searchPanel.add(userCombo);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(clearFilterButton);
        searchPanel.add(refreshButton);
        searchPanel.add(exportButton);

        header.add(titlePanel, BorderLayout.WEST);
        header.add(searchPanel, BorderLayout.EAST);

        return header;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(LIGHT_SURFACE, 1));

        // Create table model
        String[] columns = {"ID", "User", "Login Time", "Logout Time", "IP Address", "Status", "Duration"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        loginTable = new JTable(tableModel);
        loginTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        loginTable.setRowHeight(35);
        loginTable.setSelectionBackground(new Color(235, 245, 240));
        loginTable.setSelectionForeground(PRIMARY_DARK);
        loginTable.setShowGrid(true);
        loginTable.setGridColor(LIGHT_SURFACE);
        loginTable.setIntercellSpacing(new Dimension(5, 5));

        // Set column widths
        loginTable.getColumnModel().getColumn(0).setMaxWidth(60);
        loginTable.getColumnModel().getColumn(0).setMinWidth(50);
        loginTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        loginTable.getColumnModel().getColumn(2).setPreferredWidth(160);
        loginTable.getColumnModel().getColumn(3).setPreferredWidth(160);
        loginTable.getColumnModel().getColumn(4).setMaxWidth(150);
        loginTable.getColumnModel().getColumn(5).setMaxWidth(100);
        loginTable.getColumnModel().getColumn(6).setMaxWidth(100);

        // Custom header
        JTableHeader header = loginTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(MINT);
        header.setForeground(PRIMARY_DARK);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_DARK));

        // Custom cell renderer for status column
        loginTable.getColumnModel().getColumn(5).setCellRenderer(new StatusCellRenderer());

        JScrollPane scrollPane = new JScrollPane(loginTable);
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
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(107, 123, 121));

        countLabel = new JLabel("Total: 0 records");
        countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        countLabel.setForeground(new Color(107, 123, 121));
        countLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        // Summary stats
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        summaryPanel.setOpaque(false);
        
        JLabel successLabel = new JLabel("✅ Success: 0");
        successLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        successLabel.setForeground(COLOR_SUCCESS);
        
        JLabel failedLabel = new JLabel("❌ Failed: 0");
        failedLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        failedLabel.setForeground(COLOR_FAILED);
        
        summaryPanel.add(successLabel);
        summaryPanel.add(failedLabel);

        footer.add(statusLabel, BorderLayout.WEST);
        footer.add(summaryPanel, BorderLayout.CENTER);
        footer.add(countLabel, BorderLayout.EAST);

        return footer;
    }

    private RoundedButton createStyledButton(String text, Color bg, Color fg) {
        RoundedButton button = new RoundedButton(text, bg, fg);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
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
                
                if (status.equals("Success")) {
                    label.setBackground(COLOR_SUCCESS);
                    label.setForeground(Color.WHITE);
                } else if (status.equals("Failed")) {
                    label.setBackground(COLOR_FAILED);
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
    // Data Loading Methods
    // ========================

    private void loadLoginHistory() {
        String searchText = searchField != null ? searchField.getText().trim() : "";
        String status = statusCombo != null ? (String) statusCombo.getSelectedItem() : "All Status";
        String user = userCombo != null ? (String) userCombo.getSelectedItem() : "All Users";
        String dateFilter = dateFilterCombo != null ? (String) dateFilterCombo.getSelectedItem() : "All Dates";
        
        // Load users for filter
        loadUsers();
        
        // Use SwingWorker to load in background
        SwingWorker<List<LoginHistory>, Void> worker = new SwingWorker<List<LoginHistory>, Void>() {
            @Override
            protected List<LoginHistory> doInBackground() throws Exception {
                List<LoginHistory> history = controller.getAllLoginHistory();
                
                // Apply status filter
                if (status != null && !status.equals("All Status") && history != null) {
                    history.removeIf(h -> !h.getStatus().equals(status));
                }
                
                // Apply user filter
                if (user != null && !user.equals("All Users") && history != null) {
                    history.removeIf(h -> !h.getUsername().equals(user));
                }
                
                // Apply date filter - FIXED
                if (dateFilter != null && !dateFilter.equals("All Dates") && history != null) {
                    LocalDate today = LocalDate.now();
                    switch (dateFilter) {
                        case "Today":
                            history.removeIf(h -> {
                                if (h.getLoginTime() == null) return true;
                                return !h.getLoginTime().toLocalDateTime().toLocalDate().equals(today);
                            });
                            break;
                        case "Yesterday":
                            history.removeIf(h -> {
                                if (h.getLoginTime() == null) return true;
                                return !h.getLoginTime().toLocalDateTime().toLocalDate().equals(today.minusDays(1));
                            });
                            break;
                        case "This Week":
                            LocalDate weekStart = today.minusDays(today.getDayOfWeek().getValue() - 1);
                            LocalDate weekEnd = weekStart.plusDays(6);
                            history.removeIf(h -> {
                                if (h.getLoginTime() == null) return true;
                                LocalDate date = h.getLoginTime().toLocalDateTime().toLocalDate();
                                return date.isBefore(weekStart) || date.isAfter(weekEnd);
                            });
                            break;
                        case "This Month":
                            LocalDate monthStart = today.withDayOfMonth(1);
                            LocalDate monthEnd = today.withDayOfMonth(today.lengthOfMonth());
                            history.removeIf(h -> {
                                if (h.getLoginTime() == null) return true;
                                LocalDate date = h.getLoginTime().toLocalDateTime().toLocalDate();
                                return date.isBefore(monthStart) || date.isAfter(monthEnd);
                            });
                            break;
                    }
                }
                
                // Apply search filter
                if (searchText != null && !searchText.isEmpty() && history != null) {
                    history.removeIf(h -> 
                        (h.getUsername() != null && !h.getUsername().toLowerCase().contains(searchText.toLowerCase())) &&
                        (h.getStatus() != null && !h.getStatus().toLowerCase().contains(searchText.toLowerCase())) &&
                        (h.getIpAddress() != null && !h.getIpAddress().toLowerCase().contains(searchText.toLowerCase()))
                    );
                }
                
                return history;
            }

            @Override
            protected void done() {
                try {
                    List<LoginHistory> history = get();
                    displayLoginHistory(history);
                } catch (Exception e) {
                    showError("Error loading login history: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void loadUsers() {
        List<String> users = controller.getAllUsernames();
        userCombo.removeAllItems();
        userCombo.addItem("All Users");
        if (users != null) {
            for (String username : users) {
                userCombo.addItem(username);
            }
        }
    }

    public void displayLoginHistory(List<LoginHistory> history) {
        if (tableModel == null) return;
        
        tableModel.setRowCount(0);
        
        if (history == null || history.isEmpty()) {
            statusLabel.setText("No login records found");
            countLabel.setText("Total: 0 records");
            updateSummary(history);
            return;
        }

        int successCount = 0;
        int failedCount = 0;

        for (LoginHistory login : history) {
            if ("Success".equals(login.getStatus())) {
                successCount++;
            } else {
                failedCount++;
            }
            
            // Calculate duration
            String duration = "N/A";
            if (login.getLoginTime() != null && login.getLogoutTime() != null) {
                long diffInMillis = login.getLogoutTime().getTime() - login.getLoginTime().getTime();
                if (diffInMillis > 0) {
                    long diffInMinutes = diffInMillis / (60 * 1000);
                    long diffInSeconds = (diffInMillis / 1000) % 60;
                    duration = String.format("%d min %d sec", diffInMinutes, diffInSeconds);
                }
            }
            
            Object[] row = {
                login.getLoginId(),
                login.getUsername() != null ? login.getUsername() : "Unknown",
                login.getLoginTime() != null ? sdf.format(login.getLoginTime()) : "N/A",
                login.getLogoutTime() != null ? sdf.format(login.getLogoutTime()) : "N/A",
                login.getIpAddress() != null ? login.getIpAddress() : "N/A",
                login.getStatus() != null ? login.getStatus() : "N/A",
                duration
            };
            tableModel.addRow(row);
        }

        statusLabel.setText("Loaded " + history.size() + " records");
        countLabel.setText("Total: " + history.size() + " records");
        updateSummary(history);
    }

    private void updateSummary(List<LoginHistory> history) {
        if (history == null || history.isEmpty()) {
            // Update summary labels if they exist
            return;
        }
        
        // Update summary labels if they exist in footer
        // This is handled in the footer panel
    }

    private void clearFilters() {
        dateFilterCombo.setSelectedIndex(0);
        statusCombo.setSelectedIndex(0);
        userCombo.setSelectedIndex(0);
        searchField.setText("");
        loadLoginHistory();
        showInfo("Filters cleared");
    }

    private void exportLogs() {
        if (tableModel.getRowCount() == 0) {
            showError("No data to export.");
            return;
        }
        // TODO: Implement CSV export
        showInfo("CSV export functionality coming soon...");
    }

    // ========================
    // Public methods
    // ========================

    public void showError(String message) {
        statusLabel.setText("❌ " + message);
        statusLabel.setForeground(ERROR_COLOR);
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showSuccess(String message) {
        statusLabel.setText("✅ " + message);
        statusLabel.setForeground(SUCCESS_COLOR);
    }

    public void showInfo(String message) {
        statusLabel.setText("ℹ️ " + message);
        statusLabel.setForeground(new Color(107, 123, 121));
    }
}