package controller;

import dao.DentistDAO;
import model.Dentist;
import view.AddDentistPanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import view.MainFrame;

public class DentistController {
    private AddDentistPanel view;
    private DentistDAO dentistDAO;

    public DentistController(AddDentistPanel view) {
        this.view = view;
        this.dentistDAO = new DentistDAO();
        initController();
    }

    private void initController() {
        view.addSaveListener(e -> handleSaveDentist());
        view.addClearListener(e -> view.clearForm());
        view.addCancelListener(e -> {
            Container parent = view.getParent();
            while (parent != null && !(parent instanceof MainFrame)) {
                parent = parent.getParent();
            }
            if (parent instanceof MainFrame) {
                ((MainFrame) parent).showCard("DENTIST_LIST");
            }
        });
    }

    private void handleSaveDentist() {
        // Get all form values
        String firstName = view.getFirstName();
        String lastName = view.getLastName();
        String specialization = view.getSpecialization();
        String licenseNumber = view.getLicenseNumber();
        String phone = view.getPhone();
        String email = view.getEmail();
        String experienceStr = view.getExperience();
        String feeStr = view.getConsultationFee();
        boolean isAvailable = view.isAvailable();

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

        // Validate specialization
        if (specialization.isEmpty()) {
            view.showError("Specialization is required.");
            return;
        }

        // Validate license number
        if (licenseNumber.isEmpty()) {
            view.showError("License Number is required.");
            return;
        }

        if (dentistDAO.licenseNumberExists(licenseNumber)) {
            view.showError("License Number already exists. Please enter a unique license number.");
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

        // Validate years of experience
        int yearsOfExperience = 0;
        try {
            yearsOfExperience = Integer.parseInt(experienceStr);
            if (yearsOfExperience < 0) {
                view.showError("Years of experience cannot be negative.");
                return;
            }
        } catch (NumberFormatException e) {
            view.showError("Please enter a valid number for years of experience.");
            return;
        }

        // Validate consultation fee
        double consultationFee = 0;
        try {
            consultationFee = Double.parseDouble(feeStr);
            if (consultationFee < 0) {
                view.showError("Consultation fee cannot be negative.");
                return;
            }
        } catch (NumberFormatException e) {
            view.showError("Please enter a valid number for consultation fee.");
            return;
        }

        // Create Dentist object
        Dentist dentist = new Dentist(
            firstName, lastName, specialization, licenseNumber,
            phone, email, yearsOfExperience, consultationFee, isAvailable
        );

        // Show loading message
        view.showSuccess("Saving dentist... Please wait.");
        view.setCursor(new Cursor(Cursor.WAIT_CURSOR));

        // Use SwingWorker to save in background
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return dentistDAO.addDentist(dentist);
            }

            @Override
            protected void done() {
                view.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                try {
                    boolean success = get();
                    if (success) {
                        view.showSuccess("Dentist saved successfully!");
                        view.clearForm();
                        
                        // Show success and navigate back after delay
                        Timer timer = new Timer(1500, e -> {
                            Container parent = view.getParent();
                            while (parent != null && !(parent instanceof MainFrame)) {
                                parent = parent.getParent();
                            }
                            if (parent instanceof MainFrame) {
                                ((MainFrame) parent).showCard("DENTIST_LIST");
                            }
                        });
                        timer.setRepeats(false);
                        timer.start();
                    } else {
                        view.showError("Failed to save dentist. Please try again.");
                    }
                } catch (Exception e) {
                    view.showError("Error saving dentist: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    public Dentist getDentistById(int dentistId) {
        return dentistDAO.getDentistById(dentistId);
    }

    public List<Dentist> getAllDentists() {
        return dentistDAO.getAllDentists();
    }

    public List<Dentist> getAvailableDentists() {
        return dentistDAO.getAvailableDentists();
    }

    public boolean updateDentist(Dentist dentist) {
        return dentistDAO.updateDentist(dentist);
    }

    public boolean deleteDentist(int dentistId) {
        return dentistDAO.deleteDentist(dentistId);
    }

    public int getDentistCount() {
        return dentistDAO.getDentistCount();
    }
}