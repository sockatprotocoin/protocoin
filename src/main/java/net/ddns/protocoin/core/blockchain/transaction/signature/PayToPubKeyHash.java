package net.ddns.protocoin.core.blockchain.transaction.signature;

import net.ddns.protocoin.core.util.ArrayUtil;
import net.ddns.protocoin.core.util.Hash;

import java.util.Arrays;

import static net.ddns.protocoin.core.script.OpCode.*;

public class PayToPubKeyHash extends LockingScript {
    private final byte[] pubKeyHash;

    private PayToPubKeyHash(byte[] script, byte[] pubKeyHash) {
        super(script);
        this.pubKeyHash = pubKeyHash;
    }

    public static PayToPubKeyHash fromPublicKey(byte[] publicKey) {
        var pubKeyHash = Hash.ripeMD160(Hash.sha256(publicKey));
        return new PayToPubKeyHash(generateScript(pubKeyHash), pubKeyHash);
    }

    public static PayToPubKeyHash loadScript(byte[] script) {
        var pubKeyHash = Arrays.copyOfRange(script, 2, 22);
        return new PayToPubKeyHash(script, pubKeyHash);
    }

    private static byte[] generateScript(byte[] publicKeyHash) {
        return ArrayUtil.concat(
                new byte[]{OP_DUP.getOpCode(), OP_HASH160.getOpCode()},
                publicKeyHash,
                new byte[]{OP_EQUALVERIFY.getOpCode(), OP_CHECKSIG.getOpCode()}
        );
    }

    @Override
    public byte[] getReceiver() {
        return pubKeyHash;
    }

    @Override
    public byte[] getBytes() {
        return getScript();
    }
}
