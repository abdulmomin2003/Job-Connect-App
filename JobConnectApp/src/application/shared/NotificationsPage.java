package application.shared;

import Backend.entities.Notification;
import Backend.JobConnect;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.List;
import java.util.stream.Collectors;

public class NotificationsPage {
    private final BorderPane dashboardRoot;
    private final JobConnect jobConnect; // Use JobConnect for backend communication
    private final int userId; // Logged-in user's ID
    private VBox notificationListContainer; // Container for notification cards

    public NotificationsPage(BorderPane dashboardRoot) {
        this.dashboardRoot = dashboardRoot;
        this.jobConnect = JobConnect.getInstance(); // Singleton instance of JobConnect
        this.userId = jobConnect.getSessionUser().getUserId(); // Retrieve logged-in user ID from session
    }

    /**
     * Returns the Notifications Page layout.
     */
    public VBox getView() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setStyle("-fx-background-color: #ecf0f1;");

        // Title
        Label titleLabel = new Label("Notifications");
        titleLabel.setFont(Font.font("Arial", 24));
        titleLabel.setTextFill(Color.DARKBLUE);

        // Search Bar
        HBox searchBar = createSearchBar();

        // Scrollable notifications list
        ScrollPane scrollPane = createScrollableNotificationsList();

        // Add all elements to layout
        layout.getChildren().addAll(titleLabel, searchBar, scrollPane);
        return layout;
    }

    /**
     * Creates a search bar with search and delete functionality.
     */
    private HBox createSearchBar() {
        HBox searchBar = new HBox(10);
        searchBar.setAlignment(Pos.CENTER);
        searchBar.setPadding(new Insets(10));
        searchBar.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 10;");

        // Search Field
        TextField searchField = new TextField();
        searchField.setPromptText("Search notifications...");
        searchField.setPrefWidth(300);

        // Search Button
        Button searchButton = new Button("Search");
        searchButton.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white;");
        searchButton.setOnAction(e -> performSearch(searchField.getText()));

        // Delete All Button
        Button deleteAllButton = new Button("Delete All");
        deleteAllButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        deleteAllButton.setOnAction(e -> deleteAllNotifications());

        searchBar.getChildren().addAll(searchField, searchButton, deleteAllButton);
        return searchBar;
    }

    /**
     * Creates a scrollable pane for notifications.
     */
    private ScrollPane createScrollableNotificationsList() {
        notificationListContainer = new VBox(10);
        notificationListContainer.setAlignment(Pos.TOP_CENTER);
        notificationListContainer.setPadding(new Insets(10));

        // Populate notifications using JobConnect
        updateNotificationList(jobConnect.getNotificationsForUser(userId));

        ScrollPane scrollPane = new ScrollPane(notificationListContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        return scrollPane;
    }

    /**
     * Updates the notification list dynamically.
     */
    private void updateNotificationList(List<Notification> notifications) {
        notificationListContainer.getChildren().clear();

        if (notifications.isEmpty()) {
            Label noResultsLabel = new Label("No notifications found.");
            noResultsLabel.setTextFill(Color.RED);
            notificationListContainer.getChildren().add(noResultsLabel);
        } else {
            for (Notification notification : notifications) {
                VBox notificationCard = createNotificationCard(notification);
                notificationListContainer.getChildren().add(notificationCard);
            }
        }
    }

    /**
     * Performs a search and updates the notifications list.
     */
    private void performSearch(String query) {
        List<Notification> allNotifications = jobConnect.getNotificationsForUser(userId);

        if (query == null || query.trim().isEmpty()) {
            updateNotificationList(allNotifications); // Show all notifications if search is empty
            return;
        }

        List<Notification> filteredNotifications = allNotifications.stream()
                .filter(notification -> notification.getMessage().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());

        updateNotificationList(filteredNotifications);
    }

    /**
     * Deletes all notifications for the user.
     */
    private void deleteAllNotifications() {
        boolean success = jobConnect.deleteAllNotificationsForUser(userId);
        if (success) {
            updateNotificationList(jobConnect.getNotificationsForUser(userId));
            System.out.println("All notifications have been deleted.");
        } else {
            System.err.println("Failed to delete all notifications.");
        }
    }

    /**
     * Deletes a single notification.
     */
    private void deleteNotification(Notification notification) {
        boolean success = jobConnect.deleteNotification(notification.getNotificationId());
        if (success) {
            updateNotificationList(jobConnect.getNotificationsForUser(userId));
            System.out.println("Notification deleted: " + notification.getMessage());
        } else {
            System.err.println("Failed to delete notification: " + notification.getNotificationId());
        }
    }

    /**
     * Creates a styled notification card with a delete button.
     */
    private VBox createNotificationCard(Notification notification) {
        VBox notificationCard = new VBox(10);
        notificationCard.setPadding(new Insets(15));
        notificationCard.setAlignment(Pos.CENTER_LEFT);
        notificationCard.setStyle("-fx-background-color: #ffffff; -fx-border-color: #bdc3c7; -fx-border-radius: 10; -fx-background-radius: 10;");

        Label notificationLabel = new Label(notification.getMessage());
        notificationLabel.setFont(Font.font("Arial", 16));
        notificationLabel.setTextFill(Color.web("#34495e"));

        Label dateLabel = new Label("Date: " + notification.getDate());
        dateLabel.setFont(Font.font("Arial", 12));
        dateLabel.setTextFill(Color.GRAY);

        Button deleteButton = new Button("Delete");
        deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        deleteButton.setOnAction(e -> deleteNotification(notification));

        HBox cardFooter = new HBox(deleteButton);
        cardFooter.setAlignment(Pos.CENTER_RIGHT);

        notificationCard.getChildren().addAll(notificationLabel, dateLabel, cardFooter);
        return notificationCard;
    }
}
