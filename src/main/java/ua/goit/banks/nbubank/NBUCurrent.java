package ua.goit.banks.nbubank;

public class NBUCurrent {
    private final int r030;
    private final String txt;
    private final float rate;
    private final String cc;
    private final String exchangedate;

    public NBUCurrent(int r030, String txt, float rate, String cc, String exchangedate) {
        super();
        this.r030 = r030;
        this.txt = txt;
        this.rate = rate;
        this.cc = cc;
        this.exchangedate = exchangedate;
    }

    public int getR030() {
        return r030;
    }

    public String getTxt() {
        return txt;
    }

    public float getRate() {
        return rate;
    }

    public String getCc() {
        return cc;
    }

    public String getExchangedate() {
        return exchangedate;
    }
}
