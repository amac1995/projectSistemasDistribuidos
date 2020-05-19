package edu.ufp.inf.sd.rmi.project.server;

import edu.ufp.inf.sd.rmi.project.client.Client;
import edu.ufp.inf.sd.rmi.project.client.ObserverRI;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class SubjectImpl extends UnicastRemoteObject implements SubjectRI{

    private State subjectState;

    private ArrayList<Client> clients = new ArrayList<>();


    protected SubjectImpl() throws RemoteException {
        super();
        this.subjectState = new State(null);
    }

    @Override
    public void attach(Client client) {
        if(!this.clients.contains(client)) this.clients.add(client);
    }

    @Override
    public void detach(Client client) {
        this.clients.remove(client);
    }

    @Override
    public State getState() {
        return this.subjectState;
    }

    @Override
    public void setState(State state) throws RemoteException {
        this.subjectState = state;
        this.notifyAllObservers();
    }


    public void notifyAllObservers() {
        for(Client client: clients){
            try{
                client.update();
            } catch (RemoteException ex){
                System.out.println(ex.toString());
            }
        }
    }
}