package net.ddns.protocoin.service.database;

import net.ddns.protocoin.core.blockchain.Blockchain;
import net.ddns.protocoin.core.blockchain.data.Satoshi;
import net.ddns.protocoin.core.blockchain.transaction.Transaction;
import net.ddns.protocoin.core.blockchain.transaction.TransactionInput;
import net.ddns.protocoin.core.blockchain.transaction.TransactionOutput;
import net.ddns.protocoin.core.blockchain.transaction.signature.PayToPubKeyHash;
import net.ddns.protocoin.core.blockchain.transaction.signature.ScriptSignature;
import net.ddns.protocoin.core.ecdsa.Curve;
import net.ddns.protocoin.core.ecdsa.ECPoint;
import net.ddns.protocoin.core.util.ArrayUtil;
import net.ddns.protocoin.core.util.Converter;
import net.ddns.protocoin.core.util.Hash;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UTXOStorageTest {
    private UTXOStorage utxoStorage;

    @BeforeEach
    void setup(){
        utxoStorage = new UTXOStorage();
    }

    @Test
    void shouldAddUnspentTransactionOutput(){
        var publicKeyMock = new ECPoint(new BigInteger("661BA57FED0D115222E30FE7E9509325EE30E7E284D3641E6FB5E67368C2DB18", 16), new BigInteger("5ADA8EFC5DC43AF6BF474A41ED6237573DC4ED693D49102C42FFC88510500799", 16));
        var publicKeyHash = "D0ECF5C0DCF3797201379ED48F2DE37002FE823C"; //Hash.ripeMD160(Hash.sha256(publicKeyMock.toByteArray()));
        var lockingScript = PayToPubKeyHash.fromPublicKey(publicKeyMock.toByteArray());
        var transactionOutput1 = new TransactionOutput(Satoshi.valueOf(10.0),lockingScript);
        var transactionOutput2 = new TransactionOutput(Satoshi.valueOf(20.0),lockingScript);

        var transactionOutputs = new ArrayList<TransactionOutput>();
        transactionOutputs.add(transactionOutput1);
        transactionOutputs.add(transactionOutput2);

        utxoStorage.addUnspentTransactionOutput(transactionOutput1);
        utxoStorage.addUnspentTransactionOutput(transactionOutput2);

        var received = utxoStorage.getUTXOs(Converter.hexStringToByteArray(publicKeyHash));

        assertEquals(2, received.size());
        for(TransactionOutput output : transactionOutputs){
            assertTrue(received.contains(output));
        }
    }

    @Test
    void shouldSpendTransactionOutput() {
        var privateKeyMock1 = new BigInteger("D12D2FACA9AD92828D89683778CB8DFCCDBD6C9E92F6AB7D6065E8AACC1FF6D6", 16);
        var publicKeyHex1 = "661BA57FED0D115222E30FE7E9509325EE30E7E284D3641E6FB5E67368C2DB185ADA8EFC5DC43AF6BF474A41ED6237573DC4ED693D49102C42FFC88510500799";
        var publicKeyHex2 = "6E17F61F5A05F10A29CF5ECB638069F9A0DB0F97753D046EBB6EA41055FB5316E7FE027B9934A6A31C65E95142BA2678E9045A46F18B12DC8000F73DECCF22B7";

        var publicKey1 = new BigInteger(publicKeyHex1, 16).toByteArray();
        var publicKey2 = new BigInteger(publicKeyHex2, 16).toByteArray();

        var publicKey1Hash = Hash.ripeMD160(Hash.sha256(publicKey1));
        var publicKey2Hash = Hash.ripeMD160(Hash.sha256(publicKey2));
        var blockchain = new Blockchain(publicKey1);

        var topBlock = blockchain.getTopBlock();

        var transactionInput = new TransactionInput(
                topBlock.getTransactions().get(0).getTxId(),
                ArrayUtil.newByteArrayPaddedWithZeros(4, BigInteger.ZERO.toByteArray())
        );
        var transactionOutput = new TransactionOutput(
                Satoshi.valueOf(10.0),
                PayToPubKeyHash.fromPublicKey(publicKey2)
        );
        var transaction = new Transaction(
                Collections.singletonList(transactionInput),
                Collections.singletonList(transactionOutput)
        );

        var transactionHash = Hash.sha256(transaction.getBytesWithoutSignatures());
        var signature = Curve.secp256k1.sign(privateKeyMock1, transactionHash);

        transactionInput.setScriptSignature(new ScriptSignature(signature.getBytes(), publicKey1));

        utxoStorage.addUnspentTransactionOutput(topBlock.getTransactions().get(0).getTransactionOutputs().get(0));
        assertEquals(1, utxoStorage.getUTXOs(publicKey1Hash).size());
        assertEquals(0, utxoStorage.getUTXOs(publicKey2Hash).size());

        utxoStorage.spentTransactionOutput(transactionInput);
        assertEquals(0, utxoStorage.getUTXOs(publicKey1Hash).size());
        assertEquals(0, utxoStorage.getUTXOs(publicKey2Hash).size());

        utxoStorage.addUnspentTransactionOutput(transactionOutput);
        assertEquals(0, utxoStorage.getUTXOs(publicKey1Hash).size());
        assertEquals(1, utxoStorage.getUTXOs(publicKey2Hash).size());
    }
}