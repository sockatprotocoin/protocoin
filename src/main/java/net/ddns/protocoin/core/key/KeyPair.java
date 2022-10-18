package net.ddns.protocoin.core.key;

import net.ddns.protocoin.core.ecdsa.Curve;
import net.ddns.protocoin.core.ecdsa.ECPoint;
import net.ddns.protocoin.core.util.Base58Check;
import net.ddns.protocoin.core.util.Hash;

import java.math.BigInteger;

public class KeyPair {
    private final BigInteger privateKey;
    private final ECPoint publicKey;
    private final String wallet;

    public KeyPair(Curve curve) {
        this(curve.privateKey(), curve);
    }

    public KeyPair(BigInteger privateKey, Curve curve) {
        this.privateKey = privateKey;
        this.publicKey = curve.publicKey(privateKey);
        this.wallet = generateWallet(publicKey.toByteArray());
    }

    private String generateWallet(byte[] publickeyBytes) {
        return Base58Check.encode(Hash.ripeMD160(Hash.sha256(publickeyBytes)));
    }

    public BigInteger getPrivateKey() {
        return privateKey;
    }

    public ECPoint getPublicKey() {
        return publicKey;
    }

    public String getWallet() {
        return wallet;
    }
}
