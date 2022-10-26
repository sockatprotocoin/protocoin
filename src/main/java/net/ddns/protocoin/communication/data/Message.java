package net.ddns.protocoin.communication.data;

public class Message {
    private ReqType reqType;
    private String content;

    public Message() {
    }

    public Message(ReqType reqType, String content) {
        this.reqType = reqType;
        this.content = content;
    }

    public ReqType getReqType() {
        return reqType;
    }

    public String getContent() {
        return content;
    }
}
