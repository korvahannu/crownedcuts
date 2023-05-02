package com.crownedcuts.booking;

import com.crownedcuts.booking.records.BarberHairdresser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
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

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc(addFilters = false)
class ReservationRestControllerTests
{
    private final MockMvc mvc;

    @Autowired
    public ReservationRestControllerTests(MockMvc mvc)
    {
        this.mvc = mvc;
    }

    @Test
    void getBarbersWorks() throws Exception
    {
        var request = get("/rest/getBarbers")
                .accept(MediaType.APPLICATION_JSON);

        var result = mvc
                .perform(request)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var barbers = new ObjectMapper()
                .readValue(result, BarberHairdresser[].class);

        Assertions.assertEquals(5, barbers.length);
    }
}
