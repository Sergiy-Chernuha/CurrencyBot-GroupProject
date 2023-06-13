package ua.goit.telegrambot.buttonmenus;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ua.goit.telegrambot.KeyboardBuilder;
import ua.goit.telegrambot.MyTelBot;
import ua.goit.userssetting.ChatBotSettings;

public class RemindersMenu {

    public static InlineKeyboardMarkup getChoiceReminderKeyBoard(ChatBotSettings userSettings) {
        int reminderTime = userSettings.getReminderTime();
        boolean reminderStarted = userSettings.isReminderStarted();

        ButtonValue[] buttons = new ButtonValue[]{
                new ButtonValue((reminderTime == 9 && reminderStarted) ? "✅ 9:00" : "9:00", "9")
                , new ButtonValue((reminderTime == 10 && reminderStarted) ? "✅ 10:00" : "10:00", "10")
                , new ButtonValue((reminderTime == 11 && reminderStarted) ? "✅ 11:00" : "11:00", "11")
                , new ButtonValue((reminderTime == 12 && reminderStarted) ? "✅ 12:00" : "12:00", "12")
                , new ButtonValue((reminderTime == 13 && reminderStarted) ? "✅ 13:00" : "13:00", "13")
                , new ButtonValue((reminderTime == 14 && reminderStarted) ? "✅ 14:00" : "14:00", "14")
                , new ButtonValue((reminderTime == 15 && reminderStarted) ? "✅ 15:00" : "15:00", "15")
                , new ButtonValue((reminderTime == 16 && reminderStarted) ? "✅ 16:00" : "16:00", "16")
                , new ButtonValue((reminderTime == 17 && reminderStarted) ? "✅ 17:00" : "17:00", "17")
                , new ButtonValue((reminderTime == 18 && reminderStarted) ? "✅ 18:00" : "18:00", "18")
                , new ButtonValue((!reminderStarted ? "✅ Вимкнути сповіщення" : "Вимкнути сповіщення"), "OffReminder")};

        return KeyboardBuilder.getReminderKeyboard(buttons);
    }

    public static void sendChoiceReminderMessage(SendMessage sendMessage, ChatBotSettings userSettings) {
        InlineKeyboardMarkup inlineKeyboardMarkup = getChoiceReminderKeyBoard(userSettings);

        sendMessage.setText("Оберіть час сповіщення:");
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        new MyTelBot().sendNextMessage(sendMessage);
    }
}
