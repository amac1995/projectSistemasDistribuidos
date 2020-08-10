package edu.ufp.inf.sd.rmi.project.client;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ObserverRI extends Remote {
    //public void updateFactory() throws RemoteException;;
    public void updateSubject() throws RemoteException;;
}