package net.ddns.protocoin.service.database;

import net.ddns.protocoin.core.blockchain.data.Bytes;
import net.ddns.protocoin.core.blockchain.transaction.Transaction;
import net.ddns.protocoin.core.blockchain.transaction.TransactionInput;
import net.ddns.protocoin.core.blockchain.transaction.TransactionOutput;
import net.ddns.protocoin.core.blockchain.transaction.signature.LockingScript;
import net.ddns.protocoin.core.blockchain.transaction.signature.ScriptSignature;
import net.ddns.protocoin.core.script.ScriptInterpreter;
import net.ddns.protocoin.core.util.Converter;
import net.ddns.protocoin.core.util.Hash;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UTXOStorageTest {
    private UTXOStorage utxoStorage;

    private TransactionOutput transactionOutputMock1;
    private byte[] publicKeyHashBytes1;

    private TransactionOutput transactionOutputMock2;
    private Bytes publicKeyBytes2;
    private byte[] publicKeyHashBytes2;
    private byte[] txid2;

    @BeforeEach
    void setup(){
        var scriptInterpreter = mock(ScriptInterpreter.class);
        utxoStorage = new UTXOStorage(scriptInterpreter);

        transactionOutputMock1 = mock(TransactionOutput.class);
        var lockingScriptMock1 = mock(LockingScript.class);
        var publicKeyBytes1 = Bytes.of(Converter.hexStringToByteArray("01".repeat(64)), 64);
        publicKeyHashBytes1 = Hash.ripeMD160(Hash.sha256(publicKeyBytes1.getBytes()));
        var parentMock1 = mock(Transaction.class);
        byte[] txid1 = new byte[]{0};
        when(transactionOutputMock1.getLockingScript()).thenReturn(lockingScriptMock1);
        when(transactionOutputMock1.getVout()).thenReturn(Bytes.of(new byte[1], 1));
        when(lockingScriptMock1.getReceiver()).thenReturn(publicKeyHashBytes1);
        when(transactionOutputMock1.getParent()).thenReturn(parentMock1);
        when(parentMock1.getTxId()).thenReturn(txid1);

        transactionOutputMock2 = mock(TransactionOutput.class);
        var lockingScriptMock2 = mock(LockingScript.class);
        publicKeyBytes2 = Bytes.of(Converter.hexStringToByteArray("10".repeat(64)), 64);
        publicKeyHashBytes2 = Hash.ripeMD160(Hash.sha256(publicKeyBytes2.getBytes()));
        var parentMock2 = mock(Transaction.class);
        txid2 = new byte[]{1};
        when(transactionOutputMock2.getLockingScript()).thenReturn(lockingScriptMock2);
        when(transactionOutputMock2.getVout()).thenReturn(Bytes.of(new byte[1], 1));
        when(lockingScriptMock2.getReceiver()).thenReturn(publicKeyHashBytes2);
        when(transactionOutputMock2.getParent()).thenReturn(parentMock2);
        when(parentMock2.getTxId()).thenReturn(txid2);
    }

    @Test
    void shouldRegisterOutput() {
        // when:
        utxoStorage.addUnspentTransactionOutput(transactionOutputMock1);
        utxoStorage.addUnspentTransactionOutput(transactionOutputMock2);

        // then:
        assertEquals(1, utxoStorage.getUTXOs(publicKeyHashBytes1).size());
        assertEquals(transactionOutputMock1, utxoStorage.getUTXOs(publicKeyHashBytes1).get(0));
        assertEquals(1, utxoStorage.getUTXOs(publicKeyHashBytes2).size());
        assertEquals(transactionOutputMock2, utxoStorage.getUTXOs(publicKeyHashBytes2).get(0));
    }

    @Test
    void shouldSpendOutput() {
        // given:
        var transactionInputMock = mock(TransactionInput.class);
        var scriptMock = mock(ScriptSignature.class);
        when(transactionInputMock.getScriptSignature()).thenReturn(scriptMock);
        when(transactionInputMock.getVout()).thenReturn(Bytes.of(new byte[1], 1));
        when(scriptMock.getPublicKey()).thenReturn(publicKeyBytes2);
        when(transactionInputMock.getTxid()).thenReturn(Bytes.of(txid2, txid2.length));

        utxoStorage.addUnspentTransactionOutput(transactionOutputMock1);
        utxoStorage.addUnspentTransactionOutput(transactionOutputMock2);
        utxoStorage.spendTransactionOutput(transactionInputMock);

        // then:
        assertEquals(1, utxoStorage.getUTXOs(publicKeyHashBytes1).size());
        assertEquals(transactionOutputMock1, utxoStorage.getUTXOs(publicKeyHashBytes1).get(0));
        assertEquals(0, utxoStorage.getUTXOs(publicKeyHashBytes2).size());
    }

    @Test
    void shouldNotSpendOutputWhenDifferentPublicKey() {
        // given:
        var transactionInputMock = mock(TransactionInput.class);
        var scriptMock = mock(ScriptSignature.class);
        when(transactionInputMock.getScriptSignature()).thenReturn(scriptMock);
        when(transactionInputMock.getVout()).thenReturn(Bytes.of(new byte[1], 1));
        when(scriptMock.getPublicKey()).thenReturn(Bytes.of(new byte[64], 64));
        when(transactionInputMock.getTxid()).thenReturn(Bytes.of(txid2, txid2.length));

        utxoStorage.addUnspentTransactionOutput(transactionOutputMock1);
        utxoStorage.addUnspentTransactionOutput(transactionOutputMock2);
        utxoStorage.spendTransactionOutput(transactionInputMock);

        // then:
        assertEquals(1, utxoStorage.getUTXOs(publicKeyHashBytes1).size());
        assertEquals(1, utxoStorage.getUTXOs(publicKeyHashBytes2).size());
    }

    @Test
    void shouldNotSpendOutputWhenDifferentTxid() {
        // given:
        var transactionInputMock = mock(TransactionInput.class);
        var scriptMock = mock(ScriptSignature.class);
        when(transactionInputMock.getScriptSignature()).thenReturn(scriptMock);
        when(transactionInputMock.getVout()).thenReturn(Bytes.of(new byte[1], 1));
        when(scriptMock.getPublicKey()).thenReturn(publicKeyBytes2);
        when(transactionInputMock.getTxid()).thenReturn(Bytes.of(new byte[0], 0));

        utxoStorage.addUnspentTransactionOutput(transactionOutputMock1);
        utxoStorage.addUnspentTransactionOutput(transactionOutputMock2);
        utxoStorage.spendTransactionOutput(transactionInputMock);

        // then:
        assertEquals(1, utxoStorage.getUTXOs(publicKeyHashBytes1).size());
        assertEquals(1, utxoStorage.getUTXOs(publicKeyHashBytes2).size());
    }

    @Test
    void shouldNotSpendOutputWhenDifferentVout() {
        // given:
        var transactionInputMock = mock(TransactionInput.class);
        var scriptMock = mock(ScriptSignature.class);
        when(transactionInputMock.getScriptSignature()).thenReturn(scriptMock);
        when(transactionInputMock.getVout()).thenReturn(Bytes.of(new byte[]{1}, 1));
        when(scriptMock.getPublicKey()).thenReturn(publicKeyBytes2);
        when(transactionInputMock.getTxid()).thenReturn(Bytes.of(txid2, txid2.length));

        utxoStorage.addUnspentTransactionOutput(transactionOutputMock1);
        utxoStorage.addUnspentTransactionOutput(transactionOutputMock2);
        utxoStorage.spendTransactionOutput(transactionInputMock);

        // then:
        assertEquals(1, utxoStorage.getUTXOs(publicKeyHashBytes1).size());
        assertEquals(1, utxoStorage.getUTXOs(publicKeyHashBytes2).size());
    }
}