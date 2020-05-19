package edu.ufp.inf.sd.rmi.project.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class Task implements Serializable {
    ArrayList<User> userArrayList = new ArrayList<>();;
    Integer taskID;
    Integer creditos;
    Boolean pause = false;

    public Task(Integer creditos, User user) {
        Random rand = new Random();
        this.taskID = rand.nextInt(1000);
        this.creditos = creditos;
        setUsers(user);
    }

    public ArrayList<User> getUsers() {
        return userArrayList;
    }

    public void setUsers(User users) {
        userArrayList.add(users);
    }

    public Integer getTaskID() {
        return taskID;
    }

    public Integer getCreditos() {
        return creditos;
    }

    public void setCreditos(Integer creditos) {
        this.creditos = creditos;
    }

    public Boolean getPause() {
        return pause;
    }

    public void setPause(Boolean pause) {
        this.pause = pause;
    }
}
