package Backend.models;

import Backend.interfaces.UserInterface;

public abstract class User implements UserInterface {
    protected int userId;
    protected String name;
    protected String email;
    protected String password;
    protected String role;

    public User(int userId2, String name, String email, String password, String role) {
        this.userId = userId2;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    @Override
    public int getUserId() {
        return userId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getRole() {
        return role;
    }

    @Override
    public void updateProfile(String name, String email) {
        this.name = name;
        this.email = email;
        System.out.println("Profile updated: " + this.name + ", " + this.email);
    }

    @Override
    public void viewProfile() {
        System.out.println("Profile Information:");
        System.out.println("Name: " + this.name);
        System.out.println("Email: " + this.email);
        System.out.println("Role: " + this.role);
    }
}
