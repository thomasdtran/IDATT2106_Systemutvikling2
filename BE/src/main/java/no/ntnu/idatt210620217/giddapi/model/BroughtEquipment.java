package no.ntnu.idatt210620217.giddapi.model;

/**
 * Class to represent a user bringing equipment for an activity
 * @version 1.0
 */
public class BroughtEquipment {
    private long userId;
    private long equipmentId;
    private int quantity;

    /**
     * Constructor
     * @param userId Id of user bringing equipment
     * @param equipmentId id of equipment
     * @param quantity number of brought equipment
     */
    public BroughtEquipment(long userId, long equipmentId, int quantity){
        this.userId = userId;
        this.equipmentId = equipmentId;
        this.quantity = quantity;
    }

    /**
     *getters
     */
    public long getUserId() {
        return userId;
    }
    public long getEquipmentId() {
        return equipmentId;
    }
    public int getQuantity() {
        return quantity;
    }
}
