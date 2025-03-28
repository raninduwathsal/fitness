package pg222.fitness.service;

import pg222.fitness.model.Membership;
import pg222.fitness.model.RenewalRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Service
public class RequestService {
    @Autowired
    private FileService fileService;
    @Autowired
    private MembershipService membershipService;

    private Queue<RenewalRequest> requestQueue = new LinkedList<>();

    // Constructor is now empty
    public RequestService() {
    }

    // Initialization logic moved to @PostConstruct
    @PostConstruct
    public void init() {
        try {
            loadRequestsIntoQueue();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadRequestsIntoQueue() throws IOException {
        List<String> lines = fileService.readFile("requests.txt");
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts[3].equals("pending")) {
                RenewalRequest request = new RenewalRequest(
                        Integer.parseInt(parts[0]), parts[1], parts[2], parts[3], Integer.parseInt(parts[4]));
                requestQueue.offer(request);
            }
        }
    }

    public void createRequest(String username, int tierId) throws IOException {
        List<String> lines = fileService.readFile("requests.txt");
        int requestId = lines.size() + 1;
        String requestDate = LocalDate.now().toString();
        RenewalRequest request = new RenewalRequest(requestId, username, requestDate, "pending", tierId);
        fileService.appendToFile("requests.txt", request.toString());
        requestQueue.offer(request);
    }

    public Queue<RenewalRequest> getPendingRequests() {
        return requestQueue;
    }

    public RenewalRequest processNextRequest() throws IOException {
        RenewalRequest request = requestQueue.poll();
        if (request != null) {
            updateRequestStatus(request.getRequestId(), "processed");
        }
        return request;
    }

    public void approveRequest(int requestId) throws IOException {
        RenewalRequest request = null;
        Queue<RenewalRequest> tempQueue = new LinkedList<>();
        while (!requestQueue.isEmpty()) {
            RenewalRequest r = requestQueue.poll();
            if (r.getRequestId() == requestId) {
                request = r;
                break;
            } else {
                tempQueue.offer(r);
            }
        }
        requestQueue.addAll(tempQueue);

        if (request != null) {
            updateRequestStatus(requestId, "approved");
            LocalDate nextMonth = LocalDate.now().plusMonths(1).withDayOfMonth(1);
            Membership membership = new Membership(
                    request.getUsername(), "active", nextMonth.plusMonths(1).minusDays(1).toString(), request.getTierId());
            membershipService.updateMembership(membership);
        }
    }

    private void updateRequestStatus(int requestId, String status) throws IOException {
        List<String> lines = fileService.readFile("requests.txt");
        List<String> updatedLines = new ArrayList<>();
        for (String line : lines) {
            String[] parts = line.split(",");
            if (Integer.parseInt(parts[0]) == requestId) {
                updatedLines.add(parts[0] + "," + parts[1] + "," + parts[2] + "," + status + "," + parts[4]);
            } else {
                updatedLines.add(line);
            }
        }
        fileService.writeFile("requests.txt", updatedLines);
    }
}