package ua.goit.telegrambot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.goit.banks.Banks;
import ua.goit.banks.Currencies;
import ua.goit.banks.BankFactory;
import ua.goit.userssetting.ChatBotSettings;
import ua.goit.userssetting.SettingUtils;

import java.util.List;
import java.util.ArrayList;

public class MyTelBot extends TelegramLongPollingBot {

    private final ChatBotSettings userSettings;
    private final ReminderTimer secondThreadReminderTime;
    private final List<String> choicesCurrencies;
    private boolean nBUCheckMark, monobankCheckMark, threeDecimalsCheckMark, fourDecimalsCheckMark, eURCheckMark, offReminder = false;
    private boolean twoDecimalsCheckMark, privatCheckMark, uSDCheckMark = true;
    private int currentNotifications = 9;

    public MyTelBot() {
        choicesCurrencies = new ArrayList<>();
        userSettings = new ChatBotSettings();
        secondThreadReminderTime = new ReminderTimer(this);
        choicesCurrencies.add(userSettings.getChoicesCurrencies().get(0).toString());
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
//            Нужно убрать следующую строку перед финишем.
            System.out.println("id user= " + update.getCallbackQuery().getMessage().getChatId() + "  ");
            String inputQueryMessage = String.valueOf(update.getCallbackQuery().getData());
            SendMessage sendMessage = new SendMessage();

            sendMessage.setChatId(String.valueOf(update.getCallbackQuery().getMessage().getChatId()));

            switch (inputQueryMessage) {
                case ("bank") -> sendNextMessage(sendChoiceBankMessage(sendMessage));
                case ("decimals") -> sendNextMessage(sendChoiceDecimalsMessage(sendMessage));
                case ("currencies") -> sendNextMessage(sendChoiceCurrenciesMessage(sendMessage));
                case ("USD"), ("EUR") -> {
                    if (choicesCurrencies.contains(inputQueryMessage)) {
                        if (choicesCurrencies.size() > 1) {
                            choicesCurrencies.remove(inputQueryMessage);
                        }
                    } else {
                        choicesCurrencies.add(inputQueryMessage);

                    }
                    if(inputQueryMessage.equals("USD")){
                        uSDCheckMark = !uSDCheckMark;
                    }
                    else{
                        eURCheckMark = !eURCheckMark;
                    }
                    InlineKeyboardMarkup keyboardMarkup = getChoiceCurrenciesKeyBoard();
                    EditMessageReplyMarkup editMarkup = new EditMessageReplyMarkup();
                    editMarkup.setChatId(update.getCallbackQuery().getMessage().getChatId());
                    editMarkup.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
                    editMarkup.setReplyMarkup(keyboardMarkup);

                    try {
                        execute(editMarkup);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }

                }
                case ("confirm") -> { //в этом блоке добавляем сохраненные валюты (1 или 2) в настройки
                    List<Currencies> newCurrenciesList = new ArrayList<>();

                    for (String currency : choicesCurrencies) {
                        newCurrenciesList.add(Currencies.valueOf(currency));
                    }
                    sendNextMessage(sendUpdatedSettingMessage(sendMessage, newCurrenciesList.toString()));
                    userSettings.setChoicesCurrencies(newCurrenciesList);
                }
                case ("2"), ("3"), ("4") -> {
                    sendNextMessage(sendUpdatedSettingMessage(sendMessage, inputQueryMessage));
                    userSettings.setNumberOfDecimal(Integer.parseInt(inputQueryMessage));
                    if(inputQueryMessage.equals("2")){
                        if(twoDecimalsCheckMark) twoDecimalsCheckMark = false;
                        else{
                            twoDecimalsCheckMark = true;
                            threeDecimalsCheckMark = false;
                            fourDecimalsCheckMark = false;
                        }
                    }
                    else if(inputQueryMessage.equals("3")){
                        if(threeDecimalsCheckMark) threeDecimalsCheckMark = false;
                        else{
                            twoDecimalsCheckMark = false;
                            threeDecimalsCheckMark = true;
                            fourDecimalsCheckMark = false;
                        }
                    }
                    else{
                        if(fourDecimalsCheckMark) fourDecimalsCheckMark = false;
                        else{
                            twoDecimalsCheckMark = false;
                            threeDecimalsCheckMark = false;
                            fourDecimalsCheckMark = true;
                        }
                    }

                    InlineKeyboardMarkup keyboardMarkup = getChoiceDecimalsKeyBoard();
                    EditMessageReplyMarkup editMarkup = new EditMessageReplyMarkup();
                    editMarkup.setChatId(update.getCallbackQuery().getMessage().getChatId());
                    editMarkup.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
                    editMarkup.setReplyMarkup(keyboardMarkup);

                    try {
                        execute(editMarkup);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
                case ("NBUBank"), ("PrivatBank"), ("MonoBank") -> {
                    Banks newBank = BankFactory.getBank(inputQueryMessage);
                    sendNextMessage(sendUpdatedSettingMessage(sendMessage, inputQueryMessage));
                    userSettings.setBank(newBank);
                    if(inputQueryMessage.equals("NBUBank")) {
                        if(nBUCheckMark) nBUCheckMark = false;
                        else{
                            nBUCheckMark = true;
                            privatCheckMark = false;
                            monobankCheckMark = false;
                        }
                    }
                    else if(inputQueryMessage.equals("PrivatBank")){
                        if(privatCheckMark) privatCheckMark = false;
                        else{
                            nBUCheckMark = false;
                            privatCheckMark = true;
                            monobankCheckMark = false;
                        }
                    }
                    else{
                        if(monobankCheckMark) monobankCheckMark = false;
                        else{
                            nBUCheckMark = false;
                            privatCheckMark = false;
                            monobankCheckMark = true;
                        }
                    }

                    InlineKeyboardMarkup keyboardMarkup = getChoiceBankKeyBoard();
                    EditMessageReplyMarkup editMarkup = new EditMessageReplyMarkup();
                    editMarkup.setChatId(update.getCallbackQuery().getMessage().getChatId());
                    editMarkup.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
                    editMarkup.setReplyMarkup(keyboardMarkup);

                    try {
                        execute(editMarkup);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
                case ("reminders") -> sendNextMessage(sendChoiceReminderMessage(sendMessage));
                case ("9"), ("10"), ("11"), ("12"), ("13"), ("14"), ("15"), ("16"), ("17"), ("18") -> {
                    sendNextMessage(sendUpdatedSettingMessage(sendMessage, inputQueryMessage));
                    userSettings.setReminderTime(Integer.parseInt(inputQueryMessage));
                    userSettings.setReminderStarted(true);
                    userSettings.setChatId(update.getCallbackQuery().getMessage().getChatId());
                    secondThreadReminderTime.start();
                    currentNotifications = Integer.parseInt(inputQueryMessage);

                    InlineKeyboardMarkup keyboardMarkup = getChoiceReminderKeyBoard();
                    EditMessageReplyMarkup editMarkup = new EditMessageReplyMarkup();
                    editMarkup.setChatId(update.getCallbackQuery().getMessage().getChatId());
                    editMarkup.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
                    editMarkup.setReplyMarkup(keyboardMarkup);

                    try {
                        execute(editMarkup);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
                case ("OffReminder") -> {
                    sendNextMessage(sendUpdatedSettingMessage(sendMessage, "false"));
                    userSettings.setReminderStarted(false);
                    offReminder = !offReminder;
                    currentNotifications = 0;

                    InlineKeyboardMarkup keyboardMarkup = getChoiceReminderKeyBoard();
                    EditMessageReplyMarkup editMarkup = new EditMessageReplyMarkup();
                    editMarkup.setChatId(update.getCallbackQuery().getMessage().getChatId());
                    editMarkup.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
                    editMarkup.setReplyMarkup(keyboardMarkup);

                    try {
                        execute(editMarkup);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
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

    private SendMessage sendUpdatedSettingMessage(SendMessage sendMessage, String inputQueryMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = getDefaultKeyBoard();
        String bank = userSettings.getBank().getName();
        String numberOfDecimal = String.valueOf(userSettings.getNumberOfDecimal());
        String currencies = userSettings.getChoicesCurrencies().toString();
        String reminderTime = String.valueOf(userSettings.getReminderTime());
        String reminderStarted = String.valueOf(userSettings.isReminderStarted());

        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        if (bank.equals(inputQueryMessage) || numberOfDecimal.equals(inputQueryMessage) ||
                currencies.equals(inputQueryMessage) || reminderTime.equals(inputQueryMessage) ||
                reminderStarted.equals(inputQueryMessage)) {
            sendMessage.setText("Ці налаштування вже встановлені.");

            return sendMessage;
        }
        sendMessage.setText("Налаштування оновлені.");

        return sendMessage;
    }

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
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        String button1Name = nBUCheckMark ? "✅ Національний банк України" : "Національний банк України";
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton(button1Name);
        inlineKeyboardButton1.setCallbackData("NBUBank");
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(inlineKeyboardButton1);

        String button2Name = privatCheckMark ? "✅ Приват Банк" : "Приват Банк";
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton(button2Name);
        inlineKeyboardButton2.setCallbackData("PrivatBank");
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        keyboardButtonsRow2.add(inlineKeyboardButton2);

        String button3Name = monobankCheckMark ? "✅ МоноБанк" : "МоноБанк";
        InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton(button3Name);
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

    private InlineKeyboardMarkup getChoiceDecimalsKeyBoard() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        String button1Name = twoDecimalsCheckMark ? "✅ 2" : "2";
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton(button1Name);
        inlineKeyboardButton1.setCallbackData("2");
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(inlineKeyboardButton1);

        String button2Name = threeDecimalsCheckMark ? "✅ 3" : "3";
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton(button2Name);
        inlineKeyboardButton2.setCallbackData("3");
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        keyboardButtonsRow2.add(inlineKeyboardButton2);

        String button3Name = fourDecimalsCheckMark ? "✅ 4" : "4";
        InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton(button3Name);
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

    private InlineKeyboardMarkup getChoiceOptionsKeyBoard() {
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

    private InlineKeyboardMarkup getChoiceCurrenciesKeyBoard() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        String button1Name = eURCheckMark ? "✅ Євро" : "Євро";
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton(button1Name);
        inlineKeyboardButton1.setCallbackData("EUR");
        inlineKeyboardButton1.setSwitchInlineQueryCurrentChat("+EUR"); //позволяет выбирать не только этот вариант
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(inlineKeyboardButton1);

        String button2Name = uSDCheckMark ? "✅ Американський долар" : "Американський долар";
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton(button2Name);
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

    private InlineKeyboardMarkup getChoiceReminderKeyBoard() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        String button1Name = currentNotifications == 9 ? "✅ 9:00" : "9:00";
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton(button1Name);
        inlineKeyboardButton1.setCallbackData("9");
        String button2Name = currentNotifications == 10 ? "✅ 10:00" : "10:00";
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton(button2Name);
        inlineKeyboardButton2.setCallbackData("10");
        String button3Name = currentNotifications == 11 ? "✅ 11:00" : "11:00";
        InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton(button3Name);
        inlineKeyboardButton3.setCallbackData("11");
        List<InlineKeyboardButton> keyboardButtonsRow3 = new ArrayList<>();
        keyboardButtonsRow3.add(inlineKeyboardButton1);
        keyboardButtonsRow3.add(inlineKeyboardButton2);
        keyboardButtonsRow3.add(inlineKeyboardButton3);

        String button4Name = currentNotifications == 12 ? "✅ 12:00" : "12:00";
        InlineKeyboardButton inlineKeyboardButton4 = new InlineKeyboardButton(button4Name);
        inlineKeyboardButton4.setCallbackData("12");
        String button5Name = currentNotifications == 13 ? "✅ 13:00" : "13:00";
        InlineKeyboardButton inlineKeyboardButton5 = new InlineKeyboardButton(button5Name);
        inlineKeyboardButton5.setCallbackData("13");
        String button6Name = currentNotifications == 14 ? "✅ 14:00" : "14:00";
        InlineKeyboardButton inlineKeyboardButton6 = new InlineKeyboardButton(button6Name);
        inlineKeyboardButton6.setCallbackData("14");
        List<InlineKeyboardButton> keyboardButtonsRow6 = new ArrayList<>();
        keyboardButtonsRow6.add(inlineKeyboardButton4);
        keyboardButtonsRow6.add(inlineKeyboardButton5);
        keyboardButtonsRow6.add(inlineKeyboardButton6);

        String button7Name = currentNotifications == 15 ? "✅ 15:00" : "15:00";
        InlineKeyboardButton inlineKeyboardButton7 = new InlineKeyboardButton(button7Name);
        inlineKeyboardButton7.setCallbackData("15");
        String button8Name = currentNotifications == 16 ? "✅ 16:00" : "16:00";
        InlineKeyboardButton inlineKeyboardButton8 = new InlineKeyboardButton(button8Name);
        inlineKeyboardButton8.setCallbackData("16");
        String button9Name = currentNotifications == 17 ? "✅ 17:00" : "17:00";
        InlineKeyboardButton inlineKeyboardButton9 = new InlineKeyboardButton(button9Name);
        inlineKeyboardButton9.setCallbackData("17");
        List<InlineKeyboardButton> keyboardButtonsRow9 = new ArrayList<>();
        keyboardButtonsRow9.add(inlineKeyboardButton7);
        keyboardButtonsRow9.add(inlineKeyboardButton8);
        keyboardButtonsRow9.add(inlineKeyboardButton9);

        String button10Name = currentNotifications == 18 ? "✅ 18:00" : "18:00";
        InlineKeyboardButton inlineKeyboardButton10 = new InlineKeyboardButton(button10Name);
        inlineKeyboardButton10.setCallbackData("18");
        String button11Name = offReminder ? "✅ Вимкнути сповіщення" : "Вимкнути сповіщення";
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton(button11Name);
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

    @Override
    public String getBotUsername() {
        return MyTelBotConst.MY_TEL_BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return MyTelBotConst.MY_TEL_BOT_TOKEN;
    }
}
