package no.ntnu.idatt210620217.giddapi.controller;

import no.ntnu.idatt210620217.giddapi.GiddApiApplication;
import no.ntnu.idatt210620217.giddapi.model.Activity;
import no.ntnu.idatt210620217.giddapi.model.BroughtEquipment;
import no.ntnu.idatt210620217.giddapi.model.Equipment;
import no.ntnu.idatt210620217.giddapi.model.EquipmentUser;
import no.ntnu.idatt210620217.giddapi.model.Notification;
import no.ntnu.idatt210620217.giddapi.model.User;
import no.ntnu.idatt210620217.giddapi.repo.ActivityRepo;
import no.ntnu.idatt210620217.giddapi.repo.EquipmentRepo;
import no.ntnu.idatt210620217.giddapi.repo.EquipmentUserRepo;
import no.ntnu.idatt210620217.giddapi.repo.NotificationRepo;
import no.ntnu.idatt210620217.giddapi.repo.UserRepo;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.event.annotation.AfterTestMethod;
import org.springframework.test.context.event.annotation.BeforeTestMethod;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;

@SpringBootTest
@AutoConfigureMockMvc
public class NotificationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepo userRepo;

    @Autowired
    NotificationRepo notificationRepo;

    private User user;

    Notification testNotification;

    private String expectedTitle =  "I dag er en fin dag!";

    @BeforeEach
    public void setup(){
        user = new User(1, "Ola", "Nordmann", "ola.nordmann@email.com", "password", "Mann", "Stabæk",
                (short) 1965);
        userRepo.save(user);
        testNotification = new Notification(expectedTitle, "description", "2021-10-02", "16:00", user);
    }

    @AfterEach
    public void tearDown() {
        notificationRepo.deleteAll();
        userRepo.deleteAll();
    }

    @Test
    void whenCreateNotification_thenReturnCreatedNotification() throws Exception{
        mockMvc.perform(post("/api/v1/notification/1")
                .content(objectMapper.writeValueAsString(testNotification))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value(testNotification.getTitle()));
    }

    @Test
    void whenGetAllNotifications_thenReturnNotificationWithRightTitle() throws Exception {
        mockMvc.perform(post("/api/v1/notification/1").content(objectMapper.writeValueAsString(testNotification))
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value(testNotification.getTitle()));

        mockMvc.perform(get("/api/v1/notification/1")
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].title").value(this.expectedTitle));
    }

}
