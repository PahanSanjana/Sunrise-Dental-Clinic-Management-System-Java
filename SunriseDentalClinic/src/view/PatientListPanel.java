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
    private static final Color HOVER_SURFACE = new Color(0xE8F0F1);
    private static final Color ERROR_COLOR = new Color(220, 80, 80);
    private static final Color SUCCESS_COLOR = new Color(60, 160, 80);

    // Components
    private JTable patientTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private RoundedButton searchButton;
    private RoundedButton addButton;
    private RoundedButton refreshButton;
    private RoundedButton viewButton;
    private RoundedButton editButton;
    private RoundedButton deleteButton;
    private JLabel statusLabel;
    private JLabel countLabel;
    private JComboBox<String> filterCombo;
    
    private PatientListController controller;

    public PatientListPanel() {
        initComponents();
        this.controller = new PatientListController(this);
        loadPatients();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(SOFT_SURFACE);
        setBorder(new EmptyBorder(20, 30, 20, 30));

        // Title Panel - At the top
        add(createTitlePanel(), BorderLayout.NORTH);
        
        // Main Content Panel
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

        add(mainPanel, BorderLayout.CENTER);
    }

    /**
     * ✅ Title Panel - Separate from other content
     */
    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(SOFT_SURFACE);
        titlePanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JLabel titleLabel = new JLabel("Patient Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_DARK);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Manage all patient records in the system");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(107, 123, 121));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 2)));
        titlePanel.add(subtitleLabel);
        
        return titlePanel;
    }

    /**
     * ✅ Search Panel - Search and filter controls
     */
    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
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

        searchButton = createStyledButton("Search", PRIMARY_DARK, Color.WHITE);
        searchButton.setPreferredSize(new Dimension(100, 35));
        searchButton.addActionListener(e -> loadPatients());

        addButton = createStyledButton("Add Patient", PRIMARY_DARK, Color.WHITE);
        addButton.setPreferredSize(new Dimension(120, 35));
        addButton.addActionListener(e -> {
            Container parent = getParent();
            while (parent != null && !(parent instanceof MainFrame)) {
                parent = parent.getParent();
            }
            if (parent instanceof MainFrame) {
                ((MainFrame) parent).showCard("PATIENT_ADD");
            }
        });

        refreshButton = createStyledButton("Refresh", SOFT_SURFACE, PRIMARY_DARK);
        refreshButton.setBorderColor(LIGHT_SURFACE);
        refreshButton.setPreferredSize(new Dimension(100, 35));
        refreshButton.addActionListener(e -> loadPatients());

        searchPanel.add(new JLabel("Filter:"));
        searchPanel.add(filterCombo);
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

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightPanel.setOpaque(false);
        
        viewButton = createStyledButton("View", SOFT_SURFACE, PRIMARY_DARK);
        viewButton.setBorderColor(LIGHT_SURFACE);
        viewButton.setPreferredSize(new Dimension(80, 30));
        viewButton.addActionListener(e -> {
            int row = patientTable.getSelectedRow();
            if (row != -1) {
                viewPatient(row);
            } else {
                showError("Please select a patient to view.");
            }
        });

        editButton = createStyledButton("Edit", SOFT_SURFACE, PRIMARY_DARK);
        editButton.setBorderColor(LIGHT_SURFACE);
        editButton.setPreferredSize(new Dimension(80, 30));
        editButton.addActionListener(e -> {
            int row = patientTable.getSelectedRow();
            if (row != -1) {
                editPatient(row);
            } else {
                showError("Please select a patient to edit.");
            }
        });

        deleteButton = createStyledButton("Delete", SOFT_SURFACE, ERROR_COLOR);
        deleteButton.setBorderColor(LIGHT_SURFACE);
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

    private static class MouseAdapter extends java.awt.event.MouseAdapter {
        // Empty implementation
    }

    // ========================
    // Public methods for Controller
    // ========================

    public void loadPatients() {
        String searchText = searchField != null ? searchField.getText().trim() : "";
        String filter = filterCombo != null ? (String) filterCombo.getSelectedItem() : "All";
        
        if (controller != null) {
            controller.loadPatients(searchText, filter);
        }
    }

    public void displayPatients(List<Patient> patients) {
        if (tableModel == null) return;
        
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
    }

    public void viewPatient(int row) {
        int patientId = (int) tableModel.getValueAt(row, 0);
        
        Container parent = getParent();
        while (parent != null && !(parent instanceof MainFrame)) {
            parent = parent.getParent();
        }
        if (parent instanceof MainFrame) {
            MainFrame mainFrame = (MainFrame) parent;
            
            Patient patient = controller.getPatientById(patientId);
            if (patient != null) {
                PatientDetailsPanel detailsPanel = new PatientDetailsPanel(patient);
                detailsPanel.setName("PATIENT_DETAILS");
                mainFrame.addScreen("PATIENT_DETAILS", detailsPanel);
                mainFrame.showCard("PATIENT_DETAILS");
            } else {
                showError("Patient not found.");
            }
        }
    }

    public void editPatient(int row) {
        int patientId = (int) tableModel.getValueAt(row, 0);
        
        Container parent = getParent();
        while (parent != null && !(parent instanceof MainFrame)) {
            parent = parent.getParent();
        }
        if (parent instanceof MainFrame) {
            MainFrame mainFrame = (MainFrame) parent;
            
            Patient patient = controller.getPatientById(patientId);
            if (patient != null) {
                PatientDetailsPanel detailsPanel = new PatientDetailsPanel(patient);
                detailsPanel.setName("PATIENT_DETAILS");
                mainFrame.addScreen("PATIENT_DETAILS", detailsPanel);
                mainFrame.showCard("PATIENT_DETAILS");
                detailsPanel.toggleEditMode();
            } else {
                showError("Patient not found.");
            }
        }
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
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showInfo(String message) {
        statusLabel.setText("ℹ️ " + message);
        statusLabel.setForeground(new Color(107, 123, 121));
    }
}