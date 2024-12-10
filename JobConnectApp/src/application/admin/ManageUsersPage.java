package application.admin;

import Backend.models.User;
import Backend.JobConnect;
import application.SceneManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.List;
import java.util.stream.Collectors;

public class ManageUsersPage {
    private final SceneManager sceneManager;
    private final BorderPane root;
    private VBox userListContainer; // Container for dynamic user rows
    private final JobConnect jobConnect; // Backend encapsulation through JobConnect

    public ManageUsersPage(SceneManager sceneManager, BorderPane root) {
        this.sceneManager = sceneManager;
        this.root = root;
        this.jobConnect = JobConnect.getInstance(); // Initialize JobConnect singleton
    }

    /**
     * Returns the complete scene for managing users.
     */
    public Scene getScene() {
        // Set up the center view (main content)
        VBox content = getView();
        root.setCenter(content);

        // Return the root wrapped in a scene
        return new Scene(root, 1000, 600);
    }

    /**
     * Returns the interface for managing users.
     */
    public VBox getView() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_CENTER);

        Label titleLabel = new Label("Manage Users");
        titleLabel.setFont(Font.font("Arial", 24));
        titleLabel.setTextFill(Color.DARKBLUE);

        // Search bar
        HBox searchBar = createSearchBar();

        // Scrollable pane for users
        ScrollPane scrollPane = createScrollableUserList();

        layout.getChildren().addAll(titleLabel, searchBar, scrollPane);
        return layout;
    }

    /**
     * Creates a search bar to filter users.
     */
    private HBox createSearchBar() {
        HBox searchBar = new HBox(10);
        searchBar.setAlignment(Pos.CENTER);
        searchBar.setPadding(new Insets(10));
        searchBar.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 10;");

        TextField searchField = new TextField();
        searchField.setPromptText("Search by ID, Name, or Email");
        searchField.setPrefWidth(300);

        Button searchButton = new Button("Search");
        searchButton.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white;");
        searchButton.setOnAction(e -> performSearch(searchField.getText()));

        searchBar.getChildren().addAll(searchField, searchButton);
        return searchBar;
    }

    /**
     * Creates a scrollable pane for displaying users.
     */
    private ScrollPane createScrollableUserList() {
        userListContainer = new VBox(10);
        userListContainer.setAlignment(Pos.TOP_CENTER);
        userListContainer.setPadding(new Insets(10));

        // Populate with all users initially
        List<User> allUsers = jobConnect.getAllUsers(); // Fetch users via JobConnect
        updateUserList(allUsers);

        ScrollPane scrollPane = new ScrollPane(userListContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");

        return scrollPane;
    }

    /**
     * Performs a search and updates the user list dynamically.
     */
    private void performSearch(String query) {
        if (query == null || query.trim().isEmpty()) {
            updateUserList(jobConnect.getAllUsers()); // Show all users if search is empty
            return;
        }

        // Filter users based on the query
        List<User> filteredUsers = jobConnect.getAllUsers().stream()
                .filter(user -> String.valueOf(user.getUserId()).equals(query) ||
                        user.getName().toLowerCase().contains(query.toLowerCase()) ||
                        user.getEmail().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());

        updateUserList(filteredUsers);
    }

    /**
     * Updates the user list in the scrollable pane.
     */
    private void updateUserList(List<User> userList) {
        userListContainer.getChildren().clear();

        if (userList.isEmpty()) {
            Label noResultsLabel = new Label("No users found.");
            noResultsLabel.setTextFill(Color.RED);
            userListContainer.getChildren().add(noResultsLabel);
        } else {
            for (User user : userList) {
                HBox userRow = createUserRow(user);
                userListContainer.getChildren().add(userRow);
            }
        }
    }

    /**
     * Creates a row for a single user with edit and delete options.
     */
    private HBox createUserRow(User user) {
        HBox row = new HBox(20);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #dcdcdc; -fx-padding: 10;");
        row.setPadding(new Insets(10));

        Label idLabel = new Label("ID: " + user.getUserId());
        idLabel.setFont(Font.font("Arial", 14));
        Label nameLabel = new Label("Name: " + user.getName());
        nameLabel.setFont(Font.font("Arial", 14));
        Label emailLabel = new Label("Email: " + user.getEmail());
        emailLabel.setFont(Font.font("Arial", 14));

        Button editButton = new Button("Edit");
        editButton.setStyle("-fx-background-color: #f1c40f; -fx-text-fill: white;");
        editButton.setOnAction(e -> loadEditUserPage(user));

        Button deleteButton = new Button("Delete");
        deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        deleteButton.setOnAction(e -> {
            // Prevent deleting the logged-in user
            User loggedInUser = jobConnect.getSessionUser();
            if (loggedInUser.getUserId() == user.getUserId()) {
                showAlert(Alert.AlertType.ERROR, "Error", "You cannot delete your own account.");
                return;
            }

            // Delete the user via JobConnect
            boolean success = jobConnect.deleteUser(user.getUserId());
            if (success) {
                // Update the displayed user list
                updateUserList(jobConnect.getAllUsers());
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete user.");
            }
        });

        row.getChildren().addAll(idLabel, nameLabel, emailLabel, editButton, deleteButton);
        return row;
    }

    /**
     * Navigates to the Edit User Page for the selected user.
     */
    private void loadEditUserPage(User user) {
        EditUserPage editUserPage = new EditUserPage(sceneManager, root, user.getUserId());
        root.setCenter(editUserPage.getView());
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
