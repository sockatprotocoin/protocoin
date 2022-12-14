package net.ddns.protocoin.core.util;

import net.ddns.protocoin.core.blockchain.Bytable;
import net.ddns.protocoin.core.blockchain.data.Bytes;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ArrayUtil {
    public static byte[] concat(Bytes... bytesArray) {
        return concat(Arrays.stream(bytesArray).map(Bytes::getBytes).toArray(byte[][]::new));
    }

    public static byte[] concat(byte[]... arrays) {
        if (arrays.length == 0) {
            return new byte[0];
        }

        byte[] concatenated = arrays[0];
        for (int i = 1; i < arrays.length; i++) {
            var arr = arrays[i];
            byte[] joinedArray = new byte[concatenated.length + arr.length];
            System.arraycopy(concatenated, 0, joinedArray, 0, concatenated.length);
            System.arraycopy(arr, 0, joinedArray, concatenated.length, arr.length);
            concatenated = joinedArray;
        }

        return concatenated;
    }

    public static byte[] bytableListToArray(List<? extends Bytable> bytables) {
        return concat(
                bytables.stream()
                        .map(Bytable::getBytes)
                        .collect(Collectors.toList())
                        .toArray(new byte[0][0])
        );
    }

    public static byte[] newByteArrayPaddedWithZeros(int newArraySize, byte[] value) {
        var array = new byte[newArraySize];
        System.arraycopy(value, 0, array, array.length - value.length, value.length);

        return array;
    }
}
