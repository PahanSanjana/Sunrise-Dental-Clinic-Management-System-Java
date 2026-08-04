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