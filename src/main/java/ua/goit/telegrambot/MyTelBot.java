package ua.goit.telegrambot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.goit.banks.Banks;
import ua.goit.banks.WorkingCurrency;
import ua.goit.banks.BankFactory;
import ua.goit.userssetting.ChatBotSettings;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class MyTelBot extends TelegramLongPollingBot {

    private final ChatBotSettings options;

    public MyTelBot() {
        options = new ChatBotSettings();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            if (update.getMessage().hasText()) {
                if (update.getMessage().getText().equals("/start")) {
                    sendNextMessage(sendHelloMessage(update.getMessage().getChatId()));
                } else if (update.getMessage().getText().equals("/end")) {
                    sendNextMessage(sendEndMessage(update.getMessage().getChatId()));
                    System.exit(0);
                }
            }
        } else if (update.hasCallbackQuery()) {
            System.out.print("id user= " + update.getCallbackQuery().getMessage().getChatId() + "  ");
            String inputQueryMessage = String.valueOf(update.getCallbackQuery().getData());
            SendMessage sendMessage = new SendMessage();

            sendMessage.setChatId(String.valueOf(update.getCallbackQuery().getMessage().getChatId()));

            switch (inputQueryMessage) {
                case ("current") -> {
                    sendMessage.setText(getCurrentData());
                    sendNextMessage(sendMessage);
                }
                case ("options") -> sendNextMessage(sendChoiceBankMessage(sendMessage));
                case ("NBUBank"), ("PrivatBank"), ("MonoBank") -> {
                    Banks newBank = BankFactory.getBank(inputQueryMessage);
                    options.setBank(newBank);
                    sendNextMessage(sendUpdatedSettingMessage(sendMessage));
                }
                default -> {
                    sendMessage.setText("Тут може бути ваша реклама): " + update.getCallbackQuery().getData());
                    sendNextMessage(sendMessage);
                }
            }
        }
    }

    private void sendNextMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private SendMessage sendHelloMessage(long chatId) {
        SendMessage sendMessage = new SendMessage();
        InlineKeyboardMarkup inlineKeyboardMarkup = getDefaultKeyBoard();

        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText("Ласкаво просимо. Цей бот допоможе відслідковувати актуальні курси валют");
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        return sendMessage;
    }

    private SendMessage sendEndMessage(long chatId) {
        SendMessage sendEndMessage = new SendMessage();
        sendEndMessage.setChatId(String.valueOf(chatId));
        sendEndMessage.setText("До зустрічі!");

        return sendEndMessage;
    }

    private SendMessage sendChoiceBankMessage(SendMessage sendMessage) {
        InlineKeyboardMarkup inlineKeyboardMarkup = getChoiceBankKeyBoard();

        sendMessage.setText("Виберіть банк");
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        return sendMessage;
    }

    private SendMessage sendUpdatedSettingMessage(SendMessage sendMessage) {
        InlineKeyboardMarkup inlineKeyboardMarkup = getDefaultKeyBoard();

        sendMessage.setText("Налаштування оновлені");
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        return sendMessage;
    }

    private InlineKeyboardMarkup getDefaultKeyBoard() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton("Отримати інфо");
        inlineKeyboardButton1.setCallbackData("current");
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(inlineKeyboardButton1);

        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton("Налаштування");
        inlineKeyboardButton2.setCallbackData("options");
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        keyboardButtonsRow2.add(inlineKeyboardButton2);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);

        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    private InlineKeyboardMarkup getChoiceBankKeyBoard() {
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

    private String getCurrentData() {
        StringBuilder result = new StringBuilder();
        int numberOfDecimal = options.getNumberOfDecimal();

        try {
            options.getBank().updateCurrentData();
        } catch (IOException e) {
            System.out.println("No bank connection");
        }

        result.append("Курс в ");
        result.append(options.getBank().getName());
        result.append(": \n");

        for (WorkingCurrency current : options.getBank().getCurrencies()) {
            if (!options.getChoicesCurrencies().contains(current.getName())) {
                continue;
            }

            result.append("\n");
            result.append(current.getName());
            result.append("/UAH\n");
            result.append("   Продаж:");
            result.append(String.format("%." + numberOfDecimal + "f\n", current.getCurrencySellingRate()));
            result.append("   Купівля:");
            result.append(String.format("%." + numberOfDecimal + "f", current.getCurrencyBuyingRate()));
        }

        return result.toString();
    }

    @Override
    public String getBotUsername() {
        return "BlackBot23_bot";
    }

    // добавить имя и токен своего бота, они не подлежат заливке в GitHub
    @Override
    public String getBotToken() {
        return null;
    }
}
