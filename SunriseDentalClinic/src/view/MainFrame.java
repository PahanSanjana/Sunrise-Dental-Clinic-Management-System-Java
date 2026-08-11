package view;

import model.User;
import model.User.UserRole;
import java.awt.*;
import javax.swing.*;

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
        
        // Appointment List
        try {
            AppointmentListPanel appointmentListPanel = new AppointmentListPanel();
            appointmentListPanel.setName("APPOINTMENT_LIST");
            contentPanel.add(appointmentListPanel, "APPOINTMENT_LIST");
            System.out.println("MainFrame: Added APPOINTMENT_LIST card");
        } catch (Exception e) {
            System.err.println("MainFrame: Error creating AppointmentListPanel: " + e.getMessage());
            e.printStackTrace();
            JPanel placeholder = createPlaceholderPanel("Appointment List");
            placeholder.setName("APPOINTMENT_LIST");
            contentPanel.add(placeholder, "APPOINTMENT_LIST");
        }
        
        // Book Appointment
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
        
        // Appointment Details - Placeholder (will be replaced dynamically)
        JPanel appointmentDetails = createPlaceholderPanel("Appointment Details");
        appointmentDetails.setName("APPOINTMENT_DETAILS");
        contentPanel.add(appointmentDetails, "APPOINTMENT_DETAILS");
        
        // Daily Schedule
        try {
            DailySchedulePanel dailySchedulePanel = new DailySchedulePanel();
            dailySchedulePanel.setName("APPOINTMENT_SCHEDULE");
            contentPanel.add(dailySchedulePanel, "APPOINTMENT_SCHEDULE");
            dailySchedulePanel.loadScheduleData();
            System.out.println("MainFrame: Added APPOINTMENT_SCHEDULE card");
        } catch (Exception e) {
            System.err.println("MainFrame: Error creating DailySchedulePanel: " + e.getMessage());
            e.printStackTrace();
            JPanel placeholder = createPlaceholderPanel("Daily Schedule");
            placeholder.setName("APPOINTMENT_SCHEDULE");
            contentPanel.add(placeholder, "APPOINTMENT_SCHEDULE");
        }
        System.out.println("MainFrame: Added APPOINTMENT cards");

        // =====================================================
        // BILLING SCREENS
        // =====================================================

        // Bill List
        try {
            BillListPanel billListPanel = new BillListPanel();
            billListPanel.setName("BILL_LIST");
            contentPanel.add(billListPanel, "BILL_LIST");
            System.out.println("MainFrame: Added BILL_LIST card");
        } catch (Exception e) {
            System.err.println("MainFrame: Error creating BillListPanel: " + e.getMessage());
            e.printStackTrace();
            JPanel placeholder = createPlaceholderPanel("Bill List");
            placeholder.setName("BILL_LIST");
            contentPanel.add(placeholder, "BILL_LIST");
        }

        // Generate Bill
        try {
            GenerateBillPanel generateBillPanel = new GenerateBillPanel();
            generateBillPanel.setName("BILL_GENERATE");
            contentPanel.add(generateBillPanel, "BILL_GENERATE");
            System.out.println("MainFrame: Added BILL_GENERATE card");
        } catch (Exception e) {
            System.err.println("MainFrame: Error creating GenerateBillPanel: " + e.getMessage());
            e.printStackTrace();
            JPanel placeholder = createPlaceholderPanel("Generate Bill");
            placeholder.setName("BILL_GENERATE");
            contentPanel.add(placeholder, "BILL_GENERATE");
        }

        // Bill Details - Placeholder (will be replaced dynamically)
        JPanel billDetailsPlaceholder = createPlaceholderPanel("Bill Details");
        billDetailsPlaceholder.setName("BILL_DETAILS");
        contentPanel.add(billDetailsPlaceholder, "BILL_DETAILS");
        System.out.println("MainFrame: Added BILL_DETAILS card");
        System.out.println("MainFrame: Added BILLING cards");

        // =====================================================
        // REPORTS SCREENS
        // =====================================================
