package no.ntnu.idatt210620217.giddapi.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Class for EquipmentUserId
 * @version 1.0
 */
@Embeddable
public class EquipmentUserId implements Serializable{
        @Column(name = "equipment_id")
        private long equipmentId;

        @Column(name = "user_id")
        private long userId;

    /**
     * Empty constructor
     */
    public EquipmentUserId (){}

    /**
     * Constructor for EquipmentUserId
     * @param equipmentId Id of equipment
     * @param userId Id of user
     */
    public EquipmentUserId(long equipmentId, long userId) {
            this.equipmentId = equipmentId;
            this.userId = userId;
        }

    /**
     * getters and setters
     */
    public long getEquipmentId() {
            return equipmentId;
        }

        public long getUserId() {
            return userId;
        }
        
        public void setEquipmentId(long equipmentId) {
            this.equipmentId = equipmentId;
        }

        public void setUserId(long userId) {
            this.userId = userId;
        }
        
        public boolean equals(Object o) {
            if (this == o)
                return true;

            if (o == null || getClass() != o.getClass())
                return false;

            EquipmentUserId that = (EquipmentUserId) o;
            return Objects.equals(equipmentId, that.equipmentId) && Objects.equals(userId, that.userId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(equipmentId, userId);
        }
}
