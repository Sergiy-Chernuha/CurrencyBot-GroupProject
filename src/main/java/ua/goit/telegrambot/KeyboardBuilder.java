package ua.goit.telegrambot;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ua.goit.telegrambot.buttonmenus.ButtonValue;

import java.util.ArrayList;
import java.util.List;

public class KeyboardBuilder {

    public static InlineKeyboardMarkup getSimpleKeyboard(ButtonValue[] buttons) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        for (ButtonValue button : buttons) {
            List<InlineKeyboardButton> inlineKeyboardRow = new ArrayList<>();

            inlineKeyboardRow.add(getButton(button));
            rowList.add(inlineKeyboardRow);
        }
        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }

    public static InlineKeyboardMarkup getReminderKeyboard(ButtonValue[] buttons) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        int count = 0;
        for (int i = 0; i < 4; i++) {
            List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
            for (int j = 0; j < 3; j++) {
                if (count < buttons.length) {
                    keyboardButtonsRow.add(getButton(buttons[count]));
                    count++;
                }
            }
            rowList.add(keyboardButtonsRow);
        }

        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    public static InlineKeyboardButton getButton(ButtonValue buttonValue) {
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton(buttonValue.getButtonName());
        inlineKeyboardButton.setCallbackData(buttonValue.getCallback());

        return inlineKeyboardButton;
    }
}
