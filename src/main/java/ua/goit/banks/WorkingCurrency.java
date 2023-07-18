package ua.goit.banks;

import lombok.Data;

@Data
public class WorkingCurrency implements CurrencyInterface {

    private final Currencies name;
    private final float currencySellingRate;
    private final float currencyBuyingRate;

    @Override
    public float getCurrencySellingRate() {
        return currencySellingRate;
    }

    @Override
    public float getCurrencyBuyingRate() {
        return currencyBuyingRate;
    }
}
