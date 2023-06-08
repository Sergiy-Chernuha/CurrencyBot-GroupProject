package ua.goit.telegrambot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

public class DecimalsSettings {

    InlineKeyboardMarkup getChoiceDecimalsKeyBoard(Long chatId) {
        
        String button1Name = (MyTelBot.settings.get(chatId).getNumberOfDecimal() == 2) ? "✅ 2" : "2";
        String Callback1 = "2";

        String button2Name = (MyTelBot.settings.get(chatId).getNumberOfDecimal() == 3) ? "✅ 3" : "3";
        String Callback2 = "3";

        String button3Name = (MyTelBot.settings.get(chatId).getNumberOfDecimal() == 4) ? "✅ 4" : "4";
        String Callback3 = "4";

        String[] names = new String[]{button1Name, button2Name, button3Name};
        String[] keys = new String[]{Callback1, Callback2, Callback3};

        return KeyboardBuilder.getSimpleKeyboard(names, keys);
    }

    void sendChoiceDecimalsMessage(SendMessage sendMessage, Long chatId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = getChoiceDecimalsKeyBoard(chatId);

        sendMessage.setText("Виберіть кількість знаків після коми:");
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        new MyTelBot().sendNextMessage(sendMessage);
    }
}
