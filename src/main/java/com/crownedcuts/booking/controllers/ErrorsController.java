package com.crownedcuts.booking.controllers;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorsController implements ErrorController
{
    private static final String PATH = "/error";
    @RequestMapping(PATH)
    public String getErrorPath() {
        return "redirect:/index";
    }
}
