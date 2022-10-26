package net.ddns.protocoin.communication.connection;

import java.io.IOException;

public interface DataMiddleware<T, R> {
    R handle(T t) throws IOException;
}
