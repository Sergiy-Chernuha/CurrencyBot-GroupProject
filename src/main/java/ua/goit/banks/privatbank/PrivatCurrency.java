package ua.goit.banks.privatbank;

public class PrivatCurrency {
    String ccy;
    String base_ccy;
    float buy;
    float sale;

    public PrivatCurrency(String ccy, String base_ccy, float buy, float sale) {
        super();
        this.ccy = ccy;
        this.base_ccy = base_ccy;
        this.buy = buy;
        this.sale = sale;
    }

    public String getCcy() {
        return ccy;
    }

    public String getBase_ccy() {
        return base_ccy;
    }

    public float getBuy() {
        return buy;
    }

    public float getSale() {
        return sale;
    }
}
