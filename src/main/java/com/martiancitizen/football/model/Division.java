package com.martiancitizen.football.model;

import com.martiancitizen.football.Utilities;

import java.util.Optional;

public class Division {

    private Optional<String> id;
    private Optional<String> name;

    public Division() {
    }

    public void setId(String arg) {
        this.id = Optional.of(Utilities.requiredArg(arg));
    }

    public String getId() {
        return id.orElseThrow(Utilities.OVE);
    }

    public void setName(String arg) {
        this.name = Optional.of(Utilities.requiredArg(arg));
    }

    public String getName() {
        return name.orElseThrow(Utilities.OVE);
    }

    public Division validate() {
        id.orElseThrow(() -> new IllegalArgumentException("id not specified"));
        name.orElseThrow(() -> new IllegalArgumentException("name not specified"));
        return this;
    }

}
