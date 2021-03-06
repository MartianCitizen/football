package com.martiancitizen.football;

import com.martiancitizen.football.model.Player;
import com.martiancitizen.football.model.Team;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import static org.springframework.http.ResponseEntity.ok;

import java.util.*;


@RestController
public class WebController {

    private final static Logger LOGGER = LoggerFactory.getLogger(WebController.class);

    @GetMapping(value = "/ping")
    public ResponseEntity<?> ping() {
        LOGGER.info("Request received: /ping");
        return ok("{\"message\": \"Pong\"}");
    }

    @GetMapping(value = "/refresh")
    public ResponseEntity<?> refresh() {
        LOGGER.info("Request received: /refresh");
        try {
            WebApplication.loadDatabase();
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
        return ok("{\"message\": \"Database refreshed\"}");
    }

    @GetMapping(value = "/teams")
    public ResponseEntity<?> teamList() {
        LOGGER.info("Request received: /teams");
        List<Team> teams = WebApplication.getDatabase().getTeams();
        return ok(teams);
    }

    @GetMapping(value = "/team/{id}")
      public ResponseEntity<?> teamInfo(@PathVariable(value = "id") String teamId) {
        LOGGER.info("Request received: /team/" + teamId);
        Optional<Team> teamOpt = WebApplication.getDatabase().getTeamForId(teamId);
        if (!teamOpt.isPresent()) {
            return ResponseEntity.status(404).body("{\"message\": \"No such team\"}");
        }
        return ok(teamOpt.get());
    }

    @GetMapping(value = "/roster/{id}")
    public ResponseEntity<?> rosterForTeam(@PathVariable(value = "id") String teamId) {
        LOGGER.info("Request received: /roster/" + teamId);
        Optional<Team> teamOpt = WebApplication.getDatabase().getTeamForId(teamId);
        if (!teamOpt.isPresent()) {
            return ResponseEntity.status(404).body("{\"message\": \"No such team\"}");
        }
        List<Player> roster = WebApplication.getDatabase().getTeamRoster(teamId);
        return ok(roster);
    }

}
