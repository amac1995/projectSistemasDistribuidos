package edu.ufp.inf.sd.rmi.project.server;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class DBMockup implements Serializable {

    private static DBMockup dbMockup = new DBMockup();

    private final ArrayList<User> users = new ArrayList<>();;// = new ArrayList();
    private HashMap<User, SessionRI> sessions = new HashMap<>();// = new HashMap();
    private HashMap<User, ArrayList<SubjectRI>> taskHashMap = new HashMap<>();// = new ArrayList();
    int BDID;

    public DBMockup() {
        Random rand = new Random();
        this.BDID = rand.nextInt(1000);
        users.add(new User("alex", "alex"));
        users.add(new User("andre", "andre"));
        users.add(new User("barbara", "barbara"));
        ArrayList<SubjectRI> taskArray = new ArrayList<>();
        try {
            taskArray.add(new SubjectImpl(50, "Tarefa1", "huidhhdf"));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        this.taskHashMap.put(getUser("alex", "alex"), taskArray);
    }

    public static DBMockup getInstance() {
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

    public void saveTask(User user, SubjectRI subjectRI) {
        if (this.taskHashMap.containsKey(user)) {
            this.taskHashMap.get(user).add(subjectRI);
        } else if (!(this.taskHashMap.containsKey(user))) {
            ArrayList<SubjectRI> taskArray = new ArrayList<>();
            taskArray.add(subjectRI);
            this.taskHashMap.put(user, taskArray);
        }
    }

    public SubjectRI saveTask(User user, Integer credits, String name, String hash) {
        System.out.println("BD ID -> " + System.identityHashCode(this));
        SubjectRI subjectRI = null;
        try {
            if (this.taskHashMap.containsKey(user)) {
                subjectRI = new SubjectImpl(credits, name, hash);
                this.taskHashMap.get(user).add(subjectRI);
            } else if (!(this.taskHashMap.containsKey(user))) {
                ArrayList<SubjectRI> taskArray = new ArrayList<>();
                subjectRI = new SubjectImpl(credits, name, hash);
                taskArray.add(subjectRI);
                this.taskHashMap.put(user, taskArray);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return subjectRI;
    }

    public void dumpDatabase() {
        System.out.println("ID: " + getBDID());
        System.out.println("[Users]\n");
        for (User user : users) {
            System.out.println("Nome: " + user.getUsername() + " creditos: " + user.getCredits());
        }

        System.out.println("[Sessions]\n");
        for (User user : sessions.keySet()) {
            System.out.println("User: " + user.getUsername() + " session: " + sessions.get(user));
        }

        System.out.println("[TaskGroups]\n");
        for (User user : taskHashMap.keySet()) {
            System.out.println("User: " + user.getUsername());
            for (SubjectRI subjectRI : taskHashMap.get(user)) {
                System.out.println("Task " + subjectRI.getTask().getTaskID() + " nome " + subjectRI.getTask().getName());
            }
        }

    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public HashMap<User, SessionRI> getSessions() {
        return sessions;
    }

    public void setSessions(HashMap<User, SessionRI> sessions) {
        this.sessions = sessions;
    }

    public HashMap<User, ArrayList<SubjectRI>> getTaskHashMap() {
        return taskHashMap;
    }

    public HashMap<User, ArrayList<SubjectRI>> returnTaskList() {
        return this.taskHashMap;
    }

    public int getBDID() {
        return BDID;
    }

    public void setBDID(int BDID) {
        this.BDID = BDID;
    }

}
