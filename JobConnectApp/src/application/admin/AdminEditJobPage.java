package application.admin;

import Backend.entities.Job;
import Backend.JobConnect;
import application.SceneManager;
import application.shared.ManageJobsPage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.Optional;

public class AdminEditJobPage {
    private final SceneManager sceneManager;
    private final BorderPane root;
    private final int jobId; // Job ID to fetch and edit the job
    private final JobConnect jobConnect; // Backend handler using JobConnect
    private ObservableList<String> requirementsList; // Dynamic list of requirements

    public AdminEditJobPage(SceneManager sceneManager, BorderPane root, int jobId) {
        this.sceneManager = sceneManager;
        this.root = root;
        this.jobId = jobId;
        this.jobConnect = JobConnect.getInstance(); // Use JobConnect singleton
    }

    /**
     * Returns the scrollable interface for editing a job.
     */
    public ScrollPane getView() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_CENTER);

        // Fetch job from JobConnect
        Optional<Job> optionalJob = jobConnect.getJobById(jobId);
        if (optionalJob.isEmpty()) {
            // Show an error if the job doesn't exist
            Label errorLabel = new Label("Job not found!");
            errorLabel.setTextFill(Color.RED);
            layout.getChildren().add(errorLabel);
            return new ScrollPane(layout); // Return an empty scrollable layout
        }

        Job job = optionalJob.get();

        // Title
        Label pageTitle = new Label("Admin Edit Job Details");
        pageTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        pageTitle.setTextFill(Color.DARKBLUE);

        // Form Fields
        TextField idField = new TextField(String.valueOf(job.getJobId()));
        idField.setEditable(false); // Job ID is read-only
        idField.setPromptText("Job ID");

        TextField titleField = new TextField(job.getTitle());
        titleField.setPromptText("Job Title");

        // Dynamically sized description field based on content
        TextArea descriptionField = new TextArea(job.getDescription());
        descriptionField.setPromptText("Job Description");
        descriptionField.setWrapText(true);
        descriptionField.setPrefHeight(getDescriptionHeight(job.getDescription())); // Set height based on content

        TextField salaryField = new TextField(job.getSalary());
        salaryField.setPromptText("Salary");

        // Requirements Section
        Label requirementsLabel = new Label("Requirements:");
        requirementsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        requirementsLabel.setTextFill(Color.DARKBLUE);

        // Dynamically sized ListView for requirements
        requirementsList = FXCollections.observableArrayList(job.getRequirements());
        ListView<String> requirementsListView = new ListView<>(requirementsList);
        requirementsListView.setPrefHeight(getRequirementsHeight(requirementsList.size())); // Set height based on item count

        // Add Requirement Field
        HBox addRequirementBox = new HBox(10);
        addRequirementBox.setAlignment(Pos.CENTER);
        TextField addRequirementField = new TextField();
        addRequirementField.setPromptText("Add a new requirement");
        Button addRequirementButton = new Button("Add");
        addRequirementButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        addRequirementButton.setOnAction(e -> {
            String newRequirement = addRequirementField.getText().trim();
            if (!newRequirement.isEmpty() && !requirementsList.contains(newRequirement)) {
                requirementsList.add(newRequirement);
                requirementsListView.setPrefHeight(getRequirementsHeight(requirementsList.size())); // Update height
                addRequirementField.clear();
            }
        });
        addRequirementBox.getChildren().addAll(addRequirementField, addRequirementButton);

        // Remove Selected Requirement Button
        Button removeRequirementButton = new Button("Remove Selected");
        removeRequirementButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        removeRequirementButton.setOnAction(e -> {
            String selectedRequirement = requirementsListView.getSelectionModel().getSelectedItem();
            if (selectedRequirement != null) {
                requirementsList.remove(selectedRequirement);
                requirementsListView.setPrefHeight(getRequirementsHeight(requirementsList.size())); // Update height
            }
        });

        // Save Button
        Button saveButton = new Button("Save Changes");
        saveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        saveButton.setOnAction(e -> {
            // Update job object
            job.setTitle(titleField.getText());
            job.setDescription(descriptionField.getText());
            job.setSalary(salaryField.getText());
            job.setRequirements(requirementsList);

            // Validate and save changes via JobConnect
            if (!job.getTitle().isEmpty() && !job.getDescription().isEmpty() && !job.getSalary().isEmpty() && !job.getRequirements().isEmpty()) {
                if (jobConnect.updateJob(job)) {
                    System.out.println("Job updated: " + job.getTitle());
                    returnToManageJobsPage();
                } else {
                    showError("Failed to update job. Please try again.");
                }
            } else {
                showError("All fields and requirements must be filled!");
            }
        });

        // Cancel Button
        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        cancelButton.setOnAction(e -> returnToManageJobsPage());

        HBox buttonContainer = new HBox(10, saveButton, cancelButton);
        buttonContainer.setAlignment(Pos.CENTER);

        // Layout assembly
        layout.getChildren().addAll(pageTitle, idField, titleField, descriptionField, salaryField, requirementsLabel, requirementsListView, addRequirementBox, removeRequirementButton, buttonContainer);

        // Make the layout scrollable
        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        return scrollPane;
    }

    /**
     * Calculates dynamic height for the description box based on the number of lines.
     */
    private double getDescriptionHeight(String description) {
        int lines = description.split("\n").length; // Number of lines in the description
        return Math.min(lines * 20 + 20, 150); // 20px per line, max height 150px
    }

    /**
     * Calculates dynamic height for the requirements list based on the number of items.
     */
    private double getRequirementsHeight(int itemCount) {
        return Math.min(itemCount * 24 + 10, 150); // 24px per item, max height 150px
    }

    /**
     * Returns to the Manage Jobs page.
     */
    private void returnToManageJobsPage() {
        ManageJobsPage manageJobsPage = new ManageJobsPage(sceneManager, root);
        root.setCenter(manageJobsPage.getView());
    }

    /**
     * Displays an error message as a dialog.
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
