package sexy.kome.core.rmi.inter.message.model;

import sexy.kome.core.helper.ConfigHelper;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by Hack on 2016/11/22.
 */
public class GodMessage implements Serializable {
    private String messageID;
    private String message;

    public GodMessage(String message) {
        this.messageID = UUID.randomUUID().toString();
        this.message = ConfigHelper.get("rmi.test.message.prefix").concat("-").concat(message);
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
