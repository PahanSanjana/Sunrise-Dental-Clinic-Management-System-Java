package view;

import java.awt.*;
import javax.swing.*;
import model.User;
import model.User.UserRole;
import view.AddPatientPanel;
import view.PatientListPanel;


public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel contentPanel;
    private SidebarPanel sidebarPanel;
    private TopBarPanel topBarPanel;

    public MainFrame() {
        setTitle("Sunrise Dental Clinic - Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 850);
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initContentPanel();
        
        sidebarPanel = new SidebarPanel(this);
        topBarPanel = new TopBarPanel();
        
        add(sidebarPanel, BorderLayout.WEST);
        add(topBarPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        
        // Setup logout listener
        topBarPanel.addLogoutListener(e -> handleLogout());
    }

    private void initContentPanel() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Color.WHITE);

        // Dashboard placeholder
        JPanel dashboardPlaceholder = createDashboardPlaceholder();
        contentPanel.add(dashboardPlaceholder, "DASHBOARD");
        
        // Patient placeholder
        JPanel patientPlaceholder = createPlaceholderPanel("Patients Module");
        contentPanel.add(patientPlaceholder, "PATIENT_LIST");
        AddPatientPanel addPatientPanel = new AddPatientPanel();
        contentPanel.add(addPatientPanel, "PATIENT_ADD");
        contentPanel.add(createPlaceholderPanel("Patient Details"), "PATIENT_DETAILS");
        
        // Appointment placeholders
        contentPanel.add(createPlaceholderPanel("Appointment List"), "APPOINTMENT_LIST");
        contentPanel.add(createPlaceholderPanel("Book Appointment"), "APPOINTMENT_BOOK");
        contentPanel.add(createPlaceholderPanel("Appointment Details"), "APPOINTMENT_DETAILS");
        contentPanel.add(createPlaceholderPanel("Daily Schedule"), "APPOINTMENT_SCHEDULE");
        
        // Billing placeholders
        contentPanel.add(createPlaceholderPanel("Bill List"), "BILL_LIST");
        contentPanel.add(createPlaceholderPanel("Generate Bill"), "BILL_GENERATE");
        contentPanel.add(createPlaceholderPanel("Bill Details"), "BILL_DETAILS");
        
        // Reports placeholders
        contentPanel.add(createPlaceholderPanel("Report Dashboard"), "REPORT_DASHBOARD");
        contentPanel.add(createPlaceholderPanel("Revenue Report"), "REPORT_REVENUE");
        contentPanel.add(createPlaceholderPanel("Schedule Report"), "REPORT_SCHEDULE");
        contentPanel.add(createPlaceholderPanel("Patient Report"), "REPORT_PATIENT");
        
        // Staff placeholders (Admin only)
        contentPanel.add(createPlaceholderPanel("Staff List"), "STAFF_LIST");
        contentPanel.add(createPlaceholderPanel("Add Staff"), "STAFF_ADD");
        contentPanel.add(createPlaceholderPanel("Staff Details"), "STAFF_DETAILS");
        
        // Dentist placeholders (Admin only)
        contentPanel.add(createPlaceholderPanel("Dentist List"), "DENTIST_LIST");
        contentPanel.add(createPlaceholderPanel("Add Dentist"), "DENTIST_ADD");
        
        // Treatment placeholders
        contentPanel.add(createPlaceholderPanel("Treatment List"), "TREATMENT_LIST");
        contentPanel.add(createPlaceholderPanel("Add Treatment"), "TREATMENT_ADD");
        
        // Audit placeholders (Admin only)
        contentPanel.add(createPlaceholderPanel("Activity Log"), "AUDIT_ACTIVITY");
        contentPanel.add(createPlaceholderPanel("Login History"), "AUDIT_LOGIN");

        cardLayout.show(contentPanel, "DASHBOARD");
    }

    private JPanel createDashboardPlaceholder() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 20, 40));
        headerPanel.setLayout(new BorderLayout());
        
        JLabel welcomeLabel = new JLabel("Dashboard");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeLabel.setForeground(new Color(0x2F3E3C));
        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Center content
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(Color.WHITE);
        
        JLabel placeholderLabel = new JLabel("Welcome to Sunrise Dental Management System", SwingConstants.CENTER);
        placeholderLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        placeholderLabel.setForeground(new Color(122, 138, 135));
        centerPanel.add(placeholderLabel);
        
        panel.add(centerPanel, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createPlaceholderPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 20, 40));
        headerPanel.setLayout(new BorderLayout());
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0x2F3E3C));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Center content
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(Color.WHITE);
        
        JLabel placeholderLabel = new JLabel(title + " - Coming Soon", SwingConstants.CENTER);
        placeholderLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        placeholderLabel.setForeground(new Color(122, 138, 135));
        centerPanel.add(placeholderLabel);
        
        panel.add(centerPanel, BorderLayout.CENTER);
        
        return panel;
    }

    public void configureSidebarForRole(UserRole role) {
        sidebarPanel.configureForRole(role);
        
        // Update top bar with user info
        User currentUser = model.LoginSession.getInstance().getCurrentUser();
        if (currentUser != null) {
            topBarPanel.setUserInfo(currentUser.getUsername(), currentUser.getRole().name());
        }
    }

    public void showCard(String cardName) {
        cardLayout.show(contentPanel, cardName);
    }

    public void addScreen(String cardName, JPanel screen) {
        contentPanel.add(screen, cardName);
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Clear session
            model.LoginSession.getInstance().logout();
            
            // Close main frame
            this.dispose();
            
            // Open login screen
            SwingUtilities.invokeLater(() -> {
                Login loginView = new Login();
                new controller.LoginController(loginView);
                loginView.setVisible(true);
            });
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}