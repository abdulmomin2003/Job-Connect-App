package Backend.models;

import Backend.entities.Job;
import Backend.persistence.DatabaseHandler;

import java.util.ArrayList;
import java.util.List;

public class Admin extends User {
    private List<Job> monitoredJobs;
    public Admin( String name, String email, String password) {
    	 super(DatabaseHandler.getInstance().getMaxUserId() + 1, name, email, password, "admin");
        this.monitoredJobs = new ArrayList<>();
    }
    public Admin(int id, String name, String email, String password) {
   	 super(id, name, email, password, "admin");
       this.monitoredJobs = new ArrayList<>();
   }

    public void monitorJob(Job job) {
        monitoredJobs.add(job);
        System.out.println("Job added to monitoring: " + job.getTitle());
    }

    public void stopMonitoringJob(int jobId) {
        monitoredJobs.removeIf(job -> job.getJobId()==jobId);
        System.out.println("Stopped monitoring job: " + jobId);
    }

    public List<Job> getMonitoredJobs() {
        return monitoredJobs;
    }
}
