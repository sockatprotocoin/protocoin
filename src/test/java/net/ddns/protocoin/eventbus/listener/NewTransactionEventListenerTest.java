package net.ddns.protocoin.eventbus.listener;

import net.ddns.protocoin.core.blockchain.transaction.Transaction;
import net.ddns.protocoin.eventbus.event.NewTransactionEvent;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class NewTransactionEventListenerTest {
    @Test
    void shouldHandleNewBlockEvent() {
        final var transactionToVerify = new Transaction[1];
        var transaction = mock(Transaction.class);
        Consumer<Transaction> transactionConsumer = blk ->  transactionToVerify[0] = blk;

        new NewTransactionEventListener(transactionConsumer).handle(new NewTransactionEvent(transaction));

        assertEquals(transactionToVerify[0], transaction);
    }
}