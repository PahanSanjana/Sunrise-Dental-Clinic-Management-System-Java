package model;

import java.sql.Date;

public class Staff {
    private int staffId;
    private int userId;
    private String firstName;
    private String lastName;
    private String position;
    private String department;
    private String phone;
    private String email;
    private Date hireDate;
    private double salary;
    private boolean isActive;
    private String createdAt;
    private String updatedAt;

    public Staff() {}

    public Staff(int staffId, int userId, String firstName, String lastName, 
                 String position, String department, String phone, String email,
                 Date hireDate, double salary, boolean isActive, 
                 String createdAt, String updatedAt) {
        this.staffId = staffId;
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
        this.department = department;
        this.phone = phone;
        this.email = email;
        this.hireDate = hireDate;
        this.salary = salary;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Constructor for new staff (without ID)
    public Staff(String firstName, String lastName, String position, 
                 String department, String phone, String email,
                 Date hireDate, double salary, boolean isActive) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
        this.department = department;
        this.phone = phone;
        this.email = email;
        this.hireDate = hireDate;
        this.salary = salary;
        this.isActive = isActive;
        this.userId = -1; // -1 means NULL
    }

    // Getters and Setters
    public int getStaffId() { return staffId; }
    public void setStaffId(int staffId) { this.staffId = staffId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Date getHireDate() { return hireDate; }
    public void setHireDate(Date hireDate) { this.hireDate = hireDate; }

    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public String toString() {
        return getFullName() + " (" + position + ")";
    }
}