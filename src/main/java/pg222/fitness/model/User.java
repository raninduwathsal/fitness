package pg222.fitness.model;

public class User {
    private String username;
    private String password;
    private String role;
    private String email;
    private String status; // New field for status (active/pending)

    public User(String username, String password, String role, String email) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.email = email;
        this.status = "active"; // Default to active for members and existing admins
    }

    public User(String username, String password, String role, String email, String status) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.email = email;
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public String getEmail() {
        return email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return username + "," + password + "," + role + "," + email + "," + status;
    }
}