package application;

import Backend.JobConnect;
import Backend.entities.Application;
import Backend.models.User;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.Optional;

public class ManageJobApplications {

    private final BorderPane dashboardRoot;
    private final String jobTitle;
    private final String jobDescription;
    private final int jobId;
    private final JobConnect jobConnect; // Use JobConnect for backend communication
    private List<Application> applicants; // List to track applicants
    private final VBox applicantsLayout; // VBox to dynamically update the applicants' display
    private HBox selectedApplicantBox; // Track the currently selected applicant's HBox

    public ManageJobApplications(BorderPane dashboardRoot, String jobTitle, String jobDescription, int jobId) {
        this.dashboardRoot = dashboardRoot;
        this.jobTitle = jobTitle;
        this.jobDescription = jobDescription;
        this.jobId = jobId;
        this.jobConnect = JobConnect.getInstance(); // Use JobConnect instance
        this.applicants = initializeApplicants(); // Initialize applicants list
        this.applicantsLayout = new VBox(15); // VBox to hold applicant views
        this.selectedApplicantBox = null; // Initially, no applicant is selected
    }

    public VBox getView() {
        VBox layout = new VBox(25);
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #f9fafc;");

        // Job Information
        Label jobInfoLabel = new Label("Job Title: " + jobTitle);
        jobInfoLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

        // Section title for applicants
        Label applicantsTitleLabel = new Label("Applicants");
        applicantsTitleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

        // Populate initial list of applicants
        refreshApplicantList();

        // Wrap the applicants list inside a ScrollPane to make it scrollable
        ScrollPane scrollPane = new ScrollPane(applicantsLayout);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-focus-color: transparent;");

        // Add elements to the layout
        layout.getChildren().addAll(jobInfoLabel, applicantsTitleLabel, scrollPane);

        return layout;
    }

    private List<Application> initializeApplicants() {
        // Fetch all applications for the given job via JobConnect
        List<Application> allApplicants = jobConnect.getApplicationsForJob(jobId);

        // Filter out only the pending applications
        return allApplicants.stream()
                .filter(application -> "Pending".equalsIgnoreCase(application.getStatus()))
                .toList();
    }

    private void refreshApplicantList() {
        applicantsLayout.getChildren().clear(); // Clear the current list
        for (Application applicant : applicants) {
            HBox applicantBox = createApplicantView(applicant);

            // Get the buttons from the applicant box
            HBox buttonsBox = (HBox) ((VBox) applicantBox.getChildren().get(0)).getChildren().get(1);
            Button acceptBtn = (Button) buttonsBox.getChildren().get(1);
            Button declineBtn = (Button) buttonsBox.getChildren().get(2);

            // Disable buttons if the applicant is already accepted
            if ("Accepted".equalsIgnoreCase(applicant.getStatus())) {
                acceptBtn.setDisable(true);
                declineBtn.setDisable(true);
            }

            applicantsLayout.getChildren().add(applicantBox);
        }
    }

    private HBox createApplicantView(Application applicant) {
        HBox applicantBox = new HBox(15);
        applicantBox.setPadding(new Insets(15));
        applicantBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #dce1e3; -fx-border-radius: 10; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0.5, 0, 0);");

        // Details Section
        VBox detailsBox = new VBox(8); // Compact stacking of labels
        detailsBox.setAlignment(Pos.TOP_LEFT);

        // Fetch user details via JobConnect
        Optional<User> user = jobConnect.getUserById(applicant.getUserId());
        String userName = user.map(User::getName).orElse("Unknown User");
        String userEmail = user.map(User::getEmail).orElse("Unknown Email");

        Label nameLabel = new Label("Name: " + userName);
        nameLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495e;");

        Label emailLabel = new Label("Email: " + userEmail);
        emailLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");

        // Fetch job title via JobConnect
        String jobAppliedFor = jobConnect.getJobById(applicant.getJobId())
                .map(job -> job.getTitle())
                .orElse("Unknown Job");

        Label jobLabel = new Label("Applied for: " + jobAppliedFor);
        jobLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");

        Label statusLabel = new Label("Status: " + applicant.getStatus());
        statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + getStatusColor(applicant.getStatus()) + ";");

        detailsBox.getChildren().addAll(nameLabel, emailLabel, jobLabel, statusLabel);

        // Buttons Section
        HBox buttonsBox = new HBox(10);
        buttonsBox.setAlignment(Pos.CENTER_RIGHT);

        Button viewProfileBtn = createActionButton("View Cover Letter", "#2980b9");
        viewProfileBtn.setOnAction(e -> viewProfile(applicant));

        Button acceptBtn = createActionButton("Accept", "#27ae60");
        Button declineBtn = createActionButton("Decline", "#e74c3c");

