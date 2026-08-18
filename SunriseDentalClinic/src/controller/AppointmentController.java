package controller;

import dao.AppointmentDAO;
import dao.PatientDAO;
import dao.DentistDAO;
import model.Appointment;
import model.Patient;
import model.Dentist;
import model.User;
import model.User.UserRole;
import model.LoginSession;
import model.RolePermissions;
import view.BookAppointmentPanel;
import view.AppointmentListPanel;
import view.AppointmentDetailsPanel;
import view.DailySchedulePanel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AppointmentController {
    private BookAppointmentPanel bookView;
    private AppointmentListPanel listView;
    private AppointmentDetailsPanel detailsView;
    private DailySchedulePanel scheduleView;
    private AppointmentDAO appointmentDAO;
    private PatientDAO patientDAO;
    private DentistDAO dentistDAO;

    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    /**
     * Constructor for BookAppointmentPanel
     * @param view The BookAppointmentPanel instance
     */
    public AppointmentController(BookAppointmentPanel view) {
        this.bookView = view;
        this.appointmentDAO = new AppointmentDAO();
        this.patientDAO = new PatientDAO();
        this.dentistDAO = new DentistDAO();
    }

    /**
     * Constructor for AppointmentListPanel
     * @param view The AppointmentListPanel instance
     */
    public AppointmentController(AppointmentListPanel view) {
        this.listView = view;
        this.appointmentDAO = new AppointmentDAO();
        this.patientDAO = new PatientDAO();
        this.dentistDAO = new DentistDAO();
    }

    /**
     * Constructor for AppointmentDetailsPanel
     * @param view The AppointmentDetailsPanel instance
     */
    public AppointmentController(AppointmentDetailsPanel view) {
        this.detailsView = view;
        this.appointmentDAO = new AppointmentDAO();
        this.patientDAO = new PatientDAO();
        this.dentistDAO = new DentistDAO();
    }

    /**
     * Constructor for DailySchedulePanel
     * @param view The DailySchedulePanel instance
     */
    public AppointmentController(DailySchedulePanel view) {
        this.scheduleView = view;
        this.appointmentDAO = new AppointmentDAO();
        this.patientDAO = new PatientDAO();
        this.dentistDAO = new DentistDAO();
    }

    // =====================================================
    // PATIENT METHODS
    // =====================================================

    /**
     * Get all patients
     * @return List of all patients
     */
    public List<Patient> getAllPatients() {
        return patientDAO.getAllPatients();
    }

    /**
     * Get patient by ID
     * @param patientId The patient ID
     * @return Patient object if found, null otherwise
     */
    public Patient getPatientById(int patientId) {
        return patientDAO.getPatientById(patientId);
    }

    /**
     * Get patient name by ID
     * @param patientId The patient ID
     * @return Patient name or "Unknown"
     */
    public String getPatientName(int patientId) {
        Patient patient = getPatientById(patientId);
        return patient != null ? patient.getPatientName() : "Unknown";
    }

    // =====================================================
    // DENTIST METHODS
    // =====================================================

    /**
     * Get all dentists
     * @return List of all dentists
     */
    public List<Dentist> getAllDentists() {
        return dentistDAO.getAllDentists();
    }

    /**
     * Get dentist by ID
     * @param dentistId The dentist ID
     * @return Dentist object if found, null otherwise
     */
    public Dentist getDentistById(int dentistId) {
        return dentistDAO.getDentistById(dentistId);
    }

    /**
     * Get all dentists for filter (alias for getAllDentists)
     * @return List of all dentists
     */
    public List<Dentist> getAllDentistsForFilter() {
        return dentistDAO.getAllDentists();
    }

    /**
     * Get dentist name by ID
     * @param dentistId The dentist ID
     * @return Dentist name or "Unknown"
     */
    public String getDentistName(int dentistId) {
        Dentist dentist = getDentistById(dentistId);
        return dentist != null ? dentist.getDentistName() : "Unknown";
    }

    // =====================================================
    // APPOINTMENT METHODS
    // =====================================================

    /**
     * Book a new appointment
     * @param appointment The appointment to book
     * @return true if successful, false otherwise
     */
    public boolean bookAppointment(Appointment appointment) {
        return appointmentDAO.bookAppointment(appointment);
    }

    /**
     * Check if a dentist is available at a specific date and time
     * @param dentistId The dentist ID
     * @param date The date
     * @param time The time
     * @return true if available, false otherwise
     */
    public boolean checkAvailability(int dentistId, String date, String time) {
        return appointmentDAO.checkAvailability(dentistId, date, time);
    }

    /**
     * Get all appointments - ADMIN and RECEPTION only
     * @return List of all appointments
     */
    public List<Appointment> getAllAppointments() {
        return appointmentDAO.getAllAppointments();
    }

    /**
     * Get appointment by ID
     * @param appointmentId The appointment ID
     * @return Appointment object if found, null otherwise
     */
    public Appointment getAppointmentById(int appointmentId) {
        return appointmentDAO.getAppointmentById(appointmentId);
    }

    /**
     * Get appointments by patient ID
     * @param patientId The patient ID
     * @return List of appointments for the patient
     */
    public List<Appointment> getAppointmentsByPatient(int patientId) {
        return appointmentDAO.getAppointmentsByPatient(patientId);
    }

    /**
     * Get appointments by dentist ID
     * @param dentistId The dentist ID
     * @return List of appointments for the dentist
     */
    public List<Appointment> getAppointmentsByDentist(int dentistId) {
        return appointmentDAO.getAppointmentsByDentist(dentistId);
    }

    /**
     * Get appointments by dentist ID and date
     * @param dentistId The dentist ID
     * @param date The date
     * @return List of appointments for the dentist on the specified date
     */
    public List<Appointment> getAppointmentsByDentistAndDate(int dentistId, String date) {
        return appointmentDAO.getAppointmentsByDentistAndDate(dentistId, date);
    }

    /**
     * Get appointments by date
     * @param date The date
     * @return List of appointments on the specified date
     */
    public List<Appointment> getAppointmentsByDate(String date) {
        return appointmentDAO.getAppointmentsByDate(date);
    }

    /**
     * Get appointments by status
     * @param status The status to filter by
     * @return List of appointments with the specified status
     */
    public List<Appointment> getAppointmentsByStatus(String status) {
        return appointmentDAO.getAppointmentsByStatus(status);
    }

    /**
     * Get today's appointments
     * @return List of today's appointments
     */
    public List<Appointment> getTodayAppointments() {
        return appointmentDAO.getTodayAppointments();
    }

    /**
     * Update an appointment
     * @param appointment The appointment to update
     * @return true if successful, false otherwise
     */
    public boolean updateAppointment(Appointment appointment) {
        return appointmentDAO.updateAppointment(appointment);
    }

    /**
     * Cancel an appointment
     * @param appointmentId The appointment ID to cancel
     * @return true if successful, false otherwise
     */
    public boolean cancelAppointment(int appointmentId) {
        return appointmentDAO.cancelAppointment(appointmentId);
    }

    /**
     * Delete an appointment
     * @param appointmentId The appointment ID to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteAppointment(int appointmentId) {
        return appointmentDAO.deleteAppointment(appointmentId);
    }

    /**
     * Get appointment count
     * @return Total number of appointments
     */
    public int getAppointmentCount() {
        return appointmentDAO.getAppointmentCount();
    }

    // =====================================================
    // ROLE-BASED DATA ACCESS METHODS
    // =====================================================

    /**
     * Get appointments based on user role
     * @param user The current logged-in user
     * @return List of appointments filtered by role
     */
    public List<Appointment> getAppointmentsForUser(User user) {
        if (user == null) {
            return new ArrayList<>();
        }
        
        UserRole role = user.getRole();
        
        switch (role) {
            case ADMIN:
            case RECEPTION:
                // Admin and Reception can see all appointments
                return appointmentDAO.getAllAppointments();
                
            case DENTIST:
                // Dentist can only see their own appointments
                if (user.getDentistId() != null && user.getDentistId() > 0) {
                    return appointmentDAO.getAppointmentsByDentist(user.getDentistId());
                }
                return new ArrayList<>();
                
            case PATIENT:
                // Patient can only see their own appointments
                if (user.getPatientId() != null && user.getPatientId() > 0) {
                    return appointmentDAO.getAppointmentsByPatient(user.getPatientId());
                }
                return new ArrayList<>();
                
            default:
                return new ArrayList<>();
        }
    }

    /**
     * Get appointments for the current logged-in user
     * @return List of appointments filtered by current user's role
     */
    public List<Appointment> getAppointmentsForCurrentUser() {
        User currentUser = LoginSession.getInstance().getCurrentUser();
        return getAppointmentsForUser(currentUser);
    }

    /**
     * Get appointment by ID with permission check
     * @param appointmentId The appointment ID
     * @param user The current user
     * @return Appointment object if authorized, null otherwise
     */
    public Appointment getAppointmentByIdForUser(int appointmentId, User user) {
        if (user == null) {
            return null;
        }
        
        Appointment appointment = appointmentDAO.getAppointmentById(appointmentId);
        if (appointment == null) {
            return null;
        }
        
        UserRole role = user.getRole();
        
        switch (role) {
            case ADMIN:
            case RECEPTION:
                // Admin and Reception can view any appointment
                return appointment;
                
            case DENTIST:
                // Dentist can only view their own appointments
                if (user.getDentistId() != null && appointment.getDentistId() == user.getDentistId()) {
                    return appointment;
                }
                return null;
                
            case PATIENT:
                // Patient can only view their own appointments
                if (user.getPatientId() != null && appointment.getPatientId() == user.getPatientId()) {
                    return appointment;
                }
                return null;
                
            default:
                return null;
        }
    }

    /**
     * Get appointment by ID for the current logged-in user
     * @param appointmentId The appointment ID
     * @return Appointment object if authorized, null otherwise
     */
    public Appointment getAppointmentByIdForCurrentUser(int appointmentId) {
        User currentUser = LoginSession.getInstance().getCurrentUser();
        return getAppointmentByIdForUser(appointmentId, currentUser);
    }

    /**
     * Search appointments with role-based filtering
     * @param searchTerm The search term
     * @param user The current user
     * @return List of matching appointments
     */
    public List<Appointment> searchAppointmentsForUser(String searchTerm, User user) {
        if (user == null) {
            return new ArrayList<>();
        }
        
        List<Appointment> appointments = getAppointmentsForUser(user);
        if (appointments == null || appointments.isEmpty() || searchTerm == null || searchTerm.isEmpty()) {
            return appointments;
        }
        
        String searchLower = searchTerm.toLowerCase().trim();
        List<Appointment> filtered = new ArrayList<>();
        
        for (Appointment appt : appointments) {
            String patientName = getPatientName(appt.getPatientId());
            String dentistName = getDentistName(appt.getDentistId());
            
            if (patientName.toLowerCase().contains(searchLower) ||
                dentistName.toLowerCase().contains(searchLower) ||
                (appt.getStatus() != null && appt.getStatus().toLowerCase().contains(searchLower)) ||
                (appt.getReason() != null && appt.getReason().toLowerCase().contains(searchLower))) {
                filtered.add(appt);
            }
        }
        
        return filtered;
    }

    /**
     * Search appointments for the current logged-in user
     * @param searchTerm The search term
     * @return List of matching appointments
     */
    public List<Appointment> searchAppointmentsForCurrentUser(String searchTerm) {
        User currentUser = LoginSession.getInstance().getCurrentUser();
        return searchAppointmentsForUser(searchTerm, currentUser);
    }

    /**
     * Get appointments with pagination and role-based filtering
     * @param page The page number (0-based)
     * @param pageSize The page size
     * @param user The current user
     * @return List of appointments for the page
     */
    public List<Appointment> getAppointmentsForUserPaginated(int page, int pageSize, User user) {
        List<Appointment> allAppointments = getAppointmentsForUser(user);
        if (allAppointments == null || allAppointments.isEmpty()) {
            return new ArrayList<>();
        }
        
        int start = page * pageSize;
        int end = Math.min(start + pageSize, allAppointments.size());
        
        if (start >= allAppointments.size()) {
            return new ArrayList<>();
        }
        
        return allAppointments.subList(start, end);
    }

    // =====================================================
    // PERMISSION CHECK METHODS
    // =====================================================

    /**
     * Check if user can edit an appointment
     * @param appointment The appointment
     * @param user The current user
     * @return true if can edit, false otherwise
     */
    public boolean canEditAppointment(Appointment appointment, User user) {
        if (user == null || appointment == null) {
            return false;
        }
        
        UserRole role = user.getRole();
        
        switch (role) {
            case ADMIN:
                return true;
                
            case RECEPTION:
                return true;
                
            case DENTIST:
                // Dentist can only edit their own appointments
                return user.getDentistId() != null && appointment.getDentistId() == user.getDentistId();
                
            case PATIENT:
                // Patient can only edit their own appointments (if status allows)
                if (user.getPatientId() != null && appointment.getPatientId() == user.getPatientId()) {
                    // Patients can only edit Scheduled or Confirmed appointments
                    return "Scheduled".equals(appointment.getStatus()) || 
                           "Confirmed".equals(appointment.getStatus());
                }
                return false;
                
            default:
                return false;
        }
    }

    /**
     * Check if user can delete an appointment
     * @param appointment The appointment
     * @param user The current user
     * @return true if can delete, false otherwise
     */
    public boolean canDeleteAppointment(Appointment appointment, User user) {
        if (user == null || appointment == null) {
            return false;
        }
        
        UserRole role = user.getRole();
        
        switch (role) {
            case ADMIN:
                return true;
                
            case RECEPTION:
                return true;
                
            case DENTIST:
            case PATIENT:
                return false;
                
            default:
                return false;
        }
    }

    /**
     * Check if user can cancel an appointment
     * @param appointment The appointment
     * @param user The current user
     * @return true if can cancel, false otherwise
     */
    public boolean canCancelAppointment(Appointment appointment, User user) {
        if (user == null || appointment == null) {
            return false;
        }
        
        UserRole role = user.getRole();
        
        switch (role) {
            case ADMIN:
                return true;
                
            case RECEPTION:
                return true;
                
            case DENTIST:
                return user.getDentistId() != null && appointment.getDentistId() == user.getDentistId();
                
            case PATIENT:
                return user.getPatientId() != null && appointment.getPatientId() == user.getPatientId();
                
            default:
                return false;
        }
    }

    /**
     * Check if user can book an appointment
     * @param user The current user
     * @return true if can book, false otherwise
     */
    public boolean canBookAppointment(User user) {
        if (user == null) {
            return false;
        }
        
        return RolePermissions.hasActionPermission(user.getRole(), "ADD_APPOINTMENTS");
    }

    // =====================================================
    // UPDATE METHODS WITH PERMISSION CHECK
    // =====================================================

    /**
     * Update an appointment with permission check
     * @param appointment The appointment to update
     * @param user The current user
     * @return true if successful, false otherwise
     */
    public boolean updateAppointmentForUser(Appointment appointment, User user) {
        if (!canEditAppointment(appointment, user)) {
            return false;
        }
        return appointmentDAO.updateAppointment(appointment);
    }

    /**
     * Cancel an appointment with permission check
     * @param appointmentId The appointment ID to cancel
     * @param user The current user
     * @return true if successful, false otherwise
     */
    public boolean cancelAppointmentForUser(int appointmentId, User user) {
        Appointment appointment = getAppointmentById(appointmentId);
        if (appointment == null) {
            return false;
        }
        
        if (!canCancelAppointment(appointment, user)) {
            return false;
        }
        
        return appointmentDAO.cancelAppointment(appointmentId);
    }

    /**
     * Delete an appointment with permission check
     * @param appointmentId The appointment ID to delete
     * @param user The current user
     * @return true if successful, false otherwise
     */
    public boolean deleteAppointmentForUser(int appointmentId, User user) {
        Appointment appointment = getAppointmentById(appointmentId);
        if (appointment == null) {
            return false;
        }
        
        if (!canDeleteAppointment(appointment, user)) {
            return false;
        }
        
        return appointmentDAO.deleteAppointment(appointmentId);
    }

    // =====================================================
    // HELPER METHODS FOR LIST VIEW
    // =====================================================

    /**
     * Load appointments for the list view (role-based)
     */
    public void loadAppointments() {
        if (listView != null) {
            listView.loadAppointments();
        }
    }

    /**
     * Refresh the appointment list
     */
    public void refreshAppointmentList() {
        if (listView != null) {
            listView.loadAppointments();
        }
    }

    /**
     * Load appointments for the list view based on current user
     */
    public void loadAppointmentsForCurrentUser() {
        if (listView != null) {
            User currentUser = LoginSession.getInstance().getCurrentUser();
            List<Appointment> appointments = getAppointmentsForUser(currentUser);
            listView.displayAppointments(appointments);
        }
    }

    // =====================================================
    // HELPER METHODS FOR SCHEDULE VIEW
    // =====================================================

    /**
     * Load schedule for the daily schedule view
     */
    public void loadSchedule() {
        if (scheduleView != null) {
            scheduleView.loadScheduleData();
        }
    }

    /**
     * Refresh the daily schedule
     */
    public void refreshSchedule() {
        if (scheduleView != null) {
            scheduleView.loadScheduleData();
        }
    }

    /**
     * Load schedule for the daily schedule view based on current user
     * Uses loadScheduleData() which already exists in DailySchedulePanel
     */
    public void loadScheduleForCurrentUser() {
        if (scheduleView != null) {
            // Simply call loadScheduleData() - it will handle filtering internally
            scheduleView.loadScheduleData();
        }
    }

    // =====================================================
    // COUNT METHODS
    // =====================================================

    /**
     * Get appointment count for the current user
     * @param user The current user
     * @return Total number of appointments for the user
     */
    public int getAppointmentCountForUser(User user) {
        List<Appointment> appointments = getAppointmentsForUser(user);
        return appointments != null ? appointments.size() : 0;
    }

    /**
     * Get appointment count for the current logged-in user
     * @return Total number of appointments for the current user
     */
    public int getAppointmentCountForCurrentUser() {
        User currentUser = LoginSession.getInstance().getCurrentUser();
        return getAppointmentCountForUser(currentUser);
    }

    /**
     * Get appointment count by status for a user
     * @param status The status to count
     * @param user The current user
     * @return Number of appointments with the specified status
     */
    public int getAppointmentCountByStatusForUser(String status, User user) {
        List<Appointment> appointments = getAppointmentsForUser(user);
        if (appointments == null || appointments.isEmpty()) {
            return 0;
        }
        
        int count = 0;
        for (Appointment appt : appointments) {
            if (appt.getStatus() != null && appt.getStatus().equals(status)) {
                count++;
            }
        }
        return count;
    }

    // =====================================================
    // VALIDATION METHODS
    // =====================================================

    /**
     * Validate appointment data before booking
     * @param appointment The appointment to validate
     * @return Error message if invalid, null if valid
     */
    public String validateAppointment(Appointment appointment) {
        if (appointment.getPatientId() <= 0) {
            return "Please select a patient.";
        }
        if (appointment.getDentistId() <= 0) {
            return "Please select a dentist.";
        }
        if (appointment.getAppointmentDate() == null) {
            return "Please select a date.";
        }
        if (appointment.getAppointmentTime() == null) {
            return "Please select a time.";
        }
        if (appointment.getAppointmentDate().before(new java.sql.Date(System.currentTimeMillis()))) {
            return "Appointment date cannot be in the past.";
        }
        return null;
    }

    /**
     * Get available time slots for a dentist on a specific date
     * This method returns a list of available time slots
     * @param dentistId The dentist ID
     * @param date The date
     * @return List of available time slots
     */
    public List<String> getAvailableTimeSlots(int dentistId, String date) {
        // List of all possible time slots
        String[] allSlots = {
            "08:00", "08:30", "09:00", "09:30", "10:00", "10:30",
            "11:00", "11:30", "12:00", "12:30", "13:00", "13:30",
            "14:00", "14:30", "15:00", "15:30", "16:00", "16:30"
        };
        
        List<String> availableSlots = new ArrayList<>();
        
        // Get booked appointments for this dentist on this date
        List<Appointment> bookedAppointments = appointmentDAO.getAppointmentsByDentistAndDate(dentistId, date);
        
        // Convert booked times to a set for quick lookup
        List<String> bookedTimes = new ArrayList<>();
        for (Appointment appt : bookedAppointments) {
            if (appt.getAppointmentTime() != null) {
                // Format time to HH:mm
                String timeStr = appt.getAppointmentTime().toString();
                if (timeStr.length() > 5) {
                    timeStr = timeStr.substring(0, 5);
                }
                bookedTimes.add(timeStr);
            }
        }
        
        // Return only available slots
        for (String slot : allSlots) {
            if (!bookedTimes.contains(slot)) {
                availableSlots.add(slot);
            }
        }
        
        return availableSlots;
    }

    /**
     * Check if a time slot is available excluding a specific appointment ID
     * (Useful for updating appointments)
     * @param dentistId The dentist ID
     * @param date The date
     * @param time The time
     * @param excludeAppointmentId The appointment ID to exclude
     * @return true if available, false otherwise
     */
    public boolean checkAvailabilityWithExclusion(int dentistId, String date, String time, int excludeAppointmentId) {
        // Get all appointments for this dentist on this date
        List<Appointment> appointments = appointmentDAO.getAppointmentsByDentistAndDate(dentistId, date);
        
        // Check if any appointment (except the excluded one) has the same time
        for (Appointment appt : appointments) {
            if (appt.getAppointmentId() != excludeAppointmentId && 
                appt.getAppointmentTime() != null) {
                String timeStr = appt.getAppointmentTime().toString();
                if (timeStr.length() > 5) {
                    timeStr = timeStr.substring(0, 5);
                }
                if (timeStr.equals(time)) {
                    return false; // Slot is not available
                }
            }
        }
        
        return true; // Slot is available
    }
}