package ua.goit.telegrambot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

public class DecimalMenu {

    InlineKeyboardMarkup getChoiceDecimalsKeyBoard(Long chatId) {

        SomeButton button1 = new SomeButton((MyTelBot.settings.get(chatId).getNumberOfDecimal() == 2) ? "✅ 2" : "2", "2");
        SomeButton button2 = new SomeButton((MyTelBot.settings.get(chatId).getNumberOfDecimal() == 3) ? "✅ 3" : "3", "3");
        SomeButton button3 = new SomeButton((MyTelBot.settings.get(chatId).getNumberOfDecimal() == 4) ? "✅ 4" : "4", "4");

        String[] names = new String[]{button1.getButtonName(), button2.getButtonName(), button3.getButtonName()};
        String[] keys = new String[]{button1.getCallback(), button2.getCallback(), button3.getCallback()};

        return KeyboardBuilder.getSimpleKeyboard(names, keys);
    }

    void sendChoiceDecimalsMessage(SendMessage sendMessage, Long chatId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = getChoiceDecimalsKeyBoard(chatId);

        sendMessage.setText("Виберіть кількість знаків після коми:");
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        new MyTelBot().sendNextMessage(sendMessage);
    }
}
