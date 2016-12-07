package sexy.kome.core.rmi.inter.transfer;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by Hack on 2016/11/24.
 */
public interface ITransferService extends Remote {
    void transfer(byte[] bytes, int size) throws RemoteException;
}
