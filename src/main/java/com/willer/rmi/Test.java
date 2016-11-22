package com.willer.rmi;

import com.willer.rmi.skeleton.RemoteRepertory;
import com.willer.rmi.skeleton.inter.impl.MessageService;

import java.io.Serializable;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Hack on 2016/11/22.
 */
public class Test {

    static class God implements Serializable {
        public String name;
        public int age;

        public God(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public String toString() {
            return String.format("God [%s-%s]", name, age);
        }
    }

    interface IHello extends Remote {
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
            System.out.println(String.format(" -- Put Request [key=%s, value=%s]", key, value));
        }

        @Override
        public Object get(String key) throws RemoteException {
            System.out.println(String.format(" -- Get Request [key=%s]", key));
            return REPERTORY.get(key);
        }
    }

    static class Server extends Thread {
        public void run() {
            try {
                IHello hello = new HelloImpl();
                LocateRegistry.createRegistry(8888);
                Naming.bind("rmi://localhost:8888/HelloImpl", hello);
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
                String key = "tom";
                God value = new God("tom", 23);

                hello.put(key, value);
                System.out.println(hello.get(key).toString());
            } catch (Exception e) {
                ;
            }
        }
    }

    static class ServerTest extends Thread {
        public void run() {
            try {
                RemoteRepertory.register("MessageService", new MessageService());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static class ClientTest extends Thread {
        public void run() {
            try {
                RemoteRepertory.lookup("MessageService");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
//        new Server().start();
//        Thread.sleep(2000);
//        new Client().start();

        RemoteRepertory.register("MessageService", new MessageService());

    }
}
