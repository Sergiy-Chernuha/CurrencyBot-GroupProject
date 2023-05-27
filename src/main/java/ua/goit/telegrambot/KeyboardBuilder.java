package ua.goit.telegrambot;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class KeyboardBuilder {

      static InlineKeyboardMarkup getSimpleKeyboard(String[] names, String[] keys, String key) {
          InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
          List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

          if(key.equals("simple")) {
              for (int i = 0; i < names.length; i++) {
                  List<InlineKeyboardButton> inlineKeyboardRow = new ArrayList<>();
                  inlineKeyboardRow.add(getButton(names[i], keys[i]));
                  rowList.add(inlineKeyboardRow);
              }
          } else if(key.equals("reminder")){
              int count = 0;
              for (int i = 0; i < 4; i++) {
                  List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
                  for (int j = 0; j < 3; j++) {
                      keyboardButtonsRow.add(getButton(names[count],keys[count]));
                      count++;
                  }
                  rowList.add(keyboardButtonsRow);
              }
          }

          inlineKeyboardMarkup.setKeyboard(rowList);
          return inlineKeyboardMarkup;
    }

    public static InlineKeyboardButton getButton(String name, String key) {
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton(name);
        inlineKeyboardButton.setCallbackData(key);

        return inlineKeyboardButton;
    }
}
