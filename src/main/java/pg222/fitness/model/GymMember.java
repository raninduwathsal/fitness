package pg222.fitness.model;

public class GymMember extends User {
    public GymMember(String username, String password, String email) {
        super(username, password, "member", email);
    }

    public GymMember(String username, String password, String email, String status) {
        super(username, password, "member", email, status);
    }
}