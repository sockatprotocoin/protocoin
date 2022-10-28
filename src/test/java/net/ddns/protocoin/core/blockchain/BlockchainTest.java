package net.ddns.protocoin.core.blockchain;

import net.ddns.protocoin.core.blockchain.Blockchain;
import net.ddns.protocoin.core.blockchain.block.Block;
import net.ddns.protocoin.core.blockchain.block.BlockHeader;
import net.ddns.protocoin.core.blockchain.data.Satoshi;
import net.ddns.protocoin.core.blockchain.transaction.Transaction;
import net.ddns.protocoin.core.blockchain.transaction.TransactionInput;
import net.ddns.protocoin.core.blockchain.transaction.TransactionOutput;
import net.ddns.protocoin.core.blockchain.transaction.signature.PayToPubKeyHash;
import net.ddns.protocoin.core.blockchain.transaction.signature.ScriptSignature;
import net.ddns.protocoin.core.ecdsa.Curve;
import net.ddns.protocoin.core.util.ArrayUtil;
import net.ddns.protocoin.core.util.Hash;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Collections;

class BlockchainTest {
    private final BigInteger privateKeyMock1 = new BigInteger("D12D2FACA9AD92828D89683778CB8DFCCDBD6C9E92F6AB7D6065E8AACC1FF6D6", 16);
    private final String publicKeyHex1 = "661BA57FED0D115222E30FE7E9509325EE30E7E284D3641E6FB5E67368C2DB185ADA8EFC5DC43AF6BF474A41ED6237573DC4ED693D49102C42FFC88510500799";
    private final BigInteger privateKeyMock2 = new BigInteger("8A5E51BF00D2FBDDD65069966C1EF18C7C99D73A0F6DA8A5E34358C11E01CE4", 16);
    private final String publicKeyHex2 = "6E17F61F5A05F10A29CF5ECB638069F9A0DB0F97753D046EBB6EA41055FB5316E7FE027B9934A6A31C65E95142BA2678E9045A46F18B12DC8000F73DECCF22B7";
    private final BigInteger privateKeyMock3 = new BigInteger("B38549B065C4AC95F4015C272448EF4BEEE88A69AB2E7DB0A3E476D2E95F5B72", 16);
    private final String publicKeyHex3 = "7B16C2323FE33C992E4592651FFBC7CC7E18CD8CAB3E8C3E9E02D4D9A48285EFD7B464ADB705A4FB9C9CBF6941E6740C42E8001D936E9F85951AAE9603E09340";
    private final BigInteger privateKeyMock4 = new BigInteger("919AD6ED16F8AB764F0FBA6DC2EA0564EEC7DCE5B630DE0FD39A7515543CA406", 16);
    private final String publicKeyHex4 = "DD886CF04D637C6E8CBAE17D702AC1FDF07B013C7799EEC30AA5983BD26CD9D526D736527D3609162846236D6A6DA5AB86BABEF9C4309996855C38FA499D4ECD";

    @Test
    void shouldCreateGenesisBlock() {
        var blockchain = new Blockchain(new BigInteger(publicKeyHex1, 16).toByteArray());
        var block = blockchain.getBlock(0);
        block.mine();

        System.out.println();
    }

    @Test
    void idk() throws IOException {
        var publicKey1 = new BigInteger(publicKeyHex1, 16).toByteArray();
        var publicKey2 = new BigInteger(publicKeyHex2, 16).toByteArray();
        var blockchain = new Blockchain(publicKey1);

        var topBlock = blockchain.getTopBlock();

        var header = new BlockHeader(
                topBlock.getHash(),
                new byte[32],
                ByteBuffer.allocate(4).putInt(1666463956).array(),
                new BigInteger("200696f4", 16).toByteArray(),
                new byte[4]
        );
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
        var newBlock = new Block(header, Collections.singletonList(transaction));
        newBlock.mine();
        blockchain.addBlock(
                newBlock
        );

        var blockChainIs = new ByteArrayInputStream(
                ArrayUtil.concat(
                        new BigInteger(blockchain.getBlock(0).getBytes()).toByteArray(),
                        new BigInteger(newBlock.getBytes()).toByteArray()
                )
        );

        var blockChain = Blockchain.readFromInputStream(blockChainIs);

        Assertions.assertArrayEquals(newBlock.getBytes(), blockChain.getBlock(1).getBytes());
    }
}