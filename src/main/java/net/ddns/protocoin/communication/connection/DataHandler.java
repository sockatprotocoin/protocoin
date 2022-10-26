package net.ddns.protocoin.communication.connection;

public interface DataHandler<T> {
    void handleData(T t);
    boolean connectionOpened();
}
