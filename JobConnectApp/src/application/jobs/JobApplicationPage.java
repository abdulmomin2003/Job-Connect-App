package application.jobs;

import application.SceneManager;
import application.jobseeker.MCQTestPage;
import Backend.JobConnect;
import Backend.entities.Application;
import Backend.entities.Job;
import Backend.services.GeminiApiTest;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;

public class JobApplicationPage {
    private final SceneManager sceneManager;
    private final Job job;
    private final BorderPane dashboardRoot;
    private boolean testPassed = false; // Tracks the test result
    private VBox layout; // Main layout to dynamically update the view

    public JobApplicationPage(SceneManager sceneManager, Job job, BorderPane dashboardRoot) {
        this.sceneManager = sceneManager;
        this.job = job;
        this.dashboardRoot = dashboardRoot;
    }

    public VBox getView() {
        layout = new VBox(20);
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #f4f6f7;");

        // Job Title
        Label titleLabel = new Label("Application for: " + job.getTitle());
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

        // MCQ Test Section
        Button takeTestButton = createStyledButton("Take MCQ Test", "#3498db");
        takeTestButton.setOnAction(e -> {
            GeminiApiTest geminiService = new GeminiApiTest();
            MCQTestPage mcqTestPage = new MCQTestPage(sceneManager, dashboardRoot, this, geminiService, null, job);
            dashboardRoot.setCenter(mcqTestPage.getView());
        });

        // Back Button
        Button backButton = createStyledButton("Back to Job Details", "#2980b9");
        backButton.setOnAction(e -> {
            JobDetailsPage jobDetailsPage = new JobDetailsPage(sceneManager, job, dashboardRoot);
            dashboardRoot.setCenter(jobDetailsPage.getView());
        });

        layout.getChildren().addAll(titleLabel, takeTestButton, backButton);
        return layout;
    }

    /**
     * Updates the view dynamically based on the test result.
     */
    public void updateView(boolean passed, double score) {
        this.testPassed = passed;
        layout.getChildren().clear();

        Label titleLabel = new Label("Application for: " + job.getTitle());
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

        if (testPassed) {
            // Show success message and "Apply for Job" button
            Label successLabel = new Label("Congratulations! You passed the test with a score of " + score + "%.");
            successLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #27ae60;");
            layout.getChildren().addAll(titleLabel, successLabel, createApplyButton());
        } else {
            // Show failure message and recommended courses
            Label failureLabel = new Label("Unfortunately, you did not pass the test. Your score: " + score + "%.");
            failureLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #e74c3c;");
            layout.getChildren().addAll(titleLabel, failureLabel, createCourseRecommendations());
        }

        // Back Button
        Button backButton = createStyledButton("Back to Job Details", "#2980b9");
        backButton.setOnAction(e -> {
            JobDetailsPage jobDetailsPage = new JobDetailsPage(sceneManager, job, dashboardRoot);
            dashboardRoot.setCenter(jobDetailsPage.getView());
        });

        layout.getChildren().add(backButton);
    }

    /**
     * Creates the "Apply for Job" button with a cover letter feature.
     */
    private Button createApplyButton() {
        Button applyButton = createStyledButton("Apply for Job", "#27ae60");
        applyButton.setOnAction(e -> {
            // Show a dialog for entering the cover letter
            TextArea coverLetterArea = new TextArea();
            coverLetterArea.setPromptText("Write your cover letter here...");
            coverLetterArea.setWrapText(true);
            coverLetterArea.setPrefHeight(150);

            Button submitButton = createStyledButton("Submit Application", "#27ae60");
            submitButton.setOnAction(event -> {
                String coverLetter = coverLetterArea.getText().trim();
                if (coverLetter.isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Cover Letter Missing", "Please write a cover letter before submitting your application.");
                    return;
                }

                // Process application submission
                processApplication(coverLetter);
            });

            VBox coverLetterBox = new VBox(10, new Label("Enter your cover letter:"), coverLetterArea, submitButton);
            coverLetterBox.setPadding(new Insets(20));
            coverLetterBox.setAlignment(Pos.CENTER);
            coverLetterBox.setStyle("-fx-background-color: #f4f6f7;");

            // Set the dialog to the main layout
            layout.getChildren().clear();
            layout.getChildren().add(coverLetterBox);
        });
        return applyButton;
    }

    /**
     * Processes the job application with the provided cover letter.
     */
    private void processApplication(String coverLetter) {
        JobConnect jobConnect = JobConnect.getInstance();
        int userId = jobConnect.getSessionUser().getUserId();

        // Create an Application object with the cover letter
        Application application = new Application(job.getJobId(), userId, "Pending", coverLetter);

        // Save the application using JobConnect
        boolean success = jobConnect.applyForJob(application);

        // Show a confirmation or error message based on the success of the operation
        if (success) {
            Alert confirmation = new Alert(Alert.AlertType.INFORMATION);
            confirmation.setTitle("Application Submitted");
            confirmation.setHeaderText("Application for " + job.getTitle() + " Submitted Successfully!");
            confirmation.setContentText("Your cover letter has been submitted along with your application.");
            confirmation.showAndWait();

            // Redirect to job details page
            JobDetailsPage jobDetailsPage = new JobDetailsPage(sceneManager, job, dashboardRoot);
            dashboardRoot.setCenter(jobDetailsPage.getView());
        } else {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Application Failed");
            errorAlert.setHeaderText("Failed to Submit Application");
            errorAlert.setContentText("An error occurred while submitting your application. Please try again.");
            errorAlert.showAndWait();
        }
    }


    /**
     * Creates a VBox displaying recommended courses.
     */
    private VBox createCourseRecommendations() {
        VBox coursesBox = new VBox(10);
        coursesBox.setAlignment(Pos.TOP_CENTER);
        coursesBox.setPadding(new Insets(10));
        coursesBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #bdc3c7; -fx-border-radius: 5; -fx-background-radius: 5;");

        Label recommendationLabel = new Label("Recommended Courses:");
        recommendationLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

        coursesBox.getChildren().addAll(
                recommendationLabel,
                createStyledLabel("1. Java Programming Basics - Learn Java Fundamentals"),
                createStyledLabel("2. Spring Boot Essentials - Build RESTful APIs"),
                createStyledLabel("3. Advanced Data Structures and Algorithms in Java")
        );

        return coursesBox;
    }

    /**
     * Creates a styled label for course recommendations.
     */
    private Label createStyledLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 14px; -fx-text-fill: #2c3e50;");
        return label;
    }

    /**
     * Creates a styled button.
     */
    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10 20; -fx-border-radius: 5; -fx-background-radius: 5;");
        return button;
    }

    /**
     * Shows an alert dialog.
     */
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
