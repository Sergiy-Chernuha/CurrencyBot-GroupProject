package ua.goit.banks.monobank;

public class MonoCurrency {
    private  final int currencyCodeA;
    private final int currencyCodeB;
    private final int date;
    private final float rateSell;
    private final float rateBuy;
    private final float rateCross;

    public MonoCurrency(int currencyCodeA, int currencyCodeB, int date, float rateSell, float rateBuy,
                        float rateCross) {
        super();
        this.currencyCodeA = currencyCodeA;
        this.currencyCodeB = currencyCodeB;
        this.date = date;
        this.rateSell = rateSell;
        this.rateBuy = rateBuy;
        this.rateCross = rateCross;
    }

    public int getCurrencyCodeA() {
        return currencyCodeA;
    }

    public int getCurrencyCodeB() {
        return currencyCodeB;
    }

    public int getDate() {
        return date;
    }

    public float getRateSell() {
        return rateSell;
    }

    public float getRateBuy() {
        return rateBuy;
    }

    public float getRateCross() {
        return rateCross;
    }
}
