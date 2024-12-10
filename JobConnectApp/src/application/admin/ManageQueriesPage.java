package application.admin;

import Backend.JobConnect;
import Backend.entities.SupportQuery;
import Backend.models.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;

import java.util.List;

public class ManageQueriesPage {
    private final BorderPane root;

    public ManageQueriesPage(BorderPane root) {
        this.root = root;
    }

    public VBox getView() {
        // Use JobConnect to access the list of support queries
        List<SupportQuery> queries = JobConnect.getInstance().getAllSupportQueries();

        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_CENTER);

        Label titleLabel = new Label("Manage Support Queries");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

        ScrollPane queriesPane = createQueriesPane(queries);

        layout.getChildren().addAll(titleLabel, queriesPane);

        return layout;
    }

    private ScrollPane createQueriesPane(List<SupportQuery> queries) {
        VBox queryList = new VBox(15);
        queryList.setPadding(new Insets(10));

        for (SupportQuery query : queries) {
            VBox queryBox = createQueryBox(query);
            queryList.getChildren().add(queryBox);
        }

        ScrollPane scrollPane = new ScrollPane(queryList);
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }

    private VBox createQueryBox(SupportQuery query) {
        VBox queryBox = new VBox(10);
        queryBox.setPadding(new Insets(15));
        queryBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #bdc3c7; -fx-border-radius: 10; -fx-background-radius: 10;");

        // Retrieve user information based on userId using Optional
        String userName = JobConnect.getInstance()
                                    .getUserById(query.getUserId())
                                    .map(User::getName) // Extract the user's name if present
                                    .orElse("Unknown User"); // Default if user is not found

        Label userLabel = new Label("Submitted by: " + userName + " (User ID: " + query.getUserId() + ")");
        userLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2c3e50;");

        Label queryText = new Label("Query: " + query.getQueryText());
        queryText.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495e;");

        Label queryStatus = new Label("Resolved: " + query.isResolved());
        queryStatus.setStyle("-fx-font-size: 12px; -fx-text-fill: " + (query.isResolved() ? "green" : "red") + ";");

        Button resolveButton = new Button("Mark as Resolved");
        resolveButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        
        // Disable the button if the query is already resolved
        resolveButton.setDisable(query.isResolved());

        resolveButton.setOnAction(e -> {
            // Use JobConnect to resolve the query
            boolean success = JobConnect.getInstance().resolveSupportQuery(query.getQueryId());
            if (success) {
                queryStatus.setText("Resolved: true");
                queryStatus.setStyle("-fx-font-size: 12px; -fx-text-fill: green;");
                resolveButton.setDisable(true); // Disable the button after resolving
            }
        });

        queryBox.getChildren().addAll(userLabel, queryText, queryStatus, resolveButton);
        return queryBox;
    }

}
