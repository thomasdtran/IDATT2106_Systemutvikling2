package no.ntnu.idatt210620217.giddapi.Service;

import no.ntnu.idatt210620217.giddapi.model.Activity;
import no.ntnu.idatt210620217.giddapi.model.Equipment;
import no.ntnu.idatt210620217.giddapi.repo.ActivityRepo;
import no.ntnu.idatt210620217.giddapi.repo.EquipmentRepo;

import java.util.Collection;

import no.ntnu.idatt210620217.giddapi.security.BasicUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.AccessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service class for Equipment
 */
@Service
public class EquipmentService {
    @Autowired
    private EquipmentRepo equipmentRepo;

    @Autowired
    private ActivityRepo activityRepo;

    Logger logger = LoggerFactory.getLogger(EquipmentService.class);

    /**
     * Method for adding equipment to activity
     * @param activityId Id of the activity to add equipment to
     * @param equipments List of equipments to be added to activity
     * @return List of added equipment
     * @throws AccessException
     */
    public Collection<Equipment>addEquipment(long activityId, Collection<Equipment> equipments) throws AccessException {
        Activity activity = activityRepo.findById(activityId);

        if (equipments.isEmpty()) {
            return equipments;
        } 
        else if(activity == null){
            return null;
        }

        //checks if authenticated user is also organizer of activity
        BasicUserDetails authenticatedUser = (BasicUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(authenticatedUser.getId() != activity.getOrganizer().getId())
            throw new AccessException("Cannot add equipment if authenticated user is not organizer of activity");

        for (Equipment eq : equipments){
            eq.setActivity(activity);
            equipmentRepo.save(eq);
        }
        return equipmentRepo.findByActivityId(activityId);
    }

}
