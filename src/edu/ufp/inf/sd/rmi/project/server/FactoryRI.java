package edu.ufp.inf.sd.rmi.project.server;

import java.rmi.Remote;
import java.rmi.RemoteException;


//interface da factory onde estao os metodos (.h)
public interface FactoryRI extends Remote {
    public boolean register(String jwtToken) throws RemoteException;
    public SessionRI login(String jwtToken) throws RemoteException;
    public boolean alive() throws RemoteException;
}
