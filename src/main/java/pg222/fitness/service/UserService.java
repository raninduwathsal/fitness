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

    public boolean registerUser(User user) throws IOException {
        List<String> lines = fileService.readFile("users.txt");
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts[0].equals(user.getUsername()) || parts[3].equals(user.getEmail())) {
                return false; // Duplicate username or email
            }
        }
        fileService.appendToFile("users.txt", user.toString());
        return true;
    }

    public User login(String username, String password) throws IOException {
        List<String> lines = fileService.readFile("users.txt");
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts[0].equals(username) && parts[1].equals(password)) {
                return parts[2].equals("member") ?
                        new GymMember(parts[0], parts[1], parts[3]) :
                        new Admin(parts[0], parts[1], parts[3]);
            }
        }
        return null;
    }

    public void updateUser(String username, String newEmail, String newPassword) throws IOException {
        List<String> lines = fileService.readFile("users.txt");
        List<String> updatedLines = new ArrayList<>();
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts[0].equals(username)) {
                updatedLines.add(parts[0] + "," + newPassword + "," + parts[2] + "," + newEmail);
            } else {
                updatedLines.add(line);
            }
        }
        fileService.writeFile("users.txt", updatedLines);
    }
}