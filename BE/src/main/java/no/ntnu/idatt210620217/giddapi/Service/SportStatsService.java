package no.ntnu.idatt210620217.giddapi.Service;

import no.ntnu.idatt210620217.giddapi.model.*;
import no.ntnu.idatt210620217.giddapi.repo.ActivityRepo;
import no.ntnu.idatt210620217.giddapi.repo.SportRepo;
import no.ntnu.idatt210620217.giddapi.repo.SportStatsRepo;
import no.ntnu.idatt210620217.giddapi.repo.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

/**
 * Service class for sportStats service
 */
@Service
public class SportStatsService {
    @Autowired
    SportStatsRepo sportStatsRepo;
    @Autowired
    SportRepo sportRepo;
    @Autowired
    UserRepo userRepo;
    @Autowired
    ActivityRepo activityRepo;

    Logger logger = LoggerFactory.getLogger(EquipmentUserService.class);


    /**
     * Method for creating sportstats
     * @param sportStatsInfo object containing info about sport id, user id and points
     * @return sportstats or throws exception if sportStatsInfo contain wrong info
     */
    public SportStats createSportStatsWithPoints (SportStatsInfo sportStatsInfo) {
        Sport sport = sportRepo.findById(sportStatsInfo.getSportId());
        User user = userRepo.findById(sportStatsInfo.getUserId());
        if(sport == null || user == null) {
            throw new NoSuchElementException();
        }
        SportStats sportStats = new SportStats(sport, user, sportStatsInfo.getPoints());

        int currentTotaltPoints = sportStats.getUser().getTotalPoints();
        currentTotaltPoints += sportStatsInfo.getPoints();
        sportStats.getUser().setTotalPoints(currentTotaltPoints);
        return sportStatsRepo.save(sportStats);
    }

    /**
     * Method for adding points to an already created sportstats
     * @param sportStatsInfo containing sport id, user id and the points to get added
     * @return sportstats or throws exception if sportStatsInfo contain wrong info
     */
    public SportStats addPoints (SportStatsInfo sportStatsInfo) {
        SportUserId sportUserId = new SportUserId(sportStatsInfo.getSportId(), sportStatsInfo.getUserId());
        Optional<SportStats> sportStats = sportStatsRepo.findById(sportUserId);
        if(!sportStats.isPresent()) {
            createSportStatsWithPoints(sportStatsInfo);
            logger.info("SportStats not found, so automatically create an empty one");
        }
        logger.info("Addpoints method: " + sportStatsInfo.getUserId());
        int currentPoints = sportStats.get().getPoints();
        currentPoints += 10;
        sportStats.get().setPoints(currentPoints);
        int currentTotaltPoints = sportStats.get().getUser().getTotalPoints();
        currentTotaltPoints += 10;
        sportStats.get().getUser().setTotalPoints(currentTotaltPoints);
        userRepo.save(sportStats.get().getUser());
        return sportStatsRepo.save(sportStats.get());
    }

    /**
     * Method for finding all stats by a spesific sport
     * @param sportId the sport you want to find stats for
     * @return a sorted list containing all sportstats to a sport in descending order
     */
    public List<SportStats> findAllStatsBySportId(long sportId) {
        List<SportStats> allSports = sportStatsRepo.findAll();
        List<SportStats> allSportsById = new ArrayList<>();
        for(SportStats sportStats : allSports) {
            if(sportStats.getId().getSportId() == sportId) {
                allSportsById.add(sportStats);
            }
        }
        allSportsById.sort(Comparator.comparingInt(SportStats::getPoints));
        Collections.reverse(allSportsById);
        return allSportsById;
    }


    /**
     *  Method fetching all sport stats to a given user
     * @param userId Identifier for a user
     * @return A list of sport-stats for a given user
     */
    public List<SportStats> findAllStatsByUserId(long userId) {
        List<SportStats> allSports = sportStatsRepo.findAll();
        List<SportStats> allSportsByUserId = new ArrayList<>();
        for(SportStats sportStats : allSports) {
            if(sportStats.getId().getUserId() == userId) {
                allSportsByUserId.add(sportStats);
            }
        }
        return allSportsByUserId;
    }

    /**
     * Method for delegating points to participant of activity after activity has finished
     * @param activity Activity object
     */
    private void delegateActivityPoints(Activity activity){
        for (Participant participant : activity.getParticipants()){
            SportStatsInfo sportStatsInfo = new SportStatsInfo(activity.getSport().getId(),
                    participant.getUser().getId(),  0);
            logger.info("Points awarded to: " + participant.getUser().toString());
            addPoints(sportStatsInfo);
            activity.setHasReceivedActivityPoints(true);
            activityRepo.save(activity);
        }
    }

    /**
     * Scheduler that checks once an hour wether activities has finished or not.
     * If finished the activities will delegate points to participants
     */
    @Async
    @Scheduled(fixedRate = 60000 ) // Should run once an hour 3600000ms
    public void activityIsOverScheduler() {
        logger.info("running scheduler");
        for (Activity activity : activityRepo.findAll()){
            if (!activity.isHasReceivedActivityPoints()){
                // Dates for "now-time" and activity
                LocalDate nowDate = LocalDate.now();
                LocalDate activityDate = LocalDate.parse(activity.getDate());
                // checks if activity day is before this months day
                if (!activityDate.isBefore(nowDate)){
                    // checks if activity day is equal to today, and if activity (in hours) is less than "now-time"
                    if (nowDate.equals(activityDate) && LocalTime.parse(activity.getEndTime()).isBefore(LocalTime.now())){
                        delegateActivityPoints(activity);
                        logger.info("delegating points");
                    }
                } else {
                    // If activity-day of month is less than "now-time"-day of month
                    delegateActivityPoints(activity);
                    logger.info("delegating points");
                }
            }
        }
    }
}
