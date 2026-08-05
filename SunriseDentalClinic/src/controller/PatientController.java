package controller;

import dao.PatientDAO;
import java.awt.Container;
import java.awt.Cursor;
import model.Patient;
import view.AddPatientPanel;
import view.MainFrame;

import javax.swing.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PatientController {
    private AddPatientPanel view;
    private PatientDAO patientDAO;

    public PatientController(AddPatientPanel view) {
        this.view = view;
        this.patientDAO = new PatientDAO();
        initController();
    }

    private void initController() {
        view.addSaveListener(e -> handleSavePatient());
        view.addClearListener(e -> view.clearForm());
        view.addCancelListener(e -> {
            // Navigate back to patient list or dashboard
            Container parent = view.getParent();
            while (parent != null && !(parent instanceof MainFrame)) {
                parent = parent.getParent();
            }
            if (parent instanceof MainFrame) {
                ((MainFrame) parent).showCard("PATIENT_LIST");
            }
        });
    }

    private void handleSavePatient() {
        // Validate all fields
        String firstName = view.getFirstName();
        String lastName = view.getLastName();
        String dob = view.getDateOfBirth();
        String gender = view.getGender();
        String phone = view.getPhone();
        String email = view.getEmail();
        String address = view.getAddress();
        String emergencyContact = view.getEmergencyContact();
        String emergencyPhone = view.getEmergencyPhone();
        String medicalHistory = view.getMedicalHistory();
        String allergies = view.getAllergies();

        // Validate required fields
        if (firstName.isEmpty() || lastName.isEmpty()) {
            view.showError("First Name and Last Name are required.");
            return;
        }

        if (firstName.length() < 2 || lastName.length() < 2) {
            view.showError("Name must be at least 2 characters.");
            return;
        }

        if (!firstName.matches("^[a-zA-Z\\s]+$") || !lastName.matches("^[a-zA-Z\\s]+$")) {
            view.showError("Name can only contain letters and spaces.");
            return;
        }

        // Validate Date of Birth
        if (dob.isEmpty()) {
            view.showError("Date of Birth is required.");
            return;
        }

        Date dateOfBirth = null;
        try {
            LocalDate localDate = LocalDate.parse(dob, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            dateOfBirth = Date.valueOf(localDate);
            
            // Check if date is in the future
            if (localDate.isAfter(LocalDate.now())) {
                view.showError("Date of Birth cannot be in the future.");
                return;
            }
        } catch (Exception e) {
            view.showError("Invalid date format. Please use YYYY-MM-DD.");
            return;
        }

        // Validate phone
        if (phone.isEmpty()) {
            view.showError("Phone number is required.");
            return;
        }
        String phoneDigits = phone.replaceAll("[^0-9]", "");
        if (phoneDigits.length() < 10) {
            view.showError("Please enter a valid phone number (at least 10 digits).");
            return;
        }

        // Validate email
        if (email.isEmpty()) {
            view.showError("Email is required.");
            return;
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            view.showError("Please enter a valid email address.");
            return;
        }

        // Validate address
        if (address.isEmpty()) {
            view.showError("Address is required.");
            return;
        }

        // Validate emergency contact
        if (emergencyContact.isEmpty()) {
            view.showError("Emergency contact name is required.");
            return;
        }
        if (emergencyContact.length() < 2) {
            view.showError("Emergency contact name must be at least 2 characters.");
            return;
        }

        // Validate emergency phone
        if (emergencyPhone.isEmpty()) {
            view.showError("Emergency phone number is required.");
            return;
        }
        String emergencyPhoneDigits = emergencyPhone.replaceAll("[^0-9]", "");
        if (emergencyPhoneDigits.length() < 10) {
            view.showError("Please enter a valid emergency phone number (at least 10 digits).");
            return;
        }

        // Create Patient object
        Patient patient = new Patient(
            firstName, lastName, dateOfBirth, gender,
            phone, email, address, emergencyContact, emergencyPhone,
            medicalHistory, allergies
        );

        // Show loading message
        view.showSuccess("Saving patient... Please wait.");
        view.setCursor(new Cursor(Cursor.WAIT_CURSOR));

        // Use SwingWorker to save in background
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return patientDAO.addPatient(patient);
            }

            @Override
            protected void done() {
                view.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                try {
                    boolean success = get();
                    if (success) {
                        view.showSuccess("Patient saved successfully! Patient ID: " + patient.getPatientId());
                        view.clearForm();
                        
                        // Show success message and navigate back after delay
                        Timer timer = new Timer(2000, e -> {
                            // Navigate back to patient list
                            Container parent = view.getParent();
                            while (parent != null && !(parent instanceof MainFrame)) {
                                parent = parent.getParent();
                            }
                            if (parent instanceof MainFrame) {
                                ((MainFrame) parent).showCard("PATIENT_LIST");
                            }
                        });
                        timer.setRepeats(false);
                        timer.start();
                    } else {
                        view.showError("Failed to save patient. Please try again.");
                    }
                } catch (Exception e) {
                    view.showError("Error saving patient: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            
            JFrame frame = new JFrame("Add Patient");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 800);
            frame.setLocationRelativeTo(null);
            frame.add(new AddPatientPanel());
            frame.setVisible(true);
        });
    }
}