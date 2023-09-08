package bookkeeper.telegram.scenarios.viewAssets;

import bookkeeper.services.repositories.AccountRepository;
import bookkeeper.services.repositories.AccountTransactionRepository;
import bookkeeper.services.repositories.AccountTransferRepository;
import bookkeeper.services.repositories.TelegramUserRepository;
import bookkeeper.telegram.shared.AbstractHandler;
import bookkeeper.telegram.shared.CallbackMessageRegistry;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import java.time.LocalDate;
import java.util.Objects;


/**
 * Scenario: user requests total assets.
 */
public class ViewAssetsHandler extends AbstractHandler {
    private final AssetsResponseFactory assetsResponseFactory;

    public ViewAssetsHandler(TelegramBot bot, TelegramUserRepository telegramUserRepository, AccountRepository accountRepository, AccountTransactionRepository transactionRepository, AccountTransferRepository transferRepository) {
        super(bot, telegramUserRepository);
        this.assetsResponseFactory = new AssetsResponseFactory(accountRepository, transactionRepository, transferRepository);
    }

    /**
     * Display total assets overview
     */
    @Override
    public Boolean handle(Update update) {
        return handleCallbackMessage(update) || handleSlashAssets(update);
    }

    private Boolean handleSlashAssets(Update update) {
        if (!Objects.equals(getMessageText(update), "/assets"))
            return false;

        sendMessageWithAssets(update, 0, false);
        return true;
    }

    private Boolean handleCallbackMessage(Update update) {
        var callbackMessage = CallbackMessageRegistry.getCallbackMessage(update);
        if (!(callbackMessage.isPresent() && callbackMessage.get() instanceof ViewAssetsWithOffsetCallback cm))
            return false;

        sendMessageWithAssets(update, cm.getMonthOffset(), true);
        return true;
    }

    private void sendMessageWithAssets(Update update, int monthOffset, boolean edit) {
        var date = LocalDate.now();
        var user = getTelegramUser(update);
        var message = assetsResponseFactory.getTotalAssets(user, monthOffset);
        var keyboard = new InlineKeyboardMarkup().addRow(
                new ViewAssetsWithOffsetCallback(monthOffset - 1).asPrevMonthButton(date, monthOffset - 1),
                new ViewAssetsWithOffsetCallback(monthOffset + 1).asNextMonthButton(date, monthOffset + 1)
        );

        if (edit)
            editMessage(update, message, keyboard);
        else
            sendMessage(update, message, keyboard);
    }

}
