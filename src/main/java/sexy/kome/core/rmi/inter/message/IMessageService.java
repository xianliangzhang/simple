package sexy.kome.core.rmi.inter.message;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by Hack on 2016/11/24.
 */
public interface IMessageService extends Remote {
    void put(String key, Serializable value) throws RemoteException;
    Object get(String key) throws RemoteException;
}