package no.ntnu.idatt210620217.giddapi.model;

import java.util.Objects;
import java.util.Set;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import org.hibernate.annotations.Cascade;

/**
 * Class to represent an equipment
 * @version 1.0
 */
@Entity
@Table(name = "Equipment")
public class Equipment {
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "type")
    private String type;

    @Column(name = "neededQuantity")
    private int neededQuantity;

    @Column(name = "quantity")
    private int quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="activity_id", nullable = true)
    @JsonIgnore
    private Activity activity;

    @OneToMany(
        mappedBy = "equipment",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private Set<EquipmentUser> equipmentUsers;

    public Equipment(){}

    /**
     * Constructor
     * @param type What kind of equipment
     * @param neededQuantity Needed quantity of an specific equipment
     * @param quantity quantity of equipment
     * @param activity Which activity a certain equipment is connected to
     * @param users List of users who have contributed to the qunaitity of the needed equipment
     */
    public Equipment(String type, int neededQuantity,
     int quantity, Activity activity, Set<EquipmentUser> users){
        this.type = type;
        this.neededQuantity = neededQuantity;
        this.quantity = quantity;
        this.activity = activity;
        this.equipmentUsers = users;
    }

    /**
     * Constructor with id and quantity
     * @param id id of equipment
     * @param type What kind of equipment
     * @param neededQuantity Needed quantity of an specific equipment
     * @param quantity quantity of equipment
     * @param activity Which activity a certain equipment is connected to
     * @param users List of users who have contributed to the qunaitity of the needed equipment
     */
    public Equipment(long id, String type, int neededQuantity, int quantity, Activity activity, Set<EquipmentUser> users) {
        this.id = id;
        this.type = type;
        this.neededQuantity = neededQuantity;
        this.quantity = quantity;
        this.activity = activity;
        this.equipmentUsers = users;
    }

    /**
     * Getters and setters
     */
    public long getId() {
        return id;
    }
    public String getType() {
        return type;
    }
    public int getNeededQuantity() {
        return neededQuantity;
    }
    public int getQuantity() {
        return quantity;
    }

    public Activity getActivity() {
        return activity;
    }
    
    public Set<EquipmentUser> getEquipmentUsers() {
        return equipmentUsers;
    }

    public void setType(String type) {
        this.type = type;
    }
    public void setNeededQuantity(int neededQuantity) {
        this.neededQuantity = neededQuantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public void setActivity(Activity activity) {
        this.activity = activity;
    }
    public void setUsers(Set<EquipmentUser> users) {
        this.equipmentUsers = users;
    }

    /**
     * Subtracts from the needed quantity of an equipment 
     * when a user registers that it can bring a ceratin amount
     * of the specific equipment
     * 
     * @param broughtQuantity The amount of a certain equipment a user will bring
     */
    public boolean updateQuantity(int broughtQuantity){
        if(broughtQuantity <= 0){
            return false;
        } 
        else if((this.quantity + broughtQuantity) > this.neededQuantity){
            return false;
        }else{
            this.quantity += broughtQuantity;
            return true;  
        }
    }

    /**
     * Method for removing from quantity of equipment
     * @param broughtQuantity amount of brought equipment to remove from quantity
     */
    public void removeFromQuantity(int broughtQuantity){
        this.quantity -= broughtQuantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        Equipment equipment = (Equipment) o;
        return Objects.equals(type, equipment.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.type);
    }
}

