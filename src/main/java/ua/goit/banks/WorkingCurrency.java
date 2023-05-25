package ua.goit.banks;

import lombok.Data;

@Data
public class WorkingCurrency implements CurrencyInterface {

    private final Currencies name;
    private final float currencySellingRate;
    private final float currencyBuyingRate;

    @Override
    public Currencies getName() {
        return name;
    }

    @Override
    public double getCurrencySellingRate() {
        return currencySellingRate;
    }

    @Override
    public double getCurrencyBuyingRate() {
        return currencyBuyingRate;
    }
}
