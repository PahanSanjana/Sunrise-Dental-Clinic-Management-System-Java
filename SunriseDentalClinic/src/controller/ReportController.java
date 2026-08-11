package controller;

import dao.PatientDAO;
import dao.AppointmentDAO;
import dao.TreatmentDAO;
import dao.BillDAO;
import dao.DentistDAO;
import dao.StaffDAO;
import model.Patient;
import model.Appointment;
import model.Treatment;
import model.Bill;
import model.Dentist;
import model.Staff;
import view.PatientReportPanel;
import view.ScheduleReportPanel;
import view.RevenueReportPanel;
import view.ReportDashboardPanel;
import java.util.LinkedHashMap;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class ReportController {
    private PatientReportPanel patientView;
    private ScheduleReportPanel scheduleView;
    private RevenueReportPanel revenueView;
    private ReportDashboardPanel dashboardView;
    private PatientDAO patientDAO;
    private AppointmentDAO appointmentDAO;
    private TreatmentDAO treatmentDAO;
    private BillDAO billDAO;
    private DentistDAO dentistDAO;
    private StaffDAO staffDAO;

    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    /**
     * Constructor for PatientReportPanel
     * @param view The PatientReportPanel instance
     */
    public ReportController(PatientReportPanel view) {
        this.patientView = view;
        this.patientDAO = new PatientDAO();
        this.appointmentDAO = new AppointmentDAO();
        this.treatmentDAO = new TreatmentDAO();
        this.billDAO = new BillDAO();
        this.dentistDAO = new DentistDAO();
        this.staffDAO = new StaffDAO();
    }

    /**
     * Constructor for ScheduleReportPanel
     * @param view The ScheduleReportPanel instance
     */
    public ReportController(ScheduleReportPanel view) {
        this.scheduleView = view;
        this.patientDAO = new PatientDAO();
        this.appointmentDAO = new AppointmentDAO();
        this.treatmentDAO = new TreatmentDAO();
        this.billDAO = new BillDAO();
        this.dentistDAO = new DentistDAO();
        this.staffDAO = new StaffDAO();
    }

    /**
     * Constructor for RevenueReportPanel
     * @param view The RevenueReportPanel instance
     */
    public ReportController(RevenueReportPanel view) {
        this.revenueView = view;
        this.patientDAO = new PatientDAO();
        this.appointmentDAO = new AppointmentDAO();
        this.treatmentDAO = new TreatmentDAO();
        this.billDAO = new BillDAO();
        this.dentistDAO = new DentistDAO();
        this.staffDAO = new StaffDAO();
    }

    /**
     * Constructor for ReportDashboardPanel
     * @param view The ReportDashboardPanel instance
     */
    public ReportController(ReportDashboardPanel view) {
        this.dashboardView = view;
        this.patientDAO = new PatientDAO();
        this.appointmentDAO = new AppointmentDAO();
        this.treatmentDAO = new TreatmentDAO();
        this.billDAO = new BillDAO();
        this.dentistDAO = new DentistDAO();
        this.staffDAO = new StaffDAO();
    }

    // =====================================================
    // PATIENT REPORT METHODS
    // =====================================================

    /**
     * Get all patients
     * @return List of all patients
     */
    public List<Patient> getAllPatients() {
        return patientDAO.getAllPatients();
    }

    /**
     * Get patient details by ID
     * @param patientId The patient ID
     * @return Patient object if found, null otherwise
     */
    public Patient getPatientDetails(int patientId) {
        return patientDAO.getPatientById(patientId);
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
     * Get treatments by patient ID
     * @param patientId The patient ID
     * @return List of treatments for the patient
     */
    public List<Treatment> getTreatmentsByPatient(int patientId) {
        return treatmentDAO.getAllTreatments();
    }

    /**
     * Get bills by patient ID
     * @param patientId The patient ID
     * @return List of bills for the patient
     */
    public List<Bill> getBillsByPatient(int patientId) {
        return billDAO.getBillsByPatient(patientId);
    }

    /**
     * Get patient name by ID
     * @param patientId The patient ID
     * @return Patient name
     */
    public String getPatientName(int patientId) {
        Patient patient = patientDAO.getPatientById(patientId);
        return patient != null ? patient.getPatientName() : "Unknown";
    }

    /**
     * Get dentist name by ID
     * @param dentistId The dentist ID
     * @return Dentist name
     */
    public String getDentistName(int dentistId) {
        Dentist dentist = dentistDAO.getDentistById(dentistId);
        return dentist != null ? dentist.getDentistName() : "Unknown";
    }

    // =====================================================
    // SCHEDULE REPORT METHODS
    // =====================================================

    /**
     * Get all dentists
     * @return List of all dentists
     */
    public List<Dentist> getAllDentists() {
        return dentistDAO.getAllDentists();
    }

    /**
     * Get appointments by period
     * @param period The period (Today, This Week, This Month, Next Week, Next Month)
     * @return List of appointments in the period
     */
    public List<Appointment> getAppointmentsByPeriod(String period) {
        LocalDate today = LocalDate.now();
        String startDate = "";
        String endDate = "";
        
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

    /**
     * Get appointments by date range
     * @param startDate The start date
     * @param endDate The end date
     * @return List of appointments in the date range
     */
    public List<Appointment> getAppointmentsByDateRange(String startDate, String endDate) {
        return appointmentDAO.getAppointmentsByDateRange(startDate, endDate);
    }

    // =====================================================
    // REVENUE REPORT METHODS
    // =====================================================

    /**
     * Get bills by period
     * @param period The period (Today, This Week, This Month, Last Month, This Quarter, This Year)
     * @return List of bills in the period
     */
    public List<Bill> getBillsByPeriod(String period) {
        LocalDate today = LocalDate.now();
        String startDate = "";
        String endDate = "";
        
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
            case "Last Month":
                LocalDate lastMonthStart = today.minusMonths(1).withDayOfMonth(1);
                LocalDate lastMonthEnd = lastMonthStart.withDayOfMonth(lastMonthStart.lengthOfMonth());
                startDate = lastMonthStart.toString();
                endDate = lastMonthEnd.toString();
                break;
            case "This Quarter":
                int quarterMonth = ((today.getMonthValue() - 1) / 3) * 3 + 1;
                LocalDate quarterStart = LocalDate.of(today.getYear(), quarterMonth, 1);
                LocalDate quarterEnd = quarterStart.plusMonths(3).minusDays(1);
                startDate = quarterStart.toString();
                endDate = quarterEnd.toString();
                break;
            case "This Year":
                LocalDate yearStart = LocalDate.of(today.getYear(), 1, 1);
                LocalDate yearEnd = LocalDate.of(today.getYear(), 12, 31);
                startDate = yearStart.toString();
                endDate = yearEnd.toString();
                break;
            default:
                // Default to this month
                LocalDate defaultStart = today.withDayOfMonth(1);
                LocalDate defaultEnd = today.withDayOfMonth(today.lengthOfMonth());
                startDate = defaultStart.toString();
                endDate = defaultEnd.toString();
                break;
        }
        
        return billDAO.getBillsByDateRange(startDate, endDate);
    }

    /**
     * Get bills by date range
     * @param startDate The start date
     * @param endDate The end date
     * @return List of bills in the date range
     */
    public List<Bill> getBillsByDateRange(String startDate, String endDate) {
        return billDAO.getBillsByDateRange(startDate, endDate);
    }

    /**
     * Get bills by status
     * @param status The status to filter by
     * @return List of bills with the specified status
     */
    public List<Bill> getBillsByStatus(String status) {
        return billDAO.getBillsByStatus(status);
    }

    /**
     * Get bills by payment method
     * @param paymentMethod The payment method to filter by
     * @return List of bills with the specified payment method
     */
    public List<Bill> getBillsByPaymentMethod(String paymentMethod) {
        return billDAO.getBillsByPaymentMethod(paymentMethod);
    }

    /**
     * Get total revenue from bills
     * @param bills The list of bills
     * @return Total revenue
     */
    public double getTotalRevenue(List<Bill> bills) {
        double total = 0;
        if (bills != null) {
            for (Bill bill : bills) {
                total += bill.getTotalAmount();
            }
        }
        return total;
    }

    /**
     * Get paid revenue from bills
     * @param bills The list of bills
     * @return Paid revenue
     */
    public double getPaidRevenue(List<Bill> bills) {
        double total = 0;
        if (bills != null) {
            for (Bill bill : bills) {
                if ("Paid".equals(bill.getStatus())) {
                    total += bill.getTotalAmount();
                } else if ("Partial".equals(bill.getStatus())) {
                    total += bill.getAmountPaid();
                }
            }
        }
        return total;
    }

    /**
     * Get pending revenue from bills
     * @param bills The list of bills
     * @return Pending revenue
     */
    public double getPendingRevenue(List<Bill> bills) {
        double total = 0;
        if (bills != null) {
            for (Bill bill : bills) {
                if ("Pending".equals(bill.getStatus())) {
                    total += bill.getTotalAmount();
                } else if ("Partial".equals(bill.getStatus())) {
                    total += bill.getBalance();
                }
            }
        }
        return total;
    }

    /**
     * Get overdue revenue from bills
     * @param bills The list of bills
     * @return Overdue revenue
     */
    public double getOverdueRevenue(List<Bill> bills) {
        double total = 0;
        if (bills != null) {
            for (Bill bill : bills) {
                if ("Overdue".equals(bill.getStatus())) {
                    total += bill.getTotalAmount();
                }
            }
        }
        return total;
    }

    // =====================================================
    // DASHBOARD METHODS
    // =====================================================

    /**
     * Get patient count
     * @return Total number of patients
     */
    public int getPatientCount() {
        return patientDAO.getPatientCount();
    }

    /**
     * Get appointment count
     * @return Total number of appointments
     */
    public int getAppointmentCount() {
        return appointmentDAO.getAppointmentCount();
    }

    /**
     * Get dentist count
     * @return Total number of dentists
     */
    public int getDentistCount() {
        return dentistDAO.getDentistCount();
    }

    /**
     * Get treatment count
     * @return Total number of treatments
     */
    public int getTreatmentCount() {
        return treatmentDAO.getTreatmentCount();
    }

    /**
     * Get staff count
     * @return Total number of staff members
     */
    public int getStaffCount() {
        if (staffDAO != null) {
            return staffDAO.getStaffCount();
        }
        return 0;
    }

    /**
     * Get total revenue from all bills
     * @return Total revenue
     */
    public double getTotalRevenue() {
        return billDAO.getTotalRevenue();
    }

    /**
     * Get recent appointments
     * @param limit Number of recent appointments to get
     * @return List of recent appointments
     */
    public List<Appointment> getRecentAppointments(int limit) {
        return appointmentDAO.getRecentAppointments(limit);
    }

    /**
     * Get recent bills
     * @param limit Number of recent bills to get
     * @return List of recent bills
     */
    public List<Bill> getRecentBills(int limit) {
        return billDAO.getRecentBills(limit);
    }

    /**
     * Get appointment count by status
     * @param status The status to count
     * @return Number of appointments with the specified status
     */
    public int getAppointmentCountByStatus(String status) {
        return appointmentDAO.getAppointmentCountByStatus(status);
    }

    /**
     * Get bill count by status
     * @param status The status to count
     * @return Number of bills with the specified status
     */
    public int getBillCountByStatus(String status) {
        return billDAO.getBillCountByStatus(status);
    }

    // =====================================================
    // HELPER METHODS
    // =====================================================

    /**
     * Calculate revenue by status
     * @param bills The list of bills
     * @return Map with revenue by status
     */
    public Map<String, Double> getRevenueByStatus(List<Bill> bills) {
        Map<String, Double> revenueByStatus = new HashMap<>();
        
        if (bills != null) {
            for (Bill bill : bills) {
                String status = bill.getStatus() != null ? bill.getStatus() : "Unknown";
                double amount = bill.getTotalAmount();
                revenueByStatus.put(status, revenueByStatus.getOrDefault(status, 0.0) + amount);
            }
        }
        
        return revenueByStatus;
    }

    /**
     * Calculate revenue by payment method
     * @param bills The list of bills
     * @return Map with revenue by payment method
     */
    public Map<String, Double> getRevenueByPaymentMethod(List<Bill> bills) {
        Map<String, Double> revenueByMethod = new HashMap<>();
        
        if (bills != null) {
            for (Bill bill : bills) {
                String method = bill.getPaymentMethod() != null ? bill.getPaymentMethod() : "Unknown";
                double amount = bill.getTotalAmount();
                revenueByMethod.put(method, revenueByMethod.getOrDefault(method, 0.0) + amount);
            }
        }
        
        return revenueByMethod;
    }

    /**
     * Calculate monthly revenue trend
     * @param bills The list of bills
     * @return Map with monthly revenue
     */
    public Map<String, Double> getMonthlyRevenueTrend(List<Bill> bills) {
        Map<String, Double> monthlyRevenue = new LinkedHashMap<>();
        
        if (bills != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
            for (Bill bill : bills) {
                if (bill.getBillDate() != null) {
                    String month = bill.getBillDate().toLocalDate().format(formatter);
                    double amount = bill.getTotalAmount();
                    monthlyRevenue.put(month, monthlyRevenue.getOrDefault(month, 0.0) + amount);
                }
            }
        }
        
        return monthlyRevenue;
    }

    /**
     * Get bill count by status
     * @param bills The list of bills
     * @return Map with count by status
     */
    public Map<String, Integer> getBillCountByStatus(List<Bill> bills) {
        Map<String, Integer> countByStatus = new HashMap<>();
        
        if (bills != null) {
            for (Bill bill : bills) {
                String status = bill.getStatus() != null ? bill.getStatus() : "Unknown";
                countByStatus.put(status, countByStatus.getOrDefault(status, 0) + 1);
            }
        }
        
        return countByStatus;
    }

    /**
     * Get appointment count by status from list
     * @param appointments The list of appointments
     * @return Map with count by status
     */
    public Map<String, Integer> getAppointmentCountByStatus(List<Appointment> appointments) {
        Map<String, Integer> countByStatus = new HashMap<>();
        
        if (appointments != null) {
            for (Appointment appt : appointments) {
                String status = appt.getStatus() != null ? appt.getStatus() : "Unknown";
                countByStatus.put(status, countByStatus.getOrDefault(status, 0) + 1);
            }
        }
        
        return countByStatus;
    }
}