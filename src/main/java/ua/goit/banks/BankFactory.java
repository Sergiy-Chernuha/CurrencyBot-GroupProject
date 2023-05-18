package ua.goit.banks;

import ua.goit.banks.monobank.MonoBank;
import ua.goit.banks.nbubank.NBUBank;
import ua.goit.banks.privatbank.PrivatBank;

public class BankFactory {

    private BankFactory() {
    }

    public static Banks getBank(String bankName) {
        if (bankName.equals("PrivatBank")) {
            return new PrivatBank();
        } else if (bankName.equals("MonoBank")) {
            return new MonoBank();
        }

        return new NBUBank();
    }
}
