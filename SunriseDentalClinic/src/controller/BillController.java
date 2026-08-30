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
import model.User;
import model.User.UserRole;
import model.LoginSession;
import model.RolePermissions;
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
     * Get all patients - ADMIN and RECEPTION only
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

    /**
     * Get patient by user ID
     * @param userId The user ID
     * @return Patient object if found, null otherwise
     */
    public Patient getPatientByUserId(int userId) {
        return patientDAO.getPatientByUserId(userId);
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
     * Get all bills - ADMIN and RECEPTION only
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
     * Delete a bill - ADMIN only
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
    // BILL ITEM METHODS (NEW)
    // =====================================================

    /**
     * Delete all bill items for a bill
     * @param billId The bill ID
     * @return true if successful, false otherwise
     */
    public boolean deleteBillItems(int billId) {
        return billDAO.deleteBillItems(billId);
    }

    /**
     * Insert a bill item
     * @param billId The bill ID
     * @param item The bill item to insert
     * @return true if successful, false otherwise
     */
    public boolean insertBillItem(int billId, BillItem item) {
        return billDAO.insertBillItem(billId, item);
    }

    /**
     * Update bill items (delete all and insert new ones)
     * @param billId The bill ID
     * @param items The list of bill items
     * @return true if successful, false otherwise
     */
    public boolean updateBillItems(int billId, List<BillItem> items) {
        return billDAO.updateBillItems(billId, items);
    }

    // =====================================================
    // ROLE-BASED DATA ACCESS METHODS
    // =====================================================

    /**
     * Get bills based on user role
     * @param user The current logged-in user
     * @return List of bills filtered by role
     */
    public List<Bill> getBillsForUser(User user) {
        if (user == null) {
            return new ArrayList<>();
        }
        return billDAO.getBillsForUser(user);
    }

    /**
     * Get bills for the current logged-in user
     * @return List of bills filtered by current user's role
     */
    public List<Bill> getBillsForCurrentUser() {
        User currentUser = LoginSession.getInstance().getCurrentUser();
        return getBillsForUser(currentUser);
    }

    /**
     * Get bill by ID with permission check
     * @param billId The bill ID
     * @param user The current user
     * @return Bill object if authorized, null otherwise
     */
    public Bill getBillByIdForUser(int billId, User user) {
        if (user == null) {
            return null;
        }
        return billDAO.getBillByIdForUser(billId, user);
    }

    /**
     * Get bill by ID for the current logged-in user
     * @param billId The bill ID
     * @return Bill object if authorized, null otherwise
     */
    public Bill getBillByIdForCurrentUser(int billId) {
        User currentUser = LoginSession.getInstance().getCurrentUser();
        return getBillByIdForUser(billId, currentUser);
    }

    /**
     * Get filtered bills with role-based access
     * @param searchText The search text
     * @param status The status filter
     * @param dateFilter The date filter
     * @param user The current user
     * @return List of filtered bills
     */
    public List<Bill> getFilteredBillsForUser(String searchText, String status, String dateFilter, User user) {
        if (user == null) {
            return new ArrayList<>();
        }
        
        List<Bill> bills = getBillsForUser(user);
        
        if (bills == null || bills.isEmpty()) {
            return new ArrayList<>();
        }
        
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
            String searchLower = searchText.toLowerCase().trim();
            bills.removeIf(b -> {
                String patientName = getPatientName(b.getPatientId());
                return !patientName.toLowerCase().contains(searchLower) &&
                       !b.getBillNumber().toLowerCase().contains(searchLower) &&
                       !b.getStatus().toLowerCase().contains(searchLower);
            });
        }
        
        return bills;
    }

    /**
     * Get filtered bills for the current logged-in user
     * @param searchText The search text
     * @param status The status filter
     * @param dateFilter The date filter
     * @return List of filtered bills
     */
    public List<Bill> getFilteredBillsForCurrentUser(String searchText, String status, String dateFilter) {
        User currentUser = LoginSession.getInstance().getCurrentUser();
        return getFilteredBillsForUser(searchText, status, dateFilter, currentUser);
    }

    /**
     * Get bills with pagination and role-based filtering
     * @param page The page number (0-based)
     * @param pageSize The page size
     * @param user The current user
     * @return List of bills for the page
     */
    public List<Bill> getBillsForUserPaginated(int page, int pageSize, User user) {
        List<Bill> allBills = getBillsForUser(user);
        if (allBills == null || allBills.isEmpty()) {
            return new ArrayList<>();
        }
        
        int start = page * pageSize;
        int end = Math.min(start + pageSize, allBills.size());
        
        if (start >= allBills.size()) {
            return new ArrayList<>();
        }
        
        return allBills.subList(start, end);
    }

    // =====================================================
    // PERMISSION CHECK METHODS
    // =====================================================

    /**
     * Check if user can generate a bill
     * @param user The current user
     * @return true if can generate, false otherwise
     */
    public boolean canGenerateBill(User user) {
        if (user == null) {
            return false;
        }
        return RolePermissions.hasActionPermission(user.getRole(), "ADD_BILLS");
    }

    /**
     * Check if user can edit a bill
     * @param bill The bill
     * @param user The current user
     * @return true if can edit, false otherwise
     */
    public boolean canEditBill(Bill bill, User user) {
        if (user == null || bill == null) {
            return false;
        }
        return RolePermissions.hasActionPermission(user.getRole(), "EDIT_BILLS");
    }

    /**
     * Check if user can delete a bill
     * @param bill The bill
     * @param user The current user
     * @return true if can delete, false otherwise
     */
    public boolean canDeleteBill(Bill bill, User user) {
        if (user == null || bill == null) {
            return false;
        }
        return RolePermissions.hasActionPermission(user.getRole(), "DELETE_BILLS");
    }

    /**
     * Check if user can view bill details
     * @param bill The bill
     * @param user The current user
     * @return true if can view, false otherwise
     */
    public boolean canViewBill(Bill bill, User user) {
        if (user == null || bill == null) {
            return false;
        }
        
        UserRole role = user.getRole();
        
        switch (role) {
            case ADMIN:
            case RECEPTION:
                return true;
                
            case DENTIST:
                // Dentist can view bills from their appointments
                if (user.getDentistId() != null) {
                    // Check if this bill is from an appointment with this dentist
                    return billDAO.getBillByIdForUser(bill.getBillId(), user) != null;
                }
                return false;
                
            case PATIENT:
                // Patient can view their own bills
                if (user.getPatientId() != null) {
                    return bill.getPatientId() == user.getPatientId();
                }
                return false;
                
            default:
                return false;
        }
    }

    /**
     * Check if dentist can add payment method (dentists can only create pending bills)
     * @param user The current user
     * @return true if dentist, false otherwise
     */
    public boolean isDentist(User user) {
        return user != null && user.isDentist();
    }

    /**
     * Get default status for bill based on user role
     * @param user The current user
     * @return Default status
     */
    public String getDefaultBillStatus(User user) {
        if (user == null) {
            return "Pending";
        }
        // Dentists can only create pending bills
        if (user.isDentist()) {
            return "Pending";
        }
        return "Pending";
    }

    // =====================================================
    // BILL GENERATION WITH ROLE-BASED RESTRICTIONS
    // =====================================================

    /**
     * Generate a new bill with role-based restrictions
     * @param bill The bill to generate
     * @param items The bill items
     * @param user The current user
     * @return true if successful, false otherwise
     */
    public boolean generateBillForUser(Bill bill, List<BillItem> items, User user) {
        if (user == null) {
            return false;
        }
        
        if (!canGenerateBill(user)) {
            return false;
        }
        
        // If user is dentist, force status to Pending and remove payment details
        if (user.isDentist()) {
            bill.setStatus("Pending");
            bill.setAmountPaid(0);
            bill.setBalance(bill.getTotalAmount());
            bill.setPaymentMethod(null);
        }
        
        return billDAO.generateBill(bill, items);
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
     * Load bills for the list view based on current user
     */
    public void loadBillsForCurrentUser() {
        if (listView != null) {
            User currentUser = LoginSession.getInstance().getCurrentUser();
            List<Bill> bills = getBillsForUser(currentUser);
            listView.displayBills(bills);
        }
    }

    /**
     * Get bills with filters (backward compatibility)
     * @param searchText The search text
     * @param status The status filter
     * @param dateFilter The date filter
     * @return List of filtered bills
     */
    public List<Bill> getFilteredBills(String searchText, String status, String dateFilter) {
        User currentUser = LoginSession.getInstance().getCurrentUser();
        return getFilteredBillsForUser(searchText, status, dateFilter, currentUser);
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
     * Load bill details with permission check
     * @param billId The bill ID
     * @param user The current user
     */
    public void loadBillDetailsForUser(int billId, User user) {
        if (detailsView != null) {
            Bill bill = getBillByIdForUser(billId, user);
            if (bill != null) {
                List<BillItem> items = getBillItemsByBillId(billId);
                detailsView.displayBill(bill, items);
            } else {
                detailsView.showError("You don't have permission to view this bill.");
            }
        }
    }

    /**
     * Load bill details for the current user
     * @param billId The bill ID
     */
    public void loadBillDetailsForCurrentUser(int billId) {
        User currentUser = LoginSession.getInstance().getCurrentUser();
        loadBillDetailsForUser(billId, currentUser);
    }

    /**
     * Load bill details (backward compatibility)
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

    // =====================================================
    // COUNT METHODS WITH ROLE-BASED FILTERING
    // =====================================================

    /**
     * Get bill count for a user
     * @param user The current user
     * @return Total number of bills for the user
     */
    public int getBillCountForUser(User user) {
        List<Bill> bills = getBillsForUser(user);
        return bills != null ? bills.size() : 0;
    }

    /**
     * Get bill count for the current logged-in user
     * @return Total number of bills for the current user
     */
    public int getBillCountForCurrentUser() {
        User currentUser = LoginSession.getInstance().getCurrentUser();
        return getBillCountForUser(currentUser);
    }

    /**
     * Get bill count by status for a user
     * @param status The status to count
     * @param user The current user
     * @return Number of bills with the specified status
     */
    public int getBillCountByStatusForUser(String status, User user) {
        List<Bill> bills = getBillsForUser(user);
        if (bills == null || bills.isEmpty()) {
            return 0;
        }
        
        int count = 0;
        for (Bill bill : bills) {
            if (bill.getStatus() != null && bill.getStatus().equals(status)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Get revenue for a user
     * @param user The current user
     * @return Total revenue for the user
     */
    public double getRevenueForUser(User user) {
        List<Bill> bills = getBillsForUser(user);
        return getTotalRevenueFromBills(bills);
    }

    /**
     * Get revenue for the current logged-in user
     * @return Total revenue for the current user
     */
    public double getRevenueForCurrentUser() {
        User currentUser = LoginSession.getInstance().getCurrentUser();
        return getRevenueForUser(currentUser);
    }
}