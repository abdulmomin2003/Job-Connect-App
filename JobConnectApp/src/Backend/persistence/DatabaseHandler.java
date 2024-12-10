package Backend.persistence;

import Backend.Factory.UserFactory;
import Backend.entities.Application;
import Backend.entities.Job;
import Backend.entities.Notification;
import Backend.entities.SupportQuery;
import Backend.models.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class DatabaseHandler {
	private static DatabaseHandler instance;
    private static final String DB_URL = "jdbc:sqlserver://MOMINS-COMPUTER\\SQLEXPRESS02:1433;databaseName=JobConnectApp;encrypt=true;trustServerCertificate=true;";
    private static final String DB_USERNAME = "momin";
    private static final String DB_PASSWORD = "12";
    private Connection connection;
    
    private DatabaseHandler() {
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            System.out.println("Database connection successful.");
        } catch (SQLException e) {
            System.err.println("Error connecting to the database: " + e.getMessage());
        }
    }
    public static synchronized DatabaseHandler getInstance() {
        if (instance == null) {
            instance = new DatabaseHandler();
        }
        return instance;
    }

    // ----------- USER MANAGEMENT ------------ //
    private User createUserFromResultSet(ResultSet rs) throws SQLException {
        int userId = rs.getInt("UserId");
        String username = rs.getString("username");
        String email = rs.getString("Email");
        String password = rs.getString("Password");
        String role = rs.getString("Role");

        // Use UserFactory to create the User object
        return UserFactory.createUser(userId, username, email, password, role);
    }
	
	public List<User> getAllUsers() {
	    String query = "SELECT * FROM Users";
	    List<User> users = new ArrayList<>();
	    try (PreparedStatement stmt = connection.prepareStatement(query);
	         ResultSet rs = stmt.executeQuery()) {
	        while (rs.next()) {
	            users.add(createUserFromResultSet(rs));
	        }
	    } catch (SQLException e) {
	        System.err.println("Error fetching all users: " + e.getMessage());
	    }
	    return users;
	}
	
	public boolean deleteUser(int userId) {
	    String query = "DELETE FROM Users WHERE UserId = ?";
	    try (PreparedStatement stmt = connection.prepareStatement(query)) {
	        stmt.setInt(1, userId);
	        return stmt.executeUpdate() > 0;
	    } catch (SQLException e) {
	        System.err.println("Error deleting user: " + e.getMessage());
	        return false;
	    }
	}
	
	public Optional<User> getUserById(int userId) {
	    String query = "SELECT * FROM Users WHERE UserId = ?";
	    try (PreparedStatement stmt = connection.prepareStatement(query)) {
	        stmt.setInt(1, userId);
	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                return Optional.of(createUserFromResultSet(rs));
	            }
	        }
	    } catch (SQLException e) {
	        System.err.println("Error fetching user by ID: " + e.getMessage());
	    }
	    return Optional.empty();
	}
	
	public Optional<User> getUserByEmail(String email) {
	    String query = "SELECT * FROM Users WHERE Email = ?";
	    try (PreparedStatement stmt = connection.prepareStatement(query)) {
	        stmt.setString(1, email);
	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                return Optional.of(createUserFromResultSet(rs));
	            }
	        }
	    } catch (SQLException e) {
	        System.err.println("Error fetching user by email: " + e.getMessage());
	    }
	    return Optional.empty();
	}
	
	public Optional<User> getUserByUsername(String username) {
	    String query = "SELECT * FROM Users WHERE username = ?";
	    try (PreparedStatement stmt = connection.prepareStatement(query)) {
	        stmt.setString(1, username);
	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                return Optional.of(createUserFromResultSet(rs));
	            }
	        }
	    } catch (SQLException e) {
	        System.err.println("Error fetching user by name: " + e.getMessage());
	    }
	    return Optional.empty();
	}
	
	public Optional<User> getUserByUsernameAndPassword(String username, String password) {
	    String query = "SELECT * FROM Users WHERE Username = ? AND Password = ?";
	    try (PreparedStatement stmt = connection.prepareStatement(query)) {
	        stmt.setString(1, username);
	        stmt.setString(2, password);
	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                return Optional.of(createUserFromResultSet(rs));
	            }
	        }
	    } catch (SQLException e) {
	        System.err.println("Error fetching user by username and password: " + e.getMessage());
	    }
	    return Optional.empty();
	}
	
	public boolean registerUser(String name, String email, String password, String role) {
	    String query = "INSERT INTO Users (Name, Email, Password, Role) VALUES (?, ?, ?, ?)";
	    try (PreparedStatement stmt = connection.prepareStatement(query)) {
	        stmt.setString(1, name);
	        stmt.setString(2, email);
	        stmt.setString(3, password);
	        stmt.setString(4, role);
	        return stmt.executeUpdate() > 0;
	    } catch (SQLException e) {
	        System.err.println("Error registering user: " + e.getMessage());
	    }
	    return false;
	}
	
	public boolean registerUser(User user) {
	    String query = "INSERT INTO Users (username, Email, Password, Role) VALUES (?, ?, ?, ?)";
	    try (PreparedStatement stmt = connection.prepareStatement(query)) {
	        stmt.setString(1, user.getName());
	        stmt.setString(2, user.getEmail());
	        stmt.setString(3, user.getPassword());
	        stmt.setString(4, user.getRole());
	        return stmt.executeUpdate() > 0;
	    } catch (SQLException e) {
	        System.err.println("Error registering user: " + e.getMessage());
	    }
	    return false;
	}
	
	public int getMaxUserId() {
	    String query = "SELECT MAX(UserId) FROM Users";
	    try (PreparedStatement stmt = connection.prepareStatement(query);
	         ResultSet rs = stmt.executeQuery()) {
	        if (rs.next()) {
	            return rs.getInt(1);
	        }
	    } catch (SQLException e) {
	        System.err.println("Error fetching max user ID: " + e.getMessage());
	    }
	    return 0; // Return 0 if no users are found
	}
	
	public boolean updateUser(User updatedUser) {
	    String query = "UPDATE Users SET username = ?, Email = ?, Password = ?, Role = ? WHERE UserId = ?";
	    try (PreparedStatement stmt = connection.prepareStatement(query)) {
	        stmt.setString(1, updatedUser.getName());
	        stmt.setString(2, updatedUser.getEmail());
	        stmt.setString(3, updatedUser.getPassword());
	        stmt.setString(4, updatedUser.getRole());
	        stmt.setInt(5, updatedUser.getUserId());
	        boolean result = stmt.executeUpdate() > 0;
	
	        if (result) {
	            System.out.println("User updated successfully.");
	        } else {
	            System.out.println("No user found with the given UserId.");
	        }
	        return result;
	    } catch (SQLException e) {
	        System.err.println("Error updating user: " + e.getMessage());
	        return false;
	    }
	}
	
	    // ----------- JOB MANAGEMENT ------------ //
	    private Job createJobFromResultSet(ResultSet rs) throws SQLException {
	    int jobId = rs.getInt("JobId");
	    String title = rs.getString("Title");
	    String description = rs.getString("Description");
	    String salary = rs.getString("Salary");
	    int postedBy = rs.getInt("PostedBy");
	    List<String> requirements = List.of(rs.getString("Requirements").split(","));
	    return new Job(jobId, title, description, salary, postedBy, requirements);
	}
	
	public List<Job> getAllJobs(int posterId) {
	    String query = "SELECT * FROM Jobs WHERE PostedBy = ?";
	    List<Job> jobs = new ArrayList<>();
	    try (PreparedStatement stmt = connection.prepareStatement(query)) {
	        stmt.setInt(1, posterId);
	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                jobs.add(createJobFromResultSet(rs));
	            }
	        }
	    } catch (SQLException e) {
	        System.err.println("Error fetching all jobs: " + e.getMessage());
	    }
	    return jobs;
	}
	
	public List<Job> getAllJobs() {
	    String query = "SELECT * FROM Jobs";
	    List<Job> jobs = new ArrayList<>();
	    try (PreparedStatement stmt = connection.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
	        while (rs.next()) {
	            jobs.add(createJobFromResultSet(rs));
	        }
	    } catch (SQLException e) {
	        System.err.println("Error fetching all jobs: " + e.getMessage());
	    }
	    return jobs;
	}
	
	public Optional<Job> getJobById(int jobId) {
	    String query = "SELECT * FROM Jobs WHERE JobId = ?";
	    try (PreparedStatement stmt = connection.prepareStatement(query)) {
	        stmt.setInt(1, jobId);
	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                return Optional.of(createJobFromResultSet(rs));
	            }
	        }
	    } catch (SQLException e) {
	        System.err.println("Error fetching job by ID: " + e.getMessage());
	    }
	    return Optional.empty();
	}
	
	public boolean saveJob(Job job) {
	    String query = "INSERT INTO Jobs (Title, Description, Salary, PostedBy, Requirements) VALUES (?, ?, ?, ?, ?)";
	    try (PreparedStatement stmt = connection.prepareStatement(query)) {
	        stmt.setString(1, job.getTitle());
	        stmt.setString(2, job.getDescription());
	        stmt.setString(3, job.getSalary());
	        stmt.setInt(4, job.getPosterId());
	        stmt.setString(5, String.join(",", job.getRequirements()));
	        return stmt.executeUpdate() > 0;
	    } catch (SQLException e) {
	        System.err.println("Error saving job: " + e.getMessage());
	        return false;
	    }
	}
	
	public boolean updateJob(Job updatedJob) {
	    String query = "UPDATE Jobs SET Title = ?, Description = ?, Salary = ?, PostedBy = ?, Requirements = ? WHERE JobId = ?";
	    try (PreparedStatement stmt = connection.prepareStatement(query)) {
	        stmt.setString(1, updatedJob.getTitle());
	        stmt.setString(2, updatedJob.getDescription());
	        stmt.setString(3, updatedJob.getSalary());
	        stmt.setInt(4, updatedJob.getPosterId());
	        stmt.setString(5, String.join(",", updatedJob.getRequirements()));
	        stmt.setInt(6, updatedJob.getJobId());
	        return stmt.executeUpdate() > 0;
	    } catch (SQLException e) {
	        System.err.println("Error updating job: " + e.getMessage());
	        return false;
	    }
	}
	
	public List<Job> getJobsBySeekerId(int seekerId) {
	    String query = "SELECT j.* FROM jobs j LEFT JOIN job_applications ja ON j.JobId = ja.JobId AND ja.UserId = ? WHERE ja.ApplicationId IS NULL";
	    List<Job> jobs = new ArrayList<>();
	    try (PreparedStatement stmt = connection.prepareStatement(query)) {
	        stmt.setInt(1, seekerId);
	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                jobs.add(createJobFromResultSet(rs));
	            }
	        }
	    } catch (SQLException e) {
	        System.err.println("Error fetching jobs by seeker ID: " + e.getMessage());
	    }
	    return jobs;
	}
	
	public boolean deleteJob(int jobId) {
	    String query = "DELETE FROM Jobs WHERE JobId = ?";
	    try (PreparedStatement stmt = connection.prepareStatement(query)) {
	        stmt.setInt(1, jobId);
	        return stmt.executeUpdate() > 0;
	    } catch (SQLException e) {
	        System.err.println("Error deleting job: " + e.getMessage());
	        return false;
	    }
	}

    // ----------- APPLICATION MANAGEMENT ------------ //

    public boolean acceptApplication(int applicationId) {
        String query = "UPDATE job_Applications SET Status = 'Accepted' WHERE ApplicationId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, applicationId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error accepting application: " + e.getMessage());
            return false;
        }
    }

    public boolean rejectApplication(int applicationId) {
        String query = "UPDATE job_Applications SET Status = 'Rejected' WHERE ApplicationId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, applicationId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error rejecting application: " + e.getMessage());
            return false;
        }
    }

    public boolean applyForJob(Application application) {
        String query = "INSERT INTO job_Applications (JobId, UserId, Status, ApplicationDate) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, application.getJobId());
            stmt.setInt(2, application.getUserId());
            stmt.setString(3, application.getStatus());
            stmt.setTimestamp(4, Timestamp.valueOf(application.getApplicationDate()));
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error applying for job: " + e.getMessage());
            return false;
        }
    }

    public List<Application> getApplicationsForJob(int jobId) {
        String query = "SELECT * FROM job_Applications WHERE JobId = ?";
        List<Application> applications = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, jobId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    applications.add(createApplicationFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching applications for job: " + e.getMessage());
        }
        return applications;
    }

    public List<Application> getApplicationsForUser(int userId) {
        String query = "SELECT * FROM job_Applications WHERE UserId = ?";
        List<Application> applications = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    applications.add(createApplicationFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching applications for user: " + e.getMessage());
        }
        return applications;
    }

    public Application getApplicationById(int applicationId) {
        String query = "SELECT * FROM job_Applications WHERE ApplicationId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, applicationId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createApplicationFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching application by ID: " + e.getMessage());
        }
        return null;
    }

    public List<Application> getApplicationsForPoster(int posterId) {
        String query = """
            SELECT a.*
            FROM job_applications a
            JOIN jobs j ON a.JobId = j.JobId
            WHERE j.PostedBy = ?
        """;

        List<Application> applications = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, posterId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    applications.add(createApplicationFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching applications for poster ID: " + e.getMessage());
        }
        return applications;
    }

    public Job getJobByApplicationId(int applicationId) {
        String query = """
            SELECT j.*
            FROM jobs j
            JOIN job_applications a ON j.JobId = a.JobId
            WHERE a.ApplicationId = ?
        """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, applicationId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createJobFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching job by application ID: " + e.getMessage());
        }
        return null;
    }

    public int getJobIdByApplicationId(int applicationId) {
        String query = "SELECT JobId FROM Job_Applications WHERE ApplicationId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, applicationId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("JobId");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching JobId for ApplicationId " + applicationId + ": " + e.getMessage());
        }
        return -1;
    }

    public boolean saveApplication(Application application) {
        String query = "INSERT INTO job_Applications (JobId, UserId, Status, ApplicationDate, CoverLetter) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, application.getJobId());
            stmt.setInt(2, application.getUserId());
            stmt.setString(3, application.getStatus());
            stmt.setTimestamp(4, Timestamp.valueOf(application.getApplicationDate()));
            stmt.setString(5, application.getCoverLetter());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error saving application: " + e.getMessage());
            return false;
        }
    }

    public boolean updateApplicationStatus(int applicationId, String newStatus) {
        String query = "UPDATE job_Applications SET Status = ? WHERE ApplicationId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, newStatus);
            stmt.setInt(2, applicationId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating application status: " + e.getMessage());
            return false;
        }
    }

    private Application createApplicationFromResultSet(ResultSet rs) throws SQLException {
        int applicationId = rs.getInt("ApplicationId");
        int jobId = rs.getInt("JobId");
        int userId = rs.getInt("UserId");
        String status = rs.getString("Status");
        LocalDateTime applicationDate = rs.getTimestamp("ApplicationDate").toLocalDateTime();
        String coverLetter = rs.getString("CoverLetter");

        Application application = new Application(jobId, userId, status, coverLetter);
        application.setApplicationId(applicationId);
        application.setApplicationDate(applicationDate);
        return application;
    }
 // ----------- NOTIFICATION MANAGEMENT ------------ //

    public List<Notification> getNotificationsForUser(int userId) {
        List<Notification> notifications = new ArrayList<>();
        String query = "SELECT * FROM Notifications WHERE userId = ? ORDER BY date DESC";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    notifications.add(new Notification(
                        rs.getInt("notificationId"),
                        rs.getInt("userId"),
                        rs.getString("message"),
                        rs.getTimestamp("date"),
                        rs.getBoolean("isRead")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching notifications: " + e.getMessage());
        }
        return notifications;
    }

    public boolean saveNotification(Notification notification) {
        String query = "INSERT INTO Notifications (userId, message) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, notification.getUserId());
            stmt.setString(2, notification.getMessage());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error saving notification: " + e.getMessage());
            return false;
        }
    }

    public boolean markNotificationAsRead(int notificationId) {
        String query = "UPDATE Notifications SET isRead = 1 WHERE notificationId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, notificationId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error marking notification as read: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteNotification(int notificationId) {
        String query = "DELETE FROM Notifications WHERE NotificationId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, notificationId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting notification: " + e.getMessage());
            return false;
        }
    }

    private int generateNotificationId() {
        String query = "SELECT MAX(NotificationId) FROM Notifications";
        try (PreparedStatement stmt = connection.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) + 1;
            }
        } catch (SQLException e) {
            System.err.println("Error fetching max notification ID: " + e.getMessage());
        }
        return 1; // Return 1 if no notifications are found
    }

    public void populateSampleNotifications() {
        String[] messages = {"Welcome to the platform!", "You have a new job application.", "Your profile has been updated."};
        for (String message : messages) {
            saveNotification(new Notification(generateNotificationId(), 1, message, new Timestamp(System.currentTimeMillis()), false));
        }
    }
 // ---------------- Support Query Management ---------------- //

    public boolean saveSupportQuery(SupportQuery supportQuery) {
        String query = "INSERT INTO SupportQueries (UserId, QueryText, QueryDate, Resolved) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, supportQuery.getUserId());
            stmt.setString(2, supportQuery.getQueryText());
            stmt.setTimestamp(3, new Timestamp(supportQuery.getQueryDate().getTime()));
            stmt.setBoolean(4, supportQuery.isResolved());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error saving support query: " + e.getMessage());
            return false;
        }
    }

    public List<SupportQuery> getAllSupportQueries() {
        List<SupportQuery> supportQueries = new ArrayList<>();
        String query = "SELECT * FROM SupportQueries";
        try (PreparedStatement stmt = connection.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                supportQueries.add(createSupportQueryFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all support queries: " + e.getMessage());
        }
        return supportQueries;
    }

    /**
     * Fetches unresolved support queries.
     */
    public List<SupportQuery> getUnresolvedSupportQueries() {
        List<SupportQuery> supportQueries = new ArrayList<>();
        String query = "SELECT * FROM SupportQueries WHERE Resolved = 0";
        try (PreparedStatement stmt = connection.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                supportQueries.add(createSupportQueryFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching unresolved support queries: " + e.getMessage());
        }
        return supportQueries;
    }

    public boolean resolveSupportQuery(int queryId) {
        String query = "UPDATE SupportQueries SET Resolved = 1 WHERE QueryId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, queryId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error resolving support query: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteSupportQuery(int queryId) {
        String query = "DELETE FROM SupportQueries WHERE QueryId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, queryId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting support query: " + e.getMessage());
            return false;
        }
    }

    public List<SupportQuery> getSupportQueriesByUserId(int userId) {
        List<SupportQuery> supportQueries = new ArrayList<>();
        String query = "SELECT * FROM SupportQueries WHERE UserId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    supportQueries.add(createSupportQueryFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching support queries by user ID: " + e.getMessage());
        }
        return supportQueries;
    }

    public Optional<SupportQuery> getSupportQueryById(int queryId) {
        String query = "SELECT * FROM SupportQueries WHERE QueryId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, queryId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(createSupportQueryFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching support query by ID: " + e.getMessage());
        }
        return Optional.empty();
    }

    private SupportQuery createSupportQueryFromResultSet(ResultSet rs) throws SQLException {
        SupportQuery supportQuery = new SupportQuery(
            rs.getInt("UserId"),
            rs.getString("QueryText")
        );
        supportQuery.setQueryId(rs.getInt("QueryId"));
        supportQuery.setQueryDate(rs.getTimestamp("QueryDate"));
        supportQuery.setResolved(rs.getBoolean("Resolved"));
        return supportQuery;
    }   
}