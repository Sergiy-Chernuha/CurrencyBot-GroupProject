package ua.goit.userssetting;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import ua.goit.banks.WorkingCurrency;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

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
        result.append(": \n");

        for (WorkingCurrency current : userSettings.getBank().getCurrencies()) {
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

    public static void writeUserSettings(Map<Long, ChatBotSettings> userSetting, Long chatId) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String fileName = "src/main/resources/user2.json";

//        Type type = TypeToken.getParameterized(Map.class,Long.class, userSetting.getClass()).getType();
//        Gson gson2 = new Gson();
//        Map<Long, ChatBotSettings> deepCopy = gson2.fromJson(gson.toJson(userSetting), type);
//        System.out.println(deepCopy);

        try (JsonWriter jsonWriter = new JsonWriter(new FileWriter(fileName, false))) {
            String json = gson.toJson(userSetting.get(chatId));

            jsonWriter.jsonValue(json);
            jsonWriter.flush();

            System.out.println(userSetting.get(chatId) + "\ndone seving");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ChatBotSettings readUserSetting(Long chatId) {
//        String reader;
        ChatBotSettings rededUserSetting = null;
        String fileName = "src/main/resources/user2.json";
        Gson gson = new GsonBuilder().create();
        StringBuilder stringBuilder = new StringBuilder();

        try {
//            Type type = TypeToken.getParameterized(Map.class, Long.class, ChatBotSettings.class).getType();
//            Type type = new TypeToken<Map<Long, ChatBotSettings>>() {}.getType();
//            Type type = new TypeToken<Map<Long, Date>>() {}.getType();

            FileReader in = new FileReader(fileName);
            Scanner scanner = new Scanner(in);
            while (scanner.hasNext()) {
                stringBuilder.append(scanner.nextLine());
                if (scanner.hasNext()) {
                    stringBuilder.append("\n");
                }
            }

//            reader = new String(String.valueOf(in));
            System.out.println(stringBuilder + " \n json");
            rededUserSetting = gson.fromJson(stringBuilder.toString(), ChatBotSettings.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(rededUserSetting + "\nis read");

        return rededUserSetting;
    }
//    public static ChatBotSettings readUserSetting(Long chatId) {
//        String reader;
//        Map<Long, ChatBotSettings> rededUserSetting = null;
//        String fileName = "src/main/resources/user2.json";
//        Gson gson = new GsonBuilder().create();
//        StringBuilder stringBuilder = new StringBuilder();
//
//        try {
////            Type type = TypeToken.getParameterized(Map.class, Long.class, ChatBotSettings.class).getType();
////            Type type = new TypeToken<Map<Long, ChatBotSettings>>() {}.getType();
//            Type type = new TypeToken<Map<Long, Date>>() {}.getType();
//
//            FileReader in = new FileReader(fileName);
//            Scanner scanner = new Scanner(in);
//            while (scanner.hasNext()) {
//                stringBuilder.append(scanner.nextLine());
//                if (scanner.hasNext()) {
//                    stringBuilder.append("\n");
//                }
//            }
//
////            reader = new String(String.valueOf(in));
//            System.out.println(stringBuilder);
//            rededUserSetting = gson.fromJson(stringBuilder.toString(), type);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
////        System.out.println(rededUserSetting + "\nis read");
//
//        return rededUserSetting.get(chatId);
//    }
}
