package edu.ufp.inf.sd.rmi.project.server;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

//base de dados, tem os utilizadores, as sessoes de trabalho e as tarefas
public class DBMockup implements Serializable{

    private static DBMockup dbMockup = null;        //classe estatica

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

    //diz que a classe só pode ser instanciada uma unica vez e é sincronizada com todas as outras classes que a utilizam
    //se tiver 3 servidores e o 1 instanciar esta classe, quando os outros chamarem a mesma eles vao receber exatamente a mesma copia da bd
    //garante que todos tem os mesmos dados, um faz aleracoes e todos vem
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

    //guarda a tarefa na bd
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
