package com.ariados.ariadosclient;

public class Session {

    private String key;

    public Session() {
        this.key = "";
    }

    public Session(String key) {
        this.key = key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
