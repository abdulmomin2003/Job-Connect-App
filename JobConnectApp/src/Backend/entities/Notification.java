package Backend.entities;

import java.util.Date;

public class Notification {
    private int notificationId;      // Unique ID for each notification
    private int userId;              // ID of the user to whom the notification is addressed
    private String message;          // Notification message content
    private Date date;               // The date when the notification was generated
    private boolean isRead;          // Whether the notification has been read or not

    // Constructors

    /**
     * Full constructor for loading notifications from the database.
     */
    public Notification(int notificationId, int userId, String message, Date date, boolean isRead) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.message = message;
        this.date = date;
        this.isRead = isRead;
    }
    

    public Notification(int userId, String message) {
        this.notificationId = 0; // Will be auto-generated when saved to the database
        this.userId = userId;
        this.message = message;
        this.date = new Date(); // Set the current timestamp
        this.isRead = false; // Default to unread
    }

    // Getters and Setters
    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }

    // Mark the notification as read
    public void markAsRead() {
        this.isRead = true;
    }

    // ToString for debugging and logging
    @Override
    public String toString() {
        return "Notification{" +
                "notificationId=" + notificationId +
                ", userId=" + userId +
                ", message='" + message + '\'' +
                ", date=" + date +
                ", isRead=" + isRead +
                '}';
    }
}
