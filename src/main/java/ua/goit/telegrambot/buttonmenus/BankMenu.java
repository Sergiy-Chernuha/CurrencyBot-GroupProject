package ua.goit.telegrambot.buttonmenus;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ua.goit.telegrambot.KeyboardBuilder;
import ua.goit.telegrambot.MyTelBot;
import ua.goit.userssetting.ChatBotSettings;

public class BankMenu {

    public static InlineKeyboardMarkup getChoiceBankKeyBoard(ChatBotSettings userSettings) {
        String bankNow = userSettings.getBank();

        ButtonValue[] buttons = new ButtonValue[]{
                new ButtonValue((bankNow.equals("NBUBank")) ? "✅ Національний банк України" : "Національний банк України", "NBUBank")
                , new ButtonValue((bankNow.equals("PrivatBank")) ? "✅ Приват Банк" : "Приват Банк", "PrivatBank")
                , new ButtonValue((bankNow.equals("MonoBank")) ? "✅ МоноБанк" : "МоноБанк", "MonoBank")};

        return KeyboardBuilder.getSimpleKeyboard(buttons);
    }

    public static void sendChoiceBankMessage(SendMessage sendMessage, ChatBotSettings userSettings) {
        InlineKeyboardMarkup inlineKeyboardMarkup = getChoiceBankKeyBoard(userSettings);

        sendMessage.setText("Виберіть банк:");
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        new MyTelBot().sendNextMessage(sendMessage);
    }
}
