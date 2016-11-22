package com.willer.rmi;

import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.ObjID;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Hack on 2016/11/22.
 */
public class Test {

    static interface IHello extends Remote {
        void put(String key, Object value) throws RemoteException;
        Object get(String key) throws RemoteException;
    }

    static class HelloImpl extends UnicastRemoteObject implements IHello {
        private Map<String, Object> REPERTORY = new HashMap<String, Object>();
        public HelloImpl() throws RemoteException {

        }

        @Override
        public void put(String key, Object value) throws RemoteException {
            REPERTORY.put(key, value);
            System.out.println("Put Request: " + key);
        }

        @Override
        public Object get(String key) throws RemoteException {
            System.out.println("Get Request: " + key);
            return REPERTORY.get(key);
        }
    }

    static class Server extends Thread {
        public void run() {
            try {
                IHello hello = new HelloImpl();
                LocateRegistry.createRegistry(8888);
                Naming.bind("rmi://localhost:8888/HelloImpl",hello);
                System.out.println("HelloImpl Bind To localhost:8888");
            } catch (Exception e) {
                ;
            }
        }
    }

    static class Client extends Thread {
        public void run() {
            try {
                IHello hello =(IHello) Naming.lookup("rmi://localhost:8888/HelloImpl");
                String key = "god";
                String value = "GOD IS HERE!";

                hello.put(key, value);
                System.out.println(hello.get(key));
            } catch (Exception e) {
                ;
            }
        }
    }

    public static void main(String[] args) throws Exception {
        new Server().start();
        Thread.sleep(2000);
        new Client().start();
    }
}
