package net.ddns.protocoin.eventbus.listener;

import net.ddns.protocoin.core.blockchain.transaction.Transaction;
import net.ddns.protocoin.eventbus.event.NewTransactionEvent;

import java.util.function.Consumer;

public class NewTransactionEventListener extends Listener<NewTransactionEvent> {
    private final Consumer<Transaction> transactionRegisterer;

    public NewTransactionEventListener(Consumer<Transaction> transactionRegisterer) {
        this.transactionRegisterer = transactionRegisterer;
    }

    @Override
    protected void handle(NewTransactionEvent event) {
        transactionRegisterer.accept(event.eventPayload());
    }
}
