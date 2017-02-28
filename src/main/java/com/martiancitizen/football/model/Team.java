package com.martiancitizen.football.model;

import java.util.Optional;
import java.util.function.Function;

import com.fasterxml.jackson.annotation.JsonIgnore;

import static com.martiancitizen.football.Utilities.*;


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
        return id.orElseThrow(REQUIRED_ATTRIBUTE_MISSING);
    }

    public void setName(String arg) {
        this.name = Optional.of(requiredArg(arg));
    }

    public String getName() {
        return name.orElseThrow(REQUIRED_ATTRIBUTE_MISSING);
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
        id.orElseThrow(() -> new IllegalArgumentException("id not specified"));
        name.orElseThrow(() -> new IllegalArgumentException("name not specified"));
        return this;
    }

}
