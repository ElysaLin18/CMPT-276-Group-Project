package com.github.elysalin18.cmpt276groupproject.donatedesk.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.github.elysalin18.cmpt276groupproject.donatedesk.models.InboxMeta;
import com.github.elysalin18.cmpt276groupproject.donatedesk.models.EmailMessage;
import com.github.elysalin18.cmpt276groupproject.donatedesk.models.RestClientInterceptor;
import com.github.elysalin18.cmpt276groupproject.donatedesk.models.Token;
import com.github.elysalin18.cmpt276groupproject.donatedesk.models.User;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;



@Controller
public class EmailController {

    private Token getToken(RestClient client, String address, String password) {
        Map<String, String> account = Map.of("address", address, "password", password);
        try {
            Token token = client.post().uri("/token").accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).body(account).retrieve().body(Token.class);
            return token;
        }
        catch (RestClientResponseException e) {
            return null;
        }
    }

    private List<EmailMessage> getEmailList(RestClient client, Token token, String approvedSender, String startDate, String endDate) {
        try {
            List<InboxMeta> inbox = client.get().uri("/messages?page=1").accept(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + token).retrieve().body(new ParameterizedTypeReference<List<InboxMeta>>() {});
        
            int pageCount = inbox.size();
            for (int i=2;pageCount == 30; i++) {
                List<InboxMeta> extraInbox = client.get().uri("/messages?page=" + i).accept(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + token).retrieve().body(new ParameterizedTypeReference<List<InboxMeta>>() {});
                inbox.addAll(extraInbox);
                pageCount = extraInbox.size();
            }
    
            List<EmailMessage> emailList = new ArrayList<EmailMessage>() {};
            for (InboxMeta meta : inbox) {
                if (!meta.getIsDeleted() && (approvedSender == "" || meta.getFromAddress().equals(approvedSender)) && (startDate == "" || endDate == "" || meta.dateInequality(startDate, endDate))) {
                    EmailMessage email = client.get().uri("/messages/" + meta.getId()).accept(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + token).retrieve().body(EmailMessage.class);
                    emailList.add(email);
                }
            }
            
            return emailList;
        }
        catch (RestClientResponseException e) {
            return null;
        }
    }

    @GetMapping("/email")
    public String getEmail(HttpSession session, Model model) {
        
        User user = (User) session.getAttribute("session_user"); 
        if (user == null || user.getRole().equals("maintainer")) {
            return "redirect:/users/login";
        }

        Token token = (Token) session.getAttribute("session_token");
        if (token == null) {
            return "email";
        }

        model.addAttribute("isEmailLinked", true);
        return "email";
    }

    @PostMapping("/email/extract")
    public String extractEmail(@RequestParam Map<String,String> emailFilter, HttpSession session, Model model) {
        User user = (User) session.getAttribute("session_user");
        if (user == null || user.getRole().equals("maintainer")) {
            return "redirect:/users/login";
        }
        
        Token token = (Token) session.getAttribute("session_token");
        if (token == null) {
            return "email";
        }
        
        RestClient client = RestClient.builder().baseUrl("https://api.mail.tm").requestInterceptor(new RestClientInterceptor()).build();
        List<EmailMessage> emailList = getEmailList(client, token, emailFilter.get("approvedSender"), emailFilter.get("startDate"), emailFilter.get("endDate"));
        
        if (emailList == null) {
            return "email";
        }

        for (EmailMessage message : emailList) {
            System.out.println(message.getText());
        }

        // Parse text
        // Create excel

        model.addAttribute("isEmailLinked", true);
        return "email";
    }

    @PostMapping("/email/link")
    public String linkEmail(@RequestParam Map<String,String> emailInfo, HttpSession session, Model model, HttpServletResponse response) {
        User user = (User) session.getAttribute("session_user");
        if (user == null || user.getRole().equals("maintainer")) {
            return "redirect:/users/login";
        }
        else if (emailInfo == null) {
            return "email";
        }

        RestClient client = RestClient.builder().baseUrl("https://api.mail.tm").requestInterceptor(new RestClientInterceptor()).build();
        Token token = getToken(client, emailInfo.get("address"), emailInfo.get("password"));
        session.setAttribute("session_token", token);


        model.addAttribute("isEmailLinked", true);
        return "email";
    }

    @GetMapping("/email/unlink")
    public String unlinkEmail(HttpSession session) {
        User user = (User) session.getAttribute("session_user");
        if (user == null || user.getRole().equals("maintainer")) {
            return "redirect:/users/login";
        }
        
        session.removeAttribute("session_token");
        return "email";
    }
}