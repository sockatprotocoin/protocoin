package net.ddns.protocoin.core.ecdsa;

import net.ddns.protocoin.core.util.RandomGenerator;

import java.math.BigInteger;

public class Curve {
    private final BigInteger a;
    private final BigInteger b;
    private final BigInteger primeNumber;
    private final BigInteger order;
    private final ECPoint g;

    public Curve(BigInteger a, BigInteger b, BigInteger primeNumber, BigInteger order, ECPoint g) {
        this.a = a;
        this.b = b;
        this.primeNumber = primeNumber;
        this.order = order;
        this.g = g;
    }

    public static final Curve secp256k1 = new Curve(
            BigInteger.ZERO,
            BigInteger.valueOf(7),
            new BigInteger("fffffffffffffffffffffffffffffffffffffffffffffffffffffffefffffc2f", 16),
            new BigInteger("fffffffffffffffffffffffffffffffebaaedce6af48a03bbfd25e8cd0364141", 16),
            new ECPoint(
                    new BigInteger("79be667ef9dcbbac55a06295ce870b07029bfcdb2dce28d959f2815b16f81798", 16),
                    new BigInteger("483ada7726a3c4655da4fbfc0e1108a8fd17b448a68554199c47d08ffb10d4b8", 16)
            )
    );

    public BigInteger inversion(BigInteger x, BigInteger n) {
        if (x.compareTo(BigInteger.ZERO) == 0) {
            return BigInteger.ZERO;
        }
        var lm = BigInteger.ONE;
        var hm = BigInteger.ZERO;
        var high = n;
        var low = x.mod(n);
        BigInteger r, nm, nw;

        while (low.compareTo(BigInteger.ONE) > 0) {
            r = high.divide(low);
            nm = hm.subtract(lm.multiply(r));
            nw = high.subtract(low.multiply(r));
            high = low;
            hm = lm;
            low = nw;
            lm = nm;
        }
        return lm.mod(n);
    }

    public ECPoint add(ECPoint point1, ECPoint point2) {
        var lambda = point2.getY().subtract(point1.getY()).multiply(inversion(point2.getX().subtract(point1.getX()), primeNumber)).mod(primeNumber);
        var x = lambda.pow(2).subtract(point1.getX()).subtract(point2.getX()).mod(primeNumber);
        var y = lambda.multiply(point1.getX().subtract(x)).subtract(point1.getY()).mod(primeNumber);

        return new ECPoint(x, y);
    }

    public ECPoint doublePoint(ECPoint point) {
        var two = BigInteger.valueOf(2);
        var three = BigInteger.valueOf(3);

        var lambda = point.getX().pow(2).multiply(three).add(a).multiply(inversion(two.multiply(point.getY()), primeNumber)).mod(primeNumber);
        var x = lambda.pow(2).subtract(point.getX().multiply(two)).mod(primeNumber);
        var y = lambda.multiply(point.getX().subtract(x)).subtract(point.getY()).mod(primeNumber);

        return new ECPoint(x, y);
    }

    public ECPoint multiply(ECPoint point, BigInteger scalar) {
        var bitsLength = scalar.bitLength();
        var newPoint = point;

        for (int i = bitsLength - 2; i >= 0; i--) {
            newPoint = doublePoint(newPoint);
            if (scalar.testBit(i)) {
                newPoint = add(newPoint, point);
            }
        }

        return newPoint;
    }

    public BigInteger privateKey() {
        var random = new RandomGenerator();
        BigInteger privateKey;

        do {
            privateKey = new BigInteger(1, random.bytes(32));
        } while (
                privateKey.compareTo(BigInteger.ZERO) == 0 ||
                privateKey.compareTo(primeNumber) == 1
        );

        return privateKey;
    }

    public ECPoint publicKey(BigInteger privateKey) {
        return multiply(g, privateKey);
    }

    public Signature sign(BigInteger privateKey, byte[] data) {
//        var random = privateKey();
        var random = BigInteger.ONE;

        var randomPoint = publicKey(random);
        var r = randomPoint.getX().mod(order);
        var s = ((new BigInteger(data).add(r.multiply(privateKey))).multiply(inversion(random, order))).mod(order);

        return new Signature(r, s);
    }

    public boolean verify(Signature signature, ECPoint publicKey, byte[] data) {
        var w = inversion(signature.getS(), order);
        var u1 = publicKey(new BigInteger(data).multiply(w).mod(order));
        var u2 = multiply(publicKey, signature.getR().multiply(w).mod(order));
        var point = add(u1, u2);

        return signature.getR().compareTo(point.getX().mod(order)) == 0;
    }

    public ECPoint getG() {
        return g;
    }
}
