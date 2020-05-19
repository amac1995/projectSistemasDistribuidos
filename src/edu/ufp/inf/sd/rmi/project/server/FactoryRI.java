package edu.ufp.inf.sd.rmi.project.server;

import edu.ufp.inf.sd.rmi.project.client.Client;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author rmoreira
 */
public interface FactoryRI extends Remote {
    public boolean register(String uname, String pw) throws RemoteException;
    public SessionRI login(String uname, String pw) throws RemoteException;
    public void attach(Client client) throws RemoteException;;
    public void detach(Client client) throws RemoteException;;
    public State getState() throws RemoteException;
    public void setState(State state) throws RemoteException;;
}
