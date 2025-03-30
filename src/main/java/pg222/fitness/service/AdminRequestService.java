package pg222.fitness.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdminRequestService {
    @Autowired
    private FileService fileService;

    public void addAdminRequest(String username) throws IOException {
        fileService.appendToFile("admin_requests.txt", username);
    }

    public List<String> getPendingAdminRequests() throws IOException {
        List<String> lines = fileService.readFile("admin_requests.txt");
        List<String> requests = new ArrayList<>();
        for (String line : lines) {
            if (line != null && !line.trim().isEmpty()) {
                requests.add(line.trim());
            }
        }
        return requests;
    }

    public void removeAdminRequest(String username) throws IOException {
        List<String> lines = fileService.readFile("admin_requests.txt");
        List<String> updatedLines = new ArrayList<>();
        for (String line : lines) {
            if (line == null || line.trim().isEmpty() || !line.trim().equals(username)) {
                updatedLines.add(line);
            }
        }
        fileService.writeFile("admin_requests.txt", updatedLines);
    }
}