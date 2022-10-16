package net.ddns.protocoin.blockchain;

import net.ddns.protocoin.blockchain.transaction.Transaction;
import net.ddns.protocoin.blockchain.transaction.TransactionInput;
import net.ddns.protocoin.blockchain.transaction.TransactionOutput;
import net.ddns.protocoin.blockchain.transaction.signature.PayToPubkeyHash;
import net.ddns.protocoin.blockchain.transaction.signature.ScriptSignature;
import net.ddns.protocoin.blockchain.util.Satoshi;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Collections;

class BlockchainTest {
    @Test
    void shouldCreateGenesisBlock() {
        var transactionInput = new TransactionInput(new byte[32], new byte[4], new ScriptSignature(new byte[4]));
        var transactionOutput = new TransactionOutput(
                Satoshi.valueOf(new BigInteger("5000000000", 10)),
                new PayToPubkeyHash(new byte[10])
        );
    }
}