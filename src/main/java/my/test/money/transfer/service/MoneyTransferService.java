package my.test.money.transfer.service;

import my.test.money.transfer.data.DataStore;
import my.test.money.transfer.data.DataValidation;
import my.test.money.transfer.model.Account;
import my.test.money.transfer.model.Transfer;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Path("/transfer-service")
@Produces(MediaType.APPLICATION_JSON)
public class MoneyTransferService {

    @POST
    @Path("account")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createAccount(Account account) {
        Account existingAccount = DataStore.getInstance().addAccount(account);
        if (existingAccount != null) {
            return new Response(account, "ERROR", "Account already exists");
        } else {
            return new Response(account, "SUCCESS", "Account created successfully");
        }
    }

    @GET
    @Path("accounts/{accountId}")
    public Response getAccountDetails(@PathParam("accountId") String accountId) {
        Account account = DataStore.getInstance().getAccount(accountId);
        if (Objects.isNull(account)) {
            return new Response(null, "ERROR", "Account(s) not found");
        } else {
            return new Response(account, "SUCCESS", "Account(s) retrieved successfully");
        }
    }

    @POST
    @Path("accounts/{accountId}/transfers")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response transfer(@PathParam("accountId") String accountId, Transfer transfer) {

        DataStore dataStore = DataStore.getInstance();

        Account creditorAccount = dataStore.getAccount(accountId);
        Account debitorAccount = dataStore.getAccount(transfer.getDebitorAccountId());

        try {
            DataValidation.validateTransferParameters(transfer, creditorAccount, debitorAccount);
        } catch (Exception e) {
            return new Response(null, "ERROR", e.getMessage());
        }

        Account firstLock, secondLock;

        if (creditorAccount.getId().compareTo(debitorAccount.getId()) < 0) {
            firstLock = creditorAccount;
            secondLock = debitorAccount;
        } else {
            firstLock = debitorAccount;
            secondLock = creditorAccount;
        }

        synchronized (firstLock) {
            synchronized (secondLock) {

                if (creditorAccount.getBalance().compareTo(transfer.getAmount().getValue()) < 0) {
                    return new Response(transfer, "ERROR", "Not enough funds");
                }

                creditorAccount.setBalance(creditorAccount.getBalance().subtract(transfer.getAmount().getValue()));
                debitorAccount.setBalance(debitorAccount.getBalance().add(transfer.getAmount().getValue()));

                dataStore.createAccountTransaction(accountId, transfer);
            }
        }
        return new Response(transfer, "SUCCESS", "Transfer created successfully");
    }

    @GET
    @Path("accounts/{accountId}/transfers")
    public Response getTransfers(@PathParam("accountId") String accountId) {
        DataStore instance = DataStore.getInstance();

        List<Transfer> accountTransactions = instance.getAccountTransactions(accountId);
        if (accountTransactions != null) {
            return new Response(accountTransactions, "SUCCESS", "");
        } else {
            return new Response(null, "ERROR", "Account transactions not found");
        }
    }
}