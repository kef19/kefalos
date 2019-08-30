package my.test.money.transfer.data;

import my.test.money.transfer.model.Account;
import my.test.money.transfer.model.Transfer;

import java.math.BigDecimal;

public class DataValidation {

    public static void validateTransferParameters(Transfer transfer, Account creditorAccount, Account debitorAccount) throws Exception {

        //validate creditorAccount is different than debitorAccount
        if (creditorAccount.getId().equalsIgnoreCase(debitorAccount.getId())) {
            throw new Exception("Cannot transfer from account to itself");
        }

        if (creditorAccount == null || debitorAccount == null) {
            throw new Exception("Creditor or debitor account not found");
        }

        //validate amount > 0
        if (transfer.getAmount().getValue().compareTo(new BigDecimal(0)) < 0) {
            throw new Exception("Not supported, please add a positive amount to be transferred");
        }

        //validate same currency as the accounts, we don't know how to do FX
        if (!transfer.getAmount().getCurrency().equalsIgnoreCase(creditorAccount.getCurrency()) || !transfer.getAmount().getCurrency().equalsIgnoreCase(debitorAccount.getCurrency())) {
            throw new Exception("Currency mismatch");
        }

    }
}