package ua.goit.telegrambot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.goit.banks.Banks;
import ua.goit.banks.Currencies;
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

    private final List<String> choicesCurrencies = new ArrayList<>();

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            if (update.getMessage().hasText() && update.getMessage().getText().equals("/start")) {
                sendNextMessage(sendHelloMessage(update.getMessage().getChatId()));
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
                case ("options") -> sendNextMessage(sendChoiceOptionsMessage(sendMessage));
                case("bank") -> sendNextMessage(sendChoiceBankMessage(sendMessage));
                case("decimals") -> sendNextMessage(sendChoiceDecimalsMessage(sendMessage));
                case("currencies") -> sendNextMessage(sendChoiceCurrenciesMessage(sendMessage));
                case("USD"), ("EUR") -> {

                    if(choicesCurrencies.contains(inputQueryMessage)){
                        if(choicesCurrencies.size()>1){
                            choicesCurrencies.remove(inputQueryMessage);
                        }
                    }
                    else{
                        choicesCurrencies.add(inputQueryMessage);
                    }

                }
                case("confirm") -> { //в этом блоке добавляем сохраненные валюты (1 или 2) в настройки
                    List<Currencies> currencies = new ArrayList<>();
                    for(String currency : choicesCurrencies){
                        currencies.add(Currencies.valueOf(currency));
                    }
                    options.setChoicesCurrencies(currencies);

                    choicesCurrencies.clear();

                    sendNextMessage(sendUpdatedSettingMessage(sendMessage));
                }
                case("two") -> {
                    options.setNumberOfDecimal(2);
                    sendNextMessage(sendUpdatedSettingMessage(sendMessage));
                }
                case("three") -> {
                    options.setNumberOfDecimal(3);
                    sendNextMessage(sendUpdatedSettingMessage(sendMessage));
                }
                case("four") -> {
                    options.setNumberOfDecimal(4);
                    sendNextMessage(sendUpdatedSettingMessage(sendMessage));
                }
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

    private SendMessage sendChoiceOptionsMessage(SendMessage sendMessage) {
        InlineKeyboardMarkup inlineKeyboardMarkup = getChoiceOptionsKeyBoard();

        sendMessage.setText("Налаштування");
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        return sendMessage;
    }

    private SendMessage sendChoiceDecimalsMessage(SendMessage sendMessage) {
        InlineKeyboardMarkup inlineKeyboardMarkup = getChoiceDecimalsKeyBoard();

        sendMessage.setText("Виберіть кількість знаків після коми:");
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        return sendMessage;
    }

    private SendMessage sendChoiceCurrenciesMessage(SendMessage sendMessage) {
        InlineKeyboardMarkup inlineKeyboardMarkup = getChoiceCurrenciesKeyBoard();

        sendMessage.setText("Виберіть валюту:");
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        return sendMessage;
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

    private InlineKeyboardMarkup getChoiceDecimalsKeyBoard(){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton("2");
        inlineKeyboardButton1.setCallbackData("two");
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(inlineKeyboardButton1);

        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton("3");
        inlineKeyboardButton2.setCallbackData("three");
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        keyboardButtonsRow2.add(inlineKeyboardButton2);

        InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton("4");
        inlineKeyboardButton3.setCallbackData("four");
        List<InlineKeyboardButton> keyboardButtonsRow3 = new ArrayList<>();
        keyboardButtonsRow3.add(inlineKeyboardButton3);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);
        rowList.add(keyboardButtonsRow3);

        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }

    private InlineKeyboardMarkup getChoiceOptionsKeyBoard(){
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
        inlineKeyboardButton4.setCallbackData("notifications");
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

    private InlineKeyboardMarkup getChoiceCurrenciesKeyBoard(){
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
