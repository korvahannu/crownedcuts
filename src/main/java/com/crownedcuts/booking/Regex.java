package com.crownedcuts.booking;

import java.util.regex.Pattern;

public final class Regex
{
    public static final String REGEX_EMAIL = "^\\w*?[a-zA-Z]\\w+@[a-z\\d\\-]+(\\.[a-z\\d\\-]+)*\\.[a-z]+\\z";

    private Regex()
    {
    }

    public static boolean isEmail(String email)
    {
        return Pattern
                .compile(REGEX_EMAIL)
                .matcher(email)
                .matches();
    }
}
