package view;

import controller.AppointmentController;
import model.Patient;
import model.Dentist;
import model.Treatment;
import model.Appointment;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BookAppointmentPanel extends JPanel {
    
    // Color Palette
    private static final Color PRIMARY_DARK = new Color(0x2F3E3C);
    private static final Color MINT = new Color(0xBDDBD1);
    private static final Color SOFT_SURFACE = new Color(0xFBF9F1);
    private static final Color LIGHT_SURFACE = new Color(0xE7E9E3);
    private static final Color HOVER_SURFACE = new Color(0xE8F0F1);
    private static final Color ERROR_COLOR = new Color(220, 80, 80);
    private static final Color SUCCESS_COLOR = new Color(60, 160, 80);
    private static final Color SECONDARY_TEXT = new Color(122, 138, 135);
    private static final Color SELECTED_COLOR = new Color(0xBDDBD1);

    // Components
    private JPanel contentPanel;
    private CardLayout cardLayout;
    
    // Step 1: Patient Selection
    private JTextField searchField;
    private JPanel patientListPanel;
    private JButton newPatientButton;
    private JLabel selectedPatientLabel;
    private JButton clearPatientButton;
    private Patient selectedPatient;
    
    // Step 2: Treatment Selection
    private JPanel treatmentGridPanel;
    private Treatment selectedTreatment;
    
    // Step 3: Dentist Selection
    private JPanel dentistGridPanel;
    private Dentist selectedDentist;
    
    // Step 4: Date & Time
    private JPanel calendarPanel;
    private JPanel timeSlotPanel;
    private JLabel monthLabel;
    private JButton prevMonthButton;
    private JButton nextMonthButton;
    private String selectedDate;
    private String selectedTime;
    private int currentMonth;
    private int currentYear;
    private Map<String, List<String>> bookedSlots;
    
    // Step 5: Confirmation
    private JTextArea notesArea;
    private JLabel confirmationPatient;
    private JLabel confirmationTreatment;
    private JLabel confirmationDentist;
    private JLabel confirmationDateTime;
    
    // Navigation
    private int currentStep = 1;
    private JButton backButton;
    private JButton nextButton;
    private JButton confirmButton;
    private JLabel stepIndicator;
    
    // Controller
    private AppointmentController controller;
    
    // Data
    private List<Patient> patients;
    private List<Dentist> dentists;
    private List<Treatment> treatments;
    
    // Time slots
    private static final String[] TIME_SLOTS = {
        "08:00", "08:30", "09:00", "09:30", "10:00", "10:30",
        "11:00", "11:30", "12:00", "12:30", "13:00", "13:30",
        "14:00", "14:30", "15:00", "15:30", "16:00", "16:30",
        "17:00", "17:30"
    };

    public BookAppointmentPanel() {
        this.controller = new AppointmentController(this);
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(SOFT_SURFACE);
        setBorder(new EmptyBorder(20, 30, 20, 30));

        // Header
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Content with CardLayout for steps
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(SOFT_SURFACE);
        contentPanel.setBorder(new EmptyBorder(20, 0, 20, 0));
        
        contentPanel.add(createPatientStep(), "STEP_1");
        contentPanel.add(createTreatmentStep(), "STEP_2");
        contentPanel.add(createDentistStep(), "STEP_3");
        contentPanel.add(createDateTimeStep(), "STEP_4");
        contentPanel.add(createConfirmationStep(), "STEP_5");
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Footer with navigation
        add(createFooterPanel(), BorderLayout.SOUTH);
        
        showStep(1);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(SOFT_SURFACE);
        header.setBorder(new EmptyBorder(0, 0, 15, 0));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Book Appointment");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_DARK);
        
        JLabel subtitleLabel = new JLabel("Schedule a new appointment for a patient");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(SECONDARY_TEXT);
        
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);

        // Step indicator
        stepIndicator = new JLabel("Step 1 of 5: Select Patient");
        stepIndicator.setFont(new Font("Segoe UI", Font.BOLD, 14));
        stepIndicator.setForeground(PRIMARY_DARK);
        
        header.add(titlePanel, BorderLayout.WEST);
        header.add(stepIndicator, BorderLayout.EAST);

        return header;
    }

    // ========================
    // STEP 1: Select Patient
    // ========================
    private JPanel createPatientStep() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        
        JLabel title = new JLabel("Select Patient");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(PRIMARY_DARK);
        header.add(title, BorderLayout.WEST);
        
        newPatientButton = createStyledButton("New Patient", PRIMARY_DARK, Color.WHITE);
        newPatientButton.setPreferredSize(new Dimension(130, 35));
        newPatientButton.addActionListener(e -> openNewPatient());
        header.add(newPatientButton, BorderLayout.EAST);
        
        panel.add(header, BorderLayout.NORTH);

        // Search
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setOpaque(false);
        searchPanel.setBorder(new EmptyBorder(15, 0, 15, 0));
        
        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterPatients(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterPatients(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterPatients(); }
        });
        
        JButton searchButton = createStyledButton("Search", PRIMARY_DARK, Color.WHITE);
        searchButton.setPreferredSize(new Dimension(100, 40));
        searchButton.addActionListener(e -> filterPatients());
        
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        
        panel.add(searchPanel, BorderLayout.CENTER);

        // Patient List
        patientListPanel = new JPanel();
        patientListPanel.setLayout(new BoxLayout(patientListPanel, BoxLayout.Y_AXIS));
        patientListPanel.setBackground(Color.WHITE);
        patientListPanel.setBorder(BorderFactory.createLineBorder(LIGHT_SURFACE, 1));
        
        JScrollPane scrollPane = new JScrollPane(patientListPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setPreferredSize(new Dimension(0, 400));
        
        panel.add(scrollPane, BorderLayout.SOUTH);

        return panel;
    }

    // ========================
    // STEP 2: Select Treatment
    // ========================
    private JPanel createTreatmentStep() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Select Treatment");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(PRIMARY_DARK);
        panel.add(title, BorderLayout.NORTH);

        treatmentGridPanel = new JPanel(new GridLayout(0, 3, 15, 15));
        treatmentGridPanel.setBackground(Color.WHITE);
        treatmentGridPanel.setBorder(new EmptyBorder(15, 0, 15, 0));
        
        JScrollPane scrollPane = new JScrollPane(treatmentGridPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // ========================
    // STEP 3: Select Dentist
    // ========================
    private JPanel createDentistStep() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Select Dentist");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(PRIMARY_DARK);
        panel.add(title, BorderLayout.NORTH);

        dentistGridPanel = new JPanel(new GridLayout(0, 3, 15, 15));
        dentistGridPanel.setBackground(Color.WHITE);
        dentistGridPanel.setBorder(new EmptyBorder(15, 0, 15, 0));
        
        JScrollPane scrollPane = new JScrollPane(dentistGridPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // ========================
    // STEP 4: Select Date & Time
    // ========================
    private JPanel createDateTimeStep() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Select Date & Time");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(PRIMARY_DARK);
        panel.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridLayout(1, 2, 20, 0));
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(15, 0, 15, 0));

        // Calendar Panel
        calendarPanel = createCalendarPanel();
        content.add(calendarPanel);

        // Time Slots Panel
        timeSlotPanel = createTimeSlotPanel();
        content.add(timeSlotPanel);

        panel.add(content, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createCalendarPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(LIGHT_SURFACE, 1));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(MINT);
        header.setBorder(new EmptyBorder(10, 15, 10, 15));
        
        prevMonthButton = new JButton("<");
        prevMonthButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        prevMonthButton.setBackground(MINT);
        prevMonthButton.setBorderPainted(false);
        prevMonthButton.setFocusPainted(false);
        prevMonthButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        prevMonthButton.addActionListener(e -> changeMonth(-1));
        
        monthLabel = new JLabel("", SwingConstants.CENTER);
        monthLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        monthLabel.setForeground(PRIMARY_DARK);
        
        nextMonthButton = new JButton(">");
        nextMonthButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nextMonthButton.setBackground(MINT);
        nextMonthButton.setBorderPainted(false);
        nextMonthButton.setFocusPainted(false);
        nextMonthButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        nextMonthButton.addActionListener(e -> changeMonth(1));
        
        header.add(prevMonthButton, BorderLayout.WEST);
        header.add(monthLabel, BorderLayout.CENTER);
        header.add(nextMonthButton, BorderLayout.EAST);
        
        panel.add(header, BorderLayout.NORTH);

        // Calendar Grid
        JPanel gridPanel = new JPanel(new GridLayout(0, 7, 5, 5));
        gridPanel.setBackground(Color.WHITE);
        gridPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        String[] days = {"S", "M", "T", "W", "T", "F", "S"};
        for (String day : days) {
            JLabel label = new JLabel(day, SwingConstants.CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 12));
            label.setForeground(SECONDARY_TEXT);
            gridPanel.add(label);
        }
        
        // We'll populate this dynamically
        gridPanel.setName("CALENDAR_GRID");
        
        panel.add(gridPanel, BorderLayout.CENTER);

        // Initialize current month/year
        LocalDate now = LocalDate.now();
        currentMonth = now.getMonthValue();
        currentYear = now.getYear();
        updateCalendar();

        return panel;
    }

    private JPanel createTimeSlotPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(LIGHT_SURFACE, 1));

        JLabel title = new JLabel("Available Time Slots", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(PRIMARY_DARK);
        title.setBorder(new EmptyBorder(10, 0, 10, 0));
        panel.add(title, BorderLayout.NORTH);

        JPanel slotsPanel = new JPanel(new GridLayout(0, 4, 8, 8));
        slotsPanel.setBackground(Color.WHITE);
        slotsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        slotsPanel.setName("TIME_SLOTS");
        
        JScrollPane scrollPane = new JScrollPane(slotsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // ========================
    // STEP 5: Confirmation
    // ========================
    private JPanel createConfirmationStep() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Confirm Booking");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(PRIMARY_DARK);
        panel.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(15, 0, 15, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.weightx = 1.0;

        // Patient
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        JLabel patientLabel = new JLabel("Patient:");
        patientLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        patientLabel.setForeground(PRIMARY_DARK);
        content.add(patientLabel, gbc);

        gbc.gridx = 1;
        confirmationPatient = new JLabel("--");
        confirmationPatient.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        content.add(confirmationPatient, gbc);

        // Treatment
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel treatmentLabel = new JLabel("Treatment:");
        treatmentLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        treatmentLabel.setForeground(PRIMARY_DARK);
        content.add(treatmentLabel, gbc);

        gbc.gridx = 1;
        confirmationTreatment = new JLabel("--");
        confirmationTreatment.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        content.add(confirmationTreatment, gbc);

        // Dentist
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel dentistLabel = new JLabel("Dentist:");
        dentistLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        dentistLabel.setForeground(PRIMARY_DARK);
        content.add(dentistLabel, gbc);

        gbc.gridx = 1;
        confirmationDentist = new JLabel("--");
        confirmationDentist.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        content.add(confirmationDentist, gbc);

        // Date & Time
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel dateTimeLabel = new JLabel("Date & Time:");
        dateTimeLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        dateTimeLabel.setForeground(PRIMARY_DARK);
        content.add(dateTimeLabel, gbc);

        gbc.gridx = 1;
        confirmationDateTime = new JLabel("--");
        confirmationDateTime.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        content.add(confirmationDateTime, gbc);

        // Notes
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        JLabel notesLabel = new JLabel("Notes (Optional):");
        notesLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        notesLabel.setForeground(PRIMARY_DARK);
        content.add(notesLabel, gbc);

        gbc.gridy = 5;
        notesArea = new JTextArea(3, 30);
        notesArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        notesArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        JScrollPane notesScroll = new JScrollPane(notesArea);
        notesScroll.setPreferredSize(new Dimension(400, 80));
        content.add(notesScroll, gbc);

        panel.add(content, BorderLayout.CENTER);

        return panel;
    }

    // ========================
    // FOOTER PANEL
    // ========================
    private JPanel createFooterPanel() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(SOFT_SURFACE);
        footer.setBorder(new EmptyBorder(15, 0, 0, 0));

        backButton = createStyledButton("← Back", SOFT_SURFACE, PRIMARY_DARK);
        backButton.setBorderColor(LIGHT_SURFACE);
        backButton.setPreferredSize(new Dimension(120, 40));
        backButton.addActionListener(e -> navigateBack());

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);

        nextButton = createStyledButton("Next →", PRIMARY_DARK, Color.WHITE);
        nextButton.setPreferredSize(new Dimension(120, 40));
        nextButton.addActionListener(e -> navigateNext());

        confirmButton = createStyledButton("✓ Confirm Booking", PRIMARY_DARK, Color.WHITE);
        confirmButton.setPreferredSize(new Dimension(160, 40));
        confirmButton.setVisible(false);
        confirmButton.addActionListener(e -> confirmBooking());

        rightPanel.add(nextButton);
        rightPanel.add(confirmButton);

        footer.add(backButton, BorderLayout.WEST);
        footer.add(rightPanel, BorderLayout.EAST);

        return footer;
    }

    // ========================
    // HELPER METHODS
    // ========================

    private void loadData() {
        // Load patients, dentists, treatments from database
        patients = controller.getAllPatients();
        dentists = controller.getAllDentists();
        treatments = controller.getAllTreatments();
        
        // Initialize booked slots (will come from database)
        bookedSlots = new HashMap<>();
        
        // Populate patient list
        displayPatients(patients);
        populateTreatmentGrid();
        populateDentistGrid();
    }

    private void displayPatients(List<Patient> patientList) {
        patientListPanel.removeAll();
        
        if (patientList == null || patientList.isEmpty()) {
            JLabel emptyLabel = new JLabel("No patients found", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            emptyLabel.setForeground(SECONDARY_TEXT);
            emptyLabel.setBorder(new EmptyBorder(30, 0, 30, 0));
            patientListPanel.add(emptyLabel);
        } else {
            for (Patient patient : patientList) {
                JPanel card = createPatientCard(patient);
                patientListPanel.add(card);
                patientListPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        }
        
        patientListPanel.revalidate();
        patientListPanel.repaint();
    }

    private JPanel createPatientCard(Patient patient) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            new EmptyBorder(12, 15, 12, 15)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectPatient(patient);
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(HOVER_SURFACE);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(Color.WHITE);
            }
        });

        // Avatar
        JLabel avatar = new JLabel(getInitials(patient.getPatientName()));
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        avatar.setForeground(Color.WHITE);
        avatar.setOpaque(true);
        avatar.setBackground(PRIMARY_DARK);
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.setPreferredSize(new Dimension(40, 40));
        avatar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Info
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);
        
        JLabel nameLabel = new JLabel(patient.getPatientName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setForeground(PRIMARY_DARK);
        
        JLabel detailLabel = new JLabel("ID: " + patient.getPatientId() + " | " + patient.getContactNumber());
        detailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        detailLabel.setForeground(SECONDARY_TEXT);
        
        info.add(nameLabel);
        info.add(detailLabel);

        card.add(avatar, BorderLayout.WEST);
        card.add(info, BorderLayout.CENTER);

        return card;
    }

    private void populateTreatmentGrid() {
        treatmentGridPanel.removeAll();
        
        if (treatments != null) {
            for (Treatment treatment : treatments) {
                JPanel card = createTreatmentCard(treatment);
                treatmentGridPanel.add(card);
            }
        }
        
        treatmentGridPanel.revalidate();
        treatmentGridPanel.repaint();
    }

    private JPanel createTreatmentCard(Treatment treatment) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectTreatment(treatment);
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(HOVER_SURFACE);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(selectedTreatment != null && selectedTreatment.getId() == treatment.getId() ? 
                    SELECTED_COLOR : Color.WHITE);
            }
        });

        // Icon
        JLabel iconLabel = new JLabel("🦷", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 30));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(iconLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));

        // Name
        JLabel nameLabel = new JLabel(treatment.getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setForeground(PRIMARY_DARK);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(nameLabel);
        card.add(Box.createRigidArea(new Dimension(0, 5)));

        // Duration
        JLabel durationLabel = new JLabel(treatment.getDuration() + " min");
        durationLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        durationLabel.setForeground(SECONDARY_TEXT);
        durationLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(durationLabel);

        // Cost
        JLabel costLabel = new JLabel("Rs. " + treatment.getCost());
        costLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        costLabel.setForeground(PRIMARY_DARK);
        costLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(costLabel);

        return card;
    }

    private void populateDentistGrid() {
        dentistGridPanel.removeAll();
        
        if (dentists != null) {
            for (Dentist dentist : dentists) {
                if (dentist.isActive()) {
                    JPanel card = createDentistCard(dentist);
                    dentistGridPanel.add(card);
                }
            }
        }
        
        dentistGridPanel.revalidate();
        dentistGridPanel.repaint();
    }

    private JPanel createDentistCard(Dentist dentist) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_SURFACE, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectDentist(dentist);
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(HOVER_SURFACE);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(selectedDentist != null && selectedDentist.getId() == dentist.getId() ? 
                    SELECTED_COLOR : Color.WHITE);
            }
        });

        // Avatar
        JLabel avatar = new JLabel(getInitials(dentist.getName()));
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        avatar.setForeground(Color.WHITE);
        avatar.setOpaque(true);
        avatar.setBackground(MINT);
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.setPreferredSize(new Dimension(50, 50));
        avatar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Info
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);
        
        JLabel nameLabel = new JLabel(dentist.getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setForeground(PRIMARY_DARK);
        
        JLabel specLabel = new JLabel(dentist.getSpecialization());
        specLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        specLabel.setForeground(SECONDARY_TEXT);
        
        info.add(nameLabel);
        info.add(specLabel);

        card.add(avatar, BorderLayout.WEST);
        card.add(info, BorderLayout.CENTER);

        return card;
    }

    private void updateCalendar() {
        LocalDate date = LocalDate.of(currentYear, currentMonth, 1);
        monthLabel.setText(date.getMonth().toString() + " " + currentYear);
        
        JPanel gridPanel = (JPanel) ((JPanel) calendarPanel.getComponent(1));
        gridPanel.removeAll();
        
        // Day names
        String[] days = {"S", "M", "T", "W", "T", "F", "S"};
        for (String day : days) {
            JLabel label = new JLabel(day, SwingConstants.CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 12));
            label.setForeground(SECONDARY_TEXT);
            gridPanel.add(label);
        }
        
        // Calendar days
        int firstDayOfMonth = date.getDayOfWeek().getValue() % 7;
        int daysInMonth = date.lengthOfMonth();
        LocalDate today = LocalDate.now();
        String todayStr = today.toString();
        
        for (int i = 0; i < firstDayOfMonth; i++) {
            gridPanel.add(new JLabel(""));
        }
        
        for (int day = 1; day <= daysInMonth; day++) {
            String dateStr = currentYear + "-" + String.format("%02d", currentMonth) + "-" + String.format("%02d", day);
            LocalDate currentDate = LocalDate.of(currentYear, currentMonth, day);
            
            JButton dayButton = new JButton(String.valueOf(day));
            dayButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            dayButton.setBackground(Color.WHITE);
            dayButton.setBorder(BorderFactory.createLineBorder(LIGHT_SURFACE, 1));
            dayButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            boolean isPast = currentDate.isBefore(today);
            boolean isBooked = isDateFullyBooked(dateStr);
            boolean isSelected = dateStr.equals(selectedDate);
            
            if (isPast) {
                dayButton.setEnabled(false);
                dayButton.setForeground(Color.LIGHT_GRAY);
            } else if (isBooked) {
                dayButton.setEnabled(false);
                dayButton.setBackground(new Color(255, 200, 200));
                dayButton.setForeground(Color.GRAY);
                dayButton.setToolTipText("Fully booked");
            } else {
                if (isSelected) {
                    dayButton.setBackground(MINT);
                    dayButton.setForeground(PRIMARY_DARK);
                }
                dayButton.addActionListener(e -> selectDate(dateStr));
            }
            
            gridPanel.add(dayButton);
        }
        
        gridPanel.revalidate();
        gridPanel.repaint();
        updateTimeSlots();
    }

    private void updateTimeSlots() {
        JPanel slotsPanel = (JPanel) ((JPanel) timeSlotPanel.getComponent(1));
        slotsPanel.removeAll();
        
        if (selectedDate == null) {
            JLabel hint = new JLabel("Please select a date first", SwingConstants.CENTER);
            hint.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            hint.setForeground(SECONDARY_TEXT);
            hint.setBorder(new EmptyBorder(30, 0, 30, 0));
            slotsPanel.add(hint);
        } else {
            List<String> booked = bookedSlots.getOrDefault(selectedDate, new ArrayList<>());
            boolean hasAvailable = false;
            
            for (String time : TIME_SLOTS) {
                boolean isBooked = booked.contains(time);
                boolean isSelected = time.equals(selectedTime);
                
                JButton timeButton = new JButton(time);
                timeButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                timeButton.setBackground(Color.WHITE);
                timeButton.setBorder(BorderFactory.createLineBorder(LIGHT_SURFACE, 1));
                timeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
                
                if (isBooked) {
                    timeButton.setEnabled(false);
                    timeButton.setBackground(new Color(255, 200, 200));
                    timeButton.setForeground(Color.GRAY);
                } else {
                    hasAvailable = true;
                    if (isSelected) {
                        timeButton.setBackground(MINT);
                        timeButton.setForeground(PRIMARY_DARK);
                    }
                    timeButton.addActionListener(e -> selectTime(time));
                }
                
                slotsPanel.add(timeButton);
            }
            
            if (!hasAvailable) {
                slotsPanel.removeAll();
                JLabel hint = new JLabel("No slots available for this date", SwingConstants.CENTER);
                hint.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                hint.setForeground(SECONDARY_TEXT);
                hint.setBorder(new EmptyBorder(30, 0, 30, 0));
                slotsPanel.add(hint);
            }
        }
        
        slotsPanel.revalidate();
        slotsPanel.repaint();
    }

    private void changeMonth(int delta) {
        currentMonth += delta;
        if (currentMonth > 12) {
            currentMonth = 1;
            currentYear++;
        } else if (currentMonth < 1) {
            currentMonth = 12;
            currentYear--;
        }
        updateCalendar();
    }

    private boolean isDateFullyBooked(String date) {
        List<String> booked = bookedSlots.getOrDefault(date, new ArrayList<>());
        return booked.size() >= TIME_SLOTS.length;
    }

    private void selectPatient(Patient patient) {
        selectedPatient = patient;
        // Highlight selected card
        for (Component comp : patientListPanel.getComponents()) {
            if (comp instanceof JPanel) {
                comp.setBackground(Color.WHITE);
            }
        }
        // Navigate to next step automatically
        showStep(2);
    }

    private void selectTreatment(Treatment treatment) {
        selectedTreatment = treatment;
        // Highlight selected card
        for (Component comp : treatmentGridPanel.getComponents()) {
            if (comp instanceof JPanel) {
                comp.setBackground(Color.WHITE);
            }
        }
        showStep(3);
    }

    private void selectDentist(Dentist dentist) {
        selectedDentist = dentist;
        // Highlight selected card
        for (Component comp : dentistGridPanel.getComponents()) {
            if (comp instanceof JPanel) {
                comp.setBackground(Color.WHITE);
            }
        }
        showStep(4);
    }

    private void selectDate(String date) {
        selectedDate = date;
        selectedTime = null;
        updateCalendar();
        updateTimeSlots();
    }

    private void selectTime(String time) {
        selectedTime = time;
        updateTimeSlots();
    }

    private void filterPatients() {
        String query = searchField.getText().trim().toLowerCase();
        if (query.isEmpty()) {
            displayPatients(patients);
        } else {
            List<Patient> filtered = new ArrayList<>();
            for (Patient p : patients) {
                if (p.getPatientName().toLowerCase().contains(query) ||
                    p.getContactNumber().contains(query) ||
                    String.valueOf(p.getPatientId()).contains(query)) {
                    filtered.add(p);
                }
            }
            displayPatients(filtered);
        }
    }

    private void openNewPatient() {
        Container parent = getParent();
        while (parent != null && !(parent instanceof MainFrame)) {
            parent = parent.getParent();
        }
        if (parent instanceof MainFrame) {
            ((MainFrame) parent).showCard("PATIENT_ADD");
        }
    }

    private void showStep(int step) {
        currentStep = step;
        String[] stepLabels = {"Select Patient", "Select Treatment", "Select Dentist", "Select Date & Time", "Confirm Booking"};
        stepIndicator.setText("Step " + step + " of 5: " + stepLabels[step - 1]);
        
        cardLayout.show(contentPanel, "STEP_" + step);
        
        // Update navigation buttons
        backButton.setVisible(step > 1);
        nextButton.setVisible(step < 5);
        confirmButton.setVisible(step == 5);
        
        // Update confirmation details if on step 5
        if (step == 5) {
            updateConfirmation();
        }
        
        // Update time slots if on step 4
        if (step == 4) {
            updateTimeSlots();
        }
    }

    private void navigateBack() {
        if (currentStep > 1) {
            showStep(currentStep - 1);
        }
    }

    private void navigateNext() {
        if (currentStep < 5) {
            showStep(currentStep + 1);
        }
    }

    private void updateConfirmation() {
        if (selectedPatient != null) {
            confirmationPatient.setText(selectedPatient.getPatientName() + " (ID: " + selectedPatient.getPatientId() + ")");
        }
        if (selectedTreatment != null) {
            confirmationTreatment.setText(selectedTreatment.getName() + " - Rs. " + selectedTreatment.getCost());
        }
        if (selectedDentist != null) {
            confirmationDentist.setText(selectedDentist.getName() + " (" + selectedDentist.getSpecialization() + ")");
        }
        if (selectedDate != null && selectedTime != null) {
            confirmationDateTime.setText(selectedDate + " at " + selectedTime);
        }
    }

    private void confirmBooking() {
        // Validate all selections
        if (selectedPatient == null || selectedTreatment == null || 
            selectedDentist == null || selectedDate == null || selectedTime == null) {
            JOptionPane.showMessageDialog(this, 
                "Please complete all steps before confirming.",
                "Incomplete Booking",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Confirm appointment for " + selectedPatient.getPatientName() + 
            "\nTreatment: " + selectedTreatment.getName() +
            "\nDentist: " + selectedDentist.getName() +
            "\nDate: " + selectedDate + " at " + selectedTime,
            "Confirm Appointment",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            // Save appointment
            boolean success = controller.bookAppointment(
                selectedPatient.getPatientId(),
                selectedDentist.getId(),
                selectedTreatment.getId(),
                selectedDate,
                selectedTime,
                notesArea.getText()
            );

            if (success) {
                JOptionPane.showMessageDialog(this,
                    "Appointment booked successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Navigate back to appointment list
                Container parent = getParent();
                while (parent != null && !(parent instanceof MainFrame)) {
                    parent = parent.getParent();
                }
                if (parent instanceof MainFrame) {
                    ((MainFrame) parent).showCard("APPOINTMENT_LIST");
                }
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to book appointment. Please try again.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String getInitials(String name) {
        if (name == null || name.isEmpty()) return "";
        String[] parts = name.split(" ");
        if (parts.length == 1) return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        return (parts[0].charAt(0) + "" + parts[parts.length - 1].charAt(0)).toUpperCase();
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
}