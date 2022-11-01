package net.ddns.protocoin.core.script;

import java.util.Arrays;

public enum OpCode {
    OP_DUP((byte) 0x01),
    OP_HASH160((byte) 0x02),
    OP_EQUALVERIFY((byte) 0x03),
    OP_CHECKSIG((byte) 0x05),
    OP_PUSHDATA((byte) 0x14);

    private final byte opCode;

    OpCode(byte opCode) {
        this.opCode = opCode;
    }

    public byte getOpCode() {
        return opCode;
    }

    public static OpCode getOperationFromOpCode(byte op) {
        var opCode = Arrays.stream(OpCode.values()).filter(opcode -> opcode.getOpCode() == op).findFirst();

        if (opCode.isPresent()) {
            return opCode.get();
        } else {
            throw new IllegalArgumentException("Cannot find opcode: " + op);
        }
    }
}
