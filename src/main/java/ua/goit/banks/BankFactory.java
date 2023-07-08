package ua.goit.banks;

import ua.goit.banks.monobank.MonoBankService;
import ua.goit.banks.nbubank.NBUBankService;
import ua.goit.banks.privatbank.PrivatBankService;

public class BankFactory {

    private BankFactory() {
    }

    public static Banks getBank(String bankName) {
        if (bankName.equals("PrivatBank")) {
            return new PrivatBankService();
        } else if (bankName.equals("MonoBank")) {
            return new MonoBankService();
        }

        return new NBUBankService();
    }
}
