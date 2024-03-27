package com.github.elysalin18.cmpt276groupproject.donatedesk.controllers;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Random;
import java.util.random.RandomGenerator;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestClient;

import com.github.elysalin18.cmpt276groupproject.donatedesk.models.RestClientInterceptor;
import com.github.elysalin18.cmpt276groupproject.donatedesk.models.Token;
import com.github.elysalin18.cmpt276groupproject.donatedesk.models.User;

@SpringBootTest
@AutoConfigureMockMvc
public class EmailControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired 
    private EmailController emailController;

    @MockBean
    private Map<String,String> emailInfo;

    @BeforeAll
    static void setup() {
        
    }

    @Test 
    void testContextLoads() throws Exception {
        assertNotNull(emailController);
    }

    @Test
    void testGetEmailAdmin() throws Exception {
        User admin = new User("name", "password", "admin");
        mockMvc.perform(MockMvcRequestBuilders.get("/email").sessionAttr("session_user", admin)).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.view().name("email"));
    }
    
    @Test
    void testGetEmailMaintainer() throws Exception {
        User maintainer = new User("name","password","maintainer");
        mockMvc.perform(MockMvcRequestBuilders.get("/email").sessionAttr("session_user", maintainer)).andExpect(MockMvcResultMatchers.status().is3xxRedirection()).andExpect(MockMvcResultMatchers.view().name("redirect:/users/login"));
    }

    @Test
    void testGetEmailNull() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/email")).andExpect(MockMvcResultMatchers.status().is3xxRedirection()).andExpect(MockMvcResultMatchers.view().name("redirect:/users/login"));
    }

    /*
    @Test
    void testExtractEmailSimple() throws Exception {
        User admin = new User("name", "password", "admin");
        
        RestClient client = RestClient.builder().baseUrl("https://api.mail.tm").requestInterceptor(new RestClientInterceptor()).build();
        String domain = client.get().uri("/domains").accept(MediaType.APPLICATION_JSON).retrieve().body(String.class);
        Random rand = new Random();
        Map<String,String> account = Map.of("address","user" + rand.nextInt(2000) + "@" + domain,"password","1234");
        String id = client.post().uri("/accounts").accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).body(account).retrieve().body(String.class);

        mockMvc.perform(MockMvcRequestBuilders.post("/email").sessionAttr("session_user", admin)).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.view().name("email"));
    }
    */
}
