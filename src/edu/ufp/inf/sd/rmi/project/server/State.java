package edu.ufp.inf.sd.rmi.project.server;

import java.io.Serializable;
import java.util.ArrayList;

public class State implements Serializable {
    private Integer id = 0;
    private ArrayList<Task> arrayList = new ArrayList<>();

    public State(ArrayList<Task> tasks) {
        this.id += 1;
        this.arrayList = tasks;
    }

    public Integer getId() {
        return id;
    }

    public ArrayList<Task> getArrayList() {
        return arrayList;
    }
}