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
import view.ScheduleReportPanel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReportController {
    private PatientReportPanel patientView;
    private ScheduleReportPanel scheduleView;
    private PatientDAO patientDAO;
    private AppointmentDAO appointmentDAO;
    private TreatmentDAO treatmentDAO;
    private BillDAO billDAO;
    private DentistDAO dentistDAO;

    // Constructor for PatientReportPanel
    public ReportController(PatientReportPanel view) {
        this.patientView = view;
        this.patientDAO = new PatientDAO();
        this.appointmentDAO = new AppointmentDAO();
        this.treatmentDAO = new TreatmentDAO();
        this.billDAO = new BillDAO();
        this.dentistDAO = new DentistDAO();
    }

    // Constructor for ScheduleReportPanel
    public ReportController(ScheduleReportPanel view) {
        this.scheduleView = view;
        this.patientDAO = new PatientDAO();
        this.appointmentDAO = new AppointmentDAO();
        this.treatmentDAO = new TreatmentDAO();
        this.billDAO = new BillDAO();
        this.dentistDAO = new DentistDAO();
    }

    // Patient Report methods
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
        return treatmentDAO.getAllTreatments();
    }

    public List<Bill> getBillsByPatient(int patientId) {
        return billDAO.getBillsByPatient(patientId);
    }

    public String getPatientName(int patientId) {
        Patient patient = patientDAO.getPatientById(patientId);
        return patient != null ? patient.getPatientName() : "Unknown";
    }

    public String getDentistName(int dentistId) {
        Dentist dentist = dentistDAO.getDentistById(dentistId);
        return dentist != null ? dentist.getDentistName() : "Unknown";
    }

    // Schedule Report methods
    public List<Dentist> getAllDentists() {
        return dentistDAO.getAllDentists();
    }

    public List<Appointment> getAppointmentsByPeriod(String period) {
        LocalDate today = LocalDate.now();
        String startDate = "";
        String endDate = "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        switch (period) {
            case "Today":
                startDate = today.toString();
                endDate = today.toString();
                break;
            case "This Week":
                LocalDate weekStart = today.minusDays(today.getDayOfWeek().getValue() - 1);
                LocalDate weekEnd = weekStart.plusDays(6);
                startDate = weekStart.toString();
                endDate = weekEnd.toString();
                break;
            case "This Month":
                LocalDate monthStart = today.withDayOfMonth(1);
                LocalDate monthEnd = today.withDayOfMonth(today.lengthOfMonth());
                startDate = monthStart.toString();
                endDate = monthEnd.toString();
                break;
            case "Next Week":
                LocalDate nextWeekStart = today.plusDays(7 - today.getDayOfWeek().getValue() + 1);
                LocalDate nextWeekEnd = nextWeekStart.plusDays(6);
                startDate = nextWeekStart.toString();
                endDate = nextWeekEnd.toString();
                break;
            case "Next Month":
                LocalDate nextMonthStart = today.plusMonths(1).withDayOfMonth(1);
                LocalDate nextMonthEnd = nextMonthStart.withDayOfMonth(nextMonthStart.lengthOfMonth());
                startDate = nextMonthStart.toString();
                endDate = nextMonthEnd.toString();
                break;
            default:
                // Default to this month
                LocalDate defaultStart = today.withDayOfMonth(1);
                LocalDate defaultEnd = today.withDayOfMonth(today.lengthOfMonth());
                startDate = defaultStart.toString();
                endDate = defaultEnd.toString();
                break;
        }
        
        return appointmentDAO.getAppointmentsByDateRange(startDate, endDate);
    }
}