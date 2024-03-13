package com.github.elysalin18.cmpt276groupproject.donatedesk.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.github.elysalin18.cmpt276groupproject.donatedesk.models.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

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
        /*if (role == "maintainer") {
            return "maintainer";
        }
        
        return "office";*/
    }

    @GetMapping("/users/login")
    public String getLogin(Model model, HttpSession session) {
        User user = (User) session.getAttribute("session_user");

        if (user == null) {
            return "login";
        }
        /*
        else if (user.getRole() == "admin") {
            List<User> users = userRepo.findAll();
            users.remove(user);
            model.addAttribute("userList", users);
            model.addAttribute("user", user);
            return "office";
        }
        else if (user.getRole() == "assistant") {
            model.addAttribute("user", user);
            return "office";
        }

        return "maintenance";
        */
        return getLanding(user.getRole());
    }
    
    @PostMapping("/users/login")
    public String login(@RequestParam Map<String,String> formData, Model model, HttpServletRequest request, HttpSession session) {
        String name = formData.get("name");
        String password = formData.get("password");
        User user = userRepo.findByNameAndPassword(name, password);
        
        if (user == null) {
            model.addAttribute("userMissing", true);
            return "login";
        }

        request.getSession().setAttribute("session_user", user);
        
        /*
        if (user.getRole().equals("admin")) {
            List<User> users = userRepo.findAll();
            users.remove(user);
            model.addAttribute("userList", users);
            model.addAttribute("user", user);
            return "office";
        }
        else if (user.getRole().equals("assistant")) {
            model.addAttribute("user", user);
            return "office";
        }
        else {
            return "maintenance";
        }
        */

        //model.addAttribute("user", user);
        return getLanding(user.getRole());
    }
    
    
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
        return getLanding(role);
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
            users.remove(user);
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
