package bookkeeper.telegram;

import bookkeeper.repositories.TelegramUserRepository;
import bookkeeper.telegram.shared.AbstractHandler;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;

import java.util.Date;

/**
 * Print incoming request to console.
 */
public class LoggingHandler extends AbstractHandler {
    public LoggingHandler(TelegramBot bot, TelegramUserRepository telegramUserRepository) {
        super(bot, telegramUserRepository);
    }

    @Override
    public Boolean handle(Update update) {
        logger.info("{} {} -> {}", new Date(), getTelegramUser(update), updateToString(update));
        return false;
    }

    protected String updateToString(Update update) {
        if (update.message() != null)
            return update.message().text();
        return String.format("(callback): %s", update.callbackQuery().data());
    }
}
