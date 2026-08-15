package view;

import controller.TreatmentController;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public class AddTreatmentPanel extends JPanel {
    
    // Color Palette
    private static final Color PRIMARY_DARK = new Color(0x2F3E3C);
    private static final Color MINT = new Color(0xBDDBD1);
    private static final Color SOFT_SURFACE = new Color(0xFBF9F1);
    private static final Color LIGHT_SURFACE = new Color(0xE7E9E3);
    private static final Color ERROR_COLOR = new Color(220, 80, 80);
    private static final Color SUCCESS_COLOR = new Color(60, 160, 80);
    private static final Color SECONDARY_TEXT = new Color(122, 138, 135);

    // Form Fields
    private JTextField treatmentNameField;
    private JTextArea descriptionArea;
    private JComboBox<String> categoryCombo;
    private JTextField costField;
    private JTextField durationField;
    private JCheckBox activeCheckBox;
    
    // Buttons
    private RoundedButton saveButton;
    private RoundedButton clearButton;
    private RoundedButton cancelButton;
    
    private JLabel statusLabel;
    private TreatmentController controller;

    public AddTreatmentPanel() {
        initComponents();
        this.controller = new TreatmentController(this);
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

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(SOFT_SURFACE);
        header.setBorder(new EmptyBorder(0, 0, 15, 0));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Add New Treatment");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_DARK);
        
        JLabel subtitleLabel = new JLabel("Register a new treatment in the system");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(107, 123, 121));
        
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);

        header.add(titlePanel, BorderLayout.WEST);
        return header;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            new EmptyBorder(20, 30, 20, 30)
        ));

        // Treatment Information Section
        formPanel.add(createSectionPanel("Treatment Information", createTreatmentInfoPanel()));
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Pricing & Duration Section
        formPanel.add(createSectionPanel("Pricing & Duration", createPricingPanel()));
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Status Section
        formPanel.add(createSectionPanel("Status", createStatusPanel()));

        return formPanel;
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

    private JPanel createTreatmentInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.weightx = 1.0;

        // Treatment Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel nameLabel = new JLabel("Treatment Name:");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nameLabel.setForeground(PRIMARY_DARK);
        panel.add(nameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 0.8;
        treatmentNameField = createTextField();
        treatmentNameField.setToolTipText("Name of the treatment");
        panel.add(treatmentNameField, gbc);

        // Category
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel categoryLabel = new JLabel("Category:");
        categoryLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        categoryLabel.setForeground(PRIMARY_DARK);
        panel.add(categoryLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 0.8;
        String[] categories = {"Preventive", "Restorative", "Endodontic", "Orthodontic", 
                               "Cosmetic", "Surgical", "Periodontic", "Other"};
        categoryCombo = new JComboBox<>(categories);
        categoryCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        categoryCombo.setPreferredSize(new Dimension(300, 35));
        panel.add(categoryCombo, gbc);

        // Description
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel descLabel = new JLabel("Description:");
        descLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        descLabel.setForeground(PRIMARY_DARK);
        panel.add(descLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.weightx = 0.8;
        descriptionArea = createTextArea();
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setPreferredSize(new Dimension(400, 80));
        panel.add(descScroll, gbc);

        return panel;
    }

    private JPanel createPricingPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.weightx = 1.0;

        // Cost
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel costLabel = new JLabel("Cost (RS):");
        costLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        costLabel.setForeground(PRIMARY_DARK);
        panel.add(costLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        costField = createTextField();
        costField.setToolTipText("Cost of the treatment in USD");
        panel.add(costField, gbc);

        // Duration
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel durationLabel = new JLabel("Duration (mins):");
        durationLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        durationLabel.setForeground(PRIMARY_DARK);
        panel.add(durationLabel, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        durationField = createTextField();
        durationField.setToolTipText("Duration in minutes");
        panel.add(durationField, gbc);

        return panel;
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.weightx = 1.0;

        // Status
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        statusLabel.setForeground(PRIMARY_DARK);
        panel.add(statusLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 0.8;
        activeCheckBox = new JCheckBox("Active");
        activeCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        activeCheckBox.setSelected(true);
        activeCheckBox.setToolTipText("Check if the treatment is currently active");
        panel.add(activeCheckBox, gbc);

        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(SOFT_SURFACE);
        footer.setBorder(new EmptyBorder(15, 0, 0, 0));

        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(SECONDARY_TEXT);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        saveButton = createStyledButton("Save Treatment", PRIMARY_DARK, Color.WHITE);
        saveButton.setPreferredSize(new Dimension(160, 40));

        clearButton = createStyledButton("Clear", SOFT_SURFACE, PRIMARY_DARK);
        clearButton.setBorderColor(LIGHT_SURFACE);
        clearButton.setPreferredSize(new Dimension(100, 40));

        cancelButton = createStyledButton("Cancel", SOFT_SURFACE, PRIMARY_DARK);
        cancelButton.setBorderColor(LIGHT_SURFACE);
        cancelButton.setPreferredSize(new Dimension(100, 40));

        buttonPanel.add(clearButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        footer.add(statusLabel, BorderLayout.WEST);
        footer.add(buttonPanel, BorderLayout.EAST);

        return footer;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        field.setPreferredSize(new Dimension(200, 35));
        return field;
    }

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

    public String getTreatmentName() { return treatmentNameField.getText().trim(); }
    public String getDescription() { return descriptionArea.getText().trim(); }
    public String getCategory() { return (String) categoryCombo.getSelectedItem(); }
    public String getCost() { return costField.getText().trim(); }
    public String getDuration() { return durationField.getText().trim(); }
    public boolean isActive() { return activeCheckBox.isSelected(); }

    public void clearForm() {
        treatmentNameField.setText("");
        descriptionArea.setText("");
        categoryCombo.setSelectedIndex(0);
        costField.setText("");
        durationField.setText("");
        activeCheckBox.setSelected(true);
        statusLabel.setText("Form cleared");
        statusLabel.setForeground(SECONDARY_TEXT);
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
        statusLabel.setForeground(new Color(0, 120, 215));
    }

    public void addSaveListener(ActionListener listener) {
        if (saveButton != null) {
            saveButton.addActionListener(listener);
        }
    }

    public void addClearListener(ActionListener listener) {
        if (clearButton != null) {
            clearButton.addActionListener(listener);
        }
    }

    public void addCancelListener(ActionListener listener) {
        if (cancelButton != null) {
            cancelButton.addActionListener(listener);
        }
    }
}