package net.ddns.protocoin.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class BlockChainFileSource implements BlockChainSource {
    private final String path;

    public BlockChainFileSource(String path) {
        this.path = path;
    }

    @Override
    public InputStream getBlockChainInputStream() throws IOException {
        return new FileInputStream(path);
    }
}
