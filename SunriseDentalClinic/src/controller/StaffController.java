package controller;

import dao.StaffDAO;
import dao.UserDAO;
import model.Staff;
import model.User;
import model.User.UserRole;
import model.LoginSession;
import model.RolePermissions;
import view.AddStaffPanel;
import view.StaffListPanel;
import view.StaffDetailsPanel;
import view.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
            // ✅ REMOVED: save listener - handled directly in the view
            // addView.addSaveListener(e -> handleSaveStaff()); // ← REMOVED
            
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
    // CRUD METHODS
    // =====================================================

    /**
     * Add a new staff member
     * @param staff The staff to add
     * @return true if successful, false otherwise
     */
    public boolean addStaff(Staff staff) {
        return staffDAO.addStaff(staff);
    }

    /**
     * Get staff by ID
     */
    public Staff getStaffById(int staffId) {
        return staffDAO.getStaffById(staffId);
    }

    /**
     * Get staff by user ID
     */
    public Staff getStaffByUserId(int userId) {
        return staffDAO.getStaffByUserId(userId);
    }

    /**
     * Get all staff
     */
    public List<Staff> getAllStaff() {
        return staffDAO.getAllStaff();
    }

    /**
     * Get staff by position
     */
    public List<Staff> getStaffByPosition(String position) {
        return staffDAO.getStaffByPosition(position);
    }

    /**
     * Get staff by department
     */
    public List<Staff> getStaffByDepartment(String department) {
        return staffDAO.getStaffByDepartment(department);
    }

    /**
     * Get active staff
     */
    public List<Staff> getActiveStaff() {
        return staffDAO.getActiveStaff();
    }

    /**
     * Search staff
     */
    public List<Staff> searchStaff(String searchTerm) {
        return staffDAO.searchStaff(searchTerm);
    }

    /**
     * Update staff
     */
    public boolean updateStaff(Staff staff) {
        return staffDAO.updateStaff(staff);
    }

    /**
     * Deactivate staff
     */
    public boolean deactivateStaff(int staffId) {
        return staffDAO.deactivateStaff(staffId);
    }

    /**
     * Activate staff
     */
    public boolean activateStaff(int staffId) {
        return staffDAO.activateStaff(staffId);
    }

    /**
     * Link staff to user
     */
    public boolean linkStaffToUser(int staffId, int userId) {
        return staffDAO.linkStaffToUser(staffId, userId);
    }

    /**
     * Create login account for a staff member
     * @param staffId The staff ID
     * @param username The username
     * @param password The password
     * @return true if successful, false otherwise
     */
    public boolean createLoginForStaff(int staffId, String username, String password) {
        // Get staff details
        Staff staff = getStaffById(staffId);
        if (staff == null) {
            return false;
        }
        
        // Check if username exists
        if (userDAO.usernameExists(username)) {
            return false;
        }
        
        // Determine role based on position
        UserRole role = determineStaffRole(staff.getPosition());
        
        // Create profile data
        Map<String, Object> profileData = new HashMap<>();
        profileData.put("firstName", staff.getFirstName());
        profileData.put("lastName", staff.getLastName());
        profileData.put("position", staff.getPosition());
        profileData.put("department", staff.getDepartment());
        profileData.put("phone", staff.getPhone());
        profileData.put("email", staff.getEmail());
        profileData.put("hireDate", staff.getHireDate());
        profileData.put("salary", staff.getSalary());
        
        // Create user with appropriate role
        int createdBy = LoginSession.getInstance().getCurrentUserId();
        User newUser = userDAO.createUserWithProfile(
            username, password, staff.getEmail(), role,
            createdBy, profileData
        );
        
        if (newUser == null) {
            return false;
        }
        
        // Link staff to user
        return staffDAO.linkStaffToUser(staffId, newUser.getUserId());
    }

    /**
     * Delete staff
     */
    public boolean deleteStaff(int staffId) {
        return staffDAO.deleteStaff(staffId);
    }

    /**
     * Get staff count
     */
    public int getStaffCount() {
        return staffDAO.getStaffCount();
    }

    /**
     * Get active staff count
     */
    public int getActiveStaffCount() {
        return staffDAO.getActiveStaffCount();
    }

    // =====================================================
    // HELPER METHODS
    // =====================================================

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

    // =====================================================
    // ROLE-BASED DATA ACCESS METHODS
    // =====================================================

    /**
     * Get staff based on user role
     * @param user The current logged-in user
     * @return List of staff filtered by role
     */
    public List<Staff> getStaffForUser(User user) {
        if (user == null) {
            return new ArrayList<>();
        }
        return staffDAO.getStaffForUser(user);
    }

    /**
     * Get staff for the current logged-in user
     * @return List of staff filtered by current user's role
     */
    public List<Staff> getStaffForCurrentUser() {
        User currentUser = LoginSession.getInstance().getCurrentUser();
        return getStaffForUser(currentUser);
    }

    /**
     * Get staff by ID with permission check
     * @param staffId The staff ID
     * @param user The current user
     * @return Staff object if authorized, null otherwise
     */
    public Staff getStaffByIdForUser(int staffId, User user) {
        if (user == null) {
            return null;
        }
        return staffDAO.getStaffByIdForUser(staffId, user);
    }

    /**
     * Get staff by ID for the current logged-in user
     * @param staffId The staff ID
     * @return Staff object if authorized, null otherwise
     */
    public Staff getStaffByIdForCurrentUser(int staffId) {
        User currentUser = LoginSession.getInstance().getCurrentUser();
        return getStaffByIdForUser(staffId, currentUser);
    }

    /**
     * Search staff with role-based filtering
     * @param searchTerm The search term
     * @param user The current user
     * @return List of matching staff
     */
    public List<Staff> searchStaffForUser(String searchTerm, User user) {
        if (user == null) {
            return new ArrayList<>();
        }
        return staffDAO.searchStaff(searchTerm, user);
    }

    /**
     * Search staff for the current logged-in user
     * @param searchTerm The search term
     * @return List of matching staff
     */
    public List<Staff> searchStaffForCurrentUser(String searchTerm) {
        User currentUser = LoginSession.getInstance().getCurrentUser();
        return searchStaffForUser(searchTerm, currentUser);
    }

    /**
     * Get staff with pagination and role-based filtering
     * @param page The page number (0-based)
     * @param pageSize The page size
     * @param user The current user
     * @return List of staff for the page
     */
    public List<Staff> getStaffForUserPaginated(int page, int pageSize, User user) {
        List<Staff> allStaff = getStaffForUser(user);
        if (allStaff == null || allStaff.isEmpty()) {
            return new ArrayList<>();
        }
        
        int start = page * pageSize;
        int end = Math.min(start + pageSize, allStaff.size());
        
        if (start >= allStaff.size()) {
            return new ArrayList<>();
        }
        
        return allStaff.subList(start, end);
    }

    /**
     * Get staff by department with role-based filtering
     * @param department The department to filter by
     * @param user The current user
     * @return List of staff in the specified department
     */
    public List<Staff> getStaffByDepartmentForUser(String department, User user) {
        if (user == null) {
            return new ArrayList<>();
        }
        
        List<Staff> allStaff = getStaffForUser(user);
        if (allStaff == null || allStaff.isEmpty() || department == null || department.isEmpty()) {
            return allStaff;
        }
        
        List<Staff> filtered = new ArrayList<>();
        for (Staff staff : allStaff) {
            if (staff.getDepartment() != null && staff.getDepartment().equals(department)) {
                filtered.add(staff);
            }
        }
        return filtered;
    }

    /**
     * Get active staff with role-based filtering
     * @param user The current user
     * @return List of active staff
     */
    public List<Staff> getActiveStaffForUser(User user) {
        if (user == null) {
            return new ArrayList<>();
        }
        
        List<Staff> allStaff = getStaffForUser(user);
        if (allStaff == null || allStaff.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Staff> active = new ArrayList<>();
        for (Staff staff : allStaff) {
            if (staff.isActive()) {
                active.add(staff);
            }
        }
        return active;
    }

    // =====================================================
    // PERMISSION CHECK METHODS
    // =====================================================

    /**
     * Check if user can view staff
     * @param user The current user
     * @return true if can view, false otherwise
     */
    public boolean canViewStaff(User user) {
        if (user == null) {
            return false;
        }
        return RolePermissions.hasActionPermission(user.getRole(), "VIEW_STAFF");
    }

    /**
     * Check if user can add staff
     * @param user The current user
     * @return true if can add, false otherwise
     */
    public boolean canAddStaff(User user) {
        if (user == null) {
            return false;
        }
        return RolePermissions.hasActionPermission(user.getRole(), "ADD_STAFF");
    }

    /**
     * Check if user can edit staff
     * @param staff The staff member
     * @param user The current user
     * @return true if can edit, false otherwise
     */
    public boolean canEditStaff(Staff staff, User user) {
        if (user == null || staff == null) {
            return false;
        }
        
        UserRole role = user.getRole();
        
        switch (role) {
            case ADMIN:
                return true;
                
            case RECEPTION:
            case DENTIST:
            case PATIENT:
                return false;
                
            default:
                return false;
        }
    }

    /**
     * Check if user can delete staff
     * @param staff The staff member
     * @param user The current user
     * @return true if can delete, false otherwise
     */
    public boolean canDeleteStaff(Staff staff, User user) {
        if (user == null || staff == null) {
            return false;
        }
        
        UserRole role = user.getRole();
        
        switch (role) {
            case ADMIN:
                return true;
                
            case RECEPTION:
            case DENTIST:
            case PATIENT:
                return false;
                
            default:
                return false;
        }
    }

    /**
     * Check if user can toggle staff status
     * @param staff The staff member
     * @param user The current user
     * @return true if can toggle, false otherwise
     */
    public boolean canToggleStaffStatus(Staff staff, User user) {
        if (user == null || staff == null) {
            return false;
        }
        
        UserRole role = user.getRole();
        
        switch (role) {
            case ADMIN:
                return true;
                
            case RECEPTION:
            case DENTIST:
            case PATIENT:
                return false;
                
            default:
                return false;
        }
    }

    // =====================================================
    // UPDATE METHODS WITH PERMISSION CHECK
    // =====================================================

    /**
     * Update staff with permission check
     * @param staff The staff to update
     * @param user The current user
     * @return true if successful, false otherwise
     */
    public boolean updateStaffForUser(Staff staff, User user) {
        if (!canEditStaff(staff, user)) {
            return false;
        }
        return staffDAO.updateStaff(staff);
    }

    /**
     * Delete staff with permission check
     * @param staffId The staff ID to delete
     * @param user The current user
     * @return true if successful, false otherwise
     */
    public boolean deleteStaffForUser(int staffId, User user) {
        Staff staff = getStaffById(staffId);
        if (staff == null) {
            return false;
        }
        
        if (!canDeleteStaff(staff, user)) {
            return false;
        }
        
        return staffDAO.deleteStaff(staffId);
    }

    /**
     * Toggle staff status with permission check
     * @param staffId The staff ID
     * @param user The current user
     * @return true if successful, false otherwise
     */
    public boolean toggleStaffStatusForUser(int staffId, User user) {
        Staff staff = getStaffById(staffId);
        if (staff == null) {
            return false;
        }
        
        if (!canToggleStaffStatus(staff, user)) {
            return false;
        }
        
        boolean success;
        if (staff.isActive()) {
            success = staffDAO.deactivateStaff(staffId);
        } else {
            success = staffDAO.activateStaff(staffId);
        }
        
        return success;
    }

    // =====================================================
    // COUNT METHODS WITH ROLE-BASED FILTERING
    // =====================================================

    /**
     * Get staff count for a user
     * @param user The current user
     * @return Total number of staff for the user
     */
    public int getStaffCountForUser(User user) {
        List<Staff> staffList = getStaffForUser(user);
        return staffList != null ? staffList.size() : 0;
    }

    /**
     * Get staff count for the current logged-in user
     * @return Total number of staff for the current user
     */
    public int getStaffCountForCurrentUser() {
        User currentUser = LoginSession.getInstance().getCurrentUser();
        return getStaffCountForUser(currentUser);
    }

    /**
     * Get active staff count for a user
     * @param user The current user
     * @return Number of active staff
     */
    public int getActiveStaffCountForUser(User user) {
        List<Staff> staffList = getActiveStaffForUser(user);
        return staffList != null ? staffList.size() : 0;
    }

    /**
     * Get staff count by department for a user
     * @param department The department to count
     * @param user The current user
     * @return Number of staff in the specified department
     */
    public int getStaffCountByDepartmentForUser(String department, User user) {
        List<Staff> staffList = getStaffByDepartmentForUser(department, user);
        return staffList != null ? staffList.size() : 0;
    }

    // =====================================================
    // HELPER METHODS FOR DETAILS VIEW
    // =====================================================

    /**
     * Load staff details with permission check
     * @param staffId The staff ID
     * @param user The current user
     */
    public void loadStaffDetailsForUser(int staffId, User user) {
        if (detailsView != null) {
            Staff staff = getStaffByIdForUser(staffId, user);
            if (staff != null) {
                detailsView.displayStaff(staff);
            } else {
                detailsView.showError("You don't have permission to view this staff member.");
            }
        }
    }

    /**
     * Load staff details for the current user
     * @param staffId The staff ID
     */
    public void loadStaffDetailsForCurrentUser(int staffId) {
        User currentUser = LoginSession.getInstance().getCurrentUser();
        loadStaffDetailsForUser(staffId, currentUser);
    }

    // =====================================================
    // LOAD METHODS FOR LIST VIEW
    // =====================================================

    /**
     * Load staff for the list view based on current user
     */
    public void loadStaffForCurrentUser() {
        if (listView != null) {
            User currentUser = LoginSession.getInstance().getCurrentUser();
            List<Staff> staffList = getStaffForUser(currentUser);
            listView.displayStaff(staffList);
        }
    }

    /**
     * Get filtered staff with role-based access
     * @param searchText The search text
     * @param position The position filter
     * @param department The department filter
     * @param filter The status filter (All, Active, Inactive)
     * @param user The current user
     * @return List of filtered staff
     */
    public List<Staff> getFilteredStaffForUser(String searchText, String position, String department, 
                                                String filter, User user) {
        if (user == null) {
            return new ArrayList<>();
        }
        
        List<Staff> staffList = getStaffForUser(user);
        if (staffList == null || staffList.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Apply position filter
        if (position != null && !position.isEmpty() && !position.equals("All Positions")) {
            staffList.removeIf(s -> s.getPosition() == null || !s.getPosition().equals(position));
        }
        
        // Apply department filter
        if (department != null && !department.isEmpty() && !department.equals("All Departments")) {
            staffList.removeIf(s -> s.getDepartment() == null || !s.getDepartment().equals(department));
        }
        
        // Apply status filter
        if (filter != null && !filter.isEmpty() && !filter.equals("All")) {
            if ("Active".equals(filter)) {
                staffList.removeIf(s -> !s.isActive());
            } else if ("Inactive".equals(filter)) {
                staffList.removeIf(s -> s.isActive());
            }
        }
        
        // Apply search filter
        if (searchText != null && !searchText.isEmpty()) {
            String searchLower = searchText.toLowerCase().trim();
            staffList.removeIf(s -> {
                String fullName = (s.getFirstName() + " " + s.getLastName()).toLowerCase();
                boolean nameMatch = fullName.contains(searchLower);
                boolean positionMatch = s.getPosition() != null && 
                                      s.getPosition().toLowerCase().contains(searchLower);
                boolean deptMatch = s.getDepartment() != null && 
                                   s.getDepartment().toLowerCase().contains(searchLower);
                boolean phoneMatch = s.getPhone() != null && s.getPhone().contains(searchText);
                boolean emailMatch = s.getEmail() != null && 
                                   s.getEmail().toLowerCase().contains(searchLower);
                return !nameMatch && !positionMatch && !deptMatch && !phoneMatch && !emailMatch;
            });
        }
        
        return staffList;
    }

    // =====================================================
    // VALIDATION METHODS
    // =====================================================

    /**
     * Check if email already exists for another staff member
     * @param email The email to check
     * @param excludeStaffId Staff ID to exclude (for updates)
     * @return true if exists, false otherwise
     */
    public boolean emailExists(String email, int excludeStaffId) {
        return staffDAO.emailExists(email, excludeStaffId);
    }

    /**
     * Check if phone already exists for another staff member
     * @param phone The phone to check
     * @param excludeStaffId Staff ID to exclude (for updates)
     * @return true if exists, false otherwise
     */
    public boolean phoneExists(String phone, int excludeStaffId) {
        return staffDAO.phoneExists(phone, excludeStaffId);
    }
}