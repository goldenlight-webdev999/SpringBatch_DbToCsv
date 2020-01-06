package com.example.databaseToCsv.model;

public class User {
    private Integer id;
    private String name;
    private String email;

    public User() {
    }

    public User( Integer id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public  String toString() {
        return "id: " + id + "name: " + name + ", email: " + email;
    }

}
