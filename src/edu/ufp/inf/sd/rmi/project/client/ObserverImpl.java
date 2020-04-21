package edu.ufp.inf.sd.rmi.project.client;


import edu.ufp.inf.sd.rmi.project.server.State;
import edu.ufp.inf.sd.rmi.project.server.SubjectRI;
import edu.ufp.inf.sd.rmi.util.rmisetup.SetupContextRMI;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ObserverImpl extends UnicastRemoteObject implements ObserverRI {

    public String username;
    //public SubjectRI subjectRI= new SubjectImpl();
    public SubjectRI subjectRI;
    public State lastObserverState;
    public ObserverGuiClient gui;


    //boas alex
    //boas andre

    private SetupContextRMI contextRMI;

    public ObserverImpl(String username, SubjectRI subjectRI, State lastObserverState, ObserverGuiClient gui) throws RemoteException {
        super();
        this.username = username;
        this.subjectRI = subjectRI;
        this.lastObserverState = new State(username, "");
        this.gui = gui;
    }

    public ObserverImpl(String username, ObserverGuiClient observer, String[] args) throws RemoteException {
        this.username= username;
        this.gui= observer;

        export();

        String registryIP = args[0];
        String registryPort = args[1];
        String serviceName = args[2];
        //Create a context for RMI setup
        contextRMI = new SetupContextRMI(this.getClass(), registryIP, registryPort, new String[]{serviceName});

        this.subjectRI = (SubjectRI)lookupService();

        this.subjectRI.attach(this);
    }

    public ObserverImpl(String username, SubjectRI subjectRI, State lastObserverState, ObserverGuiClient gui, SetupContextRMI contextRMI) throws RemoteException {
        this.username = username;
        this.subjectRI = subjectRI;
        this.lastObserverState = lastObserverState;
        this.gui = gui;
        this.contextRMI = contextRMI;
    }
    
    public  void export() throws RemoteException {
        UnicastRemoteObject.exportObject(this,0);
    }

    private Remote lookupService() {
        try {
            //Get proxy to rmiregistry
            Registry registry = contextRMI.getRegistry();
            //Lookup service on rmiregistry and wait for calls
            if (registry != null) {
                //Get service url (including servicename)
                String serviceUrl = contextRMI.getServicesUrl(0);
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "going to lookup service @ {0}", serviceUrl);

                //============ Get proxy to HelloWorld service ============
                subjectRI = (SubjectRI) registry.lookup(serviceUrl);
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "registry not bound (check IPs). :(");
                //registry = LocateRegistry.createRegistry(1099);
            }
        } catch (RemoteException | NotBoundException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
        return subjectRI;
    }

    public State getLastObserverState() {
        return lastObserverState;
    }

    public void setLastObserverState(State lastObserverState) {
        this.lastObserverState = lastObserverState;
    }

    @Override
    //vai buscar o ultimo estado
    public void update() throws RemoteException{
        this.lastObserverState= subjectRI.getState();
        //this.gui.updateTextArea();
    }

}
