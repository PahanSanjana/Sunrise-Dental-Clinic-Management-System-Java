package controller;

import dao.*;
import model.DashboardStats;
import model.RecentActivity;
import view.ReceptionDashboardPanel;

import java.util.ArrayList;
import java.util.List;

public class ReceptionDashboardController {
    private ReceptionDashboardPanel view;
    private PatientDAO patientDAO;
    private AppointmentDAO appointmentDAO;
    private BillDAO billDAO;
    private AuditDAO auditDAO;

    public ReceptionDashboardController(ReceptionDashboardPanel view) {
        this.view = view;
        this.patientDAO = new PatientDAO();
        this.appointmentDAO = new AppointmentDAO();
        this.billDAO = new BillDAO();
        this.auditDAO = new AuditDAO();
    }

    public DashboardStats getDashboardStats() {
        DashboardStats stats = new DashboardStats();
        
        stats.setTotalPatients(patientDAO.getPatientCount());
        stats.setTotalAppointments(appointmentDAO.getAppointmentCount());
        stats.setTodayAppointments(appointmentDAO.getTodayAppointmentCount());
        stats.setTotalRevenue(billDAO.getTotalRevenue());
        
        return stats;
    }

    public List<RecentActivity> getRecentActivities() {
        List<RecentActivity> activities = new ArrayList<>();
        
        // Get recent audit logs
        List<model.AuditLog> auditLogs = auditDAO.getAllLogs();
        int count = 0;
        if (auditLogs != null) {
            for (model.AuditLog log : auditLogs) {
                if (count >= 10) break;
                String icon = getActionIcon(log.getAction());
                String message = log.getUsername() + " " + log.getAction().toLowerCase() + 
                                 " " + log.getDescription();
                activities.add(new RecentActivity(icon, message, log.getCreatedAt(), log.getAction()));
                count++;
            }
        }
        
        return activities;
    }

    private String getActionIcon(String action) {
        if (action == null) return "📌";
        switch (action) {
            case "LOGIN": return "🔑";
            case "LOGOUT": return "🚪";
            case "CREATE": return "➕";
            case "UPDATE": return "✏️";
            case "DELETE": return "🗑️";
            case "VIEW": return "👁️";
            default: return "📌";
        }
    }

    public void refreshDashboard() {
        if (view != null) {
            view.loadDashboardData();
        }
    }
}