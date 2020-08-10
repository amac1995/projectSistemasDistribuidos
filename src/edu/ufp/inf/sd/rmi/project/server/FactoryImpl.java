package edu.ufp.inf.sd.rmi.project.server;

import org.json.JSONObject;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Base64;
import java.util.HashMap;

//factory - produz objetos que sao sessoes de trabalho
//esta classe cria sessoes de trabalho
public class FactoryImpl extends UnicastRemoteObject implements FactoryRI {


    //DBMockup db = DBMockup.getInstance();
    DBMockup db;
    private String subjectState;
    //private ThreadPool pool;// = new ThreadPool(10);
    //private ArrayList<ObserverRI> observers = new ArrayList<>();
    private HashMap<String, SessionRI> sessions;// = new HashMap();
    //private HashMap<User, SessionRI> sessions = new HashMap<>();// = new HashMap();

    public FactoryImpl() throws RemoteException {
        super();
        db = DBMockup.getInstance();
        //pool = new ThreadPool(10);
        sessions = new HashMap();
    }

    @Override
    //faz o registo do user
    public boolean register(String jwtToken) throws RemoteException {
        Base64.Decoder decoder = Base64.getUrlDecoder(); //metodo que descofdifca a hash
        String[] parts = jwtToken.split("\\."); // split out the "parts" (header, payload(onde esta o nome e pass) and signature)
        String headerJson = new String(decoder.decode(parts[0]));
        String payloadJson = new String(decoder.decode(parts[1]));  //guarda payload numa string
        JSONObject jsonObject = new JSONObject(payloadJson);    //transforma em json, tira nome e pass de la
        String uname = jsonObject.getString("iss"); //busca nome
        String pw = jsonObject.getString("sub");    //busca pass
        User user = db.exists(uname, pw);   //ve na bd se o user existe
        if (user == null) {     //se nao existir cria registo novo
            db.register(uname, pw);
            return true;
        }
        return false;
    }


    @Override

    public SessionRI login(String jwtToken) throws RemoteException {
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String[] parts = jwtToken.split("\\."); // split out the "parts" (header, payload and signature)
        String headerJson = new String(decoder.decode(parts[0]));
        String payloadJson = new String(decoder.decode(parts[1]));
        JSONObject jsonObject = new JSONObject(payloadJson);
        String uname = jsonObject.getString("iss");
        String pw = jsonObject.getString("sub");
        User user = db.exists(uname, pw);
        if (user != null) {         //se existir faz login
            System.out.println("Login");
            if (!sessions.containsKey(uname)) {
                return new SessionImpl(db, user);
            } else {
                return sessions.get(uname);     //devolve sessao de trabalho do user
            }
        }
        return null;
    }

    @Override
    public boolean alive() throws RemoteException {
        return true;
    }
}
