package com.willer.rmi;

import com.willer.common.ConfigHelper;
import com.willer.rmi.inter.impl.MessageService;
import com.willer.rmi.skeleton.ServerRepertory;
import org.apache.log4j.Logger;

/**
 * Created by Hack on 2016/11/23.
 */
public class Server {
    private static final Logger RUN_LOG = Logger.getLogger(Server.class);

    public static void main(String[] args) throws Exception {
        if (args.length > 0) {
            ConfigHelper.load(args[0]);
        }

        ServerRepertory.register("MessageService", new MessageService());
        RUN_LOG.info("MessageService Deployed!");
    }
}
