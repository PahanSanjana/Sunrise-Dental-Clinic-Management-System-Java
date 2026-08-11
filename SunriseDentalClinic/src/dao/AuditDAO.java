package dao;

import db.DBconnection;
import model.AuditLog;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuditDAO {

    /**
     * Add a new audit log entry
     */
    public boolean addLog(AuditLog log) {
        String sql = "INSERT INTO audit_log (user_id, username, action, description, ip_address, user_agent) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, log.getUserId());
            pstmt.setString(2, log.getUsername());
            pstmt.setString(3, log.getAction());
            pstmt.setString(4, log.getDescription());
            pstmt.setString(5, log.getIpAddress());
            pstmt.setString(6, log.getUserAgent());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding audit log: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get all audit logs
     */
    public List<AuditLog> getAllLogs() {
        List<AuditLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM audit_log ORDER BY created_at DESC";
        
        try (Connection conn = DBconnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                logs.add(mapResultSetToLog(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting audit logs: " + e.getMessage());
            e.printStackTrace();
        }
        return logs;
    }

    /**
     * Get audit logs by user ID
     */
    public List<AuditLog> getLogsByUser(int userId) {
        List<AuditLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM audit_log WHERE user_id = ? ORDER BY created_at DESC";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                logs.add(mapResultSetToLog(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting audit logs by user: " + e.getMessage());
            e.printStackTrace();
        }
        return logs;
    }

    /**
     * Get audit logs by action
     */
    public List<AuditLog> getLogsByAction(String action) {
        List<AuditLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM audit_log WHERE action = ? ORDER BY created_at DESC";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, action);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                logs.add(mapResultSetToLog(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting audit logs by action: " + e.getMessage());
            e.printStackTrace();
        }
        return logs;
    }

    /**
     * Get audit logs by date range
     */
    public List<AuditLog> getLogsByDateRange(String startDate, String endDate) {
        List<AuditLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM audit_log WHERE DATE(created_at) BETWEEN ? AND ? ORDER BY created_at DESC";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, startDate);
            pstmt.setString(2, endDate);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                logs.add(mapResultSetToLog(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting audit logs by date range: " + e.getMessage());
            e.printStackTrace();
        }
        return logs;
    }

    /**
     * Get all unique usernames from audit logs
     */
    public List<String> getAllUsernames() {
        List<String> usernames = new ArrayList<>();
        String sql = "SELECT DISTINCT username FROM audit_log WHERE username IS NOT NULL ORDER BY username";
        
        try (Connection conn = DBconnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                usernames.add(rs.getString("username"));
            }
        } catch (SQLException e) {
            System.err.println("Error getting usernames: " + e.getMessage());
            e.printStackTrace();
        }
        return usernames;
    }

    /**
     * Get log count
     */
    public int getLogCount() {
        String sql = "SELECT COUNT(*) FROM audit_log";
        
        try (Connection conn = DBconnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting audit logs: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Clear old logs
     */
    public boolean clearOldLogs(int days) {
        String sql = "DELETE FROM audit_log WHERE created_at < DATE_SUB(NOW(), INTERVAL ? DAY)";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, days);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error clearing old logs: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    private AuditLog mapResultSetToLog(ResultSet rs) throws SQLException {
        return new AuditLog(
            rs.getInt("audit_id"),
            rs.getInt("user_id"),
            rs.getString("username"),
            rs.getString("action"),
            rs.getString("description"),
            rs.getString("ip_address"),
            rs.getString("user_agent"),
            rs.getTimestamp("created_at")
        );
    }
}