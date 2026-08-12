package controller;

import dao.*;
import model.DashboardStats;
import model.RecentActivity;
import model.Patient;
import model.LoginSession;
import view.PatientDashboardPanel;

import java.util.ArrayList;
import java.util.List;

public class PatientDashboardController {
    private PatientDashboardPanel view;
    private PatientDAO patientDAO;
    private AppointmentDAO appointmentDAO;
    private TreatmentDAO treatmentDAO;
    private BillDAO billDAO;
    private AuditDAO auditDAO;
    private int patientId;

    public PatientDashboardController(PatientDashboardPanel view) {
        this.view = view;
        this.patientDAO = new PatientDAO();
        this.appointmentDAO = new AppointmentDAO();
        this.treatmentDAO = new TreatmentDAO();
        this.billDAO = new BillDAO();
        this.auditDAO = new AuditDAO();
        
        // Get the logged-in patient ID
        this.patientId = getPatientIdByUser();
    }

    private int getPatientIdByUser() {
        // Get user ID from session
        int userId = LoginSession.getInstance().getCurrentUserId();
        if (userId > 0) {
            Patient patient = patientDAO.getPatientByLoginId(userId);
            if (patient != null) {
                return patient.getPatientId();
            }
        }
        return -1; // No patient found
    }

    public String getPatientName() {
        if (patientId > 0) {
            Patient patient = patientDAO.getPatientById(patientId);
            if (patient != null) {
                return patient.getPatientName();
            }
        }
        return null;
    }

    public DashboardStats getDashboardStats() {
        DashboardStats stats = new DashboardStats();
        
        // Get patient-specific data
        if (patientId > 0) {
            // Get appointments for this patient
            List<model.Appointment> appointments = appointmentDAO.getAppointmentsByPatient(patientId);
            stats.setTotalAppointments(appointments != null ? appointments.size() : 0);
            
            // Get bills for this patient
            List<model.Bill> bills = billDAO.getBillsByPatient(patientId);
            stats.setTotalBills(bills != null ? bills.size() : 0);
            
            // Get treatments for this patient (simplified)
            stats.setTotalTreatments(treatmentDAO.getTreatmentCount());
            
            // Check if patient is active
            Patient patient = patientDAO.getPatientById(patientId);
            stats.setActive(patient != null);
        }
        
        return stats;
    }

    public List<RecentActivity> getRecentActivities() {
        List<RecentActivity> activities = new ArrayList<>();
        
        // Get recent audit logs for this patient
        List<model.AuditLog> auditLogs = auditDAO.getAllLogs();
        int count = 0;
        if (auditLogs != null) {
            for (model.AuditLog log : auditLogs) {
                if (count >= 10) break;
                // Filter logs related to this patient
                if (log.getUserId() == LoginSession.getInstance().getCurrentUserId()) {
                    String icon = getActionIcon(log.getAction());
                    String message = log.getUsername() + " " + log.getAction().toLowerCase() + 
                                     " " + log.getDescription();
                    activities.add(new RecentActivity(icon, message, log.getCreatedAt(), log.getAction()));
                    count++;
                }
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