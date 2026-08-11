package model;

import java.sql.Timestamp;

public class AuditLog {
    private int auditId;
    private int userId;
    private String username;
    private String action;
    private String description;
    private String ipAddress;
    private String userAgent;
    private Timestamp createdAt;

    public AuditLog() {}

    public AuditLog(int auditId, int userId, String username, String action,
                    String description, String ipAddress, String userAgent,
                    Timestamp createdAt) {
        this.auditId = auditId;
        this.userId = userId;
        this.username = username;
        this.action = action;
        this.description = description;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getAuditId() { return auditId; }
    public void setAuditId(int auditId) { this.auditId = auditId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}