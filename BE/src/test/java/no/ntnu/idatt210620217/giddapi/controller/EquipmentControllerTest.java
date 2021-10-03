package no.ntnu.idatt210620217.giddapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import no.ntnu.idatt210620217.giddapi.GiddApiApplication;
import no.ntnu.idatt210620217.giddapi.model.*;
import no.ntnu.idatt210620217.giddapi.repo.ActivityRepo;
import no.ntnu.idatt210620217.giddapi.repo.EquipmentRepo;
import no.ntnu.idatt210620217.giddapi.repo.SportRepo;
import no.ntnu.idatt210620217.giddapi.repo.UserRepo;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;

import java.util.ArrayList;
import java.util.Collection;


@SpringBootTest
@AutoConfigureMockMvc
public class EquipmentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ActivityRepo activityRepo;

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private SportRepo sportRepo;

    @Autowired
    private EquipmentRepo equipmentRepo;

    @Autowired
    ObjectMapper objectMapper;

    private Activity activity;
    private User user;

    private Equipment eq;
    private ArrayList<Equipment> eqList = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        Sport sport = new Sport("Fotball");
        sportRepo.save(sport);


        activity = new Activity(1, "Fotballtrening", "Blir gøy, pls join", "Dødens dal",63.419127584258284, 10.4066510858173, "2021-04-14", "19:00",
                "20:00", "middels", sport, 10);
    
        user = new User(1, "Ola", "Nordmann", "ola.nordmann@email.com", "password", "Mann", "Stabæk", (short) 1965);

    

        // todo: denne fungerer ikke sammen med createActivity metodene siden jeg ikke
        // får sendt inn organizer id på denne måten her.
        userRepo.save(user);
        activity.setOrganizer(user);
        activityRepo.save(activity);

    }
    
    @AfterEach
    public void tearDown() {
        equipmentRepo.deleteAll();
        activityRepo.deleteAll();
        userRepo.deleteAll();
        sportRepo.deleteAll();
    }

    @Test
    void whenAddNewEquipmentToActicity_thenReturnEquipment() throws Exception {
        eq = new Equipment(1, "Ball", 5, 0, activity, null);
        eqList.add(eq);
        mockMvc.perform(
                post("/api/v1/activities/equipment?activityId=1")
                .content(objectMapper.writeValueAsString((Collection)eqList)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].type").value(eq.getType()));
    }

}
