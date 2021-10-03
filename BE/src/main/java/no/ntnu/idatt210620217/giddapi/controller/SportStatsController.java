package no.ntnu.idatt210620217.giddapi.controller;

import no.ntnu.idatt210620217.giddapi.Service.SportStatsService;
import no.ntnu.idatt210620217.giddapi.model.SportStats;
import no.ntnu.idatt210620217.giddapi.model.SportStatsInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/stats")
@CrossOrigin
public class SportStatsController {
    @Autowired
    SportStatsService sportStatsService;

    /**
     * PostMapping for creating sportStats
     * @param sportStatsInfo Requestbody
     * @return sportstats and HttpStatus.CREATED or HttpStatus.BAD_REQUEST
     */
    @PostMapping
    public ResponseEntity<?> createSportStatsWithPoints(@RequestBody SportStatsInfo sportStatsInfo) {
        try{
            SportStats sportStats = sportStatsService.createSportStatsWithPoints(sportStatsInfo);
            return new ResponseEntity<>(sportStats, HttpStatus.CREATED);
        }catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * PutMapping for adding points
     * @param sportStatsInfo Requestbody with sportstats id and points to be added
     * @return sportstats and HttpStatus.OK or HttpStatus.BAD_REQUEST
     */
    @PutMapping
    public ResponseEntity<?> addPoints(@RequestBody SportStatsInfo sportStatsInfo) {
        try {
            SportStats sportStats = sportStatsService.addPoints(sportStatsInfo);
            return new ResponseEntity<>(sportStats, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * GetMapping for getting a list of sports by id sorted descending
     * @param id of the sport to be listed
     * @return list of all users with sportstats for the specific sport and HttpStatus.OK
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> findAllStatsBySportId(@PathVariable long id) {
        List<SportStats> allSportsById = sportStatsService.findAllStatsBySportId(id);
        return new ResponseEntity<>(allSportsById, HttpStatus.OK);
    }

    /**
     * GetMapping for getting a list of sportstats that a user had points in
     * @param id of the user
     * @return list of all sportsstats with specific user id and HttpStatus.OK
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<?> findAllStatsByUserId(@PathVariable long id) {
        List<SportStats> allSportsByUserId = sportStatsService.findAllStatsByUserId(id);
        return new ResponseEntity<>(allSportsByUserId, HttpStatus.OK);
    }
}
