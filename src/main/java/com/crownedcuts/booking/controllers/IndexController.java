package com.crownedcuts.booking.controllers;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IndexController
{
    @GetMapping("/")
    public ModelAndView onGet()
    {
        ModelAndView mav = new ModelAndView("index");

        var auth = SecurityContextHolder.getContext().getAuthentication();

        try
        {
            mav.addObject("userDetails", auth.getPrincipal());
        }
        catch (Exception exception)
        {
            mav.addObject("userDetails", "anonymous user");
        }

        return mav;
    }
}
