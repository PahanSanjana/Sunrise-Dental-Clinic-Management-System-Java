package view;

import java.awt.*;
import javax.swing.*;
import model.User;
import model.User.UserRole;

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
        
        System.out.println("MainFrame: Initialized successfully");
    }

    private void initContentPanel() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Color.WHITE);

        System.out.println("MainFrame: Adding cards to content panel...");

        // Dashboard
        JPanel dashboardPlaceholder = createDashboardPlaceholder();
        dashboardPlaceholder.setName("DASHBOARD");
        contentPanel.add(dashboardPlaceholder, "DASHBOARD");
        System.out.println("MainFrame: Added DASHBOARD card");

        // =====================================================
        // PATIENT SCREENS
        // =====================================================
        
        // Patient List
        try {
            PatientListPanel patientListPanel = new PatientListPanel();
            patientListPanel.setName("PATIENT_LIST");
            contentPanel.add(patientListPanel, "PATIENT_LIST");
            System.out.println("MainFrame: Added PATIENT_LIST card");
        } catch (Exception e) {
            System.err.println("MainFrame: Error creating PatientListPanel: " + e.getMessage());
            e.printStackTrace();
            JPanel placeholder = createPlaceholderPanel("Patient List");
            placeholder.setName("PATIENT_LIST");
            contentPanel.add(placeholder, "PATIENT_LIST");
        }

        // Add Patient
        try {
            AddPatientPanel addPatientPanel = new AddPatientPanel();
            addPatientPanel.setName("PATIENT_ADD");
            contentPanel.add(addPatientPanel, "PATIENT_ADD");
            System.out.println("MainFrame: Added PATIENT_ADD card");
        } catch (Exception e) {
            System.err.println("MainFrame: Error creating AddPatientPanel: " + e.getMessage());
            e.printStackTrace();
            JPanel placeholder = createPlaceholderPanel("Add Patient");
            placeholder.setName("PATIENT_ADD");
            contentPanel.add(placeholder, "PATIENT_ADD");
        }

        // Patient Details - Placeholder (will be replaced dynamically)
        JPanel patientDetailsPlaceholder = createPlaceholderPanel("Patient Details");
        patientDetailsPlaceholder.setName("PATIENT_DETAILS");
        contentPanel.add(patientDetailsPlaceholder, "PATIENT_DETAILS");
        System.out.println("MainFrame: Added PATIENT_DETAILS placeholder");

        // =====================================================
        // APPOINTMENT SCREENS
        // =====================================================
        
        // Appointment List - Placeholder
        JPanel appointmentList = createPlaceholderPanel("Appointment List");
        appointmentList.setName("APPOINTMENT_LIST");
        contentPanel.add(appointmentList, "APPOINTMENT_LIST");
        
        // Book Appointment - Real Implementation
        try {
            BookAppointmentPanel bookAppointmentPanel = new BookAppointmentPanel();
            bookAppointmentPanel.setName("APPOINTMENT_BOOK");
            contentPanel.add(bookAppointmentPanel, "APPOINTMENT_BOOK");
            System.out.println("MainFrame: Added APPOINTMENT_BOOK card");
        } catch (Exception e) {
            System.err.println("MainFrame: Error creating BookAppointmentPanel: " + e.getMessage());
            e.printStackTrace();
            JPanel placeholder = createPlaceholderPanel("Book Appointment");
            placeholder.setName("APPOINTMENT_BOOK");
            contentPanel.add(placeholder, "APPOINTMENT_BOOK");
        }
        
        // Appointment Details - Placeholder
        JPanel appointmentDetails = createPlaceholderPanel("Appointment Details");
        appointmentDetails.setName("APPOINTMENT_DETAILS");
        contentPanel.add(appointmentDetails, "APPOINTMENT_DETAILS");
        
        // Daily Schedule - Placeholder
        JPanel dailySchedule = createPlaceholderPanel("Daily Schedule");
        dailySchedule.setName("APPOINTMENT_SCHEDULE");
        contentPanel.add(dailySchedule, "APPOINTMENT_SCHEDULE");
        System.out.println("MainFrame: Added APPOINTMENT cards");

        // =====================================================
        // BILLING SCREENS
        // =====================================================
        contentPanel.add(createPlaceholderPanel("Bill List"), "BILL_LIST");
        contentPanel.add(createPlaceholderPanel("Generate Bill"), "BILL_GENERATE");
        contentPanel.add(createPlaceholderPanel("Bill Details"), "BILL_DETAILS");
        System.out.println("MainFrame: Added BILLING cards");

        // =====================================================
        // REPORTS SCREENS
        // =====================================================
        contentPanel.add(createPlaceholderPanel("Report Dashboard"), "REPORT_DASHBOARD");
        contentPanel.add(createPlaceholderPanel("Revenue Report"), "REPORT_REVENUE");
        contentPanel.add(createPlaceholderPanel("Schedule Report"), "REPORT_SCHEDULE");
        contentPanel.add(createPlaceholderPanel("Patient Report"), "REPORT_PATIENT");
        System.out.println("MainFrame: Added REPORTS cards");

        // =====================================================
        // STAFF SCREENS (Admin only)
        // =====================================================
        contentPanel.add(createPlaceholderPanel("Staff List"), "STAFF_LIST");
        contentPanel.add(createPlaceholderPanel("Add Staff"), "STAFF_ADD");
        contentPanel.add(createPlaceholderPanel("Staff Details"), "STAFF_DETAILS");
        System.out.println("MainFrame: Added STAFF cards");

        // =====================================================
        // DENTIST SCREENS (Admin only) - FIXED
        // =====================================================
        
        // Add Dentist - Real Implementation
        try {
            AddDentistPanel addDentistPanel = new AddDentistPanel();
            addDentistPanel.setName("DENTIST_ADD");
            contentPanel.add(addDentistPanel, "DENTIST_ADD");
            System.out.println("MainFrame: Added DENTIST_ADD card");
        } catch (Exception e) {
            System.err.println("MainFrame: Error creating AddDentistPanel: " + e.getMessage());
            e.printStackTrace();
            JPanel placeholder = createPlaceholderPanel("Add Dentist");
            placeholder.setName("DENTIST_ADD");
            contentPanel.add(placeholder, "DENTIST_ADD");
        }

        // Dentist List - Placeholder (will be replaced with real implementation later)
        JPanel dentistListPlaceholder = createPlaceholderPanel("Dentist List");
        dentistListPlaceholder.setName("DENTIST_LIST");
        contentPanel.add(dentistListPlaceholder, "DENTIST_LIST");
        System.out.println("MainFrame: Added DENTIST_LIST card");

        // =====================================================
        // TREATMENT SCREENS
        // =====================================================
        contentPanel.add(createPlaceholderPanel("Treatment List"), "TREATMENT_LIST");
        contentPanel.add(createPlaceholderPanel("Add Treatment"), "TREATMENT_ADD");
        System.out.println("MainFrame: Added TREATMENT cards");

        // =====================================================
        // AUDIT SCREENS (Admin only)
        // =====================================================
        contentPanel.add(createPlaceholderPanel("Activity Log"), "AUDIT_ACTIVITY");
        contentPanel.add(createPlaceholderPanel("Login History"), "AUDIT_LOGIN");
        System.out.println("MainFrame: Added AUDIT cards");

        // Show dashboard by default
        cardLayout.show(contentPanel, "DASHBOARD");
        System.out.println("MainFrame: Content panel initialized, showing DASHBOARD");
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

    /**
     * Configure sidebar for a specific user role
     * @param role The user role
     */
    public void configureSidebarForRole(UserRole role) {
        sidebarPanel.configureForRole(role);
        
        // Update top bar with user info
        User currentUser = model.LoginSession.getInstance().getCurrentUser();
        if (currentUser != null) {
            topBarPanel.setUserInfo(currentUser.getUsername(), currentUser.getRole().name());
        }
    }

    /**
     * Show a specific card in the content panel
     * @param cardName The name of the card to show
     */
    public void showCard(String cardName) {
        System.out.println("MainFrame: Attempting to show card: " + cardName);
        
        // Check if the card exists
        boolean cardExists = false;
        for (Component comp : contentPanel.getComponents()) {
            if (comp.getName() != null && comp.getName().equals(cardName)) {
                cardExists = true;
                break;
            }
        }
        
        if (cardExists) {
            cardLayout.show(contentPanel, cardName);
            System.out.println("MainFrame: Successfully showed card: " + cardName);
        } else {
            System.err.println("MainFrame: Card not found: " + cardName);
            // Show dashboard as fallback
            cardLayout.show(contentPanel, "DASHBOARD");
            System.out.println("MainFrame: Showing DASHBOARD as fallback");
        }
    }

    /**
     * Add a new screen dynamically to the content panel
     * @param cardName The name of the card
     * @param screen The panel to add
     */
    public void addScreen(String cardName, JPanel screen) {
        // Remove existing card if it exists
        for (Component comp : contentPanel.getComponents()) {
            if (comp.getName() != null && comp.getName().equals(cardName)) {
                contentPanel.remove(comp);
                break;
            }
        }
        screen.setName(cardName);
        contentPanel.add(screen, cardName);
        contentPanel.revalidate();
        contentPanel.repaint();
        System.out.println("MainFrame: Added/Updated screen: " + cardName);
    }

    /**
     * Remove a screen from the content panel
     * @param cardName The name of the card to remove
     */
    public void removeScreen(String cardName) {
        for (Component comp : contentPanel.getComponents()) {
            if (comp.getName() != null && comp.getName().equals(cardName)) {
                contentPanel.remove(comp);
                contentPanel.revalidate();
                contentPanel.repaint();
                System.out.println("MainFrame: Removed screen: " + cardName);
                break;
            }
        }
    }

    /**
     * Get the content panel
     * @return The content panel
     */
    public JPanel getContentPanel() {
        return contentPanel;
    }

    /**
     * Handle logout action
     */
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