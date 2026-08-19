package view;

import controller.BillController;
import model.Patient;
import model.Appointment;
import model.Treatment;
import model.Bill;
import model.BillItem;
import model.User;
import model.LoginSession;
import model.RolePermissions;
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
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class GenerateBillPanel extends JPanel {
    
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

    private static final String UI_FONT_FAMILY = "Segoe UI";

    // =====================================================
    // ICON HELPERS (Ikonli FontIcon)
    // =====================================================
    private static FontIcon icon(FontAwesomeSolid glyph, int size, Color color) {
        return FontIcon.of(glyph, size, color);
    }

    // Form Fields
    private JComboBox<Patient> patientCombo;
    private JComboBox<Appointment> appointmentCombo;
    private JComboBox<Treatment> treatmentCombo;
    private JTextField quantityField;
    private JTextField billNumberField;
    private JTextField billDateField;
    // REMOVED: dueDateField
    private JTextField subtotalField;
    private JTextField taxField;
    private JTextField discountField;
    private JTextField totalAmountField;
    private JTextField amountPaidField;
    private JTextField balanceField;
    private JComboBox<String> statusCombo;
    private JComboBox<String> paymentMethodCombo;
    private JTextArea notesArea;
    
    // Table for bill items
    private JTable billItemsTable;
    private DefaultTableModel tableModel;
    private List<BillItem> billItems;
    
    // Buttons
    private RoundedButton addItemButton;
    private RoundedButton removeItemButton;
    private RoundedButton generateButton;
    private RoundedButton clearButton;
    private RoundedButton cancelButton;
    private JButton refreshButton;
    
    private JLabel statusLabel;
    private BillController controller;
    private DecimalFormat df = new DecimalFormat("#.00");
    private boolean isUpdating = false;
    
    // Payment Section Panel (for visibility control)
    private JPanel paymentSectionPanel;
    private JPanel statusPanel;

    // ✅ Auto-refresh timer (hidden)
    private Timer refreshTimer;
    private static final int AUTO_REFRESH_DELAY = 30000; // 30 seconds

    public GenerateBillPanel() {
        this.controller = new BillController(this);
        this.billItems = new ArrayList<>();
        initComponents();
        loadData();
        generateBillNumber();
        setDefaultDates();
        startAutoRefresh();
        // Apply role-based restrictions
        applyRoleBasedRestrictions();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(SOFT_SURFACE);
        setBorder(new EmptyBorder(20, 30, 20, 30));

        // Header Panel
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Form Panel
        JPanel formPanel = createFormPanel();
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
        
        // Footer Panel
        add(createFooterPanel(), BorderLayout.SOUTH);
    }

    // =====================================================
    // ✅ AUTO-REFRESH (Hidden - No UI Indicator)
    // =====================================================
    
    private void startAutoRefresh() {
        if (refreshTimer == null) {
            refreshTimer = new Timer(AUTO_REFRESH_DELAY, e -> {
                if (isShowing()) {
                    loadData();
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

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(SOFT_SURFACE);
        header.setBorder(new EmptyBorder(0, 0, 15, 0));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Generate Bill");
        titleLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_DARK);
        
        JLabel subtitleLabel = new JLabel("Create a new bill for a patient");
        subtitleLabel.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(107, 123, 121));
        
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);

        header.add(titlePanel, BorderLayout.WEST);
        
        // ✅ Manual Refresh Button - ICON ONLY
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);
        
        refreshButton = createIconButton(FontAwesomeSolid.SYNC_ALT, COLOR_REFRESH);
        refreshButton.setPreferredSize(new Dimension(40, 40));
        refreshButton.setToolTipText("Refresh Data");
        refreshButton.addActionListener(e -> loadData());
        rightPanel.add(refreshButton);

        header.add(rightPanel, BorderLayout.EAST);

        return header;
    }

    private JPanel createFormPanel() {
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
        
        // Bill Items Section
        mainPanel.add(createSectionPanel("Bill Items", createBillItemsPanel()));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Payment Information Section
        paymentSectionPanel = createPaymentPanel();
        mainPanel.add(createSectionPanel("Payment Information", paymentSectionPanel));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Additional Information Section
        mainPanel.add(createSectionPanel("Additional Information", createNotesPanel()));

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
            new Font(UI_FONT_FAMILY, Font.BOLD, 14),
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

        // Row 0: Patient
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.15;
        JLabel patientLabel = new JLabel("Patient:");
        patientLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        patientLabel.setForeground(PRIMARY_DARK);
        panel.add(patientLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.35;
        patientCombo = new JComboBox<>();
        patientCombo.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        patientCombo.setPreferredSize(new Dimension(200, 35));
        patientCombo.addActionListener(e -> {
            if (!isUpdating) loadAppointments();
        });
        panel.add(patientCombo, gbc);

        // Row 0: Appointment
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.15;
        JLabel appointmentLabel = new JLabel("Appointment:");
        appointmentLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        appointmentLabel.setForeground(PRIMARY_DARK);
        panel.add(appointmentLabel, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.35;
        appointmentCombo = new JComboBox<>();
        appointmentCombo.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        appointmentCombo.setPreferredSize(new Dimension(200, 35));
        appointmentCombo.addActionListener(e -> {
            if (!isUpdating) loadTreatmentsFromAppointment();
        });
        panel.add(appointmentCombo, gbc);

        // Row 1: Bill Number
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.15;
        JLabel billNumberLabel = new JLabel("Bill Number:");
        billNumberLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        billNumberLabel.setForeground(PRIMARY_DARK);
        panel.add(billNumberLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.35;
        billNumberField = createTextField();
        billNumberField.setEditable(false);
        panel.add(billNumberField, gbc);

        // Row 1: Bill Date
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.15;
        JLabel billDateLabel = new JLabel("Bill Date:");
        billDateLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        billDateLabel.setForeground(PRIMARY_DARK);
        panel.add(billDateLabel, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.35;
        billDateField = createTextField();
        billDateField.setEditable(false);
        panel.add(billDateField, gbc);

        return panel;
    }

    private JPanel createBillItemsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Top: Add item controls
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setOpaque(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.weightx = 1.0;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.15;
        JLabel treatmentLabel = new JLabel("Treatment:");
        treatmentLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        treatmentLabel.setForeground(PRIMARY_DARK);
        topPanel.add(treatmentLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.45;
        treatmentCombo = new JComboBox<>();
        treatmentCombo.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        treatmentCombo.setPreferredSize(new Dimension(250, 35));
        treatmentCombo.addActionListener(e -> updateTreatmentDetails());
        topPanel.add(treatmentCombo, gbc);

        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.15;
        JLabel quantityLabel = new JLabel("Qty:");
        quantityLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        quantityLabel.setForeground(PRIMARY_DARK);
        topPanel.add(quantityLabel, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.1;
        quantityField = new JTextField("1");
        quantityField.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        quantityField.setHorizontalAlignment(JTextField.CENTER);
        quantityField.setPreferredSize(new Dimension(50, 35));
        quantityField.setBorder(BorderFactory.createLineBorder(LIGHT_SURFACE, 1));
        topPanel.add(quantityField, gbc);

        gbc.gridx = 4;
        gbc.gridwidth = 1;
        gbc.weightx = 0.15;
        addItemButton = createStyledButton("Add Item", PRIMARY_DARK, Color.WHITE);
        addItemButton.setPreferredSize(new Dimension(100, 35));
        addItemButton.addActionListener(e -> addBillItem());
        addItemButton.setIcon(icon(FontAwesomeSolid.PLUS, 14, Color.WHITE));
        addItemButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        addItemButton.setIconTextGap(6);
        topPanel.add(addItemButton, gbc);

        // Table
        String[] columns = {"Item", "Description", "Qty", "Unit Price", "Total"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        billItemsTable = new JTable(tableModel);
        billItemsTable.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        billItemsTable.setRowHeight(35);
        billItemsTable.setSelectionBackground(new Color(235, 245, 240));
        billItemsTable.setShowGrid(true);
        billItemsTable.setGridColor(LIGHT_SURFACE);

        // Custom header
        JTableHeader header = billItemsTable.getTableHeader();
        header.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 12));
        header.setBackground(MINT);
        header.setForeground(PRIMARY_DARK);

        // Set column widths
        billItemsTable.getColumnModel().getColumn(0).setMaxWidth(100);
        billItemsTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        billItemsTable.getColumnModel().getColumn(2).setMaxWidth(60);
        billItemsTable.getColumnModel().getColumn(3).setMaxWidth(100);
        billItemsTable.getColumnModel().getColumn(4).setMaxWidth(100);

        JScrollPane scrollPane = new JScrollPane(billItemsTable);
        scrollPane.setPreferredSize(new Dimension(600, 120));
        scrollPane.setBorder(BorderFactory.createLineBorder(LIGHT_SURFACE, 1));

        // Bottom: Remove button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        
        removeItemButton = createStyledButton("Remove Selected", SOFT_SURFACE, PRIMARY_DARK);
        removeItemButton.setBorderColor(LIGHT_SURFACE);
        removeItemButton.setPreferredSize(new Dimension(140, 30));
        removeItemButton.addActionListener(e -> removeBillItem());
        removeItemButton.setIcon(icon(FontAwesomeSolid.TRASH_ALT, 12, PRIMARY_DARK));
        removeItemButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        removeItemButton.setIconTextGap(6);
        bottomPanel.add(removeItemButton);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

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

        // Row 0: Subtotal
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.15;
        JLabel subtotalLabel = new JLabel("Subtotal (RS):");
        subtotalLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        subtotalLabel.setForeground(PRIMARY_DARK);
        panel.add(subtotalLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.1;
        subtotalField = createTextField();
        subtotalField.setEditable(false);
        subtotalField.setHorizontalAlignment(JTextField.RIGHT);
        panel.add(subtotalField, gbc);

        // Row 0: Tax
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.15;
        JLabel taxLabel = new JLabel("Tax (%):");
        taxLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        taxLabel.setForeground(PRIMARY_DARK);
        panel.add(taxLabel, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.1;
        taxField = createTextField();
        taxField.setText("10");
        taxField.setHorizontalAlignment(JTextField.RIGHT);
        taxField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { calculateTotal(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { calculateTotal(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { calculateTotal(); }
        });
        panel.add(taxField, gbc);

        // Row 1: Discount
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.15;
        JLabel discountLabel = new JLabel("Discount (RS):");
        discountLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        discountLabel.setForeground(PRIMARY_DARK);
        panel.add(discountLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.1;
        discountField = createTextField();
        discountField.setText("0");
        discountField.setHorizontalAlignment(JTextField.RIGHT);
        discountField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { calculateTotal(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { calculateTotal(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { calculateTotal(); }
        });
        panel.add(discountField, gbc);

        // Row 1: Total Amount
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.15;
        JLabel totalLabel = new JLabel("Total Amount (RS):");
        totalLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        totalLabel.setForeground(PRIMARY_DARK);
        panel.add(totalLabel, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.1;
        totalAmountField = createTextField();
        totalAmountField.setEditable(false);
        totalAmountField.setHorizontalAlignment(JTextField.RIGHT);
        totalAmountField.setForeground(SUCCESS_COLOR);
        totalAmountField.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 14));
        panel.add(totalAmountField, gbc);

        // Row 2: Amount Paid
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.15;
        JLabel paidLabel = new JLabel("Amount Paid (RS):");
        paidLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        paidLabel.setForeground(PRIMARY_DARK);
        panel.add(paidLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.1;
        amountPaidField = createTextField();
        amountPaidField.setText("0");
        amountPaidField.setHorizontalAlignment(JTextField.RIGHT);
        amountPaidField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { calculateBalance(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { calculateBalance(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { calculateBalance(); }
        });
        panel.add(amountPaidField, gbc);

        // Row 2: Balance (Refund)
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.15;
        JLabel balanceLabel = new JLabel("Balance (Refund) (RS):");
        balanceLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        balanceLabel.setForeground(PRIMARY_DARK);
        panel.add(balanceLabel, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.1;
        balanceField = createTextField();
        balanceField.setEditable(false);
        balanceField.setHorizontalAlignment(JTextField.RIGHT);
        balanceField.setForeground(SUCCESS_COLOR);
        balanceField.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 14));
        panel.add(balanceField, gbc);

        // Row 3: Status
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.15;
        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        statusLabel.setForeground(PRIMARY_DARK);
        panel.add(statusLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.35;
        statusCombo = new JComboBox<>(new String[]{"Pending", "Paid", "Partial", "Overdue"});
        statusCombo.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        statusCombo.setPreferredSize(new Dimension(150, 35));
        statusCombo.addActionListener(e -> updatePaymentFields());
        panel.add(statusCombo, gbc);

        // Row 3: Payment Method
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.15;
        JLabel methodLabel = new JLabel("Payment Method:");
        methodLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        methodLabel.setForeground(PRIMARY_DARK);
        panel.add(methodLabel, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.35;
        paymentMethodCombo = new JComboBox<>(new String[]{"Cash", "Credit Card", "Debit Card", "Insurance", "Bank Transfer", "Other"});
        paymentMethodCombo.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        paymentMethodCombo.setPreferredSize(new Dimension(150, 35));
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
        JLabel notesLabel = new JLabel("Notes:");
        notesLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        notesLabel.setForeground(PRIMARY_DARK);
        panel.add(notesLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 0.85;
        notesArea = createTextArea();
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
        statusLabel.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 12));
        statusLabel.setForeground(SECONDARY_TEXT);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        generateButton = createStyledButton("Generate Bill", PRIMARY_DARK, Color.WHITE);
        generateButton.setPreferredSize(new Dimension(160, 40));
        generateButton.addActionListener(e -> generateBill());
        generateButton.setIcon(icon(FontAwesomeSolid.FILE_INVOICE_DOLLAR, 14, Color.WHITE));
        generateButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        generateButton.setIconTextGap(8);

        clearButton = createStyledButton("Clear", SOFT_SURFACE, PRIMARY_DARK);
        clearButton.setBorderColor(LIGHT_SURFACE);
        clearButton.setPreferredSize(new Dimension(100, 40));
        clearButton.addActionListener(e -> clearForm());
        clearButton.setIcon(icon(FontAwesomeSolid.ERASER, 14, PRIMARY_DARK));
        clearButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        clearButton.setIconTextGap(8);

        cancelButton = createStyledButton("Cancel", SOFT_SURFACE, PRIMARY_DARK);
        cancelButton.setBorderColor(LIGHT_SURFACE);
        cancelButton.setPreferredSize(new Dimension(100, 40));
        cancelButton.addActionListener(e -> navigateBack());
        cancelButton.setIcon(icon(FontAwesomeSolid.TIMES, 14, PRIMARY_DARK));
        cancelButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        cancelButton.setIconTextGap(8);

        buttonPanel.add(clearButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(generateButton);

        footer.add(statusLabel, BorderLayout.WEST);
        footer.add(buttonPanel, BorderLayout.EAST);

        return footer;
    }

    // ========================
    // Helper Methods
    // ========================

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        field.setPreferredSize(new Dimension(150, 35));
        return field;
    }

    private JTextArea createTextArea() {
        JTextArea area = new JTextArea();
        area.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
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

    private static class MouseAdapter extends java.awt.event.MouseAdapter {
        // Empty implementation
    }

    // =====================================================
    // ROLE-BASED RESTRICTIONS
    // =====================================================

    /**
     * Apply role-based restrictions to the form
     */
    private void applyRoleBasedRestrictions() {
        User currentUser = LoginSession.getInstance().getCurrentUser();
        if (currentUser == null) {
            return;
        }

        // If user is a dentist, restrict payment section
        if (currentUser.isDentist()) {
            // Hide payment section
            if (paymentSectionPanel != null) {
                Component parent = paymentSectionPanel.getParent();
                if (parent instanceof JPanel) {
                    JPanel sectionPanel = (JPanel) parent;
                    // Find the parent TitledBorder panel and hide it
                    Container grandParent = sectionPanel.getParent();
                    if (grandParent instanceof JPanel) {
                        grandParent.setVisible(false);
                    }
                }
            }

            // Force status to Pending
            statusCombo.setSelectedItem("Pending");
            statusCombo.setEnabled(false);
            statusCombo.setVisible(false);
            
            // Disable payment method
            paymentMethodCombo.setEnabled(false);
            paymentMethodCombo.setVisible(false);
            
            // Disable amount paid and balance fields
            amountPaidField.setEnabled(false);
            amountPaidField.setVisible(false);
            balanceField.setEnabled(false);
            balanceField.setVisible(false);

            // Hide related labels for cleaner UI
            // Find parent panel and hide status and payment method rows
            Container paymentPanel = paymentSectionPanel;
            if (paymentPanel != null) {
                Component[] components = paymentPanel.getComponents();
                for (Component comp : components) {
                    if (comp instanceof JPanel) {
                        // Hide status and payment method panels
                    }
                }
            }

            // Set default values for dentist
            amountPaidField.setText("0");
            balanceField.setText("0");
            paymentMethodCombo.setSelectedItem("Cash");
            
            showInfo("Dentist mode: Bills will be generated with Pending status.");
        } else {
            // For ADMIN and RECEPTION - full access
            statusCombo.setEnabled(true);
            paymentMethodCombo.setEnabled(true);
            amountPaidField.setEnabled(true);
            amountPaidField.setVisible(true);
            balanceField.setEnabled(false);
            balanceField.setVisible(true);
        }
    }

    // ========================
    // Data Loading Methods
    // ========================

    private void loadData() {
        isUpdating = true;
        loadPatients();
        loadTreatments();
        isUpdating = false;
    }

    private void loadPatients() {
        List<Patient> patients = controller.getAllPatients();
        patientCombo.removeAllItems();
        if (patients != null && !patients.isEmpty()) {
            for (Patient patient : patients) {
                patientCombo.addItem(patient);
            }
        }
        if (patientCombo.getItemCount() > 0) {
            patientCombo.setSelectedIndex(0);
        }
    }

    private void loadAppointments() {
        Patient selected = (Patient) patientCombo.getSelectedItem();
        appointmentCombo.removeAllItems();
        if (selected != null) {
            List<Appointment> appointments = controller.getAppointmentsByPatient(selected.getPatientId());
            if (appointments != null && !appointments.isEmpty()) {
                for (Appointment appointment : appointments) {
                    appointmentCombo.addItem(appointment);
                }
            }
        }
        if (appointmentCombo.getItemCount() > 0) {
            appointmentCombo.setSelectedIndex(0);
        }
    }

    private void loadTreatments() {
        List<Treatment> treatments = controller.getAllTreatments();
        treatmentCombo.removeAllItems();
        if (treatments != null && !treatments.isEmpty()) {
            for (Treatment treatment : treatments) {
                treatmentCombo.addItem(treatment);
            }
        }
        if (treatmentCombo.getItemCount() > 0) {
            treatmentCombo.setSelectedIndex(0);
        }
    }

    private void loadTreatmentsFromAppointment() {
        if (treatmentCombo.getItemCount() > 0) {
            treatmentCombo.setSelectedIndex(0);
        }
    }

    private void updateTreatmentDetails() {
        Treatment selected = (Treatment) treatmentCombo.getSelectedItem();
        if (selected != null) {
            // You could update a preview field here
        }
    }

    private void generateBillNumber() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String billNumber = "BILL-" + dateStr + "-" + System.currentTimeMillis() % 10000;
        billNumberField.setText(billNumber);
    }

    private void setDefaultDates() {
        LocalDate today = LocalDate.now();
        billDateField.setText(today.toString());
    }

    // ========================
    // Bill Item Management
    // ========================

    private void addBillItem() {
        Treatment selected = (Treatment) treatmentCombo.getSelectedItem();
        if (selected == null) {
            showError("Please select a treatment.");
            return;
        }

        int quantity = 1;
        try {
            quantity = Integer.parseInt(quantityField.getText().trim());
            if (quantity <= 0) {
                showError("Quantity must be greater than 0.");
                return;
            }
        } catch (NumberFormatException e) {
            showError("Please enter a valid quantity.");
            return;
        }

        double totalPrice = selected.getCost() * quantity;
        
        BillItem item = new BillItem(
            0,
            selected.getTreatmentId(),
            selected.getTreatmentName(),
            quantity,
            selected.getCost(),
            totalPrice
        );
        
        billItems.add(item);
        updateBillItemsTable();
        calculateSubtotal();
        
        quantityField.setText("1");
        showSuccess("Item added successfully!");
    }

    private void removeBillItem() {
        int selectedRow = billItemsTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select an item to remove.");
            return;
        }
        
        billItems.remove(selectedRow);
        updateBillItemsTable();
        calculateSubtotal();
        showSuccess("Item removed successfully!");
    }

    private void updateBillItemsTable() {
        tableModel.setRowCount(0);
        for (BillItem item : billItems) {
            Object[] row = {
                item.getTreatmentId(),
                item.getDescription(),
                item.getQuantity(),
                "RS " + df.format(item.getUnitPrice()),
                "RS " + df.format(item.getTotalPrice())
            };
            tableModel.addRow(row);
        }
    }

    // ========================
    // Calculation Methods
    // ========================

    private void calculateSubtotal() {
        double subtotal = 0;
        for (BillItem item : billItems) {
            subtotal += item.getTotalPrice();
        }
        subtotalField.setText(df.format(subtotal));
        calculateTotal();
    }

    private void calculateTotal() {
        try {
            double subtotal = Double.parseDouble(subtotalField.getText().trim());
            double tax = 0;
            double discount = 0;
            
            try {
                tax = Double.parseDouble(taxField.getText().trim());
                discount = Double.parseDouble(discountField.getText().trim());
            } catch (NumberFormatException e) {
                // Use default values
            }
            
            double total = subtotal + (subtotal * tax / 100) - discount;
            if (total < 0) total = 0;
            
            totalAmountField.setText(df.format(total));
            calculateBalance();
        } catch (Exception e) {
            // Handle any calculation errors silently
        }
    }

    private void calculateBalance() {
        try {
            double total = 0;
            double paid = 0;
            
            try {
                total = Double.parseDouble(totalAmountField.getText().trim());
                paid = Double.parseDouble(amountPaidField.getText().trim());
            } catch (NumberFormatException e) {
                // Use default values
            }
            
            // NEW LOGIC: Balance = Paid - Total
            // Positive = Refund due to customer
            // Zero = Exact payment
            // Negative = Still owes
            double balance = paid - total;
            
            // Update color based on balance value
            if (balance < 0) {
                balanceField.setForeground(ERROR_COLOR);  // Red = still owes
            } else if (balance > 0) {
                balanceField.setForeground(SUCCESS_COLOR);  // Green = refund due
            } else {
                balanceField.setForeground(PRIMARY_DARK);  // Dark = exact
            }
            
            balanceField.setText(df.format(balance));
            
            // Auto-update status based on payment
            updateStatusBasedOnPayment(total, paid);
            
        } catch (Exception e) {
            // Handle any calculation errors silently
        }
    }

    private void updateStatusBasedOnPayment(double total, double paid) {
        if (paid >= total && paid > 0) {
            statusCombo.setSelectedItem("Paid");
            paymentMethodCombo.setEnabled(true);
        } else if (paid > 0 && paid < total) {
            statusCombo.setSelectedItem("Partial");
            paymentMethodCombo.setEnabled(true);
        } else if (paid == 0) {
            statusCombo.setSelectedItem("Pending");
            paymentMethodCombo.setEnabled(false);
        }
    }

    private void updatePaymentFields() {
        String status = (String) statusCombo.getSelectedItem();
        if ("Paid".equals(status)) {
            paymentMethodCombo.setEnabled(true);
        } else if ("Pending".equals(status)) {
            paymentMethodCombo.setEnabled(false);
        } else {
            paymentMethodCombo.setEnabled(true);
        }
        calculateBalance();
    }

    // ========================
    // Core Actions
    // ========================

    private void generateBill() {
        Patient patient = (Patient) patientCombo.getSelectedItem();
        if (patient == null) {
            showError("Please select a patient.");
            return;
        }

        if (billItems.isEmpty()) {
            showError("Please add at least one item to the bill.");
            return;
        }

        // Check if dentist is generating bill - force Pending status
        User currentUser = LoginSession.getInstance().getCurrentUser();
        boolean isDentist = currentUser != null && currentUser.isDentist();

        double subtotal = Double.parseDouble(subtotalField.getText().trim());
        double taxPercentage = Double.parseDouble(taxField.getText().trim());
        double discountAmount = Double.parseDouble(discountField.getText().trim());
        double total = Double.parseDouble(totalAmountField.getText().trim());
        
        // For dentists, force amount paid to 0 and status to Pending
        double paid = isDentist ? 0 : Double.parseDouble(amountPaidField.getText().trim());
        double balance = isDentist ? total : Double.parseDouble(balanceField.getText().trim());
        String status = isDentist ? "Pending" : (String) statusCombo.getSelectedItem();
        String paymentMethod = isDentist ? null : (String) paymentMethodCombo.getSelectedItem();

        Bill bill = new Bill(
            patient.getPatientId(),
            0,
            billNumberField.getText(),
            Date.valueOf(LocalDate.now()),
            null,  // No due date
            subtotal,
            taxPercentage,   // Store the percentage (e.g., 10)
            discountAmount,  // Store flat amount (e.g., 100)
            total,
            paid,
            balance,
            status,
            paymentMethod,
            notesArea.getText().trim()
        );

        showInfo("Generating bill... Please wait.");
        setCursor(new Cursor(Cursor.WAIT_CURSOR));

        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return controller.generateBill(bill, billItems);
            }

            @Override
            protected void done() {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                try {
                    boolean success = get();
                    if (success) {
                        showSuccess("Bill generated successfully! Bill Number: " + bill.getBillNumber());
                        clearForm();
                        
                        Timer timer = new Timer(1500, e -> navigateBack());
                        timer.setRepeats(false);
                        timer.start();
                    } else {
                        showError("Failed to generate bill. Please try again.");
                    }
                } catch (Exception e) {
                    showError("Error generating bill: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void clearForm() {
        if (patientCombo.getItemCount() > 0) patientCombo.setSelectedIndex(0);
        appointmentCombo.removeAllItems();
        billItems.clear();
        updateBillItemsTable();
        subtotalField.setText("0.00");
        taxField.setText("10");
        discountField.setText("0");
        totalAmountField.setText("0.00");
        amountPaidField.setText("0");
        balanceField.setText("0.00");
        statusCombo.setSelectedIndex(0);
        paymentMethodCombo.setSelectedIndex(0);
        notesArea.setText("");
        setDefaultDates();
        generateBillNumber();
        statusLabel.setText("Form cleared");
        statusLabel.setForeground(SECONDARY_TEXT);
        
        // Re-apply role-based restrictions after clearing
        applyRoleBasedRestrictions();
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
        statusLabel.setForeground(new Color(0, 120, 215));
    }
}