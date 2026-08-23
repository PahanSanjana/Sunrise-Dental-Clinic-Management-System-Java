package model;

import model.User.UserRole;

public class LoginSession {
    private static LoginSession instance;
    private User currentUser;

    private LoginSession() {}

    public static synchronized LoginSession getInstance() {
        if (instance == null) {
            instance = new LoginSession();
        }
        return instance;
    }

    // ✅ Login method
    public void login(User user) {
        this.currentUser = user;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public UserRole getCurrentUserRole() {
        return currentUser != null ? currentUser.getRole() : null;
    }

    public void logout() {
        currentUser = null;
    }

    public boolean hasRole(UserRole... roles) {
        if (currentUser == null || roles == null) {
            return false;
        }
        for (UserRole role : roles) {
            if (currentUser.getRole() == role) {
                return true;
            }
        }
        return false;
    }

    public boolean isAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }

    public boolean isReception() {
        return currentUser != null && currentUser.isReception();
    }

    public boolean isDentist() {
        return currentUser != null && currentUser.isDentist();
    }

    public boolean isPatient() {
        return currentUser != null && currentUser.isPatient();
    }

    public String getCurrentUsername() {
        return currentUser != null ? currentUser.getUsername() : null;
    }

    public int getCurrentUserId() {
        return currentUser != null ? currentUser.getUserId() : -1;
    }

    public String getCurrentUserFullName() {
        return currentUser != null ? currentUser.getUsername() : "Guest";
    }

    // ✅ Get current user's role as string
    public String getCurrentUserRoleString() {
        if (currentUser == null) {
            return "GUEST";
        }
        return currentUser.getRole().name();
    }

    // ✅ Get patient ID if logged in user is a patient
    public Integer getCurrentPatientId() {
        if (currentUser != null && currentUser.isPatient()) {
            return currentUser.getPatientId();
        }
        return null;
    }

    // ✅ Get dentist ID if logged in user is a dentist
    public Integer getCurrentDentistId() {
        if (currentUser != null && currentUser.isDentist()) {
            return currentUser.getDentistId();
        }
        return null;
    }

    // ✅ Get staff ID if logged in user is a staff member
    public Integer getCurrentStaffId() {
        if (currentUser != null && currentUser.isStaff()) {
            return currentUser.getStaffId();
        }
        return null;
    }

    // ✅ Check if user has access to a specific page - FIXED
    public boolean hasPageAccess(String cardName) {
        if (currentUser == null) {
            return false;
        }
        return RolePermissions.hasPageAccess(currentUser.getRole(), cardName);
    }

    // ✅ Get the appropriate dashboard card for the current user
    public String getDashboardCard() {
        if (currentUser == null) {
            return "LOGIN";
        }
        
        switch (currentUser.getRole()) {
            case ADMIN:
                return "DASHBOARD";
            case RECEPTION:
                return "DASHBOARD";
            case DENTIST:
                return "DASHBOARD";
            case PATIENT:
                return "DASHBOARD";
            default:
                return "DASHBOARD";
        }
    }

    public void clearSession() {
        currentUser = null;
    }

    public boolean isSessionValid() {
        return currentUser != null && currentUser.isActive();
    }

    @Override
    public String toString() {
        if (currentUser == null) {
            return "No user logged in";
        }
        return "Logged in as: " + currentUser.getUsername() + " (" + currentUser.getRole() + ")";
    }
}