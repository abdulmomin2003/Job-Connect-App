package Backend.controllers;

import Backend.entities.Notification;
import Backend.persistence.DatabaseHandler;

import java.util.List;

public class NotificationController {
    private final DatabaseHandler databaseHandler;

    public NotificationController() {
        this.databaseHandler = DatabaseHandler.getInstance();
    }

    /**
     * Retrieves all notifications for a specific user.
     */
    public List<Notification> getNotificationsForUser(int userId) {
        return databaseHandler.getNotificationsForUser(userId);
    }

    /**
     * Marks a notification as read.
     */
    public boolean markNotificationAsRead(int notificationId) {
        return databaseHandler.markNotificationAsRead(notificationId);
    }

    /**
     * Adds a new notification.
     */
    public boolean addNotification(Notification notification) {
        return databaseHandler.saveNotification(notification);
    }

    /**
     * Deletes a notification by its ID.
     */
    public boolean deleteNotification(int notificationId) {
        return databaseHandler.deleteNotification(notificationId);
    }
    public boolean deleteAllNotificationsForUser(int userId) {
        try {
            // Fetch all notifications for the user
            List<Notification> notifications = databaseHandler.getNotificationsForUser(userId);

            // Delete each notification
            for (Notification notification : notifications) {
                boolean success = databaseHandler.deleteNotification(notification.getNotificationId());
                if (!success) {
                    System.err.println("Failed to delete notification with ID: " + notification.getNotificationId());
                    return false; // Stop and return false if any deletion fails
                }
            }
            System.out.println("All notifications for user ID " + userId + " have been deleted.");
            return true; // Return true if all notifications are successfully deleted
        } catch (Exception e) {
            System.err.println("Error deleting notifications for user ID " + userId + ": " + e.getMessage());
            return false; // Return false in case of any exception
        }
    }
    public boolean notify(int userId, String message) {
        // Create a new Notification object
        Notification notification = new Notification(userId, message);

        // Attempt to save the notification to the database
        boolean success = databaseHandler.saveNotification(notification);

        // Log success or failure
        if (success) {
            System.out.println("Notification sent to User ID " + userId + ": " + message);
        } else {
            System.err.println("Failed to send notification to User ID " + userId + ": " + message);
        }

        // Return the success status
        return success;
    }


}
