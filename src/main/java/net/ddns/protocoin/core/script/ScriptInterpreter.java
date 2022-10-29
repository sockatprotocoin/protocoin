package net.ddns.protocoin.core.script;

import net.ddns.protocoin.core.blockchain.transaction.signature.LockingScript;
import net.ddns.protocoin.core.blockchain.transaction.signature.ScriptSignature;
import net.ddns.protocoin.core.ecdsa.Curve;
import net.ddns.protocoin.core.ecdsa.ECPoint;
import net.ddns.protocoin.core.ecdsa.Signature;
import net.ddns.protocoin.core.util.Hash;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Stack;

public class ScriptInterpreter {
    private final Curve curve;

    public ScriptInterpreter(Curve curve) {
        this.curve = curve;
    }

    public boolean verify(LockingScript lockingScript, byte[] transactionHash, ScriptSignature scriptSignature) {
        try {
            var stack = parse(lockingScript.getScript(),transactionHash,
                    scriptSignature.getSignature().getBytes(), scriptSignature.getPublicKey().getBytes());
            if(stack.size() != 1){
                return false;
            }
            return Arrays.equals(stack.get(0), new byte[]{1});
        } catch (IOException | ScriptException e) {
            return false;
        }
    }

    public Stack<byte[]> parse(byte[] script, byte[] transactionBytes, byte[]... inputData) throws IOException, ScriptException {
        Stack<byte[]> stack = new Stack<>();

        for (byte[] data : inputData) {
            stack.push(data);
        }

        var scriptStream = new ByteArrayInputStream(script);
        while (scriptStream.available() > 0) {
            var b = scriptStream.read();

            var op = OpCode.getOperationFromOpCode((byte) b);
            switch (op) {
                case OP_PUSHDATA:
                    stack.push(scriptStream.readNBytes(OpCode.OP_PUSHDATA.getOpCode()));
                    break;
                case OP_DUP:
                    stack.push(stack.peek());
                    break;
                case OP_HASH160:
                    stack.push(Hash.ripeMD160(Hash.sha256(stack.pop())));
                    break;
                case OP_EQUALVERIFY:
                    var data1 = stack.pop();
                    var data2 = stack.pop();
                    if(Arrays.compare(data1, data2) != 0){
                        throw new ScriptException();
                    }
                    break;
                case OP_CHECKSIG:
                    var publicKey = stack.pop();
                    var signature = stack.pop();
                    var valid = curve.verify(
                            new Signature(
                                    new BigInteger(Arrays.copyOfRange(signature, 0, 32)),
                                    new BigInteger(Arrays.copyOfRange(signature, 32, 64))
                            ),
                            new ECPoint(
                                    new BigInteger(Arrays.copyOfRange(publicKey, 0, 32)),
                                    new BigInteger(Arrays.copyOfRange(publicKey, 32, 64))
                            ),
                            transactionBytes
                    );
                    stack.push(valid ? new byte[]{1} : new byte[]{0});
            }
        }

        return stack;
    }
}
