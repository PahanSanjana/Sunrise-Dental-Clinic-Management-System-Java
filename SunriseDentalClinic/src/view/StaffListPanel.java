package view;

import controller.StaffController;
import model.Staff;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
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

    // Components
    private JTable staffTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private RoundedButton searchButton;
    private RoundedButton addButton;
    private RoundedButton refreshButton;
    private RoundedButton viewButton;
    private RoundedButton editButton;
    private RoundedButton deleteButton;
    private RoundedButton toggleStatusButton;
    private JLabel statusLabel;
    private JLabel countLabel;
    private JComboBox<String> filterCombo;
    private JComboBox<String> departmentCombo;
    
    private StaffController controller;

    public StaffListPanel() {
        this.controller = new StaffController(this);
        initComponents();
        loadStaff();
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
        
        JLabel titleLabel = new JLabel("Staff Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_DARK);
        
        JLabel subtitleLabel = new JLabel("Manage all staff members in the system");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(107, 123, 121));
        
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setOpaque(false);

        // Department filter
        departmentCombo = new JComboBox<>(new String[]{"All Departments", "Front Desk", "Clinical", "Administration", "Other"});
        departmentCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        departmentCombo.setPreferredSize(new Dimension(150, 35));
        departmentCombo.addActionListener(e -> loadStaff());

        // Status filter
        filterCombo = new JComboBox<>(new String[]{"All", "Active", "Inactive"});
        filterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        filterCombo.setPreferredSize(new Dimension(100, 35));
        filterCombo.addActionListener(e -> loadStaff());

        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setPreferredSize(new Dimension(250, 35));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        searchField.addActionListener(e -> loadStaff());

        searchButton = createStyledButton("Search", PRIMARY_DARK, Color.WHITE);
        searchButton.setPreferredSize(new Dimension(100, 35));
        searchButton.addActionListener(e -> loadStaff());

        addButton = createStyledButton("Add Staff", PRIMARY_DARK, Color.WHITE);
        addButton.setPreferredSize(new Dimension(120, 35));
        addButton.addActionListener(e -> {
            Container parent = getParent();
            while (parent != null && !(parent instanceof MainFrame)) {
                parent = parent.getParent();
            }
            if (parent instanceof MainFrame) {
                ((MainFrame) parent).showCard("STAFF_ADD");
            }
        });

        refreshButton = createStyledButton("Refresh", SOFT_SURFACE, PRIMARY_DARK);
        refreshButton.setBorderColor(LIGHT_SURFACE);
        refreshButton.setPreferredSize(new Dimension(100, 35));
        refreshButton.addActionListener(e -> loadStaff());

        searchPanel.add(new JLabel("Department:"));
        searchPanel.add(departmentCombo);
        searchPanel.add(new JLabel("Status:"));
        searchPanel.add(filterCombo);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(addButton);
        searchPanel.add(refreshButton);

        header.add(titlePanel, BorderLayout.WEST);
        header.add(searchPanel, BorderLayout.EAST);

        return header;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(LIGHT_SURFACE, 1));

        // Create table model
        String[] columns = {"ID", "Name", "Position", "Department", "Phone", "Email", "Hire Date", "Salary", "Status", "Actions"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        staffTable = new JTable(tableModel);
        staffTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
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
        staffTable.getColumnModel().getColumn(9).setMaxWidth(200);
        staffTable.getColumnModel().getColumn(9).setMinWidth(180);

        // Custom header
        JTableHeader header = staffTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(MINT);
        header.setForeground(PRIMARY_DARK);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_DARK));

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

        // Action buttons for each row
        staffTable.getColumnModel().getColumn(9).setCellRenderer(new ActionButtonRenderer());
        staffTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int column = staffTable.getColumnModel().getColumnIndex("Actions");
                int row = staffTable.rowAtPoint(e.getPoint());
                int col = staffTable.columnAtPoint(e.getPoint());
                
                if (row >= 0 && col == column) {
                    int x = e.getX();
                    int cellWidth = staffTable.getCellRect(row, col, true).width;
                    int buttonWidth = cellWidth / 4;
                    
                    if (x < buttonWidth) {
                        viewStaff(row);
                    } else if (x < buttonWidth * 2) {
                        editStaff(row);
                    } else if (x < buttonWidth * 3) {
                        toggleStatus(row);
                    } else {
                        deleteStaff(row);
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
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(107, 123, 121));

        countLabel = new JLabel("Total: 0 staff members");
        countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        countLabel.setForeground(new Color(107, 123, 121));

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        
        viewButton = createStyledButton("View", SOFT_SURFACE, PRIMARY_DARK);
        viewButton.setBorderColor(LIGHT_SURFACE);
        viewButton.setPreferredSize(new Dimension(80, 30));
        viewButton.addActionListener(e -> {
            int row = staffTable.getSelectedRow();
            if (row != -1) {
                viewStaff(row);
            } else {
                showError("Please select a staff member to view.");
            }
        });

        editButton = createStyledButton("Edit", SOFT_SURFACE, PRIMARY_DARK);
        editButton.setBorderColor(LIGHT_SURFACE);
        editButton.setPreferredSize(new Dimension(80, 30));
        editButton.addActionListener(e -> {
            int row = staffTable.getSelectedRow();
            if (row != -1) {
                editStaff(row);
            } else {
                showError("Please select a staff member to edit.");
            }
        });

        toggleStatusButton = createStyledButton("Toggle Status", SOFT_SURFACE, PRIMARY_DARK);
        toggleStatusButton.setBorderColor(LIGHT_SURFACE);
        toggleStatusButton.setPreferredSize(new Dimension(120, 30));
        toggleStatusButton.addActionListener(e -> {
            int row = staffTable.getSelectedRow();
            if (row != -1) {
                toggleStatus(row);
            } else {
                showError("Please select a staff member to toggle status.");
            }
        });

        deleteButton = createStyledButton("Delete", SOFT_SURFACE, ERROR_COLOR);
        deleteButton.setBorderColor(LIGHT_SURFACE);
        deleteButton.setPreferredSize(new Dimension(80, 30));
        deleteButton.addActionListener(e -> {
            int row = staffTable.getSelectedRow();
            if (row != -1) {
                deleteStaff(row);
            } else {
                showError("Please select a staff member to delete.");
            }
        });

        rightPanel.add(viewButton);
        rightPanel.add(editButton);
        rightPanel.add(toggleStatusButton);
        rightPanel.add(deleteButton);

        footer.add(statusLabel, BorderLayout.WEST);
        footer.add(countLabel, BorderLayout.CENTER);
        footer.add(rightPanel, BorderLayout.EAST);

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

    // Action Button Renderer for table
    private class ActionButtonRenderer extends javax.swing.table.DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            JPanel panel = new JPanel(new GridLayout(1, 4, 3, 3));
            panel.setOpaque(true);
            panel.setBackground(isSelected ? new Color(235, 245, 240) : Color.WHITE);
            panel.setBorder(BorderFactory.createEmptyBorder(2, 3, 2, 3));
            
            JButton viewBtn = new JButton("View");
            viewBtn.setFont(new Font("Segoe UI", Font.PLAIN, 9));
            viewBtn.setBackground(MINT);
            viewBtn.setForeground(PRIMARY_DARK);
            viewBtn.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
            viewBtn.setFocusPainted(false);
            viewBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            JButton editBtn = new JButton("Edit");
            editBtn.setFont(new Font("Segoe UI", Font.PLAIN, 9));
            editBtn.setBackground(new Color(200, 220, 240));
            editBtn.setForeground(PRIMARY_DARK);
            editBtn.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
            editBtn.setFocusPainted(false);
            editBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            // Check status from table data
            String status = (String) table.getValueAt(row, 8);
            boolean isActive = "Active".equals(status);
            
            JButton statusBtn = new JButton(isActive ? "Active" : "Inactive");
            statusBtn.setFont(new Font("Segoe UI", Font.PLAIN, 9));
            statusBtn.setBackground(isActive ? ACTIVE_COLOR : INACTIVE_COLOR);
            statusBtn.setForeground(Color.WHITE);
            statusBtn.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
            statusBtn.setFocusPainted(false);
            statusBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            JButton delBtn = new JButton("Delete");
            delBtn.setFont(new Font("Segoe UI", Font.PLAIN, 9));
            delBtn.setBackground(new Color(240, 200, 200));
            delBtn.setForeground(ERROR_COLOR);
            delBtn.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
            delBtn.setFocusPainted(false);
            delBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            panel.add(viewBtn);
            panel.add(editBtn);
            panel.add(statusBtn);
            panel.add(delBtn);
            
            return panel;
        }
    }

    // ========================
    // Public methods
    // ========================

    public void loadStaff() {
        String searchText = searchField != null ? searchField.getText().trim() : "";
        String filter = filterCombo != null ? (String) filterCombo.getSelectedItem() : "All";
        String department = departmentCombo != null ? (String) departmentCombo.getSelectedItem() : "All Departments";
        
        // Use SwingWorker to load in background
        SwingWorker<List<Staff>, Void> worker = new SwingWorker<List<Staff>, Void>() {
            @Override
            protected List<Staff> doInBackground() throws Exception {
                List<Staff> staffList;
                
                if (searchText != null && !searchText.isEmpty()) {
                    staffList = controller.searchStaff(searchText);
                } else {
                    staffList = controller.getAllStaff();
                }
                
                // Apply department filter
                if (department != null && !department.equals("All Departments") && staffList != null) {
                    staffList.removeIf(s -> s.getDepartment() == null || !s.getDepartment().equals(department));
                }
                
                // Apply status filter
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
                "$" + df.format(staff.getSalary()),
                status,
                "Actions"
            };
            tableModel.addRow(row);
        }

        statusLabel.setText("Loaded " + staffList.size() + " staff members");
        countLabel.setText("Total: " + staffList.size() + " staff members");
    }

    public void viewStaff(int row) {
        int staffId = (int) tableModel.getValueAt(row, 0);
        
        // Navigate to staff details
        Container parent = getParent();
        while (parent != null && !(parent instanceof MainFrame)) {
            parent = parent.getParent();
        }
        if (parent instanceof MainFrame) {
            MainFrame mainFrame = (MainFrame) parent;
            
            // Get the staff from database
            Staff staff = controller.getStaffById(staffId);
            if (staff != null) {
                // Create a new details panel with the staff
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
        
        // Navigate to staff details in edit mode
        Container parent = getParent();
        while (parent != null && !(parent instanceof MainFrame)) {
            parent = parent.getParent();
        }
        if (parent instanceof MainFrame) {
            MainFrame mainFrame = (MainFrame) parent;
            
            // Get the staff from database
            Staff staff = controller.getStaffById(staffId);
            if (staff != null) {
                // Create a new details panel with the staff
                StaffDetailsPanel detailsPanel = new StaffDetailsPanel(staff);
                detailsPanel.setName("STAFF_DETAILS");
                mainFrame.addScreen("STAFF_DETAILS", detailsPanel);
                mainFrame.showCard("STAFF_DETAILS");
                // Switch to edit mode
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
                loadStaff(); // Refresh the list
            } else {
                showError("Failed to update staff status.");
            }
        }
    }

    public void deleteStaff(int row) {
        int staffId = (int) tableModel.getValueAt(row, 0);
        String staffName = (String) tableModel.getValueAt(row, 1);
        
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
                loadStaff(); // Refresh the list
            } else {
                showError("Failed to delete staff member.");
            }
        }
    }

    public void showError(String message) {
        statusLabel.setText("❌ " + message);
        statusLabel.setForeground(ERROR_COLOR);
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showSuccess(String message) {
        statusLabel.setText("✅ " + message);
        statusLabel.setForeground(SUCCESS_COLOR);
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showInfo(String message) {
        statusLabel.setText("ℹ️ " + message);
        statusLabel.setForeground(new Color(107, 123, 121));
    }
}