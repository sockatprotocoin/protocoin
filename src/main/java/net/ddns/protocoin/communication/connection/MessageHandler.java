package net.ddns.protocoin.communication.connection;

import net.ddns.protocoin.communication.data.Message;

public class MessageHandler implements DataHandler<Message> {
    private boolean receivedCloseConMessage = false;

    @Override
    public void handleData(Message message) {
//        if (message.getReqType().equals(ReqType.CLOSE_CONNECTION)) {
//            this.receivedCloseConMessage = true;
//        }
    }

    @Override
    public boolean connectionOpened() {
        return !receivedCloseConMessage;
    }
}
