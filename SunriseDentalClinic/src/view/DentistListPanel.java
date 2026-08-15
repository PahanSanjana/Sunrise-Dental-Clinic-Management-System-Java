package view;

import controller.DentistController;
import model.Dentist;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.List;

public class DentistListPanel extends JPanel {
    
    // Color Palette
    private static final Color PRIMARY_DARK = new Color(0x2F3E3C);
    private static final Color MINT = new Color(0xBDDBD1);
    private static final Color SOFT_SURFACE = new Color(0xFBF9F1);
    private static final Color LIGHT_SURFACE = new Color(0xE7E9E3);
    private static final Color HOVER_SURFACE = new Color(0xE8F0F1);
    private static final Color ERROR_COLOR = new Color(220, 80, 80);
    private static final Color SUCCESS_COLOR = new Color(60, 160, 80);
    private static final Color AVAILABLE_COLOR = new Color(60, 160, 80);
    private static final Color UNAVAILABLE_COLOR = new Color(200, 80, 80);

    // Components
    private JTable dentistTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private RoundedButton searchButton;
    private RoundedButton addButton;
    private RoundedButton refreshButton;
    private RoundedButton viewButton;
    private RoundedButton editButton;
    private RoundedButton deleteButton;
    private RoundedButton toggleAvailabilityButton;
    private JLabel statusLabel;
    private JLabel countLabel;
    private JComboBox<String> filterCombo;
    
    private DentistController controller;

    public DentistListPanel() {
        this.controller = new DentistController(this);
        initComponents();
        loadDentists();
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
        
        JLabel titleLabel = new JLabel("Dentist Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_DARK);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Manage all dentists in the system");
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
        filterCombo = new JComboBox<>(new String[]{"All", "Available", "Unavailable"});
        filterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        filterCombo.setPreferredSize(new Dimension(120, 35));
        filterCombo.addActionListener(e -> loadDentists());

        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setPreferredSize(new Dimension(250, 35));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        searchField.addActionListener(e -> loadDentists());

        searchButton = createStyledButton("Search", PRIMARY_DARK, Color.WHITE);
        searchButton.setPreferredSize(new Dimension(100, 35));
        searchButton.addActionListener(e -> loadDentists());

        addButton = createStyledButton("Add Dentist", PRIMARY_DARK, Color.WHITE);
        addButton.setPreferredSize(new Dimension(120, 35));
        addButton.addActionListener(e -> {
            Container parent = getParent();
            while (parent != null && !(parent instanceof MainFrame)) {
                parent = parent.getParent();
            }
            if (parent instanceof MainFrame) {
                ((MainFrame) parent).showCard("DENTIST_ADD");
            }
        });

        refreshButton = createStyledButton("Refresh", SOFT_SURFACE, PRIMARY_DARK);
        refreshButton.setBorderColor(LIGHT_SURFACE);
        refreshButton.setPreferredSize(new Dimension(100, 35));
        refreshButton.addActionListener(e -> loadDentists());

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
        String[] columns = {"ID", "Dentist Name", "Specialization", "Phone", "Email", "Experience", "Fee", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        dentistTable = new JTable(tableModel);
        dentistTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dentistTable.setRowHeight(40);
        dentistTable.setSelectionBackground(new Color(235, 245, 240));
        dentistTable.setSelectionForeground(PRIMARY_DARK);
        dentistTable.setShowGrid(true);
        dentistTable.setGridColor(LIGHT_SURFACE);
        dentistTable.setIntercellSpacing(new Dimension(5, 5));

        // Set column widths
        dentistTable.getColumnModel().getColumn(0).setMaxWidth(60);
        dentistTable.getColumnModel().getColumn(0).setMinWidth(50);
        dentistTable.getColumnModel().getColumn(3).setMaxWidth(150);
        dentistTable.getColumnModel().getColumn(4).setMaxWidth(150);
        dentistTable.getColumnModel().getColumn(5).setMaxWidth(80);
        dentistTable.getColumnModel().getColumn(6).setMaxWidth(80);
        dentistTable.getColumnModel().getColumn(7).setMaxWidth(100);

        // Custom header
        JTableHeader header = dentistTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(MINT);
        header.setForeground(PRIMARY_DARK);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_DARK));

