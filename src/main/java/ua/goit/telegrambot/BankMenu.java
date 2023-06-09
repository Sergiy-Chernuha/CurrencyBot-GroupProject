package ua.goit.telegrambot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

public class BankMenu {

    InlineKeyboardMarkup getChoiceBankKeyBoard(Long chatId) {
        String bankNow = MyTelBot.settings.get(chatId).getBank();

        SomeButton button1 = new SomeButton((bankNow.equals("NBUBank")) ? "✅ Національний банк України" : "Національний банк України", "NBUBank");
        SomeButton button2 = new SomeButton((bankNow.equals("PrivatBank")) ? "✅ Приват Банк" : "Приват Банк", "PrivatBank");
        SomeButton button3 = new SomeButton((bankNow.equals("MonoBank")) ? "✅ МоноБанк" : "МоноБанк", "MonoBank");

        String[] names = new String[]{button1.getButtonName(), button2.getButtonName(), button3.getButtonName()};
        String[] keys = new String[]{button1.getCallback(), button2.getCallback(), button3.getCallback()};

        return KeyboardBuilder.getSimpleKeyboard(names, keys);
    }

    void sendChoiceBankMessage(SendMessage sendMessage, Long chatId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = getChoiceBankKeyBoard(chatId);

        sendMessage.setText("Виберіть банк:");
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        new MyTelBot().sendNextMessage(sendMessage);
    }
}
