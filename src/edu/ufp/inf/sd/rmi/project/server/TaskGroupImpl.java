package edu.ufp.inf.sd.rmi.project.server;

import com.lambdaworks.crypto.SCryptUtil;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeoutException;

//Tarefa
public class TaskGroupImpl implements TaskGroupRI, Serializable {

    private Task task;
    DBMockup db;

    private List<SessionRI> userInTask = new ArrayList<>();

    protected TaskGroupImpl(Integer credits, String name, String hash, DBMockup db) throws RemoteException {
        super();
        this.task = new Task(name, credits, hash);
        this.db = db;
    }

    public boolean checkHash(String attempt) {
        return SCryptUtil.check(attempt, task.getSecurePassword());
    }

    @Override
    public void printTaskInfo() throws RemoteException {
        if (this.task != null) {
            System.out.println("Tarefa ID: " + task.getTaskID() +
                    "\n\tNome: " + task.getName() +
                    "\n\tCreditos: " + task.getCreditos() +
                    "\n\tEm pausa: " + task.getPause().toString() +
                    "\n\tUtilizadores nesta tarefa: " + userInTask.toString() +
                    "\n\tCompleta: " + task.done);
        }
    }

    @Override
    public void finishTask() throws RemoteException {
        System.out.println("A terminar tarefa!");
        task.setDone(true);
        userInTask.clear();
        try {
            sendUpdate(String.valueOf(this.task.taskID), "stop");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pauseTask() throws RemoteException {

        try {
            if (this.task.getPause()) {
                sendUpdate(String.valueOf(this.task.taskID), "resume");
                this.task.setPause(false);
            } else {
                sendUpdate(String.valueOf(this.task.taskID), "pause");
                this.task.setPause(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean giveUpTask() throws RemoteException {
        return false;
    }

    @Override
    public String joinTask(SessionRI session) throws RemoteException {
        userInTask.add(session);
        return task.getSecurePassword();
    }

    public void remUserToList(SessionRI user) {
        userInTask.remove(user);
    }

    public List<SessionRI> getUserToList() {
        return userInTask;
    }

    @Override
    public Task getTask() {
        return task;
    }

    @Override
    public List<SessionRI> getUserInTask() throws RemoteException {
        return userInTask;
    }

    public void setUserInTask(List<SessionRI> userInTask) {
        this.userInTask = userInTask;
    }

    public void sendUpdate(String taskID, String op) throws Exception {
//        new Thread(new Runnable() {
//            public void run() {
        String channelName = new String(taskID.concat("update"));
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        System.out.println("Channel [" + channelName + "].Mensagem a enviar: " + op);
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            connection.addShutdownListener(new ShutdownListener() {
                public void shutdownCompleted(ShutdownSignalException cause)
                {
                    System.out.println(cause);
                    return;
                }
            });
            channel.exchangeDeclare(channelName, "fanout");
            String message = "{\"operation\":\"" + op + "\", \"values\":" + taskID + "}";
            channel.basicPublish(channelName, "", null, message.getBytes("UTF-8"));
            System.out.println(" [x] Sent '" + message + "'");
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
//            }
//        }).start();
    }

    @Override
    public String toString() {
        return "TaskGroupImpl{" +
                "taskName=" + task.getName() +
                ", taskID=" + task.getTaskID() +
                '}';
    }
}