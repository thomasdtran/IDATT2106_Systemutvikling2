package no.ntnu.idatt210620217.giddapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.ntnu.idatt210620217.giddapi.model.Sport;
import no.ntnu.idatt210620217.giddapi.repo.SportRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SportControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SportRepo sportRepo;

    @Autowired
    ObjectMapper objectMapper;

    private Sport sport1;

    @BeforeEach
    public void setUp() {
        sport1 = new Sport("Fotball");
        sportRepo.save(sport1);
    }
    @AfterEach
    public void tearDown() {
        sportRepo.deleteAll();
    }

    @Test
    void testCreateSport() throws Exception {
        mockMvc.perform(post("/api/v1/sports").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sport1)))
                .andExpect(status().is(201))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.type", is(sport1.getType())));
    }

    @Test
    void testFindSportById() throws Exception {
        mockMvc.perform(get("/api/v1/sports/{id}", sport1.getId()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.type", is(sport1.getType())));
    }

    @Test
    void testFindAllSports() throws Exception{
        mockMvc.perform(get("/api/v1/sports").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].type", is(sport1.getType())));
    }

    @Test
    void testUpdateSport() throws Exception {
        sport1.setType("Håndball");
        mockMvc.perform(put("/api/v1/sports/{id}", sport1.getId()).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sport1)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.type", is("Håndball")));
    }

    @Test
    void testDeleteSport() throws Exception {
        mockMvc.perform(delete("/api/v1/sports/{id}", sport1.getId()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