// Report Dashboard - Real Implementation
try {
    ReportDashboardPanel reportDashboardPanel = new ReportDashboardPanel();
    reportDashboardPanel.setName("REPORT_DASHBOARD");
    contentPanel.add(reportDashboardPanel, "REPORT_DASHBOARD");
    System.out.println("MainFrame: Added REPORT_DASHBOARD card");
} catch (Exception e) {
    System.err.println("MainFrame: Error creating ReportDashboardPanel: " + e.getMessage());
    e.printStackTrace();
    JPanel placeholder = createPlaceholderPanel("Report Dashboard");
    placeholder.setName("REPORT_DASHBOARD");
    contentPanel.add(placeholder, "REPORT_DASHBOARD");
}
// Revenue Report - Real Implementation
try {
    RevenueReportPanel revenueReportPanel = new RevenueReportPanel();
    revenueReportPanel.setName("REPORT_REVENUE");
    contentPanel.add(revenueReportPanel, "REPORT_REVENUE");
    System.out.println("MainFrame: Added REPORT_REVENUE card");
} catch (Exception e) {
    System.err.println("MainFrame: Error creating RevenueReportPanel: " + e.getMessage());
    e.printStackTrace();
    JPanel placeholder = createPlaceholderPanel("Revenue Report");
    placeholder.setName("REPORT_REVENUE");
    contentPanel.add(placeholder, "REPORT_REVENUE");
}
// Schedule Report - Real Implementation
try {
    ScheduleReportPanel scheduleReportPanel = new ScheduleReportPanel();
    scheduleReportPanel.setName("REPORT_SCHEDULE");
    contentPanel.add(scheduleReportPanel, "REPORT_SCHEDULE");
    System.out.println("MainFrame: Added REPORT_SCHEDULE card");
} catch (Exception e) {
    System.err.println("MainFrame: Error creating ScheduleReportPanel: " + e.getMessage());
    e.printStackTrace();
    JPanel placeholder = createPlaceholderPanel("Schedule Report");
    placeholder.setName("REPORT_SCHEDULE");
    contentPanel.add(placeholder, "REPORT_SCHEDULE");
}       
// Patient Report - Real Implementation
try {
    PatientReportPanel patientReportPanel = new PatientReportPanel();
    patientReportPanel.setName("REPORT_PATIENT");
    contentPanel.add(patientReportPanel, "REPORT_PATIENT");
    System.out.println("MainFrame: Added REPORT_PATIENT card");
} catch (Exception e) {
    System.err.println("MainFrame: Error creating PatientReportPanel: " + e.getMessage());
    e.printStackTrace();
    JPanel placeholder = createPlaceholderPanel("Patient Report");
    placeholder.setName("REPORT_PATIENT");
    contentPanel.add(placeholder, "REPORT_PATIENT");
}
        System.out.println("MainFrame: Added REPORTS cards");

        // =====================================================
        // STAFF SCREENS (Admin only)
        // =====================================================
        
        // Staff List
        try {
            StaffListPanel staffListPanel = new StaffListPanel();
            staffListPanel.setName("STAFF_LIST");
            contentPanel.add(staffListPanel, "STAFF_LIST");
            System.out.println("MainFrame: Added STAFF_LIST card");
        } catch (Exception e) {
            System.err.println("MainFrame: Error creating StaffListPanel: " + e.getMessage());
            e.printStackTrace();
            JPanel placeholder = createPlaceholderPanel("Staff List");
            placeholder.setName("STAFF_LIST");
            contentPanel.add(placeholder, "STAFF_LIST");
        }
        
        // Add Staff
        try {
            AddStaffPanel addStaffPanel = new AddStaffPanel();
            addStaffPanel.setName("STAFF_ADD");
            contentPanel.add(addStaffPanel, "STAFF_ADD");
            System.out.println("MainFrame: Added STAFF_ADD card");
        } catch (Exception e) {
            System.err.println("MainFrame: Error creating AddStaffPanel: " + e.getMessage());
            e.printStackTrace();
            JPanel placeholder = createPlaceholderPanel("Add Staff");
            placeholder.setName("STAFF_ADD");
            contentPanel.add(placeholder, "STAFF_ADD");
        }
        
        // Staff Details - Placeholder (will be replaced dynamically)
        JPanel staffDetailsPlaceholder = createPlaceholderPanel("Staff Details");
        staffDetailsPlaceholder.setName("STAFF_DETAILS");
        contentPanel.add(staffDetailsPlaceholder, "STAFF_DETAILS");
        System.out.println("MainFrame: Added STAFF_DETAILS card");
        System.out.println("MainFrame: Added STAFF cards");

        // =====================================================
        // DENTIST SCREENS (Admin only)
        // =====================================================
        
        // Add Dentist
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

        // Dentist List
        try {
            DentistListPanel dentistListPanel = new DentistListPanel();
            dentistListPanel.setName("DENTIST_LIST");
            contentPanel.add(dentistListPanel, "DENTIST_LIST");
            System.out.println("MainFrame: Added DENTIST_LIST card");
        } catch (Exception e) {
            System.err.println("MainFrame: Error creating DentistListPanel: " + e.getMessage());
            e.printStackTrace();
            JPanel placeholder = createPlaceholderPanel("Dentist List");
            placeholder.setName("DENTIST_LIST");
            contentPanel.add(placeholder, "DENTIST_LIST");
        }

        // Dentist Details - Placeholder (will be replaced dynamically)
        JPanel dentistDetailsPlaceholder = createPlaceholderPanel("Dentist Details");
        dentistDetailsPlaceholder.setName("DENTIST_DETAILS");
        contentPanel.add(dentistDetailsPlaceholder, "DENTIST_DETAILS");
        System.out.println("MainFrame: Added DENTIST_DETAILS card");
        System.out.println("MainFrame: Added DENTIST cards");

        // =====================================================
        // TREATMENT SCREENS
        // =====================================================
        
        // Add Treatment
        try {
            AddTreatmentPanel addTreatmentPanel = new AddTreatmentPanel();
            addTreatmentPanel.setName("TREATMENT_ADD");
            contentPanel.add(addTreatmentPanel, "TREATMENT_ADD");
            System.out.println("MainFrame: Added TREATMENT_ADD card");
        } catch (Exception e) {
            System.err.println("MainFrame: Error creating AddTreatmentPanel: " + e.getMessage());
            e.printStackTrace();
            JPanel placeholder = createPlaceholderPanel("Add Treatment");
            placeholder.setName("TREATMENT_ADD");
            contentPanel.add(placeholder, "TREATMENT_ADD");
        }

        // Treatment List
        try {
            TreatmentListPanel treatmentListPanel = new TreatmentListPanel();
            treatmentListPanel.setName("TREATMENT_LIST");
            contentPanel.add(treatmentListPanel, "TREATMENT_LIST");
            System.out.println("MainFrame: Added TREATMENT_LIST card");
        } catch (Exception e) {
            System.err.println("MainFrame: Error creating TreatmentListPanel: " + e.getMessage());
            e.printStackTrace();
            JPanel placeholder = createPlaceholderPanel("Treatment List");
            placeholder.setName("TREATMENT_LIST");
            contentPanel.add(placeholder, "TREATMENT_LIST");
        }

        // Treatment Details - Placeholder (will be replaced dynamically)
        JPanel treatmentDetailsPlaceholder = createPlaceholderPanel("Treatment Details");
        treatmentDetailsPlaceholder.setName("TREATMENT_DETAILS");
        contentPanel.add(treatmentDetailsPlaceholder, "TREATMENT_DETAILS");
        System.out.println("MainFrame: Added TREATMENT_DETAILS card");
        System.out.println("MainFrame: Added TREATMENT cards");

        // =====================================================
        // AUDIT SCREENS (Admin only)
        // =====================================================
