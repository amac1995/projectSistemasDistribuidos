package edu.ufp.inf.sd.rmi.project.server;

import edu.ufp.inf.sd.rmi.project.client.Client;
import edu.ufp.inf.sd.rmi.project.client.ObserverRI;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.ArrayList;

//Tarefa
public class SubjectImpl implements SubjectRI{

    private State subjectState;
    private Task task;

    private ArrayList<ObserverRI> observers = new ArrayList<>();

    protected SubjectImpl(Integer credits, String name, String hash) throws RemoteException {
        super();
        this.task = new Task(name, credits);
        this.subjectState = new State("","");
    }

    @Override
    public void setHashingState() throws RemoteException {

    }

    @Override
    public void updateHashingState() throws RemoteException {

    }

    @Override
    public boolean printTaskInfo() throws RemoteException {
        if(task != null) {
            System.out.println("Tarefa ID: " + task.getTaskID() + "\n\tNome: " + task.getName() + "\n\tCreditos: " + task.getCreditos() + "\n\tEm pausa: " + task.getPause().toString());
            return true;
        }
        return false;
    }

    @Override
    public boolean finishHash() throws RemoteException {
        return false;
    }

    @Override
    public boolean finishTask() throws RemoteException {
        return false;
    }

    @Override
    public void pauseTask() throws RemoteException {
        task.setPause(!task.getPause());
        setState(new State("pause", task.getPause().toString()));
    }

    @Override
    public boolean giveUpTask() throws RemoteException {
        return false;
    }

    @Override
    public SubjectRI joinTask() throws RemoteException {
        return this;
    }

    public Task getTask() {
        return task;
    }

    @Override
    public void attach(ObserverRI obsRI) {
        if(!this.observers.contains(obsRI)) this.observers.add(obsRI);
    }

    @Override
    public void detach(ObserverRI obsRI) {
        this.observers.remove(obsRI);
    }

    @Override
    public State getState() {
        return this.subjectState;
    }

    @Override
    public void setState(State state) {
        this.subjectState = state;
        this.notifyAllObservers();
    }

    public void notifyAllObservers() {
        for(ObserverRI obs : observers){
            try{
                obs.update();
            } catch (RemoteException ex){
                System.out.println(ex.toString());
            }
        }
    }
}