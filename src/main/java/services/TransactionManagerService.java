package services;

import domain.AccountModel;
import domain.AccountType;
import domain.MoneyModel;
import domain.TransactionModel;
import repository.AccountsRepository;
import utils.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TransactionManagerService {

    public TransactionModel transfer(String fromAccountId, String toAccountId, MoneyModel value) {
        AccountModel fromAccount = AccountsRepository.INSTANCE.get(fromAccountId);
        AccountModel toAccount = AccountsRepository.INSTANCE.get(toAccountId);

        if (fromAccount == null || toAccount == null) {
            throw new RuntimeException("Specified account does not exist");
        }

        if (fromAccount.getAccountType() == AccountType.SAVINGS)
            throw new RuntimeException("You cannot preform transactions from a savings account.");

        if (value.getAmount() > fromAccount.getBalance().getAmount())
            throw new RuntimeException("Negative Balance Error. Please give a smaller value.");

        if (fromAccount.getBalance().getCurrency() != value.getCurrency())
            throw new RuntimeException("The currency of the transferred value must match the currency from the account you want to transfer money out of.");


        TransactionModel transaction = new TransactionModel(
                UUID.randomUUID(),
                fromAccountId,
                toAccountId,
                value,
                LocalDate.now()
        );

        fromAccount.getBalance().setAmount(fromAccount.getBalance().getAmount() - value.getAmount());
        fromAccount.getTransactions().add(transaction);

        //As it wasn't specified, I will assume the value currency always matches the fromAccount currency.
        if (fromAccount.getBalance().getCurrency() != toAccount.getBalance().getCurrency()) {
            MoneyModel convertedValue = MoneyUtils.convert(value, toAccount.getBalance().getCurrency());
            toAccount.getBalance().setAmount(toAccount.getBalance().getAmount() + convertedValue.getAmount());
            toAccount.getTransactions().add(transaction);
        }
        else {
            toAccount.getBalance().setAmount(toAccount.getBalance().getAmount() + value.getAmount());
            toAccount.getTransactions().add(transaction);
        }

        return transaction;
    }

    public TransactionModel withdraw(String accountId, MoneyModel amount) {
        AccountModel account = AccountsRepository.INSTANCE.get(accountId);

        if (account == null)
            throw new RuntimeException("Specified account does not exist");

        if (amount.getAmount() > account.getBalance().getAmount())
            throw new RuntimeException("Negative Balance Error. Please give a smaller value.");

        if (account.getBalance().getCurrency() != amount.getCurrency())
            throw new RuntimeException("Currency Mismatch Error");

        TransactionModel transaction = new TransactionModel(
                UUID.randomUUID(),
                accountId,
                accountId,
                amount,
                LocalDate.now()
        );

        account.getBalance().setAmount(account.getBalance().getAmount() - amount.getAmount());

        return transaction;
    }

    public MoneyModel checkFunds(String accountId) {
        if (!AccountsRepository.INSTANCE.exist(accountId)) {
            throw new RuntimeException("Specified account does not exist");
        }
        return AccountsRepository.INSTANCE.get(accountId).getBalance();
    }

    public List<TransactionModel> retrieveTransactions(String accountId) {
        if (!AccountsRepository.INSTANCE.exist(accountId)) {
            throw new RuntimeException("Specified account does not exist");
        }
        return new ArrayList<>(AccountsRepository.INSTANCE.get(accountId).getTransactions());
    }
}

