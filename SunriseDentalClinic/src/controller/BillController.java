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
import view.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class BillController {
    private GenerateBillPanel view;
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
        this.view = view;
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
     * Calculate total amount for a bill
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
}