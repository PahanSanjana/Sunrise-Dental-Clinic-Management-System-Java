package model;

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

    public User.UserRole getCurrentUserRole() {
        return currentUser != null ? currentUser.getRole() : null;
    }

    public void logout() {
        currentUser = null;
    }

    public boolean hasRole(User.UserRole... roles) {
        if (currentUser == null) return false;
        for (User.UserRole role : roles) {
            if (currentUser.getRole() == role) {
                return true;
            }
        }
        return false;
    }
}