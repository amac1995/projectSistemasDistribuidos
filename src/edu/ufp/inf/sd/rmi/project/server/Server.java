package edu.ufp.inf.sd.rmi.project.server;

import edu.ufp.inf.sd.rmi.util.rmisetup.SetupContextRMI;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server{

    /**
     * Context for running a RMI Servant on a host
     */

    //cria o servidor RMI
    private SetupContextRMI contextRMI;
    //private DBMockup db;// = new DBMockup();

    public static void main(String[] args) {
        if (args != null && args.length < 3) {
            System.err.println("usage: java [options] edu.ufp.sd.edu.ufp.inf.sd.rmi.helloworld.server.HelloWorldServer <rmi_registry_ip> <rmi_registry_port> <service_name>");
            System.exit(-1);
        } else {
            //1. ============ Create Servant ============
            Server hws = new Server(args);
            //2. ============ Rebind servant on rmiregistry ============
            hws.rebindService();
        }
    }

    /**
     *
     * @param args
     */
    public Server(String[] args) {
        super();
        try {
            //factoryRI= new FactoryImpl(this.db);
            //============ List and Set args ============
            SetupContextRMI.printArgs(this.getClass().getName(), args);
            String registryIP = args[0];
            String registryPort = args[1];
            String serviceName = args[2];
            //============ Create a context for RMI setup ============
            contextRMI = new SetupContextRMI(this.getClass(), registryIP, registryPort, new String[]{serviceName});
        } catch (RemoteException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
        }
    }

    private void rebindService() {
        try {
            //Get proxy to rmiregistry
            Registry registry = contextRMI.getRegistry();
            //Bind service on rmiregistry and wait for calls
            if (registry != null) {
                //============ Create Servant ============
                /**
                 * Remote interface that will hold reference to the Servant impl
                 */
                FactoryRI factoryRI = new FactoryImpl();
                //Get service url (including servicename)
                String serviceUrl = contextRMI.getServicesUrl(0);
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "going to rebind service @ {0}", serviceUrl);

                System.out.println(Arrays.toString(registry.list()));
                try{
                    Remote alive;
                    do {
                        factoryRI = (FactoryRI) registry.lookup(serviceUrl);        //devolve a fabrica para o clinete poder registar ou login
                    } while (factoryRI.alive());
                    System.out.println("Server die or not exists\n");
                } catch (NotBoundException e) {
                    registry.rebind(serviceUrl, factoryRI);
                }
                //============ Rebind servant ============
                //registry.rebind(serviceUrl, factoryRI);
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "service bound and running. :)");
            } else {
                //System.out.println("HelloWorldServer - Constructor(): create registry on port 1099");
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "registry not bound (check IPs). :(");
                //registry = LocateRegistry.createRegistry(1099);
            }
        } catch (RemoteException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

}
