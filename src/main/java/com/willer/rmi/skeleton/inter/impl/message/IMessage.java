package com.willer.rmi.skeleton.inter.impl.message;

import java.util.UUID;

/**
 * Created by Hack on 2016/11/22.
 */
public class IMessage {
    private String messageID;

    public IMessage() {
        this.messageID = UUID.randomUUID().toString();
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }
}
