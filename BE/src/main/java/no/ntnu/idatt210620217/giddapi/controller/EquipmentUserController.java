package no.ntnu.idatt210620217.giddapi.controller;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import no.ntnu.idatt210620217.giddapi.model.BroughtEquipment;
import no.ntnu.idatt210620217.giddapi.model.Equipment;
import no.ntnu.idatt210620217.giddapi.Service.EquipmentUserService;
import java.util.Collection;
import org.slf4j.Logger;

@RestController
@RequestMapping("/api/v1/activities/{activityId}/equipment")
@CrossOrigin
public class EquipmentUserController {
    @Autowired
    EquipmentUserService service;
    Logger logger = LoggerFactory.getLogger(EquipmentUserController.class);

    /**
     * For when a user updates an equipment in the equipmentlist for an activity
     * @return Response message
     */
    @PutMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateEquipmentList(@RequestBody BroughtEquipment broughtEquipment){
        Equipment equipment = service.updateEquipmentList(broughtEquipment);
        if(equipment == null){
            logger.error("Something went wrong");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }else{
            logger.info("Sucessfully updated equipmentlist");
            return new ResponseEntity<>(equipment,HttpStatus.OK);
        }
    }

    /**
     * If a user wants to remove itself from bringing an equipment,
     * or decides to leave an activity
     * @param equipmentId
     */
    @DeleteMapping("/{equipmentId}/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> removeUserFromEquipment(@PathVariable long equipmentId, @PathVariable long userId){
        Equipment equipment = service.removeUserFromEquipment(equipmentId, userId);
        if(equipment == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }else{
            logger.info("Sucessfully updated equipmentlist");
            return new ResponseEntity<>(equipment, HttpStatus.OK);
        }
    }

    /**
     * Delete an equipment from the equipmentlist in the activity
     *
     * @param equipmentId
     * @return Response message
     */
    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteEquipment(@RequestParam long equipmentId){
        Equipment equipment = service.deleteEquipment(equipmentId);
        if(equipment == null){
            logger.error("Equipment not found");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }else{
            logger.info("Sucessfully deleted equipment from equipmentlist");
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    /**
     * Methode ot retrieve the equipmentlist from an activity
     * @param activityId
     * @return Response message and/or list with equipmentlist
     */
    @GetMapping
    public ResponseEntity<?> getEquipmentList(@PathVariable long activityId) {
        Collection<Equipment> response = service.getEquipmentList(activityId);
        if (response.isEmpty()) {
            logger.error("Equipmentlist is either empty or non-existent");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            logger.info("Sucessfully retrieved equipmentlist");
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
    }
}
