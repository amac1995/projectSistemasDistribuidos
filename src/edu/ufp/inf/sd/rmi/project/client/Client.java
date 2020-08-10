package edu.ufp.inf.sd.rmi.project.client;

import com.rabbitmq.client.*;
import edu.ufp.inf.sd.rmi.project.server.*;
import edu.ufp.inf.sd.rmi.util.rmisetup.SetupContextRMI;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.json.JSONObject;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client extends UnicastRemoteObject {

    private SetupContextRMI contextRMI;
    private FactoryRI factoryRI;
    private SessionRI sessionRI;

    private Integer nThreads = 5;
    ExecutorService pool = Executors.newFixedThreadPool(nThreads);

    Integer currentTaskID = null;
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
                    String jws = Jwts.builder()
                            .setIssuer(user)
                            .setSubject(pass)
                            .claim("name", user)
                            .claim("scope", "admins")
                            .setIssuedAt(Date.from(Instant.ofEpochSecond(1466796822L)))
                            .setExpiration(Date.from(Instant.ofEpochSecond(4622470422L)))
                            .signWith(
                                    Keys.secretKeyFor(SignatureAlgorithm.HS512)
                            )
                            .compact();
                    System.out.println(jws);
                    //sessionRI = this.factoryRI.login(user, pass);
                    sessionRI = this.factoryRI.login(jws);
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
                    jws = Jwts.builder()
                            .setIssuer(user)
                            .setSubject(pass)
                            .claim("name", user)
                            .claim("scope", "admins")
                            .setIssuedAt(Date.from(Instant.ofEpochSecond(1466796822L)))
                            .setExpiration(Date.from(Instant.ofEpochSecond(4622470422L)))
                            .signWith(
                                    Keys.secretKeyFor(SignatureAlgorithm.HS512)
                            )
                            .compact();
                    if (this.factoryRI.register(jws)) {
                        System.out.println("Registo com sucesso");
                    } else {
                        System.out.println("Utilizador já registado, a entrar...");
                    }
                    sessionRI = this.factoryRI.login(jws);
                    if (sessionRI != null) {
                        System.out.println("Login com sucesso");
                    } else {
                        System.out.println("Login sem sucesso");
                        System.exit(0);
                    }
                    break;
                default:
                    System.out.println("Unexpected value: " + opt);
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
                                "7 -> Limpar\n" +
                                "8 -> Check threads\n");
                int input = Integer.parseInt(System.console().readLine());
                switch (input) {
                    case 1: //"1 -> Criar tarefa\n"
                        try {
                            System.out.println("Insira o numero de creditos, nome e a hash:");
                            int credit = Integer.parseInt(System.console().readLine());
                            String name = System.console().readLine();
                            String hash = System.console().readLine();
                            sessionRI.createTaskGroup(credit, name, hash);

                            //subjectRI.attach(this);
                            System.out.println("Tarefa criada");
                        } catch (NumberFormatException e) {
                            System.out.println("Apenas pode introduzir digitos");
                        }
                        break;
                    case 2: //"2 -> Juntar tarefa\n"
                        currentTaskID = Integer.parseInt(System.console().readLine());
                        //List<String> subHash = sessionRI.joinTaskGroup(taskID, nThreads);
                        try {
                            String securePass = sessionRI.joinTaskGroup(currentTaskID, nThreads);
                            receivePub(String.valueOf(currentTaskID));
                            for (int i = 0; i <= nThreads; i++) {
                                Worker worker = new Worker(String.valueOf(currentTaskID), securePass, this);
                                System.out.println("Created worked: " + i);
                                //this.future = pool.submit(worker);
                                pool.execute(worker);
                            }
                        } catch (CustomException e) {
                            currentTaskID=null;
                            System.out.println("A tarefa escolhida já foi concluida!");
                        } catch (Exception e) {
                            e.printStackTrace();
                        } catch (NoClassDefFoundError e) {
                            e.printStackTrace();
                        }
                        break;
                    case 3: //"2 -> Listar tarefa a trabalhar\n"
                        try {
                            getCurrentTask().printTaskInfo();
                        } catch (NullPointerException e) {
                            System.out.println("Não está inscrito a nenhuma tarefa.");
                        }
                        break;
                    case 4: //"3 -> Listar tarefas\n"
                        printTaskGroup(sessionRI.getTaskGroups());
                        break;
                    case 5: //"4 -> Pausar tarefa\n"
                        try {
                            getCurrentTask().pauseTask();
                        } catch (NullPointerException e) {
                            System.out.println("Não está inscrito a nenhuma tarefa.");
                        }
                        break;
                    case 6: //"5 -> Apagar tarefas\n"
                        System.out.println("Insira o id da tarefa a eliminar");
                        Integer taskid = Integer.parseInt(System.console().readLine());
                        System.out.println(sessionRI.deleteTaskGroup(taskid) ? "Tarefa apagada" : "Erro a apagar a tarefa");
                        break;
                    case 7: //"5 -> Apagar tarefas\n"
                        System.out.print("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
                        break;
                    case 8: //"5 -> Apagar tarefas\n"

                        break;
                    default:
                        System.out.println("Unexpected value: " + input);
                }

            }
            //Logger.getLogger(this.getClass().getName()).log(Level.INFO, "going to finish, bye. ;)");
        } catch (RemoteException | IllegalArgumentException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    public TaskGroupRI getCurrentTask() {
        HashMap<User, ArrayList<TaskGroupRI>> taskgroups = null;
        try {
            taskgroups = sessionRI.getTaskGroups();
            for (Map.Entry<User, ArrayList<TaskGroupRI>> entry : taskgroups.entrySet()) {
                for (TaskGroupRI taskGroupRI : taskgroups.get(entry.getKey())) {
                    for (SessionRI session : taskGroupRI.getUserInTask()) {
                        if (this.sessionRI.getMyUser().getUsername().equals(session.getMyUser().getUsername())) {
                            System.out.println("Encontrei a tarefa!!!!!!!!!!!!");
                            return taskGroupRI;
                        }
                    }

                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void printTaskGroup(HashMap<User, ArrayList<TaskGroupRI>> taskGroup) {
        try {
            for (User user : taskGroup.keySet()) {
                System.out.println("[" + user.getUsername() + "]" + ":\n ");
                if (!taskGroup.get(user).isEmpty()) {
                    for (TaskGroupRI task : taskGroup.get(user)) {
                        task.printTaskInfo();

                    }
                } else {
                    System.out.println("Sem tarefas.");
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void answerFromThread(String msg) {
        try {
            System.out.println("[Sou client] -> " + msg);
            //getCurrentTask().finishTask();
            sessionRI.stopTask(currentTaskID);
            currentTaskID = null;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void stopThreads() {
        //future.cancel(true);
        pool.shutdown();
        try {
            if (!pool.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                pool.shutdownNow();
            }
        } catch (InterruptedException e) {
            pool.shutdownNow();
        }
        System.out.println("Os threads terminaram? -> " + pool.isTerminated());
        System.out.println("Os threads desligaram? -> " + pool.isShutdown());
        pool=null;
        pool = Executors.newFixedThreadPool(nThreads);
    }

    public void receivePub(String taskID) {
        new Thread(new Runnable() {
            public void run() {
                String channelName = new String(taskID.concat("update"));
                ConnectionFactory factory = new ConnectionFactory();
                factory.setHost("localhost");
                Connection connection = null;
                try {
                    connection = factory.newConnection();
                    connection.addShutdownListener(new ShutdownListener() {
                        public void shutdownCompleted(ShutdownSignalException cause) {
                            System.out.println(cause);
                            Thread.currentThread().interrupt();
                        }
                    });
                    Channel channel = connection.createChannel();
                    channel.exchangeDeclare(channelName, "fanout");
                    String queueName = channel.queueDeclare().getQueue();
                    channel.queueBind(queueName, channelName, "");
                    DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                        String message = new String(delivery.getBody(), "UTF-8");
                        JSONObject jsonObject = new JSONObject(message);
                        System.out.println("Mensagem de :" + jsonObject.getString("operation") + " recebida.");
                        switch (jsonObject.getString("operation")) {
                            case "info":
                                System.out.println("Mensagem recebida: " + jsonObject.get("values"));
                                break;
                            case "pause":
                                System.out.println("Mensagem de pausa: " + jsonObject.get("values"));
                                break;
                            case "resume":
                                System.out.println("Mensagem de resumo: " + jsonObject.get("values"));
                                break;
                            case "stop":
                                stopThreads();
                                Thread.currentThread().interrupt();
                                break;
                            default:
                                throw new IllegalStateException("Unexpected value: " + jsonObject.getString("operation"));
                        }
                        ;
                    };
                    channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}