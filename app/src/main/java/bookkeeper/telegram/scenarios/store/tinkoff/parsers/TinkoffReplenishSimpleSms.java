package bookkeeper.telegram.scenarios.store.tinkoff.parsers;

import bookkeeper.services.parsers.Spending;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

/**
 * Example:
 * Пополнение, счет RUB. 236 RUB. Доступно 713.79 RUB
 */
public class TinkoffReplenishSimpleSms implements Spending {
    public BigDecimal replenishSum;  // 236
    public Currency replenishCurrency;  // RUB
    public BigDecimal accountBalance;  // 499.28
    public Currency accountCurrency;  // RUB

    @NotNull
    @Override
    public String getMerchant() {
        return "Пополнение";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TinkoffReplenishSimpleSms)) return false;
        TinkoffReplenishSimpleSms that = (TinkoffReplenishSimpleSms) o;
        return Objects.equals(replenishSum, that.replenishSum) && Objects.equals(replenishCurrency, that.replenishCurrency) && Objects.equals(accountBalance, that.accountBalance) && Objects.equals(accountCurrency, that.accountCurrency);
    }

    @Override
    public String toString() {
        return "TinkoffReplenishSimpleSms{" +
                "replenishSum=" + replenishSum +
                ", replenishCurrency=" + replenishCurrency +
                ", accountBalance=" + accountBalance +
                ", accountCurrency=" + accountCurrency +
                '}';
    }
}
