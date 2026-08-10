package view;

import controller.BillController;
import model.Bill;
import model.Patient;
import model.BillItem;
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

public class BillListPanel extends JPanel {
    
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
    private static final Color COLOR_PENDING = new Color(241, 196, 15);
    private static final Color COLOR_PAID = new Color(46, 204, 113);
    private static final Color COLOR_PARTIAL = new Color(52, 152, 219);
    private static final Color COLOR_OVERDUE = new Color(231, 76, 60);
    private static final Color COLOR_DRAFT = new Color(149, 165, 166);
    private static final Color COLOR_CANCELLED = new Color(149, 165, 166);

    // Components
    private JTable billTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private RoundedButton searchButton;
    private RoundedButton addButton;
    private RoundedButton refreshButton;
    private RoundedButton viewButton;
    private RoundedButton editButton;
    private RoundedButton deleteButton;
    private RoundedButton markPaidButton;
    private JLabel statusLabel;
    private JLabel countLabel;
    private JLabel totalRevenueLabel;
    private JComboBox<String> statusCombo;
    private JComboBox<String> dateFilterCombo;
    
    private BillController controller;
    private DecimalFormat df = new DecimalFormat("#.00");

    public BillListPanel() {
        this.controller = new BillController(this);
        initComponents();
        loadBills();
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
        
        JLabel titleLabel = new JLabel("Bill Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_DARK);
        
        JLabel subtitleLabel = new JLabel("Manage all bills in the system");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(107, 123, 121));
        
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setOpaque(false);

        // Date filter
        String[] dateFilters = {"All Dates", "Today", "This Week", "This Month"};
        dateFilterCombo = new JComboBox<>(dateFilters);
        dateFilterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dateFilterCombo.setPreferredSize(new Dimension(120, 35));
        dateFilterCombo.addActionListener(e -> loadBills());

        // Status filter
        String[] statuses = {"All Status", "Pending", "Paid", "Partial", "Overdue", "Draft", "Cancelled"};
        statusCombo = new JComboBox<>(statuses);
        statusCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusCombo.setPreferredSize(new Dimension(120, 35));
        statusCombo.addActionListener(e -> loadBills());

        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setPreferredSize(new Dimension(200, 35));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        searchField.addActionListener(e -> loadBills());

        searchButton = createStyledButton("Search", PRIMARY_DARK, Color.WHITE);
        searchButton.setPreferredSize(new Dimension(100, 35));
        searchButton.addActionListener(e -> loadBills());

        addButton = createStyledButton("Generate Bill", PRIMARY_DARK, Color.WHITE);
        addButton.setPreferredSize(new Dimension(140, 35));
        addButton.addActionListener(e -> {
            Container parent = getParent();
            while (parent != null && !(parent instanceof MainFrame)) {
                parent = parent.getParent();
            }
            if (parent instanceof MainFrame) {
                ((MainFrame) parent).showCard("BILL_GENERATE");
            }
        });

        refreshButton = createStyledButton("Refresh", SOFT_SURFACE, PRIMARY_DARK);
        refreshButton.setBorderColor(LIGHT_SURFACE);
        refreshButton.setPreferredSize(new Dimension(100, 35));
        refreshButton.addActionListener(e -> loadBills());

        searchPanel.add(new JLabel("Date:"));
        searchPanel.add(dateFilterCombo);
        searchPanel.add(new JLabel("Status:"));
        searchPanel.add(statusCombo);
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
        String[] columns = {"ID", "Bill Number", "Patient", "Date", "Due Date", "Total", "Status", "Actions"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        billTable = new JTable(tableModel);
        billTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        billTable.setRowHeight(40);
        billTable.setSelectionBackground(new Color(235, 245, 240));
        billTable.setSelectionForeground(PRIMARY_DARK);
        billTable.setShowGrid(true);
        billTable.setGridColor(LIGHT_SURFACE);
        billTable.setIntercellSpacing(new Dimension(5, 5));

        // Set column widths
        billTable.getColumnModel().getColumn(0).setMaxWidth(60);
        billTable.getColumnModel().getColumn(0).setMinWidth(50);
        billTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        billTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        billTable.getColumnModel().getColumn(5).setMaxWidth(100);
        billTable.getColumnModel().getColumn(6).setMaxWidth(120);
        billTable.getColumnModel().getColumn(7).setMaxWidth(180);
        billTable.getColumnModel().getColumn(7).setMinWidth(150);

        // Custom header
        JTableHeader header = billTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(MINT);
        header.setForeground(PRIMARY_DARK);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_DARK));

