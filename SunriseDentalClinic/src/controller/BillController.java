package controller;

import dao.BillDAO;
import dao.PatientDAO;
import dao.AppointmentDAO;
import dao.TreatmentDAO;
import model.Bill;
import model.BillItem;
import model.Patient;
import model.Appointment;
import model.Treatment;
import view.GenerateBillPanel;
import view.BillListPanel;
import view.BillDetailsPanel;
import view.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class BillController {
    private GenerateBillPanel generateView;
    private BillListPanel listView;
    private BillDetailsPanel detailsView;
    private BillDAO billDAO;
    private PatientDAO patientDAO;
    private AppointmentDAO appointmentDAO;
    private TreatmentDAO treatmentDAO;

    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    /**
     * Constructor for GenerateBillPanel
     * @param view The GenerateBillPanel instance
     */
    public BillController(GenerateBillPanel view) {
        this.generateView = view;
        this.billDAO = new BillDAO();
        this.patientDAO = new PatientDAO();
        this.appointmentDAO = new AppointmentDAO();
        this.treatmentDAO = new TreatmentDAO();
    }

    /**
     * Constructor for BillListPanel
     * @param view The BillListPanel instance
     */
    public BillController(BillListPanel view) {
        this.listView = view;
        this.billDAO = new BillDAO();
        this.patientDAO = new PatientDAO();
        this.appointmentDAO = new AppointmentDAO();
        this.treatmentDAO = new TreatmentDAO();
    }

    /**
     * Constructor for BillDetailsPanel
     * @param view The BillDetailsPanel instance
     */
    public BillController(BillDetailsPanel view) {
        this.detailsView = view;
        this.billDAO = new BillDAO();
        this.patientDAO = new PatientDAO();
        this.appointmentDAO = new AppointmentDAO();
        this.treatmentDAO = new TreatmentDAO();
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
     * Get patient name by patient ID
     * @param patientId The patient ID
     * @return Patient name
     */
    public String getPatientName(int patientId) {
        Patient patient = getPatientById(patientId);
        return patient != null ? patient.getPatientName() : "Unknown";
    }

    // =====================================================
    // APPOINTMENT METHODS
    // =====================================================

    /**
     * Get appointments by patient ID
     * @param patientId The patient ID
     * @return List of appointments for the patient
     */
    public List<Appointment> getAppointmentsByPatient(int patientId) {
        return appointmentDAO.getAppointmentsByPatient(patientId);
    }

    /**
     * Get appointment by ID
     * @param appointmentId The appointment ID
     * @return Appointment object if found, null otherwise
     */
    public Appointment getAppointmentById(int appointmentId) {
        return appointmentDAO.getAppointmentById(appointmentId);
    }

    // =====================================================
    // TREATMENT METHODS
    // =====================================================

    /**
     * Get all treatments
     * @return List of all treatments
     */
    public List<Treatment> getAllTreatments() {
        return treatmentDAO.getAllTreatments();
    }

    /**
     * Get treatment by ID
     * @param treatmentId The treatment ID
     * @return Treatment object if found, null otherwise
     */
    public Treatment getTreatmentById(int treatmentId) {
        return treatmentDAO.getTreatmentById(treatmentId);
    }

    /**
     * Get active treatments
     * @return List of active treatments
     */
    public List<Treatment> getActiveTreatments() {
        return treatmentDAO.getActiveTreatments();
    }

    // =====================================================
    // BILL METHODS
    // =====================================================

    /**
     * Generate a new bill with items
     * @param bill The bill to generate
     * @param items The bill items
     * @return true if successful, false otherwise
     */
    public boolean generateBill(Bill bill, List<BillItem> items) {
        return billDAO.generateBill(bill, items);
    }

    /**
     * Get bill by ID
     * @param billId The bill ID
     * @return Bill object if found, null otherwise
     */
    public Bill getBillById(int billId) {
        return billDAO.getBillById(billId);
    }

    /**
     * Get bill by bill number
     * @param billNumber The bill number
     * @return Bill object if found, null otherwise
     */
    public Bill getBillByNumber(String billNumber) {
        return billDAO.getBillByNumber(billNumber);
    }

    /**
     * Get all bills
     * @return List of all bills
     */
    public List<Bill> getAllBills() {
        return billDAO.getAllBills();
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
     * Get bills by status
     * @param status The status to filter by
     * @return List of bills with the specified status
     */
    public List<Bill> getBillsByStatus(String status) {
        return billDAO.getBillsByStatus(status);
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
     * Get bills by payment method
     * @param paymentMethod The payment method to filter by
     * @return List of bills with the specified payment method
     */
    public List<Bill> getBillsByPaymentMethod(String paymentMethod) {
        return billDAO.getBillsByPaymentMethod(paymentMethod);
    }

    /**
     * Get bill items by bill ID
     * @param billId The bill ID
     * @return List of bill items
     */
    public List<BillItem> getBillItemsByBillId(int billId) {
        return billDAO.getBillItemsByBillId(billId);
    }

    /**
     * Update bill information
     * @param bill The bill to update
     * @return true if successful, false otherwise
     */
    public boolean updateBill(Bill bill) {
        return billDAO.updateBill(bill);
    }

    /**
     * Update bill status
     * @param billId The bill ID
     * @param status The new status
     * @return true if successful, false otherwise
     */
    public boolean updateBillStatus(int billId, String status) {
        return billDAO.updateBillStatus(billId, status);
    }

    /**
     * Update bill payment
     * @param billId The bill ID
     * @param amountPaid The amount paid
     * @param paymentMethod The payment method
     * @return true if successful, false otherwise
     */
    public boolean updateBillPayment(int billId, double amountPaid, String paymentMethod) {
        return billDAO.updateBillPayment(billId, amountPaid, paymentMethod);
    }

    /**
     * Delete a bill
     * @param billId The bill ID to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteBill(int billId) {
        return billDAO.deleteBill(billId);
    }

    /**
     * Get bill count
     * @return Total number of bills
     */
    public int getBillCount() {
        return billDAO.getBillCount();
    }

    /**
     * Get total revenue
     * @return Total revenue from all bills
     */
    public double getTotalRevenue() {
        return billDAO.getTotalRevenue();
    }

    /**
     * Get pending bills count
     * @return Number of pending bills
     */
    public int getPendingBillCount() {
        return billDAO.getPendingBillCount();
    }

    /**
     * Get count of bills by status
     * @param status The status to count
     * @return Number of bills with the specified status
     */
    public int getBillCountByStatus(String status) {
        return billDAO.getBillCountByStatus(status);
    }

    /**
     * Get count of bills by date
     * @param date The date
     * @return Number of bills on the specified date
     */
    public int getBillCountByDate(String date) {
        return billDAO.getBillCountByDate(date);
    }

    /**
     * Check if bill number exists
     * @param billNumber The bill number to check
     * @return true if exists, false otherwise
     */
    public boolean billNumberExists(String billNumber) {
        return billDAO.billNumberExists(billNumber);
    }

    // =====================================================
    // HELPER METHODS
    // =====================================================

    /**
     * Generate a unique bill number
     * @return A unique bill number
     */
    public String generateBillNumber() {
        return billDAO.generateBillNumber();
    }

    /**
     * Calculate total amount for a list of bill items
     * @param items The bill items
     * @return The total amount
     */
    public double calculateTotal(List<BillItem> items) {
        double total = 0;
        for (BillItem item : items) {
            total += item.getTotalPrice();
        }
        return total;
    }

    /**
     * Calculate balance for a bill
     * @param totalAmount The total amount
     * @param amountPaid The amount paid
     * @return The balance
     */
    public double calculateBalance(double totalAmount, double amountPaid) {
        double balance = totalAmount - amountPaid;
        return balance < 0 ? 0 : balance;
    }

    /**
     * Validate bill data before saving
     * @param bill The bill to validate
     * @return Error message if invalid, null if valid
     */
    public String validateBill(Bill bill) {
        if (bill.getPatientId() <= 0) {
            return "Patient is required.";
        }
        if (bill.getBillNumber() == null || bill.getBillNumber().isEmpty()) {
            return "Bill Number is required.";
        }
        if (bill.getBillDate() == null) {
            return "Bill Date is required.";
        }
        if (bill.getDueDate() == null) {
            return "Due Date is required.";
        }
        if (bill.getTotalAmount() < 0) {
            return "Total amount cannot be negative.";
        }
        if (bill.getAmountPaid() < 0) {
            return "Amount paid cannot be negative.";
        }
        if (bill.getStatus() == null || bill.getStatus().isEmpty()) {
            return "Status is required.";
        }
        return null;
    }

    /**
     * Validate bill item data
     * @param item The bill item to validate
     * @return Error message if invalid, null if valid
     */
    public String validateBillItem(BillItem item) {
        if (item.getDescription() == null || item.getDescription().isEmpty()) {
            return "Item description is required.";
        }
        if (item.getQuantity() <= 0) {
            return "Quantity must be greater than 0.";
        }
        if (item.getUnitPrice() < 0) {
            return "Unit price cannot be negative.";
        }
        if (item.getTotalPrice() < 0) {
            return "Total price cannot be negative.";
        }
        return null;
    }

    // =====================================================
    // HELPER METHODS FOR LIST VIEW
    // =====================================================

    /**
     * Load bills for the list view
     */
    public void loadBills() {
        if (listView != null) {
            listView.loadBills();
        }
    }

    /**
     * Refresh the bill list
     */
    public void refreshBillList() {
        if (listView != null) {
            listView.loadBills();
        }
    }

    /**
     * Get bills with filters
     * @param searchText The search text
     * @param status The status filter
     * @param dateFilter The date filter
     * @return List of filtered bills
     */
    public List<Bill> getFilteredBills(String searchText, String status, String dateFilter) {
        List<Bill> bills = billDAO.getAllBills();
        
        if (bills == null) return new ArrayList<>();
        
        // Apply status filter
        if (status != null && !status.equals("All Status")) {
            bills.removeIf(b -> !b.getStatus().equals(status));
        }
        
        // Apply date filter
        if (dateFilter != null && !dateFilter.equals("All Dates")) {
            java.time.LocalDate today = java.time.LocalDate.now();
            switch (dateFilter) {
                case "Today":
                    bills.removeIf(b -> !b.getBillDate().toLocalDate().equals(today));
                    break;
                case "This Week":
                    java.time.LocalDate weekStart = today.minusDays(today.getDayOfWeek().getValue() - 1);
                    java.time.LocalDate weekEnd = weekStart.plusDays(6);
                    bills.removeIf(b -> {
                        java.time.LocalDate date = b.getBillDate().toLocalDate();
                        return date.isBefore(weekStart) || date.isAfter(weekEnd);
                    });
                    break;
                case "This Month":
                    java.time.LocalDate monthStart = today.withDayOfMonth(1);
                    java.time.LocalDate monthEnd = today.withDayOfMonth(today.lengthOfMonth());
                    bills.removeIf(b -> {
                        java.time.LocalDate date = b.getBillDate().toLocalDate();
                        return date.isBefore(monthStart) || date.isAfter(monthEnd);
                    });
                    break;
            }
        }
        
        // Apply search filter
        if (searchText != null && !searchText.isEmpty()) {
            bills.removeIf(b -> {
                String patientName = getPatientName(b.getPatientId());
                return !patientName.toLowerCase().contains(searchText.toLowerCase()) &&
                       !b.getBillNumber().toLowerCase().contains(searchText.toLowerCase()) &&
                       !b.getStatus().toLowerCase().contains(searchText.toLowerCase());
            });
        }
        
        return bills;
    }

    /**
     * Get total revenue from a list of bills
     * @param bills The list of bills
     * @return Total revenue
     */
    public double getTotalRevenueFromBills(List<Bill> bills) {
        double total = 0;
        if (bills != null) {
            for (Bill bill : bills) {
                if ("Paid".equals(bill.getStatus()) || "Partial".equals(bill.getStatus())) {
                    total += bill.getTotalAmount();
                }
            }
        }
        return total;
    }

    // =====================================================
    // HELPER METHODS FOR DETAILS VIEW
    // =====================================================

    /**
     * Load bill details
     * @param billId The bill ID
     */
    public void loadBillDetails(int billId) {
        if (detailsView != null) {
            Bill bill = getBillById(billId);
            if (bill != null) {
                List<BillItem> items = getBillItemsByBillId(billId);
                detailsView.displayBill(bill, items);
            }
        }
    }

    /**
     * Print bill
     * @param billId The bill ID
     */
    public void printBill(int billId) {
        // TODO: Implement bill printing functionality
        System.out.println("Printing bill: " + billId);
    }

    /**
     * Send bill via email
     * @param billId The bill ID
     */
    public void sendBillEmail(int billId) {
        // TODO: Implement email sending functionality
        System.out.println("Sending bill email: " + billId);
    }
}