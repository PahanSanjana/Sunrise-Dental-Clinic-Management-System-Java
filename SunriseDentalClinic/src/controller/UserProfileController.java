package controller;

import dao.*;
import model.*;
import model.User.UserRole;
import model.LoginSession;
import view.UserProfilePanel;

import javax.swing.*;
import java.sql.Date;
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
    // GET USER BY ID FROM DATABASE
    // =====================================================
    
    /**
     * Get user by ID directly from database (fresh data)
     * @param userId The user ID
     * @return User object if found, null otherwise
     */
    public User getUserById(int userId) {
        User user = userDAO.getUserById(userId);
        if (user == null) {
            return null;
        }
        
        // Load role-specific ID based on user role
        switch (user.getRole()) {
            case PATIENT:
                Patient patient = patientDAO.getPatientByUserId(userId);
                if (patient != null) {
                    user.setPatientId(patient.getPatientId());
                }
                break;
            case RECEPTION:
                Staff staff = staffDAO.getStaffByUserId(userId);
                if (staff != null) {
                    user.setStaffId(staff.getStaffId());
                }
                break;
            case DENTIST:
                Dentist dentist = dentistDAO.getDentistByUserId(userId);
                if (dentist != null) {
                    user.setDentistId(dentist.getDentistId());
                }
                break;
            case ADMIN:
                // Admin has no additional profile
                break;
        }
        
        return user;
    }

    // =====================================================
    // PROFILE DATA LOADING - SYNCHRONOUS
    // =====================================================

    public Map<String, Object> getProfileData(int userId, UserRole role) {
        Map<String, Object> data = new HashMap<>();
        
        switch (role) {
            case PATIENT:
                Patient patient = patientDAO.getPatientByUserId(userId);
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
                Staff staff = staffDAO.getStaffByUserId(userId);
                if (staff != null) {
                    data.put("staffId", staff.getStaffId());
                    data.put("firstName", staff.getFirstName());
                    data.put("lastName", staff.getLastName());
                    data.put("fullName", staff.getFullName());
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
                Dentist dentist = dentistDAO.getDentistByUserId(userId);
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
                data.put("permissions", "Full access to all system features");
                data.put("userId", userId);
                break;
        }
        
        return data;
    }

    // =====================================================
    // PROFILE DATA LOADING - ASYNCHRONOUS (For SwingWorker)
    // =====================================================

    public void loadPatientProfile(int userId, Consumer<Map<String, Object>> callback) {
        SwingWorker<Map<String, Object>, Void> worker = new SwingWorker<Map<String, Object>, Void>() {
            @Override
            protected Map<String, Object> doInBackground() throws Exception {
                Patient patient = patientDAO.getPatientByUserId(userId);
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
                    data.put("createdAt", patient.getCreatedAt());
                    data.put("updatedAt", patient.getUpdatedAt());
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
                Staff staff = staffDAO.getStaffByUserId(userId);
                Map<String, Object> data = new HashMap<>();
                if (staff != null) {
                    data.put("staffId", staff.getStaffId());
                    data.put("firstName", staff.getFirstName());
                    data.put("lastName", staff.getLastName());
                    data.put("fullName", staff.getFullName());
                    data.put("position", staff.getPosition());
                    data.put("department", staff.getDepartment());
                    data.put("phone", staff.getPhone());
                    data.put("email", staff.getEmail());
                    data.put("hireDate", staff.getHireDate() != null ? staff.getHireDate().toString() : "");
                    data.put("salary", staff.getSalary());
                    data.put("isActive", staff.isActive());
                    data.put("createdAt", staff.getCreatedAt());
                    data.put("updatedAt", staff.getUpdatedAt());
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
                Dentist dentist = dentistDAO.getDentistByUserId(userId);
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
                    data.put("createdAt", dentist.getCreatedAt());
                    data.put("updatedAt", dentist.getUpdatedAt());
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

    public boolean updateUserUsername(int userId, String username) {
        User user = userDAO.getUserById(userId);
        if (user == null) return false;
        user.setUsername(username);
        return userDAO.updateUser(user);
    }

    public boolean updatePatientProfile(int userId, Map<String, Object> data) {
        Patient patient = patientDAO.getPatientByUserId(userId);
        if (patient == null) return false;
        
        // Update patient fields
        String patientName = (String) data.get("patientName");
        if (patientName != null && !patientName.isEmpty()) {
            patient.setPatientName(patientName);
        }
        
        String gender = (String) data.get("gender");
        if (gender != null && !gender.isEmpty()) {
            patient.setGender(gender);
        }
        
        String address = (String) data.get("address");
        if (address != null) {
            patient.setAddress(address);
        }
        
        String contactNumber = (String) data.get("contactNumber");
        if (contactNumber != null && !contactNumber.isEmpty()) {
            patient.setContactNumber(contactNumber);
        }
        
        String email = (String) data.get("email");
        if (email != null && !email.isEmpty()) {
            patient.setEmail(email);
            // Update email in users table too
            updateUserEmail(userId, email);
        }
        
        String emergencyContact = (String) data.get("emergencyContact");
        if (emergencyContact != null) {
            patient.setEmergencyContact(emergencyContact);
        }
        
        String emergencyPhone = (String) data.get("emergencyPhone");
        if (emergencyPhone != null) {
            patient.setEmergencyPhone(emergencyPhone);
        }
        
        String medicalHistory = (String) data.get("medicalHistory");
        if (medicalHistory != null) {
            patient.setMedicalHistory(medicalHistory);
        }
        
        String allergies = (String) data.get("allergies");
        if (allergies != null) {
            patient.setAllergies(allergies);
        }
        
        // Update date of birth if provided
        String dobStr = (String) data.get("dateOfBirth");
        if (dobStr != null && !dobStr.isEmpty()) {
            try {
                patient.setDateOfBirth(Date.valueOf(dobStr));
            } catch (Exception e) {
                // Ignore invalid date
            }
        }
        
        return patientDAO.updatePatient(patient);
    }

    public boolean updateStaffProfile(int userId, Map<String, Object> data) {
        Staff staff = staffDAO.getStaffByUserId(userId);
        if (staff == null) return false;
        
        String firstName = (String) data.get("firstName");
        if (firstName != null && !firstName.isEmpty()) {
            staff.setFirstName(firstName);
        }
        
        String lastName = (String) data.get("lastName");
        if (lastName != null && !lastName.isEmpty()) {
            staff.setLastName(lastName);
        }
        
        String position = (String) data.get("position");
        if (position != null && !position.isEmpty()) {
            staff.setPosition(position);
        }
        
        String department = (String) data.get("department");
        if (department != null && !department.isEmpty()) {
            staff.setDepartment(department);
        }
        
        String phone = (String) data.get("phone");
        if (phone != null && !phone.isEmpty()) {
            staff.setPhone(phone);
        }
        
        String email = (String) data.get("email");
        if (email != null && !email.isEmpty()) {
            staff.setEmail(email);
            // Update email in users table too
            updateUserEmail(userId, email);
        }
        
        Object salaryObj = data.get("salary");
        if (salaryObj != null) {
            if (salaryObj instanceof Number) {
                staff.setSalary(((Number) salaryObj).doubleValue());
            } else if (salaryObj instanceof String) {
                try {
                    staff.setSalary(Double.parseDouble((String) salaryObj));
                } catch (NumberFormatException e) {
                    // Ignore invalid salary
                }
            }
        }
        
        // Update hire date if provided
        String hireDateStr = (String) data.get("hireDate");
        if (hireDateStr != null && !hireDateStr.isEmpty()) {
            try {
                staff.setHireDate(Date.valueOf(hireDateStr));
            } catch (Exception e) {
                // Ignore invalid date
            }
        }
        
        return staffDAO.updateStaff(staff);
    }

    public boolean updateDentistProfile(int userId, Map<String, Object> data) {
        Dentist dentist = dentistDAO.getDentistByUserId(userId);
        if (dentist == null) return false;
        
        String dentistName = (String) data.get("dentistName");
        if (dentistName != null && !dentistName.isEmpty()) {
            dentist.setDentistName(dentistName);
        }
        
        String specialization = (String) data.get("specialization");
        if (specialization != null && !specialization.isEmpty()) {
            dentist.setSpecialization(specialization);
        }
        
        String workingHours = (String) data.get("workingHours");
        if (workingHours != null) {
            dentist.setWorkingHours(workingHours);
        }
        
        String phone = (String) data.get("phone");
        if (phone != null && !phone.isEmpty()) {
            dentist.setPhone(phone);
        }
        
        String email = (String) data.get("email");
        if (email != null && !email.isEmpty()) {
            dentist.setEmail(email);
            // Update email in users table too
            updateUserEmail(userId, email);
        }
        
        Object experienceObj = data.get("yearsOfExperience");
        if (experienceObj != null) {
            if (experienceObj instanceof Number) {
                dentist.setYearsOfExperience(((Number) experienceObj).intValue());
            } else if (experienceObj instanceof String) {
                try {
                    dentist.setYearsOfExperience(Integer.parseInt((String) experienceObj));
                } catch (NumberFormatException e) {
                    // Ignore invalid experience
                }
            }
        }
        
        Object feeObj = data.get("consultationFee");
        if (feeObj != null) {
            if (feeObj instanceof Number) {
                dentist.setConsultationFee(((Number) feeObj).doubleValue());
            } else if (feeObj instanceof String) {
                try {
                    dentist.setConsultationFee(Double.parseDouble((String) feeObj));
                } catch (NumberFormatException e) {
                    // Ignore invalid fee
                }
            }
        }
        
        Object availableObj = data.get("isAvailable");
        if (availableObj != null) {
            if (availableObj instanceof Boolean) {
                dentist.setAvailable((Boolean) availableObj);
            } else if (availableObj instanceof String) {
                dentist.setAvailable(Boolean.parseBoolean((String) availableObj));
            }
        }
        
        return dentistDAO.updateDentist(dentist);
    }

    // =====================================================
    // PASSWORD METHODS
    // =====================================================

    public boolean verifyCurrentPassword(int userId, String currentPassword) {
        User user = userDAO.getUserById(userId);
        if (user == null) return false;
        // For demo - compare plain text
        // In production, use proper password hashing
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

    // =====================================================
    // VALIDATION METHODS
    // =====================================================

    public boolean usernameExists(String username) {
        return userDAO.usernameExists(username);
    }

    public boolean usernameExists(String username, int excludeUserId) {
        return userDAO.usernameExists(username, excludeUserId);
    }

    public boolean emailExists(String email) {
        return userDAO.emailExists(email);
    }

    // =====================================================
    // ROLE-SPECIFIC HELPER METHODS
    // =====================================================

    /**
     * Get patient by user ID
     */
    public Patient getPatientByUserId(int userId) {
        return patientDAO.getPatientByUserId(userId);
    }

    /**
     * Get staff by user ID
     */
    public Staff getStaffByUserId(int userId) {
        return staffDAO.getStaffByUserId(userId);
    }

    /**
     * Get dentist by user ID
     */
    public Dentist getDentistByUserId(int userId) {
        return dentistDAO.getDentistByUserId(userId);
    }

    /**
     * Get user role display name
     */
    public String getRoleDisplayName(UserRole role) {
        if (role == null) return "Unknown";
        switch (role) {
            case ADMIN: return "System Administrator";
            case RECEPTION: return "Receptionist";
            case DENTIST: return "Dentist";
            case PATIENT: return "Patient";
            default: return role.name();
        }
    }
}