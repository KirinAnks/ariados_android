package com.ariados.ariadosclient.models;

import org.json.JSONObject;

public class Trainer {
    private String name;
    private String team;
    private String home_location;
    private String current_location;
    private String user__username;
    private String user__email;

    /**
     * @param name = username in Pokémon Go!
     * @param team = chosed team in Pokémon Go!
     * @param home_location = home city/town
     * @param current_location = current Geoposition (lattitude / longitude)
     * @param user__username = Ariados' user name
     * @param user__email =  Ariados' user email
     */
    public Trainer(String name, String team, String home_location, String current_location, String user__username, String user__email) {
        this.name = name;
        this.team = team;
        this.home_location = home_location;
        this.current_location = current_location;
        this.user__username = user__username;
        this.user__email = user__email;
    }

    public Trainer(JSONObject json_trainer) {
        try {
            this.name = json_trainer.getString("name");
            this.team = json_trainer.getString("team");
            this.home_location = json_trainer.getString("home_location");
            this.current_location = json_trainer.getString("current_location");
//            this.user__username = json_trainer.getString("user.username");
//            this.user__email = json_trainer.getString("user.email");
        } catch (Exception e) {
            this.name = "";
            this.team = "";
            this.home_location = "";
            this.current_location = "";
            this.user__username = "";
            this.user__email = "";
        }

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getHome_location() {
        return home_location;
    }

    public void setHome_location(String home_location) {
        this.home_location = home_location;
    }

    public String getCurrent_location() {
        return current_location;
    }

    public void setCurrent_location(String current_location) {
        this.current_location = current_location;
    }

    public String getUser__username() {
        return user__username;
    }

    public void setUser__username(String user__username) {
        this.user__username = user__username;
    }

    public String getUser__email() {
        return user__email;
    }

    public void setUser__email(String user__email) {
        this.user__email = user__email;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s", this.name, this.team, this.home_location);
    }
}
