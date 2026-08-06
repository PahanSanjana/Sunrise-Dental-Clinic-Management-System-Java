package controller;

import dao.PatientDAO;
import model.Patient;
import view.AddPatientPanel;
import view.MainFrame;

import javax.swing.*;
import java.awt.*;
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
            // Navigate back to patient list
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
        // Get all form values
        String patientName = view.getPatientName();
        String gender = view.getGender();
        String address = view.getAddress();
        String contactNumber = view.getContactNumber();
        String email = view.getEmail();
        String dob = view.getDateOfBirth();
        String emergencyContact = view.getEmergencyContact();
        String emergencyPhone = view.getEmergencyPhone();
        String medicalHistory = view.getMedicalHistory();
        String allergies = view.getAllergies();

        // Validate required fields
        if (patientName.isEmpty()) {
            view.showError("Patient Name is required.");
            return;
        }

        if (patientName.length() < 2) {
            view.showError("Patient Name must be at least 2 characters.");
            return;
        }

        if (!patientName.matches("^[a-zA-Z\\s.]+$")) {
            view.showError("Patient Name can only contain letters, spaces, and dots.");
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
            
            if (localDate.isAfter(LocalDate.now())) {
                view.showError("Date of Birth cannot be in the future.");
                return;
            }
        } catch (Exception e) {
            view.showError("Invalid date format. Please use YYYY-MM-DD.");
            return;
        }

        // Validate contact number
        if (contactNumber.isEmpty()) {
            view.showError("Contact Number is required.");
            return;
        }
        String contactDigits = contactNumber.replaceAll("[^0-9]", "");
        if (contactDigits.length() < 10) {
            view.showError("Please enter a valid contact number (at least 10 digits).");
            return;
        }

        // Validate email (optional but if provided, validate format)
        if (!email.isEmpty() && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            view.showError("Please enter a valid email address.");
            return;
        }

        // Create Patient object with NULL patientLoginId (0 means no user linked)
        // Using -1 to indicate NULL in the database
        Patient patient = new Patient(
            patientName, gender, address, contactNumber,
            email, dateOfBirth, emergencyContact, emergencyPhone,
            -1, // Use -1 to indicate NULL (will be handled in DAO)
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
                        
                        // Show success and navigate back after delay
                        Timer timer = new Timer(2000, e -> {
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
}