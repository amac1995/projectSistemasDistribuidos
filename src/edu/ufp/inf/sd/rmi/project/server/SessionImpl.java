package edu.ufp.inf.sd.rmi.project.server;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

//Sessão de trabalho
public class SessionImpl implements SessionRI, Serializable{
    User myUser;
    DBMockup db = DBMockup.getInstance();

    public SessionImpl(User user) {
        this.myUser = user;
    }


    @Override
    public void listTaskGroups() throws RemoteException {
        HashMap<User, ArrayList<SubjectRI>> taskHashMap = db.returnTaskList();
        for (User user: taskHashMap.keySet()){
            System.out.println("[" + user.getUsername() +"]" + ":\n ");
            if (!taskHashMap.get(user).isEmpty()){
                for (SubjectRI task: taskHashMap.get(user)) {
                    task.printTaskInfo();
                }
            } else {
                System.out.println("Sem tarefas.");
            }
        }
    }

    @Override
    public SubjectRI createTaskGroup(Integer credits, String name, String hash) throws RemoteException {
        try {
            //SubjectRI subjectRI = new SubjectImpl(credits, name, hash);
            SubjectRI subjectRI = db.saveTask(myUser, credits, name, hash);
            System.out.println("New Task Created\n");
            return subjectRI;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }
    @Override
    public SubjectRI joinTaskGroup(Integer taskID) throws RemoteException{
        HashMap<User, ArrayList<SubjectRI>> taskHashMap = db.returnTaskList();
        for (User user: taskHashMap.keySet()){
            if (!taskHashMap.get(user).isEmpty()){
                for(SubjectRI subjectRI:taskHashMap.get(user)){
                    if(subjectRI.getTask().getTaskID().equals(taskID)){
                        return subjectRI;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public boolean deleteTaskGroup(Integer taskID) throws RemoteException {
        HashMap<User, ArrayList<SubjectRI>> taskHashMap = db.returnTaskList();
        for (User user: taskHashMap.keySet()){
            if (!taskHashMap.get(user).isEmpty()){
                return taskHashMap.get(user).removeIf(task -> task.getTask().getTaskID().equals(taskID));
            }
        }
        return false;
    }

    @Override
    public void logout() throws RemoteException {

    }

    public DBMockup getDb() {
        return db;
    }

    public void setDb(DBMockup db) {
        this.db = db;
    }
}
