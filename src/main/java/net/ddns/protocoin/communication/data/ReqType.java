package net.ddns.protocoin.communication.data;

public enum ReqType {
    ASK_FOR_CONNECTED_NODES,
    RETURN_CONNECTED_NODES,
    HANDLE_TRANSACTION,
    REGISTER_NEW_BLOCK,
    CLOSE_CONNECTION;
}
