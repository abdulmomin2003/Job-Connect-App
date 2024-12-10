package application.user;

import Backend.JobConnect;
import Backend.models.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class ProfilePage {
    private final BorderPane dashboardRoot;
    private final JobConnect jobConnect; // Central backend service
    private boolean isEditable = false; // Tracks if fields are editable

    public ProfilePage(BorderPane dashboardRoot) {
        this.dashboardRoot = dashboardRoot;
        this.jobConnect = JobConnect.getInstance(); // Instantiate JobConnect
    }

    /**
     * Returns the interface for the Profile Page.
     */
    public VBox getView() {
        // Fetch the current user from the session
        User currentUser = jobConnect.getSessionUser();
        if (currentUser == null) {
            throw new IllegalStateException("No user is logged in.");
        }

        // Main layout for the Profile Page
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setStyle("-fx-background-color: #f4f6f7;");

        // Title for the Profile Page
        Label titleLabel = new Label("Your Profile");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

        // Fields to display user details
        TextField idField = new TextField(String.valueOf(currentUser.getUserId()));
        idField.setEditable(false);
        styleTextField(idField);

        TextField nameField = new TextField(currentUser.getName());
        styleTextField(nameField);

        TextField emailField = new TextField(currentUser.getEmail());
        styleTextField(emailField);

        TextField roleField = new TextField(currentUser.getRole());
        roleField.setEditable(false); // Role is non-editable
        styleTextField(roleField);

        // Toggle Edit Mode button
        Button toggleEditButton = new Button("Edit Profile");
        styleButton(toggleEditButton, "#3498db"); // Blue for Edit button
        toggleEditButton.setOnAction(e -> {
            isEditable = !isEditable; // Toggle edit state
            toggleFields(nameField, emailField);
            toggleEditButton.setText(isEditable ? "Save Changes" : "Edit Profile");

            if (!isEditable) {
                saveChanges(nameField.getText(), emailField.getText(), currentUser);
            }
        });

        // Back to Dashboard Button
        
        // Adding elements to the layout
        layout.getChildren().addAll(
                titleLabel,
                createFieldLayout("User ID:", idField),
                createFieldLayout("Name:", nameField),
                createFieldLayout("Email:", emailField),
                createFieldLayout("Role:", roleField),
                toggleEditButton
        );

        return layout;
    }

    /**
     * Toggles the fields between editable and non-editable.
     */
    private void toggleFields(TextField... fields) {
        for (TextField field : fields) {
            field.setEditable(isEditable);
            field.setStyle(isEditable ? "-fx-background-color: #ffffff;" : "-fx-background-color: #ecf0f1;");
        }
    }

    /**
     * Saves the changes to the backend using JobConnect.
     */
    private void saveChanges(String name, String email, User currentUser) {
        // Update the user's profile locally
        currentUser.updateProfile(name, email);

        // Update the user's details in the backend via JobConnect
        boolean success = jobConnect.updateUser(currentUser);

        if (success) {
            // Update the session with the latest user details
            jobConnect.updateSession(currentUser);

            // Show success alert
            showAlert(Alert.AlertType.INFORMATION, "Profile Updated", "Your profile has been successfully updated.");
        } else {
            // Show error alert
            showAlert(Alert.AlertType.ERROR, "Update Failed", "Failed to update your profile. Please try again.");
        }
    }

    /**
     * Returns the user to the Dashboard.
     */
    private void returnToDashboard() {
        Label welcomeLabel = new Label("Welcome to Job Connect Dashboard!");
        welcomeLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

        VBox welcomeContent = new VBox(welcomeLabel);
        welcomeContent.setAlignment(Pos.CENTER);
        welcomeContent.setPadding(new Insets(30));
        welcomeContent.setStyle("-fx-background-color: #f4f6f7;");

        dashboardRoot.setCenter(welcomeContent);
    }

    /**
     * Creates a styled field layout with a label and text field.
     */
    private HBox createFieldLayout(String labelText, TextField textField) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #34495e;");
        HBox fieldLayout = new HBox(10, label, textField);
        fieldLayout.setAlignment(Pos.CENTER_LEFT);
        fieldLayout.setPadding(new Insets(5, 0, 5, 0));
        return fieldLayout;
    }

    /**
     * Styles a text field consistently.
     */
    private void styleTextField(TextField textField) {
        textField.setEditable(false); // Default non-editable
        textField.setStyle("-fx-font-size: 16px; -fx-text-fill: #34495e; -fx-background-color: #ecf0f1; -fx-border-color: #dcdcdc; -fx-border-radius: 5;");
        textField.setPrefWidth(400);
    }

    /**
     * Styles a button with a given background color.
     */
    private void styleButton(Button button, String color) {
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10 20; -fx-border-radius: 5;");
        button.setPrefWidth(250);
    }

    /**
     * Displays an alert dialog.
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}