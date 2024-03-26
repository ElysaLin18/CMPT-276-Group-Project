package com.github.elysalin18.cmpt276groupproject.donatedesk.controllers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.boot.jackson.JsonObjectDeserializer;
import org.springframework.boot.jackson.JsonObjectSerializer;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import com.github.elysalin18.cmpt276groupproject.donatedesk.models.RestClientInterceptor;

@Controller
public class EmailController {

    @GetMapping("/email")
    public String getEmail() {
        RestClient defaultClient = RestClient.create();
        // Why do I need an interceptor?
        defaultClient = defaultClient.mutate().requestInterceptor(new RestClientInterceptor()).build();
        Map<String, String> account = Map.of("address", "", "password", "");
        
        String response = defaultClient.post().uri("https://api.mail.tm/token").accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).body(account).retrieve().body(String.class);
        JSONObject object = new JSONObject(response);
        String token = object.getString("token");
        // todo check all pages
        response = defaultClient.get().uri("https://api.mail.tm/messages?page=1").accept(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + token).retrieve().body(String.class);
        JSONArray messages = new JSONArray(response);
        
        List<String> raw = new ArrayList<String>();
        for (int i = 0; i < messages.length(); i++) {
            JSONObject messageInfo = messages.getJSONObject(i);
            // todo filter from address
            // todo filter isDeleted
            // todo filter createdAt
            // todo find replyTo
            String id = messageInfo.getString("id");
            response = defaultClient.get().uri("https://api.mail.tm/messages/" + id).accept(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + token).retrieve().body(String.class);
            JSONObject message = new JSONObject(response);
            String text = message.getString("text");
            raw.add(text);
        }
        
        for (String m : raw) {
            System.out.println(m);
        }

        return "signup";
    }
}
