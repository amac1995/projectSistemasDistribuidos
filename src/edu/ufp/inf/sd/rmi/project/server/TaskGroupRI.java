package edu.ufp.inf.sd.rmi.project.server;


import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

//Tarefa
public interface TaskGroupRI extends Remote {
    public void printTaskInfo() throws RemoteException;
    public void finishTask() throws RemoteException;
    public void pauseTask() throws RemoteException;
    public boolean giveUpTask() throws RemoteException;
    public String joinTask(SessionRI session)  throws RemoteException;
    public Task getTask();
    public List<SessionRI> getUserInTask() throws RemoteException;
}