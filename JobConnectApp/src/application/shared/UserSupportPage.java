package application.shared;

import Backend.entities.SupportQuery;
import Backend.JobConnect;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class UserSupportPage {
    private final BorderPane dashboardRoot;
    private final JobConnect jobConnect; // Use JobConnect for backend communication

    public UserSupportPage(BorderPane dashboardRoot) {
        this.dashboardRoot = dashboardRoot;
        this.jobConnect = JobConnect.getInstance(); // Get the singleton instance of JobConnect
    }

    public VBox getView() {
        // Main layout for the User Support page
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #f4f6f7;");

        // Title for the User Support page
        Label titleLabel = new Label("User Support");
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #34495e; -fx-padding: 0 0 20 0;");

        // Subtitle with instructions
        Label subtitleLabel = new Label("We are here to help! Please describe your issue or query below.");
        subtitleLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #7f8c8d; -fx-padding: 0 0 20 0;");

        // Create the query input area with card-like styling
        VBox queryCard = createQueryCard();

        // Create the Submit Query button
        Button submitQueryButton = createStyledButton("Submit Query", "#2980b9");
        submitQueryButton.setOnAction(e -> {
            TextArea queryArea = (TextArea) queryCard.getChildren().get(0);
            String queryText = queryArea.getText();

            if (queryText.isEmpty()) {
                showErrorDialog("Query cannot be empty!");
            } else {
                // Save the query using JobConnect
                boolean success = saveUserQuery(queryText);
                if (success) {
                    showConfirmationDialog("Your query has been submitted successfully. Our support team will get back to you soon!");
                } else {
                    showErrorDialog("Failed to submit your query. Please try again later.");
                }
            }
        });

        // Adding all elements to the layout
        layout.getChildren().addAll(titleLabel, subtitleLabel, queryCard, submitQueryButton);

        return layout;
    }

    // Helper method to create the query input card
    private VBox createQueryCard() {
        // Create a VBox for the card
        VBox queryCard = new VBox(10);
        queryCard.setAlignment(Pos.CENTER);
        queryCard.setPadding(new Insets(15));
        queryCard.setStyle("-fx-background-color: white; -fx-border-radius: 10px; -fx-border-color: #bdc3c7; -fx-background-radius: 10px; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.1), 10, 0, 2, 2);");

        // TextArea for the user query
        TextArea queryArea = new TextArea();
        queryArea.setPromptText("Describe your issue or query...");
        queryArea.setPrefRowCount(5);
        queryArea.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495e; -fx-border-color: #bdc3c7; -fx-border-radius: 8px; -fx-padding: 10; -fx-background-color: #fdfdfd;");

        queryCard.getChildren().add(queryArea);

        return queryCard;
    }

    // Helper method to create styled buttons
    private Button createStyledButton(String text, String backgroundColor) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + backgroundColor + "; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10 20; -fx-border-radius: 8px; -fx-background-radius: 8px;");
        button.setPrefWidth(200);
        return button;
    }

    // Helper method to display a confirmation dialog
    private void showConfirmationDialog(String message) {
        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #2c3e50;");

        VBox dialogLayout = new VBox(10, messageLabel, createStyledButton("OK", "#27ae60"));
        dialogLayout.setAlignment(Pos.CENTER);
        dialogLayout.setPadding(new Insets(20));
        dialogLayout.setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #bdc3c7; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        dashboardRoot.setCenter(dialogLayout);
    }

    // Helper method to display an error dialog
    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Save the user's query using JobConnect
    private boolean saveUserQuery(String queryText) {
        try {
            int userId = jobConnect.getSessionUser().getUserId(); // Get the logged-in user via JobConnect
            SupportQuery supportQuery = new SupportQuery(userId, queryText); // Create a new query
            return jobConnect.addSupportQuery(supportQuery); // Save it using JobConnect
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
