package Backend.interfaces;

public interface UserInterface {
    int getUserId();
    String getName();
    String getEmail();
    String getPassword();
    String getRole();

    void updateProfile(String name, String email);
    void viewProfile();
}
