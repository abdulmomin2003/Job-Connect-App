package Backend.persistence;

import Backend.models.User;

public class SessionManager {
    private static SessionManager instance;
    private User loggedInUser;

    // Private constructor
    private SessionManager() {
    }

    // Public method to provide the Singleton instance
    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    // Getter and Setter for loggedInUser
    public User getLoggedInUser() {
        if (loggedInUser == null) {
            throw new IllegalStateException("No user is currently logged in.");
        }
        return loggedInUser;
    }

    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
        System.out.println("Session set for UserId: " + user.getUserId());
    }

    // Clear session on logout
    public void clearSession() {
        loggedInUser = null;
        System.out.println("Session cleared.");
    }
    public boolean isLoggedIn() {
        return loggedInUser != null;
    }
    
}
