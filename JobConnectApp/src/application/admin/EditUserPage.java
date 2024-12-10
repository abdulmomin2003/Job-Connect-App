package application.admin;

import Backend.models.User;
import Backend.JobConnect;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.Optional;

import application.SceneManager;

public class EditUserPage {
    private final SceneManager sceneManager;
    private final BorderPane root;
    private final int userId; // User ID to fetch and edit the user
    private final JobConnect jobConnect; // Encapsulation with JobConnect

    public EditUserPage(SceneManager sceneManager, BorderPane root, int userId) {
        this.sceneManager = sceneManager;
        this.root = root;
        this.userId = userId;
        this.jobConnect = JobConnect.getInstance(); // Singleton instance of JobConnect
    }

    /**
     * Returns the interface for editing user details.
     */
    public VBox getView() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_CENTER);

        // Fetch user from JobConnect
        Optional<User> optionalUser = jobConnect.getUserById(userId);
        if (optionalUser.isEmpty()) {
            // Show an error if the user doesn't exist
            Label errorLabel = new Label("User not found!");
            errorLabel.setTextFill(Color.RED);
            layout.getChildren().add(errorLabel);
            return layout;
        }

        User user = optionalUser.get();

        // Title
        Text title = new Text("Edit User Details");
        title.setFont(Font.font("Arial", 24));
        title.setFill(Color.DARKBLUE);

        // Form Fields
        TextField idField = new TextField(String.valueOf(user.getUserId()));
        idField.setEditable(false); // ID is read-only
        styleTextField(idField);

        TextField nameField = new TextField(user.getName());
        styleTextField(nameField);

        TextField emailField = new TextField(user.getEmail());
        styleTextField(emailField);

        // Save and Back Buttons
        Button saveButton = new Button("Save Changes");
        styleButton(saveButton, "#4CAF50");
        saveButton.setOnAction(e -> {
            // Update the user's data
            user.updateProfile(nameField.getText(), emailField.getText());

            // Save changes using JobConnect
            if (jobConnect.updateUser(user)) {
                System.out.println("User updated: " + user.getName());
                returnToManageUsersPage();
            } else {
                showError("Failed to update user. Please try again.");
            }
        });

        Button backButton = new Button("Back to Manage Users");
        styleButton(backButton, "#e74c3c");
        backButton.setOnAction(e -> returnToManageUsersPage());

        // Layout assembly
        layout.getChildren().addAll(title, idField, nameField, emailField, saveButton, backButton);
        return layout;
    }

    /**
     * Returns to the Manage Users page.
     */
    private void returnToManageUsersPage() {
        ManageUsersPage manageUsersPage = new ManageUsersPage(sceneManager, root);
        root.setCenter(manageUsersPage.getView());
    }

    /**
     * Displays an error message as a dialog.
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Styles a text field consistently.
     */
    private void styleTextField(TextField textField) {
        textField.setEditable(true);
        textField.setStyle("-fx-font-size: 14px; -fx-border-color: #bdc3c7; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 5;");
        textField.setPrefWidth(300);
    }

    /**
     * Styles a button with a given background color.
     */
    private void styleButton(Button button, String color) {
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10; -fx-border-radius: 5; -fx-background-radius: 5;");
        button.setPrefWidth(200);
    }
}
