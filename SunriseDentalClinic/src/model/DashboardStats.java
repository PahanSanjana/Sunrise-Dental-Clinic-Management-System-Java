package model;

public class DashboardStats {
    // Common fields for all dashboards
    private int totalPatients;
    private int totalAppointments;
    private int todayAppointments;
    private double totalRevenue;
    
    // Admin-specific fields
    private int totalUsers;
    private int totalStaff;
    private int totalDentists;
    private int totalTreatments;
    
    // Patient-specific fields
    private int totalBills;
    private boolean isActive;
    
    // Dentist-specific fields (reuse totalTreatments)
    
    // Default constructor
    public DashboardStats() {
        this.totalPatients = 0;
        this.totalAppointments = 0;
        this.todayAppointments = 0;
        this.totalRevenue = 0.0;
        this.totalUsers = 0;
        this.totalStaff = 0;
        this.totalDentists = 0;
        this.totalTreatments = 0;
        this.totalBills = 0;
        this.isActive = true;
    }

    // =====================================================
    // GETTERS AND SETTERS
    // =====================================================

    // Total Patients
    public int getTotalPatients() {
        return totalPatients;
    }

    public void setTotalPatients(int totalPatients) {
        this.totalPatients = totalPatients;
    }

    // Total Appointments
    public int getTotalAppointments() {
        return totalAppointments;
    }

    public void setTotalAppointments(int totalAppointments) {
        this.totalAppointments = totalAppointments;
    }

    // Today's Appointments
    public int getTodayAppointments() {
        return todayAppointments;
    }

    public void setTodayAppointments(int todayAppointments) {
        this.todayAppointments = todayAppointments;
    }

    // Total Revenue
    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    // Total Users (Admin only)
    public int getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(int totalUsers) {
        this.totalUsers = totalUsers;
    }

    // Total Staff (Admin only)
    public int getTotalStaff() {
        return totalStaff;
    }

    public void setTotalStaff(int totalStaff) {
        this.totalStaff = totalStaff;
    }

    // Total Dentists (Admin only)
    public int getTotalDentists() {
        return totalDentists;
    }

    public void setTotalDentists(int totalDentists) {
        this.totalDentists = totalDentists;
    }

    // Total Treatments (Admin, Dentist, Patient)
    public int getTotalTreatments() {
        return totalTreatments;
    }

    public void setTotalTreatments(int totalTreatments) {
        this.totalTreatments = totalTreatments;
    }

    // Total Bills (Patient only)
    public int getTotalBills() {
        return totalBills;
    }

    public void setTotalBills(int totalBills) {
        this.totalBills = totalBills;
    }

    // Account Status (Patient only)
    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    // =====================================================
    // HELPER METHODS
    // =====================================================

    /**
     * Check if the stats are for admin dashboard
     * @return true if admin stats are present
     */
    public boolean isAdminStats() {
        return totalUsers > 0 || totalStaff > 0 || totalDentists > 0;
    }

    /**
     * Check if the stats are for patient dashboard
     * @return true if patient stats are present
     */
    public boolean isPatientStats() {
        return totalBills > 0 || isActive;
    }

    /**
     * Get formatted revenue as string
     * @return Formatted revenue string
     */
    public String getFormattedRevenue() {
        return String.format("$%.2f", totalRevenue);
    }

    /**
     * Get patient status as string
     * @return "Active" or "Inactive"
     */
    public String getStatusText() {
        return isActive ? "Active" : "Inactive";
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("DashboardStats{");
        sb.append("totalPatients=").append(totalPatients);
        sb.append(", totalAppointments=").append(totalAppointments);
        sb.append(", todayAppointments=").append(todayAppointments);
        sb.append(", totalRevenue=").append(totalRevenue);
        sb.append(", totalUsers=").append(totalUsers);
        sb.append(", totalStaff=").append(totalStaff);
        sb.append(", totalDentists=").append(totalDentists);
        sb.append(", totalTreatments=").append(totalTreatments);
        sb.append(", totalBills=").append(totalBills);
        sb.append(", isActive=").append(isActive);
        sb.append('}');
        return sb.toString();
    }
}