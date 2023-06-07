package ua.goit.banks.monobank;

import lombok.Data;

@Data
public class MonoCurrency {
    private final int currencyCodeA;
    private final int currencyCodeB;
    private final int date;
    private final float rateSell;
    private final float rateBuy;
    private final float rateCross;
}
