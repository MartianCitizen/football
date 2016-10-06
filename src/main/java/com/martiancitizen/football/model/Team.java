package com.martiancitizen.football.model;

import java.util.Optional;
import java.util.function.Function;

import com.fasterxml.jackson.annotation.JsonIgnore;

import static com.martiancitizen.football.Utilities.*;

/**
 * Created by johnchamberlain on 10/3/16.
 */
public class Team {

    private Optional<String> id;
    private Optional<String> name;
    private Optional<String> conference;
    private Optional<String> division;

    public Team() {
    }

    public void setId(String arg) {
        this.id = Optional.of(requiredArg(arg));
    }

    public String getId() {
        return id.orElseThrow(OVE);
    }

    public void setName(String arg) {
        this.name = Optional.of(requiredArg(arg));
    }

    public String getName() {
        return name.orElseThrow(OVE);
    }

    @JsonIgnore
    public Function<String, String> setConference = arg -> {
        this.conference = Optional.of(requiredArg(arg));
        return arg;
    };

    public String getConference() {
        return conference.orElse("NA");
    }

    @JsonIgnore
    public Function<String, String> setDivision = arg -> {
        this.division = Optional.of(requiredArg(arg));
        return arg;
    };

    public String getDivision() {
        return division.orElse("NA");
    }

    public Team validate() {
        id.orElseThrow(() -> new AssertionError("id not specified"));
        name.orElseThrow(() -> new AssertionError("name not specified"));
        return this;
    }

}
