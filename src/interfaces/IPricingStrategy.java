package interfaces;

import domain.Zakaznik;

public interface IPricingStrategy {
    int calculatePrice(int basePrice, Zakaznik zakaznik);
    String getStrategyName();
}