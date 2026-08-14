package view;

import model.User;
import model.User.UserRole;
import model.LoginSession;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel contentPanel;
    private SidebarPanel sidebarPanel;
    private TopBarPanel topBarPanel;
    private JPanel contentWrapper;
    
    private static final int TOP_BAR_HEIGHT = 72;

    public MainFrame() {
        setTitle("Sunrise Dental Clinic - Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 850);
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Initialize components
        initContentPanel();
        
        sidebarPanel = new SidebarPanel(this);
        topBarPanel = new TopBarPanel();
        
        // ✅ Content wrapper with NO padding - content starts immediately after TopBar
        contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setBackground(Color.WHITE);
        contentWrapper.setBorder(new EmptyBorder(0, 0, 0, 0));  // No padding
        contentWrapper.add(contentPanel, BorderLayout.CENTER);
        
        // Add to frame
        add(topBarPanel, BorderLayout.NORTH);
        add(sidebarPanel, BorderLayout.WEST);
        add(contentWrapper, BorderLayout.CENTER);
        
        // Setup logout listener
        topBarPanel.addLogoutListener(e -> handleLogout());
        
        System.out.println("MainFrame: Initialized successfully");
    }

    private void initContentPanel() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Color.WHITE);

        System.out.println("MainFrame: Adding cards to content panel...");

        // =====================================================
        // DASHBOARD - Default (will be replaced based on role)
        // =====================================================
        JPanel dashboardPlaceholder = createDashboardPlaceholder();
        dashboardPlaceholder.setName("DASHBOARD");
        contentPanel.add(dashboardPlaceholder, "DASHBOARD");
        System.out.println("MainFrame: Added DASHBOARD placeholder");

        // =====================================================
        // USER PROFILE SCREEN (All roles)
        // =====================================================
        try {
            UserProfilePanel userProfilePanel = new UserProfilePanel();
            userProfilePanel.setName("USER_PROFILE");
            contentPanel.add(userProfilePanel, "USER_PROFILE");
            System.out.println("MainFrame: Added USER_PROFILE card");
        } catch (Exception e) {
            System.err.println("MainFrame: Error creating UserProfilePanel: " + e.getMessage());
            e.printStackTrace();
            JPanel placeholder = createPlaceholderPanel();
            placeholder.setName("USER_PROFILE");
            contentPanel.add(placeholder, "USER_PROFILE");
        }

        // =====================================================
        // USER MANAGEMENT SCREENS (Admin only)
        // =====================================================
        try {
            UserManagementPanel userManagementPanel = new UserManagementPanel();
            userManagementPanel.setName("USER_MANAGEMENT");
            contentPanel.add(userManagementPanel, "USER_MANAGEMENT");
            System.out.println("MainFrame: Added USER_MANAGEMENT card");
        } catch (Exception e) {
            System.err.println("MainFrame: Error creating UserManagementPanel: " + e.getMessage());
            e.printStackTrace();
            JPanel placeholder = createPlaceholderPanel();
            placeholder.setName("USER_MANAGEMENT");
            contentPanel.add(placeholder, "USER_MANAGEMENT");
        }

        // =====================================================
        // PATIENT SCREENS
        // =====================================================
        try {
            PatientListPanel patientListPanel = new PatientListPanel();
            patientListPanel.setName("PATIENT_LIST");
            contentPanel.add(patientListPanel, "PATIENT_LIST");
            System.out.println("MainFrame: Added PATIENT_LIST card");
        } catch (Exception e) {
            System.err.println("MainFrame: Error creating PatientListPanel: " + e.getMessage());
            e.printStackTrace();
            JPanel placeholder = createPlaceholderPanel();
            placeholder.setName("PATIENT_LIST");
            contentPanel.add(placeholder, "PATIENT_LIST");
        }

        try {
            AddPatientPanel addPatientPanel = new AddPatientPanel();
            addPatientPanel.setName("PATIENT_ADD");
            contentPanel.add(addPatientPanel, "PATIENT_ADD");
            System.out.println("MainFrame: Added PATIENT_ADD card");
        } catch (Exception e) {
            System.err.println("MainFrame: Error creating AddPatientPanel: " + e.getMessage());
            e.printStackTrace();
            JPanel placeholder = createPlaceholderPanel();
            placeholder.setName("PATIENT_ADD");
            contentPanel.add(placeholder, "PATIENT_ADD");
        }

        JPanel patientDetailsPlaceholder = createPlaceholderPanel();
        patientDetailsPlaceholder.setName("PATIENT_DETAILS");
        contentPanel.add(patientDetailsPlaceholder, "PATIENT_DETAILS");
        System.out.println("MainFrame: Added PATIENT_DETAILS placeholder");

        // =====================================================
        // APPOINTMENT SCREENS
        // =====================================================
        try {
            AppointmentListPanel appointmentListPanel = new AppointmentListPanel();
            appointmentListPanel.setName("APPOINTMENT_LIST");
            contentPanel.add(appointmentListPanel, "APPOINTMENT_LIST");
            System.out.println("MainFrame: Added APPOINTMENT_LIST card");
        } catch (Exception e) {
            System.err.println("MainFrame: Error creating AppointmentListPanel: " + e.getMessage());
            e.printStackTrace();
            JPanel placeholder = createPlaceholderPanel();
            placeholder.setName("APPOINTMENT_LIST");
            contentPanel.add(placeholder, "APPOINTMENT_LIST");
        }
        
        try {
            BookAppointmentPanel bookAppointmentPanel = new BookAppointmentPanel();
            bookAppointmentPanel.setName("APPOINTMENT_BOOK");
            contentPanel.add(bookAppointmentPanel, "APPOINTMENT_BOOK");
            System.out.println("MainFrame: Added APPOINTMENT_BOOK card");
        } catch (Exception e) {
            System.err.println("MainFrame: Error creating BookAppointmentPanel: " + e.getMessage());
            e.printStackTrace();
            JPanel placeholder = createPlaceholderPanel();
            placeholder.setName("APPOINTMENT_BOOK");
            contentPanel.add(placeholder, "APPOINTMENT_BOOK");
        }
        
        JPanel appointmentDetails = createPlaceholderPanel();
        appointmentDetails.setName("APPOINTMENT_DETAILS");
        contentPanel.add(appointmentDetails, "APPOINTMENT_DETAILS");
        
        try {
            DailySchedulePanel dailySchedulePanel = new DailySchedulePanel();
            dailySchedulePanel.setName("APPOINTMENT_SCHEDULE");
            contentPanel.add(dailySchedulePanel, "APPOINTMENT_SCHEDULE");
            dailySchedulePanel.loadScheduleData();
            System.out.println("MainFrame: Added APPOINTMENT_SCHEDULE card");
        } catch (Exception e) {
            System.err.println("MainFrame: Error creating DailySchedulePanel: " + e.getMessage());
            e.printStackTrace();
            JPanel placeholder = createPlaceholderPanel();
            placeholder.setName("APPOINTMENT_SCHEDULE");
            contentPanel.add(placeholder, "APPOINTMENT_SCHEDULE");
        }
        System.out.println("MainFrame: Added APPOINTMENT cards");

        // =====================================================
        // BILLING SCREENS
        // =====================================================
        try {
            BillListPanel billListPanel = new BillListPanel();
            billListPanel.setName("BILL_LIST");
            contentPanel.add(billListPanel, "BILL_LIST");
            System.out.println("MainFrame: Added BILL_LIST card");
        } catch (Exception e) {
            System.err.println("MainFrame: Error creating BillListPanel: " + e.getMessage());
            e.printStackTrace();
            JPanel placeholder = createPlaceholderPanel();
            placeholder.setName("BILL_LIST");
            contentPanel.add(placeholder, "BILL_LIST");
        }

        try {
            GenerateBillPanel generateBillPanel = new GenerateBillPanel();
            generateBillPanel.setName("BILL_GENERATE");
            contentPanel.add(generateBillPanel, "BILL_GENERATE");
            System.out.println("MainFrame: Added BILL_GENERATE card");
        } catch (Exception e) {
            System.err.println("MainFrame: Error creating GenerateBillPanel: " + e.getMessage());
            e.printStackTrace();
            JPanel placeholder = createPlaceholderPanel();
            placeholder.setName("BILL_GENERATE");
            contentPanel.add(placeholder, "BILL_GENERATE");
        }

        JPanel billDetailsPlaceholder = createPlaceholderPanel();
        billDetailsPlaceholder.setName("BILL_DETAILS");
        contentPanel.add(billDetailsPlaceholder, "BILL_DETAILS");
        System.out.println("MainFrame: Added BILL_DETAILS card");
        System.out.println("MainFrame: Added BILLING cards");

        // =====================================================
        // REPORTS SCREENS
        // =====================================================
        try {
            ReportDashboardPanel reportDashboardPanel = new ReportDashboardPanel();
            reportDashboardPanel.setName("REPORT_DASHBOARD");
            contentPanel.add(reportDashboardPanel, "REPORT_DASHBOARD");
            System.out.println("MainFrame: Added REPORT_DASHBOARD card");
        } catch (Exception e) {
            System.err.println("MainFrame: Error creating ReportDashboardPanel: " + e.getMessage());
            e.printStackTrace();
            JPanel placeholder = createPlaceholderPanel();
            placeholder.setName("REPORT_DASHBOARD");
            contentPanel.add(placeholder, "REPORT_DASHBOARD");
        }
        
        try {
            PatientReportPanel patientReportPanel = new PatientReportPanel();
            patientReportPanel.setName("REPORT_PATIENT");
            contentPanel.add(patientReportPanel, "REPORT_PATIENT");
            System.out.println("MainFrame: Added REPORT_PATIENT card");
        } catch (Exception e) {
            System.err.println("MainFrame: Error creating PatientReportPanel: " + e.getMessage());
            e.printStackTrace();
            JPanel placeholder = createPlaceholderPanel();
            placeholder.setName("REPORT_PATIENT");
            contentPanel.add(placeholder, "REPORT_PATIENT");
        }
        
        try {
            ScheduleReportPanel scheduleReportPanel = new ScheduleReportPanel();
            scheduleReportPanel.setName("REPORT_SCHEDULE");
            contentPanel.add(scheduleReportPanel, "REPORT_SCHEDULE");
            System.out.println("MainFrame: Added REPORT_SCHEDULE card");
        } catch (Exception e) {
            System.err.println("MainFrame: Error creating ScheduleReportPanel: " + e.getMessage());
            e.printStackTrace();
            JPanel placeholder = createPlaceholderPanel();
            placeholder.setName("REPORT_SCHEDULE");
            contentPanel.add(placeholder, "REPORT_SCHEDULE");
        }
        
        try {
            RevenueReportPanel revenueReportPanel = new RevenueReportPanel();
            revenueReportPanel.setName("REPORT_REVENUE");
            contentPanel.add(revenueReportPanel, "REPORT_REVENUE");
            System.out.println("MainFrame: Added REPORT_REVENUE card");
        } catch (Exception e) {
            System.err.println("MainFrame: Error creating RevenueReportPanel: " + e.getMessage());
            e.printStackTrace();
            JPanel placeholder = createPlaceholderPanel();
            placeholder.setName("REPORT_REVENUE");
            contentPanel.add(placeholder, "REPORT_REVENUE");
        }
        System.out.println("MainFrame: Added REPORTS cards");

        // =====================================================
        // STAFF SCREENS (Admin only)
        // =====================================================
        try {
            StaffListPanel staffListPanel = new StaffListPanel();
            staffListPanel.setName("STAFF_LIST");
            contentPanel.add(staffListPanel, "STAFF_LIST");
            System.out.println("MainFrame: Added STAFF_LIST card");
        } catch (Exception e) {
            System.err.println("MainFrame: Error creating StaffListPanel: " + e.getMessage());
            e.printStackTrace();
            JPanel placeholder = createPlaceholderPanel();
            placeholder.setName("STAFF_LIST");
            contentPanel.add(placeholder, "STAFF_LIST");
        }
        
        try {
            AddStaffPanel addStaffPanel = new AddStaffPanel();
            addStaffPanel.setName("STAFF_ADD");
            contentPanel.add(addStaffPanel, "STAFF_ADD");
            System.out.println("MainFrame: Added STAFF_ADD card");
        } catch (Exception e) {
            System.err.println("MainFrame: Error creating AddStaffPanel: " + e.getMessage());
            e.printStackTrace();
            JPanel placeholder = createPlaceholderPanel();
            placeholder.setName("STAFF_ADD");
            contentPanel.add(placeholder, "STAFF_ADD");
        }
        
        JPanel staffDetailsPlaceholder = createPlaceholderPanel();
        staffDetailsPlaceholder.setName("STAFF_DETAILS");
        contentPanel.add(staffDetailsPlaceholder, "STAFF_DETAILS");
        System.out.println("MainFrame: Added STAFF_DETAILS card");
        System.out.println("MainFrame: Added STAFF cards");

        // =====================================================
        // DENTIST SCREENS (Admin only)
        // =====================================================
        try {
            AddDentistPanel addDentistPanel = new AddDentistPanel();
            addDentistPanel.setName("DENTIST_ADD");
            contentPanel.add(addDentistPanel, "DENTIST_ADD");
            System.out.println("MainFrame: Added DENTIST_ADD card");
        } catch (Exception e) {
            System.err.println("MainFrame: Error creating AddDentistPanel: " + e.getMessage());
            e.printStackTrace();
            JPanel placeholder = createPlaceholderPanel();
            placeholder.setName("DENTIST_ADD");
            contentPanel.add(placeholder, "DENTIST_ADD");
        }

        try {
            DentistListPanel dentistListPanel = new DentistListPanel();
            dentistListPanel.setName("DENTIST_LIST");
            contentPanel.add(dentistListPanel, "DENTIST_LIST");
            System.out.println("MainFrame: Added DENTIST_LIST card");
        } catch (Exception e) {
            System.err.println("MainFrame: Error creating DentistListPanel: " + e.getMessage());
            e.printStackTrace();
            JPanel placeholder = createPlaceholderPanel();
            placeholder.setName("DENTIST_LIST");
            contentPanel.add(placeholder, "DENTIST_LIST");
        }

        JPanel dentistDetailsPlaceholder = createPlaceholderPanel();
        dentistDetailsPlaceholder.setName("DENTIST_DETAILS");
        contentPanel.add(dentistDetailsPlaceholder, "DENTIST_DETAILS");
        System.out.println("MainFrame: Added DENTIST_DETAILS card");
        System.out.println("MainFrame: Added DENTIST cards");

        // =====================================================
        // TREATMENT SCREENS
        // =====================================================
        try {
            AddTreatmentPanel addTreatmentPanel = new AddTreatmentPanel();
            addTreatmentPanel.setName("TREATMENT_ADD");
            contentPanel.add(addTreatmentPanel, "TREATMENT_ADD");
            System.out.println("MainFrame: Added TREATMENT_ADD card");
        } catch (Exception e) {
            System.err.println("MainFrame: Error creating AddTreatmentPanel: " + e.getMessage());
            e.printStackTrace();
            JPanel placeholder = createPlaceholderPanel();
            placeholder.setName("TREATMENT_ADD");
            contentPanel.add(placeholder, "TREATMENT_ADD");
        }

        try {
            TreatmentListPanel treatmentListPanel = new TreatmentListPanel();
            treatmentListPanel.setName("TREATMENT_LIST");
            contentPanel.add(treatmentListPanel, "TREATMENT_LIST");
            System.out.println("MainFrame: Added TREATMENT_LIST card");
        } catch (Exception e) {
            System.err.println("MainFrame: Error creating TreatmentListPanel: " + e.getMessage());
            e.printStackTrace();
            JPanel placeholder = createPlaceholderPanel();
            placeholder.setName("TREATMENT_LIST");
            contentPanel.add(placeholder, "TREATMENT_LIST");
        }

        JPanel treatmentDetailsPlaceholder = createPlaceholderPanel();
        treatmentDetailsPlaceholder.setName("TREATMENT_DETAILS");
        contentPanel.add(treatmentDetailsPlaceholder, "TREATMENT_DETAILS");
        System.out.println("MainFrame: Added TREATMENT_DETAILS card");
        System.out.println("MainFrame: Added TREATMENT cards");

        // =====================================================
        // AUDIT SCREENS (Admin only)
        // =====================================================
        try {
            ActivityLogPanel activityLogPanel = new ActivityLogPanel();
            activityLogPanel.setName("AUDIT_ACTIVITY");
            contentPanel.add(activityLogPanel, "AUDIT_ACTIVITY");
            System.out.println("MainFrame: Added AUDIT_ACTIVITY card");
        } catch (Exception e) {
            System.err.println("MainFrame: Error creating ActivityLogPanel: " + e.getMessage());
            e.printStackTrace();
            JPanel placeholder = createPlaceholderPanel();
            placeholder.setName("AUDIT_ACTIVITY");
            contentPanel.add(placeholder, "AUDIT_ACTIVITY");
        }
        
        try {
            LoginHistoryPanel loginHistoryPanel = new LoginHistoryPanel();
            loginHistoryPanel.setName("AUDIT_LOGIN");
            contentPanel.add(loginHistoryPanel, "AUDIT_LOGIN");
            System.out.println("MainFrame: Added AUDIT_LOGIN card");
        } catch (Exception e) {
            System.err.println("MainFrame: Error creating LoginHistoryPanel: " + e.getMessage());
            e.printStackTrace();
            JPanel placeholder = createPlaceholderPanel();
            placeholder.setName("AUDIT_LOGIN");
            contentPanel.add(placeholder, "AUDIT_LOGIN");
        }
        System.out.println("MainFrame: Added AUDIT cards");

        // =====================================================
        // HELP SCREEN
        // =====================================================
        try {
            HelpPanel helpPanel = new HelpPanel();
            helpPanel.setName("HELP");
            contentPanel.add(helpPanel, "HELP");
            System.out.println("MainFrame: Added HELP card");
        } catch (Exception e) {
            System.err.println("MainFrame: Error creating HelpPanel: " + e.getMessage());
            e.printStackTrace();
            JPanel placeholder = createPlaceholderPanel();
            placeholder.setName("HELP");
            contentPanel.add(placeholder, "HELP");
        }

        // Show dashboard by default
        cardLayout.show(contentPanel, "DASHBOARD");
        System.out.println("MainFrame: Content panel initialized, showing DASHBOARD");
    }

    /**
     * Create a simple placeholder panel with no title
     */
    private JPanel createPlaceholderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(Color.WHITE);
        
        JLabel placeholderLabel = new JLabel("Loading...", SwingConstants.CENTER);
        placeholderLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        placeholderLabel.setForeground(new Color(122, 138, 135));
        centerPanel.add(placeholderLabel);
        
        panel.add(centerPanel, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createDashboardPlaceholder() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(Color.WHITE);
        
        JLabel placeholderLabel = new JLabel("Loading dashboard...", SwingConstants.CENTER);
        placeholderLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        placeholderLabel.setForeground(new Color(122, 138, 135));
        centerPanel.add(placeholderLabel);
        
        panel.add(centerPanel, BorderLayout.CENTER);
        
        return panel;
    }

    /**
     * Configure sidebar for a specific user role and set appropriate dashboard
     * @param role The user role
     */
    public void configureSidebarForRole(UserRole role) {
        sidebarPanel.configureForRole(role);
        
        User currentUser = LoginSession.getInstance().getCurrentUser();
        if (currentUser != null) {
            topBarPanel.setUserInfo(currentUser.getUsername(), currentUser.getRole().name());
        }
        
        setDashboardForRole(role);
    }

    /**
     * Set the appropriate dashboard based on user role
     * @param role The user role
     */
    private void setDashboardForRole(UserRole role) {
        removeDashboardCard();
        
        JPanel dashboardPanel = null;
        
        if (role == UserRole.ADMIN) {
            try {
                AdminDashboardPanel adminDashboard = new AdminDashboardPanel();
                adminDashboard.setName("DASHBOARD");
                dashboardPanel = adminDashboard;
                System.out.println("MainFrame: Added Admin Dashboard");
            } catch (Exception e) {
                System.err.println("MainFrame: Error creating AdminDashboardPanel: " + e.getMessage());
                e.printStackTrace();
                dashboardPanel = createDashboardPlaceholder();
                dashboardPanel.setName("DASHBOARD");
            }
        } else if (role == UserRole.RECEPTION) {
            try {
                ReceptionDashboardPanel receptionDashboard = new ReceptionDashboardPanel();
                receptionDashboard.setName("DASHBOARD");
                dashboardPanel = receptionDashboard;
                System.out.println("MainFrame: Added Reception Dashboard");
            } catch (Exception e) {
                System.err.println("MainFrame: Error creating ReceptionDashboardPanel: " + e.getMessage());
                e.printStackTrace();
                dashboardPanel = createDashboardPlaceholder();
                dashboardPanel.setName("DASHBOARD");
            }
        } else if (role == UserRole.DENTIST) {
            try {
                DentistDashboardPanel dentistDashboard = new DentistDashboardPanel();
                dentistDashboard.setName("DASHBOARD");
                dashboardPanel = dentistDashboard;
                System.out.println("MainFrame: Added Dentist Dashboard");
            } catch (Exception e) {
                System.err.println("MainFrame: Error creating DentistDashboardPanel: " + e.getMessage());
                e.printStackTrace();
                dashboardPanel = createDashboardPlaceholder();
                dashboardPanel.setName("DASHBOARD");
            }
        } else if (role == UserRole.PATIENT) {
            try {
                PatientDashboardPanel patientDashboard = new PatientDashboardPanel();
                patientDashboard.setName("DASHBOARD");
                dashboardPanel = patientDashboard;
                System.out.println("MainFrame: Added Patient Dashboard");
            } catch (Exception e) {
                System.err.println("MainFrame: Error creating PatientDashboardPanel: " + e.getMessage());
                e.printStackTrace();
                dashboardPanel = createDashboardPlaceholder();
                dashboardPanel.setName("DASHBOARD");
            }
        } else {
            dashboardPanel = createDashboardPlaceholder();
            dashboardPanel.setName("DASHBOARD");
            System.out.println("MainFrame: Added default dashboard for " + role);
        }
        
        if (dashboardPanel != null) {
            contentPanel.add(dashboardPanel, "DASHBOARD");
            contentPanel.revalidate();
            contentPanel.repaint();
            showCard("DASHBOARD");
        }
    }

    private void removeDashboardCard() {
        for (Component comp : contentPanel.getComponents()) {
            if (comp.getName() != null && comp.getName().equals("DASHBOARD")) {
                contentPanel.remove(comp);
                break;
            }
        }
    }

    /**
     * Show a specific card in the content panel
     * @param cardName The name of the card to show
     */
    public void showCard(String cardName) {
        System.out.println("MainFrame: Attempting to show card: " + cardName);
        
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
     * Get the top bar panel
     * @return The top bar panel
     */
    public TopBarPanel getTopBarPanel() {
        return topBarPanel;
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
            LoginSession.getInstance().logout();
            this.dispose();
            
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