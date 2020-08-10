package edu.ufp.inf.sd.rmi.project.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class Task implements Serializable {
    Integer taskID;
    String name;
    Integer creditos;
    Boolean pause = false;

    public Task(String name, Integer creditos) {
        Random rand = new Random();
        this.taskID = rand.nextInt(100000);
        this.name=name;
        this.creditos = creditos;
    }

    public Integer getTaskID() {
        return taskID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
