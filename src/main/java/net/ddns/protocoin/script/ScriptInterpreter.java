package net.ddns.protocoin.script;

import java.io.ByteArrayInputStream;
import java.util.Stack;

public class ScriptInterpreter {
//    Stack<Array>

    public ScriptInterpreter(byte[] script) {
        parse(script);
    }

    private void parse(byte[] script) {
        var scriptStream = new ByteArrayInputStream(script);
        while (scriptStream.available() > 0) {
            var b = scriptStream.read();

            var op = OpCode.getOperationFromOpCode(b);

            switch (op) {
                case OP_PUSHDATA:

            }
        }
    }
}
