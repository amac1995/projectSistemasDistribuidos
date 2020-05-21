package edu.ufp.inf.sd.rmi.project.server;

import edu.ufp.inf.sd.rmi.project.client.Client;
import edu.ufp.inf.sd.rmi.project.client.ObserverRI;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;

public class FactoryImpl extends UnicastRemoteObject implements FactoryRI {

    private DBMockup db;// = new DBMockup();
    //private ThreadPool pool;// = new ThreadPool(10);
    private HashMap<String, SessionRI> sessions;// = new HashMap();
    private State subjectState;

    private ArrayList<Client> clients = new ArrayList<>();
    /**
     * Uses RMI-default sockets-based transport. Runs forever (do not
     * passivates) hence, does not need rmid (activation deamon) Constructor
     * must throw RemoteException due to super() export().
     */
    public FactoryImpl() throws RemoteException {
        // Invokes UnicastRemoteObject constructor which exports remote object
        super();
        db = new DBMockup();
        //pool = new ThreadPool(10);
        sessions = new HashMap();
    }

    @Override
    public boolean register(String uname, String pw) throws RemoteException {
        if(!db.exists(uname,pw)){
            db.register(uname,pw);
            return true;
        }
        return false;
    }


    @Override
    public SessionRI login(String uname, String pw) throws RemoteException {
        if (db.exists(uname, pw)) {
            System.out.println("Login");
            if(!this.sessions.containsKey(uname)){
                return new SessionImpl(db, db.getUser(uname, pw));
            } else {
                return this.sessions.get(uname);
            }
        }
        return null;
    }

}
