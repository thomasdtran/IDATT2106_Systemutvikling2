package no.ntnu.idatt210620217.giddapi.model;

import java.util.Objects;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Class for EquipmentUser
 * @version 1.0
 */
@Entity(name = "EquipmentUser")
@Table(name = "equipment_user")
public class EquipmentUser{
    @EmbeddedId
    private EquipmentUserId id;
    
    @JsonIgnore
    @ManyToOne
    @MapsId("equipmentId")
    private Equipment equipment;

    @JsonIgnore
    @ManyToOne
    @MapsId("userId")
    private User user;

    @Column(name = "brought_quantity")
    private int broughtQuantity;

    /**
     * Empty constructor
     */
    private EquipmentUser() {}

    /**
     * Constructor
     * @param equipment Equipment that user brings
     * @param user User that brings equipment
     * @param broughtQuantity Number of equipment brought by user
     */
    public EquipmentUser(Equipment equipment, User user, int broughtQuantity) {
        this.equipment = equipment;
        this.user = user;
        this.broughtQuantity = broughtQuantity;
        this.id = new EquipmentUserId(equipment.getId(), user.getId());
    }

    /**
     * getters and setters
     */
    public EquipmentUserId getId() {
        return id;
    }
    
    public Equipment getEquipment() {
        return equipment;
    }

    public User getUser() {
        return user;
    }
    
    public int getBroughtQuantity() {
        return broughtQuantity;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setBroughtQuantity(int broughtQuantity) {
        this.broughtQuantity = broughtQuantity;
    }

    /**
     * Checks if objects is equal
     * @param o object to be checked
     * @return boolean if equal or not
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        EquipmentUser that = (EquipmentUser) o;
        return Objects.equals(equipment, that.equipment) && Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(equipment, user);
    }
}
