package no.ntnu.idatt210620217.giddapi.Service;

import java.util.Collection;
import java.util.Optional;

import no.ntnu.idatt210620217.giddapi.security.BasicUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.AccessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import no.ntnu.idatt210620217.giddapi.repo.EquipmentRepo;
import no.ntnu.idatt210620217.giddapi.repo.EquipmentUserRepo;
import no.ntnu.idatt210620217.giddapi.repo.UserRepo;
import no.ntnu.idatt210620217.giddapi.repo.ActivityRepo;
import no.ntnu.idatt210620217.giddapi.model.Activity;
import no.ntnu.idatt210620217.giddapi.model.BroughtEquipment;
import no.ntnu.idatt210620217.giddapi.model.Equipment;
import no.ntnu.idatt210620217.giddapi.model.EquipmentUser;
import no.ntnu.idatt210620217.giddapi.model.EquipmentUserId;
import no.ntnu.idatt210620217.giddapi.model.User;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * Service class for EquipmetUser
 */
@Service
public class EquipmentUserService {
    
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private EquipmentRepo equipmentRepo;

    @Autowired
    private EquipmentUserRepo equipmentUserRepo;

    Logger logger = LoggerFactory.getLogger(EquipmentUserService.class);

    /**
     * Method for updating activities equipmentlist
     * @param broughtEquipment Equipment brought by user
     * @return Brought equipment
     */
    public Equipment updateEquipmentList(BroughtEquipment broughtEquipment){
        long userId = broughtEquipment.getUserId();
        long equipmentId = broughtEquipment.getEquipmentId();
        int quantity = broughtEquipment.getQuantity();

        try{
            Equipment equipment = equipmentRepo.findById(equipmentId);
            User user = userRepo.findById(userId);

            BasicUserDetails authenticatedUser = (BasicUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if(authenticatedUser.getId() != user.getId())
                throw new AccessException("User and authenticated user must have the same id");

            if(equipment.updateQuantity(quantity)){
                EquipmentUser equipmentUser = new EquipmentUser(equipment, user, quantity);
                equipmentUserRepo.save(equipmentUser);
                return equipmentRepo.save(equipment);
            }else{
                logger.error("Quantity value too big");
                return null;
            }
        }catch(Exception e){
            logger.error(e.getMessage());
            return null;
        }

    }

    /**
     * Method for removing brought equipment from user
     * @param equipmentId Id of equipment brought by user
     * @param userId Id of user to remove equipment from
     * @return Equipment that is removed
     */
    public Equipment removeUserFromEquipment(long equipmentId, long userId){
        try{
            Equipment equipment = equipmentRepo.findById(equipmentId);
            EquipmentUserId equipmentUserId = new EquipmentUserId(equipmentId, userId);

            BasicUserDetails authenticatedUser = (BasicUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if(authenticatedUser.getId() != userId)
                throw new AccessException("User and authenticated user must have the same id");

            Optional<EquipmentUser> equipmentUser = equipmentUserRepo.findById(equipmentUserId);
            equipment.removeFromQuantity(equipmentUser.get().getBroughtQuantity());
            equipmentUserRepo.deleteById(equipmentUserId);
            return equipmentRepo.save(equipment);
        }catch(Exception e){
            logger.error(e.getMessage());
            return null;
        }
        
    }

    /**
     * Method for deleting equuipment from activity
     * @param equipmentId Id of equipment to be deleted
     * @return Deleted equipment
     */
    public Equipment deleteEquipment(long equipmentId){
        Equipment equipment = equipmentRepo.findById(equipmentId);
        if(equipment == null) return null;

        BasicUserDetails authenticatedUser = (BasicUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(authenticatedUser.getId() != equipment.getActivity().getOrganizer().getId())
            return null;

        return equipmentRepo.deleteById(equipmentId);
    }

    /**
     * Method for getting all equipments needed for activity
     * @param activityId Id of activity with connected equipmentlist
     * @return list of equipments needed for activity
     */
    public Collection<Equipment> getEquipmentList(long activityId){
        return equipmentRepo.findByActivityId(activityId);
    }
}
