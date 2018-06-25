package com.ariados.ariadosclient.models;

import org.json.JSONObject;

public class FriendRequest {
    private String id;
    private Trainer trainer_from;
    private Trainer trainer_to;
    private String status;

    public FriendRequest() {

    }

    public FriendRequest(JSONObject json) {
        try {
            this.trainer_from = new Trainer(json.getJSONObject("trainer_from"));
            this.trainer_to = new Trainer(json.getJSONObject("trainer_to"));
            this.id = json.getString("id");
            this.status = json.getString("status");
        } catch (Exception e) {
            this.trainer_from = null;
            this.trainer_to = null;
            this.id = "";
            this.status = "";
        }

    }

    @Override
    public String toString() {
        return String.format("%s -- %s", this.trainer_from, this.status);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Trainer getTrainer_from() {
        return trainer_from;
    }

    public void setTrainer_from(Trainer trainer_from) {
        this.trainer_from = trainer_from;
    }

    public Trainer getTrainer_to() {
        return trainer_to;
    }

    public void setTrainer_to(Trainer trainer_to) {
        this.trainer_to = trainer_to;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
