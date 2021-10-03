package no.ntnu.idatt210620217.giddapi.controller;

import no.ntnu.idatt210620217.giddapi.Service.SportService;
import no.ntnu.idatt210620217.giddapi.model.Sport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/sports")
@CrossOrigin
public class SportController {
    @Autowired
    SportService sportService;

    /**
     * PostMapping for creating a sport
     * @param sport RequestBody
     * @return sport and HttpStatus.Created or HttpStatus.BAD_REQUEST
     */
    @PostMapping
    public ResponseEntity<?> createSport(@RequestBody Sport sport) {
        try {
            Sport sport1 = sportService.createSport(sport);
            return new ResponseEntity<>(sport1, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * GetMapping for finding sport by id
     * @param id PathVariable
     * @return sport and HttpStatus.Created or HttpStatus.BAD_REQUEST
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> findSportById(@PathVariable long id) {
        try{
            Sport sport = sportService.findSportById(id);
            return new ResponseEntity<>(sport, HttpStatus.OK);
        }catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Getmapping for getting all sports
     * @return a list of all sports and HttpStatus.OK
     */
    @GetMapping
    public ResponseEntity<?> findAllSports() {
        List<Sport> allSports = sportService.findAllSports();
        return new ResponseEntity<>(allSports, HttpStatus.OK);
    }

    /**
     * PutMapping for changing sporttype
     * @param sport RequestBody with new sporttype
     * @param id of the sport that is going to be changed
     * @return the updated sport and HttpStatus.Created or HttpStatus.BAD_REQUEST
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSport(@RequestBody Sport sport, @PathVariable long id) {
        try{
            Sport updatedSport = sportService.updateSport(sport, id);
            return new ResponseEntity<>(updatedSport, HttpStatus.OK);
        }catch(IllegalArgumentException | NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * DeleteMapping for deleting sport by id
     * @param id of the sport to be deleted
     * @return HttpStatus.OK or HttpStatus.BAD_REQUEST
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSport(@PathVariable long id) {
        try{
            sportService.deleteSport(id);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
