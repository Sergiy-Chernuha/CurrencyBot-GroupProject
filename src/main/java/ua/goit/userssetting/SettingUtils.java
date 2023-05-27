package ua.goit.userssetting;

import ua.goit.banks.WorkingCurrency;

import java.io.IOException;

public class SettingUtils {

    private SettingUtils() {
    }

    public static String getCurrentData(ChatBotSettings userSettings) {
        StringBuilder result = new StringBuilder();
        int numberOfDecimal = userSettings.getNumberOfDecimal();

        try {
            userSettings.getBank().updateCurrentData();
        } catch (IOException e) {
            System.out.println("No bank connection");
        }

        result.append("Курс в ");
        result.append(userSettings.getBank().getName());
        result.append(": ");

        for (WorkingCurrency current : userSettings.getBank().getCurrencies()) {
            if (!userSettings.getChoicesCurrencies().contains(current.getName())) {
                continue;
            }

            result.append("\n\n");
            result.append(current.getName());
            result.append("/UAH\n");
            result.append("   Продаж:");
            result.append(String.format("%." + numberOfDecimal + "f\n", current.getCurrencySellingRate()));
            result.append("   Купівля:");
            result.append(String.format("%." + numberOfDecimal + "f", current.getCurrencyBuyingRate()));
        }

        return result.toString();
    }
}
