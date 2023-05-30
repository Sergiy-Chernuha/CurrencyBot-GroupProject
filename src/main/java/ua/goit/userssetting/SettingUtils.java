package ua.goit.userssetting;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import ua.goit.banks.BankFactory;
import ua.goit.banks.Banks;
import ua.goit.banks.WorkingCurrency;

import java.io.*;
import java.util.*;

public class SettingUtils {

    private SettingUtils() {
    }

    public static String getCurrentData(ChatBotSettings userSettings) {
        StringBuilder result = new StringBuilder();
        int numberOfDecimal = userSettings.getNumberOfDecimal();
        Banks bank = BankFactory.getBank(userSettings.getBank());

        try {
            bank.updateCurrentData();
        } catch (IOException e) {
            System.out.println("No bank connection");
        }

        result.append("Курс в ");
        result.append(bank.getName());
        result.append(": \n");

        for (WorkingCurrency current : bank.getCurrencies()) {
            if (!userSettings.getChoicesCurrencies().contains(current.getName())) {
                continue;
            }

            result.append("\n");
            result.append(current.getName());
            result.append("/UAH\n");
            result.append("   Продаж:");
            result.append(String.format("%." + numberOfDecimal + "f\n", current.getCurrencySellingRate()));
            result.append("   Купівля:");
            result.append(String.format("%." + numberOfDecimal + "f", current.getCurrencyBuyingRate()));
        }

        return result.toString();
    }

    public static void writeUserSettings(ChatBotSettings userSetting) {
//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        String fileName = "src/main/resources/Users/User" + userSetting.getChatId() + ".json";
//
//        try (JsonWriter jsonWriter = new JsonWriter(new FileWriter(fileName, false))) {
//            String json = gson.toJson(userSetting);
//
//            jsonWriter.jsonValue(json);
//            jsonWriter.flush();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public static ChatBotSettings readUserSetting(Long chatId) {
//        ChatBotSettings newUserSetting = new ChatBotSettings(chatId);
//        String fileName = "src/main/resources/Users/User" + chatId + ".json";
//        Gson gson = new GsonBuilder().create();
//        StringBuilder stringBuilder = new StringBuilder();
//
//        try (FileReader in = new FileReader(fileName)) {
//            Scanner scanner = new Scanner(in);
//
//            while (scanner.hasNext()) {
//                stringBuilder.append(scanner.nextLine());
//                if (scanner.hasNext()) {
//                    stringBuilder.append("\n");
//                }
//            }
//
//            newUserSetting = gson.fromJson(stringBuilder.toString(), ChatBotSettings.class);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return newUserSetting;
    }
}
