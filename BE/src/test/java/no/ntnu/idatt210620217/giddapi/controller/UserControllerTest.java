package no.ntnu.idatt210620217.giddapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import no.ntnu.idatt210620217.giddapi.model.AuthenticationRequest;
import no.ntnu.idatt210620217.giddapi.model.User;
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
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${security.salt}")
    String salt;

    private User user1;
    private User user2;

    private AuthenticationRequest authenticationRequest;

    private static boolean jwtIsLoaded = false;
    private static String jwt;

    @BeforeEach
    public void setUp() {
        user1 = new User(0, "Stine", "Rygh", "stine@stine.no", hash("password123"), "Kvinne", "Trondheim", (short)1994);
        user2 = new User(0, "Niklas", "Bjoru", "niklas@niklas.no", "Password123!", "Mann", "Trondheim", (short)1998);
        authenticationRequest = new AuthenticationRequest(user1.getEmail(), "password123");

        userRepo.save(user1);

        if(!jwtIsLoaded) {
            jwt = getHeader();
            jwtIsLoaded = true;
        }
    }
    @AfterEach
    public void tearDown() {
        userRepo.deleteAll();
    }

    @Test
    void testCreateUser() throws Exception {
        System.out.println(user2.getPassword());

        mockMvc.perform(post("/api/v1/users").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user2)))
                .andExpect(status().is(201));
                /*.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.lastname", is(user2.getLastname())))
                .andExpect(jsonPath("$.firstname", is(user2.getFirstname())));*/
    }
    @Test
    void testFindUserById() throws Exception {
        mockMvc.perform(get("/api/v1/users/{id}", user1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", jwt))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.lastname", is(user1.getLastname())));
    }

    @Test
    void testGetUserAndTotalPoints() throws Exception {
        mockMvc.perform(get("/api/v1/users").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].[0]", is(user1.getFirstname())));
    }
    @Test
    void testUpdateUser() throws Exception {
        //Tests updating firstname, lastname, birthyear and gender
        user1.setFirstname("Herman");
        user1.setLastname("Herman");
        user1.setBirthyear((short) 1918);
        user1.setGender("Mann");
        mockMvc.perform(put("/api/v1/users/{id}", user1.getId()).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user1))
                .header("Authorization", jwt))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstname", is("Herman")))
                .andExpect(jsonPath("$.lastname", is("Herman")))
                .andExpect(jsonPath("$.birthyear", is(1918)))
                .andExpect(jsonPath("$.gender", is("Mann")));

        //Tests updating illegal birthyear
        user1.setBirthyear((short) 198);
        mockMvc.perform(put("/api/v1/users/{id}", user1.getId()).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user1))
                .header("Authorization", jwt))
                .andExpect(status().is4xxClientError());

        //Tests updating illegal gender
        user1.setGender("hi");
        mockMvc.perform(put("/api/v1/users/{id}", user1.getId()).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user1))
                .header("Authorization", jwt))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testDeleteUser() throws Exception {
        mockMvc.perform(delete("/api/v1/users/{id}", user1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", jwt))
                .andExpect(status().isOk());
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
