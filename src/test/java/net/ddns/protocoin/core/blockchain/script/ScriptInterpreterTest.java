package net.ddns.protocoin.core.blockchain.script;

import net.ddns.protocoin.core.blockchain.Blockchain;
import net.ddns.protocoin.core.blockchain.data.Satoshi;
import net.ddns.protocoin.core.blockchain.transaction.Transaction;
import net.ddns.protocoin.core.blockchain.transaction.TransactionInput;
import net.ddns.protocoin.core.blockchain.transaction.TransactionOutput;
import net.ddns.protocoin.core.blockchain.transaction.signature.PayToPubKeyHash;
import net.ddns.protocoin.core.blockchain.transaction.signature.ScriptSignature;
import net.ddns.protocoin.core.ecdsa.Curve;
import net.ddns.protocoin.core.script.ScriptInterpreter;
import net.ddns.protocoin.core.util.ArrayUtil;
import net.ddns.protocoin.core.util.Hash;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Collections;

public class ScriptInterpreterTest {
    private ScriptInterpreter scriptInterpreter;

    @BeforeEach
    void setup(){
        this.scriptInterpreter = new ScriptInterpreter(Curve.secp256k1);
    }

    @Test
    void shouldReturnTrueForValidTransaction() {
        String publicKeyHex1 = "661BA57FED0D115222E30FE7E9509325EE30E7E284D3641E6FB5E67368C2DB185ADA8EFC5DC43AF6BF474A41ED6237573DC4ED693D49102C42FFC88510500799";
        String publicKeyHex2 = "6E17F61F5A05F10A29CF5ECB638069F9A0DB0F97753D046EBB6EA41055FB5316E7FE027B9934A6A31C65E95142BA2678E9045A46F18B12DC8000F73DECCF22B7";
        BigInteger privateKeyMock1 = new BigInteger("D12D2FACA9AD92828D89683778CB8DFCCDBD6C9E92F6AB7D6065E8AACC1FF6D6", 16);
        var publicKey1 = new BigInteger(publicKeyHex1, 16).toByteArray();
        var publicKey2 = new BigInteger(publicKeyHex2, 16).toByteArray();

        var blockchain = new Blockchain(publicKey1);
        var topBlock = blockchain.getTopBlock();

        var transactionInput = new TransactionInput(
                topBlock.getTransactions().get(0).getTxId(),
                ArrayUtil.newByteArrayPaddedWithZeros(4, BigInteger.ZERO.toByteArray())
        );
        var transactionOutput = new TransactionOutput(
                Satoshi.valueOf(50.0),
                PayToPubKeyHash.fromPublicKey(publicKey2)
        );
        var transaction = new Transaction(
                Collections.singletonList(transactionInput),
                Collections.singletonList(transactionOutput)
        );
        var transactionHash = Hash.sha256(transaction.getBytesWithoutSignatures());
        var signature = Curve.secp256k1.sign(privateKeyMock1, transactionHash);

        transactionInput.setScriptSignature(new ScriptSignature(signature.getBytes(), publicKey1));

        Assertions.assertTrue(
            scriptInterpreter.verify(topBlock.getTransactions().get(0).getTransactionOutputs().get(0).getLockingScript(),
                    transactionHash,transactionInput.getScriptSignature())
        );
    }
}
