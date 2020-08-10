package edu.ufp.inf.sd.rmi.project.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

//Sessão de trabalho
public interface SessionRI extends Remote {
    public HashMap<User, ArrayList<TaskGroupRI>> getTaskGroups() throws RemoteException;
    public TaskGroupRI createTaskGroup(Integer credits, String name, String hash) throws RemoteException;
    public String joinTaskGroup(Integer taskID, Integer nThreads) throws RemoteException, CustomException;
    public boolean stopTask(Integer taskID) throws RemoteException;
    public boolean deleteTaskGroup(Integer taskID) throws RemoteException;
    public void logout() throws RemoteException;
    public DBMockup getDb() throws RemoteException;
    public User getMyUser() throws RemoteException;
}
