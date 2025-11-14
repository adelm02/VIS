package domain;

import java.util.Objects;

public final class Price {
    private final int amountCzk;
    private final String currency;

    public Price(int amountCzk) {
        this(amountCzk, "CZK");
    }

    public Price(int amountCzk, String currency) {
        if (amountCzk < 0) throw new IllegalArgumentException("Price must be >= 0");
        if (currency == null || currency.isEmpty()) throw new IllegalArgumentException("Currency required");
        this.amountCzk = amountCzk;
        this.currency = currency;
    }

    public int getAmountCzk() { return amountCzk; }
    public String getCurrency() { return currency; }

    public Price plus(Price other) {
        ensureSameCurrency(other);
        return new Price(this.amountCzk + other.amountCzk, this.currency);
    }

    public boolean gte(Price other) {
        ensureSameCurrency(other);
        return this.amountCzk >= other.amountCzk;
    }

    private void ensureSameCurrency(Price other) {
        if (!Objects.equals(this.currency, other.currency))
            throw new IllegalArgumentException("Currency mismatch: " + this.currency + " vs " + other.currency);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Price price = (Price) o;
        return amountCzk == price.amountCzk && Objects.equals(currency, price.currency);
    }

    @Override
    public int hashCode() { return Objects.hash(amountCzk, currency); }

    @Override
    public String toString() { return amountCzk + " " + currency; }
}


