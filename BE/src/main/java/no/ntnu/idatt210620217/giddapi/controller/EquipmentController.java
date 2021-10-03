package no.ntnu.idatt210620217.giddapi.controller;

import no.ntnu.idatt210620217.giddapi.Service.EquipmentService;
import no.ntnu.idatt210620217.giddapi.model.Equipment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.AccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/v1/activities/{id}/equipment")
@CrossOrigin
public class EquipmentController {
    @Autowired
    EquipmentService equipmentService;
    
    Logger logger = LoggerFactory.getLogger(ActivityController.class);

    /**
     * Method to add a list with equipments
     * @param id activityId
     * @param equipments List with equipments that should be added to an activity
     * @return Returns a list of all the equipments
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> addEquipment(@PathVariable long id, @RequestBody Collection<Equipment> equipments){
        Collection<Equipment> response = null;
        try {
            response = equipmentService.addEquipment(id, equipments);
        } catch (AccessException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        if(response == null){
            logger.error("Tried to add an equipment to an non-existent activity, or to another activity");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        else if(response.isEmpty()) {
            logger.error("Tried to add equipmentlist, but it is empty");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        else {
            logger.info("Sucessfully added equipments to the equipmentlist");
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
    }

}
