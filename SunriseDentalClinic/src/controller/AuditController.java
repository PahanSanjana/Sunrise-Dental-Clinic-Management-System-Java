package controller;

import dao.AuditDAO;
import dao.LoginHistoryDAO;
import dao.UserDAO;
import model.AuditLog;
import model.LoginHistory;
import model.User;
import view.ActivityLogPanel;
import view.LoginHistoryPanel;

import java.sql.Timestamp;
import java.util.List;

public class AuditController {
    private ActivityLogPanel activityView;
    private LoginHistoryPanel loginHistoryView;
    private AuditDAO auditDAO;
    private LoginHistoryDAO loginHistoryDAO;
    private UserDAO userDAO;

    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    /**
     * Constructor for ActivityLogPanel
     * @param view The ActivityLogPanel instance
     */
    public AuditController(ActivityLogPanel view) {
        this.activityView = view;
        this.auditDAO = new AuditDAO();
        this.loginHistoryDAO = new LoginHistoryDAO();
        this.userDAO = new UserDAO();
    }

    /**
     * Constructor for LoginHistoryPanel
     * @param view The LoginHistoryPanel instance
     */
    public AuditController(LoginHistoryPanel view) {
        this.loginHistoryView = view;
        this.auditDAO = new AuditDAO();
        this.loginHistoryDAO = new LoginHistoryDAO();
        this.userDAO = new UserDAO();
    }

    // =====================================================
    // AUDIT LOG METHODS
    // =====================================================

    /**
     * Add a new audit log entry
     * @param log The audit log to add
     * @return true if successful, false otherwise
     */
    public boolean addLog(AuditLog log) {
        return auditDAO.addLog(log);
    }

    /**
     * Get all audit logs
     * @return List of all audit logs
     */
    public List<AuditLog> getAllLogs() {
        return auditDAO.getAllLogs();
    }

    /**
     * Get audit logs by user ID
     * @param userId The user ID
     * @return List of audit logs for the user
     */
    public List<AuditLog> getLogsByUser(int userId) {
        return auditDAO.getLogsByUser(userId);
    }

    /**
     * Get audit logs by action
     * @param action The action to filter by
     * @return List of audit logs with the specified action
     */
    public List<AuditLog> getLogsByAction(String action) {
        return auditDAO.getLogsByAction(action);
    }

    /**
     * Get audit logs by date range
     * @param startDate The start date
     * @param endDate The end date
     * @return List of audit logs in the date range
     */
    public List<AuditLog> getLogsByDateRange(String startDate, String endDate) {
        return auditDAO.getLogsByDateRange(startDate, endDate);
    }

    /**
     * Get all unique usernames from audit logs
     * @return List of unique usernames
     */
    public List<String> getAllUsernames() {
        return auditDAO.getAllUsernames();
    }

    /**
     * Get audit log count
     * @return Total number of audit logs
     */
    public int getLogCount() {
        return auditDAO.getLogCount();
    }

    /**
     * Clear old audit logs
     * @param days Number of days to keep
     * @return true if successful, false otherwise
     */
    public boolean clearOldLogs(int days) {
        return auditDAO.clearOldLogs(days);
    }

    /**
     * Get username by user ID
     * @param userId The user ID
     * @return Username if found, "Unknown" otherwise
     */
    public String getUsername(int userId) {
        User user = userDAO.getUserById(userId);
        return user != null ? user.getUsername() : "Unknown";
    }

    // =====================================================
    // LOGIN HISTORY METHODS
    // =====================================================

    /**
     * Add a new login history record
     * @param login The login history to add
     * @return true if successful, false otherwise
     */
    public boolean addLoginHistory(LoginHistory login) {
        return loginHistoryDAO.addLogin(login);
    }

    /**
     * Update logout time for a login record
     * @param loginId The login ID
     * @param logoutTime The logout time
     * @return true if successful, false otherwise
     */
    public boolean updateLogoutTime(int loginId, Timestamp logoutTime) {
        return loginHistoryDAO.updateLogoutTime(loginId, logoutTime);
    }

