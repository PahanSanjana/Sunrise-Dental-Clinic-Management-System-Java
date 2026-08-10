package controller;

import dao.AppointmentDAO;
import dao.PatientDAO;
import dao.DentistDAO;
import model.Appointment;
import model.Patient;
import model.Dentist;
import view.BookAppointmentPanel;
import view.AppointmentListPanel;
import view.AppointmentDetailsPanel;
import view.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AppointmentController {
    private BookAppointmentPanel bookView;
    private AppointmentListPanel listView;
    private AppointmentDetailsPanel detailsView;
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
     * Get all appointments
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
     * Get appointments by date
     * @param date The date
     * @return List of appointments on the specified date
     */
    public List<Appointment> getAppointmentsByDate(String date) {
        return appointmentDAO.getAppointmentsByDate(date);
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

    // =====================================================
    // HELPER METHODS FOR LIST VIEW
    // =====================================================

    /**
     * Load appointments for the list view
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
}