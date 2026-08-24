package view;

import controller.TreatmentController;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import java.awt.*;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import model.Treatment;
import model.LoginSession;
import model.User;
import model.User.UserRole;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TreatmentDetailsPanel extends JPanel {
    
    // Color Palette
    private static final Color PRIMARY_DARK = new Color(0x2F3E3C);
    private static final Color MINT = new Color(0xBDDBD1);
    private static final Color SOFT_SURFACE = new Color(0xFBF9F1);
    private static final Color LIGHT_SURFACE = new Color(0xE7E9E3);
    private static final Color ERROR_COLOR = new Color(220, 80, 80);
    private static final Color SUCCESS_COLOR = new Color(60, 160, 80);
    private static final Color SECONDARY_TEXT = new Color(122, 138, 135);
    private static final Color ACTIVE_COLOR = new Color(60, 160, 80);
    private static final Color INACTIVE_COLOR = new Color(200, 80, 80);
    
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

    private static JLabel iconLabel(FontAwesomeSolid glyph, int size, Color color) {
        return new JLabel(icon(glyph, size, color));
    }

    // Form Fields - View/Edit Mode
    private JTextField treatmentNameField;
    private JTextArea descriptionArea;
    private JComboBox<String> categoryCombo;
    private JTextField costField;
    private JTextField durationField;
    private JCheckBox activeCheckBox;
    private JLabel treatmentIdLabel;
    private JLabel createdDateLabel;
    private JLabel updatedDateLabel;
    private JLabel statusLabel;
    private JLabel lastUpdatedLabel;
    private JLabel statusBadge;
    
    // Buttons
    private RoundedButton editButton;
    private RoundedButton saveButton;
    private RoundedButton cancelButton;
    private RoundedButton backButton;
    private RoundedButton deleteButton;
    private RoundedButton toggleStatusButton;
    
    private JPanel buttonPanel;
    private boolean isEditMode = false;
    private Treatment currentTreatment;
    private TreatmentController controller;
    private User currentUser;

    // Auto-refresh timer (hidden)
    private Timer refreshTimer;
    private static final int AUTO_REFRESH_DELAY = 30000; // 30 seconds

    public TreatmentDetailsPanel() {
        this.controller = new TreatmentController(this);
        this.currentUser = LoginSession.getInstance().getCurrentUser();
        initComponents();
        setViewMode(false);
        displayEmptyState();
        startAutoRefresh();
    }

    public TreatmentDetailsPanel(Treatment treatment) {
        this.controller = new TreatmentController(this);
        this.currentUser = LoginSession.getInstance().getCurrentUser();
        this.currentTreatment = treatment;
        initComponents();
        setViewMode(false);
        displayTreatment(treatment);
        startAutoRefresh();
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

    // =====================================================
    // AUTO-REFRESH (Hidden - No UI Indicator)
    // =====================================================

    private void startAutoRefresh() {
        if (refreshTimer == null) {
            refreshTimer = new Timer(AUTO_REFRESH_DELAY, e -> {
                if (isShowing() && currentTreatment != null) {
                    refreshTreatmentData();
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

    private void refreshTreatmentData() {
        if (currentTreatment != null) {
            Treatment updated = controller.getTreatmentById(currentTreatment.getTreatmentId());
            if (updated != null) {
                currentTreatment = updated;
                displayTreatment(currentTreatment);
            }
        }
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
     * ✅ Check if current user has edit/delete permissions
     */
    private boolean hasEditPermission() {
        if (currentUser == null) return false;
        UserRole role = currentUser.getRole();
        return role == UserRole.ADMIN || role == UserRole.RECEPTION;
    }

    /**
     * ✅ Check if current user has view permission (all roles can view)
     */
    private boolean hasViewPermission() {
        return currentUser != null;
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(SOFT_SURFACE);
        header.setBorder(new EmptyBorder(0, 0, 15, 0));

        // Title and treatment info
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Treatment Details");
        titleLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_DARK);
        
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        infoPanel.setOpaque(false);
        
        treatmentIdLabel = new JLabel("Treatment ID: --");
        treatmentIdLabel.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 14));
        treatmentIdLabel.setForeground(SECONDARY_TEXT);
        
        createdDateLabel = new JLabel("Created: --");
        createdDateLabel.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 14));
        createdDateLabel.setForeground(SECONDARY_TEXT);
        
        updatedDateLabel = new JLabel("Last Updated: --");
        updatedDateLabel.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 14));
        updatedDateLabel.setForeground(SECONDARY_TEXT);
        
        // Status badge
        statusBadge = new JLabel("--");
        statusBadge.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 12));
        statusBadge.setOpaque(true);
        statusBadge.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        statusBadge.setVisible(false);
        
        infoPanel.add(treatmentIdLabel);
        infoPanel.add(createdDateLabel);
        infoPanel.add(updatedDateLabel);
        infoPanel.add(statusBadge);
        
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        titlePanel.add(infoPanel);

        header.add(titlePanel, BorderLayout.WEST);
        
        // Manual Refresh Button - ICON ONLY
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);
        
        JButton refreshBtn = createIconButton(FontAwesomeSolid.SYNC_ALT, COLOR_REFRESH);
        refreshBtn.setPreferredSize(new Dimension(40, 40));
        refreshBtn.setToolTipText("Refresh Now");
        refreshBtn.addActionListener(e -> refreshTreatmentData());
        rightPanel.add(refreshBtn);

        header.add(rightPanel, BorderLayout.EAST);

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

        // Treatment Information Section
        mainPanel.add(createSectionPanel("Treatment Information", createTreatmentInfoPanel()));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Pricing & Duration Section
        mainPanel.add(createSectionPanel("Pricing & Duration", createPricingPanel()));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Status Section
        mainPanel.add(createSectionPanel("Status", createStatusPanel()));

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
        nameLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        nameLabel.setForeground(PRIMARY_DARK);
        panel.add(nameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 0.8;
        treatmentNameField = createTextField();
        treatmentNameField.setEnabled(false);
        panel.add(treatmentNameField, gbc);

        // Category
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel categoryLabel = new JLabel("Category:");
        categoryLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        categoryLabel.setForeground(PRIMARY_DARK);
        panel.add(categoryLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 0.8;
        String[] categories = {"Preventive", "Restorative", "Endodontic", "Orthodontic", 
                               "Cosmetic", "Surgical", "Periodontic", "Other"};
        categoryCombo = new JComboBox<>(categories);
        categoryCombo.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        categoryCombo.setPreferredSize(new Dimension(300, 35));
        categoryCombo.setEnabled(false);
        panel.add(categoryCombo, gbc);

        // Description
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel descLabel = new JLabel("Description:");
        descLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        descLabel.setForeground(PRIMARY_DARK);
        panel.add(descLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.weightx = 0.8;
        descriptionArea = createTextArea();
        descriptionArea.setEnabled(false);
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
        costLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        costLabel.setForeground(PRIMARY_DARK);
        panel.add(costLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        costField = createTextField();
        costField.setEnabled(false);
        panel.add(costField, gbc);

        // Duration
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel durationLabel = new JLabel("Duration (mins):");
        durationLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        durationLabel.setForeground(PRIMARY_DARK);
        panel.add(durationLabel, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        durationField = createTextField();
        durationField.setEnabled(false);
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

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        statusLabel.setForeground(PRIMARY_DARK);
        panel.add(statusLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 0.8;
        activeCheckBox = new JCheckBox("Active");
        activeCheckBox.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        activeCheckBox.setEnabled(false);
        panel.add(activeCheckBox, gbc);

        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(SOFT_SURFACE);
        footer.setBorder(new EmptyBorder(15, 0, 0, 0));

        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 12));
        statusLabel.setForeground(SECONDARY_TEXT);

        lastUpdatedLabel = new JLabel("Last updated: --");
        lastUpdatedLabel.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 12));
        lastUpdatedLabel.setForeground(SECONDARY_TEXT);

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setOpaque(false);
        leftPanel.add(statusLabel);
        leftPanel.add(lastUpdatedLabel);

        buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        // Back button - Available to all
        backButton = createStyledButton("Back", SOFT_SURFACE, PRIMARY_DARK);
        backButton.setBorderColor(LIGHT_SURFACE);
        backButton.setPreferredSize(new Dimension(100, 35));
        backButton.addActionListener(e -> navigateBack());
        backButton.setIcon(icon(FontAwesomeSolid.ARROW_LEFT, 12, PRIMARY_DARK));
        backButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        backButton.setIconTextGap(6);

        // Edit button - Only for ADMIN and RECEPTION
        editButton = createStyledButton("Edit", PRIMARY_DARK, Color.WHITE);
        editButton.setPreferredSize(new Dimension(100, 35));
        editButton.addActionListener(e -> toggleEditMode());
        editButton.setIcon(icon(FontAwesomeSolid.EDIT, 12, Color.WHITE));
        editButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        editButton.setIconTextGap(6);
        
        // Hide Edit button for DENTIST and PATIENT
        if (!hasEditPermission()) {
            editButton.setVisible(false);
        }

        // Toggle Status button - Only for ADMIN and RECEPTION
        toggleStatusButton = createStyledButton("Toggle Status", SOFT_SURFACE, PRIMARY_DARK);
        toggleStatusButton.setBorderColor(LIGHT_SURFACE);
        toggleStatusButton.setPreferredSize(new Dimension(120, 35));
        toggleStatusButton.addActionListener(e -> toggleStatus());
        toggleStatusButton.setIcon(icon(FontAwesomeSolid.SYNC, 12, PRIMARY_DARK));
        toggleStatusButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        toggleStatusButton.setIconTextGap(6);
        
        // Hide Toggle Status button for DENTIST and PATIENT
        if (!hasEditPermission()) {
            toggleStatusButton.setVisible(false);
        }

        // Save button (hidden initially) - Only for ADMIN and RECEPTION
        saveButton = createStyledButton("Save", PRIMARY_DARK, Color.WHITE);
        saveButton.setPreferredSize(new Dimension(100, 35));
        saveButton.setVisible(false);
        saveButton.addActionListener(e -> saveTreatment());
        saveButton.setIcon(icon(FontAwesomeSolid.SAVE, 12, Color.WHITE));
        saveButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        saveButton.setIconTextGap(6);

        // Cancel button (hidden initially) - Only for ADMIN and RECEPTION
        cancelButton = createStyledButton("Cancel", SOFT_SURFACE, PRIMARY_DARK);
        cancelButton.setBorderColor(LIGHT_SURFACE);
        cancelButton.setPreferredSize(new Dimension(100, 35));
        cancelButton.setVisible(false);
        cancelButton.addActionListener(e -> cancelEdit());
        cancelButton.setIcon(icon(FontAwesomeSolid.TIMES, 12, PRIMARY_DARK));
        cancelButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        cancelButton.setIconTextGap(6);

        // Delete button - Only for ADMIN and RECEPTION
        deleteButton = createStyledButton("Delete", ERROR_COLOR, Color.WHITE);
        deleteButton.setPreferredSize(new Dimension(100, 35));
        deleteButton.addActionListener(e -> deleteTreatment());
        deleteButton.setIcon(icon(FontAwesomeSolid.TRASH_ALT, 12, Color.WHITE));
        deleteButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        deleteButton.setIconTextGap(6);
        
        // Hide Delete button for DENTIST and PATIENT
        if (!hasEditPermission()) {
            deleteButton.setVisible(false);
        }

        buttonPanel.add(backButton);
        buttonPanel.add(editButton);
        buttonPanel.add(toggleStatusButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(deleteButton);

        footer.add(leftPanel, BorderLayout.WEST);
        footer.add(buttonPanel, BorderLayout.EAST);

        return footer;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        field.setBackground(Color.WHITE);
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

    // ========================
    // Public methods
    // ========================

    public void displayTreatment(Treatment treatment) {
        this.currentTreatment = treatment;
        if (treatment == null) {
            displayEmptyState();
            return;
        }

        treatmentIdLabel.setText("Treatment ID: " + treatment.getTreatmentId());
        treatmentNameField.setText(treatment.getTreatmentName());
        categoryCombo.setSelectedItem(treatment.getCategory() != null ? treatment.getCategory() : "Other");
        descriptionArea.setText(treatment.getDescription() != null ? treatment.getDescription() : "");
        costField.setText(treatment.getCost() > 0 ? String.valueOf(treatment.getCost()) : "");
        durationField.setText(treatment.getDuration() > 0 ? String.valueOf(treatment.getDuration()) : "");
        activeCheckBox.setSelected(treatment.isActive());
        
        createdDateLabel.setText("Created: " + (treatment.getCreatedAt() != null ? treatment.getCreatedAt() : "--"));
        updatedDateLabel.setText("Last Updated: " + (treatment.getUpdatedAt() != null ? treatment.getUpdatedAt() : "--"));
        
        // Update status badge
        updateStatusBadge(treatment.isActive());
        
        lastUpdatedLabel.setText("Last updated: " + 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        
        statusLabel.setText(" ");
        setViewMode(false);
    }

    private void displayEmptyState() {
        treatmentIdLabel.setText("Treatment ID: --");
        treatmentNameField.setText("");
        categoryCombo.setSelectedIndex(0);
        descriptionArea.setText("");
        costField.setText("");
        durationField.setText("");
        activeCheckBox.setSelected(false);
        createdDateLabel.setText("Created: --");
        updatedDateLabel.setText("Last Updated: --");
        statusBadge.setVisible(false);
        statusLabel.setText("No treatment selected");
        lastUpdatedLabel.setText("Last updated: --");
        setViewMode(false);
    }

    private void updateStatusBadge(boolean isActive) {
        statusBadge.setVisible(true);
        if (isActive) {
            statusBadge.setText(" Active ");
            statusBadge.setBackground(ACTIVE_COLOR);
            statusBadge.setForeground(Color.WHITE);
        } else {
            statusBadge.setText(" Inactive ");
            statusBadge.setBackground(INACTIVE_COLOR);
            statusBadge.setForeground(Color.WHITE);
        }
    }

    private void setViewMode(boolean editMode) {
        this.isEditMode = editMode;
        
        // Enable/disable fields
        treatmentNameField.setEnabled(editMode);
        categoryCombo.setEnabled(editMode);
        descriptionArea.setEnabled(editMode);
        costField.setEnabled(editMode);
        durationField.setEnabled(editMode);
        activeCheckBox.setEnabled(editMode);

        // Show/hide buttons
        // Only show edit/save/cancel buttons if user has permission
        if (hasEditPermission()) {
            editButton.setVisible(!editMode);
            toggleStatusButton.setVisible(!editMode);
            deleteButton.setVisible(!editMode);
            saveButton.setVisible(editMode);
            cancelButton.setVisible(editMode);
        }

        if (editMode) {
            statusLabel.setText("Editing treatment information...");
            statusLabel.setForeground(new Color(0, 120, 215));
        } else {
            statusLabel.setText(" ");
            statusLabel.setForeground(SECONDARY_TEXT);
        }
    }

    public void toggleEditMode() {
        if (!hasEditPermission()) {
            showError("You don't have permission to edit treatments.");
            return;
        }
        
        if (currentTreatment == null) {
            showError("No treatment loaded to edit.");
            return;
        }
        setViewMode(true);
    }

    private void cancelEdit() {
        if (!hasEditPermission()) {
            return;
        }
        
        if (currentTreatment != null) {
            displayTreatment(currentTreatment);
        } else {
            displayEmptyState();
        }
        setViewMode(false);
        statusLabel.setText("Edit cancelled");
        statusLabel.setForeground(SECONDARY_TEXT);
    }

    private void saveTreatment() {
        if (!hasEditPermission()) {
            showError("You don't have permission to save changes.");
            return;
        }
        
        if (currentTreatment == null) {
            showError("No treatment loaded to save.");
            return;
        }

        // Validate fields
        String treatmentName = treatmentNameField.getText().trim();
        if (treatmentName.isEmpty()) {
            showError("Treatment Name is required.");
            return;
        }
        if (treatmentName.length() < 2) {
            showError("Treatment Name must be at least 2 characters.");
            return;
        }

        String description = descriptionArea.getText().trim();
        if (description.isEmpty()) {
            showError("Description is required.");
            return;
        }

        // Validate cost
        String costStr = costField.getText().trim();
        double cost = 0;
        if (costStr.isEmpty()) {
            showError("Cost is required.");
            return;
        }
        try {
            cost = Double.parseDouble(costStr);
            if (cost < 0) {
                showError("Cost cannot be negative.");
                return;
            }
        } catch (NumberFormatException e) {
            showError("Please enter a valid number for cost.");
            return;
        }

        // Validate duration
        String durationStr = durationField.getText().trim();
        int duration = 0;
        if (durationStr.isEmpty()) {
            showError("Duration is required.");
            return;
        }
        try {
            duration = Integer.parseInt(durationStr);
            if (duration <= 0) {
                showError("Duration must be greater than 0.");
                return;
            }
        } catch (NumberFormatException e) {
            showError("Please enter a valid number for duration.");
            return;
        }

        // Update treatment object
        currentTreatment.setTreatmentName(treatmentName);
        currentTreatment.setCategory((String) categoryCombo.getSelectedItem());
        currentTreatment.setDescription(description);
        currentTreatment.setCost(cost);
        currentTreatment.setDuration(duration);
        currentTreatment.setActive(activeCheckBox.isSelected());

        // Save to database
        statusLabel.setText("Saving treatment...");
        statusLabel.setForeground(new Color(0, 120, 215));
        
        boolean success = controller.updateTreatment(currentTreatment);
        
        if (success) {
            statusLabel.setText("Treatment updated successfully!");
            statusLabel.setForeground(SUCCESS_COLOR);
            setViewMode(false);
            displayTreatment(currentTreatment);
            showSuccess("Treatment information updated successfully!");
        } else {
            statusLabel.setText("Failed to update treatment.");
            statusLabel.setForeground(ERROR_COLOR);
            showError("Failed to update treatment information. Please try again.");
        }
    }

    private void toggleStatus() {
        if (!hasEditPermission()) {
            showError("You don't have permission to change treatment status.");
            return;
        }
        
        if (currentTreatment == null) {
            showError("No treatment loaded.");
            return;
        }

        boolean newStatus = !currentTreatment.isActive();
        String statusText = newStatus ? "Active" : "Inactive";
        
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to change " + currentTreatment.getTreatmentName() + "'s status to " + statusText + "?",
            "Toggle Status",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success;
            if (newStatus) {
                success = controller.activateTreatment(currentTreatment.getTreatmentId());
            } else {
                success = controller.deactivateTreatment(currentTreatment.getTreatmentId());
            }
            
            if (success) {
                currentTreatment.setActive(newStatus);
                updateStatusBadge(newStatus);
                activeCheckBox.setSelected(newStatus);
                showSuccess("Treatment status updated to " + statusText + "!");
                refreshTreatmentData();
            } else {
                showError("Failed to update treatment status.");
            }
        }
    }

    private void deleteTreatment() {
        if (!hasEditPermission()) {
            showError("You don't have permission to delete treatments.");
            return;
        }
        
        if (currentTreatment == null) {
            showError("No treatment loaded to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete treatment: " + currentTreatment.getTreatmentName() + "?\nThis action cannot be undone.",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = controller.deleteTreatment(currentTreatment.getTreatmentId());
            
            if (success) {
                showSuccess("Treatment deleted successfully!");
                navigateBack();
            } else {
                showError("Failed to delete treatment. Please try again.");
            }
        }
    }

    private void navigateBack() {
        Container parent = getParent();
        while (parent != null && !(parent instanceof MainFrame)) {
            parent = parent.getParent();
        }
        if (parent instanceof MainFrame) {
            ((MainFrame) parent).showCard("TREATMENT_LIST");
        }
    }

    // ========================
    // Public methods for Controller
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
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showInfo(String message) {
        statusLabel.setIcon(icon(FontAwesomeSolid.INFO_CIRCLE, 14, new Color(107, 123, 121)));
        statusLabel.setText(message);
        statusLabel.setForeground(new Color(107, 123, 121));
    }
}