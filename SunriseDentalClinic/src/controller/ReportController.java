package controller;

import dao.PatientDAO;
import dao.AppointmentDAO;
import dao.TreatmentDAO;
import dao.BillDAO;
import dao.DentistDAO;
import model.Patient;
import model.Appointment;
import model.Treatment;
import model.Bill;
import model.Dentist;
import view.PatientReportPanel;

import java.util.List;

public class ReportController {
    private PatientReportPanel view;
    private PatientDAO patientDAO;
    private AppointmentDAO appointmentDAO;
    private TreatmentDAO treatmentDAO;
    private BillDAO billDAO;
    private DentistDAO dentistDAO;

    public ReportController(PatientReportPanel view) {
        this.view = view;
        this.patientDAO = new PatientDAO();
        this.appointmentDAO = new AppointmentDAO();
        this.treatmentDAO = new TreatmentDAO();
        this.billDAO = new BillDAO();
        this.dentistDAO = new DentistDAO();
    }

    public List<Patient> getAllPatients() {
        return patientDAO.getAllPatients();
    }

    public Patient getPatientDetails(int patientId) {
        return patientDAO.getPatientById(patientId);
    }

    public List<Appointment> getAppointmentsByPatient(int patientId) {
        return appointmentDAO.getAppointmentsByPatient(patientId);
    }

    public List<Treatment> getTreatmentsByPatient(int patientId) {
        // This would need a method in TreatmentDAO to get treatments by patient
        // For now, return all treatments
        return treatmentDAO.getAllTreatments();
    }

    public List<Bill> getBillsByPatient(int patientId) {
        return billDAO.getBillsByPatient(patientId);
    }

    public String getDentistName(int dentistId) {
        Dentist dentist = dentistDAO.getDentistById(dentistId);
        return dentist != null ? dentist.getDentistName() : "Unknown";
    }
}