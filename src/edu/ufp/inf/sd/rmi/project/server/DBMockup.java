package edu.ufp.inf.sd.rmi.project.server;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class DBMockup implements Serializable{

    private static DBMockup dbMockup = null;

    private final ArrayList<User> users = new ArrayList<>();;// = new ArrayList();
    private HashMap<User, SessionRI> sessions = new HashMap<>();// = new HashMap();
    private HashMap<User, ArrayList<TaskGroupRI>> taskHashMap = new HashMap<>();// = new ArrayList();
    int BDID;

    public DBMockup() {
        Random rand = new Random();
        this.BDID = rand.nextInt(1000);
        users.add(new User("alex", "alex"));
        users.add(new User("andre", "andre"));
        users.add(new User("barbara", "barbara"));
        ArrayList<TaskGroupRI> taskArray = new ArrayList<>();
        try {
            taskArray.add(new TaskGroupImpl(50, "Tarefa1", "133177412dm057", this));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        this.taskHashMap.put(getUser("alex", "alex"), taskArray);
    }

    public synchronized static DBMockup getInstance() {
        if (dbMockup == null)
            dbMockup = new DBMockup();
        return dbMockup;
    }

    public void register(String u, String p) {
        if (exists(u, p) == null) {
            users.add(new User(u, p));
            exists(u, p);
        }
    }

    public User exists(String u, String p) {
        for (User usr : this.users) {
            if (usr.getUsername().compareTo(u) == 0 && usr.getPassword().compareTo(p) == 0) {
                return usr;
            }
        }
        return null;
    }

    public User getUser(String u, String p) {
        for (User usr : this.users) {
            if (usr.getUsername().compareTo(u) == 0 && usr.getPassword().compareTo(p) == 0) {
                return usr;
            }
        }
        return null;
    }

    public TaskGroupRI saveTask(User user, Integer credits, String name, String hash) {
        System.out.println("BD ID -> " + System.identityHashCode(this));
        TaskGroupRI taskGroupRI = null;
        try {
            if (this.taskHashMap.containsKey(user)) {
                taskGroupRI = new TaskGroupImpl(credits, name, hash, this);
                this.taskHashMap.get(user).add(taskGroupRI);
            } else if (!(this.taskHashMap.containsKey(user))) {
                ArrayList<TaskGroupRI> taskArray = new ArrayList<>();
                taskGroupRI = new TaskGroupImpl(credits, name, hash, this);
                taskArray.add(taskGroupRI);
                this.taskHashMap.put(user, taskArray);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return taskGroupRI;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setTaskHashMap(HashMap<User, ArrayList<TaskGroupRI>> taskHashMap) {
        this.taskHashMap = taskHashMap;
    }

    public HashMap<User, ArrayList<TaskGroupRI>> returnTaskList() {
        HashMap<User, ArrayList<TaskGroupRI>> hashMap = this.taskHashMap;
        return hashMap;
    }

    public int getBDID() {
        return BDID;
    }

}
