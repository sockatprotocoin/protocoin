package net.ddns.protocoin.core.util;

import net.ddns.protocoin.core.blockchain.data.Bytes;
import net.ddns.protocoin.core.blockchain.data.VarInt;
import net.ddns.protocoin.core.util.ArrayUtil;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class ArrayUtilTest {
    @Test
    void shouldConcat2ByteTablesCorrectly(){
        var bytes1 = new byte[]{0, 1, 2, 3, 4, 5};
        var bytes2 = new byte[]{6, 7, 8, 9, 10};
        var expected = new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

        var received = ArrayUtil.concat(bytes1,bytes2);

        assertArrayEquals(expected,received);
    }

    @Test
    void shouldConcatMoreThan2ByteTablesCorrectly(){
        var bytes1 = new byte[]{0, 1, 2};
        var bytes2 = new byte[]{ 3, 4, 5};
        var bytes3 = new byte[]{6, 7, 8, 9};
        var bytes4 = new byte[]{10, 11, 12};
        var expected = new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};

        var received = ArrayUtil.concat(bytes1,bytes2, bytes3, bytes4);

        assertArrayEquals(expected,received);
    }

    @Test
    void shouldConcat2BytableListsToBytesCorrectly(){
        var bytable1 = new VarInt(10);
        var bytable2 = new VarInt(20);

        var expected = new byte[]{10,20};
        var received = ArrayUtil.bytableListToArray(Arrays.asList(bytable1,bytable2));

        assertArrayEquals(expected, received);
    }

    @Test
    void shouldConcatMoreThan2BytableListsToBytesCorrectly(){
        var bytable1 = new VarInt(11);
        var bytable2 = new VarInt(22);
        var bytable3 = new VarInt(33);
        var bytable4 = new VarInt(44);

        var expected = new byte[]{11, 22, 33, 44};
        var received = ArrayUtil.bytableListToArray(Arrays.asList(bytable1, bytable2, bytable3, bytable4));

        assertArrayEquals(expected, received);
    }

    @Test
    void shouldConcatBytableArrayToBytesCorrectly() {
        var bytes1 = Bytes.of(new byte[]{1, 2, 3}, 3);
        var bytes2 = Bytes.of(new byte[]{4, 5, 6}, 3);
        var bytes3 = Bytes.of(new byte[]{7, 8, 9}, 3);

        var expectedByteArray = new byte[]{1, 2, 3, 4, 5, 6, 7, 8 ,9};

        var actualByteArray = ArrayUtil.concat(bytes1, bytes2, bytes3);
        assertArrayEquals(expectedByteArray, actualByteArray);
    }
}
