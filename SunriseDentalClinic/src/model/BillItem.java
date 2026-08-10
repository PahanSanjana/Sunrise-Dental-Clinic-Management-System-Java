package model;

public class BillItem {
    private int billingItemId;
    private int billId;
    private int treatmentId;
    private String description;
    private int quantity;
    private double unitPrice;
    private double totalPrice;
    private String createdAt;

    public BillItem() {}

    public BillItem(int billingItemId, int billId, int treatmentId,
                    String description, int quantity, double unitPrice,
                    double totalPrice, String createdAt) {
        this.billingItemId = billingItemId;
        this.billId = billId;
        this.treatmentId = treatmentId;
        this.description = description;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
        this.createdAt = createdAt;
    }

    // Constructor for new bill item (without ID)
    public BillItem(int billId, int treatmentId, String description,
                    int quantity, double unitPrice, double totalPrice) {
        this.billId = billId;
        this.treatmentId = treatmentId;
        this.description = description;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
    }

    // Getters and Setters
    public int getBillingItemId() { return billingItemId; }
    public void setBillingItemId(int billingItemId) { this.billingItemId = billingItemId; }

    public int getBillId() { return billId; }
    public void setBillId(int billId) { this.billId = billId; }

    public int getTreatmentId() { return treatmentId; }
    public void setTreatmentId(int treatmentId) { this.treatmentId = treatmentId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}