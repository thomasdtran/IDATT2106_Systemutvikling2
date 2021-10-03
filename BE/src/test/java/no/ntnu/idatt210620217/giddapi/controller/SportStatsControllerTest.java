package no.ntnu.idatt210620217.giddapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.ntnu.idatt210620217.giddapi.model.Sport;
import no.ntnu.idatt210620217.giddapi.model.SportStats;
import no.ntnu.idatt210620217.giddapi.model.SportStatsInfo;
import no.ntnu.idatt210620217.giddapi.model.User;
import no.ntnu.idatt210620217.giddapi.repo.SportRepo;
import no.ntnu.idatt210620217.giddapi.repo.SportStatsRepo;
import no.ntnu.idatt210620217.giddapi.repo.UserRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SportStatsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SportStatsRepo sportStatsRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private SportRepo sportRepo;

    @Autowired
    ObjectMapper objectMapper;

    private User user;
    private Sport sport;
    private SportStatsInfo sportStatsInfo;
    private SportStats sportStats;

    @BeforeEach
    public void setUp() {
        user = new User(0,"Stine", "Rygh", "stine@stine.no", "password123", "Kvinne", "Trondheim", (short)1994);
        user.setTotalPoints(0);
        userRepo.save(user);
        sport = new Sport("Fotball");
        sportRepo.save(sport);
        sportStatsInfo = new SportStatsInfo(sport.getId(), user.getId(), 10);
        sportStats = new SportStats(sport, user, 30);
        sportStatsRepo.save(sportStats);
    }
    @AfterEach
    public void tearDown() {
        sportStatsRepo.deleteAll();
        userRepo.deleteAll();
        sportRepo.deleteAll();
    }

    @Test
    void testCreateSportStatsWithPoints() throws Exception {
        mockMvc.perform(post("/api/v1/stats").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sportStatsInfo)))
                .andExpect(status().is(201))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.points", is(sportStatsInfo.getPoints())));
    }

    @Test
    void testAddPoints () throws Exception {
        mockMvc.perform(put("/api/v1/stats").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sportStatsInfo)))
                .andExpect(status().isOk());
    }

    @Test
    void testFindAllStatsBySportId() throws Exception {
        mockMvc.perform(get("/api/v1/stats/{id}", sport.getId()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].points", is(sportStats.getPoints())));
    }

    @Test
    void testFindAllStatsByUserId() throws Exception {
        mockMvc.perform(get("/api/v1/stats/users/{id}", user.getId()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].points", is(sportStats.getPoints())));
    }
}
