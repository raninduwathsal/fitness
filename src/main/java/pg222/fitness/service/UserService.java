package pg222.fitness.service;

import pg222.fitness.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private FileService fileService;

    @Autowired
    private MembershipService membershipService;

    @Autowired
    private AdminRequestService adminRequestService; // New dependency

    public boolean registerUser(User user) throws IOException {
        List<String> lines = fileService.readFile("users.txt");
        for (String line : lines) {
            if (line == null || line.trim().isEmpty()) {
                continue;
            }
            String[] parts = line.split(",");
            if (parts.length < 4) {
                continue;
            }
            if (parts[0].equals(user.getUsername()) || parts[3].equals(user.getEmail())) {
                return false; // Duplicate username or email
            }
        }

        // If the user is registering as an admin, set status to "pending" and add to admin requests
        if ("admin".equals(user.getRole())) {
            user.setStatus("pending");
            adminRequestService.addAdminRequest(user.getUsername());
        }

        // Add the user to users.txt
        fileService.appendToFile("users.txt", user.toString());

        // Add the user to memberships.txt with inactive status and tier 1 (if a member)
        if ("member".equals(user.getRole())) {
            String usernameToMembership = user.getUsername();
            Membership memberOnRegister = new Membership(usernameToMembership, "inactive", "N/A", 1);
            membershipService.updateMembership(memberOnRegister);
        }
        return true;
    }

    public User login(String username, String password) throws IOException {
        List<String> lines = fileService.readFile("users.txt");
        for (String line : lines) {
            if (line == null || line.trim().isEmpty()) {
                continue;
            }
            String[] parts = line.split(",");
            if (parts.length < 5) { // Now expecting 5 parts including status
                continue;
            }
            if (parts[0].equals(username) && parts[1].equals(password)) {
                String role = parts[2];
                String status = parts[4];
                if ("admin".equals(role)) {
                    return new Admin(parts[0], parts[1], parts[3], status);
                } else {
                    return new GymMember(parts[0], parts[1], parts[3], status);
                }
            }
        }
        return null;
    }

    public void updateUser(String username, String newEmail, String newPassword) throws IOException {
        List<String> lines = fileService.readFile("users.txt");
        List<String> updatedLines = new ArrayList<>();
        for (String line : lines) {
            if (line == null || line.trim().isEmpty()) {
                continue;
            }
            String[] parts = line.split(",");
            if (parts.length < 5) {
                continue;
            }
            if (parts[0].equals(username)) {
                updatedLines.add(parts[0] + "," + newPassword + "," + parts[2] + "," + newEmail + "," + parts[4]);
            } else {
                updatedLines.add(line);
            }
        }
        fileService.writeFile("users.txt", updatedLines);
    }

    public void approveAdmin(String username) throws IOException {
        List<String> lines = fileService.readFile("users.txt");
        List<String> updatedLines = new ArrayList<>();
        for (String line : lines) {
            if (line == null || line.trim().isEmpty()) {
                continue;
            }
            String[] parts = line.split(",");
            if (parts.length < 5) {
                continue;
            }
            if (parts[0].equals(username)) {
                updatedLines.add(parts[0] + "," + parts[1] + "," + parts[2] + "," + parts[3] + ",active");
            } else {
                updatedLines.add(line);
            }
        }
        fileService.writeFile("users.txt", updatedLines);
        adminRequestService.removeAdminRequest(username);
    }

    public void rejectAdmin(String username) throws IOException {
        List<String> lines = fileService.readFile("users.txt");
        List<String> updatedLines = new ArrayList<>();
        for (String line : lines) {
            if (line == null || line.trim().isEmpty()) {
                continue;
            }
            String[] parts = line.split(",");
            if (parts.length < 5) {
                continue;
            }
            if (!parts[0].equals(username)) {
                updatedLines.add(line);
            }
        }
        fileService.writeFile("users.txt", updatedLines);
        adminRequestService.removeAdminRequest(username);
    }
}