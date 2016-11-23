package com.willer.rmi;

import com.willer.common.ConfigHelper;
import com.willer.rmi.inter.IMessageService;
import com.willer.rmi.message.GodMessage;
import com.willer.rmi.skeleton.ClientRepertory;
import org.apache.log4j.Logger;

/**
 * Created by Hack on 2016/11/23.
 */
public class Client {
    private static final Logger RUN_LOG = Logger.getLogger(Client.class);

    public static void main(String[] args) throws Exception {
        if (args.length > 0) {
            ConfigHelper.load(args[0]);
        }

        IMessageService messageService = (IMessageService) ClientRepertory.lookup("MessageService");
        RUN_LOG.info("MessageService Found!");

        GodMessage message = new GodMessage("Message Send From Client");
        messageService.put(message.getMessageID(), message);
        RUN_LOG.info(String.format("Client Send Message [message=%s]", message.getMessage()));

        GodMessage response = (GodMessage) messageService.get(message.getMessageID());
        RUN_LOG.info(String.format("Client Received Message From Server [message=%s]", response.getMessage()));
    }
}
