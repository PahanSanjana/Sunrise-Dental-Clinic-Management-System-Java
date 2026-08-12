package controller;

import dao.*;
import model.DashboardStats;
import model.RecentActivity;
import view.AdminDashboardPanel;

import java.util.ArrayList;
import java.util.List;

public class AdminDashboardController {
    private AdminDashboardPanel view;
    private UserDAO userDAO;
    private PatientDAO patientDAO;
    private AppointmentDAO appointmentDAO;
    private BillDAO billDAO;
    private StaffDAO staffDAO;
    private DentistDAO dentistDAO;
    private AuditDAO auditDAO;

    public AdminDashboardController(AdminDashboardPanel view) {
        this.view = view;
        this.userDAO = new UserDAO();
        this.patientDAO = new PatientDAO();
        this.appointmentDAO = new AppointmentDAO();
        this.billDAO = new BillDAO();
        this.staffDAO = new StaffDAO();
        this.dentistDAO = new DentistDAO();
        this.auditDAO = new AuditDAO();
    }

    public DashboardStats getDashboardStats() {
        DashboardStats stats = new DashboardStats();
        
        stats.setTotalUsers(userDAO.getActiveUserCount());
        stats.setTotalPatients(patientDAO.getPatientCount());
        stats.setTotalAppointments(appointmentDAO.getAppointmentCount());
        stats.setTotalRevenue(billDAO.getTotalRevenue());
        stats.setTotalStaff(staffDAO.getStaffCount());
        stats.setTotalDentists(dentistDAO.getDentistCount());
        stats.setTotalTreatments(getTreatmentCount());
        stats.setTodayAppointments(appointmentDAO.getTodayAppointmentCount());
        
        return stats;
    }

    private int getTreatmentCount() {
        try {
            TreatmentDAO treatmentDAO = new TreatmentDAO();
            return treatmentDAO.getTreatmentCount();
        } catch (Exception e) {
            return 0;
        }
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
        
        // If no audit logs, add some sample activities
        if (activities.isEmpty()) {
            activities.add(new RecentActivity("👋", "Welcome to Sunrise Dental", new java.util.Date(), "INFO"));
            activities.add(new RecentActivity("📋", "System ready for use", new java.util.Date(), "INFO"));
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