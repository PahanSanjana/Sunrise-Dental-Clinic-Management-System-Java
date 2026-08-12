package model;

public class DashboardStats {
    private int totalUsers;
    private int totalPatients;
    private int totalAppointments;
    private double totalRevenue;
    private int totalStaff;
    private int totalDentists;
    private int totalTreatments;
    private int todayAppointments;

    public DashboardStats() {}

    public DashboardStats(int totalUsers, int totalPatients, int totalAppointments,
                          double totalRevenue, int totalStaff, int totalDentists,
                          int totalTreatments, int todayAppointments) {
        this.totalUsers = totalUsers;
        this.totalPatients = totalPatients;
        this.totalAppointments = totalAppointments;
        this.totalRevenue = totalRevenue;
        this.totalStaff = totalStaff;
        this.totalDentists = totalDentists;
        this.totalTreatments = totalTreatments;
        this.todayAppointments = todayAppointments;
    }

    // Getters and Setters
    public int getTotalUsers() { return totalUsers; }
    public void setTotalUsers(int totalUsers) { this.totalUsers = totalUsers; }

    public int getTotalPatients() { return totalPatients; }
    public void setTotalPatients(int totalPatients) { this.totalPatients = totalPatients; }

    public int getTotalAppointments() { return totalAppointments; }
    public void setTotalAppointments(int totalAppointments) { this.totalAppointments = totalAppointments; }

    public double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }

    public int getTotalStaff() { return totalStaff; }
    public void setTotalStaff(int totalStaff) { this.totalStaff = totalStaff; }

    public int getTotalDentists() { return totalDentists; }
    public void setTotalDentists(int totalDentists) { this.totalDentists = totalDentists; }

    public int getTotalTreatments() { return totalTreatments; }
    public void setTotalTreatments(int totalTreatments) { this.totalTreatments = totalTreatments; }

    public int getTodayAppointments() { return todayAppointments; }
    public void setTodayAppointments(int todayAppointments) { this.todayAppointments = todayAppointments; }
}