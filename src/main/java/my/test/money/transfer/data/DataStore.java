package my.test.money.transfer.data;
import my.test.money.transfer.model.Account;
import my.test.money.transfer.model.Transfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DataStore {

    private Map<String, Account> accounts = new ConcurrentHashMap<>();
    private Map<String, List<Transfer>> transactions = new ConcurrentHashMap<>();

    private static DataStore dataStore = new DataStore();

    private DataStore() {
        this.transactions = transactions;
    }

    public static DataStore getInstance() {
        return dataStore;
    }

    public Account addAccount(Account account) {
        return accounts.putIfAbsent(account.getId(), account);
    }

    public Account getAccount(String accountId) {
        return accounts.get(accountId);
    }

    public List<Transfer> getAccountTransactions(String accountId) {
        return transactions.get(accountId);
    }

    public void createAccountTransaction(String accountId, Transfer transfer) {
        List<Transfer> accountTransactions = getAccountTransactions(accountId);
        if (accountTransactions == null) {
            accountTransactions = new ArrayList<>();
            transactions.put(accountId, accountTransactions);
        }
        accountTransactions.add(transfer);
    }

    public void reset() {
        accounts.clear();
        transactions.clear();
    }

}