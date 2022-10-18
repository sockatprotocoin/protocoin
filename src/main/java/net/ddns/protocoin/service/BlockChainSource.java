package net.ddns.protocoin.service;

import java.io.IOException;
import java.io.InputStream;

public interface BlockChainSource {
    InputStream getBlockChainInputStream() throws IOException;
}