        setupApplicationActionButtons(acceptBtn, declineBtn, applicant, applicantBox);

        buttonsBox.getChildren().addAll(viewProfileBtn, acceptBtn, declineBtn);

        // Combine details and buttons into main card
        VBox contentBox = new VBox(10);
        contentBox.getChildren().addAll(detailsBox, buttonsBox);

        applicantBox.getChildren().add(contentBox);
        return applicantBox;
    }

    private void setupApplicationActionButtons(Button acceptBtn, Button declineBtn, Application applicant, HBox applicantBox) {
        acceptBtn.setOnAction(e -> {
            applicant.setStatus("Accepted");

            // Disable the Decline button
            declineBtn.setDisable(true);
            acceptBtn.setDisable(true); // Optionally disable the Accept button as well

            // Notify the applicant
            jobConnect.notify(applicant.getUserId(), "Congratulations! Your application for " + jobTitle + " has been accepted.");

            // Update the application's status via JobConnect
            jobConnect.updateApplicationStatus(applicant.getApplicationId(),"Accepted");
            refreshApplicantList(); // Refresh to update the status
        });

        declineBtn.setOnAction(e -> {
            applicants.remove(applicant); // Remove applicant from the list
            applicantsLayout.getChildren().remove(applicantBox); // Remove the card from the UI

            // Update the application's status via JobConnect
            jobConnect.updateApplicationStatus(applicant.getApplicationId(),"Declined");

            // Notify the applicant
            jobConnect.notify(applicant.getUserId(), "We regret to inform you that your application for " + jobTitle + " has been declined.");
        });
    }

    private void viewProfile(Application applicant) {
        dashboardRoot.setCenter(getProfileView(applicant));
    }

    private VBox getProfileView(Application applicant) {
        VBox profileLayout = new VBox(20);
        profileLayout.setAlignment(Pos.TOP_CENTER);
        profileLayout.setPadding(new Insets(20));
        profileLayout.setStyle("-fx-background-color: #f9fafc;");

        // Title
        Label titleLabel = new Label("Applicant Profile");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

        // Fetch user details via JobConnect
        Optional<User> user = jobConnect.getUserById(applicant.getUserId());
        String userName = user.map(User::getName).orElse("Unknown User");
        String userEmail = user.map(User::getEmail).orElse("Unknown Email");

        Label nameLabel = new Label("Name: " + userName);
        nameLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #34495e;");

        Label emailLabel = new Label("Email: " + userEmail);
        emailLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #7f8c8d;");

        String jobAppliedFor = jobConnect.getJobById(applicant.getJobId())
                .map(job -> job.getTitle())
                .orElse("Unknown Job");

        Label jobLabel = new Label("Applied for: " + jobAppliedFor);
        jobLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #7f8c8d;");

        Label statusLabel = new Label("Status: " + applicant.getStatus());
        statusLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: " + getStatusColor(applicant.getStatus()) + ";");

        // Fetch cover letter
        String coverLetter = applicant.getCoverLetter(); // Assume `getCoverLetter()` method exists in the Application class
        Label coverLetterLabel = new Label("Cover Letter:");
        coverLetterLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

        TextArea coverLetterArea = new TextArea(coverLetter);
        coverLetterArea.setEditable(false); // Make it read-only
        coverLetterArea.setWrapText(true);
        coverLetterArea.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d; -fx-border-color: #bdc3c7; -fx-border-radius: 5; -fx-background-radius: 5;");
        coverLetterArea.setPrefHeight(150);
        coverLetterArea.setPrefWidth(400);

        // Back button
        Button backButton = new Button("Back to Applicants");
        backButton.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20; -fx-border-radius: 8;");
        backButton.setOnAction(e -> dashboardRoot.setCenter(getView()));

        profileLayout.getChildren().addAll(
                titleLabel, nameLabel, emailLabel, jobLabel, statusLabel, coverLetterLabel, coverLetterArea, backButton
        );

        return profileLayout;
    }

    private String getStatusColor(String status) {
        switch (status.toLowerCase()) {
            case "accepted":
                return "#27ae60"; // Green for accepted
            case "declined":
                return "#e74c3c"; // Red for declined
            default:
                return "#e67e22"; // Orange for pending
        }
    }
    private Button createActionButton(String text, String color) {
        Button button = new Button(text);

        // Base style
        String baseStyle = "-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 6 12; -fx-border-radius: 5; -fx-background-radius: 5;";
        String hoverStyle = "-fx-background-color: #1f618d; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 6 12; -fx-border-radius: 5; -fx-background-radius: 5;";

        // Apply base style
        button.setStyle(baseStyle);

        // Hover behavior
        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(baseStyle));

        return button;
    }
}
