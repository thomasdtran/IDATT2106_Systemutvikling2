package no.ntnu.idatt210620217.giddapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "sport")
/**
 * Class for Sport
 * @version 1.0
 */
public class Sport {
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name="type")
    private String type;

    /**
     * Empty class constructor
     */
    public Sport(){}

    /**
     * Class constructor
     * @param type type of sport
     */
    public Sport(String type){
        this.type = type;
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

    public void setType(String type) {
        this.type = type;
    }
}
