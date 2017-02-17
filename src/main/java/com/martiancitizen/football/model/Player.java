package com.martiancitizen.football.model;

import static com.martiancitizen.football.Utilities.*;

import java.util.Optional;
import java.util.function.Function;

/**
 * Created by johnchamberlain on 10/4/16.
 */
public class Player {

    private Optional<String> id = Optional.empty();
    private Optional<String> name = Optional.empty();
    private Optional<String> number = Optional.empty();
    private Optional<String> team = Optional.empty();
    private Optional<String> position = Optional.empty();
    private Optional<String> height = Optional.empty();
    private Optional<String> weight = Optional.empty();
    private Optional<String> age = Optional.empty();
    private Optional<String> college = Optional.empty();

    public Player() {
    }

    // Required data

    public void setName(String arg) {
        this.name = Optional.of(requiredArg(arg));
    }

    public String getName() {
        return name.orElseThrow(OVE);
    }

    public Function<String, String> setTeam = arg -> {
        this.team = Optional.of(requiredArg(arg));
        setId();
        return arg;
    };

    public String getTeam() {
        return team.orElseThrow(OVE);
    }

    public void setNumber(String arg) {
        this.number = Optional.of(requiredArg(arg));
        setId();
    }

    public String getNumber() {
        return number.orElseThrow(OVE);
    }

    private void setId() {
        if (team.isPresent() && number.isPresent()) {
            // Player id is combination of team id and player number (which is unique within a team)
            String idStr = String.format("%s-%s", team.get(), number.get());
            this.id = Optional.of(idStr);
        }
    }

    public String getId() {
        return id.orElseThrow(OVE);
    }

    // Optional data

    public void setPosition(String arg) {
        this.position = Optional.of(requiredArg(arg));
    }

    public String getPosition() {
        return position.orElse("NA");
    }

    public void setHeight(String arg) {
        this.height = Optional.of(requiredArg(arg));
    }

    public String getHeight() {
        return height.orElse("NA");
    }

    public void setWeight(String arg) {
        this.weight = Optional.of(requiredArg(arg));
    }

    public String getWeight() {
        return weight.orElse("NA");
    }

    public void setAge(String arg) {
        this.age = Optional.of(requiredArg(arg));
    }

    public String getAge() {
        return age.orElse("NA");
    }

    public void setCollege(String arg) {
        this.college = Optional.of(requiredArg(arg));
    }

    public String getCollege() {
        return college.orElse("NA");
    }

    public Player validate() {
        id.orElseThrow(() -> new IllegalArgumentException("id not specified"));
        team.orElseThrow(() -> new IllegalArgumentException("team not specified"));
        name.orElseThrow(() -> new IllegalArgumentException("name not specified"));
        number.orElseThrow(() -> new IllegalArgumentException("number not specified"));
        return this;
    }
}
