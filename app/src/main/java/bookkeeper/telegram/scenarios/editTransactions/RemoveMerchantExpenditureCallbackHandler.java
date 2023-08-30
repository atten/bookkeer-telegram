package bookkeeper.telegram.scenarios.editTransactions;

import bookkeeper.enums.Expenditure;
import bookkeeper.services.repositories.MerchantExpenditureRepository;
import bookkeeper.services.repositories.TelegramUserRepository;
import bookkeeper.telegram.shared.AbstractHandler;
import bookkeeper.telegram.shared.CallbackMessageRegistry;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;

import static bookkeeper.telegram.shared.TransactionResponseFactory.getResponseMessage;


public class RemoveMerchantExpenditureCallbackHandler extends AbstractHandler {
    private final MerchantExpenditureRepository merchantExpenditureRepository;

    public RemoveMerchantExpenditureCallbackHandler(TelegramBot bot, TelegramUserRepository telegramUserRepository, MerchantExpenditureRepository merchantExpenditureRepository) {
        super(bot, telegramUserRepository);
        this.merchantExpenditureRepository = merchantExpenditureRepository;
    }

    @Override
    public Boolean handle(Update update) {
        var callbackMessage = CallbackMessageRegistry.getCallbackMessage(update);
        if (!(callbackMessage.isPresent() && callbackMessage.get() instanceof RemoveMerchantExpenditureCallback))
            return false;

        var cm = (RemoveMerchantExpenditureCallback) callbackMessage.get();
        merchantExpenditureRepository.removeMerchantAssociation(cm.getMerchant(), cm.getExpenditure(), getTelegramUser(update));
        editMessage(update, getResponseMessage(cm.getMerchant(), Expenditure.OTHER));
        return true;
    }
}
