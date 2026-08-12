package controller;

import dao.*;
import model.DashboardStats;
import model.RecentActivity;
import model.Dentist;
import model.LoginSession;
import view.DentistDashboardPanel;

import java.util.ArrayList;
import java.util.List;

public class DentistDashboardController {
    private DentistDashboardPanel view;
    private PatientDAO patientDAO;
    private AppointmentDAO appointmentDAO;
    private TreatmentDAO treatmentDAO;
    private AuditDAO auditDAO;
    private DentistDAO dentistDAO;
    private int dentistId;

    public DentistDashboardController(DentistDashboardPanel view) {
        this.view = view;
        this.patientDAO = new PatientDAO();
        this.appointmentDAO = new AppointmentDAO();
        this.treatmentDAO = new TreatmentDAO();
        this.auditDAO = new AuditDAO();
        this.dentistDAO = new DentistDAO();
        
        // Get the logged-in dentist ID
        String username = LoginSession.getInstance().getCurrentUsername();
        if (username != null) {
            // Find dentist by username (assuming dentist username matches)
            this.dentistId = getDentistIdByUser();
        }
    }

    private int getDentistIdByUser() {
        // Get user ID from session
        int userId = LoginSession.getInstance().getCurrentUserId();
        if (userId > 0) {
            Dentist dentist = dentistDAO.getDentistByUserId(userId);
            if (dentist != null) {
                return dentist.getDentistId();
            }
        }
        return -1; // No dentist found
    }

    public DashboardStats getDashboardStats() {
        DashboardStats stats = new DashboardStats();
        
        stats.setTotalPatients(patientDAO.getPatientCount());
        stats.setTotalAppointments(appointmentDAO.getAppointmentCount());
        stats.setTodayAppointments(appointmentDAO.getTodayAppointmentCount());
        stats.setTotalTreatments(treatmentDAO.getTreatmentCount());
        
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