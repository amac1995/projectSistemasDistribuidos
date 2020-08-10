package edu.ufp.inf.sd.rmi.project.server;

import com.rabbitmq.client.*;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeoutException;

//Sessão de trabalho, onde tem todos os metodos do protocolo
public class SessionImpl extends UnicastRemoteObject implements SessionRI, Serializable {
    User myUser;
    DBMockup db;

    public SessionImpl(DBMockup db, User user) throws RemoteException {
        super();
        this.db = db;
        this.myUser = user;
    }

    @Override
    public HashMap<User, ArrayList<TaskGroupRI>> getTaskGroups() throws RemoteException {
        HashMap<User, ArrayList<TaskGroupRI>> hashMap = db.returnTaskList();
        return hashMap;

    }

    @Override
    //cria tarefas
    //vai a bd, procura tarefa q o user quer, procura tarefa taskid e cria tarefa e executa newtask q da trabalho aos clientes
    public TaskGroupRI createTaskGroup(Integer credits, String name, String hash) throws RemoteException {
        try {
            TaskGroupRI taskGroupRI = db.saveTask(myUser, credits, name, hash);
            System.out.println("New Task Created\n");
            try {
                newTask(String.valueOf(taskGroupRI.getTask().taskID), taskGroupRI.getTask().listSubHash);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return taskGroupRI;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    //junta o worker a tarefa
    //vai a bd, procura tarefa q o user quer, procura tarefa taskid e junta-se a tarefa mas lança uma excecao se a tarefa ja tiver terminado
    //custom exception, emvez de imprimir erro diz a terminou
    public String joinTaskGroup(Integer taskID, Integer nThreads) throws RemoteException, CustomException {
        HashMap<User, ArrayList<TaskGroupRI>> taskHashMap = db.returnTaskList();
        for (User user : taskHashMap.keySet()) {
            if (!taskHashMap.get(user).isEmpty()) {
                for (TaskGroupRI taskGroupRI : taskHashMap.get(user)) {
                    if (taskGroupRI.getTask().getTaskID().equals(taskID)) {
                        if (taskGroupRI.getTask().getDone()) {
                            throw new CustomException("Task already completed.");
                        }
                        taskGroupRI.joinTask(this);
                        System.out.println("[User] -> " + user.getUsername() + " juntou-se a tarefa " + taskID + "\n");
                        return taskGroupRI.getTask().getSecurePassword();
                    }
                }
            }
        }
        return null;
    }

    @Override
    //para tarefa
    //vai a bd, procura tarefa q o user quer, procura tarefa taskid e termina
    public boolean stopTask(Integer taskID) throws RemoteException {
        HashMap<User, ArrayList<TaskGroupRI>> taskHashMap = db.returnTaskList();
        for (User user : taskHashMap.keySet()) {
            if (!taskHashMap.get(user).isEmpty()) {
                for (TaskGroupRI taskGroupRI : taskHashMap.get(user)) {
                    if (taskGroupRI.getTask().getTaskID().equals(taskID)) {
                        taskGroupRI.finishTask();
                    }
                }
            }
        }
        return false;
    }

    @Override
    //apaga
    //vai a bd, procura tarefa q o user quer, procura tarefa taskid e apaga
    public boolean deleteTaskGroup(Integer taskID) throws RemoteException {
        HashMap<User, ArrayList<TaskGroupRI>> taskHashMap = db.returnTaskList();
        for (User user : taskHashMap.keySet()) {
            if (!taskHashMap.get(user).isEmpty()) {
                    return taskHashMap.get(user).removeIf(task -> task.getTask().getTaskID().equals(taskID));
            }
        }
        return false;
    }

    @Override
    public void logout() throws RemoteException {

    }

    @Override
    public DBMockup getDb() throws RemoteException {
        return db;
    }

    @Override
    public User getMyUser() throws RemoteException {
        return myUser;
    }

    public void setMyUser(User myUser) {
        this.myUser = myUser;
    }


    //cria tarefa nova, abre o canal(workqueu no rabbit) envia todas as palavas do darkc0de para os clientes
    //assim os clientes tentam encontrar a pass orginal, dá trabalho aos clientes
    public void newTask(String taskID, List<List<String>> subHashs) throws Exception {
        new Thread(new Runnable() {
            public void run() {
                ConnectionFactory factory = new ConnectionFactory();
                factory.setHost("localhost");
                System.out.println("Channel [" + taskID + "] a enviar as Hash");

                //se um cliente fechar o canal o servidor fecha esse canal para todos os clientes
                try (Connection connection = factory.newConnection()) {
                    connection.addShutdownListener(new ShutdownListener() {
                        public void shutdownCompleted(ShutdownSignalException cause) {
                            System.out.println(cause);
                            Thread.currentThread().interrupt();
                        }
                    });
                    Channel channel = connection.createChannel();
                    channel.queueDeclare(taskID, true, false, false, null);
                    for (List<String> stringList : subHashs) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("values", stringList);
                        channel.basicPublish("", taskID,
                                MessageProperties.PERSISTENT_TEXT_PLAIN,
                                jsonObject.toString().getBytes("UTF-8"));
                    }
                } catch (IOException | TimeoutException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    @Override
    public String toString() {
        return "SessionImpl{" +
                "User=" + myUser.toString() +
                '}';
    }
}
