package edu.ufp.inf.sd.rmi.project.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface SessionRI extends Remote {
    public ArrayList<Task> listTaskGroups() throws RemoteException;
    public boolean createTaskGroup(Integer credits, String hash) throws RemoteException;
    public boolean pauseTaskGroup(Integer taskID) throws RemoteException;
    public boolean deleteTaskGroup(Integer taskID) throws RemoteException;
    public void logout() throws RemoteException;
}
