package net.ddns.protocoin.eventbus.event;

import net.ddns.protocoin.core.blockchain.transaction.Transaction;

public class NewTransactionEvent extends Event<Transaction> {
    public NewTransactionEvent(Transaction transaction) {
        super(transaction);
    }
}
