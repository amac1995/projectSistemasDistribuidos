package edu.ufp.inf.sd.rmi.project.server;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class SessionImpl implements SessionRI, Serializable {
    DBMockup db;
    ArrayList<Task> tasksgroup = new ArrayList<>();
    User user;
    SubjectRI subjectRI;
    public SessionImpl(DBMockup db, User user) {
        this.db = db;
        this.user = user;
    }

    @Override
    public ArrayList<Task> listTaskGroups() throws RemoteException {
        return tasksgroup;
    }

    @Override
    public boolean createTaskGroup(Integer credits, String hash) throws RemoteException {
        Task task = new Task(credits, user);
        tasksgroup.add(task);
        subjectRI.setState(new State(tasksgroup));
        return true;
    }

    @Override
    public boolean pauseTaskGroup(Integer taskID) throws RemoteException {
        //enviar notificação a todos os users que a tarefa está em pausa
        for (Task task : tasksgroup) {
            if (task.getTaskID().equals(taskID)) {
                task.setPause(!task.getPause());
                subjectRI.setState(new State(tasksgroup));
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean deleteTaskGroup(Integer taskID) throws RemoteException {
        //enviar notificação a todos os users que a tarefa irá ser apagada
        for (Task task : tasksgroup) {
            if (task.getTaskID().equals(taskID)) {
                tasksgroup.remove(task);
                subjectRI.setState(new State(tasksgroup));
                return true;
            }
        }
        return false;
    }


    @Override
    public void logout() throws RemoteException {

    }
}
