package Backend.controllers;

import Backend.JobConnect;
import Backend.entities.Application;
import Backend.entities.Job;
import Backend.persistence.DatabaseHandler;

import java.util.List;
import java.util.Optional;

public class ApplicationController {
    private final DatabaseHandler databaseHandler;

    public ApplicationController() {
        this.databaseHandler = DatabaseHandler.getInstance();
    }

    /**
     * Applies for a job by saving the application in the database.
     */
    public boolean applyForJob(Application application) {
        return databaseHandler.saveApplication(application);
    }

    /**
     * Updates the status of an application.
     */
    public boolean updateApplicationStatus(int applicationId, String newStatus) {
        boolean success = databaseHandler.updateApplicationStatus(applicationId, newStatus);

        if (success) {
            // Fetch application details to notify the applicant
            Application application = databaseHandler.getApplicationById(applicationId);
            int userId = application.getUserId();

            // Notify the user about the status change
            String message = "Your application for the job '" + getJobTitle(applicationId) +
                             "' has been updated to: " + newStatus + ".";
            JobConnect.getInstance().notify(userId, message);
        }

        return success;
    }

    private String getJobTitle(int applicationId) {
        // Retrieve the JobId associated with the given ApplicationId
        int jobId = databaseHandler.getJobIdByApplicationId(applicationId);

        // If no JobId is found, return null or handle the case as needed
        if (jobId == -1) {
            System.err.println("No JobId found for ApplicationId: " + applicationId);
            return null;
        }

        // Retrieve the Job using the JobId
        Optional<Job> optionalJob = databaseHandler.getJobById(jobId);

        // If the Job is not present, log the error and return null
        if (optionalJob.isEmpty()) {
            System.err.println("No Job found for JobId: " + jobId);
            return null;
        }

        // Extract and return the Job Title
        return optionalJob.get().getTitle();
    }



	/**
     * Retrieves all applications for a specific user.
     */
    public List<Application> getApplicationsForUser(int userId) {
        return databaseHandler.getApplicationsForUser(userId);
    }

    /**
     * Retrieves all applications for a specific job.
     */
    public List<Application> getApplicationsForJob(int jobId) {
        return databaseHandler.getApplicationsForJob(jobId);
    }

	public List<Application> getApplicationsForPoster(int userId) {
		return databaseHandler.getApplicationsForPoster(userId);
	}
}
