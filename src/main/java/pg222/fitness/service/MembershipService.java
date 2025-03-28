package pg222.fitness.service;

import pg222.fitness.model.Membership;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class MembershipService {
    @Autowired
    private FileService fileService;

    public Membership getMembership(String username) throws IOException {
        List<String> lines = fileService.readFile("memberships.txt");
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts[0].equals(username)) {
                return new Membership(parts[0], parts[1], parts[2], Integer.parseInt(parts[3]));
            }
        }
        return null;
    }

    public void updateMembership(Membership membership) throws IOException {
        List<String> lines = fileService.readFile("memberships.txt");
        List<String> updatedLines = new ArrayList<>();
        boolean found = false;
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts[0].equals(membership.getUsername())) {
                updatedLines.add(membership.toString());
                found = true;
            } else {
                updatedLines.add(line);
            }
        }
        if (!found) {
            updatedLines.add(membership.toString());
        }
        fileService.writeFile("memberships.txt", updatedLines);
    }

    public List<Membership> getAllMembershipsSortedByExpiryDate() throws IOException {
        List<Membership> memberships = new ArrayList<>();
        List<String> lines = fileService.readFile("memberships.txt");
        for (String line : lines) {
            String[] parts = line.split(",");
            memberships.add(new Membership(parts[0], parts[1], parts[2], Integer.parseInt(parts[3])));
        }
        insertionSortByExpiryDate(memberships);
        return memberships;
    }

    private void insertionSortByExpiryDate(List<Membership> memberships) {
        for (int i = 1; i < memberships.size(); i++) {
            Membership key = memberships.get(i);
            int j = i - 1;
            while (j >= 0 && LocalDate.parse(memberships.get(j).getExpiryDate())
                    .isAfter(LocalDate.parse(key.getExpiryDate()))) {
                memberships.set(j + 1, memberships.get(j));
                j--;
            }
            memberships.set(j + 1, key);
        }
    }
}