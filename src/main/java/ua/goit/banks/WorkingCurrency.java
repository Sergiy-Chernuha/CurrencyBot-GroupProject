package ua.goit.banks;

public class WorkingCurrency implements CurrencyInterface {

    private final Currencies name;
    private final float currencySellingRate;
    private final float currencyBuyingRate;

    public WorkingCurrency(Currencies name, float currencySellingRate, float currencyBuyingRate) {
        super();
        this.name = name;
        this.currencySellingRate = currencySellingRate;
        this.currencyBuyingRate = currencyBuyingRate;
    }

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
