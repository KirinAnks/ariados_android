package com.ariados.ariadosclient.models;

import org.json.JSONObject;

public class Post {
    private String title;
    private String viewers;
    private String status;
    private String creator__name;
    private String text;
    private String answer_of;
    private String last_update;

    public Post(){
        this.title = "";
        this.viewers = "";
        this.status = "";
        this.creator__name = "";
        this.text = "";
        this.answer_of = "";
        this.last_update = "";
    }

    public Post(String title, String viewers, String status, String creator__name, String text, String answer_of, String last_update) {
        this.title = title;
        this.viewers = viewers;
        this.status = status;
        this.creator__name = creator__name;
        this.text = text;
        this.answer_of = answer_of;
        this.last_update = last_update;
    }

    public Post(JSONObject json_trainer) {
        try {
            this.title = json_trainer.getString("title");
            this.viewers = json_trainer.getString("viewers");
            this.status = json_trainer.getString("status");
            this.creator__name = json_trainer.getString("creator__name");
            this.text = json_trainer.getString("text");
            this.answer_of = json_trainer.getString("answer_of");
            this.last_update = json_trainer.getString("last_update");
        } catch (Exception e) {
            this.title = "";
            this.viewers = "";
            this.status = "";
            this.creator__name = "";
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
        return creator__name;
    }

    public void setCreator(String creator__name) {
        this.creator__name = creator__name;
    }

    public String getUser__email() {
        return text;
    }

    public void setUser__email(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s %s", this.title, this.viewers, this.status, this.creator__name);
    }
}
