package view;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class HelpPanel extends JPanel {
    
    // Color Palette
    private static final Color PRIMARY_DARK = new Color(0x2F3E3C);
    private static final Color MINT = new Color(0xBDDBD1);
    private static final Color SOFT_SURFACE = new Color(0xFBF9F1);
    private static final Color LIGHT_SURFACE = new Color(0xE7E9E3);
    private static final Color HOVER_SURFACE = new Color(0xE8F0F1);
    private static final Color SECONDARY_TEXT = new Color(122, 138, 135);

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

    private JPanel contentPanel;
    private JButton[] navButtons;
    private String[] sectionTitles = {
        "Getting Started",
        "User Roles",
        "Patient Management",
        "Appointment Management",
        "Dentist Management",
        "Staff Management",
        "Treatment Management",
        "Billing & Payments",
        "Reports",
        "FAQ",
        "Support"
    };

    // Auto-refresh timer (hidden)
    private Timer refreshTimer;
    private static final int AUTO_REFRESH_DELAY = 30000; // 30 seconds

    public HelpPanel() {
        initComponents();
        loadContent();
        showSection(0);
        startAutoRefresh();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(SOFT_SURFACE);
        setBorder(new EmptyBorder(20, 30, 20, 30));

        // Header
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Main Content
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createLineBorder(LIGHT_SURFACE, 1));
        
        // Navigation Sidebar
        mainPanel.add(createNavigationPanel(), BorderLayout.WEST);
        
        // Content Area
        contentPanel = new JPanel(new CardLayout());
        contentPanel.setBackground(Color.WHITE);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);
    }

    // =====================================================
    // AUTO-REFRESH (Hidden - No UI Indicator)
    // =====================================================

    private void startAutoRefresh() {
        if (refreshTimer == null) {
            refreshTimer = new Timer(AUTO_REFRESH_DELAY, e -> {
                if (isShowing()) {
                    // Auto-refresh logic - e.g., check for updates
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

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(SOFT_SURFACE);
        header.setBorder(new EmptyBorder(0, 0, 15, 0));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Help Center");
        titleLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_DARK);
        
        JLabel subtitleLabel = new JLabel("Learn how to use Sunrise Dental Clinic Management System");
        subtitleLabel.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(107, 123, 121));
        
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);

        header.add(titlePanel, BorderLayout.WEST);
        return header;
    }

    private JPanel createNavigationPanel() {
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(SOFT_SURFACE);
        navPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        navPanel.setPreferredSize(new Dimension(200, 0));

        JLabel navTitle = iconLabel(FontAwesomeSolid.BOOK, 16, PRIMARY_DARK);
        navTitle.setText(" Topics");
        navTitle.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 16));
        navTitle.setForeground(PRIMARY_DARK);
        navTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        navPanel.add(navTitle);
        navPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        navButtons = new JButton[sectionTitles.length];
        for (int i = 0; i < sectionTitles.length; i++) {
            final int index = i;
            JButton btn = createNavButton(sectionTitles[i]);
            btn.addActionListener(e -> showSection(index));
            navButtons[i] = btn;
            navPanel.add(btn);
            navPanel.add(Box.createRigidArea(new Dimension(0, 2)));
        }

        JScrollPane scrollPane = new JScrollPane(navPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setPreferredSize(new Dimension(220, 0));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Wrap scroll pane in a panel
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(SOFT_SURFACE);
        wrapperPanel.add(scrollPane, BorderLayout.CENTER);
        
        return wrapperPanel;
    }

    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        btn.setBackground(SOFT_SURFACE);
        btn.setForeground(PRIMARY_DARK);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(HOVER_SURFACE);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(SOFT_SURFACE);
            }
        });
        return btn;
    }

    private void showSection(int index) {
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, String.valueOf(index));
        
        // Update button styles
        for (int i = 0; i < navButtons.length; i++) {
            if (i == index) {
                navButtons[i].setBackground(MINT);
                navButtons[i].setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 13));
            } else {
                navButtons[i].setBackground(SOFT_SURFACE);
                navButtons[i].setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
            }
        }
    }

    private void loadContent() {
        for (int i = 0; i < sectionTitles.length; i++) {
            JPanel panel = createSectionPanel(i);
            contentPanel.add(panel, String.valueOf(i));
        }
    }

    private JPanel createSectionPanel(int index) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(25, 30, 25, 30));

        switch (index) {
            case 0: // Getting Started
                panel.add(createGettingStartedContent());
                break;
            case 1: // User Roles
                panel.add(createUserRolesContent());
                break;
            case 2: // Patient Management
                panel.add(createPatientManagementContent());
                break;
            case 3: // Appointment Management
                panel.add(createAppointmentManagementContent());
                break;
            case 4: // Dentist Management
                panel.add(createDentistManagementContent());
                break;
            case 5: // Staff Management
                panel.add(createStaffManagementContent());
                break;
            case 6: // Treatment Management
                panel.add(createTreatmentManagementContent());
                break;
            case 7: // Billing & Payments
                panel.add(createBillingContent());
                break;
            case 8: // Reports
                panel.add(createReportsContent());
                break;
            case 9: // FAQ
                panel.add(createFAQContent());
                break;
            case 10: // Support
                panel.add(createSupportContent());
                break;
        }

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.add(scrollPane, BorderLayout.CENTER);
        return wrapper;
    }

    // =====================================================
    // CONTENT CREATION METHODS
    // =====================================================

    private JPanel createGettingStartedContent() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 0, 10, 0));

        panel.add(createSectionTitle("Welcome to Sunrise Dental Clinic Management System"));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        panel.add(createParagraph("Sunrise Dental Clinic Management System is a comprehensive solution designed to streamline dental clinic operations. This guide will help you understand the key features and functionalities of the system."));
        
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(createSubSectionTitle("System Overview"));
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        
        String[] overviewItems = {
            "• Dashboard - View clinic statistics and key metrics at a glance",
            "• Patient Management - Add, view, edit, and manage patient records",
            "• Appointment Management - Schedule, view, and manage appointments",
            "• Dentist Management - Manage dentist profiles and availability",
            "• Staff Management - Manage staff members and their roles",
            "• Treatment Management - Create and manage treatment catalog",
            "• Billing & Payments - Generate and manage bills and payments",
            "• Reports - Generate various reports for analysis"
        };
        
        for (String item : overviewItems) {
            panel.add(createListItem(item));
            panel.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        return panel;
    }

    private JPanel createUserRolesContent() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 0, 10, 0));

        panel.add(createSectionTitle("User Roles"));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createParagraph("The system has four different user roles with specific permissions:"));

        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Admin
        panel.add(createSubSectionTitle("1. Administrator"));
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(createParagraph("Full access to all system features."));
        panel.add(createBulletList(new String[]{
            "Manage all users and roles",
            "Access all reports and analytics",
            "Configure system settings",
            "View audit logs",
            "Full CRUD operations on all modules"
        }));

        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Receptionist
        panel.add(createSubSectionTitle("2. Receptionist"));
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(createParagraph("Front desk staff with patient management responsibilities."));
        panel.add(createBulletList(new String[]{
            "Manage patient records (Add, Edit, View)",
            "Schedule and manage appointments",
            "Generate bills",
            "View patient history"
        }));

        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Dentist
        panel.add(createSubSectionTitle("3. Dentist"));
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(createParagraph("Dental professionals with clinical access."));
        panel.add(createBulletList(new String[]{
            "View patient records",
            "Manage appointments",
            "View treatment plans",
            "Access patient reports"
        }));

        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Patient
        panel.add(createSubSectionTitle("4. Patient"));
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(createParagraph("Patients with self-service access."));
        panel.add(createBulletList(new String[]{
            "Book appointments",
            "View appointment details",
            "View treatment history",
            "View billing details"
        }));

        return panel;
    }

    private JPanel createPatientManagementContent() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 0, 10, 0));

        panel.add(createSectionTitle("Patient Management"));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createParagraph("Comprehensive patient management system to handle all patient-related operations."));

        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        panel.add(createSubSectionTitle("Features"));
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(createBulletList(new String[]{
            "Add New Patient - Register new patients with complete information",
            "Patient List - View and search all patients",
            "Patient Details - View complete patient profile",
            "Edit Patient - Update patient information",
            "Patient History - View appointments, treatments, and bills"
        }));

        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        panel.add(createSubSectionTitle("Patient Information Fields"));
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(createBulletList(new String[]{
            "Personal Information: Name, Gender, Date of Birth",
            "Contact Information: Phone, Email, Address",
            "Emergency Contact: Name and Phone",
            "Medical Information: Medical History, Allergies"
        }));

        return panel;
    }

    private JPanel createAppointmentManagementContent() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 0, 10, 0));

        panel.add(createSectionTitle("Appointment Management"));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createParagraph("Efficiently manage all appointments with the scheduling system."));

        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        panel.add(createSubSectionTitle("Features"));
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(createBulletList(new String[]{
            "Book Appointment - Schedule new appointments",
            "Appointment List - View and search all appointments",
            "Daily Schedule - View appointments by day",
            "Appointment Details - View complete appointment information",
            "Edit/Cancel Appointment - Modify or cancel appointments"
        }));

        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        panel.add(createSubSectionTitle("Appointment Status"));
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(createBulletList(new String[]{
            "Scheduled - Appointment is booked",
            "Confirmed - Appointment is confirmed",
            "In Progress - Appointment is ongoing",
            "Completed - Appointment is finished",
            "Cancelled - Appointment is cancelled",
            "No Show - Patient didn't attend"
        }));

        return panel;
    }

    private JPanel createDentistManagementContent() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 0, 10, 0));

        panel.add(createSectionTitle("Dentist Management"));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createParagraph("Manage dentist profiles, specializations, and availability."));

        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        panel.add(createSubSectionTitle("Features"));
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(createBulletList(new String[]{
            "Add Dentist - Register new dentists",
            "Dentist List - View and search all dentists",
            "Dentist Details - View complete dentist profile",
            "Edit Dentist - Update dentist information",
            "Toggle Availability - Change dentist availability status"
        }));

        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        panel.add(createSubSectionTitle("Dentist Information"));
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(createBulletList(new String[]{
            "Personal Information: Name, Specialization",
            "Professional: License Number, Years of Experience",
            "Contact: Phone, Email",
            "Schedule: Working Hours, Consultation Fee",
            "Availability: Available/Unavailable status"
        }));

        return panel;
    }

    private JPanel createStaffManagementContent() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 0, 10, 0));

        panel.add(createSectionTitle("Staff Management"));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createParagraph("Manage all staff members and their roles in the clinic."));

        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        panel.add(createSubSectionTitle("Features"));
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(createBulletList(new String[]{
            "Add Staff - Register new staff members",
            "Staff List - View and search all staff",
            "Staff Details - View complete staff profile",
            "Edit Staff - Update staff information",
            "Toggle Status - Change staff active/inactive status"
        }));

        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        panel.add(createSubSectionTitle("Staff Information"));
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(createBulletList(new String[]{
            "Personal Information: Name, Position, Department",
            "Employment: Hire Date, Salary",
            "Contact: Phone, Email",
            "Status: Active/Inactive"
        }));

        return panel;
    }

    private JPanel createTreatmentManagementContent() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 0, 10, 0));

        panel.add(createSectionTitle("Treatment Management"));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createParagraph("Create and manage the treatment catalog."));

        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        panel.add(createSubSectionTitle("Features"));
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(createBulletList(new String[]{
            "Add Treatment - Create new treatments",
            "Treatment List - View and search all treatments",
            "Treatment Details - View complete treatment information",
            "Edit Treatment - Update treatment information",
            "Toggle Status - Change treatment active/inactive status"
        }));

        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        panel.add(createSubSectionTitle("Treatment Information"));
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(createBulletList(new String[]{
            "Treatment Name, Description, Category",
            "Cost and Duration",
            "Active/Inactive Status"
        }));

        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        panel.add(createSubSectionTitle("Treatment Categories"));
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(createBulletList(new String[]{
            "Preventive - Cleanings, Fluoride treatments",
            "Restorative - Fillings, Crowns",
            "Endodontic - Root canals",
            "Orthodontic - Braces, Aligners",
            "Cosmetic - Whitening, Veneers",
            "Surgical - Extractions, Implants",
            "Periodontic - Gum treatments"
        }));

        return panel;
    }

    private JPanel createBillingContent() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 0, 10, 0));

        panel.add(createSectionTitle("Billing & Payments"));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createParagraph("Complete billing and payment management system."));

        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        panel.add(createSubSectionTitle("Features"));
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(createBulletList(new String[]{
            "Generate Bill - Create new bills with items",
            "Bill List - View and search all bills",
            "Bill Details - View complete bill information",
            "Edit Bill - Update bill information",
            "Mark as Paid - Update bill status",
            "Print Bill - Print bill for patient"
        }));

        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        panel.add(createSubSectionTitle("Bill Status"));
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(createBulletList(new String[]{
            "Draft - Bill in draft mode",
            "Pending - Bill awaiting payment",
            "Partial - Partially paid",
            "Paid - Fully paid",
            "Overdue - Payment past due",
            "Cancelled - Bill cancelled"
        }));

        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        panel.add(createSubSectionTitle("Payment Methods"));
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(createBulletList(new String[]{
            "Cash",
            "Credit Card",
            "Debit Card",
            "Insurance",
            "Bank Transfer",
            "Other"
        }));

        return panel;
    }

    private JPanel createReportsContent() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 0, 10, 0));

        panel.add(createSectionTitle("Reports"));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createParagraph("Generate various reports for analysis and decision making."));

        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        panel.add(createSubSectionTitle("Report Types"));
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(createBulletList(new String[]{
            "Patient Report - Complete patient history and statistics",
            "Schedule Report - Appointment statistics and analysis",
            "Revenue Report - Financial overview and revenue analysis"
        }));

        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        panel.add(createSubSectionTitle("Dashboard Metrics"));
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(createBulletList(new String[]{
            "Total Patients - Number of registered patients",
            "Total Appointments - All appointments in the system",
            "Total Revenue - Sum of all paid/partial bills",
            "Active Dentists - Number of available dentists",
            "Total Treatments - Number of treatments in catalog",
            "Recent Activity - Latest appointments and bills"
        }));

        return panel;
    }

    private JPanel createFAQContent() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 0, 10, 0));

        panel.add(createSectionTitle("Frequently Asked Questions"));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        String[][] faqs = {
            {"How do I add a new patient?", "Navigate to Patient Management → Add Patient. Fill in the patient details and click Save."},
            {"How do I book an appointment?", "Go to Appointment Management → Book Appointment. Select patient, dentist, date, and time."},
            {"How do I generate a bill?", "Go to Billing → Generate Bill. Select patient, add items, and click Generate Bill."},
            {"How do I view a patient's history?", "Go to Patient Management → Patient List → Select patient → View Details."},
            {"How do I change a dentist's availability?", "Go to Dentist Management → Dentist List → Select dentist → Toggle Status."},
            {"What do the different appointment statuses mean?", "Scheduled: Booked, Confirmed: Verified, In Progress: Ongoing, Completed: Done, Cancelled: Cancelled, No Show: Missed."},
            {"How do I generate a report?", "Go to Reports → Select report type → Choose filters → Click Generate Report."},
            {"How do I update staff information?", "Go to Staff Management → Staff List → Select staff → Edit."}
        };

        for (String[] faq : faqs) {
            panel.add(createFAQItem(faq[0], faq[1]));
            panel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        return panel;
    }

    private JPanel createSupportContent() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 0, 10, 0));

        panel.add(createSectionTitle("Support"));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createParagraph("Need help? Contact our support team."));

        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        panel.add(createSubSectionTitle("Contact Information"));
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(createContactRow(FontAwesomeSolid.ENVELOPE, "support@sunrisedental.com"));
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(createContactRow(FontAwesomeSolid.PHONE, "+1 (555) 123-4567"));
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(createContactRow(FontAwesomeSolid.CLOCK, "Monday - Friday, 8:00 AM - 6:00 PM"));

        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        panel.add(createSubSectionTitle("Quick Links"));
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(createBulletList(new String[]{
            "User Manual (PDF)",
            "Video Tutorials",
            "System Requirements",
            "Release Notes"
        }));

        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        panel.add(createSubSectionTitle("System Information"));
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(createParagraph("Version: 1.0.0"));
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(createParagraph("Last Updated: August 2026"));

        return panel;
    }

    // =====================================================
    // HELPER METHODS FOR CONTENT CREATION
    // =====================================================

    private JLabel createSectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 20));
        label.setForeground(PRIMARY_DARK);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JLabel createSubSectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 16));
        label.setForeground(PRIMARY_DARK);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JLabel createParagraph(String text) {
        JLabel label = new JLabel("<html><body style='width: 600px;'>" + text + "</body></html>");
        label.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 14));
        label.setForeground(SECONDARY_TEXT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JLabel createListItem(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 14));
        label.setForeground(SECONDARY_TEXT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JPanel createContactRow(FontAwesomeSolid glyph, String text) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel iconLabel = iconLabel(glyph, 16, PRIMARY_DARK);
        panel.add(iconLabel);
        
        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 14));
        textLabel.setForeground(SECONDARY_TEXT);
        panel.add(textLabel);
        
        return panel;
    }

    private JPanel createBulletList(String[] items) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        for (String item : items) {
            panel.add(createListItem("• " + item));
            panel.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        
        return panel;
    }

    private JPanel createFAQItem(String question, String answer) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(248, 249, 250));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            new EmptyBorder(12, 15, 12, 15)
        ));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(700, 100));

        JLabel qLabel = new JLabel("Q: " + question);
        qLabel.setFont(new Font(UI_FONT_FAMILY, Font.BOLD, 14));
        qLabel.setForeground(PRIMARY_DARK);
        qLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(qLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));

        JLabel aLabel = new JLabel("A: " + answer);
        aLabel.setFont(new Font(UI_FONT_FAMILY, Font.PLAIN, 13));
        aLabel.setForeground(SECONDARY_TEXT);
        aLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(aLabel);

        return panel;
    }
}