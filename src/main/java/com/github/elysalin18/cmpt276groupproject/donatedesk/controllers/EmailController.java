package com.github.elysalin18.cmpt276groupproject.donatedesk.controllers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.reactive.function.client.WebClient;

import com.github.elysalin18.cmpt276groupproject.donatedesk.models.InboxMeta;
import com.github.elysalin18.cmpt276groupproject.donatedesk.models.EmailData;
import com.github.elysalin18.cmpt276groupproject.donatedesk.models.EmailMessage;
import com.github.elysalin18.cmpt276groupproject.donatedesk.models.GmailInboxMeta;
import com.github.elysalin18.cmpt276groupproject.donatedesk.models.RestClientInterceptor;
import com.github.elysalin18.cmpt276groupproject.donatedesk.models.Token;
import com.github.elysalin18.cmpt276groupproject.donatedesk.models.User;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class EmailController {

    @Autowired
    private WebClient webClient;

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

    private List<EmailData> getEmailListGmail(String approvedSender, String startDate, String endDate) {

        GmailInboxMeta inboxWrapper;

        if (startDate != "" && endDate != "") {
            try {
                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm");
                Long start = formatter.parse(startDate).getTime();
                Long end = formatter.parse(endDate).getTime();

                String dateFilter = "after:" + start + " before:" + end;

                inboxWrapper = webClient.get().uri("https://gmail.googleapis.com/gmail/v1/users/me/messages?q=in:inbox from:" + approvedSender + " " + dateFilter).retrieve().bodyToMono(GmailInboxMeta.class).block();
            }
            catch (ParseException exception) {
                return null;
            }
        }
        else {
            inboxWrapper = webClient.get().uri("https://gmail.googleapis.com/gmail/v1/users/me/messages?q=in:inbox from:" + approvedSender).retrieve().bodyToMono(GmailInboxMeta.class).block();
        }

        List<InboxMeta> inbox = inboxWrapper.getMessages();
        List<EmailData> emailData = new ArrayList<EmailData>() {};
        for (InboxMeta meta : inbox) {
            String string = webClient.get().uri("https://gmail.googleapis.com/gmail/v1/users/me/messages/" + meta.getId() + "?format=full").retrieve().bodyToMono(String.class).block();
            JSONObject object = new JSONObject(string);
            JSONObject object2 = object.getJSONObject("payload");
            JSONArray array = object2.getJSONArray("headers");

            String date = "";
            String address = "";
            for (int i = 0; i < array.length(); i++) {
                JSONObject object3 = array.getJSONObject(i);
                if (object3.getString("name").equals("Date")) {
                    date = object3.getString("value");
                }
                else if (object3.getString("name").equals("Reply-To")) {
                    address = object3.getString("value");
                }
            }

            JSONArray array2 = object2.getJSONArray("parts");

            String text = "";
            for (int i = 0; i < array2.length(); i++) {
                JSONObject object3 = array2.getJSONObject(i);
                if (object3.getString("mimeType").equals("text/plain")) {
                    JSONObject object4 = object3.getJSONObject("body");
                    String base64url = object4.getString("data");
                    text = new String (Base64.getUrlDecoder().decode(base64url), StandardCharsets.UTF_8);
                }
            }

            String name = "";
            if (text.contains(",") && text.contains("has")) {
                name = text.substring(text.indexOf(",", 0) + 1, text.indexOf("has", 0)).trim();
            }

            String message = "";
            if (text.contains("Message:") && text.contains("Reference")) {
                message = text.substring(text.indexOf("Message:") + "Message:\n".length(), text.lastIndexOf("Reference")).trim();
            }
            emailData.add(new EmailData(name, date, message, address, text));
        }

        return emailData;
    }

    private List<EmailData> getEmailData(List<EmailMessage> emailList) {
        if (emailList == null) {
            return null;
        }

        List<EmailData> emailData = new ArrayList<EmailData>() {};
        for (EmailMessage message : emailList) {
            String message_f = message.getText().substring(message.getText().indexOf("\n"), message.getText().length()).strip();
            String[] words = message_f.split(" ");
            String name = words[0] + " " + words[1];
            String donor_message = "";
            if (message_f.toLowerCase().contains("message")) {
                donor_message = message_f.substring(message_f.toLowerCase().indexOf("message:") + "message:\n".length(), message_f.toLowerCase().indexOf("reference")).trim();
            }
            emailData.add(new EmailData(name, message.getCreatedAt(), donor_message, "", message.getText()));
        }
        return emailData;
    }
    
    private Workbook getEmailExcel(List<EmailData> emailData) {
        Workbook wb = new HSSFWorkbook();
        wb.createName().setNameName("EmailExtract");
        Sheet sheet = wb.createSheet("eTransfer");

        sheet.setDefaultColumnWidth(30);

        for (EmailData data : emailData) {
            Row row = sheet.createRow(sheet.getLastRowNum() + 1);
            row.createCell(0).setCellValue(data.getName());
            row.createCell(1).setCellValue(data.getDate());
            row.createCell(2).setCellValue(data.getMessage());
            row.createCell(3).setCellValue(data.getAddress());
            row.createCell(4).setCellValue(data.getText());
        }

        return wb;
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
    public ResponseEntity<byte[]> extractEmail(@RequestParam Map<String,String> emailFilter, HttpSession session) {
        User user = (User) session.getAttribute("session_user");
        if (user == null || user.getRole().equals("maintainer")) {
            return ResponseEntity.status(401).build();
        }

        Token token = (Token) session.getAttribute("session_token");
        if (token == null) {
            return ResponseEntity.status(401).build();
        }
    
        RestClient client = RestClient.builder().baseUrl("https://api.mail.tm").requestInterceptor(new RestClientInterceptor()).build();
        List<EmailMessage> emailList = getEmailList(client, token, emailFilter.get("approvedSender"), emailFilter.get("startDate"), emailFilter.get("endDate"));
        
        if (emailList == null) {
            return ResponseEntity.noContent().build();
        }

        List<EmailData> emailData = getEmailData(emailList);

        Workbook wb = getEmailExcel(emailData);

        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            wb.write(stream);
            wb.close();
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"email.xls\"").contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")).body(stream.toByteArray());
        }
        catch (IOException exception) {
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/gmail/extract")
    public ResponseEntity<byte[]> gmailExtract(@RequestParam Map<String,String> emailFilter, HttpSession session, @RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient authorizedClient) {
        User user = (User) session.getAttribute("session_user");
        if (user == null || user.getRole().equals("maintainer")) {
            return ResponseEntity.status(401).build();
        }

        if (authorizedClient == null) {
            return ResponseEntity.status(401).build();
        }

        List<EmailData> emailData = getEmailListGmail(emailFilter.get("approvedSender"), emailFilter.get("startDate"), emailFilter.get("endDate"));

        Workbook wb = getEmailExcel(emailData);

        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            wb.write(stream);
            wb.close();
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"email.xls\"").contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")).body(stream.toByteArray());
        }
        catch (IOException exception) {
            return ResponseEntity.status(500).build();
        }
    }


    @PostMapping("/email/link")
    public String linkEmail(@RequestParam Map<String,String> emailInfo, HttpSession session, Model model, HttpServletResponse response) {
        User user = (User) session.getAttribute("session_user");
        if (user == null || user.getRole().equals("maintainer")) {
            return "redirect:/users/login";
        }

        if (emailInfo == null) {
            return "email";
        }

        RestClient client = RestClient.builder().baseUrl("https://api.mail.tm").requestInterceptor(new RestClientInterceptor()).build();
        Token token = getToken(client, emailInfo.get("address"), emailInfo.get("password"));
        session.setAttribute("session_token", token);


        model.addAttribute("isEmailLinked", true);
        return "email";
    }

    @GetMapping("/login/oauth2/code/google")
    public String verifyEmail(HttpSession session, Model model) {
        User user = (User) session.getAttribute("session_user");
        if (user == null || user.getRole().equals("maintainer")) {
            return "redirect:/users/login";
        }

        Token token = (Token) session.getAttribute("session_token");
        if (token != null) {
            model.addAttribute("isEmailLinked", true);         
        }
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