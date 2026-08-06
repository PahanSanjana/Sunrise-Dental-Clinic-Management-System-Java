package controller;

import dao.PatientDAO;
import model.Patient;
import view.PatientListPanel;

import javax.swing.*;
import java.util.List;

public class PatientListController {
    private PatientListPanel view;
    private PatientDAO patientDAO;

    public PatientListController(PatientListPanel view) {
        this.view = view;
        this.patientDAO = new PatientDAO();
    }

    public void loadPatients(String searchText, String filter) {
        // Use SwingWorker to load patients in background
        SwingWorker<List<Patient>, Void> worker = new SwingWorker<List<Patient>, Void>() {
            @Override
            protected List<Patient> doInBackground() throws Exception {
                List<Patient> patients;
                
                if (searchText != null && !searchText.isEmpty()) {
                    patients = patientDAO.searchPatients(searchText);
                } else {
                    patients = patientDAO.getAllPatients();
                }
                
                // Apply gender filter
                if (filter != null && !filter.equals("All") && patients != null) {
                    patients.removeIf(p -> p.getGender() == null || !p.getGender().equals(filter));
                }
                
                return patients;
            }

            @Override
            protected void done() {
                try {
                    List<Patient> patients = get();
                    if (view != null) {
                        view.displayPatients(patients);
                    }
                } catch (Exception e) {
                    if (view != null) {
                        view.showError("Error loading patients: " + e.getMessage());
                    }
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    public void deletePatient(int patientId) {
        // Use SwingWorker to delete in background
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return patientDAO.deletePatient(patientId);
            }

            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        if (view != null) {
                            view.showSuccess("Patient deleted successfully!");
                            view.loadPatients(); // Refresh the list
                        }
                    } else {
                        if (view != null) {
                            view.showError("Failed to delete patient.");
                        }
                    }
                } catch (Exception e) {
                    if (view != null) {
                        view.showError("Error deleting patient: " + e.getMessage());
                    }
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
}