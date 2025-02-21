package com.example.client.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User {
    @JsonProperty
    private String name;

    public User(){
        this.name = "unknown user";
    }

    public User(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }
}
