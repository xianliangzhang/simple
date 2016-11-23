package com.willer.rmi.skeleton;

import com.willer.common.ConfigHelper;
import org.apache.log4j.Logger;

import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;

/**
 * Created by Hack on 2016/11/22.
 */
public class RemoteRepertory {
    private static final Logger RUN_LOG = Logger.getLogger(RemoteRepertory.class);
    private static final String RMI_SERVER_PORT = ConfigHelper.get("rmi.server.port");
    private static final String RMI_PREFIX = "rmi://localhost:".concat(RMI_SERVER_PORT).concat("/");

    static {
        try {
            LocateRegistry.createRegistry(Integer.valueOf(RMI_SERVER_PORT));
        } catch (Exception e) {
            RUN_LOG.error(e.getMessage(), e);
        }
    }

    public static boolean register(String key, Remote remoteBean) {
        try {
            Naming.bind(RMI_PREFIX.concat(key), remoteBean);
        } catch (Exception e) {
            RUN_LOG.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    public static Remote lookup(String key) {
        try {
            return Naming.lookup(RMI_PREFIX.concat(key));
        } catch (Exception e) {
            RUN_LOG.error(e.getMessage(), e);
        }
        return null;
    }
}