        // Custom cell renderer for status column
        billTable.getColumnModel().getColumn(6).setCellRenderer(new StatusCellRenderer());

        // Add mouse listener for double click to view
        billTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = billTable.getSelectedRow();
                    if (row != -1) {
                        viewBill(row);
                    }
                }
            }
        });

        // Action buttons for each row
        billTable.getColumnModel().getColumn(7).setCellRenderer(new ActionButtonRenderer());
        billTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int column = billTable.getColumnModel().getColumnIndex("Actions");
                int row = billTable.rowAtPoint(e.getPoint());
                int col = billTable.columnAtPoint(e.getPoint());
                
                if (row >= 0 && col == column) {
                    int x = e.getX();
                    int cellWidth = billTable.getCellRect(row, col, true).width;
                    int buttonWidth = cellWidth / 3;
                    
                    if (x < buttonWidth) {
                        viewBill(row);
                    } else if (x < buttonWidth * 2) {
                        editBill(row);
                    } else {
                        deleteBill(row);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(billTable);
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

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);

        countLabel = new JLabel("Total: 0 bills");
        countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        countLabel.setForeground(new Color(107, 123, 121));

        totalRevenueLabel = new JLabel("Total Revenue: $0.00");
        totalRevenueLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        totalRevenueLabel.setForeground(SUCCESS_COLOR);

        rightPanel.add(countLabel);
        rightPanel.add(new JLabel("|"));
        rightPanel.add(totalRevenueLabel);

        footer.add(statusLabel, BorderLayout.WEST);
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
                
                switch (status) {
                    case "Pending":
                        label.setBackground(COLOR_PENDING);
                        label.setForeground(Color.BLACK);
                        break;
                    case "Paid":
                        label.setBackground(COLOR_PAID);
                        label.setForeground(Color.WHITE);
                        break;
                    case "Partial":
                        label.setBackground(COLOR_PARTIAL);
                        label.setForeground(Color.WHITE);
                        break;
                    case "Overdue":
                        label.setBackground(COLOR_OVERDUE);
                        label.setForeground(Color.WHITE);
                        break;
                    case "Draft":
                        label.setBackground(COLOR_DRAFT);
                        label.setForeground(Color.WHITE);
                        break;
                    case "Cancelled":
                        label.setBackground(COLOR_CANCELLED);
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

    // Action Button Renderer for table
    private class ActionButtonRenderer extends javax.swing.table.DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            JPanel panel = new JPanel(new GridLayout(1, 3, 3, 3));
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
            
            JButton delBtn = new JButton("Delete");
            delBtn.setFont(new Font("Segoe UI", Font.PLAIN, 9));
            delBtn.setBackground(new Color(240, 200, 200));
            delBtn.setForeground(ERROR_COLOR);
            delBtn.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
            delBtn.setFocusPainted(false);
            delBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            panel.add(viewBtn);
            panel.add(editBtn);
            panel.add(delBtn);
            
            return panel;
        }
    }

    // ========================
    // Public methods
    // ========================

    public void loadBills() {
        String searchText = searchField != null ? searchField.getText().trim() : "";
        String status = statusCombo != null ? (String) statusCombo.getSelectedItem() : "All Status";
        String dateFilter = dateFilterCombo != null ? (String) dateFilterCombo.getSelectedItem() : "All Dates";
        
        // Use SwingWorker to load in background
        SwingWorker<List<Bill>, Void> worker = new SwingWorker<List<Bill>, Void>() {
            @Override
            protected List<Bill> doInBackground() throws Exception {
                return controller.getFilteredBills(searchText, status, dateFilter);
            }

            @Override
            protected void done() {
                try {
                    List<Bill> bills = get();
                    displayBills(bills);
                    updateSummary(bills);
                } catch (Exception e) {
                    showError("Error loading bills: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    public void displayBills(List<Bill> bills) {
        if (tableModel == null) return;
        
        tableModel.setRowCount(0);
        
        if (bills == null || bills.isEmpty()) {
            statusLabel.setText("No bills found");
            countLabel.setText("Total: 0 bills");
            totalRevenueLabel.setText("Total Revenue: $0.00");
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        for (Bill bill : bills) {
            String patientName = controller.getPatientName(bill.getPatientId());
            String date = bill.getBillDate() != null ? dateFormat.format(bill.getBillDate()) : "N/A";
            String dueDate = bill.getDueDate() != null ? dateFormat.format(bill.getDueDate()) : "N/A";
            
            Object[] row = {
                bill.getBillId(),
                bill.getBillNumber(),
                patientName,
                date,
                dueDate,
                "$" + df.format(bill.getTotalAmount()),
                bill.getStatus() != null ? bill.getStatus() : "N/A",
                "Actions"
            };
            tableModel.addRow(row);
        }

        statusLabel.setText("Loaded " + bills.size() + " bills");
        countLabel.setText("Total: " + bills.size() + " bills");
    }

    private void updateSummary(List<Bill> bills) {
        if (bills == null || bills.isEmpty()) {
            totalRevenueLabel.setText("Total Revenue: $0.00");
            return;
        }
        
        double totalRevenue = controller.getTotalRevenueFromBills(bills);
        totalRevenueLabel.setText("Total Revenue: $" + df.format(totalRevenue));
    }

    public void viewBill(int row) {
        int billId = (int) tableModel.getValueAt(row, 0);
        
        // Navigate to bill details
        Container parent = getParent();
        while (parent != null && !(parent instanceof MainFrame)) {
            parent = parent.getParent();
        }
        if (parent instanceof MainFrame) {
            MainFrame mainFrame = (MainFrame) parent;
            
            // Get the bill from database
            Bill bill = controller.getBillById(billId);
            if (bill != null) {
                List<BillItem> items = controller.getBillItemsByBillId(billId);
                // Create a new details panel with the bill
                BillDetailsPanel detailsPanel = new BillDetailsPanel(bill, items);
                detailsPanel.setName("BILL_DETAILS");
                mainFrame.addScreen("BILL_DETAILS", detailsPanel);
                mainFrame.showCard("BILL_DETAILS");
            } else {
                showError("Bill not found.");
            }
        }
    }

    public void editBill(int row) {
        int billId = (int) tableModel.getValueAt(row, 0);
        
        // Navigate to bill details in edit mode
        Container parent = getParent();
        while (parent != null && !(parent instanceof MainFrame)) {
            parent = parent.getParent();
        }
        if (parent instanceof MainFrame) {
            MainFrame mainFrame = (MainFrame) parent;
            
            // Get the bill from database
            Bill bill = controller.getBillById(billId);
            if (bill != null) {
                List<BillItem> items = controller.getBillItemsByBillId(billId);
                // Create a new details panel with the bill
                BillDetailsPanel detailsPanel = new BillDetailsPanel(bill, items);
                detailsPanel.setName("BILL_DETAILS");
                mainFrame.addScreen("BILL_DETAILS", detailsPanel);
                mainFrame.showCard("BILL_DETAILS");
                // Switch to edit mode
                detailsPanel.toggleEditMode();
            } else {
                showError("Bill not found.");
            }
        }
    }

    public void deleteBill(int row) {
        int billId = (int) tableModel.getValueAt(row, 0);
        String billNumber = (String) tableModel.getValueAt(row, 1);
        
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete bill: " + billNumber + "?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = controller.deleteBill(billId);
            if (success) {
                showSuccess("Bill deleted successfully!");
                loadBills(); // Refresh the list
            } else {
                showError("Failed to delete bill.");
            }
        }
    }

    public void markBillAsPaid(int row) {
        int billId = (int) tableModel.getValueAt(row, 0);
        String billNumber = (String) tableModel.getValueAt(row, 1);
        String currentStatus = (String) tableModel.getValueAt(row, 6);
        
        if ("Paid".equals(currentStatus)) {
            showError("This bill is already paid.");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to mark bill " + billNumber + " as paid?",
            "Mark as Paid",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = controller.updateBillStatus(billId, "Paid");
            if (success) {
                showSuccess("Bill marked as paid!");
                loadBills(); // Refresh the list
            } else {
                showError("Failed to update bill status.");
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

    private static class MouseAdapter extends java.awt.event.MouseAdapter {
        // Empty implementation
    }
}