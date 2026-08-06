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

        header.add(titlePanel, BorderLayout.WEST);
        header.add(searchPanel, BorderLayout.EAST);

        return header;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(LIGHT_SURFACE, 1));

        // Create table model
        String[] columns = {"ID", "Patient Name", "Gender", "Contact", "Email", "Date of Birth", "Actions"};
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
        patientTable.getColumnModel().getColumn(6).setMaxWidth(180);
        patientTable.getColumnModel().getColumn(6).setMinWidth(150);

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

        // Action buttons for each row
        patientTable.getColumnModel().getColumn(6).setCellRenderer(new ActionButtonRenderer());
        patientTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int column = patientTable.getColumnModel().getColumnIndex("Actions");
                int row = patientTable.rowAtPoint(e.getPoint());
                int col = patientTable.columnAtPoint(e.getPoint());
                
                if (row >= 0 && col == column) {
                    int x = e.getX();
                    int cellWidth = patientTable.getCellRect(row, col, true).width;
                    int buttonWidth = cellWidth / 3;
                    
                    if (x < buttonWidth) {
                        viewPatient(row);
                    } else if (x < buttonWidth * 2) {
                        editPatient(row);
                    } else {
                        deletePatient(row);
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

    // Action Button Renderer for table
    private class ActionButtonRenderer extends javax.swing.table.DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            JPanel panel = new JPanel(new GridLayout(1, 3, 5, 5));
            panel.setOpaque(true);
            panel.setBackground(isSelected ? new Color(235, 245, 240) : Color.WHITE);
            panel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
            
            JButton viewBtn = new JButton("View");
            viewBtn.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            viewBtn.setBackground(MINT);
            viewBtn.setForeground(PRIMARY_DARK);
            viewBtn.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
            viewBtn.setFocusPainted(false);
            viewBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            JButton editBtn = new JButton("Edit");
            editBtn.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            editBtn.setBackground(new Color(200, 220, 240));
            editBtn.setForeground(PRIMARY_DARK);
            editBtn.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
            editBtn.setFocusPainted(false);
            editBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            JButton delBtn = new JButton("Delete");
            delBtn.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            delBtn.setBackground(new Color(240, 200, 200));
            delBtn.setForeground(ERROR_COLOR);
            delBtn.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
            delBtn.setFocusPainted(false);
            delBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            panel.add(viewBtn);
            panel.add(editBtn);
            panel.add(delBtn);
            
            return panel;
        }
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
                patient.getDateOfBirth() != null ? sdf.format(patient.getDateOfBirth()) : "N/A",
                "Actions"
            };
            tableModel.addRow(row);
        }

        statusLabel.setText("Loaded " + patients.size() + " patients");
        countLabel.setText("Total: " + patients.size() + " patients");
    }

    public void viewPatient(int row) {
        int patientId = (int) tableModel.getValueAt(row, 0);
        String patientName = (String) tableModel.getValueAt(row, 1);
        
        // TODO: Open patient details view
        // For now, show a message
        JOptionPane.showMessageDialog(this, 
            "Viewing Patient: " + patientName + "\nID: " + patientId,
            "Patient Details",
            JOptionPane.INFORMATION_MESSAGE);
    }

    public void editPatient(int row) {
        int patientId = (int) tableModel.getValueAt(row, 0);
        String patientName = (String) tableModel.getValueAt(row, 1);
        
        // TODO: Open patient edit form
        JOptionPane.showMessageDialog(this, 
            "Editing Patient: " + patientName + "\nID: " + patientId,
            "Edit Patient",
            JOptionPane.INFORMATION_MESSAGE);
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