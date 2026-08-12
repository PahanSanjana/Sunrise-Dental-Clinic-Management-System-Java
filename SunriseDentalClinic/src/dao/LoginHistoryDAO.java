package dao;

import db.DBconnection;
import model.LoginHistory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoginHistoryDAO {

    /**
     * Add a new login record
     */
    public boolean addLogin(LoginHistory login) {
        String sql = "INSERT INTO login_history (user_id, username, login_time, ip_address, user_agent, status) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, login.getUserId());
            pstmt.setString(2, login.getUsername());
            pstmt.setTimestamp(3, login.getLoginTime());
            pstmt.setString(4, login.getIpAddress());
            pstmt.setString(5, login.getUserAgent());
            pstmt.setString(6, login.getStatus());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    login.setLoginId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error adding login history: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Update logout time
     */
    public boolean updateLogoutTime(int loginId, Timestamp logoutTime) {
        String sql = "UPDATE login_history SET logout_time = ? WHERE login_id = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setTimestamp(1, logoutTime);
            pstmt.setInt(2, loginId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating logout time: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get all login history
     */
    public List<LoginHistory> getAllLoginHistory() {
        List<LoginHistory> history = new ArrayList<>();
        String sql = "SELECT * FROM login_history ORDER BY login_time DESC";
        
        try (Connection conn = DBconnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                history.add(mapResultSetToLoginHistory(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting login history: " + e.getMessage());
            e.printStackTrace();
        }
        return history;
    }

    /**
     * Get login history by user ID
     */
    public List<LoginHistory> getLoginHistoryByUser(int userId) {
        List<LoginHistory> history = new ArrayList<>();
        String sql = "SELECT * FROM login_history WHERE user_id = ? ORDER BY login_time DESC";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                history.add(mapResultSetToLoginHistory(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting login history by user: " + e.getMessage());
            e.printStackTrace();
        }
        return history;
    }

    /**
     * Get login history by status
     */
    public List<LoginHistory> getLoginHistoryByStatus(String status) {
        List<LoginHistory> history = new ArrayList<>();
        String sql = "SELECT * FROM login_history WHERE status = ? ORDER BY login_time DESC";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                history.add(mapResultSetToLoginHistory(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting login history by status: " + e.getMessage());
            e.printStackTrace();
        }
        return history;
    }

    /**
     * Get login history by date range
     */
    public List<LoginHistory> getLoginHistoryByDateRange(String startDate, String endDate) {
        List<LoginHistory> history = new ArrayList<>();
        String sql = "SELECT * FROM login_history WHERE DATE(login_time) BETWEEN ? AND ? ORDER BY login_time DESC";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, startDate);
            pstmt.setString(2, endDate);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                history.add(mapResultSetToLoginHistory(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting login history by date range: " + e.getMessage());
            e.printStackTrace();
        }
        return history;
    }

    /**
     * Get login count by status
     */
    public int getLoginCountByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM login_history WHERE status = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting login history by status: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Get total login count
     */
    public int getLoginCount() {
        String sql = "SELECT COUNT(*) FROM login_history";
        
        try (Connection conn = DBconnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting login history: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Clear old login history
     */
    public boolean clearOldLoginHistory(int days) {
        String sql = "DELETE FROM login_history WHERE login_time < DATE_SUB(NOW(), INTERVAL ? DAY)";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, days);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error clearing old login history: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    private LoginHistory mapResultSetToLoginHistory(ResultSet rs) throws SQLException {
        return new LoginHistory(
            rs.getInt("login_id"),
            rs.getInt("user_id"),
            rs.getString("username"),
            rs.getTimestamp("login_time"),
            rs.getTimestamp("logout_time"),
            rs.getString("ip_address"),
            rs.getString("user_agent"),
            rs.getString("status")
        );
    }
}