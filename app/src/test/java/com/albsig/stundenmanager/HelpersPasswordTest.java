package com.albsig.stundenmanager;

import static org.junit.Assert.*;

import com.albsig.stundenmanager.common.Helpers;

import org.junit.Test;

public class HelpersPasswordTest {

    @Test
    public void testCheckLoginPassword_nullPassword() {
        String result = Helpers.checkLoginPassword(null);
        assertEquals("Password is null", result);
    }

    @Test
    public void testCheckLoginPassword_emptyPassword() {
        String result = Helpers.checkLoginPassword("");
        assertEquals("Password cannot be empty", result);
    }

    @Test
    public void testCheckLoginPassword_validPassword() {
        String result = Helpers.checkLoginPassword("validPassword123");
        assertEquals("", result);
    }
}