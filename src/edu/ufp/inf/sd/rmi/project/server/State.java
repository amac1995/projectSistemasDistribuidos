package edu.ufp.inf.sd.rmi.project.server;

import java.io.Serializable;
import java.util.ArrayList;

public class State implements Serializable {
    private String msg;
    private String id;

    public State(String id, String m) {
        this.id = id;
        this.msg = m;
    }

    public String getId() {
        return id;
    }

    public String getInfo(){
        return this.msg;
    }

    public void setInfo(String m){
        this.msg = m;
    }
}