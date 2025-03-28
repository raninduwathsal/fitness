package pg222.fitness.controller;

import pg222.fitness.model.User;
import pg222.fitness.service.RequestService;
import pg222.fitness.service.TierService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@RequestMapping("/api")
public class TierController {
    @Autowired
    private TierService tierService;
    @Autowired
    private RequestService requestService;

    @GetMapping("/upgrade")
    public String showTiers(Model model) throws IOException {
        model.addAttribute("tiers", tierService.getAllTiers());
        return "tier-upgrade";
    }

    @PostMapping("/upgrade")
    public String requestUpgrade(@RequestParam int tierId, HttpSession session) throws IOException {
        User user = (User) session.getAttribute("user");
        requestService.createRequest(user.getUsername(), tierId);
        return "redirect:/member/dashboard";
    }
}