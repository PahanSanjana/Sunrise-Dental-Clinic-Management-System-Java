package view;

import controller.UserProfileController;
import model.User;
import model.User.UserRole;
import model.LoginSession;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class UserProfilePanel extends JPanel {
    
    private static final Color PRIMARY_DARK = new Color(0x2F3E3C);
    private static final Color MINT = new Color(0xBDDBD1);
    private static final Color SOFT_SURFACE = new Color(0xFBF9F1);
    private static final Color LIGHT_SURFACE = new Color(0xE7E9E3);
    private static final Color ERROR_COLOR = new Color(220, 80, 80);
    private static final Color SUCCESS_COLOR = new Color(60, 160, 80);
    private static final Color SECONDARY_TEXT = new Color(122, 138, 135);

    private static final Color COLOR_REFRESH = new Color(52, 152, 219);
    private static final Color COLOR_REFRESH_HOVER = new Color(41, 128, 185);

    private static final String UI_FONT_FAMILY = "Segoe UI";

    // ICON HELPERS (Ikonli FontIcon)
    private static FontIcon icon(FontAwesomeSolid glyph, int size, Color color) {
        return FontIcon.of(glyph, size, color);
    }

    private static JLabel iconLabel(FontAwesomeSolid glyph, int size, Color color) {
        return new JLabel(icon(glyph, size, color));
    }

    private JLabel usernameLabel;
    private JLabel emailLabel;
    private JLabel roleLabel;
    private JLabel statusLabel;
    private JLabel createdDateLabel;
    private JLabel lastLoginLabel;
    
    private JPanel profileDetailsPanel;
    
    private RoundedButton editProfileButton;
    private RoundedButton changePasswordButton;
    private JButton refreshButton;
    
    private JLabel statusMessageLabel;
    private UserProfileController controller;
    private User currentUser;

    private Timer refreshTimer;
    private static final int AUTO_REFRESH_DELAY = 30000; // 30 seconds

    public UserProfilePanel() {
        initComponents();
        this.controller = new UserProfileController(this);
        loadUserProfile();
        startAutoRefresh();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(SOFT_SURFACE);
        setBorder(new EmptyBorder(20, 30, 20, 30));

        add(createHeaderPanel(), BorderLayout.NORTH);
        
        JPanel mainWrapper = new JPanel(new BorderLayout());
        mainWrapper.setBackground(Color.WHITE);
        mainWrapper.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            new EmptyBorder(20, 30, 20, 30)
        ));
        
        JPanel mainContentPanel = createMainContentPanel();
        
        JScrollPane scrollPane = new JScrollPane(mainContentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(12, 0));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        mainWrapper.add(scrollPane, BorderLayout.CENTER);
        add(mainWrapper, BorderLayout.CENTER);
        
        add(createFooterPanel(), BorderLayout.SOUTH);
    }

    
    private void startAutoRefresh() {
        if (refreshTimer == null) {
            refreshTimer = new Timer(AUTO_REFRESH_DELAY, e -> {
                if (isShowing()) {
                    loadUserProfile();
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

    // HEADER PANEL 
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(SOFT_SURFACE);
        header.setBorder(new EmptyBorder(0, 0, 15, 0));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("My Profile");
        titleLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_DARK);
        
        JLabel subtitleLabel = new JLabel("View and manage your account information");
        subtitleLabel.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 14));
        subtitleLabel.setForeground(SECONDARY_TEXT);
        
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);
        
        refreshButton = createIconButton(FontAwesomeSolid.SYNC_ALT, COLOR_REFRESH);
        refreshButton.setPreferredSize(new Dimension(40, 40));
        refreshButton.setToolTipText("Refresh Now");
        refreshButton.addActionListener(e -> loadUserProfile());
        rightPanel.add(refreshButton);

        header.add(titlePanel, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);

        return header;
    }

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

    private JPanel createMainContentPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);

        mainPanel.add(createUserInfoSection());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        mainPanel.add(createProfileDetailsSection());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        mainPanel.add(createActionPanel());

        return mainPanel;
    }

    private JPanel createUserInfoSection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(MINT, 1),
            "Account Information",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font(UI_FONT_FAMILY, Font.BOLD, 14),
            PRIMARY_DARK
        ));

        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.weightx = 1.0;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.15;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        userLabel.setForeground(PRIMARY_DARK);
        infoPanel.add(userLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.35;
        usernameLabel = new JLabel("--");
        usernameLabel.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        infoPanel.add(usernameLabel, gbc);

        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.15;
        JLabel roleLabelTitle = new JLabel("Role:");
        roleLabelTitle.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        roleLabelTitle.setForeground(PRIMARY_DARK);
        infoPanel.add(roleLabelTitle, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.35;
        roleLabel = new JLabel("--");
        roleLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        roleLabel.setForeground(PRIMARY_DARK);
        infoPanel.add(roleLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.15;
        JLabel emailLabelTitle = new JLabel("Email:");
        emailLabelTitle.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        emailLabelTitle.setForeground(PRIMARY_DARK);
        infoPanel.add(emailLabelTitle, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.35;
        emailLabel = new JLabel("--");
        emailLabel.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        infoPanel.add(emailLabel, gbc);

        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.15;
        JLabel statusLabelTitle = new JLabel("Status:");
        statusLabelTitle.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        statusLabelTitle.setForeground(PRIMARY_DARK);
        infoPanel.add(statusLabelTitle, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.35;
        statusLabel = new JLabel("--");
        statusLabel.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        infoPanel.add(statusLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.15;
        JLabel createdLabelTitle = new JLabel("Created:");
        createdLabelTitle.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        createdLabelTitle.setForeground(PRIMARY_DARK);
        infoPanel.add(createdLabelTitle, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 0.85;
        createdDateLabel = new JLabel("--");
        createdDateLabel.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        infoPanel.add(createdDateLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.15;
        JLabel lastLoginTitle = new JLabel("Last Login:");
        lastLoginTitle.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        lastLoginTitle.setForeground(PRIMARY_DARK);
        infoPanel.add(lastLoginTitle, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 0.85;
        lastLoginLabel = new JLabel("--");
        lastLoginLabel.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        infoPanel.add(lastLoginLabel, gbc);

        panel.add(infoPanel);
        return panel;
    }

    private JPanel createProfileDetailsSection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(MINT, 1),
            "Profile Details",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font(UI_FONT_FAMILY, Font.BOLD, 14),
            PRIMARY_DARK
        ));

        profileDetailsPanel = new JPanel();
        profileDetailsPanel.setBackground(Color.WHITE);
        profileDetailsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        profileDetailsPanel.setLayout(new BoxLayout(profileDetailsPanel, BoxLayout.Y_AXIS));

        JLabel loadingLabel = new JLabel("Loading profile details...");
        loadingLabel.setFont(new Font(UI_FONT_FAMILY, Font.ITALIC, 13));
        loadingLabel.setForeground(SECONDARY_TEXT);
        profileDetailsPanel.add(loadingLabel);

        panel.add(profileDetailsPanel);
        return panel;
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(MINT, 1),
            "Account Actions",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font(UI_FONT_FAMILY, Font.BOLD, 14),
            PRIMARY_DARK
        ));

        editProfileButton = createStyledButton("Edit Profile", PRIMARY_DARK, Color.WHITE);
        editProfileButton.setPreferredSize(new Dimension(160, 40));
        editProfileButton.addActionListener(e -> openEditProfile());
        editProfileButton.setIcon(icon(FontAwesomeSolid.EDIT, 16, Color.WHITE));
        editProfileButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        editProfileButton.setIconTextGap(8);

        changePasswordButton = createStyledButton("Change Password", MINT, PRIMARY_DARK);
        changePasswordButton.setPreferredSize(new Dimension(180, 40));
        changePasswordButton.addActionListener(e -> openChangePassword());
        changePasswordButton.setIcon(icon(FontAwesomeSolid.LOCK, 16, PRIMARY_DARK));
        changePasswordButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        changePasswordButton.setIconTextGap(8);

        panel.add(editProfileButton);
        panel.add(changePasswordButton);

        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(SOFT_SURFACE);
        footer.setBorder(new EmptyBorder(15, 0, 0, 0));

        statusMessageLabel = new JLabel(" ");
        statusMessageLabel.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 12));
        statusMessageLabel.setForeground(SECONDARY_TEXT);

        footer.add(statusMessageLabel, BorderLayout.WEST);
        return footer;
    }

    private RoundedButton createStyledButton(String text, Color bg, Color fg) {
        RoundedButton button = new RoundedButton(text, bg, fg);
        button.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        return button;
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
            } else if (bg.equals(ERROR_COLOR)) {
                hoverColor = new Color(180, 60, 60);
            } else if (bg.equals(MINT)) {
                hoverColor = new Color(150, 200, 180);
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



    public void loadUserProfile() {
        int userId = LoginSession.getInstance().getCurrentUserId();
        
        if (userId <= 0) {
            showError("No user logged in.");
            return;
        }

        this.currentUser = controller.getUserById(userId);
        
        if (currentUser == null) {
            showError("User not found in database.");
            return;
        }

        LoginSession.getInstance().setCurrentUser(currentUser);

        usernameLabel.setText(currentUser.getUsername());
        emailLabel.setText(currentUser.getEmail() != null ? currentUser.getEmail() : "N/A");
        roleLabel.setText(currentUser.getRole().name());
        statusLabel.setText(currentUser.isActive() ? "Active" : "Inactive");
        statusLabel.setForeground(currentUser.isActive() ? SUCCESS_COLOR : ERROR_COLOR);
        createdDateLabel.setText(currentUser.getCreatedAt() != null ? currentUser.getCreatedAt() : "N/A");
        
        loadRoleProfile(currentUser);

        showSuccess("Profile loaded successfully!");
    }

    private void loadRoleProfile(User user) {
        profileDetailsPanel.removeAll();
        profileDetailsPanel.setLayout(new BoxLayout(profileDetailsPanel, BoxLayout.Y_AXIS));

        switch (user.getRole()) {
            case ADMIN:
                loadAdminProfile(user);
                break;
            case PATIENT:
                loadPatientProfile(user);
                break;
            case RECEPTION:
                loadStaffProfile(user);
                break;
            case DENTIST:
                loadDentistProfile(user);
                break;
            default:
                addNoProfileMessage();
                break;
        }

        profileDetailsPanel.revalidate();
        profileDetailsPanel.repaint();
    }

    private void addNoProfileMessage() {
        JLabel label = new JLabel("No profile details available for this role.");
        label.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        label.setForeground(SECONDARY_TEXT);
        profileDetailsPanel.add(label);
    }

    private void addEmptyProfileMessage(String roleName) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(Color.WHITE);
        
        JLabel iconLabel = iconLabel(FontAwesomeSolid.EXCLAMATION_TRIANGLE, 24, ERROR_COLOR);
        JLabel messageLabel = new JLabel(" No " + roleName + " profile found. Please contact administrator.");
        messageLabel.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 14));
        messageLabel.setForeground(ERROR_COLOR);
        
        panel.add(iconLabel);
        panel.add(messageLabel);
        profileDetailsPanel.add(panel);
    }

    private void loadAdminProfile(User user) {
        addProfileField("Role", "System Administrator");
        addProfileField("Permissions", "Full access to all system features");
        addProfileField("User ID", String.valueOf(user.getUserId()));
    }

    private void loadPatientProfile(User user) {
        profileDetailsPanel.add(new JLabel("Loading patient data..."));
        profileDetailsPanel.revalidate();
        
        controller.loadPatientProfile(user.getUserId(), data -> {
            SwingUtilities.invokeLater(() -> {
                profileDetailsPanel.removeAll();
                profileDetailsPanel.setLayout(new BoxLayout(profileDetailsPanel, BoxLayout.Y_AXIS));
                
                if (data != null && !data.isEmpty()) {
                    if (data.get("patientId") != null && ((Number) data.get("patientId")).intValue() > 0) {
                        addProfileField("Patient ID", String.valueOf(data.get("patientId")));
                        addProfileField("Full Name", getString(data.get("patientName")));
                        addProfileField("Gender", getString(data.get("gender")));
                        addProfileField("Contact Number", getString(data.get("contactNumber")));
                        addProfileField("Email", getString(data.get("email")));
                        addProfileField("Address", getString(data.get("address")));
                        addProfileField("Date of Birth", getString(data.get("dateOfBirth")));
                        addProfileField("Emergency Contact", getString(data.get("emergencyContact")));
                        addProfileField("Emergency Phone", getString(data.get("emergencyPhone")));
                        addProfileField("Medical History", getString(data.get("medicalHistory")));
                        addProfileField("Allergies", getString(data.get("allergies")));
                    } else {
                        addEmptyProfileMessage("patient");
                    }
                } else {
                    addEmptyProfileMessage("patient");
                }
                
                profileDetailsPanel.revalidate();
                profileDetailsPanel.repaint();
            });
        });
    }

    private void loadStaffProfile(User user) {
        profileDetailsPanel.add(new JLabel("Loading staff data..."));
        profileDetailsPanel.revalidate();
        
        controller.loadStaffProfile(user.getUserId(), data -> {
            SwingUtilities.invokeLater(() -> {
                profileDetailsPanel.removeAll();
                profileDetailsPanel.setLayout(new BoxLayout(profileDetailsPanel, BoxLayout.Y_AXIS));
                
                if (data != null && !data.isEmpty()) {
                    if (data.get("staffId") != null && ((Number) data.get("staffId")).intValue() > 0) {
                        addProfileField("Staff ID", String.valueOf(data.get("staffId")));
                        addProfileField("Full Name", getString(data.get("fullName")));
                        addProfileField("First Name", getString(data.get("firstName")));
                        addProfileField("Last Name", getString(data.get("lastName")));
                        addProfileField("Position", getString(data.get("position")));
                        addProfileField("Department", getString(data.get("department")));
                        addProfileField("Phone", getString(data.get("phone")));
                        addProfileField("Email", getString(data.get("email")));
                        addProfileField("Hire Date", getString(data.get("hireDate")));
                        addProfileField("Salary", data.get("salary") != null ? "$" + data.get("salary") : "N/A");
                        addProfileField("Status", data.get("isActive") != null ? 
                            (Boolean) data.get("isActive") ? "Active" : "Inactive" : "N/A");
                    } else {
                        addEmptyProfileMessage("staff");
                    }
                } else {
                    addEmptyProfileMessage("staff");
                }
                
                profileDetailsPanel.revalidate();
                profileDetailsPanel.repaint();
            });
        });
    }

    private void loadDentistProfile(User user) {
        profileDetailsPanel.add(new JLabel("Loading dentist data..."));
        profileDetailsPanel.revalidate();
        
        controller.loadDentistProfile(user.getUserId(), data -> {
            SwingUtilities.invokeLater(() -> {
                profileDetailsPanel.removeAll();
                profileDetailsPanel.setLayout(new BoxLayout(profileDetailsPanel, BoxLayout.Y_AXIS));
                
                if (data != null && !data.isEmpty()) {
                    if (data.get("dentistId") != null && ((Number) data.get("dentistId")).intValue() > 0) {
                        addProfileField("Dentist ID", String.valueOf(data.get("dentistId")));
                        addProfileField("Dentist Name", getString(data.get("dentistName")));
                        addProfileField("Specialization", getString(data.get("specialization")));
                        addProfileField("License Number", getString(data.get("licenseNumber")));
                        addProfileField("Working Hours", getString(data.get("workingHours")));
                        addProfileField("Phone", getString(data.get("phone")));
                        addProfileField("Email", getString(data.get("email")));
                        addProfileField("Years of Experience", String.valueOf(data.get("yearsOfExperience")));
                        addProfileField("Consultation Fee", data.get("consultationFee") != null ? 
                            "$" + data.get("consultationFee") : "N/A");
                        addProfileField("Availability", data.get("isAvailable") != null ? 
                            (Boolean) data.get("isAvailable") ? "Available" : "Unavailable" : "N/A");
                    } else {
                        addEmptyProfileMessage("dentist");
                    }
                } else {
                    addEmptyProfileMessage("dentist");
                }
                
                profileDetailsPanel.revalidate();
                profileDetailsPanel.repaint();
            });
        });
    }

    private String getString(Object value) {
        if (value == null) return "N/A";
        String str = value.toString();
        return str.isEmpty() ? "N/A" : str;
    }

    private void addProfileField(String label, String value) {
        JPanel fieldPanel = new JPanel(new BorderLayout());
        fieldPanel.setBackground(Color.WHITE);
        fieldPanel.setBorder(new EmptyBorder(3, 5, 3, 5));
        fieldPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel labelComp = new JLabel(label + ":");
        labelComp.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
        labelComp.setForeground(PRIMARY_DARK);
        labelComp.setPreferredSize(new Dimension(160, 25));

        JLabel valueComp = new JLabel(value != null && !value.isEmpty() ? value : "N/A");
        valueComp.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        valueComp.setForeground(Color.BLACK);

        fieldPanel.add(labelComp, BorderLayout.WEST);
        fieldPanel.add(valueComp, BorderLayout.CENTER);

        profileDetailsPanel.add(fieldPanel);
    }

    private void openEditProfile() {
        if (currentUser == null) {
            showError("No user logged in.");
            return;
        }
        
        EditProfileDialog dialog = new EditProfileDialog(
            (JFrame) SwingUtilities.getWindowAncestor(this),
            currentUser,
            controller
        );
        dialog.setVisible(true);
        
        loadUserProfile();
    }

    private void openChangePassword() {
        if (currentUser == null) {
            showError("No user logged in.");
            return;
        }
        
        ChangePasswordDialog dialog = new ChangePasswordDialog(
            (JFrame) SwingUtilities.getWindowAncestor(this),
            currentUser,
            controller
        );
        dialog.setVisible(true);
        
        loadUserProfile();
    }

    public void showError(String message) {
        statusMessageLabel.setText("❌ " + message);
        statusMessageLabel.setForeground(ERROR_COLOR);
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showSuccess(String message) {
        statusMessageLabel.setText("✅ " + message);
        statusMessageLabel.setForeground(SUCCESS_COLOR);
    }

    public void showInfo(String message) {
        statusMessageLabel.setText("ℹ️ " + message);
        statusMessageLabel.setForeground(new Color(0, 120, 215));
    }

    private static class MouseAdapter extends java.awt.event.MouseAdapter {
    }
}