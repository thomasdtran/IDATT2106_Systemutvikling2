package no.ntnu.idatt210620217.giddapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import no.ntnu.idatt210620217.giddapi.model.*;
import no.ntnu.idatt210620217.giddapi.repo.ActivityRepo;
import no.ntnu.idatt210620217.giddapi.repo.ParticipantRepo;
import no.ntnu.idatt210620217.giddapi.repo.SportRepo;
import no.ntnu.idatt210620217.giddapi.repo.UserRepo;
import no.ntnu.idatt210620217.giddapi.security.Sha256PasswordEncoder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ParticipantControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ParticipantController participantController;
    @Autowired
    private ActivityRepo activityRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ParticipantRepo participantRepo;
    @Autowired
    private SportRepo sportRepo;
    @Autowired
    ObjectMapper objectMapper;

    @Value("${security.salt}")
    String salt;

    public Activity activity;
    private User user1;
    private User user2;
    private User user3;

    private static String user1Jwt;
    private static String user2Jwt;
    private static String user3Jwt;
    private static boolean jwtIsLoaded = false;

    @BeforeEach
    public void setUp(){
        Sport sport = new Sport("Fotball");
        sportRepo.save(sport);

        activity = new Activity(0, "Fotballtrening", "Blir gøy, pls join", "Dødens dal", 63.419127584258284, 10.4066510858173,
                "2021-04-14", "19:00", "20:00", "middels", sport, 1);

        user1 = new User(0, "Ola", "Nordmann", "ola.nordmann@email.com",
                hash("password"), "Mann", "Stabæk", (short)1965);

        AuthenticationRequest authenticationRequest1 =
                new AuthenticationRequest(user1.getEmail(), "password");

        user1 = userRepo.save(user1);

        user2 = new User(0, "t", "t", "khb.t@email.com",
                hash("password"), "Mann", "Stabæk", (short)1965);
        AuthenticationRequest authenticationRequest2 =
                new AuthenticationRequest(user2.getEmail(), "password");

        activity = activityRepo.save(activity);
        userRepo.save(user2);

        user3 = new User(0, "sug", "t", "hgdf.asdih@email.com",
                hash("password"), "Mann", "Stabæk", (short)1965);
        AuthenticationRequest authenticationRequest3 =
                new AuthenticationRequest(user3.getEmail(), "password");

        userRepo.save(user3);

        if(!jwtIsLoaded) {
            user1Jwt = getHeader(authenticationRequest1);
            user2Jwt = getHeader(authenticationRequest2);
            user3Jwt = getHeader(authenticationRequest3);

            jwtIsLoaded = true;
        }
    }

    @AfterEach
    public void tearDown(){
        participantRepo.deleteAll();
        activityRepo.deleteAll();
        userRepo.deleteAll();
        sportRepo.deleteAll();
    }

    @Test
    void testSignUpToFullActivity() throws Exception {
        mockMvc.perform(post("/api/v1/participants/{activity_id}/{user_id}",
                activity.getId(), user1.getId()).contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", user1Jwt))
                .andExpect(status().isOk());

        System.out.println("list: " + activity.getWaitlist().size());

        mockMvc.perform(post("/api/v1/participants/{activity_id}/{user_id}",
                activity.getId(), user2.getId()).contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", user2Jwt))
                .andExpect(status().isOk());

        System.out.println("List: " + activity.getWaitlist().size());

        mockMvc.perform(post("/api/v1/participants/{activity_id}/{user_id}",
                activity.getId(), user3.getId()).contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", user3Jwt))
                .andExpect(status().isOk());
    }

    @Test
    void testSignUpToActivity() throws Exception {
        mockMvc.perform(post("/api/v1/participants/{activity_id}/{user_id}",
                activity.getId(), user1.getId()).contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", user1Jwt))
                .andExpect(status().isOk());
    }

    @Test
    void testSignUpToActivityFail() throws Exception {
        Participant participant = new Participant(user1, activity, false);
        participantRepo.save(participant);

        mockMvc.perform(post("/api/v1/participants/{activity_id}/{user_id}",
                activity.getId(), user1.getId()).contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", user1Jwt))
                .andExpect(status().is(400));
    }

    @Test
    void testSignOffActivity() throws Exception {
        Participant participant = new Participant(user1, activity, false);
        participantRepo.save(participant);

        mockMvc.perform(delete("/api/v1/participants/{activity_id}/{user_id}",
                activity.getId(), user1.getId()).contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", user1Jwt))
                .andExpect(status().isOk());
    }

    @Test
    void testSignOffActivityFail() throws Exception {
        mockMvc.perform(delete("/api/v1/participants/{activity_id}/{user_id}",
                activity.getId(), user1.getId()).contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", user1Jwt))
                .andExpect(status().is(400));
    }

    String getHeader(AuthenticationRequest authenticationRequest) {
        try {
            MvcResult mvcResult = mockMvc.perform(post("/api/v1/users/authenticate").contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(authenticationRequest)))
                    .andReturn();
            String res = mvcResult.getResponse().getContentAsString();
            return "Bearer " + JsonPath.parse(res).read("$.jwt");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "";
        }
    }

    String hash(String toHash) {
        PasswordEncoder passwordEncoder = new Sha256PasswordEncoder(salt);
        return passwordEncoder.encode(toHash);
    }

}
