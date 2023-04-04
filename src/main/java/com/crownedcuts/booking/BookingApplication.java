package com.crownedcuts.booking;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class BookingApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(BookingApplication.class, args);
    }

    @PostConstruct
    public void init()
    {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Helsinki"));
    }
}
