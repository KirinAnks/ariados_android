package com.ariados.ariadosclient.models;

import org.json.JSONObject;

public class Answer {
    private String title;
    private String viewers;
    private String status;
    private String creator_name;
    private String text;
    private String answer_of;
    private String last_update;

    public Answer() {
        this.title = "";
        this.viewers = "";
        this.status = "";
        this.creator_name = "";
        this.text = "";
        this.answer_of = "";
        this.last_update = "";
    }

    public Answer(String title, String viewers, String status, String creator_name, String text, String answer_of, String last_update) {
        this.title = title;
        this.viewers = viewers;
        this.status = status;
        this.creator_name = creator_name;
        this.text = text;
        this.answer_of = answer_of;
        this.last_update = last_update;
    }

    public Answer(JSONObject json_trainer) {
        try {
            this.title = json_trainer.getString("title");
            this.viewers = json_trainer.getString("viewers");
            this.status = json_trainer.getString("status");
            this.creator_name = json_trainer.getString("creator_name");
            this.text = json_trainer.getString("text");
            this.answer_of = json_trainer.getString("answer_of");
            this.last_update = json_trainer.getString("last_update");
        } catch (Exception e) {
            this.title = "";
            this.viewers = "";
            this.status = "";
            this.creator_name = "";
            this.text = "";
            this.answer_of = "";
            this.last_update = "";
        }

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getViewers() {
        return viewers;
    }

    public void setViewers(String viewers) {
        this.viewers = viewers;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreator() {
        return creator_name;
    }

    public void setCreator(String creator_name) {
        this.creator_name = creator_name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLast_update() {
        return last_update;
    }

    public void setLast_update(String last_update) {
        this.last_update = last_update;
    }

    @Override
    public String toString() {
        return String.format("%s \n by %s at %s", this.text, this.creator_name, this.last_update);
    }
}
