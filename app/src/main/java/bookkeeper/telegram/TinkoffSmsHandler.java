package bookkeeper.telegram;

import bookkeeper.entities.AccountTransaction;
import bookkeeper.entities.TelegramUser;
import bookkeeper.repositories.AccountRepository;
import bookkeeper.repositories.AccountTransactionRepository;
import bookkeeper.repositories.TelegramUserRepository;
import bookkeeper.services.matchers.shared.ExpenditureMatcherByMerchant;
import bookkeeper.services.registries.TransactionParserRegistry;
import bookkeeper.services.registries.factories.TransactionParserRegistryFactoryTinkoff;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static bookkeeper.telegram.responses.TransactionResponseFactory.getResponseKeyboard;
import static bookkeeper.telegram.responses.TransactionResponseFactory.getResponseMessage;


/**
 * Scenario: user stores transactions.
 */
public class TinkoffSmsHandler extends AbstractHandler {
    private final TransactionParserRegistry transactionParserRegistry;
    private final AccountTransactionRepository transactionRepository;

    TinkoffSmsHandler(TelegramBot bot, TelegramUserRepository telegramUserRepository, AccountRepository accountRepository, AccountTransactionRepository transactionRepository, ExpenditureMatcherByMerchant expenditureMatcherByMerchant) {
        super(bot, telegramUserRepository);
        this.transactionParserRegistry = new TransactionParserRegistryFactoryTinkoff(accountRepository, expenditureMatcherByMerchant).create();
        this.transactionRepository = transactionRepository;
    }

    /**
     * Parse SMS text from Tinkoff and display summary.
     * Take Raw SMS list, Transform to AccountTransaction and put to AccountTransactionRepository.
     */
    @Override
    Boolean handle(Update update) {
        if (update.message() == null)
            return false;

        var smsList = update.message().text().split("\n");
        List<AccountTransaction> transactions;
        try {
            transactions = userSendsBankingMessages(smsList, getTelegramUser(update));
        } catch (ParseException e) {
            // provided sms was not parsed
            return false;
        }

        sendMessage(update, getResponseMessage(transactions), getResponseKeyboard(transactions), true);
        return true;

    }

    List<AccountTransaction> userSendsBankingMessages(String[] bankingMessages, TelegramUser user) throws ParseException {
        List<AccountTransaction> results = new ArrayList<>();

        for (var message : bankingMessages ) {
            var transaction = transactionParserRegistry.parse(message, user);
            transactionRepository.save(transaction);
            results.add(transaction);
        }

        return results;
    }

}
