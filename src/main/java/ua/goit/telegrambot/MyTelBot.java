package ua.goit.telegrambot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.goit.SecondThreadAlertTime;
import ua.goit.banks.Banks;
import ua.goit.banks.Currencies;
import ua.goit.banks.WorkingCurrency;
import ua.goit.banks.BankFactory;
import ua.goit.userssetting.ChatBotSettings;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;


public class MyTelBot extends TelegramLongPollingBot {

    private final ChatBotSettings userSettings;
    private final SecondThreadAlertTime secondThreadAlertTime;

    public MyTelBot() {
        userSettings = new ChatBotSettings();
        secondThreadAlertTime= new SecondThreadAlertTime(this);
    }

    public ChatBotSettings getUserSettings() {
        return userSettings;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            if (update.getMessage().hasText()) {
                if (update.getMessage().getText().equals("/start")) {
                    sendNextMessage(sendHelloMessage(update.getMessage().getChatId()));
                    userSettings.setChatId(update.getMessage().getChatId());
                } else if (update.getMessage().getText().equals("/end")) {
                    sendNextMessage(sendEndMessage(update.getMessage().getChatId()));
                    System.exit(0);
                }
            }
        } else if (update.hasCallbackQuery()) {
//            Нужно убрать следующую строку перед финишем.
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
                case ("bank") -> sendNextMessage(sendChoiceBankMessage(sendMessage));
                case ("decimals") -> sendNextMessage(sendChoiceDecimalsMessage(sendMessage));
                case ("currencies") -> sendNextMessage(sendChoiceCurrenciesMessage(sendMessage));
                case ("USD") -> {
                    userSettings.setChoicesCurrencies(List.of(Currencies.USD));
                    sendNextMessage(sendUpdatedSettingMessage(sendMessage));
                }
                case ("EUR") -> {
                    userSettings.setChoicesCurrencies(List.of(Currencies.EUR));
                    sendNextMessage(sendUpdatedSettingMessage(sendMessage));
                }
                case("two") -> {
                    userSettings.setNumberOfDecimal(2);
                    sendNextMessage(sendUpdatedSettingMessage(sendMessage));
                }
                case("three") -> {
                    userSettings.setNumberOfDecimal(3);
                    sendNextMessage(sendUpdatedSettingMessage(sendMessage));
                }
                case("four") -> {
                    userSettings.setNumberOfDecimal(4);
                    sendNextMessage(sendUpdatedSettingMessage(sendMessage));
                }
                case ("NBUBank"), ("PrivatBank"), ("MonoBank") -> {
                    Banks newBank = BankFactory.getBank(inputQueryMessage);
                    userSettings.setBank(newBank);
                    sendNextMessage(sendUpdatedSettingMessage(sendMessage));
                }
                case ("reminders") -> sendNextMessage(sendChoiceReminderMessage(sendMessage));
                case ("9"), ("10"), ("11"), ("12"), ("13"), ("14"), ("15"), ("16"), ("17"), ("18") -> {

                    userSettings.setAlerts(true);
                    sendNextMessage(sendUpdatedSettingMessage(sendMessage));
                    System.out.println("start second thread1");
                    userSettings.setChatId(update.getCallbackQuery().getMessage().getChatId());
                    secondThreadAlertTime.start();
                }
                case ("OffReminder") -> {
                    sendNextMessage(sendUpdatedSettingMessage(sendMessage));
                    userSettings.setAlerts(false);
//                    try {
//                        secondThreadAlertTime.wait();
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }
                }
                default -> {
                    sendMessage.setText("Тут може бути ваша реклама): " + update.getCallbackQuery().getData());
                    sendNextMessage(sendMessage);
                }
            }
        }
    }

    public void sendNextMessage(SendMessage message) {
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

    private SendMessage sendChoiceReminderMessage(SendMessage sendMessage) {
        InlineKeyboardMarkup inlineKeyboardMarkup = getChoiceReminderKeyBoard();

        sendMessage.setText("Оберіть час сповіщення:");
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

    private InlineKeyboardMarkup getChoiceCurrenciesKeyBoard(){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton("Євро");
        inlineKeyboardButton1.setCallbackData("EUR");
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(inlineKeyboardButton1);

        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton("Американський долар");
        inlineKeyboardButton2.setCallbackData("USD");
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        keyboardButtonsRow2.add(inlineKeyboardButton2);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);

        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    private InlineKeyboardMarkup getChoiceReminderKeyBoard() {
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

    public void sendMessageFromThread(SendMessage sendMessage){
        sendMessage.setText(getCurrentData());
        sendNextMessage(sendMessage);

//        SendMessage sendMessage = new SendMessage();
//
//        sendMessage.setText(getCurrentData());
//        sendMessage.setChatId(options.getChatId());
//        sendNextMessage(sendMessage);
    }

    public String getCurrentData() {
        StringBuilder result = new StringBuilder();
        int numberOfDecimal = userSettings.getNumberOfDecimal();

        try {
            userSettings.getBank().updateCurrentData();
        } catch (IOException e) {
            System.out.println("No bank connection");
        }

        result.append("Курс в ");
        result.append(userSettings.getBank().getName());
        result.append(": \n");

        for (WorkingCurrency current : userSettings.getBank().getCurrencies()) {
            if (!userSettings.getChoicesCurrencies().contains(current.getName())) {
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

    @Override
    public String getBotToken() {
        return "5542489649:AAETFAJZ4_C9vNCiT71yp8ET5hohTHomiiw";
    }
}

// class RemindeTimer extends Thread {
//     private final ChatBotSettings options;
//
//     public RemindeTimer(ChatBotSettings options) {
//         this.options = options;
//     }
//    @Override
//    public void run() {
//
//        while (options.isAlerts()) {
//            try {
//                Thread.sleep(60000);
//                System.out.println("not time");
//            } catch (InterruptedException e) {
//
//            }
//
//
//            if (LocalTime.now().getHour() == options.getAlertTime()
//                    && LocalTime.now().getMinute() == 0) {
////                SettingUtils
//
//            }
//        }
//
//        System.out.println("end second thread");
//    }
//}
