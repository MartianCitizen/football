package com.martiancitizen.football.model;

import static com.martiancitizen.football.Utilities.*;

import java.util.Optional;

/**
 * Created by johnchamberlain on 10/3/16.
 */
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
        id.orElseThrow(() -> new AssertionError("id not specified"));
        name.orElseThrow(() -> new AssertionError("name not specified"));
        return this;
    }

}
