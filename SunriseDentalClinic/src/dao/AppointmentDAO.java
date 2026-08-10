package dao;

import db.DBconnection;
import model.Appointment;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {

    // =====================================================
    // CREATE METHODS
    // =====================================================

    /**
     * Book a new appointment
     * @param appointment The appointment to book
     * @return true if successful, false otherwise
     */
    public boolean bookAppointment(Appointment appointment) {
        String sql = "INSERT INTO appointments (patient_id, dentist_id, appointment_date, appointment_time, "
                   + "end_time, status, reason, notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, appointment.getPatientId());
            pstmt.setInt(2, appointment.getDentistId());
            pstmt.setDate(3, appointment.getAppointmentDate());
            pstmt.setTime(4, appointment.getAppointmentTime());
            pstmt.setTime(5, appointment.getEndTime());
            pstmt.setString(6, appointment.getStatus());
            pstmt.setString(7, appointment.getReason());
            pstmt.setString(8, appointment.getNotes());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    appointment.setAppointmentId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error booking appointment: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // =====================================================
    // READ METHODS
    // =====================================================

    /**
     * Get all appointments
     * @return List of all appointments
     */
    public List<Appointment> getAllAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments ORDER BY appointment_date DESC, appointment_time DESC";
        
        try (Connection conn = DBconnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                appointments.add(mapResultSetToAppointment(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all appointments: " + e.getMessage());
            e.printStackTrace();
        }
        return appointments;
    }

    /**
     * Get appointment by ID
     * @param appointmentId The appointment ID
     * @return Appointment object if found, null otherwise
     */
    public Appointment getAppointmentById(int appointmentId) {
        String sql = "SELECT * FROM appointments WHERE appointment_id = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, appointmentId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToAppointment(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting appointment by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get appointments by patient ID
     * @param patientId The patient ID
     * @return List of appointments for the patient
     */
    public List<Appointment> getAppointmentsByPatient(int patientId) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE patient_id = ? ORDER BY appointment_date DESC, appointment_time DESC";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                appointments.add(mapResultSetToAppointment(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting appointments by patient: " + e.getMessage());
            e.printStackTrace();
        }
        return appointments;
    }

    /**
     * Get appointments by dentist ID
     * @param dentistId The dentist ID
     * @return List of appointments for the dentist
     */
    public List<Appointment> getAppointmentsByDentist(int dentistId) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE dentist_id = ? ORDER BY appointment_date DESC, appointment_time DESC";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, dentistId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                appointments.add(mapResultSetToAppointment(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting appointments by dentist: " + e.getMessage());
            e.printStackTrace();
        }
        return appointments;
    }

    /**
     * Get appointments by date
     * @param date The date
     * @return List of appointments on the specified date
     */
    public List<Appointment> getAppointmentsByDate(String date) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE appointment_date = ? ORDER BY appointment_time";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, date);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                appointments.add(mapResultSetToAppointment(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting appointments by date: " + e.getMessage());
            e.printStackTrace();
        }
        return appointments;
    }

    /**
     * Get appointments by date range
     * @param startDate The start date
     * @param endDate The end date
     * @return List of appointments in the date range
     */
    public List<Appointment> getAppointmentsByDateRange(String startDate, String endDate) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE appointment_date BETWEEN ? AND ? ORDER BY appointment_date, appointment_time";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, startDate);
            pstmt.setString(2, endDate);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                appointments.add(mapResultSetToAppointment(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting appointments by date range: " + e.getMessage());
            e.printStackTrace();
        }
        return appointments;
    }

    /**
     * Get appointments by status
     * @param status The status to filter by
     * @return List of appointments with the specified status
     */
    public List<Appointment> getAppointmentsByStatus(String status) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE status = ? ORDER BY appointment_date DESC, appointment_time DESC";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                appointments.add(mapResultSetToAppointment(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting appointments by status: " + e.getMessage());
            e.printStackTrace();
        }
        return appointments;
    }

    /**
     * Get today's appointments
     * @return List of today's appointments
     */
    public List<Appointment> getTodayAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE appointment_date = CURDATE() ORDER BY appointment_time";
        
        try (Connection conn = DBconnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                appointments.add(mapResultSetToAppointment(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting today's appointments: " + e.getMessage());
            e.printStackTrace();
        }
        return appointments;
    }

    /**
     * Get upcoming appointments (from today onwards)
     * @return List of upcoming appointments
     */
    public List<Appointment> getUpcomingAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE appointment_date >= CURDATE() AND status NOT IN ('Cancelled', 'Completed', 'No Show') ORDER BY appointment_date, appointment_time";
        
        try (Connection conn = DBconnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                appointments.add(mapResultSetToAppointment(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting upcoming appointments: " + e.getMessage());
            e.printStackTrace();
        }
        return appointments;
    }

    /**
     * Check if a dentist is available at a specific date and time
     * @param dentistId The dentist ID
     * @param date The date
     * @param time The time
     * @return true if available, false otherwise
     */
    public boolean checkAvailability(int dentistId, String date, String time) {
        String sql = "SELECT COUNT(*) FROM appointments WHERE dentist_id = ? AND appointment_date = ? "
                   + "AND appointment_time = ? AND status NOT IN ('Cancelled', 'No Show')";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, dentistId);
            pstmt.setString(2, date);
            pstmt.setString(3, time);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking availability: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // =====================================================
    // UPDATE METHODS
    // =====================================================

    /**
     * Update an appointment
     * @param appointment The appointment to update
     * @return true if successful, false otherwise
     */
    public boolean updateAppointment(Appointment appointment) {
        String sql = "UPDATE appointments SET patient_id=?, dentist_id=?, appointment_date=?, appointment_time=?, "
                   + "end_time=?, status=?, reason=?, notes=? WHERE appointment_id=?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, appointment.getPatientId());
            pstmt.setInt(2, appointment.getDentistId());
            pstmt.setDate(3, appointment.getAppointmentDate());
            pstmt.setTime(4, appointment.getAppointmentTime());
            pstmt.setTime(5, appointment.getEndTime());
            pstmt.setString(6, appointment.getStatus());
            pstmt.setString(7, appointment.getReason());
            pstmt.setString(8, appointment.getNotes());
            pstmt.setInt(9, appointment.getAppointmentId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating appointment: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Cancel an appointment (soft delete)
     * @param appointmentId The appointment ID to cancel
     * @return true if successful, false otherwise
     */
    public boolean cancelAppointment(int appointmentId) {
        String sql = "UPDATE appointments SET status = 'Cancelled' WHERE appointment_id = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, appointmentId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error cancelling appointment: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // =====================================================
    // DELETE METHODS
    // =====================================================

    /**
     * Delete an appointment (hard delete)
     * @param appointmentId The appointment ID to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteAppointment(int appointmentId) {
        String sql = "DELETE FROM appointments WHERE appointment_id = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, appointmentId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting appointment: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // =====================================================
    // COUNT METHODS
    // =====================================================

    /**
     * Get total appointment count
     * @return Total number of appointments
     */
    public int getAppointmentCount() {
        String sql = "SELECT COUNT(*) FROM appointments";
        
        try (Connection conn = DBconnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting appointments: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Get count of appointments by status
     * @param status The status to count
     * @return Number of appointments with the specified status
     */
    public int getAppointmentCountByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM appointments WHERE status = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting appointments by status: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Get count of appointments by date
     * @param date The date
     * @return Number of appointments on the specified date
     */
    public int getAppointmentCountByDate(String date) {
        String sql = "SELECT COUNT(*) FROM appointments WHERE appointment_date = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, date);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting appointments by date: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    // =====================================================
    // HELPER METHODS
    // =====================================================

    /**
     * Map ResultSet to Appointment object
     * @param rs The ResultSet
     * @return Appointment object
     * @throws SQLException if there's an error accessing the ResultSet
     */
    private Appointment mapResultSetToAppointment(ResultSet rs) throws SQLException {
        Appointment appointment = new Appointment();
        appointment.setAppointmentId(rs.getInt("appointment_id"));
        appointment.setPatientId(rs.getInt("patient_id"));
        appointment.setDentistId(rs.getInt("dentist_id"));
        appointment.setAppointmentDate(rs.getDate("appointment_date"));
        appointment.setAppointmentTime(rs.getTime("appointment_time"));
        
        // Handle end_time (may be null)
        try {
            appointment.setEndTime(rs.getTime("end_time"));
        } catch (SQLException e) {
            appointment.setEndTime(null);
        }
        
        appointment.setStatus(rs.getString("status"));
        appointment.setReason(rs.getString("reason"));
        appointment.setNotes(rs.getString("notes"));
        appointment.setCreatedAt(rs.getString("created_at"));
        appointment.setUpdatedAt(rs.getString("updated_at"));
        return appointment;
    }
}