package edu.ufp.inf.sd.rmi.project.client;

import edu.ufp.inf.sd.rmi.project.server.*;
import edu.ufp.inf.sd.rmi.util.rmisetup.SetupContextRMI;

import javax.sound.midi.Soundbank;
import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {

    private SetupContextRMI contextRMI;
    private State lastObserverState;
    private FactoryRI factoryRI;
    ArrayList<Task> taskArrayList = new ArrayList<>();

    public static void main(String[] args) {
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

    public Client(String[] args) {
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
                factoryRI = (FactoryRI) registry.lookup(serviceUrl);
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
            Scanner scanner = new Scanner(System.in);
            System.out.println("Insira nome de utilizador e password");
            String user = scanner.nextLine();
            String pass = scanner.nextLine();
            SessionRI sessionRI = this.factoryRI.login(user, pass);
            if (sessionRI != null){
                System.out.println("Login com sucesso");
            } else {
                System.out.println("Login sem sucesso");
                System.exit(0);
            }
            //Observer attach
            this.factoryRI.attach(this);
            this.update();
            //sessionRI.createTaskGroup(40, "1234567890");
            //taskArrayList = sessionRI.listTaskGroups();
            while(true){
                System.out.println(
                        "Escolha a opção:\n" +
                        "1 -> Criar tarefa\n" +
                        "2 -> Listar tarefas\n" +
                        "3 -> Apagar tarefas\n");
                int input = scanner.nextInt();
                switch (input){
                    case 1:
                        System.out.println("Insira o numero de creditos e a hash:");
                        int credit = scanner.nextInt();
                        String hash = scanner.nextLine();
                        sessionRI.createTaskGroup(credit, hash);
                        taskArrayList = sessionRI.listTaskGroups();
                        System.out.println("Tarefa criada");
                        break;
                    case 2:
                        for (Task task: taskArrayList) {
                            System.out.println("Tarefa numero: " + task.getTaskID() + " equivale a " + task.getCreditos() + "\nUtilizadores:\n");
                            for (User u: task.getUsers()){
                                System.out.println("Nome: " + u.getUsername() + " com " + u.getCredits() + " creditos e " + u.getTaskscompleted() + " tarefas completas.");
                            }
                        }
                        break;
                    case 3:
                        System.out.println("Insira o id da tarefa a eliminar");
                        Integer taskid = scanner.nextInt();
                        sessionRI.deleteTaskGroup(taskid);
                        System.out.println(sessionRI.deleteTaskGroup(taskid)?"Erro a apagar a tarefa":"Tarefa apagada");
                        break;
                    case 4:
                        this.update();
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

    public void update() throws RemoteException{
        this.lastObserverState=factoryRI.getState();
        taskArrayList = this.lastObserverState.getArrayList();
        //this.chatFrame.updateTextArea();
    }

    protected State getLastObserverState(){
        return this.lastObserverState;
    }
}
