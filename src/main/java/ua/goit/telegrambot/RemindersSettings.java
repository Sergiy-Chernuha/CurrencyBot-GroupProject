package ua.goit.telegrambot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

public class RemindersSettings {

    InlineKeyboardMarkup getChoiceReminderKeyBoard(Long chatId) {
        String button1Name = (MyTelBot.settings.get(chatId).getReminderTime() == 9 && MyTelBot.settings.get(chatId).isReminderStarted()) ? "✅ 9:00" : "9:00";
        String Callback1 = "9";
        String button2Name = (MyTelBot.settings.get(chatId).getReminderTime() == 10 && MyTelBot.settings.get(chatId).isReminderStarted()) ? "✅ 10:00" : "10:00";
        String Callback2 = "10";
        String button3Name = (MyTelBot.settings.get(chatId).getReminderTime() == 11 && MyTelBot.settings.get(chatId).isReminderStarted()) ? "✅ 11:00" : "11:00";
        String Callback3 = "11";

        String button4Name = (MyTelBot.settings.get(chatId).getReminderTime() == 12 && MyTelBot.settings.get(chatId).isReminderStarted()) ? "✅ 12:00" : "12:00";
        String Callback4 = "12";
        String button5Name = (MyTelBot.settings.get(chatId).getReminderTime() == 13 && MyTelBot.settings.get(chatId).isReminderStarted()) ? "✅ 13:00" : "13:00";
        String Callback5 = "13";
        String button6Name = (MyTelBot.settings.get(chatId).getReminderTime() == 14 && MyTelBot.settings.get(chatId).isReminderStarted()) ? "✅ 14:00" : "14:00";
        String Callback6 = "14";

        String button7Name = (MyTelBot.settings.get(chatId).getReminderTime() == 15 && MyTelBot.settings.get(chatId).isReminderStarted()) ? "✅ 15:00" : "15:00";
        String Callback7 = "15";
        String button8Name = (MyTelBot.settings.get(chatId).getReminderTime() == 16 && MyTelBot.settings.get(chatId).isReminderStarted()) ? "✅ 16:00" : "16:00";
        String Callback8 = "16";
        String button9Name = (MyTelBot.settings.get(chatId).getReminderTime() == 17 && MyTelBot.settings.get(chatId).isReminderStarted()) ? "✅ 17:00" : "17:00";
        String Callback9 = "17";

        String button10Name = (MyTelBot.settings.get(chatId).getReminderTime() == 18 && MyTelBot.settings.get(chatId).isReminderStarted()) ? "✅ 18:00" : "18:00";
        String Callback10 = "18";
        String button11Name = !MyTelBot.settings.get(chatId).isReminderStarted() ? "✅ Вимкнути сповіщення" : "Вимкнути сповіщення";
        String Callback11 = "OffReminder";

        String[] names = new String[]{button1Name, button2Name, button3Name, button4Name, button5Name, button6Name
                , button7Name, button8Name, button9Name, button10Name, button11Name};
        String[] keys = new String[]{Callback1, Callback2, Callback3, Callback4, Callback5, Callback6
                , Callback7, Callback8, Callback9, Callback10, Callback11};

        return KeyboardBuilder.getReminderKeyboard(names, keys);
    }

    void sendChoiceReminderMessage(SendMessage sendMessage, Long chatId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = getChoiceReminderKeyBoard(chatId);

        sendMessage.setText("Оберіть час сповіщення:");
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        new MyTelBot().sendNextMessage(sendMessage);
    }
}
