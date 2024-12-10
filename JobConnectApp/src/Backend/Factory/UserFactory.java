package Backend.Factory;
import Backend.models.Admin;
import Backend.models.JobPoster;
import Backend.models.JobSeeker;
import Backend.models.User;

public class UserFactory {

    public static User createUser(int userId, String username, String email, String password, String role) {
        switch (role.toLowerCase()) {
            case "admin":
                return new Admin(userId, username, email, password);
            case "job seeker":
                return new JobSeeker(userId, username, email, password);
            case "job poster":
                return new JobPoster(userId, username, email, password);
            default:
                throw new IllegalArgumentException("Unknown role: " + role);
        }
    }
}
