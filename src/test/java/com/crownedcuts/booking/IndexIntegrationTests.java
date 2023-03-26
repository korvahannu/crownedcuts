package com.crownedcuts.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class IndexIntegrationTests
{
    private final MockMvc mockMvc;

    @Autowired
    public IndexIntegrationTests(MockMvc mockMvc)
    {
        this.mockMvc = mockMvc;
    }

    @Test
    void loggedInReturnsCorrectPage() throws Exception
    {
        var request = get("/")
                .accept(MediaType.TEXT_HTML);

        mockMvc
                .perform(request)
                .andExpect(status().isOk());
    }
}
