package application.user;

import Backend.controllers.UserController;
import Backend.models.Admin;
import Backend.models.JobPoster;
import Backend.models.JobSeeker;
import Backend.models.User;
import Backend.persistence.SessionManager;
import application.Dashboard;
import application.SceneManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class RegisterPage {
    private final SceneManager sceneManager;
    private final UserController userController; // Use UserController to handle registration logic

    public RegisterPage(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
        this.userController = new UserController(); // Initialize UserController
    }

    public Scene getScene() {
        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: #f4f6f7;");

        VBox card = new VBox(20);
        card.setPadding(new Insets(30));
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #bdc3c7; -fx-border-width: 1;");
        card.setEffect(new DropShadow(10, Color.GRAY));

        Text title = new Text("Create an Account");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setFill(Color.web("#34495e"));

        Text subtitle = new Text("Join our platform to connect with job opportunities.");
        subtitle.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        subtitle.setFill(Color.GRAY);

        TextField usernameField = createStyledTextField("Enter your username");
        TextField emailField = createStyledTextField("Enter your email");
        PasswordField passwordField = createStyledPasswordField("Enter your password");

        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll("Job Seeker", "Job Poster", "Admin");
        roleComboBox.setPromptText("Select Role");
        roleComboBox.setPrefHeight(40);
        roleComboBox.setStyle("-fx-font-size: 14px; -fx-border-color: #bdc3c7; -fx-border-radius: 5; -fx-background-radius: 5;");

        Button registerButton = createStyledButton("Register", "#27ae60");
        registerButton.setOnAction(e -> {
            String username = usernameField.getText();
            String email = emailField.getText();
            String password = passwordField.getText();
            String role = roleComboBox.getValue();

            if (role == null || username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Registration Error", "Please fill in all fields.");
                return;
            }

            // Create the appropriate user object based on the role
            User newUser;
            switch (role.toLowerCase()) {
                case "job seeker":
                    newUser = new JobSeeker(username, email, password);
                    break;
                case "job poster":
                    newUser = new JobPoster(username, email, password);
                    break;
                case "admin":
                    newUser = new Admin(username, email, password);
                    break;
                default:
                    showAlert(Alert.AlertType.ERROR, "Invalid Role", "Please select a valid role.");
                    return;
            }

            // Register the user via the UserController
            boolean success = userController.register(newUser);

            if (success) {
                // Save the logged-in user to the session
                SessionManager.getInstance().setLoggedInUser(newUser);

                // Show success message and redirect to Dashboard
                showAlert(Alert.AlertType.INFORMATION, "Registration Success", "Account created successfully!");

                // Redirect to Dashboard
                Dashboard dashboard = new Dashboard(sceneManager);
                sceneManager.addScene("Dashboard", dashboard.getScene());
                sceneManager.switchTo("Dashboard");
            } else {
                showAlert(Alert.AlertType.ERROR, "Registration Failed", "Account creation failed. Please try again.");
            }
        });

        Button backButton = createStyledButton("Back to Login", "#e74c3c");
        backButton.setOnAction(e -> sceneManager.switchTo("LoginPage"));

        card.getChildren().addAll(title, subtitle, usernameField, emailField, passwordField, roleComboBox, registerButton, backButton);
        root.getChildren().add(card);

        return new Scene(root, 500, 600);
    }

    private TextField createStyledTextField(String placeholder) {
        TextField textField = new TextField();
        textField.setPromptText(placeholder);
        textField.setPrefHeight(40);
        textField.setStyle("-fx-font-size: 14px; -fx-border-color: #bdc3c7; -fx-border-radius: 5; -fx-background-radius: 5;");
        return textField;
    }

    private PasswordField createStyledPasswordField(String placeholder) {
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText(placeholder);
        passwordField.setPrefHeight(40);
        passwordField.setStyle("-fx-font-size: 14px; -fx-border-color: #bdc3c7; -fx-border-radius: 5; -fx-background-radius: 5;");
        return passwordField;
    }

    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setPrefWidth(200);
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10 20; -fx-border-radius: 5; -fx-background-radius: 5;");
        return button;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
