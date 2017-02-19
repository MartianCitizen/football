package com.martiancitizen.football.model;

import static com.martiancitizen.football.Utilities.*;

import java.util.Optional;

public class Conference {

    private Optional<String> id;
    private Optional<String> name;

    public Conference() {
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

    public Conference validate() {
        id.orElseThrow(() -> new IllegalArgumentException("id not specified"));
        name.orElseThrow(() -> new IllegalArgumentException("name not specified"));
        return this;
    }

}
