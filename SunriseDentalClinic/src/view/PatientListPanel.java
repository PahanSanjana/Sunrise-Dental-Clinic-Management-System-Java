package view;

import controller.PatientListController;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import model.Patient;

public class PatientListPanel extends JPanel {
    
    // Color Palette
    private static final Color PRIMARY_DARK = new Color(0x2F3E3C);
    private static final Color MINT = new Color(0xBDDBD1);
    private static final Color SOFT_SURFACE = new Color(0xFBF9F1);
    private static final Color LIGHT_SURFACE = new Color(0xE7E9E3);
    private static final Color ERROR_COLOR = new Color(220, 80, 80);
    private static final Color SUCCESS_COLOR = new Color(60, 160, 80);

    // Components
    private JTable patientTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton searchButton;
    private JButton addButton;
    private JButton refreshButton;
    private JButton viewButton;
    private JButton editButton;
    private JButton deleteButton;
    private JLabel statusLabel;
    private JLabel countLabel;
    private JComboBox<String> filterCombo;
    
    private PatientListController controller;

    public PatientListPanel() {
        System.out.println("PatientListPanel: Constructor called");
        initComponents();
        System.out.println("PatientListPanel: Components initialized");
        this.controller = new PatientListController(this);
        System.out.println("PatientListPanel: Controller created");
        loadPatients();
        System.out.println("PatientListPanel: Initial load completed");
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
        
        JLabel titleLabel = new JLabel("Patient Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_DARK);
        
        JLabel subtitleLabel = new JLabel("Manage all patient records in the system");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(107, 123, 121));
        
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setOpaque(false);

        // Filter dropdown
        filterCombo = new JComboBox<>(new String[]{"All", "Male", "Female", "Other"});
        filterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        filterCombo.setPreferredSize(new Dimension(100, 35));
        filterCombo.addActionListener(e -> loadPatients());

        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setPreferredSize(new Dimension(250, 35));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        searchField.addActionListener(e -> loadPatients());

        searchButton = new JButton("Search");
        searchButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        searchButton.setBackground(PRIMARY_DARK);
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchButton.setPreferredSize(new Dimension(100, 35));
        searchButton.addActionListener(e -> loadPatients());

        addButton = new JButton("Add Patient");
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addButton.setBackground(PRIMARY_DARK);
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addButton.setPreferredSize(new Dimension(120, 35));
        addButton.addActionListener(e -> {
            // Navigate to Add Patient
            Container parent = getParent();
            while (parent != null && !(parent instanceof MainFrame)) {
                parent = parent.getParent();
            }
            if (parent instanceof MainFrame) {
                ((MainFrame) parent).showCard("PATIENT_ADD");
            }
        });

        refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        refreshButton.setBackground(SOFT_SURFACE);
        refreshButton.setForeground(PRIMARY_DARK);
        refreshButton.setFocusPainted(false);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.setPreferredSize(new Dimension(100, 35));
        refreshButton.addActionListener(e -> loadPatients());

        searchPanel.add(new JLabel("Filter:"));
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
        String[] columns = {"ID", "Patient Name", "Gender", "Contact", "Email", "Date of Birth"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        patientTable = new JTable(tableModel);
        patientTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        patientTable.setRowHeight(40);
        patientTable.setSelectionBackground(new Color(235, 245, 240));
        patientTable.setSelectionForeground(PRIMARY_DARK);
        patientTable.setShowGrid(true);
        patientTable.setGridColor(LIGHT_SURFACE);
        patientTable.setIntercellSpacing(new Dimension(5, 5));

        // Set column widths
        patientTable.getColumnModel().getColumn(0).setMaxWidth(60);
        patientTable.getColumnModel().getColumn(0).setMinWidth(50);
        patientTable.getColumnModel().getColumn(2).setMaxWidth(100);
        patientTable.getColumnModel().getColumn(3).setMaxWidth(150);

        // Custom header
        JTableHeader header = patientTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(MINT);
        header.setForeground(PRIMARY_DARK);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_DARK));

