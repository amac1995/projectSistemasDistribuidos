package edu.ufp.inf.sd.rmi.project.client;


import edu.ufp.inf.sd.rmi.project.server.State;
import edu.ufp.inf.sd.rmi.project.server.SubjectRI;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ObserverImpl extends UnicastRemoteObject implements ObserverRI{

    private String id;
    private State lastObserverState;
    protected SubjectRI subjectRI;
    protected GuiClient gui;

    public ObserverImpl(String id, GuiClient f, SubjectRI subjectRI) throws RemoteException{
        super();
        this.id=id;
        this.gui = f;
        this.lastObserverState =  new State(id,"");
        this.subjectRI = subjectRI;
        this.subjectRI.attach(this);
    }

    @Override
    public void update() throws RemoteException{
        this.lastObserverState=subjectRI.getState();
        //this.gui.updateTextArea();
    }

    protected State getLastObserverState(){
        return this.lastObserverState;
    }
}