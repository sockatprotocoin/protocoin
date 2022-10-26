package net.ddns.protocoin.communication.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.ddns.protocoin.core.blockchain.block.Block;

import java.util.function.Function;

public enum ReqType {
    ASK_FOR_CONNECTED_NODES(null);
//    RETURN_CONNECTED_NODES(new TypeReference<List<InetAddress>>(){}),
//    HANDLE_TRANSACTION(new TypeReference<Transaction>(){}),
//    REGISTER_NEW_BLOCK(new TypeReference<Block>(){}),
//    CLOSE_CONNECTION(null);

//    private final TypeReference type;

//    private final Function<String, T> f;
//
//    <T> ReqType(Function<String, T> f) {
//        this.f = f;
//    }

//    ReqType() {
//        var k = Block.class;
//    }


//    ReqType(TypeReference type) {
//        this.type = type;
//    }
//
//    public <T> TypeReference<T> getType() {
//        return type;
//    }
}
