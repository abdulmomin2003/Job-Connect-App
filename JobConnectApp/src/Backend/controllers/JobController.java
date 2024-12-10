package Backend.controllers;

import Backend.entities.Job;
import Backend.persistence.DatabaseHandler;

import java.util.List;
import java.util.Optional;

public class JobController {
    private final DatabaseHandler databaseHandler;

    public JobController() {
        this.databaseHandler = DatabaseHandler.getInstance();
    }

    public List<Job> getAllJobs() {
        return databaseHandler.getAllJobs();
    }

    public Optional<Job> getJobById(int jobId) {
        return databaseHandler.getJobById(jobId);
    }

    public boolean addJob(Job job) {
        return databaseHandler.saveJob(job);
    }

    public boolean updateJob(Job job) {
        return databaseHandler.updateJob(job);
    }

    public boolean deleteJob(int jobId) {
        return databaseHandler.deleteJob(jobId);
    }

	public List<Job> getJobsBySeekerId(int userId) {
		return databaseHandler.getJobsBySeekerId(userId);
		
	}

	public List<Job> getAllJobs(int ud) {
		return databaseHandler.getAllJobs(ud);
		
	}
}
