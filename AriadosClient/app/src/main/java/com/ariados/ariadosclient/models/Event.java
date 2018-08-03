package com.ariados.ariadosclient.models;

import org.json.JSONObject;

public class Event {
    private String id;
    private String title;
    private String days;
    private String description;

    public Event() {
        this.title = "";
        this.days = "";
        this.description = "";
        this.id = "";
    }

    public Event(String title, String days, String description, String id) {
        this.title = title;
        this.days = days;
        this.description = description;
        this.id = id;
    }

    public Event(JSONObject json_trainer) {
        try {
            this.title = json_trainer.getString("title");
            this.days = json_trainer.getString("days");
            this.description = json_trainer.getString("description");
            this.id = json_trainer.getString("id");
        } catch (Exception e) {
            this.title = "";
            this.days = "";
            this.description = "";
            this.id = "";
        }

    }


    @Override
    public String toString() {
        return String.format("%s || [%s] :\n%s", this.days, this.title, this.description);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
