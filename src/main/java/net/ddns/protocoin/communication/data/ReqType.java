package net.ddns.protocoin.communication.data;

public enum ReqType {
    ASK_FOR_CONNECTED_NODES,
    RETURN_CONNECTED_NODES,
    ASK_FOR_BLOCKCHAIN,
    RETURN_BLOCKCHAIN,
    ASK_FOR_TOP_BLOCK,
    RETURN_TOP_BLOCK,
    HANDLE_TRANSACTION,
    REGISTER_NEW_BLOCK,
    CLOSE_CONNECTION;
}
