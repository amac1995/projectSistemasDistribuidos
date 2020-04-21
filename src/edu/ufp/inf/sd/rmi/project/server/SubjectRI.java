package edu.ufp.inf.sd.rmi.project.server;


import edu.ufp.inf.sd.rmi.project.client.ObserverRI;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface SubjectRI extends Remote {
    public void attach(ObserverRI o) throws RemoteException;
    public void dettach(ObserverRI o) throws RemoteException;
    public State getState() throws RemoteException;
    public void setState(State s) throws RemoteException;
}
