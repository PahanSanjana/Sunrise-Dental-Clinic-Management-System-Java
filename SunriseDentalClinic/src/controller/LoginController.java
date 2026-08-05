package controller;

import dao.UserDAO;
import java.awt.Cursor;
import model.LoginSession;
import model.User;
import view.Login;
import view.MainFrame;
import view.Signup;
import view.SunriseDentalWelcome;

import javax.swing.*;

public class LoginController {
    private Login loginView;
    private UserDAO userDAO;

    public LoginController(Login loginView) {
        this.loginView = loginView;
        this.userDAO = new UserDAO();
        initController();
    }

    private void initController() {
        loginView.addLoginListener(e -> handleLogin());
        
        // Updated: Navigate to Welcome page on Cancel
        loginView.addCancelListener(e -> {
            loginView.dispose();
            openWelcomeView();
        });
        
        loginView.addSignupLinkListener(e -> {
            loginView.dispose();
            openSignupView();
        });
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

        // Show loading message
        loginView.showSuccess("Authenticating... Please wait.");
        loginView.setCursor(new Cursor(Cursor.WAIT_CURSOR));

        // Use SwingWorker to perform authentication in background
        SwingWorker<User, Void> worker = new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() throws Exception {
                return userDAO.authenticate(username, password);
            }

            @Override
            protected void done() {
                loginView.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                try {
                    User user = get();
                    
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
                    loginView.showSuccess("Login successful! Welcome, " + user.getUsername());
                    loginView.showRole(user.getRole().name());
                    
                    // Open main application after short delay
                    Timer timer = new Timer(1000, e -> {
                        openMainApplication(user);
                        loginView.dispose();
                    });
                    timer.setRepeats(false);
                    timer.start();
                    
                } catch (Exception e) {
                    loginView.showError("Error during login: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void openMainApplication(User user) {
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.configureSidebarForRole(user.getRole());
            mainFrame.setVisible(true);
        });
    }

    private void openSignupView() {
        SwingUtilities.invokeLater(() -> {
            Signup signupView = new Signup();
            new SignupController(signupView);
            signupView.setVisible(true);
        });
    }

    private void openWelcomeView() {
        SwingUtilities.invokeLater(() -> {
            SunriseDentalWelcome welcome = new SunriseDentalWelcome();
            welcome.setVisible(true);
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            
            Login loginView = new Login();
            new LoginController(loginView);
            loginView.setVisible(true);
        });
    }
}