package model;

import java.sql.Date;
import java.sql.Time;

public class Appointment {
    private int appointmentId;
    private int patientId;
    private int dentistId;
    private int treatmentId;
    private Date appointmentDate;
    private Time appointmentTime;
    private Time endTime;
    private String status;
    private String reason;
    private String notes;
    private String createdAt;
    private String updatedAt;
    
    // Additional fields for display
    private String patientName;
    private String dentistName;
    private String treatmentName;
    private double treatmentCost;
    private int treatmentDuration;

    public Appointment() {}

    // Constructor for new appointment
    public Appointment(int patientId, int dentistId, int treatmentId, 
                       Date appointmentDate, Time appointmentTime, 
                       String reason, String notes) {
        this.patientId = patientId;
        this.dentistId = dentistId;
        this.treatmentId = treatmentId;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.reason = reason;
        this.notes = notes;
        this.status = "Scheduled";
    }

    // Getters and Setters
    public int getAppointmentId() { return appointmentId; }
    public void setAppointmentId(int appointmentId) { this.appointmentId = appointmentId; }

    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }

    public int getDentistId() { return dentistId; }
    public void setDentistId(int dentistId) { this.dentistId = dentistId; }

    public int getTreatmentId() { return treatmentId; }
    public void setTreatmentId(int treatmentId) { this.treatmentId = treatmentId; }

    public Date getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(Date appointmentDate) { this.appointmentDate = appointmentDate; }

    public Time getAppointmentTime() { return appointmentTime; }
    public void setAppointmentTime(Time appointmentTime) { this.appointmentTime = appointmentTime; }

    public Time getEndTime() { return endTime; }
    public void setEndTime(Time endTime) { this.endTime = endTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getDentistName() { return dentistName; }
    public void setDentistName(String dentistName) { this.dentistName = dentistName; }

    public String getTreatmentName() { return treatmentName; }
    public void setTreatmentName(String treatmentName) { this.treatmentName = treatmentName; }

    public double getTreatmentCost() { return treatmentCost; }
    public void setTreatmentCost(double treatmentCost) { this.treatmentCost = treatmentCost; }

    public int getTreatmentDuration() { return treatmentDuration; }
    public void setTreatmentDuration(int treatmentDuration) { this.treatmentDuration = treatmentDuration; }

    @Override
    public String toString() {
        return "Appointment #" + appointmentId + " - " + patientName + " with " + dentistName;
    }
}