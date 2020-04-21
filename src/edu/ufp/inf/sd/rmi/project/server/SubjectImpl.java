package edu.ufp.inf.sd.rmi.project.server;

import edu.ufp.inf.sd.rmi.project.client.ObserverRI;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.ArrayList;


public class SubjectImpl extends UnicastRemoteObject implements SubjectRI {

    public State state;
    public ArrayList<ObserverRI> observers= new ArrayList<>();
    // Uses RMI-default sockets-based transport
    // Runs forever (do not passivates) - Do not needs rmid (activation deamon)
    // Constructor must throw RemoteException due to export()
    public SubjectImpl() throws RemoteException {
        // Invokes UnicastRemoteObject constructor which exports remote object
        super();
        this.state=new State("","");
    }

    @Override
    public void attach(ObserverRI o) throws RemoteException {
        if(!this.observers.contains(o)){
             this.observers.add(o);
        }
    }

    @Override
    public void dettach(ObserverRI o) throws RemoteException{
        this.observers.remove(o);
    }

    @Override
    public State getState() throws RemoteException{
        return this.state;
    }

    @Override
    public void setState(State s) throws RemoteException{
        this.state=s;
        this.notifyAllObservers();
    }


    public void notifyAllObservers(){
        for (ObserverRI ob: observers) {
            try {
                ob.update();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    }
}
