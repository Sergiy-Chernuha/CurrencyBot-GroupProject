package ua.goit.telegrambot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ua.goit.banks.Currencies;
import ua.goit.userssetting.ChatBotSettings;

import java.util.ArrayList;
import java.util.List;

public class TelegramBotUtils {

    SendMessage sendHelloMessage(long chatId) {
        SendMessage sendMessage = new SendMessage();
        ReplyKeyboardMarkup replyKeyboardMarkup = getDefaultKeyBoard();

        sendMessage.setChatId(chatId);
        sendMessage.setText("Ласкаво просимо. Цей бот допоможе відслідковувати актуальні курси валют");
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        return sendMessage;
    }

    SendMessage sendEndMessage(long chatId) {
        SendMessage sendEndMessage = new SendMessage();
        sendEndMessage.setChatId(chatId);
        sendEndMessage.setText("До зустрічі!");

        return sendEndMessage;
    }

    SendMessage sendCurrentSettingsMessage(long chatId, ChatBotSettings userSettings){
        SendMessage sendCurrentSettingsMessage = new SendMessage();
        sendCurrentSettingsMessage.setChatId(chatId);
        String currencies = (userSettings.getChoicesCurrencies().size() > 1) ? "Валюти: " + userSettings.getChoicesCurrencies() : "Валюта: " + userSettings.getChoicesCurrencies();
        String reminders = (userSettings.isReminderStarted()) ? "Сповіщення на " + userSettings.getReminderTime() : "Сповіщення вимкнені";
        sendCurrentSettingsMessage.setText(currencies + "\nЗнаки після коми: " +
                userSettings.getNumberOfDecimal() + "\n" + reminders + "\n" +
                "Банк: " + userSettings.getBank());
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
}
