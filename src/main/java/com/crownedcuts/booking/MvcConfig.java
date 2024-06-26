package com.crownedcuts.booking;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import java.util.Locale;

@Configuration
public class MvcConfig implements WebMvcConfigurer
{
    @Override
    public void addViewControllers(ViewControllerRegistry registry)
    {
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/index").setViewName("index");
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/kirjaudusisaan").setViewName("login");
        registry.addViewController("/ajanvaraus").setViewName("newreservation");
        registry.addViewController("/newreservation").setViewName("newreservation");
        registry.addViewController("/information").setViewName("information");
        registry.addViewController("/omattiedot").setViewName("information");
        registry.addViewController("/ajanvarausonnistui").setViewName("reservationsuccessful");
        registry.addViewController("/reservationsuccessful").setViewName("reservationsuccessful");
        registry.addViewController("/allreservations").setViewName("allreservations");
        registry.addViewController("/allreservations").setViewName("kaikkiajanvaraukset");
    }

    @Bean
    public PasswordEncoder encoder()
    {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public LocaleResolver localeResolver()
    {
        var slr = new SessionLocaleResolver();
        slr.setDefaultLocale(new Locale("fi", "FI"));
        return slr;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor()
    {
        var lci = new LocaleChangeInterceptor();
        lci.setParamName("lang");
        return lci;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry)
    {
        registry.addInterceptor(localeChangeInterceptor());
    }
}
