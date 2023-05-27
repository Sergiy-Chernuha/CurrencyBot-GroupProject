package ua.goit.telegrambot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ua.goit.banks.Banks;
import ua.goit.banks.BankFactory;
import ua.goit.banks.Currencies;
import ua.goit.banks.monobank.MonoBank;
import ua.goit.banks.nbubank.NBUBank;
import ua.goit.banks.privatbank.PrivatBank;
import ua.goit.userssetting.ChatBotSettings;
import ua.goit.userssetting.SettingUtils;

import java.util.List;
import java.util.ArrayList;

public class MyTelBot extends TelegramLongPollingBot {

    private final ChatBotSettings userSettings;
    private ReminderTimer secondThreadReminderTime;

    public MyTelBot() {
        userSettings = new ChatBotSettings();
        secondThreadReminderTime = new ReminderTimer(this);
    }

    public ChatBotSettings getUserSettings() {
        return userSettings;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            Long chatId = update.getMessage().getChatId();

            if (message.hasText()) {
                String text = message.getText();
                SendMessage sendMessage = new SendMessage();

                sendMessage.setChatId(String.valueOf(chatId));

                if (text.equals("/start")) {
                    sendNextMessage(sendHelloMessage(chatId));
                    userSettings.setChatId(chatId);
                } else if (text.equals("Отримати інфо")) {
                    sendMessage.setText(SettingUtils.getCurrentData(userSettings));
                    sendNextMessage(sendMessage);
                } else if (text.equals("Налаштування")) {
                    sendNextMessage(sendChoiceOptionsMessage(sendMessage));
                } else if (text.equals("/end")) {
                    sendNextMessage(sendEndMessage(chatId));
                    System.exit(0);
                }
            }

        } else if (update.hasCallbackQuery()) {
            final Long chatId = update.getCallbackQuery().getMessage().getChatId();
            final Integer messageId = update.getCallbackQuery().getMessage().getMessageId();

            //            Нужно убрать следующую строку перед финишем.
            System.out.println("id user= " + chatId + "  ");
            String inputQueryMessage = String.valueOf(update.getCallbackQuery().getData());

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);

            EditMessageReplyMarkup editMessage = new EditMessageReplyMarkup();
            editMessage.setChatId(chatId);
            editMessage.setMessageId(messageId);

