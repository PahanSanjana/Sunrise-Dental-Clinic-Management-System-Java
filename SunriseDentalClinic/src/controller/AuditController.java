package controller;

import dao.AuditDAO;
import dao.UserDAO;
import model.AuditLog;
import model.User;
import view.ActivityLogPanel;

import java.util.List;

public class AuditController {
    private ActivityLogPanel view;
    private AuditDAO auditDAO;
    private UserDAO userDAO;

    public AuditController(ActivityLogPanel view) {
        this.view = view;
        this.auditDAO = new AuditDAO();
        this.userDAO = new UserDAO();
    }

    public List<AuditLog> getAllLogs() {
        return auditDAO.getAllLogs();
    }

    public List<String> getAllUsernames() {
        return auditDAO.getAllUsernames();
    }

    public boolean addLog(AuditLog log) {
        return auditDAO.addLog(log);
    }

    public List<AuditLog> getLogsByUser(int userId) {
        return auditDAO.getLogsByUser(userId);
    }

    public List<AuditLog> getLogsByAction(String action) {
        return auditDAO.getLogsByAction(action);
    }

    public List<AuditLog> getLogsByDateRange(String startDate, String endDate) {
        return auditDAO.getLogsByDateRange(startDate, endDate);
    }

    public int getLogCount() {
        return auditDAO.getLogCount();
    }

    public boolean clearOldLogs(int days) {
        return auditDAO.clearOldLogs(days);
    }

    public String getUsername(int userId) {
        User user = userDAO.getUserById(userId);
        return user != null ? user.getUsername() : "Unknown";
    }
}