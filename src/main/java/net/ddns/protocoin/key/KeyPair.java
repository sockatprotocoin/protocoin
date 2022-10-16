package net.ddns.protocoin.key;

import net.ddns.protocoin.ecdsa.Curve;
import net.ddns.protocoin.ecdsa.ECPoint;
import net.ddns.protocoin.util.Base58Check;
import net.ddns.protocoin.util.Hash;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class KeyPair {
    private final BigInteger privateKey;
    private final ECPoint publicKey;
    private final String wallet;

    public KeyPair(Curve curve) throws NoSuchAlgorithmException, NoSuchProviderException {
        this(curve.privateKey(), curve);
    }

    public KeyPair(BigInteger privateKey, Curve curve) throws NoSuchProviderException, NoSuchAlgorithmException {
        this.privateKey = privateKey;
        this.publicKey = curve.publicKey(privateKey);
        this.wallet = generateWallet(publicKey.toByteArray());
    }

    private String generateWallet(byte[] publickeyBytes) throws NoSuchAlgorithmException, NoSuchProviderException {
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
