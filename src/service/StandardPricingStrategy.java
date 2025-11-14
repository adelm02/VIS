package service;
import domain.Reservation;
import domain.Zakaznik;
import interfaces.IPricingStrategy;

//Strategy
public class StandardPricingStrategy implements IPricingStrategy {
    @Override
    public int calculatePrice(int basePrice, Zakaznik zakaznik) {
        return basePrice;
    }

    @Override
    public String getStrategyName() {
        return "Standardní";
    }
}