        // Add mouse listener for double click to view
        patientTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = patientTable.getSelectedRow();
                    if (row != -1) {
                        viewPatient(row);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(patientTable);
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

        countLabel = new JLabel("Total: 0 patients");
        countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        countLabel.setForeground(new Color(107, 123, 121));

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        
        // Add action buttons
        viewButton = new JButton("View");
        viewButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        viewButton.setBackground(SOFT_SURFACE);
        viewButton.setForeground(PRIMARY_DARK);
        viewButton.setFocusPainted(false);
        viewButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        viewButton.setPreferredSize(new Dimension(80, 30));
        viewButton.addActionListener(e -> {
            int row = patientTable.getSelectedRow();
            if (row != -1) {
                viewPatient(row);
            } else {
                showError("Please select a patient to view.");
            }
        });

        editButton = new JButton("Edit");
        editButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        editButton.setBackground(SOFT_SURFACE);
        editButton.setForeground(PRIMARY_DARK);
        editButton.setFocusPainted(false);
        editButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        editButton.setPreferredSize(new Dimension(80, 30));
        editButton.addActionListener(e -> {
            int row = patientTable.getSelectedRow();
            if (row != -1) {
                editPatient(row);
            } else {
                showError("Please select a patient to edit.");
            }
        });

        deleteButton = new JButton("Delete");
        deleteButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        deleteButton.setBackground(ERROR_COLOR);
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);
        deleteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        deleteButton.setPreferredSize(new Dimension(80, 30));
        deleteButton.addActionListener(e -> {
            int row = patientTable.getSelectedRow();
            if (row != -1) {
                deletePatient(row);
            } else {
                showError("Please select a patient to delete.");
            }
        });

        rightPanel.add(viewButton);
        rightPanel.add(editButton);
        rightPanel.add(deleteButton);

        footer.add(statusLabel, BorderLayout.WEST);
        footer.add(countLabel, BorderLayout.CENTER);
        footer.add(rightPanel, BorderLayout.EAST);

        return footer;
    }

    // ========================
    // Public methods for Controller
    // ========================

    public void loadPatients() {
        System.out.println("PatientListPanel: loadPatients called");
        String searchText = searchField != null ? searchField.getText().trim() : "";
        String filter = filterCombo != null ? (String) filterCombo.getSelectedItem() : "All";
        
        if (controller != null) {
            controller.loadPatients(searchText, filter);
        } else {
            System.out.println("PatientListPanel: Controller is null!");
        }
    }

    public void displayPatients(List<Patient> patients) {
        System.out.println("PatientListPanel: displayPatients called with " + (patients != null ? patients.size() : 0) + " patients");
        
        if (tableModel == null) {
            System.out.println("PatientListPanel: tableModel is null!");
            return;
        }
        
        tableModel.setRowCount(0);
        
        if (patients == null || patients.isEmpty()) {
            statusLabel.setText("No patients found");
            countLabel.setText("Total: 0 patients");
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        for (Patient patient : patients) {
            Object[] row = {
                patient.getPatientId(),
                patient.getPatientName(),
                patient.getGender() != null ? patient.getGender() : "N/A",
                patient.getContactNumber(),
                patient.getEmail() != null && !patient.getEmail().isEmpty() ? patient.getEmail() : "N/A",
                patient.getDateOfBirth() != null ? sdf.format(patient.getDateOfBirth()) : "N/A"
            };
            tableModel.addRow(row);
        }

        statusLabel.setText("Loaded " + patients.size() + " patients");
        countLabel.setText("Total: " + patients.size() + " patients");
        System.out.println("PatientListPanel: Display completed");
    }

    public void viewPatient(int row) {
        int patientId = (int) tableModel.getValueAt(row, 0);
        showSuccess("Viewing patient ID: " + patientId);
    }

    public void editPatient(int row) {
        int patientId = (int) tableModel.getValueAt(row, 0);
        showSuccess("Editing patient ID: " + patientId);
    }

    public void deletePatient(int row) {
        int patientId = (int) tableModel.getValueAt(row, 0);
        String patientName = (String) tableModel.getValueAt(row, 1);
        
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete patient: " + patientName + "?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (controller != null) {
                controller.deletePatient(patientId);
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
    }

    public void showInfo(String message) {
        statusLabel.setText("ℹ️ " + message);
        statusLabel.setForeground(new Color(107, 123, 121));
    }

    private static class MouseAdapter extends java.awt.event.MouseAdapter {
        // Empty implementation
    }
}