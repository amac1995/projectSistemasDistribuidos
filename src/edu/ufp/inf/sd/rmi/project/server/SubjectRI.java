package edu.ufp.inf.sd.rmi.project.server;


import edu.ufp.inf.sd.rmi.project.client.Client;
import edu.ufp.inf.sd.rmi.project.client.ObserverRI;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface SubjectRI extends Remote {
    public void attach(Client client) throws RemoteException;;
    public void detach(Client client) throws RemoteException;;
    public State getState() throws RemoteException;
    public void setState(State state) throws RemoteException;;
}