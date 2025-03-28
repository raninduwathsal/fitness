package pg222.fitness.controller;

import pg222.fitness.model.RenewalRequest;
import pg222.fitness.model.User;
import pg222.fitness.service.MembershipService;
import pg222.fitness.service.RequestService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Queue;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private RequestService requestService;
    @Autowired
    private MembershipService membershipService;

    @GetMapping("/dashboard")
    public String adminDashboard(Model model, HttpSession session) throws IOException {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().equals("admin")) return "redirect:/api/users/login";

        Queue<RenewalRequest> pendingRequests = requestService.getPendingRequests();
        model.addAttribute("requests", pendingRequests);
        model.addAttribute("members", membershipService.getAllMembershipsSortedByExpiryDate());

        return "admin-dashboard";
    }

    @PostMapping("/approve")
    public String approveRequest(@RequestParam int requestId) throws IOException {
        requestService.approveRequest(requestId);
        return "redirect:/admin/dashboard";
    }
}