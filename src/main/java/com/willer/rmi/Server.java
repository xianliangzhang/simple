package com.willer.rmi;

import com.willer.rmi.inter.message.impl.MessageService;
import com.willer.rmi.inter.transfer.impl.TransferService;
import com.willer.rmi.skeleton.ServerRepertory;
import org.apache.log4j.Logger;

/**
 * Created by Hack on 2016/11/23.
 */
public class Server {
    private static final Logger RUN_LOG = Logger.getLogger(Server.class);

    public static void main(String[] args) throws Exception {
        ServerRepertory.register("MessageService", new MessageService());
        RUN_LOG.info("MessageService Deployed!");

        ServerRepertory.register("TransferService", new TransferService());
        RUN_LOG.info("TransferService Deployed!");
    }
}
