package edu.ufp.inf.sd.rmi.project.server;

import edu.ufp.inf.sd.rmi.project.client.ObserverRI;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FactoryRI extends Remote {
    public boolean register(String uname, String pw) throws RemoteException;
    public SessionRI login(String uname, String pw) throws RemoteException;
}
