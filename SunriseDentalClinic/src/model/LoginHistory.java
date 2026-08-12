package model;

import java.sql.Timestamp;

public class LoginHistory {
    private int loginId;
    private int userId;
    private String username;
    private Timestamp loginTime;
    private Timestamp logoutTime;
    private String ipAddress;
    private String userAgent;
    private String status;

    public LoginHistory() {}

    public LoginHistory(int loginId, int userId, String username, Timestamp loginTime,
                        Timestamp logoutTime, String ipAddress, String userAgent,
                        String status) {
        this.loginId = loginId;
        this.userId = userId;
        this.username = username;
        this.loginTime = loginTime;
        this.logoutTime = logoutTime;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.status = status;
    }

    // Getters and Setters
    public int getLoginId() { return loginId; }
    public void setLoginId(int loginId) { this.loginId = loginId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Timestamp getLoginTime() { return loginTime; }
    public void setLoginTime(Timestamp loginTime) { this.loginTime = loginTime; }

    public Timestamp getLogoutTime() { return logoutTime; }
    public void setLogoutTime(Timestamp logoutTime) { this.logoutTime = logoutTime; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}