package dao;

import db.DBconnection;
import model.Bill;
import model.BillItem;
import model.User;
import model.User.UserRole;
import model.LoginSession;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BillDAO {

    // =====================================================
    // CREATE METHODS
    // =====================================================

    /**
     * Generate a new bill with items
     * @param bill The bill to generate
     * @param items The bill items
     * @return true if successful, false otherwise
     */
    public boolean generateBill(Bill bill, List<BillItem> items) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBconnection.getConnection();
            conn.setAutoCommit(false);
            
            String billSql = "INSERT INTO billing (patient_id, appointment_id, bill_number, bill_date, "
                           + "due_date, subtotal, tax, discount, total_amount, amount_paid, balance, "
                           + "status, payment_method, notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            pstmt = conn.prepareStatement(billSql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, bill.getPatientId());
            
            if (bill.getAppointmentId() > 0) {
                pstmt.setInt(2, bill.getAppointmentId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }
            
            pstmt.setString(3, bill.getBillNumber());
            pstmt.setDate(4, bill.getBillDate());
            pstmt.setDate(5, bill.getDueDate());
            pstmt.setDouble(6, bill.getSubtotal());
            pstmt.setDouble(7, bill.getTax());
            pstmt.setDouble(8, bill.getDiscount());
            pstmt.setDouble(9, bill.getTotalAmount());
            pstmt.setDouble(10, bill.getAmountPaid());
            pstmt.setDouble(11, bill.getBalance());
            pstmt.setString(12, bill.getStatus());
            pstmt.setString(13, bill.getPaymentMethod());
            pstmt.setString(14, bill.getNotes());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                conn.rollback();
                return false;
            }
            
            rs = pstmt.getGeneratedKeys();
            int billId = 0;
            if (rs.next()) {
                billId = rs.getInt(1);
                bill.setBillId(billId);
            } else {
                conn.rollback();
                return false;
            }
            
            // Insert bill items
            String itemSql = "INSERT INTO billing_items (bill_id, treatment_id, description, quantity, unit_price, total_price) "
                           + "VALUES (?, ?, ?, ?, ?, ?)";
            
            for (BillItem item : items) {
                pstmt = conn.prepareStatement(itemSql);
                pstmt.setInt(1, billId);
                
                if (item.getTreatmentId() > 0) {
                    pstmt.setInt(2, item.getTreatmentId());
                } else {
                    pstmt.setNull(2, Types.INTEGER);
                }
                
                pstmt.setString(3, item.getDescription());
                pstmt.setInt(4, item.getQuantity());
                pstmt.setDouble(5, item.getUnitPrice());
                pstmt.setDouble(6, item.getTotalPrice());
                pstmt.executeUpdate();
            }
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("Error generating bill: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // =====================================================
    // READ METHODS
    // =====================================================

    /**
     * Get bill by ID
     * @param billId The bill ID
     * @return Bill object if found, null otherwise
     */
    public Bill getBillById(int billId) {
        String sql = "SELECT * FROM billing WHERE bill_id = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, billId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToBill(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting bill by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get bill by bill number
     * @param billNumber The bill number
     * @return Bill object if found, null otherwise
     */
    public Bill getBillByNumber(String billNumber) {
        String sql = "SELECT * FROM billing WHERE bill_number = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, billNumber);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToBill(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting bill by number: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get all bills - ADMIN and RECEPTION only
     * @return List of all bills
     */
    public List<Bill> getAllBills() {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT * FROM billing ORDER BY bill_date DESC, bill_id DESC";
        
        try (Connection conn = DBconnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                bills.add(mapResultSetToBill(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all bills: " + e.getMessage());
            e.printStackTrace();
        }
        return bills;
    }

    /**
     * Get bills by patient ID
     * @param patientId The patient ID
     * @return List of bills for the patient
     */
    public List<Bill> getBillsByPatient(int patientId) {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT * FROM billing WHERE patient_id = ? ORDER BY bill_date DESC";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                bills.add(mapResultSetToBill(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting bills by patient: " + e.getMessage());
            e.printStackTrace();
        }
        return bills;
    }

    /**
     * Get bills by dentist ID (for DENTIST role)
     * Dentist can see bills related to their appointments
     * @param dentistId The dentist ID
     * @return List of bills for the dentist
     */
    public List<Bill> getBillsForDentist(int dentistId) {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT DISTINCT b.* FROM billing b " +
                     "LEFT JOIN appointments a ON b.appointment_id = a.appointment_id " +
                     "WHERE a.dentist_id = ? OR b.appointment_id IS NULL " +
                     "ORDER BY b.bill_date DESC, b.bill_id DESC";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, dentistId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                bills.add(mapResultSetToBill(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting bills for dentist: " + e.getMessage());
            e.printStackTrace();
        }
        return bills;
    }

    /**
     * Get bills by status
     * @param status The status to filter by
     * @return List of bills with the specified status
     */
    public List<Bill> getBillsByStatus(String status) {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT * FROM billing WHERE status = ? ORDER BY bill_date DESC";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                bills.add(mapResultSetToBill(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting bills by status: " + e.getMessage());
            e.printStackTrace();
        }
        return bills;
    }

    /**
     * Get bills by date range
     * @param startDate The start date
     * @param endDate The end date
     * @return List of bills in the date range
     */
    public List<Bill> getBillsByDateRange(String startDate, String endDate) {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT * FROM billing WHERE bill_date BETWEEN ? AND ? ORDER BY bill_date DESC";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, startDate);
            pstmt.setString(2, endDate);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                bills.add(mapResultSetToBill(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting bills by date range: " + e.getMessage());
            e.printStackTrace();
        }
        return bills;
    }

    /**
     * Get bills by payment method
     * @param paymentMethod The payment method to filter by
     * @return List of bills with the specified payment method
     */
    public List<Bill> getBillsByPaymentMethod(String paymentMethod) {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT * FROM billing WHERE payment_method = ? ORDER BY bill_date DESC";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, paymentMethod);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                bills.add(mapResultSetToBill(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting bills by payment method: " + e.getMessage());
            e.printStackTrace();
        }
        return bills;
    }

    /**
     * Get bills by date
     * @param date The date
     * @return List of bills on the specified date
     */
    public List<Bill> getBillsByDate(String date) {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT * FROM billing WHERE bill_date = ? ORDER BY bill_date DESC";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, date);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                bills.add(mapResultSetToBill(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting bills by date: " + e.getMessage());
            e.printStackTrace();
        }
        return bills;
    }

    /**
     * Get recent bills
     * @param limit Number of recent bills to get
     * @return List of recent bills
     */
    public List<Bill> getRecentBills(int limit) {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT * FROM billing ORDER BY created_at DESC LIMIT ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                bills.add(mapResultSetToBill(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting recent bills: " + e.getMessage());
            e.printStackTrace();
        }
        return bills;
    }

    /**
     * Get bill items by bill ID
     * @param billId The bill ID
     * @return List of bill items
     */
    public List<BillItem> getBillItemsByBillId(int billId) {
        List<BillItem> items = new ArrayList<>();
        String sql = "SELECT * FROM billing_items WHERE bill_id = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, billId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                items.add(mapResultSetToBillItem(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting bill items: " + e.getMessage());
            e.printStackTrace();
        }
        return items;
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
        
        UserRole role = user.getRole();
        
        switch (role) {
            case ADMIN:
            case RECEPTION:
                // Admin and Reception can see all bills
                return getAllBills();
                
            case DENTIST:
                // Dentist can see bills from their appointments
                if (user.getDentistId() != null) {
                    return getBillsForDentist(user.getDentistId());
                }
                return new ArrayList<>();
                
            case PATIENT:
                // Patient can only see their own bills
                if (user.getPatientId() != null) {
                    return getBillsByPatient(user.getPatientId());
                }
                return new ArrayList<>();
                
            default:
                return new ArrayList<>();
        }
    }

    /**
     * Get bill by ID with role-based permission check
     * @param billId The bill ID
     * @param user The current logged-in user
     * @return Bill object if authorized, null otherwise
     */
    public Bill getBillByIdForUser(int billId, User user) {
        if (user == null) {
            return null;
        }
        
        Bill bill = getBillById(billId);
        if (bill == null) {
            return null;
        }
        
        UserRole role = user.getRole();
        
        switch (role) {
            case ADMIN:
            case RECEPTION:
                // Admin and Reception can view any bill
                return bill;
                
            case DENTIST:
                // Dentist can only view bills from their appointments
                if (user.getDentistId() != null) {
                    // Check if this bill is from an appointment with this dentist
                    String sql = "SELECT COUNT(*) FROM billing b " +
                                 "LEFT JOIN appointments a ON b.appointment_id = a.appointment_id " +
                                 "WHERE b.bill_id = ? AND (a.dentist_id = ? OR b.appointment_id IS NULL)";
                    
                    try (Connection conn = DBconnection.getConnection();
                         PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        
                        pstmt.setInt(1, billId);
                        pstmt.setInt(2, user.getDentistId());
                        ResultSet rs = pstmt.executeQuery();
                        
                        if (rs.next() && rs.getInt(1) > 0) {
                            return bill;
                        }
                    } catch (SQLException e) {
                        System.err.println("Error checking bill access for dentist: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
                return null;
                
            case PATIENT:
                // Patient can only view their own bills
                if (user.getPatientId() != null && bill.getPatientId() == user.getPatientId()) {
                    return bill;
                }
                return null;
                
            default:
                return null;
        }
    }

    /**
     * Get bills with filters (status, date range, payment method)
     * @param status The status filter
     * @param startDate The start date
     * @param endDate The end date
     * @param paymentMethod The payment method
     * @param user The current user for role-based filtering
     * @return List of filtered bills
     */
    public List<Bill> getFilteredBills(String status, String startDate, String endDate, 
                                        String paymentMethod, User user) {
        List<Bill> bills = getBillsForUser(user);
        
        if (bills == null || bills.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Apply status filter
        if (status != null && !status.isEmpty() && !status.equals("All Status")) {
            bills.removeIf(b -> !b.getStatus().equals(status));
        }
        
        // Apply date range filter
        if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
            try {
                Date start = Date.valueOf(startDate);
                Date end = Date.valueOf(endDate);
                bills.removeIf(b -> b.getBillDate().before(start) || b.getBillDate().after(end));
            } catch (Exception e) {
                // Invalid date format, skip filter
            }
        }
        
        // Apply payment method filter
        if (paymentMethod != null && !paymentMethod.isEmpty() && !paymentMethod.equals("All Methods")) {
            bills.removeIf(b -> !paymentMethod.equals(b.getPaymentMethod()));
        }
        
        return bills;
    }

    // =====================================================
    // UPDATE METHODS
    // =====================================================

    /**
     * Update bill information
     * @param bill The bill to update
     * @return true if successful, false otherwise
     */
    public boolean updateBill(Bill bill) {
        String sql = "UPDATE billing SET patient_id=?, appointment_id=?, bill_number=?, bill_date=?, "
                   + "due_date=?, subtotal=?, tax=?, discount=?, total_amount=?, amount_paid=?, "
                   + "balance=?, status=?, payment_method=?, notes=? WHERE bill_id=?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, bill.getPatientId());
            
            if (bill.getAppointmentId() > 0) {
                pstmt.setInt(2, bill.getAppointmentId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }
            
            pstmt.setString(3, bill.getBillNumber());
            pstmt.setDate(4, bill.getBillDate());
            pstmt.setDate(5, bill.getDueDate());
            pstmt.setDouble(6, bill.getSubtotal());
            pstmt.setDouble(7, bill.getTax());
            pstmt.setDouble(8, bill.getDiscount());
            pstmt.setDouble(9, bill.getTotalAmount());
            pstmt.setDouble(10, bill.getAmountPaid());
            pstmt.setDouble(11, bill.getBalance());
            pstmt.setString(12, bill.getStatus());
            pstmt.setString(13, bill.getPaymentMethod());
            pstmt.setString(14, bill.getNotes());
            pstmt.setInt(15, bill.getBillId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating bill: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Update bill status
     * @param billId The bill ID
     * @param status The new status
     * @return true if successful, false otherwise
     */
    public boolean updateBillStatus(int billId, String status) {
        String sql = "UPDATE billing SET status = ? WHERE bill_id = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setInt(2, billId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating bill status: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Update bill payment
     * @param billId The bill ID
     * @param amountPaid The amount paid
     * @param paymentMethod The payment method
     * @return true if successful, false otherwise
     */
    public boolean updateBillPayment(int billId, double amountPaid, String paymentMethod) {
        String sql = "UPDATE billing SET amount_paid = ?, balance = total_amount - ?, payment_method = ?, "
                   + "status = CASE WHEN total_amount - ? <= 0 THEN 'Paid' ELSE 'Partial' END WHERE bill_id = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, amountPaid);
            pstmt.setDouble(2, amountPaid);
            pstmt.setString(3, paymentMethod);
            pstmt.setDouble(4, amountPaid);
            pstmt.setInt(5, billId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating bill payment: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
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
        String sql = "DELETE FROM billing_items WHERE bill_id = ?";
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, billId);
            int affected = pstmt.executeUpdate();
            return affected >= 0; // Returns true even if no rows were deleted
        } catch (SQLException e) {
            System.err.println("Error deleting bill items: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Insert a bill item
     * @param billId The bill ID
     * @param item The bill item to insert
     * @return true if successful, false otherwise
     */
    public boolean insertBillItem(int billId, BillItem item) {
        String sql = "INSERT INTO billing_items (bill_id, treatment_id, description, quantity, unit_price, total_price) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, billId);
            if (item.getTreatmentId() > 0) {
                pstmt.setInt(2, item.getTreatmentId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }
            pstmt.setString(3, item.getDescription());
            pstmt.setInt(4, item.getQuantity());
            pstmt.setDouble(5, item.getUnitPrice());
            pstmt.setDouble(6, item.getTotalPrice());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error inserting bill item: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Update bill items (delete all and insert new ones) - For Dentist/Admin/Reception
     * This method handles both deleting old items and inserting new ones in a transaction
     * @param billId The bill ID
     * @param items The list of bill items
     * @return true if successful, false otherwise
     */
    public boolean updateBillItems(int billId, List<BillItem> items) {
        Connection conn = null;
        try {
            conn = DBconnection.getConnection();
            conn.setAutoCommit(false);
            
            // Delete existing items
            String deleteSql = "DELETE FROM billing_items WHERE bill_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
                pstmt.setInt(1, billId);
                pstmt.executeUpdate();
            }
            
            // Insert new items
            if (items != null && !items.isEmpty()) {
                String insertSql = "INSERT INTO billing_items (bill_id, treatment_id, description, quantity, unit_price, total_price) "
                                 + "VALUES (?, ?, ?, ?, ?, ?)";
                for (BillItem item : items) {
                    try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                        pstmt.setInt(1, billId);
                        if (item.getTreatmentId() > 0) {
                            pstmt.setInt(2, item.getTreatmentId());
                        } else {
                            pstmt.setNull(2, Types.INTEGER);
                        }
                        pstmt.setString(3, item.getDescription());
                        pstmt.setInt(4, item.getQuantity());
                        pstmt.setDouble(5, item.getUnitPrice());
                        pstmt.setDouble(6, item.getTotalPrice());
                        pstmt.executeUpdate();
                    }
                }
            }
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("Error updating bill items: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // =====================================================
    // DELETE METHODS
    // =====================================================

    /**
     * Delete a bill (hard delete) - ADMIN only
     * @param billId The bill ID to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteBill(int billId) {
        Connection conn = null;
        
        try {
            conn = DBconnection.getConnection();
            conn.setAutoCommit(false);
            
            // Delete items first
            String itemSql = "DELETE FROM billing_items WHERE bill_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(itemSql)) {
                pstmt.setInt(1, billId);
                pstmt.executeUpdate();
            }
            
            // Delete bill
            String billSql = "DELETE FROM billing WHERE bill_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(billSql)) {
                pstmt.setInt(1, billId);
                int affected = pstmt.executeUpdate();
                conn.commit();
                return affected > 0;
            }
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("Error deleting bill: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // =====================================================
    // COUNT METHODS
    // =====================================================

    /**
     * Get total bill count
     * @return Total number of bills
     */
    public int getBillCount() {
        String sql = "SELECT COUNT(*) FROM billing";
        
        try (Connection conn = DBconnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting bills: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Get total revenue from all bills
     * @return Total revenue
     */
    public double getTotalRevenue() {
        String sql = "SELECT SUM(total_amount) FROM billing WHERE status IN ('Paid', 'Partial')";
        
        try (Connection conn = DBconnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting total revenue: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Get total revenue from paid bills
     * @return Total paid revenue
     */
    public double getPaidRevenue() {
        String sql = "SELECT SUM(total_amount) FROM billing WHERE status = 'Paid'";
        
        try (Connection conn = DBconnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting paid revenue: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Get total pending revenue
     * @return Total pending revenue
     */
    public double getPendingRevenue() {
        String sql = "SELECT SUM(total_amount) FROM billing WHERE status = 'Pending'";
        
        try (Connection conn = DBconnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting pending revenue: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Get total overdue revenue
     * @return Total overdue revenue
     */
    public double getOverdueRevenue() {
        String sql = "SELECT SUM(total_amount) FROM billing WHERE status = 'Overdue'";
        
        try (Connection conn = DBconnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting overdue revenue: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Get pending bills count
     * @return Number of pending bills
     */
    public int getPendingBillCount() {
        String sql = "SELECT COUNT(*) FROM billing WHERE status IN ('Pending', 'Partial', 'Overdue')";
        
        try (Connection conn = DBconnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting pending bills: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Get count of bills by status
     * @param status The status to count
     * @return Number of bills with the specified status
     */
    public int getBillCountByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM billing WHERE status = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting bills by status: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Get count of bills by date
     * @param date The date
     * @return Number of bills on the specified date
     */
    public int getBillCountByDate(String date) {
        String sql = "SELECT COUNT(*) FROM billing WHERE bill_date = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, date);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting bills by date: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Get count of bills by payment method
     * @param paymentMethod The payment method to count
     * @return Number of bills with the specified payment method
     */
    public int getBillCountByPaymentMethod(String paymentMethod) {
        String sql = "SELECT COUNT(*) FROM billing WHERE payment_method = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, paymentMethod);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting bills by payment method: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    // =====================================================
    // HELPER METHODS
    // =====================================================

    /**
     * Generate a unique bill number
     * @return A unique bill number
     */
    public String generateBillNumber() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int count = 0;
        String sql = "SELECT COUNT(*) FROM billing WHERE bill_number LIKE ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "BILL-" + dateStr + "-%");
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1) + 1;
            }
        } catch (SQLException e) {
            System.err.println("Error generating bill number: " + e.getMessage());
            e.printStackTrace();
        }
        
        return "BILL-" + dateStr + "-" + String.format("%04d", count);
    }

    /**
     * Check if bill number exists
     * @param billNumber The bill number to check
     * @return true if exists, false otherwise
     */
    public boolean billNumberExists(String billNumber) {
        String sql = "SELECT COUNT(*) FROM billing WHERE bill_number = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, billNumber);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking bill number existence: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // =====================================================
    // MAPPER METHODS
    // =====================================================

    /**
     * Map ResultSet to Bill object
     * @param rs The ResultSet
     * @return Bill object
     * @throws SQLException if there's an error accessing the ResultSet
     */
    private Bill mapResultSetToBill(ResultSet rs) throws SQLException {
        return new Bill(
            rs.getInt("bill_id"),
            rs.getInt("patient_id"),
            rs.getInt("appointment_id"),
            rs.getString("bill_number"),
            rs.getDate("bill_date"),
            rs.getDate("due_date"),
            rs.getDouble("subtotal"),
            rs.getDouble("tax"),
            rs.getDouble("discount"),
            rs.getDouble("total_amount"),
            rs.getDouble("amount_paid"),
            rs.getDouble("balance"),
            rs.getString("status"),
            rs.getString("payment_method"),
            rs.getString("notes"),
            rs.getString("created_at"),
            rs.getString("updated_at")
        );
    }

    /**
     * Map ResultSet to BillItem object
     * @param rs The ResultSet
     * @return BillItem object
     * @throws SQLException if there's an error accessing the ResultSet
     */
    private BillItem mapResultSetToBillItem(ResultSet rs) throws SQLException {
        return new BillItem(
            rs.getInt("billing_item_id"),
            rs.getInt("bill_id"),
            rs.getInt("treatment_id"),
            rs.getString("description"),
            rs.getInt("quantity"),
            rs.getDouble("unit_price"),
            rs.getDouble("total_price"),
            rs.getString("created_at")
        );
    }
}