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

    /**
     * Get patient by ID
     * @param patientId The patient ID
     * @return Patient object if found, null otherwise
     */
    public Patient getPatientById(int patientId) {
        return patientDAO.getPatientById(patientId);
    }

    /**
     * Update patient information
     * @param patient The patient to update
     * @return true if successful, false otherwise
     */
    public boolean updatePatient(Patient patient) {
        return patientDAO.updatePatient(patient);
    }

    /**
     * Get all patients
     * @return List of all patients
     */
    public List<Patient> getAllPatients() {
        return patientDAO.getAllPatients();
    }

    /**
     * Search patients by term
     * @param searchTerm The search term
     * @return List of matching patients
     */
    public List<Patient> searchPatients(String searchTerm) {
        return patientDAO.searchPatients(searchTerm);
    }

    /**
     * Get total patient count
     * @return Total number of patients
     */
    public int getPatientCount() {
        return patientDAO.getPatientCount();
    }

    /**
     * Get patients with pagination
     * @param offset The offset (starting point)
     * @param limit The number of records to fetch
     * @return List of patients
     */
    public List<Patient> getPatientsPaginated(int offset, int limit) {
        return patientDAO.getPatientsPaginated(offset, limit);
    }

    /**
     * Get recent patients
     * @param limit Number of recent patients to get
     * @return List of recent patients
     */
    public List<Patient> getRecentPatients(int limit) {
        return patientDAO.getRecentPatients(limit);
    }

    /**
     * Add a new patient
     * @param patient The patient to add
     * @return true if successful, false otherwise
     */
    public boolean addPatient(Patient patient) {
        return patientDAO.addPatient(patient);
    }

    /**
     * Link a patient to a user account
     * @param patientId The patient ID
     * @param userId The user ID to link
     * @return true if successful, false otherwise
     */
    public boolean linkPatientToUser(int patientId, int userId) {
        return patientDAO.linkPatientToUser(patientId, userId);
    }

    /**
     * Check if email exists
     * @param email The email to check
     * @param excludePatientId Patient ID to exclude from check
     * @return true if email exists, false otherwise
     */
    public boolean emailExists(String email, int excludePatientId) {
        return patientDAO.emailExists(email, excludePatientId);
    }

    /**
     * Check if contact number exists
     * @param contactNumber The contact number to check
     * @param excludePatientId Patient ID to exclude from check
     * @return true if contact number exists, false otherwise
     */
    public boolean contactNumberExists(String contactNumber, int excludePatientId) {
        return patientDAO.contactNumberExists(contactNumber, excludePatientId);
    }
}