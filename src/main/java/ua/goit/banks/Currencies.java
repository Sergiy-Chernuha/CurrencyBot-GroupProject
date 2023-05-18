package ua.goit.banks;

public enum Currencies {

    USD, EUR;

    public static boolean contains(String test) {
        for (Currencies currency : Currencies.values()) {
            if (currency.name().equals(test.toUpperCase())) {
                return true;
            }
        }

        return false;
    }
}