        // Custom cell renderer for status column
        dentistTable.getColumnModel().getColumn(7).setCellRenderer(new StatusCellRenderer());

        // Add mouse listener for double click to view
        dentistTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = dentistTable.getSelectedRow();
                    if (row != -1) {
                        viewDentist(row);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(dentistTable);
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

        countLabel = new JLabel("Total: 0 dentists");
        countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        countLabel.setForeground(new Color(107, 123, 121));

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightPanel.setOpaque(false);
        
        viewButton = createStyledButton("View", SOFT_SURFACE, PRIMARY_DARK);
        viewButton.setBorderColor(LIGHT_SURFACE);
        viewButton.setPreferredSize(new Dimension(80, 30));
        viewButton.addActionListener(e -> {
            int row = dentistTable.getSelectedRow();
            if (row != -1) {
                viewDentist(row);
            } else {
                showError("Please select a dentist to view.");
            }
        });

        editButton = createStyledButton("Edit", SOFT_SURFACE, PRIMARY_DARK);
        editButton.setBorderColor(LIGHT_SURFACE);
        editButton.setPreferredSize(new Dimension(80, 30));
        editButton.addActionListener(e -> {
            int row = dentistTable.getSelectedRow();
            if (row != -1) {
                editDentist(row);
            } else {
                showError("Please select a dentist to edit.");
            }
        });

        toggleAvailabilityButton = createStyledButton("Toggle Status", SOFT_SURFACE, PRIMARY_DARK);
        toggleAvailabilityButton.setBorderColor(LIGHT_SURFACE);
        toggleAvailabilityButton.setPreferredSize(new Dimension(120, 30));
        toggleAvailabilityButton.addActionListener(e -> {
            int row = dentistTable.getSelectedRow();
            if (row != -1) {
                toggleAvailability(row);
            } else {
                showError("Please select a dentist to toggle availability.");
            }
        });

        deleteButton = createStyledButton("Delete", SOFT_SURFACE, ERROR_COLOR);
        deleteButton.setBorderColor(LIGHT_SURFACE);
        deleteButton.setPreferredSize(new Dimension(80, 30));
        deleteButton.addActionListener(e -> {
            int row = dentistTable.getSelectedRow();
            if (row != -1) {
                deleteDentist(row);
            } else {
                showError("Please select a dentist to delete.");
            }
        });

        rightPanel.add(viewButton);
        rightPanel.add(editButton);
        rightPanel.add(toggleAvailabilityButton);
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
                
                if (status.equals("Available")) {
                    label.setBackground(AVAILABLE_COLOR);
                    label.setForeground(Color.WHITE);
                } else if (status.equals("Unavailable")) {
                    label.setBackground(UNAVAILABLE_COLOR);
                    label.setForeground(Color.WHITE);
                } else {
                    label.setBackground(LIGHT_SURFACE);
                    label.setForeground(PRIMARY_DARK);
                }
            }
            return c;
        }
    }

    private static class MouseAdapter extends java.awt.event.MouseAdapter {
        // Empty implementation
    }

    // ========================
    // Public methods
    // ========================

    public void loadDentists() {
        String searchText = searchField != null ? searchField.getText().trim() : "";
        String filter = filterCombo != null ? (String) filterCombo.getSelectedItem() : "All";
        
        SwingWorker<List<Dentist>, Void> worker = new SwingWorker<List<Dentist>, Void>() {
            @Override
            protected List<Dentist> doInBackground() throws Exception {
                List<Dentist> dentists;
                
                if (searchText != null && !searchText.isEmpty()) {
                    dentists = controller.searchDentists(searchText);
                } else {
                    dentists = controller.getAllDentists();
                }
                
                if (filter != null && dentists != null) {
                    if ("Available".equals(filter)) {
                        dentists.removeIf(d -> !d.isAvailable());
                    } else if ("Unavailable".equals(filter)) {
                        dentists.removeIf(d -> d.isAvailable());
                    }
                }
                
                return dentists;
            }

            @Override
            protected void done() {
                try {
                    List<Dentist> dentists = get();
                    displayDentists(dentists);
                } catch (Exception e) {
                    showError("Error loading dentists: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    public void displayDentists(List<Dentist> dentists) {
        if (tableModel == null) return;
        
        tableModel.setRowCount(0);
        
        if (dentists == null || dentists.isEmpty()) {
            statusLabel.setText("No dentists found");
            countLabel.setText("Total: 0 dentists");
            return;
        }

        DecimalFormat df = new DecimalFormat("#.00");
        
        for (Dentist dentist : dentists) {
            String status = dentist.isAvailable() ? "Available" : "Unavailable";
            Object[] row = {
                dentist.getDentistId(),
                dentist.getDentistName(),
                dentist.getSpecialization() != null ? dentist.getSpecialization() : "N/A",
                dentist.getPhone() != null ? dentist.getPhone() : "N/A",
                dentist.getEmail() != null ? dentist.getEmail() : "N/A",
                dentist.getYearsOfExperience() + " yrs",
                "RS" + df.format(dentist.getConsultationFee()),
                status
            };
            tableModel.addRow(row);
        }

        statusLabel.setText("Loaded " + dentists.size() + " dentists");
        countLabel.setText("Total: " + dentists.size() + " dentists");
    }

    public void viewDentist(int row) {
        int dentistId = (int) tableModel.getValueAt(row, 0);
        
        Container parent = getParent();
        while (parent != null && !(parent instanceof MainFrame)) {
            parent = parent.getParent();
        }
        if (parent instanceof MainFrame) {
            MainFrame mainFrame = (MainFrame) parent;
            
            Dentist dentist = controller.getDentistById(dentistId);
            if (dentist != null) {
                DentistDetailsPanel detailsPanel = new DentistDetailsPanel(dentist);
                detailsPanel.setName("DENTIST_DETAILS");
                mainFrame.addScreen("DENTIST_DETAILS", detailsPanel);
                mainFrame.showCard("DENTIST_DETAILS");
            } else {
                showError("Dentist not found.");
            }
        }
    }

    public void editDentist(int row) {
        int dentistId = (int) tableModel.getValueAt(row, 0);
        
        Container parent = getParent();
        while (parent != null && !(parent instanceof MainFrame)) {
            parent = parent.getParent();
        }
        if (parent instanceof MainFrame) {
            MainFrame mainFrame = (MainFrame) parent;
            
            Dentist dentist = controller.getDentistById(dentistId);
            if (dentist != null) {
                DentistDetailsPanel detailsPanel = new DentistDetailsPanel(dentist);
                detailsPanel.setName("DENTIST_DETAILS");
                mainFrame.addScreen("DENTIST_DETAILS", detailsPanel);
                mainFrame.showCard("DENTIST_DETAILS");
                detailsPanel.toggleEditMode();
            } else {
                showError("Dentist not found.");
            }
        }
    }

    public void toggleAvailability(int row) {
        int dentistId = (int) tableModel.getValueAt(row, 0);
        String dentistName = (String) tableModel.getValueAt(row, 1);
        String currentStatus = (String) tableModel.getValueAt(row, 7);
        boolean isAvailable = "Available".equals(currentStatus);
        String newStatus = isAvailable ? "Unavailable" : "Available";
        
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to change " + dentistName + "'s status to " + newStatus + "?",
            "Toggle Availability",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = controller.updateAvailability(dentistId, !isAvailable);
            if (success) {
                showSuccess("Dentist status updated to " + newStatus + "!");
                loadDentists();
            } else {
                showError("Failed to update dentist status.");
            }
        }
    }

    public void deleteDentist(int row) {
        int dentistId = (int) tableModel.getValueAt(row, 0);
        String dentistName = (String) tableModel.getValueAt(row, 1);
        
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete dentist: " + dentistName + "?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = controller.deleteDentist(dentistId);
            if (success) {
                showSuccess("Dentist deleted successfully!");
                loadDentists();
            } else {
                showError("Failed to delete dentist.");
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