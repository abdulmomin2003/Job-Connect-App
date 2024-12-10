package application.user;

import Backend.JobConnect;
import Backend.models.User;
import application.Dashboard;
import application.SceneManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.Optional;

public class LoginPage {
    private final SceneManager sceneManager;
    private final JobConnect jobConnect; // Centralized controller for communication with the back-end

    public LoginPage(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
        this.jobConnect = JobConnect.getInstance(); // Instantiate the JobConnect controller
    }

    public Scene getScene() {
        // Main container with centering
        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: #f4f6f7;"); // Consistent background color

        // Card container for form
        VBox card = new VBox(20);
        card.setPadding(new Insets(30));
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #dcdcdc; -fx-border-width: 1;");

        // Shadow effect
        card.setEffect(new DropShadow(10, Color.GRAY));

        // Header section
        Text title = new Text("Login to Your Account");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setFill(Color.web("#34495e")); // Dark gray color for text

        Text subtitle = new Text("Access your dashboard and manage opportunities.");
        subtitle.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        subtitle.setFill(Color.GRAY);

        // Form inputs
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setPrefHeight(40);
        usernameField.setStyle("-fx-font-size: 14px; -fx-border-color: #bdc3c7; -fx-border-radius: 5; -fx-background-radius: 5;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setPrefHeight(40);
        passwordField.setStyle("-fx-font-size: 14px; -fx-border-color: #bdc3c7; -fx-border-radius: 5; -fx-background-radius: 5;");

        // Buttons
        Button loginButton = new Button("Login");
        styleButton(loginButton, "#27ae60"); // Green for Login button
        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            // Validate inputs
            if (username.isEmpty() || password.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Login Error", "Please fill in all fields.");
                return;
            }

            // Authenticate user via UserController
            Optional<User> user = jobConnect.authenticateUser(username, password);

            if (user.isPresent()) {
            	

                // Redirect to Dashboard
                Dashboard dashboard = new Dashboard(sceneManager);
                sceneManager.addScene("Dashboard", dashboard.getScene());
                sceneManager.switchTo("Dashboard");
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Error", "Invalid username or password.");
            }
        });


        Button registerButton = new Button("Register");
        styleButton(registerButton, "#2980b9"); // Blue for Register button
        registerButton.setOnAction(e -> {
            RegisterPage registerPage = new RegisterPage(sceneManager);
            sceneManager.addScene("Register", registerPage.getScene());
            sceneManager.switchTo("Register");
        });

        // Footer text
        Text footer = new Text("Don't have an account? Register now!");
        footer.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        footer.setFill(Color.DARKGRAY);

        // Arrange components
        card.getChildren().addAll(title, subtitle, usernameField, passwordField, loginButton, registerButton, footer);
        root.getChildren().add(card);

        return new Scene(root, 500, 600);
    }

    /**
     * Styles a button consistently.
     */
    private void styleButton(Button button, String color) {
        button.setPrefWidth(200);
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10;");
    }

    /**
     * Utility method to show alerts.
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
