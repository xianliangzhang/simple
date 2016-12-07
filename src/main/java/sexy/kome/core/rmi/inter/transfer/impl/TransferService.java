package sexy.kome.core.rmi.inter.transfer.impl;

import sexy.kome.core.rmi.inter.transfer.ITransferService;

import java.io.File;
import java.io.FileOutputStream;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by Hack on 2016/11/24.
 */
public class TransferService extends UnicastRemoteObject implements Remote, ITransferService {
    private static final String TEMP_STORAGE = "d:\\lab\\temp\\";
    static {
        if (!new File(TEMP_STORAGE).exists()) {
            new File(TEMP_STORAGE).mkdirs();
        }
    }

    public TransferService() throws RemoteException {
    }

    @Override
    public void transfer(byte[] bytes, int size) throws RemoteException {
        try {
            new FileOutputStream(TEMP_STORAGE.concat("tt.exe"), true).write(bytes, 0, size);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
