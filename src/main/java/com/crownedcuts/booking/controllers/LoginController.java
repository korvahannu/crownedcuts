package com.crownedcuts.booking.controllers;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoginController
{
    @GetMapping(value = {"/login", "/kirjaudusisaan"})
    public ModelAndView getLoginPage()
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!(auth instanceof AnonymousAuthenticationToken))
        {
            return new ModelAndView("forward:/index");
        }

        return new ModelAndView("login");
    }
}
