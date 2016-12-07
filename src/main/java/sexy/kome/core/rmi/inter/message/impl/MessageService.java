package sexy.kome.core.rmi.inter.message.impl;

import sexy.kome.core.rmi.inter.message.IMessageService;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Hack on 2016/11/22.
 */
public class MessageService extends UnicastRemoteObject implements IMessageService {
    private static final Map<String, Serializable> MESSAGE_REPERTORY = new HashMap<String, Serializable>();

    public MessageService() throws RemoteException {

    }

    @Override
    public void put(String key, Serializable value) throws RemoteException {
        MESSAGE_REPERTORY.put(key, value);
    }

    @Override
    public Object get(String key) throws RemoteException {
        return MESSAGE_REPERTORY.get(key);
    }
}
