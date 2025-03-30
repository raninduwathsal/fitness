package pg222.fitness.model;

public class Admin extends User {
    public Admin(String username, String password, String email) {
        super(username, password, "admin", email);
    }

    public Admin(String username, String password, String email, String status) {
        super(username, password, "admin", email, status);
    }
}