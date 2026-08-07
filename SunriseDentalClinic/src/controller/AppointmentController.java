package controller;

import dao.AppointmentDAO;
import dao.DentistDAO;
import dao.PatientDAO;
import dao.TreatmentDAO;
import model.Appointment;
import model.Dentist;
import model.Patient;
import model.Treatment;
import view.BookAppointmentPanel;

import javax.swing.*;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AppointmentController {
    private BookAppointmentPanel view;
    private AppointmentDAO appointmentDAO;
    private PatientDAO patientDAO;
    private DentistDAO dentistDAO;
    private TreatmentDAO treatmentDAO;

    public AppointmentController(BookAppointmentPanel view) {
        this.view = view;
        this.appointmentDAO = new AppointmentDAO();
        this.patientDAO = new PatientDAO();
        this.dentistDAO = new DentistDAO();
        this.treatmentDAO = new TreatmentDAO();
    }

    public List<Patient> getAllPatients() {
        return patientDAO.getAllPatients();
    }

    public List<Dentist> getAllDentists() {
        return dentistDAO.getAllDentists();
    }

    public List<Treatment> getAllTreatments() {
        return treatmentDAO.getAllTreatments();
    }

    public boolean bookAppointment(int patientId, int dentistId, int treatmentId, 
                                   String date, String time, String notes) {
        try {
            // Parse date and time
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            
            LocalDate localDate = LocalDate.parse(date, dateFormatter);
            LocalTime localTime = LocalTime.parse(time, timeFormatter);
            
            Date sqlDate = Date.valueOf(localDate);
            Time sqlTime = Time.valueOf(localTime);
            
            // Get treatment duration to calculate end time
            Treatment treatment = treatmentDAO.getTreatmentById(treatmentId);
            LocalTime endTime = localTime.plusMinutes(treatment.getDuration());
            Time sqlEndTime = Time.valueOf(endTime);
            
            // Create appointment object
            Appointment appointment = new Appointment(
                patientId, dentistId, treatmentId,
                sqlDate, sqlTime,
                "Regular appointment", notes
            );
            appointment.setEndTime(sqlEndTime);
            appointment.setStatus("Scheduled");
            
            // Save to database
            return appointmentDAO.addAppointment(appointment);
            
        } catch (Exception e) {
            System.err.println("Error booking appointment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<Appointment> getAppointmentsForDate(String date) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate localDate = LocalDate.parse(date, formatter);
            Date sqlDate = Date.valueOf(localDate);
            return appointmentDAO.getAppointmentsByDate(sqlDate);
        } catch (Exception e) {
            System.err.println("Error getting appointments: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public List<Appointment> getAppointmentsForPatient(int patientId) {
        return appointmentDAO.getAppointmentsByPatient(patientId);
    }

    public List<Appointment> getAppointmentsForDentist(int dentistId) {
        return appointmentDAO.getAppointmentsByDentist(dentistId);
    }

    public boolean cancelAppointment(int appointmentId) {
        return appointmentDAO.updateAppointmentStatus(appointmentId, "Cancelled");
    }

    public boolean confirmAppointment(int appointmentId) {
        return appointmentDAO.updateAppointmentStatus(appointmentId, "Confirmed");
    }

    public boolean completeAppointment(int appointmentId) {
        return appointmentDAO.updateAppointmentStatus(appointmentId, "Completed");
    }

    public Appointment getAppointmentById(int appointmentId) {
        return appointmentDAO.getAppointmentById(appointmentId);
    }

    public List<String> getBookedSlotsForDate(String date) {
        return appointmentDAO.getBookedSlotsForDate(date);
    }
}