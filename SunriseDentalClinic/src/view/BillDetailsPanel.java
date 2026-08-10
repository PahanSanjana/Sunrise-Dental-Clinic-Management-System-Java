package view;

import controller.BillController;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import model.Bill;
import model.BillItem;
import model.Patient;

public class BillDetailsPanel extends JPanel {
    
    // Color Palette
    private static final Color PRIMARY_DARK = new Color(0x2F3E3C);
    private static final Color MINT = new Color(0xBDDBD1);
    private static final Color SOFT_SURFACE = new Color(0xFBF9F1);
    private static final Color LIGHT_SURFACE = new Color(0xE7E9E3);
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

    // Form Fields - View/Edit Mode
    private JLabel billIdLabel;
    private JLabel billNumberLabel;
    private JLabel patientNameLabel;
    private JLabel patientPhoneLabel;
    private JLabel patientEmailLabel;
    private JLabel billDateLabel;
    private JLabel dueDateLabel;
    private JLabel subtotalLabel;
    private JLabel taxLabel;
    private JLabel discountLabel;
    private JLabel totalAmountLabel;
    private JLabel amountPaidLabel;
    private JLabel balanceLabel;
    private JComboBox<String> statusCombo;
    private JComboBox<String> paymentMethodCombo;
    private JTextArea notesArea;
    private JLabel createdDateLabel;
    private JLabel updatedDateLabel;
    private JLabel statusBadge;
    private JLabel statusLabel;
    
    // Table for bill items
    private JTable billItemsTable;
    private DefaultTableModel tableModel;
    
    // Buttons
    private RoundedButton editButton;
    private RoundedButton saveButton;
    private RoundedButton cancelButton;
    private RoundedButton backButton;
    private RoundedButton deleteButton;
    private RoundedButton markPaidButton;
    private RoundedButton printButton;
    private RoundedButton emailButton;
    
    private JPanel buttonPanel;
    private boolean isEditMode = false;
    private Bill currentBill;
    private List<BillItem> currentItems;
    private BillController controller;
    private DecimalFormat df = new DecimalFormat("#.00");

    public BillDetailsPanel() {
        this.controller = new BillController(this);
        initComponents();
        setViewMode(false);
        displayEmptyState();
    }

