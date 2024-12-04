package com.albsig.stundenmanager;

import static org.mockito.Mockito.*;

import com.albsig.stundenmanager.common.Constants;
import com.albsig.stundenmanager.common.FirestoreUtil;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import com.google.android.gms.tasks.Task;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

public class FirestoreUtilTest {

    @Mock
    FirebaseFirestore mockDb;

    @Mock
    DocumentReference mockDocumentReference;

    @Mock
    Task<Void> mockTask;

    private FirestoreUtil firestoreUtil;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        firestoreUtil = new FirestoreUtil() {
            @Override
            public FirebaseFirestore getInstance() {
                return mockDb;
            }
        };
    }

    @Test
    public void testAddUser_success() {
        when(mockDb.collection(Constants.USERS_COLLECTION).document(anyString())).thenReturn(mockDocumentReference);
        when(mockDocumentReference.set(anyMap())).thenReturn(mockTask);
        when(mockTask.isSuccessful()).thenReturn(true);

        firestoreUtil.addUser("userId123", "John Doe", "john.doe@example.com");

        verify(mockDocumentReference).set(anyMap());
    }

    @Test
    public void testAddUser_failure() {
        when(mockDb.collection(Constants.USERS_COLLECTION).document(anyString())).thenReturn(mockDocumentReference);
        when(mockDocumentReference.set(anyMap())).thenReturn(mockTask);
        when(mockTask.isSuccessful()).thenReturn(false);

        firestoreUtil.addUser("userId123", "Jane Doe", "jane.doe@example.com");

        verify(mockDocumentReference).set(anyMap());
    }
}
