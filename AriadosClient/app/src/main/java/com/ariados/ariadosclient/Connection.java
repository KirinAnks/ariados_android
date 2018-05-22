package com.ariados.ariadosclient;

public class Connection {

    private String key = "";

    public Connection(){

    }

    public Connection(String key){
        this.key = key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
