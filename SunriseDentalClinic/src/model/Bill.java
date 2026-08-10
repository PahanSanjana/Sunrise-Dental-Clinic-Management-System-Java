package model;

import java.sql.Date;

public class Bill {
    private int billId;
    private int patientId;
    private int appointmentId;
    private String billNumber;
    private Date billDate;
    private Date dueDate;
    private double subtotal;
    private double tax;
    private double discount;
    private double totalAmount;
    private double amountPaid;
    private double balance;
    private String status;
    private String paymentMethod;
    private String notes;
    private String createdAt;
    private String updatedAt;

    public Bill() {}

    public Bill(int billId, int patientId, int appointmentId, String billNumber,
                Date billDate, Date dueDate, double subtotal, double tax,
                double discount, double totalAmount, double amountPaid,
                double balance, String status, String paymentMethod,
                String notes, String createdAt, String updatedAt) {
        this.billId = billId;
        this.patientId = patientId;
        this.appointmentId = appointmentId;
        this.billNumber = billNumber;
        this.billDate = billDate;
        this.dueDate = dueDate;
        this.subtotal = subtotal;
        this.tax = tax;
        this.discount = discount;
        this.totalAmount = totalAmount;
        this.amountPaid = amountPaid;
        this.balance = balance;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.notes = notes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Constructor for new bill (without ID)
    public Bill(int patientId, int appointmentId, String billNumber,
                Date billDate, Date dueDate, double subtotal, double tax,
                double discount, double totalAmount, double amountPaid,
                double balance, String status, String paymentMethod,
                String notes) {
        this.patientId = patientId;
        this.appointmentId = appointmentId;
        this.billNumber = billNumber;
        this.billDate = billDate;
        this.dueDate = dueDate;
        this.subtotal = subtotal;
        this.tax = tax;
        this.discount = discount;
        this.totalAmount = totalAmount;
        this.amountPaid = amountPaid;
        this.balance = balance;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.notes = notes;
    }

    // Getters and Setters
    public int getBillId() { return billId; }
    public void setBillId(int billId) { this.billId = billId; }

    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }

    public int getAppointmentId() { return appointmentId; }
    public void setAppointmentId(int appointmentId) { this.appointmentId = appointmentId; }

    public String getBillNumber() { return billNumber; }
    public void setBillNumber(String billNumber) { this.billNumber = billNumber; }

    public Date getBillDate() { return billDate; }
    public void setBillDate(Date billDate) { this.billDate = billDate; }

    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public double getTax() { return tax; }
    public void setTax(double tax) { this.tax = tax; }

    public double getDiscount() { return discount; }
    public void setDiscount(double discount) { this.discount = discount; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public double getAmountPaid() { return amountPaid; }
    public void setAmountPaid(double amountPaid) { this.amountPaid = amountPaid; }

    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}