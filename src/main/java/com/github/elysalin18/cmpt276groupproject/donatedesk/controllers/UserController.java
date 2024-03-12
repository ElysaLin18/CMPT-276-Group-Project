package com.github.elysalin18.cmpt276groupproject.donatedesk.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.github.elysalin18.cmpt276groupproject.donatedesk.models.UserRepository;

import jakarta.servlet.http.HttpServletRequest;

import com.github.elysalin18.cmpt276groupproject.donatedesk.models.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {
    @Autowired
    private UserRepository userRepo;

    @GetMapping("/users/add")
    public String getSignup(Model model) {
        model.addAttribute("user", null);
        return "signup";
    }

    @PostMapping("/users/add")
    public String addUser(@RequestParam Map<String,String> formData, Model model, HttpServletRequest request) {     
        String name = formData.get("name");
        String password = formData.get("password");
        String role = formData.get("role");

        if (userRepo.existsByNameAndPassword(name, password)) {
            model.addAttribute("userFound", true);
            return "signup";
        }

        User user = new User(name, password, role);
        userRepo.save(user);
        
        request.getSession().setAttribute("session_user", user);
        return "redirect:/users/login";
    }

    @GetMapping("/users/logout")
    public String destroySession(HttpServletRequest request) {
        request.getSession().invalidate();
        return "redirect:/users/login";
    }
}
