package no.ntnu.idatt210620217.giddapi.Service;

import no.ntnu.idatt210620217.giddapi.model.Sport;
import no.ntnu.idatt210620217.giddapi.repo.SportRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Service class for sport service
 */
@Service
public class SportService {
    @Autowired
    private SportRepo sportRepo;

    /**
     * Method for creating a sport
     * @param sport Object of sport to be created
     * @return the sport created or throw exception if input is wrong
     */
    public Sport createSport(Sport sport) {
        if(!isLetters(sport.getType())) {
            throw new IllegalArgumentException("Sport type must only contain letters.");
        }
        return sportRepo.save(sport);
    }

    /**
     * Method for finding sport by id
     * @param id the id of sport you want to find
     * @return the sport found or throw exception if the sport doesn't exist
     */
    public Sport findSportById(long id) {
        if(sportRepo.findById(id) == null) {
            throw new NoSuchElementException("Sport with this id: " + id + " does not exist");
        }
        return sportRepo.findById(id);
    }

    /**
     * Method for updating sport type
     * @param newSportType sport object containing the new type
     * @param id of the sport you want to change
     * @return the sport object with the new type or throw exception if input is wrong
     */
    public Sport updateSport(Sport newSportType, long id) {
        Sport current = findSportById(id);
        if(current == null) {
            throw new NoSuchElementException();
        } else if (newSportType.getType() == null || newSportType.getType() == "" || !isLetters(newSportType.getType())) {
            throw new IllegalArgumentException("Sport type must contain only letters and can not be empty");
        }
        current.setType(newSportType.getType());
        return sportRepo.save(current);
    }

    /**
     * Method for finding all sports
     * @return all sports
     */
    public List<Sport> findAllSports() {
        return sportRepo.findAll();
    }

    /**
     * Method for deleting a sport
     * @param id of the sport you want to delete
     * @return true if the sport is deleted or throw exception if the sport is not found
     */
    public boolean deleteSport(long id) {
        if(findSportById(id) == null) {
            throw new NoSuchElementException("Sport with this id: " + id + " does not exist");
        }
        sportRepo.deleteById(id);
        return true;
    }
    /**
     * Method used to check if sport type contains only letters
     * @param name, sport
     * @return boolean saying if the input is valid
     */
    public boolean isLetters(String name) {
        char[] chars = name.toCharArray();

        for (char c : chars) {
            if(!Character.isLetter(c)) {
                return false;
            }
        }
        return true;
    }

}
