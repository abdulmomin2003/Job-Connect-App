package application.jobs;

import Backend.entities.Job;
import application.SceneManager;
import application.jobseeker.JobListingsPage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class JobDetailsPage {
    private final SceneManager sceneManager;
    private final Job job; // Backend's Job entity
    private final BorderPane dashboardRoot;

    public JobDetailsPage(SceneManager sceneManager, Job job, BorderPane dashboardRoot) {
        this.sceneManager = sceneManager;
        this.job = job;
        this.dashboardRoot = dashboardRoot;
    }

    /**
     * Generates the detailed view for a specific job.
     */
    public VBox getView() {
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #f4f6f7;");

        // Job Title
        Label titleLabel = new Label(job.getTitle());
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

        // Job Details (Dynamic from the backend)
        Label salaryLabel = new Label("Salary: " + job.getSalary());
        Label descriptionLabel = new Label("Job Description: " + job.getDescription());
        salaryLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #2c3e50;");
        descriptionLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #2c3e50;");

        // Requirements (Dynamically displayed)
        Label requirementsLabel = new Label("Requirements:");
        requirementsLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

        VBox requirementsContainer = new VBox(5);
        requirementsContainer.setPadding(new Insets(10));
        requirementsContainer.setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #dcdcdc; -fx-border-radius: 5; -fx-background-radius: 5;");
        if (job.getRequirements().isEmpty()) {
            requirementsContainer.getChildren().add(new Label("No specific requirements listed."));
        } else {
            for (String requirement : job.getRequirements()) {
                Label requirementLabel = new Label("- " + requirement);
                requirementLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2c3e50;");
                requirementsContainer.getChildren().add(requirementLabel);
            }
        }

        // Buttons for Apply and Back to Listings
        Button applyButton = createStyledButton("Apply for Job", "#27ae60");
        applyButton.setOnAction(e -> {
            JobApplicationPage jobApplicationPage = new JobApplicationPage(sceneManager, job, dashboardRoot);
            dashboardRoot.setCenter(jobApplicationPage.getView());
        });

        Button backButton = createStyledButton("Back to Job Listings", "#2980b9");
        backButton.setOnAction(e -> {
            JobListingsPage jobListingsPage = new JobListingsPage(sceneManager, dashboardRoot);
            dashboardRoot.setCenter(jobListingsPage.getView());
        });

        layout.getChildren().addAll(
                titleLabel,
                salaryLabel,
                descriptionLabel,
                requirementsLabel,
                requirementsContainer,
                applyButton,
                backButton
        );

        return layout;
    }

    /**
     * Creates a styled button with consistent design.
     */
    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10 20; -fx-border-radius: 5; -fx-background-radius: 5;");
        return button;
    }

}
