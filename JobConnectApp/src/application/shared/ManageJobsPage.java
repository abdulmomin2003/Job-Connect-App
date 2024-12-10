package application.shared;

import Backend.JobConnect;
import Backend.entities.Job;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import application.SceneManager;
import application.admin.AdminEditJobPage;

public class ManageJobsPage {
	
	
	 private final  boolean isPoster;
    private final SceneManager sceneManager;
    private final BorderPane root;
    private final JobConnect jobConnect = JobConnect.getInstance();

    private VBox jobListContainer; // Container for dynamic job cards

    
    public ManageJobsPage(SceneManager sceneManager, BorderPane root) {
    	this.isPoster = false;	
        this.sceneManager = sceneManager;
        this.root = root;    
    }
    public ManageJobsPage(SceneManager sceneManager, BorderPane root,boolean isPoster) {
    	
    	this.isPoster = isPoster;;	
        this.sceneManager = sceneManager;
        this.root = root;
    }

    /**
     * Returns the interface for managing jobs.
     */
    public VBox getView() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_CENTER);

        Label titleLabel = new Label("Manage Jobs");
        titleLabel.setFont(Font.font("Arial", 24));
        titleLabel.setTextFill(Color.DARKBLUE);

        // Search bar
        HBox searchBar = createSearchBar();

        // Scrollable pane for jobs
        ScrollPane scrollPane = createScrollableJobList();

        layout.getChildren().addAll(titleLabel, searchBar, scrollPane);
        return layout;
    }

    /**
     * Creates a search bar to filter jobs.
     */
    private HBox createSearchBar() {
        HBox searchBar = new HBox(10);
        searchBar.setAlignment(Pos.CENTER);
        searchBar.setPadding(new Insets(10));
        searchBar.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 10;");

        TextField searchField = new TextField();
        searchField.setPromptText("Search by Job ID, Title, or Description");
        searchField.setPrefWidth(400);

        Button searchButton = new Button("Search");
        searchButton.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white;");
        searchButton.setOnAction(e -> performSearch(searchField.getText()));

        searchBar.getChildren().addAll(searchField, searchButton);
        return searchBar;
    }

    /**
     * Creates a scrollable pane for displaying jobs.
     */
    private ScrollPane createScrollableJobList() {
        jobListContainer = new VBox(10);
        jobListContainer.setAlignment(Pos.TOP_CENTER);
        jobListContainer.setPadding(new Insets(10));
        
        
       if(isPoster) {
        int ud=  jobConnect.getSessionUser().getUserId();
        // Populate with all jobs initially
        updateJobList(jobConnect.getAllJobs(ud));
        }else {
			// Populate with all jobs initially
			updateJobList(jobConnect.getAllJobs());
        }
     

        ScrollPane scrollPane = new ScrollPane(jobListContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");

        return scrollPane;
    }

    /**
     * Performs a search and updates the job list dynamically.
     */
    private void performSearch(String query) {
        if (query == null || query.trim().isEmpty()) {
            int ud=  jobConnect.getSessionUser().getUserId();
               // Populate with all jobs initially
               updateJobList(jobConnect.getAllJobs(ud));// Show all jobs if search is empty
            return;
        }

        // Filter jobs based on the query
        if(isPoster) {
        JobConnect jobConnect = JobConnect.getInstance(); // Initialize JobConnect singleto
        int ud=  jobConnect.getSessionUser().getUserId();
           // Populate with all jobs initially
           
        List<Job> filteredJobs = jobConnect.getAllJobs(ud).stream()
                .filter(job -> String.valueOf(job.getJobId()).equalsIgnoreCase(query) ||
                               job.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                               job.getDescription().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());

        updateJobList(filteredJobs);
        
        } else {
			List<Job> filteredJobs = jobConnect.getAllJobs().stream()
					.filter(job -> String.valueOf(job.getJobId()).equalsIgnoreCase(query)
							|| job.getTitle().toLowerCase().contains(query.toLowerCase())
							|| job.getDescription().toLowerCase().contains(query.toLowerCase()))
					.collect(Collectors.toList());
	
			updateJobList(filteredJobs);
	    }
    }

    /**
     * Updates the job list in the scrollable pane.
     */
    private void updateJobList(List<Job> jobList) {
        jobListContainer.getChildren().clear();

        if (jobList.isEmpty()) {
            Label noResultsLabel = new Label("No jobs found.");
            noResultsLabel.setTextFill(Color.RED);
            jobListContainer.getChildren().add(noResultsLabel);
        } else {
            for (Job job : jobList) {
                VBox jobCard = createJobCard(job);
                jobListContainer.getChildren().add(jobCard);
            }
        }
    }

    /**
     * Creates a card for a single job with options to edit, delete, or view details.
     */
    private VBox createJobCard(Job job) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #bdc3c7; -fx-border-radius: 10; -fx-background-radius: 10;");
        card.setAlignment(Pos.CENTER_LEFT);

        Label idLabel = new Label("Job ID: " + job.getJobId());
        idLabel.setFont(Font.font("Arial", 14));
        Label titleLabel = new Label("Title: " + job.getTitle());
        titleLabel.setFont(Font.font("Arial", 16));
        titleLabel.setStyle("-fx-font-weight: bold;");
        Label descriptionLabel = new Label("Description: " + job.getDescription());
        descriptionLabel.setFont(Font.font("Arial", 14));
        Label salaryLabel = new Label("Salary: $" + job.getSalary());
        salaryLabel.setFont(Font.font("Arial", 14));

        Button editButton = new Button("Edit");
        editButton.setStyle("-fx-background-color: #f1c40f; -fx-text-fill: white;");
        editButton.setOnAction(e -> loadEditJobPage(job.getJobId()));
        Button deleteButton = new Button("Delete");
        deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        deleteButton.setOnAction(e -> {
        	jobConnect.deleteJob(job.getJobId());
            updateJobList(jobConnect.getAllJobs());
        });

        Button detailsButton = new Button("View Details");
        detailsButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        detailsButton.setOnAction(e -> viewJobDetails(job));

        HBox buttonContainer = new HBox(10, detailsButton, editButton, deleteButton);
        buttonContainer.setAlignment(Pos.CENTER);

        card.getChildren().addAll(idLabel, titleLabel, descriptionLabel, salaryLabel, buttonContainer);
        return card;
    }

    /**
     * Navigates to the Edit Job Page for the selected job.
     */
    private void loadEditJobPage(int jobId) {
        AdminEditJobPage editJobPage = new AdminEditJobPage(sceneManager, root, jobId);
        root.setCenter(editJobPage.getView());
    }

    /**
     * Shows the job details in a new view.
     */
    private void viewJobDetails(Job job) {
        VBox detailsView = new VBox(20);
        detailsView.setPadding(new Insets(20));
        detailsView.setAlignment(Pos.TOP_CENTER);
        detailsView.setStyle("-fx-background-color: #f4f6f7; -fx-border-color: #dcdcdc; -fx-border-radius: 10; -fx-padding: 20;");

        // Title Section
        Label pageTitle = new Label("Job Details");
        pageTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        pageTitle.setTextFill(Color.DARKBLUE);

        // Job Information Section
        VBox jobInfoBox = new VBox(10);
        jobInfoBox.setPadding(new Insets(10));
        jobInfoBox.setAlignment(Pos.TOP_LEFT);
        jobInfoBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #bdc3c7; -fx-border-radius: 5; -fx-padding: 10;");

        Label idLabel = createStyledLabel("Job ID: ", String.valueOf(job.getJobId()));
        Label titleLabel = createStyledLabel("Title: ", job.getTitle());
        Label descriptionLabel = createStyledLabel("Description: ", job.getDescription());
        Label salaryLabel = createStyledLabel("Salary: ", "$" + job.getSalary());

        jobInfoBox.getChildren().addAll(idLabel, titleLabel, descriptionLabel, salaryLabel);

        // Requirements Section
        Label requirementsTitle = new Label("Requirements:");
        requirementsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        requirementsTitle.setTextFill(Color.DARKBLUE);

        ListView<String> requirementsListView = new ListView<>(FXCollections.observableArrayList(job.getRequirements()));
        requirementsListView.setStyle("-fx-background-color: #ffffff; -fx-border-color: #dcdcdc;");
        requirementsListView.setPrefHeight(Math.min(job.getRequirements().size() * 24 + 10, 150)); // Adjust height based on content

        // Back Button
        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10;");
        backButton.setOnAction(e -> root.setCenter(getView()));

        // Assembling the Layout
        detailsView.getChildren().addAll(pageTitle, jobInfoBox, requirementsTitle, requirementsListView, backButton);

        root.setCenter(detailsView);
    }

    /**
     * Helper method to create styled labels with bolded prefixes.
     */
    private Label createStyledLabel(String prefix, String content) {
        Label label = new Label(prefix + content);
        label.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        label.setStyle("-fx-text-fill: #34495e;");
        return label;
    }

}
