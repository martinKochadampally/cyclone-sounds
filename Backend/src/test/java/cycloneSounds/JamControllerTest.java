package cycloneSounds;

import cycloneSounds.Credentials.CredentialRepository;
import cycloneSounds.Credentials.Credentials;
import cycloneSounds.Jams.*;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class JamControllerTest {

    @Autowired
    private JamController jamController;

    @MockBean
    private JamRepository jamRepository;

    @MockBean
    private JamMessageRepository jamMessageRepository;

    @MockBean
    private CredentialRepository credentialRepository;


    @Test
    void createJam_forbiddenWhenNoCredentials() {
        when(credentialRepository.findById("user1")).thenReturn(Optional.empty());

        ResponseEntity<Jam> response =
                jamController.createJam("user1", "myJam", "auto");

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
        verify(jamRepository, never()).save(any());
    }

    @Test
    void createJam_forbiddenWhenNotAuthorized() {
        Credentials creds = new Credentials();
        creds.setUsername("user1");
        creds.setAccountType("listener"); // not jamManager / admin

        when(credentialRepository.findById("user1")).thenReturn(Optional.of(creds));

        ResponseEntity<Jam> response =
                jamController.createJam("user1", "myJam", "auto");

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
        verify(jamRepository, never()).save(any());
    }

    @Test
    void createJam_badRequestWhenJamExists() {
        Credentials creds = new Credentials();
        creds.setUsername("manager1");
        creds.setAccountType("jamManager");

        when(credentialRepository.findById("manager1")).thenReturn(Optional.of(creds));
        when(jamRepository.existsById("existingJam")).thenReturn(true);

        ResponseEntity<Jam> response =
                jamController.createJam("manager1", "existingJam", "manual");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verify(jamRepository, never()).save(any());
    }

    @Test
    void createJam_success() {
        Credentials creds = new Credentials();
        creds.setUsername("manager1");
        creds.setAccountType("jamManager");

        when(credentialRepository.findById("manager1")).thenReturn(Optional.of(creds));
        when(jamRepository.existsById("newJam")).thenReturn(false);

        Jam savedJam = new Jam();
        savedJam.setName("newJam");
        savedJam.setApprovalType("manual");
        savedJam.setManager("manager1");
        savedJam.setMembers(List.of("manager1"));

        when(jamRepository.save(any(Jam.class))).thenReturn(savedJam);

        ResponseEntity<Jam> response =
                jamController.createJam("manager1", "newJam", "manual");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Jam body = response.getBody();
        assertNotNull(body);
        assertEquals("newJam", body.getName());
        assertEquals("manual", body.getApprovalType());
        assertEquals("manager1", body.getManager());
        assertEquals(List.of("manager1"), body.getMembers());

        ArgumentCaptor<Jam> captor = ArgumentCaptor.forClass(Jam.class);
        verify(jamRepository).save(captor.capture());
        Jam passed = captor.getValue();
        assertEquals("newJam", passed.getName());
        assertEquals("manual", passed.getApprovalType());
        assertEquals("manager1", passed.getManager());
        assertEquals(List.of("manager1"), passed.getMembers());
    }

    // ----------------getAllJams --------

    @Test
    void getAllJams_returnsList() {
        Jam jam1 = new Jam();
        jam1.setName("jam1");
        Jam jam2 = new Jam();
        jam2.setName("jam2");

        when(jamRepository.findAll()).thenReturn(List.of(jam1, jam2));

        List<Jam> result = jamController.getAllJams();

        assertEquals(2, result.size());
        assertEquals("jam1", result.get(0).getName());
        assertEquals("jam2", result.get(1).getName());
    }

    // -------- getJam --------

    @Test
    void getJam_found() {
        Jam jam = new Jam();
        jam.setName("jam1");

        when(jamRepository.findById("jam1")).thenReturn(Optional.of(jam));

        ResponseEntity<Jam> response = jamController.getJam("jam1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("jam1", response.getBody().getName());
    }

    @Test
    void getJam_notFound() {
        when(jamRepository.findById("missingJam")).thenReturn(Optional.empty());

        ResponseEntity<Jam> response = jamController.getJam("missingJam");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    // -------- getChatHistory --------

    @Test
    void getChatHistory_mapsMessages() {
        JamMessage msg1 = new JamMessage();
        msg1.setUserName("user1");
        msg1.setContent("Hello");
        JamMessage msg2 = new JamMessage();
        msg2.setUserName("user2");
        msg2.setContent("World");

        when(jamMessageRepository.findByJam_NameOrderBySentAsc("jam1"))
                .thenReturn(List.of(msg1, msg2));

        List<JamMessageDTO> dtos = jamController.getChatHistory("jam1");

        assertEquals(2, dtos.size());
        assertEquals("user1: Hello", dtos.get(0).getContent());
        assertEquals("user2: World", dtos.get(1).getContent());
    }

    // -------- deleteJam --------

    @Test
    void deleteJam_notFound() {
        when(jamRepository.existsById("missingJam")).thenReturn(false);

        ResponseEntity<Void> response = jamController.deleteJam("missingJam");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(jamRepository, never()).deleteById(anyString());
    }

    @Test
    void deleteJam_success() {
        when(jamRepository.existsById("jam1")).thenReturn(true);

        ResponseEntity<Void> response = jamController.deleteJam("jam1");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(jamRepository, times(1)).deleteById("jam1");
    }
}