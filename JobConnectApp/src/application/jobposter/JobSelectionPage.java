package application.jobposter;

import javafx.geometry.Pos;

import java.util.List;

import Backend.JobConnect;
import Backend.entities.Application;
import Backend.entities.Job;
import Backend.persistence.DatabaseHandler;
import application.ManageJobApplications;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.BorderPane;

public class JobSelectionPage {

    private final BorderPane dashboardRoot;
    

    public JobSelectionPage(BorderPane dashboardRoot) {
        this.dashboardRoot = dashboardRoot;
    }

    public VBox getView() {
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #f9fafc;");

        // Section title
        Label pageTitleLabel = new Label("Select a Job to Manage Applications");
        pageTitleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

        // Example list of posted jobs
        VBox jobListLayout = new VBox(20);  // Increased gap between job cards
        jobListLayout.setAlignment(Pos.TOP_CENTER);
        jobListLayout.setPadding(new Insets(10));
        
        //List of application
//        List<Application> applications = DatabaseHandler.getInstance().getApplicationsForPoster(JobConnect.getInstance().getSessionUser().getUserId());
//        
//        
//        //Now get the jobs  from the jobID in the applications
//        
//        
//        for (Application application : applications) {
//        	//get the job from the application
//        	Job job = DatabaseHandler.getInstance().getJobByApplicationId(application.getJobId());
//        	jobListLayout.getChildren().add(createJobView(job.getTitle(), job.getDescription(), (job.getJobId())));
//        	}
//        
        // Sample jobs - Increased number for better testing
       List<Job> jobs = DatabaseHandler.getInstance().getAllJobs(JobConnect.getInstance().getSessionUser().getUserId());
    
       
       for (Job job : jobs) {
           jobListLayout.getChildren().add(createJobView(job.getTitle(), job.getDescription(), (job.getJobId())));
           
           }

        // Wrap the job list inside a ScrollPane to make it scrollable
        ScrollPane scrollPane = new ScrollPane(jobListLayout);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-focus-color: transparent;");

        // Add elements to the layout
        layout.getChildren().addAll(pageTitleLabel, scrollPane);

        return layout;
    }

    // Method to create a styled job card for each job
    private HBox createJobView(String title, String description, int jobId) {
        HBox jobBox = new HBox(20);
        jobBox.setAlignment(Pos.CENTER_LEFT);
        jobBox.setPadding(new Insets(15));
        jobBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #dce1e3; -fx-border-radius: 10; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0.5, 0, 0);");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

        Label descriptionLabel = new Label(description);
        descriptionLabel.setWrapText(true);
        descriptionLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #7f8c8d;");

        // Spacer to push the button to the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button manageApplicationsButton = createStyledButton("Manage Applications", "#3498db");
        manageApplicationsButton.setStyle("-fx-font-size: 12px; -fx-padding: 5 10; -fx-background-color: #3498db; -fx-text-fill: white; -fx-border-radius: 5; -fx-background-radius: 5;");
        manageApplicationsButton.setOnMouseEntered(e -> manageApplicationsButton.setStyle("-fx-font-size: 12px; -fx-padding: 5 10; -fx-background-color: #2c81ba; -fx-text-fill: white; -fx-border-radius: 5; -fx-background-radius: 5;"));
        manageApplicationsButton.setOnMouseExited(e -> manageApplicationsButton.setStyle("-fx-font-size: 12px; -fx-padding: 5 10; -fx-background-color: #3498db; -fx-text-fill: white; -fx-border-radius: 5; -fx-background-radius: 5;"));
        manageApplicationsButton.setOnAction(e -> navigateToManageJobApplications(title, description, jobId));

        jobBox.getChildren().addAll(titleLabel, descriptionLabel, spacer, manageApplicationsButton);
        return jobBox;
    }


    // Method to navigate to the ManageJobApplications page
    private void navigateToManageJobApplications(String jobTitle, String jobDescription, int jobId) {
        ManageJobApplications manageJobApplications = new ManageJobApplications(dashboardRoot, jobTitle, jobDescription, jobId);
        dashboardRoot.setCenter(manageJobApplications.getView());
    }

    // Method to navigate back to the dashboard
    private void navigateBackToDashboard() {
        Label welcomeLabel = new Label("Welcome to Job Connect Dashboard!");
        welcomeLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        VBox welcomeContent = new VBox(welcomeLabel);
        welcomeContent.setAlignment(Pos.CENTER);
        welcomeContent.setStyle("-fx-background-color: #ecf0f1; -fx-padding: 30;");

        dashboardRoot.setCenter(welcomeContent);
    }

    // Method to create a styled button
    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10 20; -fx-border-radius: 5; -fx-background-radius: 5;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #1f618d; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10 20; -fx-border-radius: 5; -fx-background-radius: 5;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10 20; -fx-border-radius: 5; -fx-background-radius: 5;"));
        return button;
    }
}
