package Backend.models;

import Backend.entities.Job;
import Backend.persistence.DatabaseHandler;

import java.util.ArrayList;
import java.util.List;

public class JobPoster extends User {
    private List<Job> postedJobs;

    public JobPoster( String name, String email, String password) {
    	 super(DatabaseHandler.getInstance().getMaxUserId() + 1, name, email, password, "Job Poster");
        this.postedJobs = new ArrayList<>();
    }

    public JobPoster(int userID, String name, String email, String password) {
		// TODO Auto-generated constructor stub
   	 super(userID, name, email, password, "Job Poster");
     this.postedJobs = new ArrayList<>();
	}

	public void postJob(Job job) {
        postedJobs.add(job);
        System.out.println("Job posted: " + job.getTitle());
    }

    public void deleteJob(int jobId) {
        postedJobs.removeIf(job -> job.getJobId()==jobId);
        System.out.println("Job deleted: " + jobId);
    }

    public List<Job> getPostedJobs() {
        return postedJobs;
    }
}
