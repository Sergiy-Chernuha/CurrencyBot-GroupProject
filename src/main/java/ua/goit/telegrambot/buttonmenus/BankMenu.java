package ua.goit.telegrambot.buttonmenus;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ua.goit.telegrambot.KeyboardBuilder;
import ua.goit.telegrambot.MyTelBot;
import ua.goit.userssetting.ChatBotSettings;

import java.util.List;

public class BankMenu {
    public static InlineKeyboardMarkup getChoiceBankKeyBoard(ChatBotSettings userSettings) {
        List<String> banksNow  = userSettings.getBanks();

        ButtonValue[] buttons = new ButtonValue[]{
                new ButtonValue((banksNow.contains("NBUBank")) ? "✅ Національний банк України" : "Національний банк України", "NBUBank")
                , new ButtonValue((banksNow.contains("PrivatBank")) ? "✅ Приват Банк" : "Приват Банк", "PrivatBank")
                , new ButtonValue((banksNow.contains("MonoBank")) ? "✅ МоноБанк" : "МоноБанк", "MonoBank")};

        return KeyboardBuilder.getSimpleKeyboard(buttons);
    }

    public static void sendChoiceBankMessage(SendMessage sendMessage, ChatBotSettings userSettings) {
        InlineKeyboardMarkup inlineKeyboardMarkup = getChoiceBankKeyBoard(userSettings);

        sendMessage.setText("Виберіть банки:");
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        new MyTelBot().sendNextMessage(sendMessage);
    }
}
