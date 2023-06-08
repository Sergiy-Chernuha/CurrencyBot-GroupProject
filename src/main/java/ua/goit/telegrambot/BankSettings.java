package ua.goit.telegrambot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

public class BankSettings {

    InlineKeyboardMarkup getChoiceBankKeyBoard(Long chatId) {
        String bankNow = MyTelBot.settings.get(chatId).getBank();

        String button1Name = (bankNow.equals("NBUBank")) ? "✅ Національний банк України" : "Національний банк України";
        String callback1 = "NBUBank";

        String button2Name = (bankNow.equals("PrivatBank")) ? "✅ Приват Банк" : "Приват Банк";
        String callback2 = "PrivatBank";

        String button3Name = (bankNow.equals("MonoBank")) ? "✅ МоноБанк" : "МоноБанк";
        String callback3 = "MonoBank";

        String[] names = new String[]{button1Name, button2Name, button3Name};
        String[] keys = new String[]{callback1, callback2, callback3};

        return KeyboardBuilder.getSimpleKeyboard(names, keys);
    }

    void sendChoiceBankMessage(SendMessage sendMessage, Long chatId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = getChoiceBankKeyBoard(chatId);

        sendMessage.setText("Виберіть банк:");
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        new MyTelBot().sendNextMessage(sendMessage);
    }
}
