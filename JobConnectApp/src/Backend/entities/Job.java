package Backend.entities;

import java.util.List;

public class Job {
    private int jobId; 
    private String title;
    private String description;
    private String salary;
    private int posterid;
    private List<String> requirements;

    public Job(int jobId, String title, String description, String salary,int posterid, List<String> requirements) {
        this.jobId = jobId;
        this.title = title;
        this.description = description;
        this.salary = salary;
        this.posterid=posterid;
        this.requirements = requirements;
    }

    // Getters and Setters
    public int getJobId() { return jobId; }
    public void setJobId(int jobId) { this.jobId = jobId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSalary() { return salary; }
    public void setSalary(String salary) { this.salary = salary; }

    public List<String> getRequirements() { return requirements; }
    public void setRequirements(List<String> requirements) { this.requirements = requirements; }

	public int getPosterId() {
		return this.posterid;
	}
}
