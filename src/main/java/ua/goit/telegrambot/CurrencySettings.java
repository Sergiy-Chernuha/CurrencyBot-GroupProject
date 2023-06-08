package ua.goit.telegrambot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ua.goit.banks.Currencies;

import java.util.List;

public class CurrencySettings {
    InlineKeyboardMarkup getChoiceCurrenciesKeyBoard(Long chatId) {
        List<Currencies> choicesCurrenciesNow = MyTelBot.settings.get(chatId).getChoicesCurrencies();

        String button1Name = (choicesCurrenciesNow.contains(Currencies.EUR)) ? "✅ Євро" : "Євро";
        String Callback1 = "EUR";

        String button2Name = (choicesCurrenciesNow.contains(Currencies.USD)) ? "✅ Американський долар" : "Американський долар";
        String Callback2 = "USD";

        String[] names = new String[]{button1Name, button2Name};
        String[] keys = new String[]{Callback1, Callback2};

        return KeyboardBuilder.getSimpleKeyboard(names, keys);
    }

    void sendChoiceCurrenciesMessage(SendMessage sendMessage, Long chatId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = getChoiceCurrenciesKeyBoard(chatId);

        sendMessage.setText("Виберіть валюту:");
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        new MyTelBot().sendNextMessage(sendMessage);
    }
}