// Activity Log - Real Implementation
try {
    ActivityLogPanel activityLogPanel = new ActivityLogPanel();
    activityLogPanel.setName("AUDIT_ACTIVITY");
    contentPanel.add(activityLogPanel, "AUDIT_ACTIVITY");
    System.out.println("MainFrame: Added AUDIT_ACTIVITY card");
} catch (Exception e) {
    System.err.println("MainFrame: Error creating ActivityLogPanel: " + e.getMessage());
    e.printStackTrace();
    JPanel placeholder = createPlaceholderPanel("Activity Log");
    placeholder.setName("AUDIT_ACTIVITY");
    contentPanel.add(placeholder, "AUDIT_ACTIVITY");
}        contentPanel.add(createPlaceholderPanel("Login History"), "AUDIT_LOGIN");

        System.out.println("MainFrame: Added AUDIT cards");

        // Show dashboard by default
        cardLayout.show(contentPanel, "DASHBOARD");
        System.out.println("MainFrame: Content panel initialized, showing DASHBOARD");
        
        
        // Help - Real Implementation
try {
    HelpPanel helpPanel = new HelpPanel();
    helpPanel.setName("HELP");
    contentPanel.add(helpPanel, "HELP");
    System.out.println("MainFrame: Added HELP card");
} catch (Exception e) {
    System.err.println("MainFrame: Error creating HelpPanel: " + e.getMessage());
    e.printStackTrace();
    JPanel placeholder = createPlaceholderPanel("Help");
    placeholder.setName("HELP");
    contentPanel.add(placeholder, "HELP");
}
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

    public JPanel getContentPanel() {
        return contentPanel;
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