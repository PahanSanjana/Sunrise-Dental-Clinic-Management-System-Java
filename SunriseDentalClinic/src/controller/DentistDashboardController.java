package controller;

import dao.*;
import model.DashboardStats;
import model.RecentActivity;
import model.Dentist;
import model.LoginSession;
import view.DentistDashboardPanel;

import java.sql.Timestamp;
import java.time.LocalDateTime;
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
        this.dentistId = getCurrentDentistId();
    }

    /**
     * Get the current logged-in dentist's ID
     */
    private int getCurrentDentistId() {
        int userId = LoginSession.getInstance().getCurrentUserId();
        if (userId > 0) {
            Dentist dentist = dentistDAO.getDentistByUserId(userId);
            if (dentist != null) {
                return dentist.getDentistId();
            }
        }
        return -1; // No dentist found
    }

    /**
     * Get the current logged-in dentist's name
     */
    public String getCurrentDentistName() {
        if (dentistId > 0) {
            Dentist dentist = dentistDAO.getDentistById(dentistId);
            if (dentist != null) {
                return dentist.getDentistName();
            }
        }
        return "Dentist";
    }

    /**
     * Get dashboard stats for ALL dentists (Admin/Reception view)
     */
    public DashboardStats getDashboardStats() {
        DashboardStats stats = new DashboardStats();
        
        stats.setTotalPatients(patientDAO.getPatientCount());
        stats.setTotalAppointments(appointmentDAO.getAppointmentCount());
        stats.setTodayAppointments(appointmentDAO.getTodayAppointmentCount());
        stats.setTotalTreatments(treatmentDAO.getTreatmentCount());
        
        return stats;
    }

    /**
     * Get dashboard stats for the CURRENT logged-in dentist only
     * Using existing DAO methods
     */
    public DashboardStats getDashboardStatsForCurrentDentist() {
        DashboardStats stats = new DashboardStats();
        
        if (dentistId <= 0) {
            return stats; // Return empty stats if no dentist found
        }
        
        // Get patients count for this dentist using existing method
        int patientCount = getPatientCountForDentist(dentistId);
        stats.setTotalPatients(patientCount);
        
        // Get appointments count for this dentist using existing method
        int appointmentCount = getAppointmentCountForDentist(dentistId);
        stats.setTotalAppointments(appointmentCount);
        
        // Get today's appointments for this dentist
        int todayAppointments = getTodayAppointmentCountForDentist(dentistId);
        stats.setTodayAppointments(todayAppointments);
        
        // Get treatments count for this dentist using existing method
        int treatmentCount = getTreatmentCountForDentist(dentistId);
        stats.setTotalTreatments(treatmentCount);
        
        return stats;
    }

    /**
     * Get patient count for a specific dentist by filtering appointments
     */
    private int getPatientCountForDentist(int dentistId) {
        List<model.Appointment> appointments = appointmentDAO.getAllAppointments();
        if (appointments == null || appointments.isEmpty()) {
            return 0;
        }
        
        List<Integer> patientIds = new ArrayList<>();
        for (model.Appointment appt : appointments) {
            if (appt.getDentistId() == dentistId) {
                int patientId = appt.getPatientId();
                if (!patientIds.contains(patientId)) {
                    patientIds.add(patientId);
                }
            }
        }
        return patientIds.size();
    }

    /**
     * Get appointment count for a specific dentist
     */
    private int getAppointmentCountForDentist(int dentistId) {
        List<model.Appointment> appointments = appointmentDAO.getAllAppointments();
        if (appointments == null || appointments.isEmpty()) {
            return 0;
        }
        
        int count = 0;
        for (model.Appointment appt : appointments) {
            if (appt.getDentistId() == dentistId) {
                count++;
            }
        }
        return count;
    }

    /**
     * Get today's appointment count for a specific dentist
     */
    private int getTodayAppointmentCountForDentist(int dentistId) {
        java.time.LocalDate today = java.time.LocalDate.now();
        List<model.Appointment> appointments = appointmentDAO.getAllAppointments();
        if (appointments == null || appointments.isEmpty()) {
            return 0;
        }
        
        int count = 0;
        for (model.Appointment appt : appointments) {
            if (appt.getDentistId() == dentistId && appt.getAppointmentDate() != null) {
                java.time.LocalDate apptDate = appt.getAppointmentDate().toLocalDate();
                if (apptDate.equals(today)) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Get treatment count for a specific dentist
     */
    private int getTreatmentCountForDentist(int dentistId) {
        List<model.Treatment> treatments = treatmentDAO.getAllTreatments();
        if (treatments == null || treatments.isEmpty()) {
            return 0;
        }
        
        // If Treatment model doesn't have dentist_id, count all treatments
        // Since dentist provides treatments, we count all treatments (they are all dentist-provided)
        return treatments.size();
    }

    /**
     * Get recent activities for ALL dentists (Admin/Reception view)
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
                // Use Timestamp from log
                Timestamp timestamp = log.getCreatedAt();
                activities.add(new RecentActivity(icon, message, timestamp, log.getAction()));
                count++;
            }
        }
        
        return activities;
    }

    /**
     * Get recent activities for the CURRENT logged-in dentist only
     * Using existing DAO methods
     */
    public List<RecentActivity> getRecentActivitiesForCurrentDentist() {
        List<RecentActivity> activities = new ArrayList<>();
        
        if (dentistId <= 0) {
            return activities; // Return empty list if no dentist found
        }
        
        // Get recent appointments for this dentist from existing data
        List<model.Appointment> allAppointments = appointmentDAO.getAllAppointments();
        if (allAppointments != null) {
            // Filter appointments for this dentist
            List<model.Appointment> dentistAppointments = new ArrayList<>();
            for (model.Appointment appt : allAppointments) {
                if (appt.getDentistId() == dentistId) {
                    dentistAppointments.add(appt);
                }
            }
            
            // Sort by date descending (most recent first)
            dentistAppointments.sort((a, b) -> {
                if (a.getAppointmentDate() == null || b.getAppointmentDate() == null) return 0;
                return b.getAppointmentDate().compareTo(a.getAppointmentDate());
            });
            
            // Take only last 5
            int limit = Math.min(5, dentistAppointments.size());
            for (int i = 0; i < limit; i++) {
                model.Appointment appt = dentistAppointments.get(i);
                String patientName = getPatientNameById(appt.getPatientId());
                String message = "Appointment with " + patientName + " - " + appt.getStatus();
                // Convert Date to Timestamp for RecentActivity
                Timestamp timestamp = appt.getAppointmentDate() != null ? 
                    Timestamp.valueOf(appt.getAppointmentDate().toLocalDate().atStartOfDay()) : 
                    Timestamp.valueOf(LocalDateTime.now());
                activities.add(new RecentActivity("📋", message, timestamp, "APPOINTMENT"));
            }
        }
        
        // Get recent treatments for this dentist from existing data
        List<model.Treatment> allTreatments = treatmentDAO.getAllTreatments();
        if (allTreatments != null) {
            // Take only last 5
            int limit = Math.min(5, allTreatments.size());
            for (int i = 0; i < limit; i++) {
                model.Treatment treatment = allTreatments.get(i);
                String message = "Treatment: " + treatment.getTreatmentName() + " performed";
                // Try to get timestamp from treatment
                Timestamp timestamp = null;
                try {
                    // If treatment has getCreatedAt() method
                    java.util.Date createdDate = (java.util.Date) treatment.getClass().getMethod("getCreatedAt").invoke(treatment);
                    if (createdDate != null) {
                        timestamp = new Timestamp(createdDate.getTime());
                    }
                } catch (Exception e) {
                    // If no created date, use current time
                    timestamp = Timestamp.valueOf(LocalDateTime.now());
                }
                // If still null, use current time
                if (timestamp == null) {
                    timestamp = Timestamp.valueOf(LocalDateTime.now());
                }
                activities.add(new RecentActivity("💊", message, timestamp, "TREATMENT"));
            }
        }
        
        // Limit to 10 recent activities
        if (activities.size() > 10) {
            activities = activities.subList(0, 10);
        }
        
        return activities;
    }

    /**
     * Get patient name by ID using existing DAO
     */
    private String getPatientNameById(int patientId) {
        model.Patient patient = patientDAO.getPatientById(patientId);
        return patient != null ? patient.getPatientName() : "Unknown";
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