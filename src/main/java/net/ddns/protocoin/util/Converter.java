package net.ddns.protocoin.util;

public class Converter {
    public static String byteArrayToHexString(byte[] bytes) {
        var sb = new StringBuilder();
        for (var b : bytes) {
            var cutToEightBits = 0xff & b;
            var hexRepresentation = Integer.toHexString(cutToEightBits);
            if (hexRepresentation.length() == 1) {
                sb.append('0');
            }
            sb.append(hexRepresentation);
        }

        return sb.toString();
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}
