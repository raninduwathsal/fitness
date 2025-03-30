package pg222.fitness.controller;

import pg222.fitness.model.Membership;
import pg222.fitness.model.User;
import pg222.fitness.service.AdminRequestService;
import pg222.fitness.service.MembershipService;
import pg222.fitness.service.RequestService;
import pg222.fitness.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private MembershipService membershipService;

    @Autowired
    private RequestService requestService;

    @Autowired
    private AdminRequestService adminRequestService; // New dependency

    @Autowired
    private UserService userService; // New dependency

    @GetMapping("/dashboard")
    public String adminDashboard(Model model, HttpSession session) throws IOException {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().equals("admin") || "pending".equals(user.getStatus())) {
            return "redirect:/api/users/login";
        }
        List<Membership> memberships = membershipService.getAllMembershipsSortedByExpiryDate();
        List<String> adminRequests = adminRequestService.getPendingAdminRequests();
        model.addAttribute("memberships", memberships);
        model.addAttribute("requests", requestService.getPendingRequests());
        model.addAttribute("adminRequests", adminRequests);
        return "admin-dashboard";
    }

    @PostMapping("/approve-admin")
    public String approveAdmin(@RequestParam String username) throws IOException {
        userService.approveAdmin(username);
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/reject-admin")
    public String rejectAdmin(@RequestParam String username) throws IOException {
        userService.rejectAdmin(username);
        return "redirect:/admin/dashboard";
    }
}