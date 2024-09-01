package bookkeeper.telegram.scenario.addTransaction.sber.parser;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SberRecurringChargeSmsParserTest {
    private final SberRecurringChargeSmsParser parser = new SberRecurringChargeSmsParser();

    @Test
    void parseOk() throws ParseException {
        var sms = parser.parse("СЧЁТ1234 09:58 Оплата 550р Автоплатёж Энторнет Баланс: 1 378.52р");

        var referenceSms = new SberRecurringChargeSms();

        referenceSms.setAccountName("СЧЁТ1234");
        referenceSms.setChargeSum(new BigDecimal("550"));
        referenceSms.setChargeCurrency(Currency.getInstance("RUB"));
        referenceSms.setDestination("Энторнет");
        referenceSms.setAccountBalance(new BigDecimal("1378.52"));
        referenceSms.setAccountCurrency(Currency.getInstance("RUB"));

        assertEquals(referenceSms, sms);
    }
}
