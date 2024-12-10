package Backend.controllers;

import Backend.JobConnect;
import Backend.entities.SupportQuery;
import Backend.persistence.DatabaseHandler;

import java.util.List;
import java.util.Optional;

public class SupportQueryController {
    private final DatabaseHandler databaseHandler;

    public SupportQueryController() {
        this.databaseHandler = DatabaseHandler.getInstance(); // Singleton instance of DatabaseHandler
    }

    /**
     * Adds a new support query to the database.
     * 
     * @param query The SupportQuery object containing userId, message, etc.
     * @return true if the query was added successfully, false otherwise.
     */
    /**
     * Adds a new support query to the database and notifies the user.
     * 
     * @param query The SupportQuery object containing userId, message, etc.
     * @return true if the query was added successfully, false otherwise.
     */
    public boolean addSupportQuery(SupportQuery query) {
        try {
            if (query == null || query.getUserId() <= 0 || query.getQueryText().isEmpty()) {
                System.err.println("Invalid support query: Missing required fields.");
                return false;
            }

            boolean isSaved = databaseHandler.saveSupportQuery(query);
            if (isSaved) {
                sendNotification(query.getUserId(), "Your query has been submitted: " + query.getQueryText());
            }
            return isSaved;
        } catch (Exception e) {
            System.err.println("Error adding support query: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retrieves all support queries.
     * 
     * @return List of all support queries in the database.
     */
    public List<SupportQuery> getAllSupportQueries() {
        try {
            List<SupportQuery> queries = databaseHandler.getAllSupportQueries();
            if (queries.isEmpty()) {
                System.out.println("No support queries found.");
            }
            return queries;
        } catch (Exception e) {
            System.err.println("Error retrieving all support queries: " + e.getMessage());
            return List.of(); // Return an empty list in case of error
        }
    }

    /**
     * Resolves a support query by updating its status to 'Resolved'.
     * 
     * @param queryId The ID of the query to resolve.
     * @return true if the query was successfully resolved, false otherwise.
     */
    public boolean resolveSupportQuery(int queryId) {
        try {
            if (queryId <= 0) {
                System.err.println("Invalid query ID: " + queryId);
                return false;
            }

            SupportQuery query = databaseHandler.getSupportQueryById(queryId).orElse(null);
            if (query == null) {
                System.err.println("Support query not found for ID: " + queryId);
                return false;
            }

            boolean isResolved = databaseHandler.resolveSupportQuery(queryId);
            if (isResolved) {
                sendNotification(query.getUserId(), "Your query has been resolved: " + query.getQueryText());
            }
            return isResolved;
        } catch (Exception e) {
            System.err.println("Error resolving support query with ID " + queryId + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Retrieves all support queries submitted by a specific user.
     * 
     * @param userId The ID of the user whose support queries are to be retrieved.
     * @return List of support queries submitted by the user.
     */
    public List<SupportQuery> getSupportQueriesByUserId(int userId) {
        try {
            if (userId <= 0) {
                System.err.println("Invalid user ID: " + userId);
                return List.of();
            }
            List<SupportQuery> queries = databaseHandler.getSupportQueriesByUserId(userId);
            if (queries.isEmpty()) {
                System.out.println("No support queries found for user ID: " + userId);
            }
            return queries;
        } catch (Exception e) {
            System.err.println("Error retrieving support queries for user ID " + userId + ": " + e.getMessage());
            return List.of(); // Return an empty list in case of error
        }
    }

    /**
     * Retrieves a single support query by its ID.
     * 
     * @param queryId The ID of the support query to retrieve.
     * @return An Optional containing the support query if found, or an empty Optional if not.
     */
    public Optional<SupportQuery> getSupportQueryById(int queryId) {
        try {
            if (queryId <= 0) {
                System.err.println("Invalid query ID: " + queryId);
                return Optional.empty();
            }
            return databaseHandler.getSupportQueryById(queryId);
        } catch (Exception e) {
            System.err.println("Error retrieving support query with ID " + queryId + ": " + e.getMessage());
            return Optional.empty();
        }
    }
    private void sendNotification(int userId, String message) {
        try {

            // Use the JobConnect class to send the notification
            JobConnect jobConnect = JobConnect.getInstance(); // Assuming JobConnect is a singleton
            boolean isNotified = jobConnect.notify(userId, message);

            if (isNotified) {
                System.out.println("Notification successfully sent to user " + userId + ": " + message);
            } else {
                System.err.println("Failed to send notification to user " + userId + ".");
            }
        } catch (Exception e) {
            System.err.println("Error sending notification to user " + userId + ": " + e.getMessage());
        }
    }
}
