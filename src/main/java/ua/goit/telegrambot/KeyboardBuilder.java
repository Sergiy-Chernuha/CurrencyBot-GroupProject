package ua.goit.telegrambot;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class KeyboardBuilder {
      static InlineKeyboardMarkup getChoiceBankKeyBoard() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton("Національний банк України");
        inlineKeyboardButton1.setCallbackData("NBUBank");
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(inlineKeyboardButton1);

        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton("Приват Банк");
        inlineKeyboardButton2.setCallbackData("PrivatBank");
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        keyboardButtonsRow2.add(inlineKeyboardButton2);

        InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton("МоноБанк");
        inlineKeyboardButton3.setCallbackData("MonoBank");
        List<InlineKeyboardButton> keyboardButtonsRow3 = new ArrayList<>();
        keyboardButtonsRow3.add(inlineKeyboardButton3);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);
        rowList.add(keyboardButtonsRow3);

        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    static InlineKeyboardMarkup getChoiceDecimalsKeyBoard() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton("2");
        inlineKeyboardButton1.setCallbackData("2");
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(inlineKeyboardButton1);

        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton("3");
        inlineKeyboardButton2.setCallbackData("3");
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        keyboardButtonsRow2.add(inlineKeyboardButton2);

        InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton("4");
        inlineKeyboardButton3.setCallbackData("4");
        List<InlineKeyboardButton> keyboardButtonsRow3 = new ArrayList<>();
        keyboardButtonsRow3.add(inlineKeyboardButton3);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);
        rowList.add(keyboardButtonsRow3);

        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }

    static InlineKeyboardMarkup getChoiceOptionsKeyBoard() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton("Знаки після коми");
        inlineKeyboardButton1.setCallbackData("decimals");
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(inlineKeyboardButton1);

        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton("Банк");
        inlineKeyboardButton2.setCallbackData("bank");
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        keyboardButtonsRow2.add(inlineKeyboardButton2);

        InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton("Валюти");
        inlineKeyboardButton3.setCallbackData("currencies");
        List<InlineKeyboardButton> keyboardButtonsRow3 = new ArrayList<>();
        keyboardButtonsRow3.add(inlineKeyboardButton3);

        InlineKeyboardButton inlineKeyboardButton4 = new InlineKeyboardButton("Час сповіщень");
        inlineKeyboardButton4.setCallbackData("reminders");
        List<InlineKeyboardButton> keyboardButtonsRow4 = new ArrayList<>();
        keyboardButtonsRow4.add(inlineKeyboardButton4);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);
        rowList.add(keyboardButtonsRow3);
        rowList.add(keyboardButtonsRow4);

        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    static InlineKeyboardMarkup getChoiceCurrenciesKeyBoard() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton("Євро");
        inlineKeyboardButton1.setCallbackData("EUR");
        inlineKeyboardButton1.setSwitchInlineQueryCurrentChat("+EUR"); //позволяет выбирать не только этот вариант
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(inlineKeyboardButton1);

        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton("Американський долар");
        inlineKeyboardButton2.setCallbackData("USD");
        inlineKeyboardButton2.setSwitchInlineQueryCurrentChat("+USD"); //позволяет выбирать не только этот вариант
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        keyboardButtonsRow2.add(inlineKeyboardButton2);

        InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton("Підтвердити вибір");
        inlineKeyboardButton3.setCallbackData("confirm");
        List<InlineKeyboardButton> keyboardButtonsRow3 = new ArrayList<>();
        keyboardButtonsRow3.add(inlineKeyboardButton3);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);
        rowList.add(keyboardButtonsRow3);

        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    static InlineKeyboardMarkup getChoiceReminderKeyBoard() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton("9:00");
        inlineKeyboardButton1.setCallbackData("9");
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton("10:00");
        inlineKeyboardButton2.setCallbackData("10");
        InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton("11:00");
        inlineKeyboardButton3.setCallbackData("11");
        List<InlineKeyboardButton> keyboardButtonsRow3 = new ArrayList<>();
        keyboardButtonsRow3.add(inlineKeyboardButton1);
        keyboardButtonsRow3.add(inlineKeyboardButton2);
        keyboardButtonsRow3.add(inlineKeyboardButton3);

        InlineKeyboardButton inlineKeyboardButton4 = new InlineKeyboardButton("12:00");
        inlineKeyboardButton4.setCallbackData("12");
        InlineKeyboardButton inlineKeyboardButton5 = new InlineKeyboardButton("13:00");
        inlineKeyboardButton5.setCallbackData("13");
        InlineKeyboardButton inlineKeyboardButton6 = new InlineKeyboardButton("14:00");
        inlineKeyboardButton6.setCallbackData("14");
        List<InlineKeyboardButton> keyboardButtonsRow6 = new ArrayList<>();
        keyboardButtonsRow6.add(inlineKeyboardButton4);
        keyboardButtonsRow6.add(inlineKeyboardButton5);
        keyboardButtonsRow6.add(inlineKeyboardButton6);

        InlineKeyboardButton inlineKeyboardButton7 = new InlineKeyboardButton("15:00");
        inlineKeyboardButton7.setCallbackData("15");
        InlineKeyboardButton inlineKeyboardButton8 = new InlineKeyboardButton("16:00");
        inlineKeyboardButton8.setCallbackData("16");
        InlineKeyboardButton inlineKeyboardButton9 = new InlineKeyboardButton("17:00");
        inlineKeyboardButton9.setCallbackData("17");
        List<InlineKeyboardButton> keyboardButtonsRow9 = new ArrayList<>();
        keyboardButtonsRow9.add(inlineKeyboardButton7);
        keyboardButtonsRow9.add(inlineKeyboardButton8);
        keyboardButtonsRow9.add(inlineKeyboardButton9);

        InlineKeyboardButton inlineKeyboardButton10 = new InlineKeyboardButton("18:00");
        inlineKeyboardButton10.setCallbackData("18");
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton("Вимкнути сповіщення");
        inlineKeyboardButton.setCallbackData("OffReminder");
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
        keyboardButtonsRow.add(inlineKeyboardButton10);
        keyboardButtonsRow.add(inlineKeyboardButton);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow3);
        rowList.add(keyboardButtonsRow6);
        rowList.add(keyboardButtonsRow9);
        rowList.add(keyboardButtonsRow);

        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }
}
