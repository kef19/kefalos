package my.test.money.transfer.model;

import java.time.LocalDateTime;
import java.util.concurrent.locks.ReadWriteLock;

public class Transfer {

    private ReadWriteLock accountLock;

    String debitorAccountId;
    String reference;
    Amount amount;

    public String getDebitorAccountId() {
        return debitorAccountId;
    }

    public void setDebitorAccountId(String debitorAccountId) {
        this.debitorAccountId = debitorAccountId;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }
}