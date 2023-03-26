package com.crownedcuts.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class BookingApplicationTests
{
    private final MockMvc mockMvc;

    @Autowired
    public BookingApplicationTests(MockMvc mockMvc)
    {
        this.mockMvc = mockMvc;
    }

    @Test
    void loginPageLoads() throws Exception
    {
        var request = get("/login")
                .accept(MediaType.TEXT_HTML);

        var result = mockMvc
                .perform(request)
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();

        boolean validPage = responseBody.contains("<title>Spring Security Login Page</title>");
        assertTrue(validPage);
    }

    @Test
    void notLoggedInReturnsRedirect() throws Exception
    {
        var request = get("/")
                .accept(MediaType.ALL);

        mockMvc
                .perform(request)
                .andExpect(status().is3xxRedirection());
    }
}
