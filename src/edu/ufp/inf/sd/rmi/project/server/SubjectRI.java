package edu.ufp.inf.sd.rmi.project.server;


import edu.ufp.inf.sd.rmi.project.client.Client;
import edu.ufp.inf.sd.rmi.project.client.ObserverRI;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

//Tarefa
public interface SubjectRI extends Remote {
    public void setHashingState() throws RemoteException;
    public void updateHashingState() throws RemoteException;
    public boolean printTaskInfo() throws RemoteException;
    public boolean finishHash() throws RemoteException;
    public boolean finishTask() throws RemoteException;
    public void pauseTask() throws RemoteException;
    public boolean giveUpTask() throws RemoteException;
    public SubjectRI joinTask() throws RemoteException;
    public Task getTask();


    public void attach(ObserverRI obsRI) throws RemoteException;
    public void detach(ObserverRI obsRI) throws RemoteException;
    public State getState() throws RemoteException;
    public void setState(State state) throws RemoteException;
}