package application;

import Backend.JobConnect;
import Backend.models.User;
import application.admin.ManageUsersPage;
import application.admin.ManageQueriesPage;
import application.jobposter.JobSelectionPage;
import application.jobposter.PostJobPage;
import application.jobseeker.JobListingsPage;
import application.shared.ManageJobsPage;
import application.shared.NotificationsPage;
import application.shared.UserSupportPage;
import application.user.ProfilePage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Dashboard {
    private final SceneManager sceneManager;
    private final BorderPane root;
    private final JobConnect jobConnect;

    public Dashboard(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
        this.root = new BorderPane();
        this.jobConnect = JobConnect.getInstance();
    }

    public Scene getScene() {
        // Fetch the logged-in user details from SessionManager
        User loggedInUser = jobConnect.getSessionUser();

        if (loggedInUser == null) {
            // Redirect to LoginPage if session is missing
            System.err.println("No user session found. Redirecting to login page.");
            sceneManager.switchTo("LoginPage");
            return null;
        }

        // Extract user details
        String userType = loggedInUser.getRole();
        root.setPadding(new Insets(20));

        // Header Section
        HBox header = createHeader();
        root.setTop(header);

        // Sidebar
        VBox sidebar = createSidebar(userType);
        root.setLeft(sidebar);

        // Content based on user type
        VBox dashboardOverview = createDashboardOverview(loggedInUser);
        root.setCenter(dashboardOverview);

        // Footer
        HBox footer = createFooter();
        root.setBottom(footer);

        Scene scene = new Scene(root, 1000, 600);
        scene.getStylesheets().add(getClass().getResource("dashboard.css").toExternalForm());
        return scene;
    }

    /**
     * Creates the header section.
     */
    private HBox createHeader() {
        HBox header = new HBox();
        header.setPadding(new Insets(10, 20, 10, 20));
        header.setAlignment(Pos.CENTER_RIGHT);
        header.setStyle("-fx-background-color: #34495e;");

        Label headerLabel = new Label("Job Connect Dashboard");
        headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        headerLabel.setTextFill(Color.WHITE);

        header.getChildren().add(headerLabel);
        return header;
    }

    /**
     * Creates the sidebar based on the user's role.
     */
    private VBox createSidebar(String userType) {
        VBox sidebar = new VBox(20);
        sidebar.setPadding(new Insets(20));
        sidebar.setAlignment(Pos.TOP_LEFT);
        sidebar.setStyle("-fx-background-color: #2c3e50;");

        if (userType.equalsIgnoreCase("Admin")) {
            sidebar.getChildren().addAll(
                    createNavigationButton("Manage Users", () -> loadPage(new ManageUsersPage(sceneManager, root).getView())),
                    createNavigationButton("Manage Jobs", () -> loadPage(new ManageJobsPage(sceneManager, root).getView())),
                    createNavigationButton("Manage Queries", () -> loadPage(new ManageQueriesPage(root).getView())) // Added for admin
            );
        }

        if (userType.equalsIgnoreCase("Job Seeker")) {
            sidebar.getChildren().addAll(
                    createNavigationButton("Browse Jobs", () -> loadPage(new JobListingsPage(sceneManager, root).getView()))
            );
        } else if (userType.equalsIgnoreCase("Job Poster")) {
            sidebar.getChildren().addAll(
                    createNavigationButton("Post Job", () -> loadPage(new PostJobPage(sceneManager, root).getView())),
                    createNavigationButton("Manage Jobs", () -> loadPage(new ManageJobsPage(sceneManager, root, true).getView())),
                    createNavigationButton("Manage Applications", () -> loadPage(new JobSelectionPage(root).getView()))
            );
        }

        if (!userType.equalsIgnoreCase("Admin")) {
            sidebar.getChildren().addAll(
                    createNavigationButton("Notifications", () -> loadPage(new NotificationsPage(root).getView())),
                    createNavigationButton("User Support", () -> loadPage(new UserSupportPage(root).getView()))
            );
        }

        sidebar.getChildren().addAll(
                createNavigationButton("Profile", () -> loadPage(new ProfilePage(root).getView())),
                createNavigationButton("Logout", () -> {
                    jobConnect.logoutUser(); // Clear session on logout
                    sceneManager.switchTo("LoginPage");
                })
        );

        return sidebar;
    }

    /**
     * Creates the dashboard overview tailored to the logged-in user.
     */
    private VBox createDashboardOverview(User loggedInUser) {
        VBox overview = new VBox(20);
        overview.setPadding(new Insets(20));
        overview.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Welcome, " + loggedInUser.getName() + "!");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        title.setTextFill(Color.DARKBLUE);

        String userType = loggedInUser.getRole();

        if (userType.equalsIgnoreCase("Admin")) {
            Label statsHeader = new Label("Admin Dashboard Overview");
            statsHeader.setFont(Font.font("Arial", FontWeight.BOLD, 18));

            int totalUsers = jobConnect.getAllUsers().size();
            int jobsPosted = jobConnect.getAllJobs().size();
            int activeAdmins = (int) jobConnect.getAllUsers().stream()
                    .filter(user -> user.getRole().equalsIgnoreCase("Admin"))
                    .count();

            HBox stats = new HBox(20);
            stats.setAlignment(Pos.CENTER);
            stats.getChildren().addAll(
                    createStatBox("Total Users", String.valueOf(totalUsers)),
                    createStatBox("Jobs Posted", String.valueOf(jobsPosted)),
                    createStatBox("Active Admins", String.valueOf(activeAdmins))
            );

            overview.getChildren().addAll(title, statsHeader, stats);
        } else if (userType.equalsIgnoreCase("Job Seeker")) {
            Label statsHeader = new Label("Your Dashboard Overview");
            statsHeader.setFont(Font.font("Arial", FontWeight.BOLD, 18));

            int jobsApplied = jobConnect.getApplicationsForUser(loggedInUser.getUserId()).size();

            HBox stats = new HBox(20);
            stats.setAlignment(Pos.CENTER);
            stats.getChildren().addAll(
                    createStatBox("Jobs Applied", String.valueOf(jobsApplied))
            );

            overview.getChildren().addAll(title, statsHeader, stats);
        } else if (userType.equalsIgnoreCase("Job Poster")) {
            Label statsHeader = new Label("Job Poster Dashboard Overview");
            statsHeader.setFont(Font.font("Arial", FontWeight.BOLD, 18));

            int jobsPostedByUser = (int) jobConnect.getAllJobs().stream()
                    .filter(job -> job.getPosterId() == loggedInUser.getUserId())
                    .count();

            int totalApplicationsForJobs = jobConnect.getApplicationsForPoster(jobConnect.getSessionUser().getUserId()).size();
            
            HBox stats = new HBox(20);
            stats.setAlignment(Pos.CENTER);
            stats.getChildren().addAll(
                    createStatBox("Jobs Posted", String.valueOf(jobsPostedByUser)),
                    createStatBox("Applications Received", String.valueOf(totalApplicationsForJobs))
            );

            overview.getChildren().addAll(title, statsHeader, stats);
        }

        return overview;
    }

    private VBox createStatBox(String label, String value) {
        VBox statBox = new VBox(5);
        statBox.setAlignment(Pos.CENTER);
        statBox.setPadding(new Insets(10));
        statBox.setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #bdc3c7; -fx-border-radius: 5; -fx-background-radius: 5;");

        Label statValue = new Label(value);
        statValue.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        statValue.setTextFill(Color.DARKBLUE);

        Label statLabel = new Label(label);
        statLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

        statBox.getChildren().addAll(statValue, statLabel);
        return statBox;
    }

    private HBox createFooter() {
        HBox footer = new HBox(10);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(10));
        footer.setStyle("-fx-background-color: #34495e;");

        Label privacyPolicy = new Label("Privacy Policy");
        privacyPolicy.setTextFill(Color.WHITE);

        Label terms = new Label("Terms of Service");
        terms.setTextFill(Color.WHITE);

        footer.getChildren().addAll(privacyPolicy, new Label("|"), terms);
        return footer;
    }

    private Button createNavigationButton(String text, Runnable action) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10;");
        button.setOnAction(e -> action.run());
        button.setMaxWidth(Double.MAX_VALUE);
        return button;
    }

    private void loadPage(Node content) {
        root.setCenter(content);
    }
}
