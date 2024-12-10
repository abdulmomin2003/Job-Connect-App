package Backend.models;

import Backend.entities.Application;
import Backend.entities.Job;
import Backend.persistence.DatabaseHandler;

import java.util.ArrayList;
import java.util.List;

public class JobSeeker extends User {
    private List<Job> pastJobs; // List of past jobs (Job objects)
    private List<Application> applications; // List of applications made by the JobSeeker

    // Constructor with auto-generated user ID
    public JobSeeker(String name, String email, String password) {
        super(DatabaseHandler.getInstance().getMaxUserId() + 1, name, email, password, "Job Seeker");

        this.pastJobs = new ArrayList<>();
        this.applications = new ArrayList<>();
    }

    // Constructor with all variables (allows initializing lists and user data)
    public JobSeeker(int userId, String name, String email, String password, List<Job> pastJobs, List<Application> applications) {
        super(userId, name, email, password, "Job Seeker");
        this.pastJobs = pastJobs != null ? pastJobs : new ArrayList<>();
        this.applications = applications != null ? applications : new ArrayList<>();
    }

    public JobSeeker(int userID, String name, String email, String password) {
		// TODO Auto-generated constructor stub
        super(userID, name, email, password, "Job Seeker");
        this.pastJobs = new ArrayList<>();
        this.applications = new ArrayList<>();
	}

	// Getter and Setter for Skills

    // Getter and Setter for Past Jobs
    public List<Job> getPastJobs() {
        return pastJobs;
    }

    public void addPastJob(Job pastJob) {
        if (pastJob == null) {
            System.out.println("Cannot add null past job.");
            return;
        }
        pastJobs.add(pastJob);
        System.out.println("Past job added: " + pastJob.getTitle());
    }

    public void removePastJob(Job pastJob) {
        if (pastJob == null) {
            System.out.println("Cannot remove null past job.");
            return;
        }
        if (pastJobs.contains(pastJob)) {
            pastJobs.remove(pastJob);
            System.out.println("Past job removed: " + pastJob.getTitle());
        } else {
            System.out.println("Past job not found: " + pastJob.getTitle());
        }
    }

    // Getter and Setter for Applications
    public List<Application> getApplications() {
        return applications;
    }

    public void applyForJob(Application application) {
        if (application == null) {
            System.out.println("Cannot apply with null application.");
            return;
        }
        applications.add(application);
        System.out.println("Applied for job with ID: " + application.getJobId());
    }

    // Additional functionality to fetch application history
    public void printApplicationHistory() {
        System.out.println("Application History for Job Seeker: " + getName());
        if (applications.isEmpty()) {
            System.out.println("No applications found.");
        } else {
            for (Application application : applications) {
                System.out.println("Job ID: " + application.getJobId() + ", Status: " + application.getStatus());
            }
        }
    }
}
