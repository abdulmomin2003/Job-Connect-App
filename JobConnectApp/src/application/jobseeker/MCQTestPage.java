package application.jobseeker;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import application.SceneManager;
import application.jobs.JobApplicationPage;
import Backend.JobConnect;
import Backend.entities.Application;
import Backend.entities.Course;
import Backend.entities.Job;
import Backend.entities.Question;
import Backend.services.GeminiApiTest;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class MCQTestPage {
    private final SceneManager sceneManager;
    private final BorderPane dashboardRoot;
    private final JobApplicationPage jobApplicationPage;
    private final GeminiApiTest geminiApiTest;
    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private final Job job;
    private int correctAnswers = 0;
    private String userAnswers = "";

    public MCQTestPage(SceneManager sceneManager, BorderPane dashboardRoot, JobApplicationPage jobApplicationPage, GeminiApiTest geminiApiTest, String jobRequirement, Job job) {
        this.sceneManager = sceneManager;
        this.dashboardRoot = dashboardRoot;
        this.jobApplicationPage = jobApplicationPage;
        this.geminiApiTest = geminiApiTest;
        this.questions = new ArrayList<>();
        this.job = job;
        
        List<String> requirementsList = job.getRequirements();

     // Convert List<String> to a comma-separated String
     String requirement = String.join(",", requirementsList);

     // Output the result
     System.out.println(requirement); // 
        // Fetch questions asynchronously
        fetchQuestionsAsync(requirement);
    }

    
    public VBox getView() {
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #f4f6f7;");

        if (questions.isEmpty()) {
            Label loadingLabel = new Label("Loading questions...");
            loadingLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #2c3e50;");
            layout.getChildren().add(loadingLabel);
        } else {
            showQuestion(layout);
        }

        Button backButton = createStyledButton("Back to Job Application", "#e74c3c");
        backButton.setOnAction(e -> {
            resetQuizState();
            dashboardRoot.setCenter(jobApplicationPage.getView());
        });

        layout.getChildren().add(backButton);

        return layout;
    }

    private void showQuestion(VBox layout) {
        layout.getChildren().clear();

        if (currentQuestionIndex < questions.size()) {
            Question question = questions.get(currentQuestionIndex);

            Label questionLabel = new Label(question.getText());
            questionLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

            VBox optionsBox = new VBox(10);
            optionsBox.setAlignment(Pos.CENTER_LEFT);

            ToggleGroup optionsGroup = new ToggleGroup();
            for (String option : question.getOptions()) {
                RadioButton radioButton = new RadioButton(option);
                radioButton.setToggleGroup(optionsGroup);
                radioButton.setStyle("-fx-font-size: 14px; -fx-text-fill: #2c3e50;");
                optionsBox.getChildren().add(radioButton);
            }

            Button nextButton = createStyledButton(currentQuestionIndex == questions.size() - 1 ? "Submit" : "Next", "#3498db");
            nextButton.setOnAction(e -> {
                RadioButton selected = (RadioButton) optionsGroup.getSelectedToggle();
                if (selected != null) {
                    String selectedAnswer = selected.getText();
                    userAnswers += selectedAnswer + "\n";

                    if (selectedAnswer.equals(question.getAnswer())) {
                        correctAnswers++;
                    }
                }
                if (currentQuestionIndex == questions.size() - 1) {
                    submitResults(layout);
                } else {
                    currentQuestionIndex++;
                    showQuestion(layout);
                }
            });

            layout.getChildren().addAll(questionLabel, optionsBox, nextButton);
        }
    }

    private void submitResults(VBox layout) {
        double score = ((double) correctAnswers / questions.size()) * 100;
        boolean passed = score >= 80.0;

        layout.getChildren().clear();

        Label resultLabel = new Label("You scored: " + score + "%");
        resultLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label answersLabel = new Label("Correct Answers: " + correctAnswers + "/" + questions.size());
        answersLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #2c3e50;");

        Label passFailLabel = new Label(passed ? "You passed!" : "You failed. Better luck next time!");
        passFailLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + (passed ? "#2ecc71" : "#e74c3c"));

        layout.getChildren().addAll(resultLabel, answersLabel, passFailLabel);

        if (!passed) {
            Button courseRecommendationsButton = createStyledButton("View Course Recommendations", "#27ae60");
            courseRecommendationsButton.setOnAction(e -> {
                List<Course> courses = geminiApiTest.fetchCourses(job.getRequirements());
                showCourseRecommendations(layout, courses);
            });
            layout.getChildren().add(courseRecommendationsButton);
        } else {
            Label coverLetterLabel = new Label("Enter Cover Letter:");
            coverLetterLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #2c3e50;");

            TextArea coverLetterArea = new TextArea();
            coverLetterArea.setPromptText("Write your cover letter here...");
            coverLetterArea.setWrapText(true);
            coverLetterArea.setPrefRowCount(5);

            Button applyButton = createStyledButton("Submit Job Application", "#27ae60");
            applyButton.setOnAction(e -> {
                String coverLetter = coverLetterArea.getText().trim();
                if (coverLetter.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Cover Letter Missing");
                    alert.setHeaderText(null);
                    alert.setContentText("Please enter a cover letter before submitting your application.");
                    alert.showAndWait();
                    return;
                }

                JobConnect jobConnect = JobConnect.getInstance();
                int userId = jobConnect.getSessionUser().getUserId();
                Application application = new Application(job.getJobId(), userId, "Pending", coverLetter);
                boolean success = jobConnect.applyForJob(application);

                Alert alert = new Alert(success ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
                alert.setTitle(success ? "Application Submitted" : "Application Failed");
                alert.setHeaderText(null);
                alert.setContentText(success ? "Your application for the job has been successfully submitted!" : "Failed to submit the application. Please try again.");
                alert.showAndWait();

                if (success) {
                    resetQuizState();
                    dashboardRoot.setCenter(jobApplicationPage.getView());
                }
            });

            layout.getChildren().addAll(coverLetterLabel, coverLetterArea, applyButton);
        }
    }

    private void showCourseRecommendations(VBox layout, List<Course> courses) {
        layout.getChildren().clear();

        Label title = new Label("Recommended Courses:");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #34495e;");
        layout.getChildren().add(title);

        for (Course course : courses) {
            Label courseLabel = new Label(course.getCourseName() + " by " + course.getInstructor());
            courseLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2c3e50;");

            Hyperlink courseLink = new Hyperlink(course.getLink());
            courseLink.setStyle("-fx-font-size: 14px;");
            courseLink.setOnAction(e -> openLinkInBrowser(course.getLink()));

            VBox courseBox = new VBox(5, courseLabel, courseLink);
            courseBox.setPadding(new Insets(10));
            courseBox.setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #bdc3c7; -fx-border-radius: 5; -fx-background-radius: 5;");

            layout.getChildren().add(courseBox);
        }
    }

    private void openLinkInBrowser(String link) {
        try {
            Desktop.getDesktop().browse(new URI(link));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void fetchQuestionsAsync(String jobRequirement) {
        new Thread(() -> {
            try {
                List<Question> fetchedQuestions = geminiApiTest.fetchQuestions(jobRequirement);
                Platform.runLater(() -> {
                    questions = fetchedQuestions != null && !fetchedQuestions.isEmpty() ? fetchedQuestions : generateDefaultQuestions();
                    dashboardRoot.setCenter(getView());
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    questions = generateDefaultQuestions();
                    dashboardRoot.setCenter(getView());
                });
            }
        }).start();
    }

    private List<Question> generateDefaultQuestions() {
        List<Question> defaultQuestions = new ArrayList<>();
        defaultQuestions.add(new Question("What is the capital of France?", List.of("Paris", "London", "Berlin", "Madrid"), "Paris"));
        defaultQuestions.add(new Question("What is the largest mammal?", List.of("Elephant", "Blue Whale", "Giraffe", "Hippopotamus"), "Blue Whale"));
        defaultQuestions.add(new Question("Which planet is known as the Red Planet?", List.of("Earth", "Mars", "Venus", "Jupiter"), "Mars"));
        defaultQuestions.add(new Question("What is the powerhouse of the cell?", List.of("Nucleus", "Mitochondria", "Chloroplast", "Ribosome"), "Mitochondria"));
        return defaultQuestions;
    }

    private void resetQuizState() {
        currentQuestionIndex = 0;
        correctAnswers = 0;
        userAnswers = "";
    }

    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: 14px;");
        button.setPadding(new Insets(10, 20, 10, 20));
        return button;
    }
}
