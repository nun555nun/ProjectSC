package com.example.projectsc;

public class User {

    public String email;
    public String token;
    public String username;

    public User(String email, String token) {
        this.email = email;
        this.token = token;
    }

    public User(String email, String token, String username) {
        this.email = email;
        this.token = token;
        this.username = username;
    }
}
