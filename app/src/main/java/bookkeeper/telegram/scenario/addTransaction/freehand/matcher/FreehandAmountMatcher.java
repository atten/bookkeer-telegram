package bookkeeper.telegram.scenario.addTransaction.freehand.matcher;

import bookkeeper.service.matcher.AmountMatcher;
import bookkeeper.service.parser.Spending;
import bookkeeper.telegram.scenario.addTransaction.freehand.parser.FreehandRecord;
import bookkeeper.telegram.scenario.addTransaction.freehand.parser.FreehandRecordWithCurrency;

import java.math.BigDecimal;
import java.util.Optional;

public class FreehandAmountMatcher implements AmountMatcher {
    @Override
    public Optional<BigDecimal> match(Spending spending) {
        if (spending instanceof FreehandRecord obj) {
            return Optional.of(obj.getAmount().negate());
        }
        if (spending instanceof FreehandRecordWithCurrency obj) {
            return Optional.of(obj.getAmount().negate());
        }
        return Optional.empty();
    }
}
