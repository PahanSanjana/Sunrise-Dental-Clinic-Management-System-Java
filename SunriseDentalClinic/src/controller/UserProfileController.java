package controller;

import dao.*;
import model.User;
import model.User.UserRole;
import model.LoginSession;
import view.UserProfilePanel;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class UserProfileController {
    
    private UserProfilePanel view;
    private UserDAO userDAO;
    private PatientDAO patientDAO;
    private StaffDAO staffDAO;
    private DentistDAO dentistDAO;
    private User currentUser;

    public UserProfileController(UserProfilePanel view) {
        this.view = view;
        this.userDAO = new UserDAO();
        this.patientDAO = new PatientDAO();
        this.staffDAO = new StaffDAO();
        this.dentistDAO = new DentistDAO();
        this.currentUser = LoginSession.getInstance().getCurrentUser();
    }

    public User getCurrentUser() {
        return currentUser;
    }

    // =====================================================
    // PROFILE DATA LOADING
    // =====================================================

    public Map<String, Object> getProfileData(int userId, UserRole role) {
        Map<String, Object> data = new HashMap<>();
        
        switch (role) {
            case PATIENT:
                var patient = patientDAO.getPatientByUserId(userId);
                if (patient != null) {
                    data.put("patientId", patient.getPatientId());
                    data.put("patientName", patient.getPatientName());
                    data.put("gender", patient.getGender());
                    data.put("address", patient.getAddress());
                    data.put("contactNumber", patient.getContactNumber());
                    data.put("email", patient.getEmail());
                    data.put("dateOfBirth", patient.getDateOfBirth() != null ? patient.getDateOfBirth().toString() : "");
                    data.put("emergencyContact", patient.getEmergencyContact());
                    data.put("emergencyPhone", patient.getEmergencyPhone());
                    data.put("medicalHistory", patient.getMedicalHistory());
                    data.put("allergies", patient.getAllergies());
                    data.put("createdAt", patient.getCreatedAt());
                }
                break;
                
            case RECEPTION:
                var staff = staffDAO.getStaffByUserId(userId);
                if (staff != null) {
                    data.put("staffId", staff.getStaffId());
                    data.put("firstName", staff.getFirstName());
                    data.put("lastName", staff.getLastName());
                    data.put("position", staff.getPosition());
                    data.put("department", staff.getDepartment());
                    data.put("phone", staff.getPhone());
                    data.put("email", staff.getEmail());
                    data.put("hireDate", staff.getHireDate() != null ? staff.getHireDate().toString() : "");
                    data.put("salary", staff.getSalary());
                    data.put("isActive", staff.isActive());
                }
                break;
                
            case DENTIST:
                var dentist = dentistDAO.getDentistByUserId(userId);
                if (dentist != null) {
                    data.put("dentistId", dentist.getDentistId());
                    data.put("dentistName", dentist.getDentistName());
                    data.put("specialization", dentist.getSpecialization());
                    data.put("licenseNumber", dentist.getLicenseNumber());
                    data.put("workingHours", dentist.getWorkingHours());
                    data.put("phone", dentist.getPhone());
                    data.put("email", dentist.getEmail());
                    data.put("yearsOfExperience", dentist.getYearsOfExperience());
                    data.put("consultationFee", dentist.getConsultationFee());
                    data.put("isAvailable", dentist.isAvailable());
                }
                break;
                
            case ADMIN:
                // Admin has no additional profile
                data.put("role", "System Administrator");
                break;
        }
        
        return data;
    }

    public void loadPatientProfile(int userId, Consumer<Map<String, Object>> callback) {
        SwingWorker<Map<String, Object>, Void> worker = new SwingWorker<Map<String, Object>, Void>() {
            @Override
            protected Map<String, Object> doInBackground() throws Exception {
                var patient = patientDAO.getPatientByUserId(userId);
                Map<String, Object> data = new HashMap<>();
                if (patient != null) {
                    data.put("patientId", patient.getPatientId());
                    data.put("patientName", patient.getPatientName());
                    data.put("gender", patient.getGender());
                    data.put("address", patient.getAddress());
                    data.put("contactNumber", patient.getContactNumber());
                    data.put("email", patient.getEmail());
                    data.put("dateOfBirth", patient.getDateOfBirth() != null ? patient.getDateOfBirth().toString() : "");
                    data.put("emergencyContact", patient.getEmergencyContact());
                    data.put("emergencyPhone", patient.getEmergencyPhone());
                    data.put("medicalHistory", patient.getMedicalHistory());
                    data.put("allergies", patient.getAllergies());
                }
                return data;
            }
            
            @Override
            protected void done() {
                try {
                    callback.accept(get());
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.accept(null);
                }
            }
        };
        worker.execute();
    }

    public void loadStaffProfile(int userId, Consumer<Map<String, Object>> callback) {
        SwingWorker<Map<String, Object>, Void> worker = new SwingWorker<Map<String, Object>, Void>() {
            @Override
            protected Map<String, Object> doInBackground() throws Exception {
                var staff = staffDAO.getStaffByUserId(userId);
                Map<String, Object> data = new HashMap<>();
                if (staff != null) {
                    data.put("staffId", staff.getStaffId());
                    data.put("firstName", staff.getFirstName());
                    data.put("lastName", staff.getLastName());
                    data.put("position", staff.getPosition());
                    data.put("department", staff.getDepartment());
                    data.put("phone", staff.getPhone());
                    data.put("email", staff.getEmail());
                    data.put("hireDate", staff.getHireDate() != null ? staff.getHireDate().toString() : "");
                    data.put("salary", staff.getSalary());
                }
                return data;
            }
            
            @Override
            protected void done() {
                try {
                    callback.accept(get());
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.accept(null);
                }
            }
        };
        worker.execute();
    }

    public void loadDentistProfile(int userId, Consumer<Map<String, Object>> callback) {
        SwingWorker<Map<String, Object>, Void> worker = new SwingWorker<Map<String, Object>, Void>() {
            @Override
            protected Map<String, Object> doInBackground() throws Exception {
                var dentist = dentistDAO.getDentistByUserId(userId);
                Map<String, Object> data = new HashMap<>();
                if (dentist != null) {
                    data.put("dentistId", dentist.getDentistId());
                    data.put("dentistName", dentist.getDentistName());
                    data.put("specialization", dentist.getSpecialization());
                    data.put("licenseNumber", dentist.getLicenseNumber());
                    data.put("workingHours", dentist.getWorkingHours());
                    data.put("phone", dentist.getPhone());
                    data.put("email", dentist.getEmail());
                    data.put("yearsOfExperience", dentist.getYearsOfExperience());
                    data.put("consultationFee", dentist.getConsultationFee());
                    data.put("isAvailable", dentist.isAvailable());
                }
                return data;
            }
            
            @Override
            protected void done() {
                try {
                    callback.accept(get());
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.accept(null);
                }
            }
        };
        worker.execute();
    }

    // =====================================================
    // UPDATE METHODS
    // =====================================================

    public boolean updateUserEmail(int userId, String email) {
        User user = userDAO.getUserById(userId);
        if (user == null) return false;
        user.setEmail(email);
        return userDAO.updateUser(user);
    }

    public boolean updatePatientProfile(int userId, Map<String, Object> data) {
        var patient = patientDAO.getPatientByUserId(userId);
        if (patient == null) return false;
        
        patient.setPatientName((String) data.get("patientName"));
        patient.setGender((String) data.get("gender"));
        patient.setAddress((String) data.get("address"));
        patient.setContactNumber((String) data.get("contactNumber"));
        patient.setEmail((String) data.get("email"));
        patient.setEmergencyContact((String) data.get("emergencyContact"));
        patient.setEmergencyPhone((String) data.get("emergencyPhone"));
        patient.setMedicalHistory((String) data.get("medicalHistory"));
        patient.setAllergies((String) data.get("allergies"));
        
        // Update date of birth if provided
        String dobStr = (String) data.get("dateOfBirth");
        if (dobStr != null && !dobStr.isEmpty()) {
            try {
                patient.setDateOfBirth(java.sql.Date.valueOf(dobStr));
            } catch (Exception e) {
                // Ignore invalid date
            }
        }
        
        // Update email in users table too
        updateUserEmail(userId, (String) data.get("email"));
        
        return patientDAO.updatePatient(patient);
    }

    public boolean updateStaffProfile(int userId, Map<String, Object> data) {
        var staff = staffDAO.getStaffByUserId(userId);
        if (staff == null) return false;
        
        staff.setFirstName((String) data.get("firstName"));
        staff.setLastName((String) data.get("lastName"));
        staff.setPosition((String) data.get("position"));
        staff.setDepartment((String) data.get("department"));
        staff.setPhone((String) data.get("phone"));
        staff.setEmail((String) data.get("email"));
        staff.setSalary((Double) data.get("salary"));
        
        // Update email in users table too
        updateUserEmail(userId, (String) data.get("email"));
        
        return staffDAO.updateStaff(staff);
    }

    public boolean updateDentistProfile(int userId, Map<String, Object> data) {
        var dentist = dentistDAO.getDentistByUserId(userId);
        if (dentist == null) return false;
        
        dentist.setDentistName((String) data.get("dentistName"));
        dentist.setSpecialization((String) data.get("specialization"));
        dentist.setWorkingHours((String) data.get("workingHours"));
        dentist.setPhone((String) data.get("phone"));
        dentist.setEmail((String) data.get("email"));
        dentist.setYearsOfExperience((Integer) data.get("yearsOfExperience"));
        dentist.setConsultationFee((Double) data.get("consultationFee"));
        dentist.setAvailable((Boolean) data.get("isAvailable"));
        
        // Update email in users table too
        updateUserEmail(userId, (String) data.get("email"));
        
        return dentistDAO.updateDentist(dentist);
    }

    // =====================================================
    // PASSWORD METHODS
    // =====================================================

    public boolean verifyCurrentPassword(int userId, String currentPassword) {
        User user = userDAO.getUserById(userId);
        if (user == null) return false;
        // For demo - compare plain text
        return currentPassword.equals(user.getPasswordHash());
    }

    public boolean changePassword(int userId, String newPassword) {
        User user = userDAO.getUserById(userId);
        if (user == null) return false;
        
        String salt = "salt_" + System.currentTimeMillis();
        // For demo - store plain text
        // In production, hash the password with salt
        return userDAO.changePassword(userId, newPassword, salt);
    }
}