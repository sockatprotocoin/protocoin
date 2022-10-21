package net.ddns.protocoin.core.blockchain.block;

import net.ddns.protocoin.core.blockchain.Bytable;
import net.ddns.protocoin.core.blockchain.transaction.Transaction;
import net.ddns.protocoin.core.blockchain.data.VarInt;
import net.ddns.protocoin.core.util.ArrayUtil;
import net.ddns.protocoin.core.util.Hash;
import net.ddns.protocoin.core.util.RandomGenerator;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Block implements Bytable {
    public final static byte[] MAGIC_BYTES = new byte[]{6, 9, 6, 9};
    private final BlockHeader blockHeader;
    private final VarInt transactionCount;
    private final List<Transaction> transactions;

    public Block(BlockHeader blockHeader, List<Transaction> transactions) {
        this.blockHeader = blockHeader;
        this.transactionCount = new VarInt(transactions.size());
        this.transactions = transactions;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void mine() {
        var random = new RandomGenerator();

        while (!isMined()) {
            blockHeader.setNonce(random.bytes(4));
            var hash = getHash();
            String zeros = "";
            for (var b : hash) {
                if (b == 0) {
                    zeros += "0";
                } else {
                    break;
                }
            }
            if (zeros.length() > 2) {
                System.out.println(zeros);
            }
        }
    }

    public boolean isMined() {
        var blockHash = getHash();
        var target = blockHeader.getTarget();

        return new BigInteger(1, blockHash).compareTo(target) < 0;
    }

    public byte[] getHash() {
        return Hash.sha256(getBytes(), 2);
    }

    public static Block readFromInputStream(InputStream is) throws IOException {
        var header = BlockHeader.readFromInputStream(is);
        var transactionCount = VarInt.readFromIs(is);
        var transactions = new ArrayList<Transaction>();
        for (int i = 0; i < transactionCount.integerValue(); i++) {
            transactions.add(Transaction.readFromInputStream(is));
        }

        return new Block(header, transactions);
    }

    @Override
    public byte[] getBytes() {
        var transactionsBytes = ArrayUtil.bytableListToArray(transactions);
        return ArrayUtil.concat(MAGIC_BYTES, blockHeader.getBytes(), transactionCount.getBytes(), transactionsBytes);
    }
}