    public BillDetailsPanel(Bill bill, List<BillItem> items) {
        this.controller = new BillController(this);
        this.currentBill = bill;
        this.currentItems = items;
        initComponents();
        setViewMode(false);
        displayBill(bill, items);
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(SOFT_SURFACE);
        setBorder(new EmptyBorder(20, 30, 20, 30));

        // Header Panel
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Details Panel
        add(createDetailsPanel(), BorderLayout.CENTER);
        
        // Footer Panel
        add(createFooterPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(SOFT_SURFACE);
        header.setBorder(new EmptyBorder(0, 0, 15, 0));

        // Title and bill info
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Bill Details");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_DARK);
        
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        infoPanel.setOpaque(false);
        
        billIdLabel = new JLabel("Bill ID: --");
        billIdLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        billIdLabel.setForeground(SECONDARY_TEXT);
        
        billNumberLabel = new JLabel("Bill Number: --");
        billNumberLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        billNumberLabel.setForeground(SECONDARY_TEXT);
        
        createdDateLabel = new JLabel("Created: --");
        createdDateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        createdDateLabel.setForeground(SECONDARY_TEXT);
        
        updatedDateLabel = new JLabel("Last Updated: --");
        updatedDateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        updatedDateLabel.setForeground(SECONDARY_TEXT);
        
        // Status badge
        statusBadge = new JLabel("--");
        statusBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusBadge.setOpaque(true);
        statusBadge.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        statusBadge.setVisible(false);
        
        infoPanel.add(billIdLabel);
        infoPanel.add(billNumberLabel);
        infoPanel.add(createdDateLabel);
        infoPanel.add(updatedDateLabel);
        infoPanel.add(statusBadge);
        
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        titlePanel.add(infoPanel);

        header.add(titlePanel, BorderLayout.WEST);
        return header;
    }

    private JPanel createDetailsPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            new EmptyBorder(20, 30, 20, 30)
        ));

        // Bill Information Section
        mainPanel.add(createSectionPanel("Bill Information", createBillInfoPanel()));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Patient Information Section
        mainPanel.add(createSectionPanel("Patient Information", createPatientPanel()));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Bill Items Section
        mainPanel.add(createSectionPanel("Bill Items", createBillItemsPanel()));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Payment Information Section
        mainPanel.add(createSectionPanel("Payment Information", createPaymentPanel()));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Notes Section
        mainPanel.add(createSectionPanel("Notes", createNotesPanel()));

        return mainPanel;
    }

    private JPanel createSectionPanel(String title, JPanel content) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(MINT, 1),
            title,
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            PRIMARY_DARK
        ));
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createBillInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.weightx = 1.0;

        // Bill Date
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.15;
        JLabel dateLabel = new JLabel("Bill Date:");
        dateLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        dateLabel.setForeground(PRIMARY_DARK);
        panel.add(dateLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.35;
        billDateLabel = new JLabel("--");
        billDateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        billDateLabel.setForeground(SECONDARY_TEXT);
        panel.add(billDateLabel, gbc);

        // Due Date
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.15;
        JLabel dueLabel = new JLabel("Due Date:");
        dueLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        dueLabel.setForeground(PRIMARY_DARK);
        panel.add(dueLabel, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.35;
        dueDateLabel = new JLabel("--");
        dueDateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dueDateLabel.setForeground(SECONDARY_TEXT);
        panel.add(dueDateLabel, gbc);

        return panel;
    }

    private JPanel createPatientPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.weightx = 1.0;

        // Patient Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.15;
        JLabel nameLabel = new JLabel("Patient Name:");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nameLabel.setForeground(PRIMARY_DARK);
        panel.add(nameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.35;
        patientNameLabel = new JLabel("--");
        patientNameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        patientNameLabel.setForeground(SECONDARY_TEXT);
        panel.add(patientNameLabel, gbc);

        // Phone
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.15;
        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        phoneLabel.setForeground(PRIMARY_DARK);
        panel.add(phoneLabel, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.35;
        patientPhoneLabel = new JLabel("--");
        patientPhoneLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        patientPhoneLabel.setForeground(SECONDARY_TEXT);
        panel.add(patientPhoneLabel, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.15;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        emailLabel.setForeground(PRIMARY_DARK);
        panel.add(emailLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 0.85;
        patientEmailLabel = new JLabel("--");
        patientEmailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        patientEmailLabel.setForeground(SECONDARY_TEXT);
        panel.add(patientEmailLabel, gbc);

        return panel;
    }

    private JPanel createBillItemsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Create table model
        String[] columns = {"Item", "Description", "Qty", "Unit Price", "Total"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        billItemsTable = new JTable(tableModel);
        billItemsTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        billItemsTable.setRowHeight(35);
        billItemsTable.setSelectionBackground(new Color(235, 245, 240));
        billItemsTable.setShowGrid(true);
        billItemsTable.setGridColor(LIGHT_SURFACE);

        // Custom header
        JTableHeader header = billItemsTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(MINT);
        header.setForeground(PRIMARY_DARK);

        // Set column widths
        billItemsTable.getColumnModel().getColumn(0).setMaxWidth(100);
        billItemsTable.getColumnModel().getColumn(1).setPreferredWidth(250);
        billItemsTable.getColumnModel().getColumn(2).setMaxWidth(60);
        billItemsTable.getColumnModel().getColumn(3).setMaxWidth(100);
        billItemsTable.getColumnModel().getColumn(4).setMaxWidth(100);

        JScrollPane scrollPane = new JScrollPane(billItemsTable);
        scrollPane.setPreferredSize(new Dimension(600, 150));
        scrollPane.setBorder(BorderFactory.createLineBorder(LIGHT_SURFACE, 1));

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createPaymentPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.weightx = 1.0;

        // Subtotal
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.15;
        JLabel subtotalLabelText = new JLabel("Subtotal:");
        subtotalLabelText.setFont(new Font("Segoe UI", Font.BOLD, 13));
        subtotalLabelText.setForeground(PRIMARY_DARK);
        panel.add(subtotalLabelText, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.35;
        subtotalLabel = new JLabel("$0.00");
        subtotalLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtotalLabel.setForeground(SECONDARY_TEXT);
        panel.add(subtotalLabel, gbc);

        // Tax
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.15;
        JLabel taxLabelText = new JLabel("Tax:");
        taxLabelText.setFont(new Font("Segoe UI", Font.BOLD, 13));
        taxLabelText.setForeground(PRIMARY_DARK);
        panel.add(taxLabelText, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.35;
        taxLabel = new JLabel("$0.00");
        taxLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        taxLabel.setForeground(SECONDARY_TEXT);
        panel.add(taxLabel, gbc);

        // Discount
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.15;
        JLabel discountLabelText = new JLabel("Discount:");
        discountLabelText.setFont(new Font("Segoe UI", Font.BOLD, 13));
        discountLabelText.setForeground(PRIMARY_DARK);
        panel.add(discountLabelText, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.35;
        discountLabel = new JLabel("$0.00");
        discountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        discountLabel.setForeground(SECONDARY_TEXT);
        panel.add(discountLabel, gbc);

        // Total Amount
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.15;
        JLabel totalLabelText = new JLabel("Total Amount:");
        totalLabelText.setFont(new Font("Segoe UI", Font.BOLD, 13));
        totalLabelText.setForeground(PRIMARY_DARK);
        panel.add(totalLabelText, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.35;
        totalAmountLabel = new JLabel("$0.00");
        totalAmountLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        totalAmountLabel.setForeground(SUCCESS_COLOR);
        panel.add(totalAmountLabel, gbc);

        // Amount Paid
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.15;
        JLabel paidLabelText = new JLabel("Amount Paid:");
        paidLabelText.setFont(new Font("Segoe UI", Font.BOLD, 13));
        paidLabelText.setForeground(PRIMARY_DARK);
        panel.add(paidLabelText, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.35;
        amountPaidLabel = new JLabel("$0.00");
        amountPaidLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        amountPaidLabel.setForeground(SECONDARY_TEXT);
        panel.add(amountPaidLabel, gbc);

        // Balance
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.15;
        JLabel balanceLabelText = new JLabel("Balance:");
        balanceLabelText.setFont(new Font("Segoe UI", Font.BOLD, 13));
        balanceLabelText.setForeground(PRIMARY_DARK);
        panel.add(balanceLabelText, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.35;
        balanceLabel = new JLabel("$0.00");
        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        balanceLabel.setForeground(ERROR_COLOR);
        panel.add(balanceLabel, gbc);

        // Status (ComboBox for edit mode)
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.15;
        JLabel statusLabelText = new JLabel("Status:");
        statusLabelText.setFont(new Font("Segoe UI", Font.BOLD, 13));
        statusLabelText.setForeground(PRIMARY_DARK);
        panel.add(statusLabelText, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.35;
        statusCombo = new JComboBox<>(new String[]{"Pending", "Paid", "Partial", "Overdue", "Draft", "Cancelled"});
        statusCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusCombo.setPreferredSize(new Dimension(150, 35));
        statusCombo.setEnabled(false);
        panel.add(statusCombo, gbc);

        // Payment Method
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.15;
        JLabel methodLabelText = new JLabel("Payment Method:");
        methodLabelText.setFont(new Font("Segoe UI", Font.BOLD, 13));
        methodLabelText.setForeground(PRIMARY_DARK);
        panel.add(methodLabelText, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.35;
        paymentMethodCombo = new JComboBox<>(new String[]{"Cash", "Credit Card", "Debit Card", "Insurance", "Bank Transfer", "Other"});
        paymentMethodCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        paymentMethodCombo.setPreferredSize(new Dimension(150, 35));
        paymentMethodCombo.setEnabled(false);
        panel.add(paymentMethodCombo, gbc);

        return panel;
    }

    private JPanel createNotesPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.weightx = 1.0;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.15;
        JLabel notesLabelText = new JLabel("Notes:");
        notesLabelText.setFont(new Font("Segoe UI", Font.BOLD, 13));
        notesLabelText.setForeground(PRIMARY_DARK);
        panel.add(notesLabelText, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 0.85;
        notesArea = createTextArea();
        notesArea.setEnabled(false);
        JScrollPane notesScroll = new JScrollPane(notesArea);
        notesScroll.setPreferredSize(new Dimension(400, 60));
        panel.add(notesScroll, gbc);

        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(SOFT_SURFACE);
        footer.setBorder(new EmptyBorder(15, 0, 0, 0));

        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(SECONDARY_TEXT);

        buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        // Back button
        backButton = createStyledButton("← Back", SOFT_SURFACE, PRIMARY_DARK);
        backButton.setBorderColor(LIGHT_SURFACE);
        backButton.setPreferredSize(new Dimension(100, 35));
        backButton.addActionListener(e -> navigateBack());

        // Print button
        printButton = createStyledButton("Print", SOFT_SURFACE, PRIMARY_DARK);
        printButton.setBorderColor(LIGHT_SURFACE);
        printButton.setPreferredSize(new Dimension(100, 35));
        printButton.addActionListener(e -> printBill());

        // Email button
        emailButton = createStyledButton("Email", SOFT_SURFACE, PRIMARY_DARK);
        emailButton.setBorderColor(LIGHT_SURFACE);
        emailButton.setPreferredSize(new Dimension(100, 35));
        emailButton.addActionListener(e -> emailBill());

        // Mark as Paid button
        markPaidButton = createStyledButton("Mark as Paid", SUCCESS_COLOR, Color.WHITE);
        markPaidButton.setPreferredSize(new Dimension(120, 35));
        markPaidButton.addActionListener(e -> markAsPaid());

        // Edit button
        editButton = createStyledButton("Edit", PRIMARY_DARK, Color.WHITE);
        editButton.setPreferredSize(new Dimension(100, 35));
        editButton.addActionListener(e -> toggleEditMode());

        // Save button (hidden initially)
        saveButton = createStyledButton("Save", PRIMARY_DARK, Color.WHITE);
        saveButton.setPreferredSize(new Dimension(100, 35));
        saveButton.setVisible(false);
        saveButton.addActionListener(e -> saveBill());

        // Cancel button (hidden initially)
        cancelButton = createStyledButton("Cancel", SOFT_SURFACE, PRIMARY_DARK);
        cancelButton.setBorderColor(LIGHT_SURFACE);
        cancelButton.setPreferredSize(new Dimension(100, 35));
        cancelButton.setVisible(false);
        cancelButton.addActionListener(e -> cancelEdit());

        // Delete button
        deleteButton = createStyledButton("Delete", ERROR_COLOR, Color.WHITE);
        deleteButton.setPreferredSize(new Dimension(100, 35));
        deleteButton.addActionListener(e -> deleteBill());

        buttonPanel.add(backButton);
        buttonPanel.add(printButton);
        buttonPanel.add(emailButton);
        buttonPanel.add(markPaidButton);
        buttonPanel.add(editButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(deleteButton);

        footer.add(statusLabel, BorderLayout.WEST);
        footer.add(buttonPanel, BorderLayout.EAST);

        return footer;
    }

    // ========================
    // Helper Methods
    // ========================

    private JTextArea createTextArea() {
        JTextArea area = new JTextArea();
        area.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        area.setBackground(Color.WHITE);
        return area;
    }

    private RoundedButton createStyledButton(String text, Color bg, Color fg) {
        RoundedButton button = new RoundedButton(text, bg, fg);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return button;
    }

    private void updateStatusBadge(String status) {
        statusBadge.setVisible(true);
        statusBadge.setText(" " + status + " ");
        
        switch (status) {
            case "Pending":
                statusBadge.setBackground(COLOR_PENDING);
                statusBadge.setForeground(Color.BLACK);
                break;
            case "Paid":
                statusBadge.setBackground(COLOR_PAID);
                statusBadge.setForeground(Color.WHITE);
                break;
            case "Partial":
                statusBadge.setBackground(COLOR_PARTIAL);
                statusBadge.setForeground(Color.WHITE);
                break;
            case "Overdue":
                statusBadge.setBackground(COLOR_OVERDUE);
                statusBadge.setForeground(Color.WHITE);
                break;
            case "Draft":
                statusBadge.setBackground(COLOR_DRAFT);
                statusBadge.setForeground(Color.WHITE);
                break;
            case "Cancelled":
                statusBadge.setBackground(COLOR_CANCELLED);
                statusBadge.setForeground(Color.WHITE);
                break;
            default:
                statusBadge.setBackground(LIGHT_SURFACE);
                statusBadge.setForeground(PRIMARY_DARK);
                break;
        }
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

    private static class MouseAdapter extends java.awt.event.MouseAdapter {
        // Empty implementation
    }

    // ========================
    // Public methods
    // ========================

    public void displayBill(Bill bill, List<BillItem> items) {
        this.currentBill = bill;
        this.currentItems = items;
        
        if (bill == null) {
            displayEmptyState();
            return;
        }

        billIdLabel.setText("Bill ID: " + bill.getBillId());
        billNumberLabel.setText("Bill Number: " + bill.getBillNumber());
        
        // Load patient details
        Patient patient = controller.getPatientById(bill.getPatientId());
        if (patient != null) {
            patientNameLabel.setText(patient.getPatientName());
            patientPhoneLabel.setText(patient.getContactNumber() != null ? patient.getContactNumber() : "--");
            patientEmailLabel.setText(patient.getEmail() != null ? patient.getEmail() : "--");
        }
        
        // Set dates
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        billDateLabel.setText(bill.getBillDate() != null ? sdf.format(bill.getBillDate()) : "--");
        dueDateLabel.setText(bill.getDueDate() != null ? sdf.format(bill.getDueDate()) : "--");
        
        // Set payment info
        subtotalLabel.setText("$" + df.format(bill.getSubtotal()));
        taxLabel.setText("$" + df.format(bill.getTax()));
        discountLabel.setText("$" + df.format(bill.getDiscount()));
        totalAmountLabel.setText("$" + df.format(bill.getTotalAmount()));
        amountPaidLabel.setText("$" + df.format(bill.getAmountPaid()));
        balanceLabel.setText("$" + df.format(bill.getBalance()));
        
        // Set status and payment method
        statusCombo.setSelectedItem(bill.getStatus() != null ? bill.getStatus() : "Pending");
        paymentMethodCombo.setSelectedItem(bill.getPaymentMethod() != null ? bill.getPaymentMethod() : "Cash");
        
        // Set notes
        notesArea.setText(bill.getNotes() != null ? bill.getNotes() : "");
        
        // Set created and updated dates
        createdDateLabel.setText("Created: " + (bill.getCreatedAt() != null ? bill.getCreatedAt() : "--"));
        updatedDateLabel.setText("Last Updated: " + (bill.getUpdatedAt() != null ? bill.getUpdatedAt() : "--"));
        
        // Update status badge
        updateStatusBadge(bill.getStatus() != null ? bill.getStatus() : "Pending");
        
        // Display bill items
        displayBillItems(items);
        
        // Update mark as paid button
        updateMarkPaidButton();
        
        statusLabel.setText(" ");
        setViewMode(false);
    }

    private void displayBillItems(List<BillItem> items) {
        tableModel.setRowCount(0);
        
        if (items == null || items.isEmpty()) {
            return;
        }
        
        for (BillItem item : items) {
            Object[] row = {
                item.getTreatmentId() > 0 ? "Treatment" : "Other",
                item.getDescription(),
                item.getQuantity(),
                "$" + df.format(item.getUnitPrice()),
                "$" + df.format(item.getTotalPrice())
            };
            tableModel.addRow(row);
        }
    }

    private void displayEmptyState() {
        billIdLabel.setText("Bill ID: --");
        billNumberLabel.setText("Bill Number: --");
        patientNameLabel.setText("--");
        patientPhoneLabel.setText("--");
        patientEmailLabel.setText("--");
        billDateLabel.setText("--");
        dueDateLabel.setText("--");
        subtotalLabel.setText("$0.00");
        taxLabel.setText("$0.00");
        discountLabel.setText("$0.00");
        totalAmountLabel.setText("$0.00");
        amountPaidLabel.setText("$0.00");
        balanceLabel.setText("$0.00");
        statusCombo.setSelectedIndex(0);
        paymentMethodCombo.setSelectedIndex(0);
        notesArea.setText("");
        createdDateLabel.setText("Created: --");
        updatedDateLabel.setText("Last Updated: --");
        statusBadge.setVisible(false);
        statusLabel.setText("No bill selected");
        setViewMode(false);
        tableModel.setRowCount(0);
        updateMarkPaidButton();
    }

    private void updateMarkPaidButton() {
        if (currentBill != null && "Paid".equals(currentBill.getStatus())) {
            markPaidButton.setVisible(false);
        } else {
            markPaidButton.setVisible(true);
        }
    }

    private void setViewMode(boolean editMode) {
        this.isEditMode = editMode;
        
        // Enable/disable fields
        statusCombo.setEnabled(editMode);
        paymentMethodCombo.setEnabled(editMode);
        notesArea.setEnabled(editMode);

        // Show/hide buttons
        editButton.setVisible(!editMode);
        markPaidButton.setVisible(!editMode);
        printButton.setVisible(!editMode);
        emailButton.setVisible(!editMode);
        deleteButton.setVisible(!editMode);
        saveButton.setVisible(editMode);
        cancelButton.setVisible(editMode);

        if (editMode) {
            statusLabel.setText("Editing bill information...");
            statusLabel.setForeground(new Color(0, 120, 215));
        } else {
            statusLabel.setText(" ");
            statusLabel.setForeground(SECONDARY_TEXT);
        }
    }

    public void toggleEditMode() {
        if (currentBill == null) {
            showError("No bill loaded to edit.");
            return;
        }
        setViewMode(true);
    }

    private void cancelEdit() {
        if (currentBill != null) {
            displayBill(currentBill, currentItems);
        } else {
            displayEmptyState();
        }
        setViewMode(false);
        statusLabel.setText("Edit cancelled");
        statusLabel.setForeground(SECONDARY_TEXT);
    }

    private void saveBill() {
        if (currentBill == null) {
            showError("No bill loaded to save.");
            return;
        }

        // Update bill object
        currentBill.setStatus((String) statusCombo.getSelectedItem());
        currentBill.setPaymentMethod((String) paymentMethodCombo.getSelectedItem());
        currentBill.setNotes(notesArea.getText().trim());

        // Save to database
        statusLabel.setText("Saving bill...");
        statusLabel.setForeground(new Color(0, 120, 215));
        
        boolean success = controller.updateBill(currentBill);
        
        if (success) {
            statusLabel.setText("Bill updated successfully!");
            statusLabel.setForeground(SUCCESS_COLOR);
            setViewMode(false);
            displayBill(currentBill, currentItems);
            showSuccess("Bill updated successfully!");
        } else {
            statusLabel.setText("Failed to update bill.");
            statusLabel.setForeground(ERROR_COLOR);
            showError("Failed to update bill. Please try again.");
        }
    }

    private void markAsPaid() {
        if (currentBill == null) {
            showError("No bill loaded.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to mark this bill as paid?",
            "Mark as Paid",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = controller.updateBillStatus(currentBill.getBillId(), "Paid");
            
            if (success) {
                currentBill.setStatus("Paid");
                updateStatusBadge("Paid");
                statusCombo.setSelectedItem("Paid");
                updateMarkPaidButton();
                showSuccess("Bill marked as paid!");
                statusLabel.setText("Bill marked as paid");
                statusLabel.setForeground(SUCCESS_COLOR);
            } else {
                showError("Failed to mark bill as paid.");
            }
        }
    }

    private void printBill() {
        if (currentBill == null) {
            showError("No bill to print.");
            return;
        }
        // TODO: Implement print functionality
        showInfo("Print functionality coming soon...");
    }

    private void emailBill() {
        if (currentBill == null) {
            showError("No bill to email.");
            return;
        }
        // TODO: Implement email functionality
        showInfo("Email functionality coming soon...");
    }

    private void deleteBill() {
        if (currentBill == null) {
            showError("No bill loaded to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete bill: " + currentBill.getBillNumber() + "?\nThis action cannot be undone.",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = controller.deleteBill(currentBill.getBillId());
            
            if (success) {
                showSuccess("Bill deleted successfully!");
                navigateBack();
            } else {
                showError("Failed to delete bill. Please try again.");
            }
        }
    }

    private void navigateBack() {
        Container parent = getParent();
        while (parent != null && !(parent instanceof MainFrame)) {
            parent = parent.getParent();
        }
        if (parent instanceof MainFrame) {
            ((MainFrame) parent).showCard("BILL_LIST");
        }
    }

    // ========================
    // Public methods for Controller
    // ========================

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

    public void setBillItems(List<BillItem> items) {
        this.currentItems = items;
        displayBillItems(items);
    }
}