package view;

import controller.AuditController;
import model.AuditLog;
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

public class ActivityLogPanel extends JPanel {
    
    // Color Palette
    private static final Color PRIMARY_DARK = new Color(0x2F3E3C);
    private static final Color MINT = new Color(0xBDDBD1);
    private static final Color SOFT_SURFACE = new Color(0xFBF9F1);
    private static final Color LIGHT_SURFACE = new Color(0xE7E9E3);
    private static final Color HOVER_SURFACE = new Color(0xE8F0F1);
    private static final Color ERROR_COLOR = new Color(220, 80, 80);
    private static final Color SUCCESS_COLOR = new Color(60, 160, 80);
    private static final Color SECONDARY_TEXT = new Color(122, 138, 135);
    
    // Action Colors
    private static final Color COLOR_LOGIN = new Color(52, 152, 219);
    private static final Color COLOR_LOGOUT = new Color(149, 165, 166);
    private static final Color COLOR_CREATE = new Color(46, 204, 113);
    private static final Color COLOR_UPDATE = new Color(241, 196, 15);
    private static final Color COLOR_DELETE = new Color(231, 76, 60);
    private static final Color COLOR_VIEW = new Color(155, 89, 182);

    // Components
    private JTable logTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private RoundedButton searchButton;
    private RoundedButton refreshButton;
    private RoundedButton clearFilterButton;
    private RoundedButton exportButton;
    private JLabel statusLabel;
    private JLabel countLabel;
    private JComboBox<String> actionCombo;
    private JComboBox<String> userCombo;
    private JComboBox<String> dateFilterCombo;
    
    private AuditController controller;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public ActivityLogPanel() {
        this.controller = new AuditController(this);
        initComponents();
        loadLogs();
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
        
        JLabel titleLabel = new JLabel("Activity Log");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_DARK);
        
        JLabel subtitleLabel = new JLabel("View and monitor all system activities");
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
        dateFilterCombo.addActionListener(e -> loadLogs());

        // Action filter
        String[] actions = {"All Actions", "LOGIN", "LOGOUT", "CREATE", "UPDATE", "DELETE", "VIEW"};
        actionCombo = new JComboBox<>(actions);
        actionCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        actionCombo.setPreferredSize(new Dimension(120, 35));
        actionCombo.addActionListener(e -> loadLogs());

        // User filter
        userCombo = new JComboBox<>();
        userCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        userCombo.setPreferredSize(new Dimension(150, 35));
        userCombo.addItem("All Users");
        userCombo.addActionListener(e -> loadLogs());

        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setPreferredSize(new Dimension(200, 35));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        searchField.addActionListener(e -> loadLogs());

        searchButton = createStyledButton("Search", PRIMARY_DARK, Color.WHITE);
        searchButton.setPreferredSize(new Dimension(100, 35));
        searchButton.addActionListener(e -> loadLogs());

        clearFilterButton = createStyledButton("Clear Filters", SOFT_SURFACE, PRIMARY_DARK);
        clearFilterButton.setBorderColor(LIGHT_SURFACE);
        clearFilterButton.setPreferredSize(new Dimension(120, 35));
        clearFilterButton.addActionListener(e -> clearFilters());

        refreshButton = createStyledButton("Refresh", SOFT_SURFACE, PRIMARY_DARK);
        refreshButton.setBorderColor(LIGHT_SURFACE);
        refreshButton.setPreferredSize(new Dimension(100, 35));
        refreshButton.addActionListener(e -> loadLogs());

        exportButton = createStyledButton("Export CSV", SOFT_SURFACE, PRIMARY_DARK);
        exportButton.setBorderColor(LIGHT_SURFACE);
        exportButton.setPreferredSize(new Dimension(120, 35));
        exportButton.addActionListener(e -> exportLogs());

