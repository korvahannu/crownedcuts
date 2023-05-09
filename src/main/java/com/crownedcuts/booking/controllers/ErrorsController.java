package com.crownedcuts.booking.controllers;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorsController implements ErrorController
{
    @RequestMapping("/error")
    public String getErrorPath()
    {
        return "redirect:/index";
    }
}
