package ua.goit.telegrambot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ua.goit.banks.Currencies;

import java.util.List;

public class CurrencyMenu {
    InlineKeyboardMarkup getChoiceCurrenciesKeyBoard(Long chatId) {
        List<Currencies> choicesCurrenciesNow = MyTelBot.settings.get(chatId).getChoicesCurrencies();

        SomeButton button1 = new SomeButton((choicesCurrenciesNow.contains(Currencies.EUR)) ? "✅ Євро" : "Євро", "EUR");
        SomeButton button2 = new SomeButton((choicesCurrenciesNow.contains(Currencies.USD)) ? "✅ Американський долар" : "Американський долар", "USD");

        String[] names = new String[]{button1.getButtonName(), button2.getButtonName()};
        String[] keys = new String[]{button1.getCallback(), button2.getCallback()};

        return KeyboardBuilder.getSimpleKeyboard(names, keys);
    }

    void sendChoiceCurrenciesMessage(SendMessage sendMessage, Long chatId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = getChoiceCurrenciesKeyBoard(chatId);

        sendMessage.setText("Виберіть валюту:");
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        new MyTelBot().sendNextMessage(sendMessage);
    }
}
