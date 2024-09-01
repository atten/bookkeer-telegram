package bookkeeper.telegram.scenario.addTransaction.sber.parser;

import bookkeeper.service.parser.MarkSpendingParser;
import bookkeeper.service.parser.RegexpSpendingParser;

@MarkSpendingParser(provider = "sber")
public class SberRecurringChargeSmsParser extends RegexpSpendingParser<SberRecurringChargeSms> {
    public SberRecurringChargeSmsParser() {
        super(
            SberRecurringChargeSms.class,
            ACCOUNT_FIELD,
            TIME,
            "Оплата",
            AMOUNT_FIELD + CURRENCY_FIELD,
            "Автоплатёж",
            TEXT_FIELD,
            "Баланс",
            AMOUNT_FIELD + CURRENCY_FIELD
        );
    }
}
