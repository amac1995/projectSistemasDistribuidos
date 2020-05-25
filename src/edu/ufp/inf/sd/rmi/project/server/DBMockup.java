package edu.ufp.inf.sd.rmi.project.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class simulates a DBMockup for managing users and books.
 *
 * @author rmoreira
 *
 */
public class DBMockup implements Serializable {

    private final ArrayList<User> users;// = new ArrayList();
    private final HashMap<User, ArrayList<SubjectRI>> taskHashMap = new HashMap<>();// = new ArrayList();


    /**
     * This constructor creates and inits the database with some books and users.
     */
    public DBMockup() {
        users = new ArrayList<>();
        users.add(new User("alex", "alex"));
        users.add(new User("andre", "andre"));
        users.add(new User("barbara", "barbara"));
    }

    /**
     * Registers a new user.
     * 
     * @param u username
     * @param p passwd
     */
    public User register(String u, String p) {
        if (exists(u, p)==null) {
            users.add(new User(u, p));
            return exists(u,p);
        }
        return null;
    }

    /**
     * Checks the credentials of an user.
     * 
     * @param u username
     * @param p passwd
     * @return
     */
    public User exists(String u, String p) {
        for (User usr : this.users) {
            if (usr.getUsername().compareTo(u) == 0 && usr.getPassword().compareTo(p) == 0) {
                return usr;
            }
        }
        return null;
    }

    public User getUser(String u, String p){
        for (User usr : this.users) {
            if (usr.getUsername().compareTo(u) == 0 && usr.getPassword().compareTo(p) == 0) {
                return usr;
            }
        }
        return null;
    }

    public void saveTask(User user,SubjectRI subjectRI){
        if (taskHashMap.containsKey(user)){
            taskHashMap.get(user).add(subjectRI);
        }else if (!(taskHashMap.containsKey(user))){
            ArrayList<SubjectRI> taskArray = new ArrayList<>();
            taskArray.add(subjectRI);
            taskHashMap.put(user, taskArray);
        }
    }

    public HashMap<User, ArrayList<SubjectRI>> returnTaskList(){
        return this.taskHashMap;
    }
}
