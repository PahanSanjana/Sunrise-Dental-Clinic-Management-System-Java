package view;

import controller.ReportController;
import model.Bill;
import model.Patient;
import model.Dentist;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class RevenueReportPanel extends JPanel {
    
    // Color Palette
    private static final Color PRIMARY_DARK = new Color(0x2F3E3C);
    private static final Color MINT = new Color(0xBDDBD1);
    private static final Color SOFT_SURFACE = new Color(0xFBF9F1);
    private static final Color LIGHT_SURFACE = new Color(0xE7E9E3);
    private static final Color ERROR_COLOR = new Color(220, 80, 80);
    private static final Color SUCCESS_COLOR = new Color(60, 160, 80);
    private static final Color SECONDARY_TEXT = new Color(122, 138, 135);
    
    // Refresh button colors
    private static final Color COLOR_REFRESH = new Color(52, 152, 219);
    private static final Color COLOR_REFRESH_HOVER = new Color(41, 128, 185);
    
    // Revenue Colors
    private static final Color COLOR_TOTAL = new Color(46, 204, 113);
    private static final Color COLOR_PAID = new Color(52, 152, 219);
    private static final Color COLOR_PENDING = new Color(241, 196, 15);
    private static final Color COLOR_OVERDUE = new Color(231, 76, 60);
    private static final Color COLOR_PARTIAL = new Color(155, 89, 182);

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
    private JComboBox<String> periodCombo;
    private JComboBox<String> paymentMethodCombo;
    private RoundedButton generateButton;
    private JLabel statusLabel;
    private JLabel summaryLabel;
    private JLabel lastUpdatedLabel;
    
    // Summary Cards - Store references directly
    private JLabel totalRevenueLabel;
    private JLabel paidRevenueLabel;
    private JLabel pendingRevenueLabel;
    private JLabel overdueRevenueLabel;
    private JLabel totalBillsLabel;
    
    // Tables
    private JTable revenueTable;
    private DefaultTableModel tableModel;
    
    // Chart Panel (placeholder)
    private JPanel chartPanel;
    
    private ReportController controller;
    private DecimalFormat df = new DecimalFormat("#.00");
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    
    // Revenue calculation fields
    private double totalRevenue;
    private double paidRevenue;
    private double pendingRevenue;
    private double overdueRevenue;

    // Auto-refresh timer (hidden)
    private Timer refreshTimer;
    private static final int AUTO_REFRESH_DELAY = 30000; // 30 seconds

    public RevenueReportPanel() {
        this.controller = new ReportController(this);
        initComponents();
        loadData();
        startAutoRefresh();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(SOFT_SURFACE);
        setBorder(new EmptyBorder(20, 30, 20, 30));

        // Title Panel - At the top
        add(createTitlePanel(), BorderLayout.NORTH);
        
        // Main Content Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(SOFT_SURFACE);
        
        // Search/Filter Panel
        mainPanel.add(createFilterPanel());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Summary Cards
        mainPanel.add(createSummaryPanel());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Chart Panel (placeholder)
        mainPanel.add(createChartPanel());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Table
        mainPanel.add(createTablePanel());
        
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
        
        // Footer Panel
        add(createFooterPanel(), BorderLayout.SOUTH);
    }

    // =====================================================
    // AUTO-REFRESH (Hidden - No UI Indicator)
    // =====================================================

    private void startAutoRefresh() {
        if (refreshTimer == null) {
            refreshTimer = new Timer(AUTO_REFRESH_DELAY, e -> {
                if (isShowing()) {
                    generateReport();
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
        
        JLabel titleLabel = new JLabel("Revenue Report");
        titleLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_DARK);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("View revenue statistics and financial overview");
        subtitleLabel.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(107, 123, 121));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 2)));
        titlePanel.add(subtitleLabel);
        
        return titlePanel;
    }

    /**
     * ✅ Filter Panel - Search and filter controls
     */
    private JPanel createFilterPanel() {
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            new EmptyBorder(10, 15, 10, 15)
        ));

        // Period filter
        String[] periods = {"Today", "This Week", "This Month", "Last Month", "This Quarter", "This Year", "Custom Range"};
        periodCombo = new JComboBox<>(periods);
        periodCombo.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        periodCombo.setPreferredSize(new Dimension(130, 35));
        periodCombo.addActionListener(e -> {
            if ("Custom Range".equals(periodCombo.getSelectedItem())) {
                showCustomDateDialog();
            }
        });

        // Payment Method filter
        String[] methods = {"All Methods", "Cash", "Credit Card", "Debit Card", "Insurance", "Bank Transfer", "Other"};
        paymentMethodCombo = new JComboBox<>(methods);
        paymentMethodCombo.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        paymentMethodCombo.setPreferredSize(new Dimension(140, 35));

        generateButton = createStyledButton("Generate Report", PRIMARY_DARK, Color.WHITE);
        generateButton.setPreferredSize(new Dimension(150, 35));
        generateButton.addActionListener(e -> generateReport());
        generateButton.setIcon(icon(FontAwesomeSolid.FILE_INVOICE_DOLLAR, 14, Color.WHITE));
        generateButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        generateButton.setIconTextGap(8);

        filterPanel.add(new JLabel("Period:"));
        filterPanel.add(periodCombo);
        filterPanel.add(new JLabel("Payment Method:"));
        filterPanel.add(paymentMethodCombo);
        filterPanel.add(generateButton);
        
        // Manual Refresh Button - ICON ONLY
        JButton refreshBtn = createIconButton(FontAwesomeSolid.SYNC_ALT, COLOR_REFRESH);
        refreshBtn.setPreferredSize(new Dimension(40, 40));
        refreshBtn.setToolTipText("Refresh Now");
        refreshBtn.addActionListener(e -> generateReport());
        filterPanel.add(refreshBtn);

        return filterPanel;
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 5, 12, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            new EmptyBorder(15, 20, 15, 20)
        ));

        // Total Revenue
        JPanel totalPanel = createRevenueCard(FontAwesomeSolid.MONEY_BILL_WAVE, "Total Revenue", "RS0.00", COLOR_TOTAL);
        panel.add(totalPanel);
        totalRevenueLabel = findValueLabel(totalPanel);

        // Paid Revenue
        JPanel paidPanel = createRevenueCard(FontAwesomeSolid.CHECK_CIRCLE, "Paid", "RS0.00", COLOR_PAID);
        panel.add(paidPanel);
        paidRevenueLabel = findValueLabel(paidPanel);

        // Pending Revenue
        JPanel pendingPanel = createRevenueCard(FontAwesomeSolid.CLOCK, "Pending", "RS0.00", COLOR_PENDING);
        panel.add(pendingPanel);
        pendingRevenueLabel = findValueLabel(pendingPanel);

        // Overdue Revenue
        JPanel overduePanel = createRevenueCard(FontAwesomeSolid.EXCLAMATION_TRIANGLE, "Overdue", "RS0.00", COLOR_OVERDUE);
        panel.add(overduePanel);
        overdueRevenueLabel = findValueLabel(overduePanel);

        // Total Bills
        JPanel billsPanel = createRevenueCard(FontAwesomeSolid.FILE_INVOICE, "Total Bills", "0", new Color(149, 165, 166));
        panel.add(billsPanel);
        totalBillsLabel = findValueLabel(billsPanel);

        return panel;
    }

    private JPanel createRevenueCard(FontAwesomeSolid glyph, String title, String defaultValue, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(LIGHT_SURFACE, 1));
        card.setPreferredSize(new Dimension(140, 70));

        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(color);
        leftPanel.setPreferredSize(new Dimension(50, 70));
        leftPanel.setLayout(new GridBagLayout());
        
        JLabel iconLabel = iconLabel(glyph, 22, Color.WHITE);
        leftPanel.add(iconLabel);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(new EmptyBorder(5, 8, 5, 8));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 10));
        titleLabel.setForeground(SECONDARY_TEXT);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel valueLabel = new JLabel(defaultValue);
        valueLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 16));
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

    private JPanel createChartPanel() {
        chartPanel = new JPanel();
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(MINT, 1),
            "Revenue Distribution",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font(UI_FONT_FAMILY, Font.BOLD, 14),
            PRIMARY_DARK
        ));
        chartPanel.setPreferredSize(new Dimension(600, 150));
        chartPanel.setLayout(new GridBagLayout());
        
        JLabel chartPlaceholder = iconLabel(FontAwesomeSolid.CHART_BAR, 24, SECONDARY_TEXT);
        JLabel chartText = new JLabel(" Revenue chart visualization coming soon");
        chartText.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 16));
        chartText.setForeground(SECONDARY_TEXT);
        
        JPanel placeholderPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        placeholderPanel.setOpaque(false);
        placeholderPanel.add(chartPlaceholder);
        placeholderPanel.add(chartText);
        
        chartPanel.add(placeholderPanel);
        
        return chartPanel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(MINT, 1),
            "Revenue Details",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font(UI_FONT_FAMILY, Font.BOLD, 14),
            PRIMARY_DARK
        ));

        String[] columns = {"ID", "Bill Number", "Patient", "Date", "Total", "Paid", "Balance", "Status", "Method"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        revenueTable = new JTable(tableModel);
        revenueTable.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 12));
        revenueTable.setRowHeight(32);
        revenueTable.setSelectionBackground(new Color(235, 245, 240));
        revenueTable.setShowGrid(true);
        revenueTable.setGridColor(LIGHT_SURFACE);

        JTableHeader header = revenueTable.getTableHeader();
        header.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 12));
        header.setBackground(MINT);
        header.setForeground(PRIMARY_DARK);

        // Custom cell renderer for amount columns
        revenueTable.getColumnModel().getColumn(4).setCellRenderer(new AmountCellRenderer());
        revenueTable.getColumnModel().getColumn(5).setCellRenderer(new AmountCellRenderer());
        revenueTable.getColumnModel().getColumn(6).setCellRenderer(new AmountCellRenderer());

        // Custom cell renderer for status column
        revenueTable.getColumnModel().getColumn(7).setCellRenderer(new StatusCellRenderer());

        JScrollPane scrollPane = new JScrollPane(revenueTable);
        scrollPane.setPreferredSize(new Dimension(600, 200));
        scrollPane.setBorder(BorderFactory.createLineBorder(LIGHT_SURFACE, 1));

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

        summaryLabel = new JLabel("Total: 0 bills");
        summaryLabel.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 12));
        summaryLabel.setForeground(new Color(107, 123, 121));
        summaryLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        // Use a panel to hold both summary and last updated on the right
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);
        rightPanel.add(summaryLabel);
        rightPanel.add(lastUpdatedLabel);

        footer.add(statusLabel, BorderLayout.WEST);
        footer.add(rightPanel, BorderLayout.EAST);

        return footer;
    }

    private RoundedButton createStyledButton(String text, Color bg, Color fg) {
        RoundedButton button = new RoundedButton(text, bg, fg);
        button.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 12));
        return button;
    }

    // Custom cell renderer for amounts
    private class AmountCellRenderer extends javax.swing.table.DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            JLabel label = (JLabel) c;
            label.setHorizontalAlignment(SwingConstants.RIGHT);
            return c;
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
                label.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
                
                switch (status) {
                    case "Paid":
                        label.setBackground(COLOR_PAID);
                        label.setForeground(Color.WHITE);
                        break;
                    case "Pending":
                        label.setBackground(COLOR_PENDING);
                        label.setForeground(Color.BLACK);
                        break;
                    case "Partial":
                        label.setBackground(COLOR_PARTIAL);
                        label.setForeground(Color.WHITE);
                        break;
                    case "Overdue":
                        label.setBackground(COLOR_OVERDUE);
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

    private static class MouseAdapter extends java.awt.event.MouseAdapter {
        // Empty implementation
    }

    // ========================
    // Helper Methods
    // ========================

    private void showCustomDateDialog() {
        JOptionPane.showMessageDialog(this, 
            "Custom date range selection coming soon.\nUsing this month for now.",
            "Custom Range",
            JOptionPane.INFORMATION_MESSAGE);
        periodCombo.setSelectedItem("This Month");
    }

    private void loadData() {
        generateReport();
    }

    private void generateReport() {
        String period = (String) periodCombo.getSelectedItem();
        String paymentMethod = (String) paymentMethodCombo.getSelectedItem();

        statusLabel.setText("Generating revenue report...");
        setCursor(new Cursor(Cursor.WAIT_CURSOR));

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            private List<Bill> bills;

            @Override
            protected Void doInBackground() throws Exception {
                // Get bills based on period
                bills = controller.getBillsByPeriod(period);
                
                // Filter by payment method
                if (paymentMethod != null && !"All Methods".equals(paymentMethod) && bills != null) {
                    bills.removeIf(b -> b.getPaymentMethod() == null || !b.getPaymentMethod().equals(paymentMethod));
                }
                
                // Calculate revenue
                totalRevenue = 0;
                paidRevenue = 0;
                pendingRevenue = 0;
                overdueRevenue = 0;
                
                if (bills != null) {
                    for (Bill bill : bills) {
                        totalRevenue += bill.getTotalAmount();
                        
                        switch (bill.getStatus()) {
                            case "Paid":
                                paidRevenue += bill.getTotalAmount();
                                break;
                            case "Pending":
                                pendingRevenue += bill.getTotalAmount();
                                break;
                            case "Partial":
                                paidRevenue += bill.getAmountPaid();
                                pendingRevenue += bill.getBalance();
                                break;
                            case "Overdue":
                                overdueRevenue += bill.getTotalAmount();
                                break;
                        }
                    }
                }
                
                return null;
            }

            @Override
            protected void done() {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                try {
                    displayReport(bills);
                    statusLabel.setText("Revenue report generated successfully!");
                    statusLabel.setForeground(SUCCESS_COLOR);
                    
                    lastUpdatedLabel.setText("Last updated: " + 
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                } catch (Exception e) {
                    showError("Error generating revenue report: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void displayReport(List<Bill> bills) {
        tableModel.setRowCount(0);
        
        if (bills == null || bills.isEmpty()) {
            summaryLabel.setText("Total: 0 bills");
            totalRevenueLabel.setText("RS0.00");
            paidRevenueLabel.setText("RS0.00");
            pendingRevenueLabel.setText("RS0.00");
            overdueRevenueLabel.setText("RS0.00");
            totalBillsLabel.setText("0");
            return;
        }

        // Update summary
        int totalBills = bills.size();
        totalRevenueLabel.setText("RS" + df.format(totalRevenue));
        paidRevenueLabel.setText("RS" + df.format(paidRevenue));
        pendingRevenueLabel.setText("RS" + df.format(pendingRevenue));
        overdueRevenueLabel.setText("RS" + df.format(overdueRevenue));
        totalBillsLabel.setText(String.valueOf(totalBills));

        // Update table
        for (Bill bill : bills) {
            String patientName = controller.getPatientName(bill.getPatientId());
            
            Object[] row = {
                bill.getBillId(),
                bill.getBillNumber(),
                patientName,
                bill.getBillDate() != null ? sdf.format(bill.getBillDate()) : "N/A",
                "RS" + df.format(bill.getTotalAmount()),
                "RS" + df.format(bill.getAmountPaid()),
                "RS" + df.format(bill.getBalance()),
                bill.getStatus() != null ? bill.getStatus() : "N/A",
                bill.getPaymentMethod() != null ? bill.getPaymentMethod() : "N/A"
            };
            tableModel.addRow(row);
        }

        summaryLabel.setText("Total: " + totalBills + " bills | Total Revenue: RS" + df.format(totalRevenue));
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
}