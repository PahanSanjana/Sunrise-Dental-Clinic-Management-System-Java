package view;

import controller.BillController;
import model.Bill;
import model.Patient;
import model.BillItem;
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

    // Components
    private JTable billTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private RoundedButton searchButton;
    private RoundedButton addButton;
    private JButton refreshButton;
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

    // ✅ Auto-refresh timer (hidden)
    private Timer refreshTimer;
    private static final int AUTO_REFRESH_DELAY = 30000; // 30 seconds

    public BillListPanel() {
        this.controller = new BillController(this);
        initComponents();
        loadBills();
        startAutoRefresh();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(SOFT_SURFACE);
        setBorder(new EmptyBorder(20, 30, 20, 30));

        // Title Panel - At the top
        add(createTitlePanel(), BorderLayout.NORTH);
        
        // Main Content Panel (Search + Table + Footer)
        add(createMainContentPanel(), BorderLayout.CENTER);
    }

    // =====================================================
    // ✅ AUTO-REFRESH (Hidden - No UI Indicator)
    // =====================================================
    
    private void startAutoRefresh() {
        if (refreshTimer == null) {
            refreshTimer = new Timer(AUTO_REFRESH_DELAY, e -> {
                if (isShowing()) {
                    loadBills();
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

    /**
     * ✅ Title Panel - Separate from other content
     */
    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(SOFT_SURFACE);
        titlePanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JLabel titleLabel = new JLabel("Bill Management");
        titleLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_DARK);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Manage all bills in the system");
        subtitleLabel.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(107, 123, 121));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 2)));
        titlePanel.add(subtitleLabel);
        
        return titlePanel;
    }

    /**
     * ✅ Main Content Panel - Contains search, table, and footer
     */
    private JPanel createMainContentPanel() {
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

        return mainPanel;
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setOpaque(false);

        // Date filter
        String[] dateFilters = {"All Dates", "Today", "This Week", "This Month"};
        dateFilterCombo = new JComboBox<>(dateFilters);
        dateFilterCombo.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        dateFilterCombo.setPreferredSize(new Dimension(120, 35));
        dateFilterCombo.addActionListener(e -> loadBills());

        // Status filter
        String[] statuses = {"All Status", "Pending", "Paid", "Partial", "Overdue", "Draft", "Cancelled"};
        statusCombo = new JComboBox<>(statuses);
        statusCombo.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        statusCombo.setPreferredSize(new Dimension(120, 35));
        statusCombo.addActionListener(e -> loadBills());

        searchField = new JTextField(20);
        searchField.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        searchField.setPreferredSize(new Dimension(200, 35));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        searchField.addActionListener(e -> loadBills());

        searchButton = createStyledButton("Search", PRIMARY_DARK, Color.WHITE);
        searchButton.setPreferredSize(new Dimension(100, 35));
        searchButton.addActionListener(e -> loadBills());
        searchButton.setIcon(icon(FontAwesomeSolid.SEARCH, 14, Color.WHITE));
        searchButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        searchButton.setIconTextGap(6);

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
        addButton.setIcon(icon(FontAwesomeSolid.FILE_INVOICE_DOLLAR, 14, Color.WHITE));
        addButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        addButton.setIconTextGap(6);

        // ✅ Manual Refresh Button - ICON ONLY
        refreshButton = createIconButton(FontAwesomeSolid.SYNC_ALT, COLOR_REFRESH);
        refreshButton.setPreferredSize(new Dimension(40, 40));
        refreshButton.setToolTipText("Refresh Now");
        refreshButton.addActionListener(e -> loadBills());

        searchPanel.add(new JLabel("Date:"));
        searchPanel.add(dateFilterCombo);
        searchPanel.add(new JLabel("Status:"));
        searchPanel.add(statusCombo);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(addButton);
        searchPanel.add(refreshButton);

        return searchPanel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // ✅ Removed "Actions" column - No buttons inside table
        String[] columns = {"ID", "Bill Number", "Patient", "Date", "Due Date", "Total", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        billTable = new JTable(tableModel);
        billTable.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
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

        // Custom header
        JTableHeader header = billTable.getTableHeader();
        header.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
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
        statusLabel.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 12));
        statusLabel.setForeground(new Color(107, 123, 121));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftPanel.setOpaque(false);
        
        viewButton = createStyledButton("View", SOFT_SURFACE, PRIMARY_DARK);
        viewButton.setBorderColor(LIGHT_SURFACE);
        viewButton.setPreferredSize(new Dimension(80, 30));
        viewButton.addActionListener(e -> {
            int row = billTable.getSelectedRow();
            if (row != -1) {
                viewBill(row);
            } else {
                showError("Please select a bill to view.");
            }
        });
        viewButton.setIcon(icon(FontAwesomeSolid.EYE, 12, PRIMARY_DARK));
        viewButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        viewButton.setIconTextGap(6);

        editButton = createStyledButton("Edit", SOFT_SURFACE, PRIMARY_DARK);
        editButton.setBorderColor(LIGHT_SURFACE);
        editButton.setPreferredSize(new Dimension(80, 30));
        editButton.addActionListener(e -> {
            int row = billTable.getSelectedRow();
            if (row != -1) {
                editBill(row);
            } else {
                showError("Please select a bill to edit.");
            }
        });
        editButton.setIcon(icon(FontAwesomeSolid.EDIT, 12, PRIMARY_DARK));
        editButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        editButton.setIconTextGap(6);

        markPaidButton = createStyledButton("Mark Paid", SUCCESS_COLOR, Color.WHITE);
        markPaidButton.setPreferredSize(new Dimension(120, 30));
        markPaidButton.addActionListener(e -> {
            int row = billTable.getSelectedRow();
            if (row != -1) {
                markBillAsPaid(row);
            } else {
                showError("Please select a bill to mark as paid.");
            }
        });
        markPaidButton.setIcon(icon(FontAwesomeSolid.CHECK_CIRCLE, 12, Color.WHITE));
        markPaidButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        markPaidButton.setIconTextGap(6);

        deleteButton = createStyledButton("Delete", SOFT_SURFACE, ERROR_COLOR);
        deleteButton.setBorderColor(LIGHT_SURFACE);
        deleteButton.setPreferredSize(new Dimension(100, 30));
        deleteButton.addActionListener(e -> {
            int row = billTable.getSelectedRow();
            if (row != -1) {
                deleteBill(row);
            } else {
                showError("Please select a bill to delete.");
            }
        });
        deleteButton.setIcon(icon(FontAwesomeSolid.TRASH_ALT, 12, ERROR_COLOR));
        deleteButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        deleteButton.setIconTextGap(6);

        leftPanel.add(viewButton);
        leftPanel.add(editButton);
        leftPanel.add(markPaidButton);
        leftPanel.add(deleteButton);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);

        countLabel = new JLabel("Total: 0 bills");
        countLabel.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 12));
        countLabel.setForeground(new Color(107, 123, 121));

        totalRevenueLabel = new JLabel("Total Revenue: RS0.00");
        totalRevenueLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 12));
        totalRevenueLabel.setForeground(SUCCESS_COLOR);

        rightPanel.add(countLabel);
        rightPanel.add(new JLabel("|"));
        rightPanel.add(totalRevenueLabel);

        footer.add(leftPanel, BorderLayout.WEST);
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

    // ========================
    // Public methods
    // ========================

    public void loadBills() {
        String searchText = searchField != null ? searchField.getText().trim() : "";
        String status = statusCombo != null ? (String) statusCombo.getSelectedItem() : "All Status";
        String dateFilter = dateFilterCombo != null ? (String) dateFilterCombo.getSelectedItem() : "All Dates";
        
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
            totalRevenueLabel.setText("Total Revenue: RS0.00");
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
                "RS" + df.format(bill.getTotalAmount()),
                bill.getStatus() != null ? bill.getStatus() : "N/A"
            };
            tableModel.addRow(row);
        }

        statusLabel.setText("Loaded " + bills.size() + " bills");
        countLabel.setText("Total: " + bills.size() + " bills");
    }

    private void updateSummary(List<Bill> bills) {
        if (bills == null || bills.isEmpty()) {
            totalRevenueLabel.setText("Total Revenue: RS0.00");
            return;
        }
        
        double totalRevenue = controller.getTotalRevenueFromBills(bills);
        totalRevenueLabel.setText("Total Revenue: RS" + df.format(totalRevenue));
    }

    public void viewBill(int row) {
        int billId = (int) tableModel.getValueAt(row, 0);
        
        Container parent = getParent();
        while (parent != null && !(parent instanceof MainFrame)) {
            parent = parent.getParent();
        }
        if (parent instanceof MainFrame) {
            MainFrame mainFrame = (MainFrame) parent;
            
            Bill bill = controller.getBillById(billId);
            if (bill != null) {
                List<BillItem> items = controller.getBillItemsByBillId(billId);
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
        
        Container parent = getParent();
        while (parent != null && !(parent instanceof MainFrame)) {
            parent = parent.getParent();
        }
        if (parent instanceof MainFrame) {
            MainFrame mainFrame = (MainFrame) parent;
            
            Bill bill = controller.getBillById(billId);
            if (bill != null) {
                List<BillItem> items = controller.getBillItemsByBillId(billId);
                BillDetailsPanel detailsPanel = new BillDetailsPanel(bill, items);
                detailsPanel.setName("BILL_DETAILS");
                mainFrame.addScreen("BILL_DETAILS", detailsPanel);
                mainFrame.showCard("BILL_DETAILS");
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
                loadBills();
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
                loadBills();
            } else {
                showError("Failed to update bill status.");
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
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showInfo(String message) {
        statusLabel.setText("Info: " + message);
        statusLabel.setForeground(new Color(107, 123, 121));
    }

    private static class MouseAdapter extends java.awt.event.MouseAdapter {
        // Empty implementation
    }
}