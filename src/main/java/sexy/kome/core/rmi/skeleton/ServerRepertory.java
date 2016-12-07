package sexy.kome.core.rmi.skeleton;

import org.apache.log4j.Logger;
import sexy.kome.core.helper.ConfigHelper;

import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Created by Hack on 2016/11/23.
 */
public class ServerRepertory {
    private static final Logger RUN_LOG = Logger.getLogger(ServerRepertory.class);
    private static final String RMI_SERVER_HOST = ConfigHelper.containsKey("rmi.server.host") ? ConfigHelper.get("rmi.server.host") : "localhost";
    private static final String RMI_SERVER_PORT = ConfigHelper.containsKey("rmi.server.port") ? ConfigHelper.get("rmi.server.port") : Registry.REGISTRY_PORT+"";
    private static final String RMI_PREFIX = "rmi://".concat(RMI_SERVER_HOST).concat(":").concat(RMI_SERVER_PORT).concat("/");

    static {
        try {
            LocateRegistry.createRegistry(Integer.valueOf(RMI_SERVER_PORT));
        } catch (Exception e) {
            RUN_LOG.error(e.getMessage(), e);
        }
    }

    private ServerRepertory() {

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
}
