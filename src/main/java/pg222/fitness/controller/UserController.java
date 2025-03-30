package pg222.fitness.controller;

import pg222.fitness.model.User;
import pg222.fitness.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@Controller
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String username, @RequestParam String password,
                            HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.login(username, password);
            if (user != null) {
                session.setAttribute("user", user);
                if ("admin".equals(user.getRole())) {
                    if ("pending".equals(user.getStatus())) {
                        return "waiting-approval"; // Redirect to waiting page if pending
                    }
                    return "redirect:/admin/dashboard";
                } else {
                    return "redirect:/member/dashboard";
                }
            } else {
                redirectAttributes.addFlashAttribute("error", "Invalid credentials");
                return "redirect:/api/users/login?error";
            }
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Error during login");
            return "redirect:/api/users/login?error";
        }
    }

    @GetMapping("/register")
    public String showRegister() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username, @RequestParam String password,
                           @RequestParam String email, @RequestParam String role, RedirectAttributes redirectAttributes) {
        try {
            User user = new User(username, password, role, email);
            boolean registered = userService.registerUser(user);
            if (registered) {
                return "redirect:/api/users/login";
            } else {
                redirectAttributes.addFlashAttribute("error", "Username or email already exists");
                return "redirect:/api/users/register?error";
            }
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Error during registration");
            return "redirect:/api/users/register?error";
        }
    }

    @GetMapping("/update")
    public String showUpdate() {
        return "update-details";
    }

    @PostMapping("/update")
    public String updateUser(@RequestParam String email, @RequestParam String password,
                             HttpSession session) throws IOException {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/api/users/login";
        }
        userService.updateUser(user.getUsername(), email, password);
        return "redirect:/api/users/logout";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/api/users/login";
    }
}