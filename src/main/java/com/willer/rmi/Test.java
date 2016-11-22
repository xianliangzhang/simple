package com.willer.rmi;

import com.willer.rmi.inter.IMessageService;
import com.willer.rmi.inter.impl.MessageService;
import com.willer.rmi.message.GodMessage;
import com.willer.rmi.skeleton.RemoteRepertory;

/**
 * Created by Hack on 2016/11/22.
 */
public class Test {

    public static void main(String[] args) throws Exception {
        RemoteRepertory.register("MessageService", new MessageService());
        IMessageService messageService = (IMessageService) RemoteRepertory.lookup("MessageService");

        GodMessage message = new GodMessage("Test-Value");
        messageService.put(message.getMessageID(), message);
        GodMessage response = (GodMessage) messageService.get(message.getMessageID());
        System.out.println(response.getMessage());

    }
}