            switch (inputQueryMessage) {
                case ("bank") -> sendNextMessage(sendChoiceBankMessage(sendMessage));
                case ("decimals") -> sendNextMessage(sendChoiceDecimalsMessage(sendMessage));
                case ("currencies") -> sendNextMessage(sendChoiceCurrenciesMessage(sendMessage));
                case ("USD"), ("EUR") -> {
                    List<Currencies> choicesCurrenciesNow = new ArrayList<>(userSettings.getChoicesCurrencies());
                    Currencies newCurrency = Currencies.valueOf(inputQueryMessage);

                    if (choicesCurrenciesNow.contains(newCurrency)) {
                        if (choicesCurrenciesNow.size() > 1) {
                            choicesCurrenciesNow.remove(newCurrency);
                        }
                    } else {
                        choicesCurrenciesNow.add(newCurrency);
                    }
//11111
                    sendNextMessage(sendUpdatedSettingMessage(sendMessage, choicesCurrenciesNow.toString()));
                    userSettings.setChoicesCurrencies(choicesCurrenciesNow);

                    InlineKeyboardMarkup keyboardMarkup = getChoiceCurrenciesKeyBoard();

                    editMessage.setReplyMarkup(keyboardMarkup);
                    sendNextEditMessage(editMessage);
//                    111a
                    for (String currency : choicesCurrencies) {
                        newCurrenciesList.add(Currencies.valueOf(currency));
                    }
                    AnswerCallbackQuery answerCallbackQuery = sendUpdatedSettingMessage(new AnswerCallbackQuery(), newCurrenciesList.toString());
                    sendNextQuery(this, answerCallbackQuery);
                    userSettings.setChoicesCurrencies(newCurrenciesList);
//                111111
                }
                case ("2"), ("3"), ("4") -> {
                    AnswerCallbackQuery answerCallbackQuery = sendUpdatedSettingMessage(new AnswerCallbackQuery(), inputQueryMessage);
                    //sendNextMessage(sendUpdatedSettingMessage(sendMessage, inputQueryMessage));
                    sendNextQuery(this, answerCallbackQuery);
                    userSettings.setNumberOfDecimal(Integer.parseInt(inputQueryMessage));

                    InlineKeyboardMarkup keyboardMarkup = getChoiceDecimalsKeyBoard();

                    editMessage.setReplyMarkup(keyboardMarkup);
                    sendNextEditMessage(editMessage);
                }
                case ("NBUBank"), ("PrivatBank"), ("MonoBank") -> {
                    Banks newBank = BankFactory.getBank(inputQueryMessage);
                    AnswerCallbackQuery answerCallbackQuery = sendUpdatedSettingMessage(new AnswerCallbackQuery(), inputQueryMessage);
                    //sendNextMessage(sendUpdatedSettingMessage(sendMessage, inputQueryMessage));
                    sendNextQuery(this, answerCallbackQuery);
                    userSettings.setBank(newBank);

                    InlineKeyboardMarkup keyboardMarkup = getChoiceBankKeyBoard();

                    editMessage.setReplyMarkup(keyboardMarkup);
                    sendNextEditMessage(editMessage);
                }
                case ("reminders") -> sendNextMessage(sendChoiceReminderMessage(sendMessage));
                case ("9"), ("10"), ("11"), ("12"), ("13"), ("14"), ("15"), ("16"), ("17"), ("18") -> {
                    AnswerCallbackQuery answerCallbackQuery = sendUpdatedSettingMessage(new AnswerCallbackQuery(), inputQueryMessage);
                    sendNextQuery(this, answerCallbackQuery);
                    //sendNextMessage(sendUpdatedSettingMessage(sendMessage, inputQueryMessage));
                    userSettings.setReminderTime(Integer.parseInt(inputQueryMessage));
                    userSettings.setReminderStarted(true);
                    userSettings.setChatId(chatId);

                    if (secondThreadReminderTime.isTimerOff()) {
                        secondThreadReminderTime.start();
                    }

                    InlineKeyboardMarkup keyboardMarkup = getChoiceReminderKeyBoard();

                    editMessage.setReplyMarkup(keyboardMarkup);
                    sendNextEditMessage(editMessage);
                }
                case ("OffReminder") -> {
                    AnswerCallbackQuery answerCallbackQuery = sendUpdatedSettingMessage(new AnswerCallbackQuery(), "false");
                    sendNextQuery(this, answerCallbackQuery);
                    //sendNextMessage(sendUpdatedSettingMessage(sendMessage, "false"));
                    userSettings.setReminderStarted(false);
                    secondThreadReminderTime = new ReminderTimer(this);

                    InlineKeyboardMarkup keyboardMarkup = getChoiceReminderKeyBoard();

                    editMessage.setReplyMarkup(keyboardMarkup);
                    sendNextEditMessage(editMessage);
                }
                default -> {
                    sendMessage.setText("Немає обробки цієї кнопки: " + update.getCallbackQuery().getData());
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

    public void sendNextQuery(MyTelBot bot, AnswerCallbackQuery answerCallbackQuery){
        try{
            bot.execute(answerCallbackQuery);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendNextEditMessage(EditMessageReplyMarkup editMessage) {
        try {
            execute(editMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private SendMessage sendHelloMessage(long chatId) {
        SendMessage sendMessage = new SendMessage();
        ReplyKeyboardMarkup replyKeyboardMarkup = getDefaultKeyBoard();

        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText("Ласкаво просимо. Цей бот допоможе відслідковувати актуальні курси валют");
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
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

        sendMessage.setText("Виберіть банк:");
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        return sendMessage;
    }

    private SendMessage sendChoiceReminderMessage(SendMessage sendMessage) {
        InlineKeyboardMarkup inlineKeyboardMarkup = getChoiceReminderKeyBoard();

        sendMessage.setText("Оберіть час сповіщення:");
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        return sendMessage;
    }

    private AnswerCallbackQuery sendUpdatedSettingMessage(AnswerCallbackQuery answerCallbackQuery, String inputQueryMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = getDefaultKeyBoard();
        String bank = userSettings.getBank().getName();
        String numberOfDecimal = String.valueOf(userSettings.getNumberOfDecimal());
        String currencies = userSettings.getChoicesCurrencies().toString();
        String reminderTime = String.valueOf(userSettings.getReminderTime());
        String reminderStarted = String.valueOf(userSettings.isReminderStarted());

        answerCallbackQuery.setShowAlert(false);

        if (bank.equals(inputQueryMessage) || numberOfDecimal.equals(inputQueryMessage) ||
                currencies.equals(inputQueryMessage) || reminderTime.equals(inputQueryMessage) ||
                reminderStarted.equals(inputQueryMessage)) {
            answerCallbackQuery.setText("Ці налаштування вже встановлені.");

            return answerCallbackQuery;
        }

        answerCallbackQuery.setText("Налаштування оновлені.");
        answerCallbackQuery.setShowAlert(true);
        answerCallbackQuery.setCacheTime(2);

        return answerCallbackQuery;
    }

//    private SendMessage sendUpdatedSettingMessage(SendMessage sendMessage, String inputQueryMessage) {
//        ReplyKeyboardMarkup replyKeyboardMarkup = getDefaultKeyBoard();
//        String bank = userSettings.getBank().getName();
//        String numberOfDecimal = String.valueOf(userSettings.getNumberOfDecimal());
//        String currencies = userSettings.getChoicesCurrencies().toString();
//        String reminderTime = String.valueOf(userSettings.getReminderTime());
//        String reminderStarted = String.valueOf(userSettings.isReminderStarted());
//
//        sendMessage.setReplyMarkup(replyKeyboardMarkup);
//
//        if (bank.equals(inputQueryMessage) || numberOfDecimal.equals(inputQueryMessage) ||
//                currencies.equals(inputQueryMessage) || reminderTime.equals(inputQueryMessage) ||
//                reminderStarted.equals(inputQueryMessage)) {
//            sendMessage.setText("Ці налаштування вже встановлені.");
//
//            return sendMessage;
//        }
//        sendMessage.setText("Налаштування оновлені.");
//
//        return sendMessage;
//    }
    private ReplyKeyboardMarkup getDefaultKeyBoard() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        KeyboardButton button1 = new KeyboardButton("Отримати інфо");
        row1.add(button1);

        KeyboardRow row2 = new KeyboardRow();
        KeyboardButton button2 = new KeyboardButton("Налаштування");
        row1.add(button2);

        keyboard.add(row1);
        keyboard.add(row2);

        replyKeyboardMarkup.setKeyboard(keyboard);

        return replyKeyboardMarkup;
    }

    private InlineKeyboardMarkup getChoiceBankKeyBoard() {
        boolean isPrivatBank = userSettings.getBank() instanceof PrivatBank;
        boolean isNBU = userSettings.getBank() instanceof NBUBank;
        boolean isMonoBank = userSettings.getBank() instanceof MonoBank;

        String button1Name = isNBU ? "✅ Національний банк України" : "Національний банк України";
        String callback1 = "NBUBank";

        String button2Name = isPrivatBank ? "✅ Приват Банк" : "Приват Банк";
        String callback2 = "PrivatBank";

        String button3Name = isMonoBank ? "✅ МоноБанк" : "МоноБанк";
        String callback3 = "MonoBank";

        String[] names = new String[]{button1Name, button2Name, button3Name};
        String[] keys = new String[]{callback1, callback2, callback3};

        return KeyboardBuilder.getSimpleKeyboard(names, keys);
    }

    private InlineKeyboardMarkup getChoiceDecimalsKeyBoard() {
        String button1Name = (userSettings.getNumberOfDecimal() == 2) ? "✅ 2" : "2";
        String Callback1 = "2";

        String button2Name = (userSettings.getNumberOfDecimal() == 3) ? "✅ 3" : "3";
        String Callback2 = "3";

        String button3Name = (userSettings.getNumberOfDecimal() == 4) ? "✅ 4" : "4";
        String Callback3 = "4";

        String[] names = new String[]{button1Name, button2Name, button3Name};
        String[] keys = new String[]{Callback1, Callback2, Callback3};

        return KeyboardBuilder.getSimpleKeyboard(names, keys);
    }

    private InlineKeyboardMarkup getChoiceOptionsKeyBoard() {
        String[] names = new String[]{"Знаки після коми", "Банк", "Валюти", "Час сповіщень"};
        String[] keys = new String[]{"decimals", "bank", "currencies", "reminders"};

        return KeyboardBuilder.getSimpleKeyboard(names, keys);
    }

    private InlineKeyboardMarkup getChoiceCurrenciesKeyBoard() {
        List<Currencies> choicesCurrenciesNow = userSettings.getChoicesCurrencies();

        String button1Name = (choicesCurrenciesNow.contains(Currencies.EUR)) ? "✅ Євро" : "Євро";
        String Callback1 = "EUR";

        String button2Name = (choicesCurrenciesNow.contains(Currencies.USD)) ? "✅ Американський долар" : "Американський долар";
        String Callback2 = "USD";

        String[] names = new String[]{button1Name, button2Name};
        String[] keys = new String[]{Callback1, Callback2};

        return KeyboardBuilder.getSimpleKeyboard(names, keys);
    }

    private InlineKeyboardMarkup getChoiceReminderKeyBoard() {
        String button1Name = (userSettings.getReminderTime() == 9 && userSettings.isReminderStarted()) ? "✅ 9:00" : "9:00";
        String Callback1 = "9";
        String button2Name = (userSettings.getReminderTime() == 10 && userSettings.isReminderStarted()) ? "✅ 10:00" : "10:00";
        String Callback2 = "10";
        String button3Name = (userSettings.getReminderTime() == 11 && userSettings.isReminderStarted()) ? "✅ 11:00" : "11:00";
        String Callback3 = "11";

        String button4Name = (userSettings.getReminderTime() == 12 && userSettings.isReminderStarted()) ? "✅ 12:00" : "12:00";
        String Callback4 = "12";
        String button5Name = (userSettings.getReminderTime() == 13 && userSettings.isReminderStarted()) ? "✅ 13:00" : "13:00";
        String Callback5 = "13";
        String button6Name = (userSettings.getReminderTime() == 14 && userSettings.isReminderStarted()) ? "✅ 14:00" : "14:00";
        String Callback6 = "14";

        String button7Name = (userSettings.getReminderTime() == 15 && userSettings.isReminderStarted()) ? "✅ 15:00" : "15:00";
        String Callback7 = "15";
        String button8Name = (userSettings.getReminderTime() == 16 && userSettings.isReminderStarted()) ? "✅ 16:00" : "16:00";
        String Callback8 = "16";
        String button9Name = (userSettings.getReminderTime() == 17 && userSettings.isReminderStarted()) ? "✅ 17:00" : "17:00";
        String Callback9 = "17";

        String button10Name = (userSettings.getReminderTime() == 18 && userSettings.isReminderStarted()) ? "✅ 18:00" : "18:00";
        String Callback10 = "18";
        String button11Name = !userSettings.isReminderStarted() ? "✅ Вимкнути сповіщення" : "Вимкнути сповіщення";
        String Callback11 = "OffReminder";

        String[] names = new String[]{button1Name, button2Name, button3Name, button4Name, button5Name, button6Name
                , button7Name, button8Name, button9Name, button10Name, button11Name};
        String[] keys = new String[]{Callback1, Callback2, Callback3, Callback4, Callback5, Callback6
                , Callback7, Callback8, Callback9, Callback10, Callback11};

        return KeyboardBuilder.getReminderKeyboard(names, keys);
    }

    @Override
    public String getBotUsername() {
        return MyTelBotConst.MY_TEL_BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return MyTelBotConst.MY_TEL_BOT_TOKEN;
    }
}
