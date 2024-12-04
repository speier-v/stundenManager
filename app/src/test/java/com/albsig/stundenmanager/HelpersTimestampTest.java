package com.albsig.stundenmanager;

import static org.junit.Assert.*;
import org.junit.Test;

import com.albsig.stundenmanager.common.Helpers;
import com.google.firebase.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Locale;

public class HelpersTimestampTest {

    @Test
    public void testFSTimestampToDateString() {
        Timestamp timestamp = new Timestamp(Instant.ofEpochSecond(System.currentTimeMillis()));

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String expectedDate = sdf.format(new Date(timestamp.getSeconds() * 1000));

        String result = Helpers.FSTimestampToDateString(timestamp);
        assertEquals(expectedDate, result);
    }
}

