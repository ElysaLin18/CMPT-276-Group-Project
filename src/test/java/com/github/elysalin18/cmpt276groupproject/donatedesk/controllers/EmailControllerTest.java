package com.github.elysalin18.cmpt276groupproject.donatedesk.controllers;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.github.elysalin18.cmpt276groupproject.donatedesk.models.User;

@SpringBootTest
@AutoConfigureMockMvc
public class EmailControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired 
    private EmailController emailController;

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
}
