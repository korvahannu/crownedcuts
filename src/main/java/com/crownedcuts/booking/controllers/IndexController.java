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
        mav.addObject("userDetails", SecurityContextHolder.getContext().getAuthentication().getPrincipal() + " : " + SecurityContextHolder.getContext().getAuthentication().getAuthorities());
        return mav;
    }
}
