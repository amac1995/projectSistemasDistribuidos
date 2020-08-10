package edu.ufp.inf.sd.rmi.project.server;

import java.io.Serializable;

public class User implements Serializable {
    private String username;
    private String password;
    private Double credits;
    private Integer taskscompleted;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.credits = 0.0;
        this.taskscompleted = 0;
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

    public Integer getTaskscompleted() {
        return taskscompleted;
    }

    public void setTaskscompleted(Integer taskscompleted) {
        this.taskscompleted = taskscompleted;
    }
}
