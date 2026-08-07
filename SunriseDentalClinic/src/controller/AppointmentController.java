package controller;

import dao.AppointmentDAO;
import dao.PatientDAO;
import dao.DentistDAO;
import model.Appointment;
import model.Patient;
import model.Dentist;
import view.BookAppointmentPanel;

import javax.swing.*;
import java.util.List;

public class AppointmentController {
    private BookAppointmentPanel view;
    private AppointmentDAO appointmentDAO;
    private PatientDAO patientDAO;
    private DentistDAO dentistDAO;

    public AppointmentController(BookAppointmentPanel view) {
        this.view = view;
        this.appointmentDAO = new AppointmentDAO();
        this.patientDAO = new PatientDAO();
        this.dentistDAO = new DentistDAO();
    }

    public List<Patient> getAllPatients() {
        return patientDAO.getAllPatients();
    }

    public List<Dentist> getAllDentists() {
        return dentistDAO.getAllDentists();
    }

    public boolean checkAvailability(int dentistId, String date, String time) {
        return appointmentDAO.checkAvailability(dentistId, date, time);
    }

    public boolean bookAppointment(Appointment appointment) {
        return appointmentDAO.bookAppointment(appointment);
    }

    public List<Appointment> getAppointmentsByPatient(int patientId) {
        return appointmentDAO.getAppointmentsByPatient(patientId);
    }

    public List<Appointment> getAppointmentsByDentist(int dentistId) {
        return appointmentDAO.getAppointmentsByDentist(dentistId);
    }

    public List<Appointment> getAppointmentsByDate(String date) {
        return appointmentDAO.getAppointmentsByDate(date);
    }

    public boolean updateAppointment(Appointment appointment) {
        return appointmentDAO.updateAppointment(appointment);
    }

    public boolean cancelAppointment(int appointmentId) {
        return appointmentDAO.cancelAppointment(appointmentId);
    }

    public Appointment getAppointmentById(int appointmentId) {
        return appointmentDAO.getAppointmentById(appointmentId);
    }
}