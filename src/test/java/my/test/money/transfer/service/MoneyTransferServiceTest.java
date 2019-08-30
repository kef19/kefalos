package my.test.money.transfer.service;


import my.test.money.transfer.data.DataStore;
import my.test.money.transfer.model.Account;
import my.test.money.transfer.model.Amount;
import my.test.money.transfer.model.Transfer;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class MoneyTransferServiceTest {
    private MoneyTransferService mts;

    @Before
    public void setup() {
        this.mts = new MoneyTransferService();
        DataStore.getInstance().reset();
    }

    @Test
    public void testCreateAccount() {
        Account account = createAccount("account001", "Mr A", "GBP", "123.5");
        Response response = mts.createAccount(account);

        assertEquals("SUCCESS", response.getStatus());
        Account datastoreAccount = DataStore.getInstance().getAccount("account001");
        assertEquals(new BigDecimal(123.5), datastoreAccount.getBalance());
    }

    @Test
    public void testCreateAccountWithExistingId() {
        Account account1 = createAccount("account001", "Mr A", "GBP", "123.5");
        mts.createAccount(account1);

        Account account2 = createAccount("account001", "Mr B", "GBP", "234.0");
        Response response2 = mts.createAccount(account2);

        assertEquals("ERROR", response2.getStatus());
        assertEquals("Account already exists", response2.getMessage());
        Account datastoreAccount = DataStore.getInstance().getAccount("account001");
        assertEquals(new BigDecimal(123.5), datastoreAccount.getBalance());
    }

    @Test
    public void testGetAccountDetails() {
        Account account = createAccount("account001", "Mr A", "GBP", "123.5");
        mts.createAccount(account);
        Response response = mts.getAccountDetails("account001");

        assertEquals("SUCCESS", response.getStatus());
        Account datastoreAccount = DataStore.getInstance().getAccount("account001");
        assertEquals(new BigDecimal(123.5), datastoreAccount.getBalance());
    }

    @Test
    public void testMoneyTransfer() {
        Account creditorAccount = createAccount("account001", "Mr A", "GBP", "123.45");
        DataStore.getInstance().addAccount(creditorAccount);
        Account debitorAccount = createAccount("account002", "Ms B", "GBP", "234.56");
        DataStore.getInstance().addAccount(debitorAccount);

        Transfer transfer = createTransfer("55.00", "GBP", creditorAccount.getId(), debitorAccount.getId(), "to my friend");
        Response response = mts.transfer(creditorAccount.getId(), transfer);

        assertEquals("SUCCESS", response.getStatus());
        System.out.println(creditorAccount);
        System.out.println(debitorAccount);
        assertEquals(new BigDecimal("68.45"), DataStore.getInstance().getAccount(creditorAccount.getId()).getBalance());
        assertEquals(new BigDecimal("289.56"), DataStore.getInstance().getAccount(debitorAccount.getId()).getBalance());
    }

    @Test
    public void testMoneyTransferSameAccount() {
        Account creditorAccount = createAccount("account001", "Mr A", "GBP", "123.45");
        DataStore.getInstance().addAccount(creditorAccount);

        Transfer transfer = createTransfer("55.00", "GBP", creditorAccount.getId(), creditorAccount.getId(), "to my friend");
        Response response = mts.transfer(creditorAccount.getId(), transfer);

        assertEquals("ERROR", response.getStatus());
        assertEquals("Cannot transfer from account to itself", response.getMessage());
        assertEquals(new BigDecimal("123.45"), DataStore.getInstance().getAccount(creditorAccount.getId()).getBalance());
    }

    @Test
    public void testMoneyTransferInsufficientFunds() {
        Account creditorAccount = createAccount("account001", "Mr A", "GBP", "123.45");
        DataStore.getInstance().addAccount(creditorAccount);
        Account debitorAccount = createAccount("account002", "Ms B", "GBP", "234.56");
        DataStore.getInstance().addAccount(debitorAccount);

        Transfer transfer = createTransfer("155.00", "GBP", creditorAccount.getId(), debitorAccount.getId(), "to my friend");
        Response response = mts.transfer(creditorAccount.getId(), transfer);

        assertEquals("ERROR", response.getStatus());
        assertEquals("Not enough funds", response.getMessage());
        System.out.println(creditorAccount);
        System.out.println(debitorAccount);
        assertEquals(new BigDecimal("123.45"), DataStore.getInstance().getAccount(creditorAccount.getId()).getBalance());
        assertEquals(new BigDecimal("234.56"), DataStore.getInstance().getAccount(debitorAccount.getId()).getBalance());
    }

    @Test
    public void testMoneyTransferAccountsCurrencyMismatch() {
        Account creditorAccount = createAccount("account001", "Mr A", "GBP", "123.45");
        DataStore.getInstance().addAccount(creditorAccount);
        Account debitorAccount = createAccount("account002", "Ms B", "EUR", "234.56");
        DataStore.getInstance().addAccount(debitorAccount);

        Transfer transfer = createTransfer("155.00", "GBP", creditorAccount.getId(), debitorAccount.getId(), "to my friend");
        Response response = mts.transfer(creditorAccount.getId(), transfer);

        assertEquals("ERROR", response.getStatus());
        assertEquals("Currency mismatch", response.getMessage());
        System.out.println(creditorAccount);
        System.out.println(debitorAccount);
        assertEquals(new BigDecimal("123.45"), DataStore.getInstance().getAccount(creditorAccount.getId()).getBalance());
        assertEquals(new BigDecimal("234.56"), DataStore.getInstance().getAccount(debitorAccount.getId()).getBalance());
    }

    private Account createAccount(String accountId, String name, String currency, String balance) {
        Account account = new Account();
        account.setId(accountId);
        account.setName(name);
        account.setCurrency(currency);
        account.setBalance(new BigDecimal(balance));
        return account;
    }

    private Transfer createTransfer(
            String value, String currency, String creditorAccountId, String debitorAccountId, String reference) {
        //create Amount object
        Amount amount = new Amount();
        amount.setValue(new BigDecimal(value));
        amount.setCurrency(currency);

        //create Transfer object
        Transfer transfer = new Transfer();
        transfer.setDebitorAccountId(debitorAccountId);
        transfer.setAmount(amount);
        transfer.setReference(reference);
        return transfer;
    }

}