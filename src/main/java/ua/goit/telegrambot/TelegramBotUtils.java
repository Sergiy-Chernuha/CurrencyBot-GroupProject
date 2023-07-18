package ua.goit.telegrambot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ua.goit.banks.BankFactory;
import ua.goit.banks.Banks;
import ua.goit.banks.Currencies;
import ua.goit.userssetting.ChatBotSettings;

import java.util.ArrayList;
import java.util.List;

public class TelegramBotUtils {

    public SendMessage sendHelloMessage(long chatId) {
        SendMessage sendMessage = new SendMessage();
        ReplyKeyboardMarkup replyKeyboardMarkup = getDefaultKeyBoard();

        sendMessage.setChatId(chatId);
        sendMessage.setText("Ласкаво просимо. Цей бот допоможе відслідковувати актуальні курси валют");
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        return sendMessage;
    }

    public SendMessage sendEndMessage(long chatId) {
        SendMessage sendEndMessage = new SendMessage();
        sendEndMessage.setChatId(chatId);
        sendEndMessage.setText("До зустрічі!");

        return sendEndMessage;
    }

    public SendMessage sendCurrentSettingsMessage(long chatId, ChatBotSettings userSettings) {
        SendMessage sendCurrentSettingsMessage = new SendMessage();
        sendCurrentSettingsMessage.setChatId(chatId);

        String banks = (userSettings.getBanks().size() > 1)
                ? "Банки :" + getStringBanks(userSettings.getBanks())
                : "Банк :" + userSettings.getBanks().get(0);

        String currencies = (userSettings.getChoicesCurrencies().size() > 1)
                ? "Валюти: " + getStringCurrency(userSettings.getChoicesCurrencies())
                : "Валюта: " + userSettings.getChoicesCurrencies().get(0);

        String reminders = (userSettings.isReminderStarted())
                ? "Сповіщення на " + getHours(userSettings.getReminderHours())
                : "Сповіщення вимкнені";

        sendCurrentSettingsMessage.setText(banks + "\n" + currencies + "\nЗнаки після коми: " +
                userSettings.getNumberOfDecimal() + "\n" + reminders);

        return sendCurrentSettingsMessage;
    }

    private ReplyKeyboardMarkup getDefaultKeyBoard() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        KeyboardButton button1 = new KeyboardButton("Отримати інфо");
        row1.add(button1);

        KeyboardRow row2 = new KeyboardRow();
        KeyboardButton button2 = new KeyboardButton("Налаштування");
        row1.add(button2);

        keyboard.add(row1);
        keyboard.add(row2);

        replyKeyboardMarkup.setKeyboard(keyboard);

        return replyKeyboardMarkup;
    }

    private static String getStringBanks(List<String> list) {
        StringBuilder result = new StringBuilder();

        for (String oneBank : list) {
            Banks bank = BankFactory.getBank(oneBank);

            if (!result.isEmpty()) {
                result.append(", ");
            }

            result.append(bank.getName());
        }

        return result.toString();
    }

    private static String getStringCurrency(List<Currencies> list) {
        StringBuilder result = new StringBuilder();

        for (Currencies oneCurrency : list) {
            if (!result.isEmpty()) {
                result.append(", ");
            }

            result.append(oneCurrency.toString());
        }

        return result.toString();
    }

    private static String getHours(List<Integer> list) {
        StringBuilder hours = new StringBuilder();
        list.sort(Integer::compareTo);

        for (Integer userSetting : list) {
            if (!hours.isEmpty()) {
                hours.append(", ");
            }

            hours.append(userSetting);
            hours.append(":00");
        }

        return hours.toString();
    }
}
