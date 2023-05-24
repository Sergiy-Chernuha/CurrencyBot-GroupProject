package ua.goit.userssetting;

import ua.goit.SecondThreadAlertTime;
import ua.goit.banks.Banks;
import ua.goit.banks.Currencies;
import ua.goit.banks.privatbank.PrivatBank;

import java.util.List;

public class ChatBotSettings {

    private int numberOfDecimal = 2;
    private Banks bank = new PrivatBank();
    private List<Currencies> choicesCurrencies = List.of(Currencies.USD);
    private int alertTime = 9;
    private boolean alerts = false;
    private Long chatId ;

//    SecondThreadAlertTime secondThreadAlertTime = new SecondThreadAlertTime(this);

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        System.out.println(chatId.getClass());
        System.out.println("yoshkarla"+chatId);
        this.chatId = chatId;
    }

//    public SecondThreadAlertTime getSecondThreadAlertTime() {
//        return secondThreadAlertTime;
//    }

    public int getNumberOfDecimal() {
        return numberOfDecimal;
    }

    public void setNumberOfDecimal(int numberOfDecimal) {
        this.numberOfDecimal = numberOfDecimal;
    }

    public Banks getBank() {
        return bank;
    }

    public void setBank(Banks bank) {
        this.bank = bank;
    }

    public List<Currencies> getChoicesCurrencies() {
        return choicesCurrencies;
    }

    public void setChoicesCurrencies(List<Currencies> choicesCurrencies) {
        this.choicesCurrencies = choicesCurrencies;
    }

    public synchronized int getAlertTime() {
        return alertTime;
    }

    public void setAlertTime(int alertTime) {
        this.alertTime = alertTime;
    }

    public boolean isAlerts() {
            return alerts;
    }

    public void setAlerts(boolean alerts) {
        this.alerts = alerts;
    }

//    public long getChatId() {
//        return chatId;
//    }
//
//    public void setChatId(Long chatId) {
//        this.chatId = chatId;
//    }
}
