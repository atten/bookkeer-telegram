package bookkeeper.telegram.scenarios.viewMonthlyExpenses;

import bookkeeper.entities.TelegramUser;
import bookkeeper.enums.Expenditure;
import bookkeeper.services.repositories.AccountRepository;
import bookkeeper.services.repositories.AccountTransactionRepository;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

class MonthlyExpensesResponseFactory {
    private final AccountRepository accountRepository;
    private final AccountTransactionRepository transactionRepository;

    MonthlyExpensesResponseFactory(AccountRepository accountRepository, AccountTransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    String getMonthlyExpenses(TelegramUser user, int monthOffset) {
        var lines = new ArrayList<String>();
        var creditByCurrency = new HashMap<Currency, BigDecimal>();
        var debitByCurrency = new HashMap<Currency, BigDecimal>();
        var allByCurrency = new HashMap<Currency, BigDecimal>();
        var maxExpenditureLength = Expenditure.enabledValues().stream().map(expenditure -> expenditure.getVerboseName().length()).max(Comparator.naturalOrder()).orElse(0);
        var formatString = "%-" + maxExpenditureLength + "s %s";  // example: "%-15s %s"
        var periodVerbose = LocalDate.now().plusMonths(monthOffset).format(DateTimeFormatter.ofPattern("MMM yyyy"));
        var accounts = accountRepository.filter(user);

        for (var account : accounts) {
            var currency = account.getCurrency();
            var amountByExpenditure = transactionRepository.getMonthlyAmount(account, monthOffset);

            if (amountByExpenditure.isEmpty())
                continue;

            creditByCurrency.putIfAbsent(currency, BigDecimal.ZERO);
            debitByCurrency.putIfAbsent(currency, BigDecimal.ZERO);
            allByCurrency.putIfAbsent(currency, BigDecimal.ZERO);

            lines.add(String.format("*%s*", account.getName()));
            lines.add("```");

            for (var expenditure : amountByExpenditure.keySet()) {
                var amount = amountByExpenditure.get(expenditure);
                var sign = amount.compareTo(BigDecimal.ZERO);

                allByCurrency.merge(currency, amount, BigDecimal::add);

                if (sign > 0) {
                    debitByCurrency.merge(currency, amount, BigDecimal::add);
                } else if (sign < 0) {
                    creditByCurrency.merge(currency, amount, BigDecimal::add);
                } else {
                    continue;
                }

                lines.add(String.format(formatString, expenditure.getVerboseName(), roundedAmountString(amount)));
            }

            lines.add("```");
        }

        lines.add(String.format("*Всего за %s*", periodVerbose));
        lines.add("```");
        lines.add(String.format("%-7s %s", "Расходы", amountByCurrencyString(creditByCurrency)));
        lines.add(String.format("%-7s %s", "Доходы", amountByCurrencyString(debitByCurrency)));
        lines.add(String.format("%-7s %s", "Баланс", amountByCurrencyString(allByCurrency)));
        lines.add("```");

        return String.join("\n", lines);
    }

    static InlineKeyboardMarkup getMonthlyExpensesKeyboard(int monthOffset) {
        var keyboard = new InlineKeyboardMarkup()
                .addRow(new SelectMonthlyExpendituresCallback(monthOffset).asButton("Разобрать"));

        var date = LocalDate.now();
        var prevMonthButton = new ViewMonthlyExpensesWithOffsetCallback(monthOffset - 1).asPrevMonthButton(date, monthOffset - 1);
        var nextMonthButton = new ViewMonthlyExpensesWithOffsetCallback(monthOffset + 1).asNextMonthButton(date, monthOffset + 1);
        return keyboard.addRow(prevMonthButton, nextMonthButton);
    }

    private String amountByCurrencyString(Map<Currency, BigDecimal> values) {
        return values
            .entrySet().stream()
            .map(i -> roundedAmountString(i.getValue()) + " " + i.getKey().getSymbol())
            .collect(Collectors.joining(", "));
    }

    private String roundedAmountString(BigDecimal value) {
        return String.format("% ,.0f", value.negate()).replace("-", "+");
    }
}
