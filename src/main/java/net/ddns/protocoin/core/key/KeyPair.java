package net.ddns.protocoin.core.key;

import net.ddns.protocoin.core.ecdsa.Curve;
import net.ddns.protocoin.core.ecdsa.ECPoint;
import net.ddns.protocoin.core.util.Base58Check;
import net.ddns.protocoin.core.util.Hash;

import java.math.BigInteger;
import java.util.Arrays;

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
        var hash160 = Hash.ripeMD160(Hash.sha256(publickeyBytes));
        var checksum = Arrays.copyOfRange (Hash.sha256(hash160,2),0,3);
        var hash160WithChecksum = new byte[hash160.length + checksum.length];
        System.arraycopy(hash160,0,hash160WithChecksum,0,hash160.length);
        System.arraycopy(checksum,0,hash160WithChecksum,hash160.length,checksum.length);
        return Base58Check.encode(hash160WithChecksum);
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
