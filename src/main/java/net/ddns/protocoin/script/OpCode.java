package net.ddns.protocoin.script;

import java.util.Arrays;

public enum OpCode {
    OP_DUP(0x01),
    OP_HASH160(0x02),
    OP_EQUALVERIFY(0x03),
    OP_CHECKSIG(0x05),
    OP_PUSHDATA(0x41);

    private final int opCode;

    OpCode(int opCode) {
        this.opCode = opCode;
    }

    public int getOpCode() {
        return opCode;
    }

    public static OpCode getOperationFromOpCode(int op) {
        var opCode = Arrays.stream(OpCode.values()).filter(opcode -> opcode.getOpCode() == op).findFirst();

        if (opCode.isPresent()) {
            return opCode.get();
        } else {
            throw new IllegalArgumentException("Cannot find opcode: " + op);
        }
    }
}
