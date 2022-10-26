package net.ddns.protocoin.communication.connection.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.ddns.protocoin.communication.data.ReqType;

public abstract class Request<T> {
    protected String content;
    private ReqType reqType;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public TypeReference<T> payloadType() {
        return new TypeReference<>() {};
    }

    public T getPayloadObject() throws JsonProcessingException {
        return objectMapper.readValue(this.content, payloadType());
    }

    public byte[] getPayloadBytes() throws JsonProcessingException {
        return objectMapper.writeValueAsBytes(this);
    }

    public abstract void handle();
}
