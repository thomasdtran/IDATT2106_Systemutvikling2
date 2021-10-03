package no.ntnu.idatt210620217.giddapi.controller;

import no.ntnu.idatt210620217.giddapi.GiddApiApplication;
import no.ntnu.idatt210620217.giddapi.model.*;
import no.ntnu.idatt210620217.giddapi.repo.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;

@SpringBootTest
@AutoConfigureMockMvc
public class EquipmentUserControllerTest {
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
    private EquipmentUserRepo equipmentUserRepo;

    @Autowired
    ObjectMapper objectMapper;

    private Activity activity;
    private User user;
    private Equipment eq;

    private BroughtEquipment broughtEquipment;

    @BeforeEach
    public void setUp() {
        Sport sport = new Sport("Fotball");
        sportRepo.save(sport);

        activity = new Activity(1, "Fotballtrening", "Blir gøy, pls join", "Dødens dal",63.419127584258284, 10.4066510858173, "2021-04-14", "19:00", "20:00",
                "middels", sport, 10);

        user = new User(1, "Ola", "Nordmann", "ola.nordmann@email.com", "password", "Mann", "Stabæk", (short) 1965);

        eq = new Equipment(1, "Ball", 5, 3, activity, null);

        userRepo.save(user);
        activity.setOrganizer(user);
        activityRepo.save(activity);
        equipmentRepo.save(eq);

    }

    @AfterEach
    public void tearDown() {
        equipmentUserRepo.deleteAll();
        equipmentRepo.deleteAll();
        activityRepo.deleteAll();
        userRepo.deleteAll();
        sportRepo.deleteAll();
    }

    @Test
    void whenUpdateEquipment_thenReturnUpdatedQuantityForEquipment() throws Exception{
        int quantity = 2;
        int expectedQuantity = quantity + eq.getQuantity();
        //Takes the current quantity for an equipment and adds the new quantity
    
        broughtEquipment = new BroughtEquipment(activity.getId(),eq.getId(),quantity);

        mockMvc.perform(put("/api/v1/activities/1/equipment", activity.getId())
                .content(objectMapper.writeValueAsString(broughtEquipment))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.quantity").value(expectedQuantity));
    }
    
    @Test
    void whenRemoveUserFromEquipment_thenReturnExpectedQuantity() throws Exception{
        //User adds quantity to equipment with id 1
        int quantity = 3;
        EquipmentUser equipmentUser = new EquipmentUser(eq, user, quantity);
        equipmentUserRepo.save(equipmentUser);

        int expectedQuantityRemoved = eq.getQuantity() - quantity;

        //Removes user from the equipment, therefore takes away the quantity of 3
        mockMvc.perform(delete("/api/v1/activities/{id}/equipment/{equipmentId}", activity.getId(), eq.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.quantity").value(expectedQuantityRemoved));
    }
    

}
