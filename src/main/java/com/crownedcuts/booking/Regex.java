package com.crownedcuts.booking;

import java.util.regex.Pattern;

public final class Regex
{
    public final static String emailPattern = "^\\w*?[a-zA-Z]\\w+@[a-z\\d\\-]+(\\.[a-z\\d\\-]+)*\\.[a-z]+\\z";

    public static boolean isEmail(String email)
    {
        return Pattern
                .compile(emailPattern)
                .matcher(email)
                .matches();
    }
}