        searchPanel.add(new JLabel("Date:"));
        searchPanel.add(dateFilterCombo);
        searchPanel.add(new JLabel("Action:"));
        searchPanel.add(actionCombo);
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
        String[] columns = {"ID", "User", "Action", "Description", "IP Address", "Timestamp"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        logTable = new JTable(tableModel);
        logTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        logTable.setRowHeight(35);
        logTable.setSelectionBackground(new Color(235, 245, 240));
        logTable.setSelectionForeground(PRIMARY_DARK);
        logTable.setShowGrid(true);
        logTable.setGridColor(LIGHT_SURFACE);
        logTable.setIntercellSpacing(new Dimension(5, 5));

        // Set column widths
        logTable.getColumnModel().getColumn(0).setMaxWidth(60);
        logTable.getColumnModel().getColumn(0).setMinWidth(50);
        logTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        logTable.getColumnModel().getColumn(2).setMaxWidth(100);
        logTable.getColumnModel().getColumn(3).setPreferredWidth(300);
        logTable.getColumnModel().getColumn(4).setMaxWidth(150);
        logTable.getColumnModel().getColumn(5).setPreferredWidth(160);

        // Custom header
        JTableHeader header = logTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(MINT);
        header.setForeground(PRIMARY_DARK);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_DARK));

        // Custom cell renderer for action column
        logTable.getColumnModel().getColumn(2).setCellRenderer(new ActionCellRenderer());

        JScrollPane scrollPane = new JScrollPane(logTable);
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

        footer.add(statusLabel, BorderLayout.WEST);
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

    // Action Cell Renderer
    private class ActionCellRenderer extends javax.swing.table.DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (value != null) {
                String action = value.toString();
                JLabel label = (JLabel) c;
                label.setOpaque(true);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
                
                switch (action) {
                    case "LOGIN":
                        label.setBackground(COLOR_LOGIN);
                        label.setForeground(Color.WHITE);
                        break;
                    case "LOGOUT":
                        label.setBackground(COLOR_LOGOUT);
                        label.setForeground(Color.WHITE);
                        break;
                    case "CREATE":
                        label.setBackground(COLOR_CREATE);
                        label.setForeground(Color.WHITE);
                        break;
                    case "UPDATE":
                        label.setBackground(COLOR_UPDATE);
                        label.setForeground(Color.BLACK);
                        break;
                    case "DELETE":
                        label.setBackground(COLOR_DELETE);
                        label.setForeground(Color.WHITE);
                        break;
                    case "VIEW":
                        label.setBackground(COLOR_VIEW);
                        label.setForeground(Color.WHITE);
                        break;
                    default:
                        label.setBackground(LIGHT_SURFACE);
                        label.setForeground(PRIMARY_DARK);
                        break;
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

    public void loadLogs() {
        String searchText = searchField != null ? searchField.getText().trim() : "";
        String action = actionCombo != null ? (String) actionCombo.getSelectedItem() : "All Actions";
        String user = userCombo != null ? (String) userCombo.getSelectedItem() : "All Users";
        String dateFilter = dateFilterCombo != null ? (String) dateFilterCombo.getSelectedItem() : "All Dates";
        
        // Load users for filter
        loadUsers();
        
        // Use SwingWorker to load in background
        SwingWorker<List<AuditLog>, Void> worker = new SwingWorker<List<AuditLog>, Void>() {
            @Override
            protected List<AuditLog> doInBackground() throws Exception {
                List<AuditLog> logs = controller.getAllLogs();
                
                // Apply action filter
                if (action != null && !action.equals("All Actions") && logs != null) {
                    logs.removeIf(l -> !l.getAction().equals(action));
                }
                
                // Apply user filter
                if (user != null && !user.equals("All Users") && logs != null) {
                    logs.removeIf(l -> !l.getUsername().equals(user));
                }
                
                // Apply date filter
                if (dateFilter != null && !dateFilter.equals("All Dates") && logs != null) {
                    LocalDate today = LocalDate.now();
                    switch (dateFilter) {
                        case "Today":
                            logs.removeIf(l -> {
                                if (l.getCreatedAt() == null) return true;
                                return !l.getCreatedAt().toLocalDateTime().toLocalDate().equals(today);
                            });
                            break;
                        case "Yesterday":
                            logs.removeIf(l -> {
                                if (l.getCreatedAt() == null) return true;
                                return !l.getCreatedAt().toLocalDateTime().toLocalDate().equals(today.minusDays(1));
                            });
                            break;
                        case "This Week":
                            LocalDate weekStart = today.minusDays(today.getDayOfWeek().getValue() - 1);
                            LocalDate weekEnd = weekStart.plusDays(6);
                            logs.removeIf(l -> {
                                if (l.getCreatedAt() == null) return true;
                                LocalDate date = l.getCreatedAt().toLocalDateTime().toLocalDate();
                                return date.isBefore(weekStart) || date.isAfter(weekEnd);
                            });
                            break;
                        case "This Month":
                            LocalDate monthStart = today.withDayOfMonth(1);
                            LocalDate monthEnd = today.withDayOfMonth(today.lengthOfMonth());
                            logs.removeIf(l -> {
                                if (l.getCreatedAt() == null) return true;
                                LocalDate date = l.getCreatedAt().toLocalDateTime().toLocalDate();
                                return date.isBefore(monthStart) || date.isAfter(monthEnd);
                            });
                            break;
                    }
                }
                
                // Apply search filter
                if (searchText != null && !searchText.isEmpty() && logs != null) {
                    logs.removeIf(l -> 
                        (l.getUsername() != null && !l.getUsername().toLowerCase().contains(searchText.toLowerCase())) &&
                        (l.getAction() != null && !l.getAction().toLowerCase().contains(searchText.toLowerCase())) &&
                        (l.getDescription() != null && !l.getDescription().toLowerCase().contains(searchText.toLowerCase()))
                    );
                }
                
                return logs;
            }

            @Override
            protected void done() {
                try {
                    List<AuditLog> logs = get();
                    displayLogs(logs);
                } catch (Exception e) {
                    showError("Error loading logs: " + e.getMessage());
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

    public void displayLogs(List<AuditLog> logs) {
        if (tableModel == null) return;
        
        tableModel.setRowCount(0);
        
        if (logs == null || logs.isEmpty()) {
            statusLabel.setText("No logs found");
            countLabel.setText("Total: 0 records");
            return;
        }

        for (AuditLog log : logs) {
            Object[] row = {
                log.getAuditId(),
                log.getUsername() != null ? log.getUsername() : "System",
                log.getAction() != null ? log.getAction() : "N/A",
                log.getDescription() != null ? log.getDescription() : "N/A",
                log.getIpAddress() != null ? log.getIpAddress() : "N/A",
                log.getCreatedAt() != null ? sdf.format(log.getCreatedAt()) : "N/A"
            };
            tableModel.addRow(row);
        }

        statusLabel.setText("Loaded " + logs.size() + " records");
        countLabel.setText("Total: " + logs.size() + " records");
    }

    private void clearFilters() {
        dateFilterCombo.setSelectedIndex(0);
        actionCombo.setSelectedIndex(0);
        userCombo.setSelectedIndex(0);
        searchField.setText("");
        loadLogs();
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