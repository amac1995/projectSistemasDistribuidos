package edu.ufp.inf.sd.rmi.project.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

//Sessão de trabalho
public interface SessionRI extends Remote {
    public void listTaskGroups() throws RemoteException;
    public SubjectRI createTaskGroup(Integer credits, String name, String hash) throws RemoteException;
    public SubjectRI joinTaskGroup(Integer taskID) throws RemoteException;
    public boolean deleteTaskGroup(Integer taskID) throws RemoteException;
    public void logout() throws RemoteException;
    public DBMockup getDb();
}
