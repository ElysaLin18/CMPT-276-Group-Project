package com.github.elysalin18.cmpt276groupproject.donatedesk.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.github.elysalin18.cmpt276groupproject.donatedesk.models.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import com.github.elysalin18.cmpt276groupproject.donatedesk.models.PasswordValidator;
import com.github.elysalin18.cmpt276groupproject.donatedesk.models.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class UserController {
    @Autowired
    private UserRepository userRepo;

    private String getLanding(String role) {
        return "redirect:/users/" + role;
    }
    
    @GetMapping("/users/add")
    public String getSignup(Model model) {
        model.addAttribute("user", null);
        return "signup";
    }

    @PostMapping("/users/add")
    public String addUser(@RequestParam Map<String,String> formData, Model model, HttpServletRequest request) {     
        if (formData == null) {
            return "signup";
        }

        String name = formData.get("name");
        String password = formData.get("password");
        String role = formData.get("role");

        if (name.isEmpty() || password.isEmpty()) {
            return "signup";
        }
        else if (PasswordValidator.isInvalid(password)) {
            model.addAttribute("addUserError", "Password is insecure must be at least 8 characters, include an uppercase letter and a special character");
            return "signup";
        }
        else if (userRepo.existsByNameAndPassword(name, password)) {
            model.addAttribute("addUserError", "This user already exists, try logging in instead");
            return "signup";
        }

        User user = new User(name, password, role);
        userRepo.save(user);
        
        request.getSession().setAttribute("session_user", user);
        return getLanding(role);
    }

    @GetMapping("/users/login")
    public String getLogin(Model model, HttpServletRequest request, HttpSession session){
        User user = (User) session.getAttribute("session_user");
        if (user == null){
            return "login";
        }
        else {
            return getLanding(user.getRole());
        }
    }

    @PostMapping("/users/login")
    public String login(@RequestParam Map<String,String> formData, Model model, HttpServletRequest request, HttpSession session){
        // processing login
        String name = formData.get("name");
        String pwd = formData.get("password");
        List<User> userlist = userRepo.findByNameAndPassword(name, pwd);
        if (userlist.isEmpty()){
            model.addAttribute("userMissing", true);
            return "login";
        }
        else {
            // success
            User user = userlist.get(0);
            request.getSession().setAttribute("session_user", user);
            return getLanding(user.getRole());
        }
    }

    @GetMapping("/users/logout")
    public String destroySession(HttpServletRequest request) {
        request.getSession().invalidate();
        return "redirect:/users/login";
    }

    @GetMapping({"/users/admin","/users/assistant"})
    public String getOffice(Model model, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("session_user");
        if (user == null) {
            return "redirect:/users/login";
        } 
        else if (user.getRole().equals("maintainer")) {
            return getLanding(user.getRole());
        }

        if (user.getRole().equals("admin")) {
            List<User> users = userRepo.findAll();
            model.addAttribute("userList", users);
        }
        
        model.addAttribute("user", user);
        return "office";
    }

    @GetMapping("/users/maintainer")
    public String getMaintenance(Model model, HttpSession session) {
        User user = (User) session.getAttribute("session_user");
        if (user == null) {
            return "redirect:/users/login";
        }
        else if (!user.getRole().equals("maintainer")) {
            return getLanding(user.getRole());
        }

        return "maintenance";
    }
}
