package controller;

import dao.*;
import model.DashboardStats;
import model.RecentActivity;
import model.Patient;
import model.LoginSession;
import view.PatientDashboardPanel;

import java.sql.Timestamp;
import java.time.LocalDateTime;
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
        this.patientId = getCurrentPatientId();
    }

    /**
     * Get the current logged-in patient's ID
     */
    private int getCurrentPatientId() {
        int userId = LoginSession.getInstance().getCurrentUserId();
        if (userId > 0) {
            Patient patient = patientDAO.getPatientByUserId(userId);
            if (patient != null) {
                return patient.getPatientId();
            }
        }
        return -1; // No patient found
    }

    /**
     * Get the current patient's name
     */
    public String getCurrentPatientName() {
        if (patientId > 0) {
            Patient patient = patientDAO.getPatientById(patientId);
            if (patient != null) {
                return patient.getPatientName();
            }
        }
        return "Patient";
    }

    /**
     * Get dashboard stats for ALL patients (Admin view)
     */
    public DashboardStats getDashboardStats() {
        DashboardStats stats = new DashboardStats();
        
        stats.setTotalAppointments(appointmentDAO.getAppointmentCount());
        stats.setTotalTreatments(treatmentDAO.getTreatmentCount());
        stats.setTotalBills(billDAO.getBillCount());
        stats.setActive(true);
        
        return stats;
    }

    /**
     * Get dashboard stats for the CURRENT logged-in patient only
     */
    public DashboardStats getDashboardStatsForCurrentPatient() {
        DashboardStats stats = new DashboardStats();
        
        if (patientId <= 0) {
            return stats; // Return empty stats if no patient found
        }
        
        // Get appointments for this patient
        List<model.Appointment> appointments = appointmentDAO.getAppointmentsByPatient(patientId);
        stats.setTotalAppointments(appointments != null ? appointments.size() : 0);
        
        // Get bills for this patient
        List<model.Bill> bills = billDAO.getBillsByPatient(patientId);
        stats.setTotalBills(bills != null ? bills.size() : 0);
        
        // Get treatments for this patient (from appointments)
        int treatmentCount = getTreatmentCountForPatient(patientId);
        stats.setTotalTreatments(treatmentCount);
        
        // Check if patient is active
        Patient patient = patientDAO.getPatientById(patientId);
        stats.setActive(patient != null);
        
        return stats;
    }

    /**
     * Get treatment count for a specific patient
     * This counts treatments associated with the patient's appointments
     */
    private int getTreatmentCountForPatient(int patientId) {
        List<model.Appointment> appointments = appointmentDAO.getAppointmentsByPatient(patientId);
        if (appointments == null || appointments.isEmpty()) {
            return 0;
        }
        
        // Count unique treatments from appointments
        // Assuming each appointment has a treatment associated
        // If you have a separate treatment table linked to appointments, adjust accordingly
        int count = 0;
        for (model.Appointment appt : appointments) {
            // If appointment has a treatment ID, count it
            // This depends on your database structure
            count++;
        }
        return count;
    }

    /**
     * Get recent activities for ALL patients (Admin view)
     */
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

    /**
     * Get recent activities for the CURRENT logged-in patient only
     */
    public List<RecentActivity> getRecentActivitiesForCurrentPatient() {
        List<RecentActivity> activities = new ArrayList<>();
        
        if (patientId <= 0) {
            return activities; // Return empty list if no patient found
        }
        
        // Get recent appointments for this patient
        List<model.Appointment> appointments = appointmentDAO.getAppointmentsByPatient(patientId);
        if (appointments != null && !appointments.isEmpty()) {
            // Sort by date descending (most recent first)
            appointments.sort((a, b) -> {
                if (a.getAppointmentDate() == null || b.getAppointmentDate() == null) return 0;
                return b.getAppointmentDate().compareTo(a.getAppointmentDate());
            });
            
            // Take only last 5
            int limit = Math.min(5, appointments.size());
            for (int i = 0; i < limit; i++) {
                model.Appointment appt = appointments.get(i);
                String dentistName = getDentistNameById(appt.getDentistId());
                String message = "Appointment with Dr. " + dentistName + " - " + appt.getStatus();
                // Convert Date to Timestamp
                Timestamp timestamp = appt.getAppointmentDate() != null ? 
                    Timestamp.valueOf(appt.getAppointmentDate().toLocalDate().atStartOfDay()) : 
                    Timestamp.valueOf(LocalDateTime.now());
                activities.add(new RecentActivity("📋", message, timestamp, "APPOINTMENT"));
            }
        }
        
        // Get recent bills for this patient
        List<model.Bill> bills = billDAO.getBillsByPatient(patientId);
        if (bills != null && !bills.isEmpty()) {
            // Sort by date descending
            bills.sort((a, b) -> {
                if (a.getBillDate() == null || b.getBillDate() == null) return 0;
                return b.getBillDate().compareTo(a.getBillDate());
            });
            
            // Take only last 5
            int limit = Math.min(5, bills.size());
            for (int i = 0; i < limit; i++) {
                model.Bill bill = bills.get(i);
                String message = "Bill #" + bill.getBillNumber() + " - " + bill.getStatus() + 
                                 " (RS" + bill.getTotalAmount() + ")";
                Timestamp timestamp = bill.getBillDate() != null ? 
                    Timestamp.valueOf(bill.getBillDate().toLocalDate().atStartOfDay()) : 
                    Timestamp.valueOf(LocalDateTime.now());
                activities.add(new RecentActivity("💰", message, timestamp, "BILL"));
            }
        }
        
        // Limit to 10 recent activities
        if (activities.size() > 10) {
            activities = activities.subList(0, 10);
        }
        
        return activities;
    }

    /**
     * Get dentist name by ID
     */
    private String getDentistNameById(int dentistId) {
        DentistDAO dentistDAO = new DentistDAO();
        model.Dentist dentist = dentistDAO.getDentistById(dentistId);
        return dentist != null ? dentist.getDentistName() : "Unknown";
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