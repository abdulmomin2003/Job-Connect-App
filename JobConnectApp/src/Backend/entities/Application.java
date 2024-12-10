package Backend.entities;


import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

public class Application {
    private static AtomicInteger idCounter = new AtomicInteger(1); // Thread-safe counter for auto-incrementing IDs

    private int applicationId; // Unique ID for the application
    private int jobId; // The job this application is associated with
    private int userId; // The user who applied for the job
    private String status; // Status of the application (e.g., "Pending", "Accepted", "Rejected")
    private LocalDateTime applicationDate; // Timestamp for when the application was created
    private String coverLetter; // New field for cover letter
    
    // Constructor for creating new applications
    public Application(int jobId, int userId, String status, String coverLetter) {
        this.jobId = jobId;
        this.userId = userId;
        this.status = status;
        this.applicationDate = LocalDateTime.now();
        this.coverLetter = coverLetter;
    }
    // Parameterized Constructor (for existing applications)
    public Application(int applicationId, int jobId, int userId, String status, LocalDateTime applicationDate2, String coverLetter) {
        this.applicationId = applicationId;
        this.jobId = jobId;
        this.userId = userId;
        this.status = status;
        this.applicationDate = applicationDate2;
        this.coverLetter = coverLetter;
    }

    // Auto-increment ID generator
    private static int generateApplicationId() {
        return idCounter.getAndIncrement();
    }

    // Getters and Setters
    public int getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(LocalDateTime applicationDate) {
        this.applicationDate = applicationDate;
    }
    public String getCoverLetter() {
        return coverLetter;
    }

    public void setCoverLetter(String coverLetter) {
        this.coverLetter = coverLetter;
    }
    // ToString for easy debugging and logging
    @Override
    public String toString() {
        return "Application{" +
                "applicationId=" + applicationId +
                ", jobId=" + jobId +
                ", userId=" + userId +
                ", status='" + status + '\'' +
                ", applicationDate=" + applicationDate +
                '}';
    }
}
