package edu.ufp.inf.sd.rmi.project.server;

import java.io.Serializable;

public class User implements Serializable {
    private String username;
    private String password;
    private Double credits;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.credits = 0.0;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Double getCredits() {
        return credits;
    }

    public void setCredits(Double credits) {
        this.credits = credits;
    }


    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                '}';
    }
}
