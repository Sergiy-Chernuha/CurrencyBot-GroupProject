package ua.goit.telegrambot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;


public class RemindersMenu {

    InlineKeyboardMarkup getChoiceReminderKeyBoard(Long chatId) {
        SomeButton button1 = new SomeButton((MyTelBot.settings.get(chatId).getReminderTime() == 9 && MyTelBot.settings.get(chatId).isReminderStarted()) ? "✅ 9:00" : "9:00", "9");
        SomeButton button2 = new SomeButton((MyTelBot.settings.get(chatId).getReminderTime() == 10 && MyTelBot.settings.get(chatId).isReminderStarted()) ? "✅ 10:00" : "10:00", "10");
        SomeButton button3 = new SomeButton((MyTelBot.settings.get(chatId).getReminderTime() == 11 && MyTelBot.settings.get(chatId).isReminderStarted()) ? "✅ 11:00" : "11:00", "11");
        SomeButton button4 = new SomeButton((MyTelBot.settings.get(chatId).getReminderTime() == 12 && MyTelBot.settings.get(chatId).isReminderStarted()) ? "✅ 12:00" : "12:00", "12");
        SomeButton button5 = new SomeButton((MyTelBot.settings.get(chatId).getReminderTime() == 13 && MyTelBot.settings.get(chatId).isReminderStarted()) ? "✅ 13:00" : "13:00", "13");
        SomeButton button6 = new SomeButton((MyTelBot.settings.get(chatId).getReminderTime() == 14 && MyTelBot.settings.get(chatId).isReminderStarted()) ? "✅ 14:00" : "14:00", "14");
        SomeButton button7 = new SomeButton((MyTelBot.settings.get(chatId).getReminderTime() == 15 && MyTelBot.settings.get(chatId).isReminderStarted()) ? "✅ 15:00" : "15:00", "15");
        SomeButton button8 = new SomeButton((MyTelBot.settings.get(chatId).getReminderTime() == 16 && MyTelBot.settings.get(chatId).isReminderStarted()) ? "✅ 16:00" : "16:00", "16");
        SomeButton button9 = new SomeButton((MyTelBot.settings.get(chatId).getReminderTime() == 17 && MyTelBot.settings.get(chatId).isReminderStarted()) ? "✅ 17:00" : "17:00", "17");
        SomeButton button10 = new SomeButton((MyTelBot.settings.get(chatId).getReminderTime() == 18 && MyTelBot.settings.get(chatId).isReminderStarted()) ? "✅ 18:00" : "18:00", "18");
        SomeButton button11 = new SomeButton((!MyTelBot.settings.get(chatId).isReminderStarted() ? "✅ Вимкнути сповіщення" : "Вимкнути сповіщення"), "OffReminder");

        String[] names = new String[]{button1.getButtonName(), button2.getButtonName(), button3.getButtonName(), button4.getButtonName(), button5.getButtonName(), button6.getButtonName()
                , button7.getButtonName(), button8.getButtonName(), button9.getButtonName(), button10.getButtonName(), button11.getButtonName()};
        String[] keys = new String[]{button1.getCallback(), button2.getCallback(), button3.getCallback(), button4.getCallback(), button5.getCallback(), button6.getCallback()
                , button7.getCallback(), button8.getCallback(), button9.getCallback(), button10.getCallback(), button11.getCallback()};

        return KeyboardBuilder.getReminderKeyboard(names, keys);
    }

    void sendChoiceReminderMessage(SendMessage sendMessage, Long chatId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = getChoiceReminderKeyBoard(chatId);

        sendMessage.setText("Оберіть час сповіщення:");
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        new MyTelBot().sendNextMessage(sendMessage);
    }
}
