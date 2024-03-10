package com.github.elysalin18.cmpt276groupproject.donatedesk.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.github.elysalin18.cmpt276groupproject.donatedesk.models.UserRepository;

import jakarta.servlet.http.HttpServletRequest;

import com.github.elysalin18.cmpt276groupproject.donatedesk.models.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;


@Controller
public class UserController {
    @Autowired
    private UserRepository userRepo;

    @PostMapping("/users/add")
    public String getMethodName(@RequestParam Map<String,String> form, HttpServletRequest request) {
        String newName = form.get("name");
        String newPw = form.get("password");
        String newRole = form.get("role");

        User newUser = new User(newName, newPw, newRole);
        userRepo.save(newUser);
        
        request.getSession().setAttribute("session_user", newUser);
        return "redirect:/users/login";
    }

    @GetMapping("/users/logout")
    public String destroySession(HttpServletRequest request) {
        request.getSession().invalidate();
        return "redirect:/users/login";
    }
}
