package net.ddns.protocoin.core.util;

import net.ddns.protocoin.core.blockchain.Bytable;

import java.util.List;
import java.util.stream.Collectors;

public class ArrayUtil {
    public static byte[] concat(byte[]... arrays) {
        if (arrays.length < 1) {
            throw new IllegalArgumentException("Can't provide less than 1 array");
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
                        .map(bytable -> bytable.getBytes())
                        .collect(Collectors.toList())
                        .toArray(new byte[0][0])
        );
    }
}
