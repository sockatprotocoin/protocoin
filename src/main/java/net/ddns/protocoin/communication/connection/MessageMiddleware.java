package net.ddns.protocoin.communication.connection;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.ddns.protocoin.communication.data.Message;

import java.io.IOException;
import java.io.InputStream;

public class MessageMiddleware implements DataMiddleware<InputStream, Message> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Message handle(InputStream inputStream) throws IOException {
        var bytes = inputStream.readNBytes(inputStream.available());
        return objectMapper.readValue(bytes, Message.class);
    }
}
