package application.jobposter;

import Backend.JobConnect;
import Backend.entities.Job;
import application.SceneManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.List;

public class PostJobPage {
    private final SceneManager sceneManager;
    private final BorderPane dashboardRoot;
    private final JobConnect jobConnect;

    public PostJobPage(SceneManager sceneManager, BorderPane dashboardRoot) {
        this.sceneManager = sceneManager;
        this.dashboardRoot = dashboardRoot;
        this.jobConnect = JobConnect.getInstance(); // Singleton instance of JobConnect
    }

    public ScrollPane getView() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setStyle("-fx-background-color: #ecf0f1;"); // Light background color

        // Page Title
        Label titleLabel = new Label("Post a New Job");
        titleLabel.setFont(Font.font("Arial", 24));
        titleLabel.setTextFill(Color.web("#34495e")); // Consistent dark blue text

        // Input Sections
        VBox jobTitleSection = createInputSection("Job Title", "Enter Job Title");
        VBox jobDescriptionSection = createInputSection("Job Description", "Enter Job Description", true);
        VBox salarySection = createInputSection("Salary", "Enter Salary");

        // Requirements Section
        VBox requirementsSection = createRequirementsSection();

        // Post Job Button
        Button postJobButton = createStyledButton("Post Job", "#2980b9");
        postJobButton.setOnAction(e -> {
            String jobTitle = ((TextField) jobTitleSection.getChildren().get(1)).getText();
            String jobDescription = ((TextArea) jobDescriptionSection.getChildren().get(1)).getText();
            String salary = ((TextField) salarySection.getChildren().get(1)).getText();
            List<String> requirements = extractRequirements(requirementsSection);

            if (jobTitle.isEmpty() || jobDescription.isEmpty() || salary.isEmpty() || requirements.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Incomplete Information", "Please fill all fields before posting the job.");
            } else {
                // Get the logged-in user ID from SessionManager
                int posterId = jobConnect.getSessionUser().getUserId();

                // Create a new Job object
                Job job = new Job(0, jobTitle, jobDescription, salary, posterId, requirements);

                // Save the job through JobConnect
                boolean success = jobConnect.addJob(job);
                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Job Posted", "Your job has been posted successfully!");
                    dashboardRoot.setCenter(new Label("Job Posted Successfully!")); // Placeholder message
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to post the job. Please try again.");
                }
            }
        });

        // Add components to layout
        layout.getChildren().addAll(
                titleLabel,
                jobTitleSection,
                jobDescriptionSection,
                salarySection,
                requirementsSection,
                postJobButton
        );

        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true); // Ensures the content stretches to the full width of the ScrollPane
        scrollPane.setStyle("-fx-background:  #ecf0f1; -fx-padding: 10;"); // Style for the ScrollPane

        return scrollPane;
    }

    /**
     * Creates an input section with a label and a text field or text area.
     */
    private VBox createInputSection(String labelText, String placeholder, boolean isTextArea) {
        Label label = new Label(labelText);
        label.setFont(Font.font("Arial", 16));
        label.setTextFill(Color.web("#34495e"));

        Region inputField;
        if (isTextArea) {
            TextArea textArea = new TextArea();
            textArea.setPromptText(placeholder);
            textArea.setPrefRowCount(5);
            textArea.setStyle("-fx-font-size: 14px; -fx-background-color: #ffffff; -fx-border-color: #bdc3c7; -fx-border-radius: 5; -fx-padding: 10;");
            inputField = textArea;
        } else {
            TextField textField = new TextField();
            textField.setPromptText(placeholder);
            textField.setStyle("-fx-font-size: 14px; -fx-background-color: #ffffff; -fx-border-color: #bdc3c7; -fx-border-radius: 5; -fx-padding: 8;");
            inputField = textField;
        }

        VBox section = new VBox(5, label, inputField);
        section.setPadding(new Insets(10));
        section.setStyle("-fx-background-color: #ffffff; -fx-border-color: #bdc3c7; -fx-border-radius: 5; -fx-background-radius: 5;");
        return section;
    }

    private VBox createInputSection(String labelText, String placeholder) {
        return createInputSection(labelText, placeholder, false);
    }

    /**
     * Creates the job requirements section with a dropdown and added requirements list.
     */
    private VBox createRequirementsSection() {
        Label requirementsLabel = new Label("Job Requirements");
        requirementsLabel.setFont(Font.font("Arial", 16));
        requirementsLabel.setTextFill(Color.web("#34495e"));

        List<String> selectedRequirements = new ArrayList<>();
        Label selectedRequirementsLabel = new Label("Added Requirements: None");
        selectedRequirementsLabel.setFont(Font.font("Arial", 14));
        selectedRequirementsLabel.setWrapText(true);

        TextField addRequirementField = new TextField();
        addRequirementField.setPromptText("Enter a requirement");

        Button addRequirementButton = createStyledButton("Add Requirement", "#34495e");
        addRequirementButton.setOnAction(e -> {
            String requirement = addRequirementField.getText().trim();
            if (!requirement.isEmpty() && !selectedRequirements.contains(requirement)) {
                selectedRequirements.add(requirement);
                selectedRequirementsLabel.setText("Added Requirements: " + String.join(", ", selectedRequirements));
                addRequirementField.clear();
            }
        });

        VBox section = new VBox(10, requirementsLabel, addRequirementField, addRequirementButton, selectedRequirementsLabel);
        section.setPadding(new Insets(10));
        section.setStyle("-fx-background-color: #ffffff; -fx-border-color: #bdc3c7; -fx-border-radius: 5; -fx-background-radius: 5;");
        section.setUserData(selectedRequirements); // Store requirements list in user data for later retrieval
        return section;
    }

    /**
     * Extracts requirements from the requirements section.
     */
    private List<String> extractRequirements(VBox requirementsSection) {
        Object userData = requirementsSection.getUserData();
        return (userData instanceof List) ? (List<String>) userData : new ArrayList<>();
    }

    /**
     * Creates a styled button with consistent theming.
     */
    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10 20; -fx-border-radius: 5; -fx-background-radius: 5;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #1abc9c; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10 20; -fx-border-radius: 5; -fx-background-radius: 5;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10 20; -fx-border-radius: 5; -fx-background-radius: 5;"));
        return button;
    }

    /**
     * Displays a styled alert dialog.
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
