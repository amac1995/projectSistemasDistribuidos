package edu.ufp.inf.sd.rmi.project.client;

import edu.ufp.inf.sd.rmi.project.server.*;
import edu.ufp.inf.sd.rmi.util.rmisetup.SetupContextRMI;

import javax.sound.midi.Soundbank;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

public class Client extends UnicastRemoteObject implements ObserverRI {

    private SetupContextRMI contextRMI;
    private String lastObserverState;
    private FactoryRI factoryRI;
    private SubjectRI subjectRI;
    private SessionRI sessionRI;
    ArrayList<Task> taskArrayList = new ArrayList<>();

    public static void main(String[] args) throws RemoteException {
        if (args != null && args.length < 2) {
            System.err.println("usage: java [options] edu.ufp.sd.inf.rmi.edu.ufp.inf.sd.rmi.helloworld.server.HelloWorldClient <rmi_registry_ip> <rmi_registry_port> <service_name>");
            System.exit(-1);
        } else {
            //1. ============ Setup client RMI context ============
            Client hwc = new Client(args);
            //2. ============ Lookup service ============
            hwc.lookupService();
            //3. ============ Play with service ============
            hwc.playService();
        }
    }

    public Client(String[] args) throws RemoteException {
        super();
        try {

            //List ans set args
            SetupContextRMI.printArgs(this.getClass().getName(), args);
            String registryIP = args[0];
            String registryPort = args[1];
            String serviceName = args[2];
            //Create a context for RMI setup
            contextRMI = new SetupContextRMI(this.getClass(), registryIP, registryPort, new String[]{serviceName});

        } catch (RemoteException e) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private Remote lookupService() {
        try {
            //Get proxy to rmiregistry
            Registry registry = contextRMI.getRegistry();
            //Lookup service on rmiregistry and wait for calls
            if (registry != null) {
                //Get service url (including servicename)
                String serviceUrl = contextRMI.getServicesUrl(0);
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "going to lookup service @ {0}", serviceUrl);

                //============ Get proxy to HelloWorld service ============
                factoryRI = (FactoryRI) registry.lookup(serviceUrl);//Lookup service
                //subjectRI=(SubjectRI)lookupService();
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "registry not bound (check IPs). :(");
                //registry = LocateRegistry.createRegistry(1099);
            }
        } catch (RemoteException | NotBoundException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
        return factoryRI;
    }

    private void playService() {
        try {
            System.out.println("Login ou registar?");
            int opt = Integer.parseInt(System.console().readLine());
            switch (opt) {
                case 1:
                    System.out.println("Insira nome de utilizador e password");
                    String user = System.console().readLine();
                    String pass = System.console().readLine();
                    sessionRI = this.factoryRI.login(user, pass);
                    if (sessionRI != null) {
                        System.out.println("Login com sucesso");
                    } else {
                        System.out.println("Login sem sucesso");
                        System.exit(0);
                    }
                    break;
                case 2:
                    System.out.println("Insira nome de utilizador e password");
                    user = System.console().readLine();
                    pass = System.console().readLine();
                    if (this.factoryRI.register(user, pass)) {
                        System.out.println("Registo com sucesso");
                        sessionRI = this.factoryRI.login(user, pass);
                        if (sessionRI != null) {
                            System.out.println("Login com sucesso");
                        } else {
                            System.out.println("Login sem sucesso");
                            System.exit(0);
                        }
                    }
            }
            System.out.println("ID base de dados: " + sessionRI.getDb().getBDID());
            while (true) {
                System.out.println(
                        "Escolha a opção:\n" +
                                "1 -> Criar tarefa\n" +
                                "2 -> Juntar a tarefa\n" +
                                "3 -> Listar tarefa a trabalhar\n" +
                                "4 -> Listar tarefas\n" +
                                "5 -> Pausar tarefa\n" +
                                "6 -> Apagar tarefas\n" +
                                "7 -> Dump DB\n");
                int input = Integer.parseInt(System.console().readLine());
                switch (input) {
                    case 1: //"1 -> Criar tarefa\n"
                        if (subjectRI == null) {
                            System.out.println("Insira o numero de creditos, nome e a hash:");
                            int credit = Integer.parseInt(System.console().readLine());
                            String name = System.console().readLine();
                            String hash = System.console().readLine();
                            subjectRI = sessionRI.createTaskGroup(credit, name, hash);

                            subjectRI.attach(this);
                            System.out.println("Tarefa criada");
                        } else
                            System.out.println("[Erro] -> Já está inscrito numa tarefa.");
                        break;
                    case 2: //"3 -> Juntar tarefas\n"
                        int taskID = Integer.parseInt(System.console().readLine());
                        subjectRI = sessionRI.joinTaskGroup(taskID);
                        break;
                    case 3: //"2 -> Listar tarefa a trabalhar\n"
                        if (subjectRI != null) {
                            subjectRI.printTaskInfo();
                        } else {
                            System.out.println("[Erro] -> Não está inscrito numa tarefa.");
                        }
                        break;
                    case 4: //"3 -> Listar tarefas\n"
                        sessionRI.listTaskGroups();
                        break;
                    case 5: //"4 -> Pausar tarefa\n"
                        if (subjectRI != null) {
                            subjectRI.pauseTask();
                        }
                        System.out.println("Não está inscrito em nenhuma tarefa.");
                        break;
                    case 6: //"5 -> Apagar tarefas\n"
                        System.out.println("Insira o id da tarefa a eliminar");
                        Integer taskid = Integer.parseInt(System.console().readLine());
                        subjectRI.detach(this);
                        subjectRI = null;
                        System.out.println(sessionRI.deleteTaskGroup(taskid) ? "Tarefa apagada" : "Erro a apagar a tarefa");
                        break;
                    case 7: //"6 -> Update\n"
                        sessionRI.listTaskGroups();
                        break;
                    default:
                        return;
                }

            }
            //Logger.getLogger(this.getClass().getName()).log(Level.INFO, "going to finish, bye. ;)");
        } catch (RemoteException | IllegalArgumentException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    /*@Override
    public void updateFactory() throws RemoteException {
        String state = factoryRI.getState();
        System.out.println(state);
        try {
            JSONObject jsonObject = new JSONObject(state);
            JSONArray values = (JSONArray) jsonObject.get("values");
            System.out.println("A operação é :" + jsonObject.getString("operation"));
            switch (jsonObject.getString("operation")) {
                case "new" -> sessionRI.createTaskGroup(values.getInt(0), values.getString(1), values.getString(2));
                case "rem" -> sessionRI.deleteTaskGroup(values.getInt(0));
                default -> throw new IllegalStateException("Unexpected value: " + jsonObject.getString("operation"));
            }
            ;
        } catch (RuntimeException e) {
            System.out.println(" [.] " + e.toString());
        }
    }*/

    @Override
    public void updateSubject() throws RemoteException {

    }

    protected String getLastObserverState() {
        return this.lastObserverState;
    }
}
