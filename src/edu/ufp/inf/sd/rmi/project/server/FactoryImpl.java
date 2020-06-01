package edu.ufp.inf.sd.rmi.project.server;

import edu.ufp.inf.sd.rmi.project.client.Client;
import edu.ufp.inf.sd.rmi.project.client.ObserverRI;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;

public class FactoryImpl extends UnicastRemoteObject implements FactoryRI {


    DBMockup db = DBMockup.getInstance();
    private String subjectState;
    //private ThreadPool pool;// = new ThreadPool(10);
    private ArrayList<ObserverRI> observers = new ArrayList<>();
    //private HashMap<User, SessionRI> sessions = new HashMap<>();// = new HashMap();

    public FactoryImpl() throws RemoteException {
        super();
        //pool = new ThreadPool(10);
        //sessions = new HashMap();
    }

    @Override
    public boolean register(String uname, String pw) throws RemoteException {
        User user = db.exists(uname, pw);
        if (user == null) {
            db.register(uname, pw);
            return true;
        }
        return false;
    }


    @Override
    public SessionRI login(String uname, String pw) throws RemoteException {
        User user = db.exists(uname, pw);
        if (user != null) {
            System.out.println("Login");
            if (!this.db.getSessions().containsKey(user)) {
                return new SessionImpl(user);
            } else {
                return this.db.getSessions().get(user);
            }
        }
        return null;
    }
}