    /**
     * Get all login history
     * @return List of all login history records
     */
    public List<LoginHistory> getAllLoginHistory() {
        return loginHistoryDAO.getAllLoginHistory();
    }

    /**
     * Get login history by user ID
     * @param userId The user ID
     * @return List of login history for the user
     */
    public List<LoginHistory> getLoginHistoryByUser(int userId) {
        return loginHistoryDAO.getLoginHistoryByUser(userId);
    }

    /**
     * Get login history by status
     * @param status The status to filter by (Success/Failed)
     * @return List of login history with the specified status
     */
    public List<LoginHistory> getLoginHistoryByStatus(String status) {
        return loginHistoryDAO.getLoginHistoryByStatus(status);
    }

    /**
     * Get login history by date range
     * @param startDate The start date
     * @param endDate The end date
     * @return List of login history in the date range
     */
    public List<LoginHistory> getLoginHistoryByDateRange(String startDate, String endDate) {
        return loginHistoryDAO.getLoginHistoryByDateRange(startDate, endDate);
    }

    /**
     * Get login count by status
     * @param status The status to count (Success/Failed)
     * @return Number of login records with the specified status
     */
    public int getLoginCountByStatus(String status) {
        return loginHistoryDAO.getLoginCountByStatus(status);
    }

    /**
     * Get total login count
     * @return Total number of login records
     */
    public int getLoginCount() {
        return loginHistoryDAO.getLoginCount();
    }

    /**
     * Clear old login history
     * @param days Number of days to keep
     * @return true if successful, false otherwise
     */
    public boolean clearOldLoginHistory(int days) {
        return loginHistoryDAO.clearOldLoginHistory(days);
    }

    // =====================================================
    // HELPER METHODS FOR VIEWS
    // =====================================================

    /**
     * Load audit logs for the activity log view
     */
    public void loadAuditLogs() {
        if (activityView != null) {
            activityView.loadLogs();
        }
    }

    /**
     * Refresh the activity log view
     */
    public void refreshActivityLog() {
        if (activityView != null) {
            activityView.loadLogs();
        }
    }

    /**
     * Load login history for the login history view
     */
    public void loadLoginHistory() {
        if (loginHistoryView != null) {
            loginHistoryView.loadLoginHistory();
        }
    }

    /**
     * Refresh the login history view
     */
    public void refreshLoginHistory() {
        if (loginHistoryView != null) {
            loginHistoryView.loadLoginHistory();
        }
    }

    // =====================================================
    // ADDITIONAL UTILITY METHODS
    // =====================================================

    /**
     * Log user login activity
     * @param userId The user ID
     * @param username The username
     * @param ipAddress The IP address
     * @param userAgent The user agent
     * @param status The login status (Success/Failed)
     * @return The login ID if successful, -1 otherwise
     */
    public int logUserLogin(int userId, String username, String ipAddress, String userAgent, String status) {
        LoginHistory login = new LoginHistory();
        login.setUserId(userId);
        login.setUsername(username);
        login.setLoginTime(new Timestamp(System.currentTimeMillis()));
        login.setIpAddress(ipAddress);
        login.setUserAgent(userAgent);
        login.setStatus(status);
        
        if (loginHistoryDAO.addLogin(login)) {
            return login.getLoginId();
        }
        return -1;
    }

    /**
     * Log user logout activity
     * @param loginId The login ID
     * @return true if successful, false otherwise
     */
    public boolean logUserLogout(int loginId) {
        return loginHistoryDAO.updateLogoutTime(loginId, new Timestamp(System.currentTimeMillis()));
    }

    /**
     * Log audit action
     * @param userId The user ID
     * @param username The username
     * @param action The action performed
     * @param description Description of the action
     * @param ipAddress The IP address
     * @param userAgent The user agent
     * @return true if successful, false otherwise
     */
    public boolean logAuditAction(int userId, String username, String action, 
                                   String description, String ipAddress, String userAgent) {
        AuditLog log = new AuditLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setAction(action);
        log.setDescription(description);
        log.setIpAddress(ipAddress);
        log.setUserAgent(userAgent);
        
        return auditDAO.addLog(log);
    }
}