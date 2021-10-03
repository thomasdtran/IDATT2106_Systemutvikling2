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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ActivityControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ActivityRepo activityRepo;

  @Autowired
  private SportRepo sportRepo;

  @Autowired
  private UserRepo userRepo;

  @Autowired
  private ParticipantRepo participantRepo;

  @Autowired
  ObjectMapper objectMapper;

  @Value("${security.salt}")
  String salt;

  private Activity activity1;
  private Activity activity2;
  private Activity activity3;
  private User user;
  private User user2;
  private Participant participant;
  private AuthenticationRequest authenticationRequest;

  private static boolean jwtIsLoaded = false;
  private static String jwt;

  @BeforeEach
  public void setUp (){
    Sport sport = new Sport("Fotball");
    Sport sport1 = new Sport("Yoga");
    sportRepo.save(sport);
    sportRepo.save(sport1);


    activity1 = new Activity(0, "Fotballtrening", "Blir gøy, pls join", "Dødens dal",63.41911060186732, 10.406659527640247,
            "2021-04-14", "19:00", "20:00", "middels", sport, 10);
    activity2 = new Activity(0, "RoligYoga", "Avslappende", "Sit Gløshaugen", 63.42102669083532, 10.404567327640317,
            "2021-04-20", "19:00", "20:00", "lav", sport1, 10);
    activity3 = new Activity(0, "Fotballtrening", "Blir gøy, pls join", "Dødens dal",63.41911060186732, 10.406659527640247,
            "2021-04-14", "19:00", "20:00", "middels", sport, 10);
    user = new User(0, "Ola", "Nordmann", "ola.nordmann@email.com",
            hash("password"), "Mann", "Stabæk", (short)1965);
    user2 = new User(0, "Mari", "Hansen", "marih.@email.com",
            hash("password"), "Kvinne", "Oslo", (short)1998);

    authenticationRequest = new AuthenticationRequest(user.getEmail(), "password");

    //todo: denne fungerer ikke sammen med createActivity metodene siden jeg ikke får sendt inn organizer id på denne måten her.
    userRepo.save(user);
    activity1.setOrganizer(user);
    activity2.setOrganizer(user);
    activityRepo.save(activity1);
    activityRepo.save(activity2);

    if(!jwtIsLoaded) {
      jwt = getHeader();
      jwtIsLoaded = true;
    }
  }

  @AfterEach
  public void tearDown(){
    activityRepo.deleteAll();
    userRepo.deleteAll();
    sportRepo.deleteAll();
  }


  @Test
  void testFindAllActivities () throws Exception {
    mockMvc.perform(get("/api/v1/activities").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].title", is("Fotballtrening")))
        .andExpect(jsonPath("$[1].title", is("RoligYoga")));
  }

  @Test
  void testFindActivityByTitle() throws Exception {
    mockMvc.perform(get("/api/v1/activities/trigger/{title}", activity1.getTitle()).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
        .andExpect(jsonPath("$[0].title", is(activity1.getTitle())));
  }
  @Test
  void testCreateActivity() throws Exception {
    mockMvc.perform(post("/api/v1/activities/{id}", user.getId()).contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(activity3))
            .header("Authorization", jwt))
        .andExpect(status().is2xxSuccessful())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.title", is(activity1.getTitle())));
  }
  @Test
  void testUpdateActivity() throws Exception {
    activity1.setTitle("Svømming");
    mockMvc.perform(put("/api/v1/activities/{id}", activity1.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(activity1))
            .header("Authorization", jwt))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.title", is("Svømming")));

    //Tests updating description
    activity1.setDescription("Test description");
    mockMvc.perform(put("/api/v1/activities/{id}", activity1.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(activity1))
            .header("Authorization", jwt))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.description", is("Test description")));

    //Tests updating loccation
    activity1.setLocation("Gudbrandsdal");
    mockMvc.perform(put("/api/v1/activities/{id}", activity1.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(activity1))
            .header("Authorization", jwt))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.location", is("Gudbrandsdal")));

    //Tests updating date
    activity1.setDate("2021-12-11");
    mockMvc.perform(put("/api/v1/activities/{id}", activity1.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(activity1))
            .header("Authorization", jwt))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.date", is("2021-12-11")));

    //Tests updating illegal date
    activity1.setDate("21");
    mockMvc.perform(put("/api/v1/activities/{id}", activity1.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(activity1))
            .header("Authorization", jwt))
            .andExpect(status().is4xxClientError());

    //Tests updating illegal start time
    activity1.setStartTime("2100");
    mockMvc.perform(put("/api/v1/activities/{id}", activity1.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(activity1))
            .header("Authorization", jwt))
            .andExpect(status().is4xxClientError());

    //Tests updating illegal end time
    activity1.setEndTime("2300");
    mockMvc.perform(put("/api/v1/activities/{id}", activity1.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(activity1))
            .header("Authorization", jwt))
            .andExpect(status().is4xxClientError());
  }
  @Test
  void testDeleteActivity() throws Exception {
    mockMvc.perform(delete("/api/v1/activities/{id}", activity1.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", jwt))
            .andExpect(status().isOk());
  }
  @Test
  void testCreateRating() throws Exception {
    participant = new Participant(user, activity1, false);
    participantRepo.save(participant);

    mockMvc.perform(post("/api/v1/activities/{activityId}/rating/{userId}/{rating}",
            activity1.getId(), user.getId(), 2).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
  }
  @Test
  void testCreateRatingFail() throws Exception {
    mockMvc.perform(post("/api/v1/activities/{activityId}/rating/{userId}/{rating}",
            activity2.getId(), user2.getId(), 6).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().is4xxClientError());
  }

  String getHeader() {
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
