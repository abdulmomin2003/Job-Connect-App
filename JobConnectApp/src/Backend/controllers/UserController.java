package Backend.controllers;

import Backend.models.User;
import Backend.persistence.DatabaseHandler;
import Backend.persistence.SessionManager;

import java.util.List;
import java.util.Optional;

public class UserController {
    private final DatabaseHandler databaseHandler;
    private final SessionManager sessionManager;

    public UserController() {
        this.databaseHandler = DatabaseHandler.getInstance();
        this.sessionManager = SessionManager.getInstance();
    }


    public Optional<User> authenticateUser(String username, String password) {
        Optional<User> user = databaseHandler.getUserByUsernameAndPassword(username, password);

        if (user.isPresent()) {
            // Update session on successful authentication
            setLoggedInUser(user.get());
        } else {
            System.out.println("Authentication failed for username: " + username);
        }

        return user;
    }

    public boolean register(String username, String email, String password, String role) {
        // Check if the username or email already exists
        if (databaseHandler.getUserByEmail(email).isPresent() || databaseHandler.getUserByUsername(username).isPresent()) {
            System.err.println("Registration failed. Username or email already exists.");
            return false;
        }

        // Register the user in the database
        boolean success = databaseHandler.registerUser(username, email, password, role);
        if (success) {
            System.out.println("User registered successfully.");
        } else {
            System.err.println("Registration failed due to a database error.");
        }
        return success;
    }
    
    public boolean register(User user) {
        return databaseHandler.registerUser(user);
    }
    private void setLoggedInUser(User user) {
        if (user == null || user.getUserId() == 0) {
            throw new IllegalArgumentException("Invalid user object. Cannot set session.");
        }
        sessionManager.setLoggedInUser(user);
    }
    public void logoutUser() {
        sessionManager.clearSession();
    }

    private User getLoggedInUser() {
        return sessionManager.getLoggedInUser();
    }

    public List<User> getAllUsers() {
        return databaseHandler.getAllUsers();
    }

    public boolean deleteUser(int userId) {
        return databaseHandler.deleteUser(userId);
    }

    public boolean updateUser(User user) {
        return databaseHandler.updateUser(user);
    }

	public void updateSession(User currentUser) {
		// TODO Auto-generated method stub
		setLoggedInUser(currentUser);	
	}

	public User getCurrentUser() {
		// TODO Auto-generated method stub
		return getLoggedInUser();
	}

	public Optional<User> getUserById(int userId) {
		// TODO Auto-generated method stub
		return databaseHandler.getUserById(userId);
	}
}
