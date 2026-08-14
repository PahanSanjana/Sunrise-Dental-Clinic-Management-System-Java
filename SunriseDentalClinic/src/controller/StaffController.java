package controller;

import dao.StaffDAO;
import dao.UserDAO;
import model.Staff;
import model.User;
import model.User.UserRole;
import model.LoginSession;
import view.AddStaffPanel;
import view.StaffListPanel;
import view.StaffDetailsPanel;
import view.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StaffController {
    private AddStaffPanel addView;
    private StaffListPanel listView;
    private StaffDetailsPanel detailsView;
    private StaffDAO staffDAO;
    private UserDAO userDAO;

    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    public StaffController(AddStaffPanel view) {
        this.addView = view;
        this.staffDAO = new StaffDAO();
        this.userDAO = new UserDAO();
        initAddController();
    }

    public StaffController(StaffListPanel view) {
        this.listView = view;
        this.staffDAO = new StaffDAO();
        this.userDAO = new UserDAO();
    }

    public StaffController(StaffDetailsPanel view) {
        this.detailsView = view;
        this.staffDAO = new StaffDAO();
        this.userDAO = new UserDAO();
    }

    // =====================================================
    // INITIALIZATION METHODS
    // =====================================================

    private void initAddController() {
        if (addView != null) {
            addView.addSaveListener(e -> handleSaveStaff());
            addView.addClearListener(e -> addView.clearForm());
            addView.addCancelListener(e -> {
                Container parent = addView.getParent();
                while (parent != null && !(parent instanceof MainFrame)) {
                    parent = parent.getParent();
                }
                if (parent instanceof MainFrame) {
                    ((MainFrame) parent).showCard("STAFF_LIST");
                }
            });
        }
    }

    // =====================================================
    // ADD STAFF METHODS
    // =====================================================

    private void handleSaveStaff() {
        if (addView == null) return;
        
        // Get all form values
        String firstName = addView.getFirstName();
        String lastName = addView.getLastName();
        String position = addView.getPosition();
        String department = addView.getDepartment();
        String phone = addView.getPhone();
        String email = addView.getEmail();
        String hireDateStr = addView.getHireDate();
        String salaryStr = addView.getSalary();
        boolean isActive = addView.isActive();

        // Check if login account is requested
        boolean createLogin = addView.isCreateLogin();
        String username = addView.getUsername();
        String password = addView.getPassword();
        String confirmPassword = addView.getConfirmPassword();

        // =============================================
        // VALIDATE REQUIRED FIELDS
        // =============================================

        // Validate Name
        if (firstName.isEmpty() || lastName.isEmpty()) {
            addView.showError("First Name and Last Name are required.");
            return;
        }

        if (firstName.length() < 2 || lastName.length() < 2) {
            addView.showError("Name must be at least 2 characters.");
            return;
        }

        if (!firstName.matches("^[a-zA-Z\\s]+$") || !lastName.matches("^[a-zA-Z\\s]+$")) {
            addView.showError("Name can only contain letters and spaces.");
            return;
        }

        // Validate Position
        if (position.isEmpty()) {
            addView.showError("Position is required.");
            return;
        }

        // Validate Department
        if (department.isEmpty()) {
            addView.showError("Department is required.");
            return;
        }

        // Validate Phone
        if (phone.isEmpty()) {
            addView.showError("Phone number is required.");
            return;
        }
        String phoneDigits = phone.replaceAll("[^0-9]", "");
        if (phoneDigits.length() < 10) {
            addView.showError("Please enter a valid phone number (at least 10 digits).");
            return;
        }

        // Validate Email
        if (email.isEmpty()) {
            addView.showError("Email is required.");
            return;
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            addView.showError("Please enter a valid email address.");
            return;
        }

        // Validate Hire Date
        if (hireDateStr.isEmpty()) {
            addView.showError("Hire Date is required.");
            return;
        }

        Date hireDate = null;
        try {
            LocalDate localDate = LocalDate.parse(hireDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            hireDate = Date.valueOf(localDate);
            
            if (localDate.isAfter(LocalDate.now())) {
                addView.showError("Hire Date cannot be in the future.");
                return;
            }
        } catch (Exception e) {
            addView.showError("Invalid date format. Please use YYYY-MM-DD.");
            return;
        }

        // Validate Salary
        double salary = 0;
        if (salaryStr.isEmpty()) {
            addView.showError("Salary is required.");
            return;
        }
        try {
            salary = Double.parseDouble(salaryStr);
            if (salary < 0) {
                addView.showError("Salary cannot be negative.");
                return;
            }
        } catch (NumberFormatException e) {
            addView.showError("Please enter a valid number for salary.");
            return;
        }

        // =============================================
        // VALIDATE LOGIN CREDENTIALS
        // =============================================
        if (createLogin) {
            if (username.isEmpty()) {
                addView.showError("Username is required for login account.");
                return;
            }
            if (username.length() < 3) {
                addView.showError("Username must be at least 3 characters.");
                return;
            }
            if (!username.matches("^[a-zA-Z0-9_]+$")) {
                addView.showError("Username can only contain letters, numbers, and underscores.");
                return;
            }
            if (userDAO.usernameExists(username)) {
                addView.showError("Username already exists. Please choose another.");
                return;
            }
            
            if (password.isEmpty()) {
                addView.showError("Password is required for login account.");
                return;
            }
            if (password.length() < 6) {
                addView.showError("Password must be at least 6 characters.");
                return;
            }
            if (!password.equals(confirmPassword)) {
                addView.showError("Passwords do not match.");
                return;
            }
        }

        // =============================================
        // CREATE STAFF OBJECT
        // =============================================
        Staff staff = new Staff(
            firstName, lastName, position, department,
            phone, email, hireDate, salary, isActive
        );

        // =============================================
        // SAVE WITH SWINGWORKER
        // =============================================
        addView.showSuccess("Saving staff... Please wait.");
        addView.setCursor(new Cursor(Cursor.WAIT_CURSOR));

        // Store final values for use in SwingWorker
        final Date finalHireDate = hireDate;
        final double finalSalary = salary;
        final String finalFirstName = firstName;
        final String finalLastName = lastName;
        final String finalPosition = position;
        final String finalDepartment = department;
        final String finalPhone = phone;
        final String finalEmail = email;

        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                // 1. Save staff
                boolean staffSaved = staffDAO.addStaff(staff);
                if (!staffSaved) {
                    return false;
                }
                
                // 2. If login required, create user and link
                if (createLogin) {
                    UserRole role = determineStaffRole(finalPosition);
                    int createdBy = LoginSession.getInstance().getCurrentUserId();
                    
                    // ✅ FIX: Create profile data using final variables
                    Map<String, Object> profileData = new HashMap<>();
                    profileData.put("firstName", finalFirstName);
                    profileData.put("lastName", finalLastName);
                    profileData.put("position", finalPosition);
                    profileData.put("department", finalDepartment);
                    profileData.put("phone", finalPhone);
                    profileData.put("email", finalEmail);
                    profileData.put("hireDate", finalHireDate);   // ✅ java.sql.Date
                    profileData.put("salary", finalSalary);       // ✅ Double
                    
                    // Create user with appropriate role
                    User newUser = userDAO.createUserWithProfile(
                        username, password, email, role,
                        createdBy, profileData
                    );
                    
                    if (newUser == null) {
                        return false;
                    }
                    
                    // Link staff to user
                    return staffDAO.linkStaffToUser(staff.getStaffId(), newUser.getUserId());
                }
                
                return true;
            }

            @Override
            protected void done() {
                addView.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                try {
                    boolean success = get();
                    if (success) {
                        String message = "Staff saved successfully! Staff ID: " + staff.getStaffId();
                        if (createLogin) {
                            message += "\nLogin account created for: " + username;
                            message += "\nRole: " + determineStaffRole(finalPosition).name();
                        }
                        addView.showSuccess(message);
                        addView.clearForm();
                        
                        Timer timer = new Timer(2000, e -> {
                            Container parent = addView.getParent();
                            while (parent != null && !(parent instanceof MainFrame)) {
                                parent = parent.getParent();
                            }
                            if (parent instanceof MainFrame) {
                                ((MainFrame) parent).showCard("STAFF_LIST");
                            }
                        });
                        timer.setRepeats(false);
                        timer.start();
                    } else {
                        addView.showError("Failed to save staff. Please try again.");
                    }
                } catch (Exception e) {
                    addView.showError("Error saving staff: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    /**
     * Determine user role based on staff position
     */
    private UserRole determineStaffRole(String position) {
        String pos = position.toLowerCase().trim();
        
        if (pos.contains("reception") || pos.contains("front desk") || pos.contains("receptionist")) {
            return UserRole.RECEPTION;
        } else if (pos.contains("dentist") || pos.contains("doctor") || pos.contains("surgeon")) {
            return UserRole.DENTIST;
        } else if (pos.contains("admin") || pos.contains("manager") || 
                   pos.contains("director") || pos.contains("ceo") || pos.contains("owner")) {
            return UserRole.ADMIN;
        } else {
            return UserRole.RECEPTION;
        }
    }

    // =====================================================
    // READ METHODS
    // =====================================================

    public Staff getStaffById(int staffId) {
        return staffDAO.getStaffById(staffId);
    }

    public Staff getStaffByUserId(int userId) {
        return staffDAO.getStaffByUserId(userId);
    }

    public List<Staff> getAllStaff() {
        return staffDAO.getAllStaff();
    }

    public List<Staff> getStaffByPosition(String position) {
        return staffDAO.getStaffByPosition(position);
    }

    public List<Staff> getStaffByDepartment(String department) {
        return staffDAO.getStaffByDepartment(department);
    }

    public List<Staff> getActiveStaff() {
        return staffDAO.getActiveStaff();
    }

    public List<Staff> searchStaff(String searchTerm) {
        return staffDAO.searchStaff(searchTerm);
    }

    // =====================================================
    // UPDATE METHODS
    // =====================================================

    public boolean updateStaff(Staff staff) {
        return staffDAO.updateStaff(staff);
    }

    public boolean deactivateStaff(int staffId) {
        return staffDAO.deactivateStaff(staffId);
    }

    public boolean activateStaff(int staffId) {
        return staffDAO.activateStaff(staffId);
    }

    public boolean linkStaffToUser(int staffId, int userId) {
        return staffDAO.linkStaffToUser(staffId, userId);
    }

    // =====================================================
    // DELETE METHODS
    // =====================================================

    public boolean deleteStaff(int staffId) {
        return staffDAO.deleteStaff(staffId);
    }

    // =====================================================
    // COUNT METHODS
    // =====================================================

    public int getStaffCount() {
        return staffDAO.getStaffCount();
    }

    public int getActiveStaffCount() {
        return staffDAO.getActiveStaffCount();
    }

    // =====================================================
    // HELPER METHODS
    // =====================================================

    public void loadStaff(String searchText, String filter, String department) {
        if (listView != null) {
            listView.loadStaff();
        }
    }

    public void refreshStaffList() {
        if (listView != null) {
            listView.loadStaff();
        }
    }

    public void navigateBack() {
        if (detailsView != null) {
            Container parent = detailsView.getParent();
            while (parent != null && !(parent instanceof MainFrame)) {
                parent = parent.getParent();
            }
            if (parent instanceof MainFrame) {
                ((MainFrame) parent).showCard("STAFF_LIST");
            }
        }
    }

    public String validateStaff(Staff staff) {
        if (staff.getFirstName() == null || staff.getFirstName().isEmpty()) {
            return "First Name is required.";
        }
        if (staff.getFirstName().length() < 2) {
            return "First Name must be at least 2 characters.";
        }
        if (staff.getLastName() == null || staff.getLastName().isEmpty()) {
            return "Last Name is required.";
        }
        if (staff.getLastName().length() < 2) {
            return "Last Name must be at least 2 characters.";
        }
        if (staff.getPosition() == null || staff.getPosition().isEmpty()) {
            return "Position is required.";
        }
        if (staff.getDepartment() == null || staff.getDepartment().isEmpty()) {
            return "Department is required.";
        }
        if (staff.getPhone() == null || staff.getPhone().isEmpty()) {
            return "Phone number is required.";
        }
        String phoneDigits = staff.getPhone().replaceAll("[^0-9]", "");
        if (phoneDigits.length() < 10) {
            return "Please enter a valid phone number (at least 10 digits).";
        }
        if (staff.getEmail() == null || staff.getEmail().isEmpty()) {
            return "Email is required.";
        }
        if (!staff.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return "Please enter a valid email address.";
        }
        if (staff.getHireDate() == null) {
            return "Hire Date is required.";
        }
        if (staff.getHireDate().toLocalDate().isAfter(LocalDate.now())) {
            return "Hire Date cannot be in the future.";
        }
        if (staff.getSalary() < 0) {
            return "Salary cannot be negative.";
        }
        return null;
    }
}