package edu.ufp.inf.sd.rmi.project.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface SessionRI extends Remote {
    public ArrayList<SubjectRI> listTaskGroups() throws RemoteException;
    public SubjectRI createTaskGroup(String name, String hash) throws RemoteException;
    public boolean pauseTaskGroup(SubjectRI subjectRI) throws RemoteException;
    public boolean deleteTaskGroup(SubjectRI subjectRI) throws RemoteException;
    public void logout() throws RemoteException;
}
