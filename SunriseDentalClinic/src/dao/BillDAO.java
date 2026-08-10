package dao;

import db.DBconnection;
import model.Bill;
import model.BillItem;
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
            
            // Insert bill
            String billSql = "INSERT INTO billing (patient_id, appointment_id, bill_number, bill_date, "
                           + "due_date, subtotal, tax, discount, total_amount, amount_paid, balance, "
                           + "status, payment_method, notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            pstmt = conn.prepareStatement(billSql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, bill.getPatientId());
            pstmt.setInt(2, bill.getAppointmentId());
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
            
            pstmt.executeUpdate();
            
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
                pstmt.setInt(2, item.getTreatmentId());
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
     * Get all bills
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
            pstmt.setInt(2, bill.getAppointmentId());
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
    // DELETE METHODS
    // =====================================================

    /**
     * Delete a bill
     * @param billId The bill ID to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteBill(int billId) {
        // First delete bill items
        String itemSql = "DELETE FROM billing_items WHERE bill_id = ?";
        String billSql = "DELETE FROM billing WHERE bill_id = ?";
        
        try (Connection conn = DBconnection.getConnection()) {
            conn.setAutoCommit(false);
            
            // Delete items
            try (PreparedStatement pstmt = conn.prepareStatement(itemSql)) {
                pstmt.setInt(1, billId);
                pstmt.executeUpdate();
            }
            
            // Delete bill
            try (PreparedStatement pstmt = conn.prepareStatement(billSql)) {
                pstmt.setInt(1, billId);
                int affected = pstmt.executeUpdate();
                conn.commit();
                return affected > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error deleting bill: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
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

    // =====================================================
    // HELPER METHODS
    // =====================================================

    /**
     * Generate a unique bill number
     * @return A unique bill number
     */
    public String generateBillNumber() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        // Get the count of bills for today to create sequential number
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