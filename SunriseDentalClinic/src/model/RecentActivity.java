package model;

import java.util.Date;

public class RecentActivity {
    private String icon;
    private String message;
    private Date timestamp;
    private String type;

    public RecentActivity() {}

    public RecentActivity(String icon, String message, Date timestamp, String type) {
        this.icon = icon;
        this.message = message;
        this.timestamp = timestamp;
        this.type = type;
    }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}