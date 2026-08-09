package model;

public class Treatment {
    private int treatmentId;
    private String treatmentName;
    private String description;
    private String category;
    private double cost;
    private int duration; // in minutes
    private boolean isActive;
    private String createdAt;
    private String updatedAt;

    public Treatment() {}

    public Treatment(int treatmentId, String treatmentName, String description,
                     String category, double cost, int duration, boolean isActive,
                     String createdAt, String updatedAt) {
        this.treatmentId = treatmentId;
        this.treatmentName = treatmentName;
        this.description = description;
        this.category = category;
        this.cost = cost;
        this.duration = duration;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Constructor for new treatment (without ID)
    public Treatment(String treatmentName, String description, String category,
                     double cost, int duration, boolean isActive) {
        this.treatmentName = treatmentName;
        this.description = description;
        this.category = category;
        this.cost = cost;
        this.duration = duration;
        this.isActive = isActive;
    }

    // Getters and Setters
    public int getTreatmentId() { return treatmentId; }
    public void setTreatmentId(int treatmentId) { this.treatmentId = treatmentId; }

    public String getTreatmentName() { return treatmentName; }
    public void setTreatmentName(String treatmentName) { this.treatmentName = treatmentName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = cost; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return treatmentName + " ($" + cost + ")";
    }
}