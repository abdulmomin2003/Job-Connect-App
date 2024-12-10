package application.jobseeker;

import Backend.JobConnect;
import Backend.entities.Job;
import Backend.persistence.DatabaseHandler;
import application.SceneManager;
import application.jobs.JobDetailsPage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;

import java.util.List;

public class JobListingsPage {
    private final SceneManager sceneManager;
    private final BorderPane dashboardRoot; // To dynamically update the center area
    private final JobConnect jobConnect;

    public JobListingsPage(SceneManager sceneManager, BorderPane dashboardRoot) {
        this.sceneManager = sceneManager;
        this.dashboardRoot = dashboardRoot;
        this.jobConnect = JobConnect.getInstance();
    }

    /**
     * Generates the main view for the Job Listings page.
     */
    public VBox getView() {
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #f4f6f7;");

        Label titleLabel = new Label("Available Jobs");
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setPadding(new Insets(10));
        scrollPane.setStyle("-fx-background-color: transparent; -fx-focus-color: transparent;");

        VBox jobContainer = new VBox(15);
        jobContainer.setPadding(new Insets(10));

        // Fetch jobs from the backend
        
        
        List<Job> jobs = jobConnect.getJobsBySeekerId();
        //display jobs here print 
       
        if (jobs.isEmpty()) {
            Label noJobsLabel = new Label("No jobs available at the moment.");
            noJobsLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #7f8c8d;");
            jobContainer.getChildren().add(noJobsLabel);
        } else {
            for (Job job : jobs) {
                jobContainer.getChildren().add(createJobBox(job));
            }
        }

        scrollPane.setContent(jobContainer);

        layout.getChildren().addAll(titleLabel, scrollPane);
        return layout;
    }

    /**
     * Creates a styled job card for each job listing.
     */
    private VBox createJobBox(Job job) {
        VBox jobBox = new VBox(10);
        jobBox.setPadding(new Insets(15));
        jobBox.setStyle("-fx-border-color: #dce1e3; -fx-border-width: 1; -fx-background-color: #ffffff; -fx-border-radius: 10; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0.3, 0, 0);");

        Label jobTitle = new Label(job.getTitle());
        jobTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 20px; -fx-text-fill: #34495e;");

        String jobDetailsText = String.format("Poster: %d | Salary: %s | Requirements: %s",
                job.getPosterId(), job.getSalary(), String.join(", ", job.getRequirements()));
        Label jobDetails = new Label(jobDetailsText);
        jobDetails.setWrapText(true);
        jobDetails.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");

        HBox actionButtons = new HBox(10);
        actionButtons.setAlignment(Pos.CENTER_RIGHT);

        Button viewDetailsButton = new Button("View Details");
        styleButton(viewDetailsButton, "#3498db");
        viewDetailsButton.setOnMouseEntered(e -> viewDetailsButton.setStyle("-fx-background-color: #2c81ba; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 8 16; -fx-border-radius: 5; -fx-background-radius: 5;"));
        viewDetailsButton.setOnMouseExited(e -> styleButton(viewDetailsButton, "#3498db"));

        viewDetailsButton.setOnAction(e -> {
            JobDetailsPage jobDetailsPage = new JobDetailsPage(sceneManager, job, dashboardRoot);
            dashboardRoot.setCenter(jobDetailsPage.getView());
        });

        actionButtons.getChildren().add(viewDetailsButton);
        HBox.setHgrow(viewDetailsButton, Priority.ALWAYS);

        jobBox.getChildren().addAll(jobTitle, jobDetails, actionButtons);
        return jobBox;
    }

    /**
     * Styles a button consistently across the application.
     */
    private void styleButton(Button button, String color) {
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 8 16; -fx-border-radius: 5; -fx-background-radius: 5;");
    }
}
