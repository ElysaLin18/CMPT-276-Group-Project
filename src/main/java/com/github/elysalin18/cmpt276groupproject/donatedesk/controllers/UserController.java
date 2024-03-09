package com.github.elysalin18.cmpt276groupproject.donatedesk.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.github.elysalin18.cmpt276groupproject.donatedesk.models.UserRepository;
import com.github.elysalin18.cmpt276groupproject.donatedesk.models.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;


@Controller
public class UserController {
    @Autowired
    private UserRepository userRepo;

    @PostMapping("/users/add")
    public RedirectView getMethodName(@RequestParam Map<String,String> form) {
        String newName = form.get("name");

        if (userRepo.existsByName(newName)) {
            return new RedirectView("/signup.html");
        }

        String newPw = form.get("password");
        String newRole = form.get("role");

        User newUser = new User(newName, newPw, newRole);
        userRepo.save(newUser);
        
        // Redirects to "/users/login/"
        return new RedirectView("login/");
    }
}
