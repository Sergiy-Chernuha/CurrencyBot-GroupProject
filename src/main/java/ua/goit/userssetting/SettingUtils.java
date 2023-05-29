package ua.goit.userssetting;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import lombok.Data;

import ua.goit.banks.WorkingCurrency;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Data
public class SettingUtils {

    private Map<Long, ChatBotSettings> clients;

    public SettingUtils() {
        this.clients = new HashMap<>();
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

    public void add(long chatId, ChatBotSettings client) {
        clients.put(chatId, client);
    }

    public Map<Long, ChatBotSettings> getWriterListOfClients() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try {
            JsonWriter jsonWriter = new JsonWriter(new FileWriter("src/main/resources/users" + clients.keySet() + ".json", false));
            String json = gson.toJson(clients);

            jsonWriter.jsonValue(json);
            jsonWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return clients;
    }

    public Map<Long, ChatBotSettings> getObtainingClientSettings() {
        JsonReader reader;
        ChatBotSettings fromJson;

        try {
            reader = new JsonReader(new FileReader("src/main/resources/users" + clients.keySet() + ".json"));
            fromJson = new Gson().fromJson(reader, ChatBotSettings.class);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        System.out.println(fromJson);

        return clients;
    }
}
