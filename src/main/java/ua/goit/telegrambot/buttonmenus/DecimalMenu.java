package ua.goit.telegrambot.buttonmenus;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ua.goit.telegrambot.KeyboardBuilder;
import ua.goit.telegrambot.MyTelBot;
import ua.goit.userssetting.ChatBotSettings;

public class DecimalMenu {

    public static InlineKeyboardMarkup getChoiceDecimalsKeyBoard(ChatBotSettings userSettings) {
        int currentDecimal = userSettings.getNumberOfDecimal();

        ButtonValue[] buttons = new ButtonValue[]{
                new ButtonValue((currentDecimal == 2) ? "✅ 2" : "2", "2")
                , new ButtonValue((currentDecimal == 3) ? "✅ 3" : "3", "3")
                , new ButtonValue((currentDecimal == 4) ? "✅ 4" : "4", "4")};

        return KeyboardBuilder.getSimpleKeyboard(buttons);
    }

    public static void sendChoiceDecimalsMessage(SendMessage sendMessage, ChatBotSettings userSettings) {
        InlineKeyboardMarkup inlineKeyboardMarkup = getChoiceDecimalsKeyBoard(userSettings);

        sendMessage.setText("Виберіть кількість знаків після коми:");
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        new MyTelBot().sendNextMessage(sendMessage);
    }
}
