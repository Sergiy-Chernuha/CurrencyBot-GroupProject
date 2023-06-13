package ua.goit.telegrambot.buttonmenus;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ua.goit.banks.Currencies;
import ua.goit.telegrambot.KeyboardBuilder;
import ua.goit.telegrambot.MyTelBot;
import ua.goit.userssetting.ChatBotSettings;

import java.util.List;

public class CurrencyMenu {
    public static InlineKeyboardMarkup getChoiceCurrenciesKeyBoard(ChatBotSettings userSettings) {
        List<Currencies> choicesCurrenciesNow = userSettings.getChoicesCurrencies();

        ButtonValue[] buttons = new ButtonValue[]{
                new ButtonValue((choicesCurrenciesNow.contains(Currencies.EUR)) ? "✅ Євро" : "Євро", "EUR")
                , new ButtonValue((choicesCurrenciesNow.contains(Currencies.USD)) ? "✅ Американський долар" : "Американський долар", "USD")};

        return KeyboardBuilder.getSimpleKeyboard(buttons);
    }

    public static void sendChoiceCurrenciesMessage(SendMessage sendMessage, ChatBotSettings userSettings) {
        InlineKeyboardMarkup inlineKeyboardMarkup = getChoiceCurrenciesKeyBoard(userSettings);

        sendMessage.setText("Виберіть валюту:");
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        new MyTelBot().sendNextMessage(sendMessage);
    }
}
