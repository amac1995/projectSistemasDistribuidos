package edu.ufp.inf.sd.rmi.project.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FactoryRI extends Remote {
    public boolean register(String jwtToken) throws RemoteException;
    public SessionRI login(String jwtToken) throws RemoteException;
    public boolean alive() throws RemoteException;
}
