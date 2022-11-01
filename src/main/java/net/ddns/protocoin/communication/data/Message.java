package net.ddns.protocoin.communication.data;

public class Message {
    private ReqType reqType;
    private byte[] content;

    public Message() {
    }

    public Message(ReqType reqType, byte[] content) {
        this.reqType = reqType;
        this.content = content;
    }

    public ReqType getReqType() {
        return reqType;
    }

    public byte[] getContent() {
        return content;
    }
}
