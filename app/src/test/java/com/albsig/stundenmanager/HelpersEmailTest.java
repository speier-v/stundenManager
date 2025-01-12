package com.albsig.stundenmanager;

import static org.junit.Assert.*;

import com.albsig.stundenmanager.common.Helpers;

import org.junit.Test;

public class HelpersEmailTest {

    @Test
    public void testCheckLoginMail_nullEmail() {
        String result = Helpers.checkLoginMail(null);
        assertEquals("Email is null", result);
    }

    @Test
    public void testCheckLoginMail_emptyEmail() {
        String result = Helpers.checkLoginMail("");
        assertEquals("Email cannot be empty", result);
    }

    @Test
    public void testCheckLoginMail_validEmail() {
        String result = Helpers.checkLoginMail("test@example.com");
        assertEquals("", result);
    }
}