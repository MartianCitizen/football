package com.martiancitizen.football;

import com.martiancitizen.football.model.Player;
import com.martiancitizen.football.model.Team;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import static com.martiancitizen.football.WebApplication.*;
import static org.springframework.http.ResponseEntity.ok;

import java.util.*;


@RestController
public class WebController {


    @RequestMapping(value = "/ping", method = RequestMethod.GET)
    public ResponseEntity<?> ping() {
        LOGGER.info("Request received: /ping");
        return ok("Ping");
    }


    @RequestMapping(value = "/refresh", method = RequestMethod.GET)
    public ResponseEntity<?> refresh() {
        LOGGER.info("Request received: /refresh");
        try {
            WebApplication.loadDatabase();
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
        return ok("Database refreshed");
    }


    @RequestMapping(value = "/team/{id}", method = RequestMethod.GET)
      public ResponseEntity<?> teamInfo(@PathVariable(value = "id") String teamId) {
        WebApplication.LOGGER.info("Request received: /team/" + teamId);
        Optional<Team> teamOpt = WebApplication.DATABASE.getTeamForId(teamId);
        if (!teamOpt.isPresent()) {
            return ResponseEntity.status(404).body("{\"message\": \"No such team\"}");
        }
        return ok(teamOpt.get());
    }

    @RequestMapping(value = "/roster/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> rosterForTeam(@PathVariable(value = "id") String teamId) {
        WebApplication.LOGGER.info("Request received: /roster/" + teamId);
        Optional<Team> teamOpt = WebApplication.DATABASE.getTeamForId(teamId);
        if (!teamOpt.isPresent()) {
            return ResponseEntity.status(404).body("{\"message\": \"No such team\"}");
        }
        List<Player> roster = WebApplication.DATABASE.getTeamRoster(teamId);
        return ok(roster);
    }

}
