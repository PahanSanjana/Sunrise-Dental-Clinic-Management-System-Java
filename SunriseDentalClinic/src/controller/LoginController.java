package controller;

import dao.UserDAO;
import model.LoginSession;
import model.User;
import view.LoginView;
import view.MainFrame;

import javax.swing.*;

public class LoginController {
    private LoginView loginView;
    private UserDAO userDAO;

    public LoginController(LoginView loginView) {
        this.loginView = loginView;
        this.userDAO = new UserDAO();
        initController();
    }

    private void initController() {
        loginView.addLoginListener(e -> handleLogin());
        loginView.addCancelListener(e -> System.exit(0));
    }

    private void handleLogin() {
        String username = loginView.getUsername();
        String password = loginView.getPassword();

        // Validate input
        if (username.isEmpty() || password.isEmpty()) {
            loginView.showError("Please enter both username and password.");
            return;
        }

        if (username.length() < 3) {
            loginView.showError("Username must be at least 3 characters.");
            return;
        }

        if (password.length() < 3) {
            loginView.showError("Password must be at least 3 characters.");
            return;
        }

        // Authenticate user
        User user = userDAO.authenticate(username, password);
        
        if (user == null) {
            loginView.showError("Invalid username or password. Please try again.");
            loginView.clearPassword();
            return;
        }

        if (!user.isActive()) {
            loginView.showError("Your account has been deactivated. Please contact administrator.");
            loginView.clearFields();
            return;
        }

        // Login successful
        LoginSession.getInstance().setCurrentUser(user);
        loginView.showSuccess("Login successful! Welcome, " + user.getFullName());
        
        // Open main application
        openMainApplication(user);
        loginView.dispose();
    }

    private void openMainApplication(User user) {
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            // Configure sidebar based on user role
            mainFrame.configureSidebarForRole(user.getRole());
            mainFrame.setVisible(true);
        });
    }
}