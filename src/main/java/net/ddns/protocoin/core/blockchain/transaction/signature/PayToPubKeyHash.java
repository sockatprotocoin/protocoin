package net.ddns.protocoin.core.blockchain.transaction.signature;

import net.ddns.protocoin.core.blockchain.data.Bytes;
import net.ddns.protocoin.core.util.ArrayUtil;
import net.ddns.protocoin.core.util.Hash;

import java.util.Arrays;

import static net.ddns.protocoin.core.script.OpCode.*;

public class PayToPubKeyHash extends LockingScript {
    private final Bytes pubKeyHash = new Bytes(20);

    private PayToPubKeyHash(byte[] script, byte[] pubKeyHash) {
        super(script);
        this.pubKeyHash.setData(pubKeyHash);
    }

    public static PayToPubKeyHash fromPublicKey(byte[] publicKey) {
        var pubKeyHash = Hash.ripeMD160(Hash.sha256(publicKey));
        return new PayToPubKeyHash(generateScript(pubKeyHash), pubKeyHash);
    }

    public static PayToPubKeyHash loadScript(byte[] script) {
        var pubKeyHash = Arrays.copyOfRange(script, 3, 23);
        return new PayToPubKeyHash(script, pubKeyHash);
    }

    private static byte[] generateScript(byte[] publicKeyHash) {
        return ArrayUtil.concat(
                new byte[]{OP_DUP.getOpCode(), OP_HASH160.getOpCode(), OP_PUSHDATA.getOpCode()},
                publicKeyHash,
                new byte[]{OP_EQUALVERIFY.getOpCode(), OP_CHECKSIG.getOpCode()}
        );
    }

    @Override
    public byte[] getReceiver() {
        return pubKeyHash.getBytes();
    }

    @Override
    public byte[] getBytes() {
        return getScript();
    }
}
