package com.albsig.stundenmanager;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.Test;

public class FirebaseConnectionTest {

    private DatabaseReference mockDatabaseReference;
    private FirebaseDatabase mockFirebaseDatabase;

    @Before
    public void setUp() {
        mockFirebaseDatabase = mock(FirebaseDatabase.class);
        mockDatabaseReference = mock(DatabaseReference.class);

        when(mockFirebaseDatabase.getReference("test")).thenReturn(mockDatabaseReference);
    }

    @Test
    public void testFirebaseConnection() {

        doAnswer(invocation -> {
            ValueEventListener listener = invocation.getArgument(0);

            DataSnapshot mockSnapshot = mock(DataSnapshot.class);
            when(mockSnapshot.getValue(String.class)).thenReturn("testValue");
            listener.onDataChange(mockSnapshot);
            return null;
        }).when(mockDatabaseReference).addListenerForSingleValueEvent(any(ValueEventListener.class));


        final String[] result = {null};
        mockDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                result[0] = snapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                fail("Database error occurred");
            }
        });


        assertEquals("testValue", result[0]);
    }
}

